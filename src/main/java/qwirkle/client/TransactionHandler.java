package qwirkle.client;

import qwirkle.game.Block;
import qwirkle.game.HumanPlayer;
import qwirkle.shared.net.IProtocol;
import qwirkle.util.ProtocolFormatter;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static qwirkle.shared.net.IProtocol.*;

public class TransactionHandler {

    private Client client;
    private Socket socket;
    private String name;
    private BufferedReader reader;
    private BufferedWriter writer;
    private ClientGame game;

    private boolean connected;

    public TransactionHandler(Client client, Socket socket) throws IOException {
        this.client = client;
        this.socket = socket;
        game = null;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void run() {
        connected = true;

        while (connected) {
            try {
                parse(reader.readLine());
            }catch (SocketException e) {
                System.out.println("[Client] Debug (TransactionHandler) - Server disconnected. (SocketException)");
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                disconnect();
            }
        }
    }

    public void sendQuit() {
        sendRaw(IProtocol.CLIENT_QUIT);
        disconnect();
    }

    public void sendIdentify(String name) {
        this.name = name;
        sendRaw(ProtocolFormatter.identify(name));
    }

    public void sendJoinQueue(int[] queues) {
        sendRaw(ProtocolFormatter.joinQueue(queues));
    }

    public void sendMovePut(Map<Point, Block> move) {
        sendRaw(ProtocolFormatter.clientMovePut(move));
    }

    public void sendMoveTrade(List<Block> blocks) {
        sendRaw(ProtocolFormatter.moveTrade(blocks));
    }

    public void sendRaw(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parse(String message) {
        String[] arguments = message.split(" ");

        //TODO: Make parseInts safe.
        switch(arguments[0]) {
            case SERVER_IDENTIFY:
                System.out.println("[Client] Debug (TransactionHandler) - Identified client.");
                sendJoinQueue(new int[]{2, 3});
                break;
            case SERVER_QUEUE:
                System.out.println("[Client] Debug (TransactionHandler) - Successfully joined a queue.");
                break;
            case SERVER_ERROR:
                int errorId = Integer.parseInt(arguments[1]);
                String reason = "";

                for (int i = 2; i < arguments.length; i++) {
                    reason = reason + " " + arguments[i];
                }

                System.out.println("[Client] Debug (TransactionHandler) - Error (" + IProtocol.Error.values()[errorId] + ") -" + reason);
                //disconnect(); TODO: Proper error handling.
                break;
            case SERVER_GAMESTART:
                System.out.println("[Client] Debug (TransactionHandler) - Started a new game: " + message);
                game = new ClientGame(new HumanPlayer(name)); // TODO: Games with AI player.
                Block testBlock = new Block(30);
                Point testPoint = new Point(0,0);
                Map<Point, Block> move = new HashMap<>();
                move.put(testPoint, testBlock);
                sendMovePut(move);
                break;
            case SERVER_GAMEEND:
                game = null;
                String endReason =  arguments[1];
                String scores = "";

                for (int i = 2; i < arguments.length; i++) {
                    scores += " " + arguments[i];
                }

                System.out.println("[Client] Debug (TransactionHandler) - The game ended because [" + endReason + "]. The scores are" + scores);
                //TODO: Allow player to enter new queue(s).
                break;
            case SERVER_DRAWTILE:
                game.drawTile(Arrays.copyOfRange(arguments, 1, arguments.length));
                break;
            case SERVER_MOVE_PUT:
                game.doMove(Arrays.copyOfRange(arguments, 1, arguments.length));
                break;
            case SERVER_TURN:
                System.out.println(game.getBoard().toString());
                if (arguments[1].equals(name)) {
                    game.getPlayer().determineMove();
                } else {
                    System.out.println("[Client] Debug (TransactionHandler) - " + arguments[1] + "'s turn started.");
                }
                break;
            default:
                System.out.println("[Client] Debug (TransactionHandler) - Received: " + message);
                break;
        }
    }

    public void disconnect() {
        try {
            reader.close();
            writer.close();
            socket.close();
            connected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
