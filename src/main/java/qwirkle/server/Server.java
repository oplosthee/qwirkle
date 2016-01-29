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
            System.out.println("[Server] Debug (Server) - Created new ServerSocket with port " + port);
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

    public static void main(String[] args) {
        //Scanner input = new Scanner(System.in);
        String portNumber = "1024";

        // Check if the String entered is between 0 and 65535 (so only valid port numbers can be entered).
        //while (!portNumber.matches("^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$")) {
        //    System.out.println(USAGE);
        //    portNumber = input.next();
        //}

        //TODO: Ask for new port number when it is in use (BindException).
        Server server = new Server(Integer.parseInt(portNumber));
        server.start();
    }

}