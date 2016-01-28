package qwirkle.server;

import qwirkle.game.*;
import qwirkle.game.exception.InvalidMoveException;
import qwirkle.server.exception.EmptyBagException;
import qwirkle.server.exception.TilesUnownedException;
import qwirkle.server.exception.TradeFirstTurnException;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerGame {

    private Board board;
    private Bag bag;
    private List<SocketPlayer> players;
    private int turn;

    public ServerGame(List<ClientHandler> clients) {
        board = new Board();
        bag = new Bag();
        players = new ArrayList<>();
        for (ClientHandler client : clients) {
            SocketPlayer player = new SocketPlayer(client.getName(), client);
            players.add(player);
        }

    }

    public void start() {
        // Send game start to all clients.
        for (SocketPlayer player : players) {
            player.getClient().startGame(this, players);
        }

        // Give all players 6 random blocks at the start of the game.
        for (SocketPlayer socketPlayer : players) {
            List<Block> hand = bag.takeHand();
            socketPlayer.addBlock(hand);
            socketPlayer.getClient().sendDrawTile(hand);
        }

        turn = 0;
        sendPlayerTurn();
    }

    public void endGame(boolean win) {
        Map<Integer, String> scores = new HashMap<>();
        for (Player player : players) {
            scores.put(player.getScore(), player.getName());
        }

        for (SocketPlayer socket : players) {
            socket.getClient().endGame(win, scores);
        }
    }

    public void doTurn() {
        if (isGameOver()) {
            endGame(true);
        } else {
            turn = (turn + 1) % players.size();

            if(!board.isMovePossible(players.get(turn).getHand()) && (bag.getSize() == 0)) {
                sendPlayerPass(); // If player can't make a move and the bag is empty, skip/pass turn.
                doTurn();
            }

            sendPlayerTurn();
        }
    }

    // Inform the SocketPlayers whose turn it is.
    public void sendPlayerTurn() {
        for (SocketPlayer socketPlayer : players) {
            socketPlayer.getClient().sendTurn(players.get(turn).getName());
        }
    }

    public void sendPlayerPass() {
        for (SocketPlayer socketPlayer : players) {
            socketPlayer.getClient().sendPass(players.get(turn).getName());
        }
    }

    public void doMovePut(Map<Point, Block> move) throws InvalidMoveException, TilesUnownedException {
        SocketPlayer player = players.get(turn);
        try {

            for (Block block : move.values()) {
                if (!player.getHand().contains(block)) {
                    throw new TilesUnownedException();
                }
            }

            board.placeBlock(move); // Try to place the blocks on the board.
            player.addScore(board.getPoints(move)); // Add the score for the move to the Player.
            move.values().forEach(player::removeBlock); // Remove the blocks from the Player's hand.

            // Give player new Blocks.
            // TODO: Make sure there are enough Blocks in the Bag left to give.
            List<Block> newBlocks = new ArrayList<>();
            for (int i = 0; i < move.size(); i++) {
                newBlocks.add(bag.takeRandomBlock());
            }

            player.addBlock(newBlocks);
            player.getClient().sendDrawTile(newBlocks);

            for (SocketPlayer socketPlayer : players) {
                socketPlayer.getClient().sendMovePut(move);
            }
        } catch (InvalidMoveException e) {
            throw new InvalidMoveException();
        }

        doTurn();
    }

    public void doMoveTrade(List<Block> blocks) throws TradeFirstTurnException, TilesUnownedException, EmptyBagException {
        if (board.isEmpty()) {
            throw new TradeFirstTurnException();
        }

        for (Block block : blocks) {
            if (!players.get(turn).getHand().contains(block)) {
                throw new TilesUnownedException();
            }
        }

        if (blocks.size() > bag.getSize()) {
            throw new EmptyBagException();
        }

        for (Block block : blocks) {
            SocketPlayer player = players.get(turn);
            player.removeBlock(block);
            player.addBlock(bag.takeRandomBlock());
            bag.addBlock(block);
        }

        for (SocketPlayer socketPlayer : players) {
            socketPlayer.getClient().sendMoveTrade(blocks.size());
        }

    }

    public boolean isGameOver() {
        if (board.isMovePossible(bag.getBag())) {
            return false; // Return false if a move is possible with any of the blocks in the bag.
        }

        for (SocketPlayer player : players) {
            if (board.isMovePossible(player.getHand())) {
                return false; // Return false if any Player can make a move.
            }
        }

        return true;
    }

    public ClientHandler getTurnClient() {
        return players.get(turn).getClient();
    }

    public Board getBoard() {
        return board;
    }

}
