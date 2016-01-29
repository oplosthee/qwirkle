package qwirkle;

import qwirkle.client.Client;
import qwirkle.server.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {
        if (args[0].equals("CLIENT")) {
            int portNumber = Integer.parseInt(args[1]);
            String ipAddress = args[2];
            String name = args[3];

            Client client;

            try {
                client = new Client(portNumber, InetAddress.getByName(ipAddress), name);
                client.start();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        if (args[0].equals("SERVER")) {
            Server server = new Server(Integer.parseInt(args[1]));
            server.start();
        }
    }

}
