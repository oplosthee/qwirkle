package qwirkle.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private int port;
    private InetAddress inetAddress;
    private String clientName;

    public Client(int port, InetAddress inetAddress, String clientName) {
        this.port = port;
        this.inetAddress = inetAddress;
        this.clientName = clientName;
    }

    public void start() {
        try {
            Socket socket = new Socket(inetAddress.getHostAddress(), port);
            System.out.println("[Client] Debug (Client) - Connected to server.");

            TransactionHandler handler = new TransactionHandler(this, socket);
            handler.sendIdentify(clientName);
            handler.run();


        } catch (IOException e) {
            System.out.println("[Client] Debug (Client) - Error: Connection refused.");
        }
    }

    public static void main(String[] args) {
        //Scanner input = new Scanner(System.in);
        String portNumber = "1024";
        String ipAddress = "127.0.0.1";

        //TODO: Add port/ip verification.
        //System.out.println("Enter port number:\n");
        //portNumber = input.next();
        //System.out.println("Enter IP address:\n");
        //ipAddress = input.next();

        Client client;

        try {
            client = new Client(Integer.parseInt(portNumber), InetAddress.getByName(ipAddress), "TestClient2");
            client.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

}
