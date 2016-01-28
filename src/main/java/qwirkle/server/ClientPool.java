package qwirkle.server;

import qwirkle.server.exception.InvalidNameException;
import qwirkle.server.exception.UsedNameException;
import qwirkle.shared.net.IProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientPool {

    private Map<String, ClientHandler> clients;
    private Map<Integer, List<ClientHandler>> queues;

    public ClientPool() {
        clients = new HashMap<>();
        queues = new HashMap<>();
        queues.put(2, new ArrayList<>());
        queues.put(3, new ArrayList<>());
        queues.put(4, new ArrayList<>());
    }

    public void addClient(String name, ClientHandler client) throws InvalidNameException, UsedNameException {
        if (!name.matches(IProtocol.NAME_REGEX)) {
            throw new InvalidNameException();
        } else if (clients.containsKey(name)) {
            throw new UsedNameException();
        }
        clients.put(name, client);
        System.out.println("[Server] Debug (ClientPool) - Added " + name + " to the client pool.");
    }

    public void removeClient(String name) {
        clients.remove(name);
        System.out.println("[Server] Debug (ClientPool) - Removed " + name + " from the client pool.");
    }

    public synchronized void checkQueue(int queue) {
        if (queues.get(queue).size() >= queue) {
            ServerGame game = new ServerGame(queues.get(queue));

            for (ClientHandler clientHandler : queues.get(queue)) {
                clientHandler.setGame(game);
            }

            queues.get(queue).clear();
            game.start();
        }
    }

    public synchronized void addClientToQueue(ClientHandler handler, int queueSize) {
        queues.get(queueSize).add(handler);
        checkQueue(queueSize);
    }

    public synchronized void removeFromQueue(ClientHandler handler, int queueSize) {
        if (queues.get(queueSize).contains(handler)) {
            queues.get(queueSize).remove(handler);
        }
    }

    public void removeFromAllQueues(ClientHandler handler) {
        for (Map.Entry<Integer, List<ClientHandler>> entry : queues.entrySet()) { //TODO: For later: removing during a loop is probably not a good idea.
            removeFromQueue(handler, entry.getKey());
        }
    }

}
