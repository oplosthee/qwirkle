package qwirkle.game;

import qwirkle.server.ClientHandler;

public class SocketPlayer extends Player {

    ClientHandler client;

    /**
     * Constructs a new player, which sets the name, a new hand and sets the score on 0.
     *
     * @param name name for the Player
     */
    public SocketPlayer(String name, ClientHandler client) {
        super(name);
        this.client = client;
    }

    public ClientHandler getClient() {
        return client;
    }

    @Override
    public String determineMove() {
        return "TODO!";
    }
}
