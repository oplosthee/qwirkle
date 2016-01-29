package qwirkle.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private int port;
    private InetAddress inetAddress;
    private String clientName;
    private ClientView view;

    public Client(int port, InetAddress inetAddress, String clientName) {
        this.port = port;
        this.inetAddress = inetAddress;
        this.clientName = clientName;
        view = new ClientView();
    }

    public void start() {
        try {
            Socket socket = new Socket(inetAddress.getHostAddress(), port);
            view.print("[Client] Debug (Client) - Connected to server.");

            TransactionHandler handler = new TransactionHandler(this, socket, view);
            handler.sendIdentify(clientName);
            handler.run();


        } catch (IOException e) {
            view.print("[Client] Debug (Client) - Error: Connection refused.");
        }
    }

    public static void main(String[] args) {
        //Scanner input = new Scanner(System.in);
        int portNumber = Integer.parseInt("1024");
        String ipAddress = "127.0.0.1";
        String name = "ClientName";

        //System.out.println("Enter port number:\n");
        //portNumber = input.next();
        //System.out.println("Enter IP address:\n");
        //ipAddress = input.next();

        Client client;

        try {
            client = new Client(portNumber, InetAddress.getByName(ipAddress), name);
            client.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

}
