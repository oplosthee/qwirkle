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
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static qwirkle.shared.net.IProtocol.*;

public class TransactionHandler {

    private Socket socket;
    private String name;
    private BufferedReader reader;
    private BufferedWriter writer;
    private ClientGame game;
    private ClientView view;

    private boolean connected;

    public TransactionHandler(Client client, Socket socket, ClientView view) throws IOException {
        this.socket = socket;
        game = null;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.view = view;
    }

    public void run() {
        connected = true;

        while (connected) {
            try {
                String input = reader.readLine();
                if (input != null) {
                    parse(input);
                }
            } catch (SocketException e) {
                view.print("[Client] Server disconnected. (SocketException)");
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                disconnect();
            }
        }
    }

    public void sendIdentify(String userName) {
        name = userName;
        sendRaw(ProtocolFormatter.identify(name));
    }

    public void sendJoinQueue(int[] queues) {
        sendRaw(ProtocolFormatter.joinQueue(queues));
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

    public void joinQueue() {
        Scanner input = new Scanner(System.in);
        view.print("[Client] Please enter which queues you want to join in format: 2,3,4");
        String userInput = input.nextLine();
        String[] queues = userInput.split(",");
        int[] joinQueues = new int[queues.length];

        for (int i = 0; i < queues.length; i++) {
            joinQueues[i] = Integer.parseInt(queues[i]);
        }

        sendJoinQueue(joinQueues);
    }

    public void parse(String message) {
        String[] arguments = message.split(" ");

        switch (arguments[0]) {
            case SERVER_IDENTIFY:
                view.print("[Client] Debug (TransactionHandler) - Identified client.");
                joinQueue();
                break;
            case SERVER_QUEUE:
                view.print("[Client] Debug (TransactionHandler) - Successfully joined a queue.");
                break;
            case SERVER_ERROR:
                int errorId = Integer.parseInt(arguments[1]);
                String reason = "";

                for (int i = 2; i < arguments.length; i++) {
                    reason = reason + " " + arguments[i];
                }

                if (errorId >= 5 && errorId <= 8 ) {
                    view.print("[Client] Invalid move - Resetting tiles to old situation.");
                    game.getPlayer().getHand().addAll(game.getPlayer().lastMove);
                }

                view.print("[Client] Error(" + IProtocol.Error.values()[errorId] + ") -" + reason);
                break;
            case SERVER_GAMESTART:
                view.print("[Client] Debug (TransactionHandler) - Started a new game: " + message);
                game = new ClientGame(new HumanPlayer(name, view)); // TODO: Games with AI player.
                view.setBoard(game.getBoard());
                break;
            case SERVER_GAMEEND:
                game = null;
                String endReason =  arguments[1];
                String scores = "";

                for (int i = 2; i < arguments.length; i++) {
                    scores += " " + arguments[i];
                }

                view.print("[Client] The game ended [" + endReason + "]. The scores are" + scores);
                view.setBoard(null);
                joinQueue();
                break;
            case SERVER_DRAWTILE:
                game.drawTile(Arrays.copyOfRange(arguments, 1, arguments.length));
                break;
            case SERVER_MOVE_PUT:
                game.doMove(Arrays.copyOfRange(arguments, 1, arguments.length));
                break;
            case SERVER_TURN:
                if (arguments[1].equals(name)) {
                    System.out.println(game.getBoard().toString());
                    sendRaw(game.getPlayer().determineMove());
                } else {
                    view.print("[Client] " + arguments[1] + "'s turn started.");
                }
                break;
            default:
                view.print("[Client] Debug (TransactionHandler) - Received: " + message);
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
