package qwirkle.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final String USAGE
            = "Enter the port number on which the server should listen.\n";

    private int port;
    private ServerSocket socket;
    private ClientPool pool;

    public Server(int port) {
        this.port = port;
        pool = new ClientPool();
    }

    public void start() {
        boolean running = true;

        try {
            socket = new ServerSocket(port);
            System.out.println("[Server] Created new ServerSocket with port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (running) {
            run();
        }
    }

    public void run() {
        try {
            Socket client = socket.accept();
            System.out.println("[Server] Debug (Server) - Client socket accepted.");

            ClientHandler clientHandler = new ClientHandler(client, pool);
            new Thread(clientHandler).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}