package qwirkle.client;

import qwirkle.game.Block;
import qwirkle.shared.net.IProtocol;
import qwirkle.util.ProtocolFormatter;

import java.util.List;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

import static qwirkle.shared.net.IProtocol.SERVER_ERROR;
import static qwirkle.shared.net.IProtocol.SERVER_IDENTIFY;
import static qwirkle.shared.net.IProtocol.SERVER_QUEUE;

public class TransactionHandler {

    private Client client;
    private Socket socket;
    private String name;
    private BufferedReader reader;
    private BufferedWriter writer;

    private boolean connected;

    public TransactionHandler(Client client, Socket socket) throws IOException {
        this.client = client;
        this.socket = socket;
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
        sendRaw(ProtocolFormatter.movePut(move));
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
                disconnect();
                break;
            default:
                System.out.println("[Client] Debug (TransActionHandler) - Received: " + message);
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
