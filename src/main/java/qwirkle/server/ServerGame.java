package qwirkle.server;

import org.omg.CORBA.ORBPackage.InvalidName;
import qwirkle.client.ClientView;
import qwirkle.game.*;
import qwirkle.game.exception.InvalidMoveException;
import qwirkle.server.exception.EmptyBagException;
import qwirkle.server.exception.InvalidNameException;
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

    /**
     * creates a new servergame
     * @param clients is a list with clienthandlers
     */
    public ServerGame(List<ClientHandler> clients) {
        board = new Board();
        bag = new Bag();
        players = new ArrayList<>();
        for (ClientHandler client : clients) {
            SocketPlayer player = new SocketPlayer(client.getName(), client, new ClientView());
            players.add(player);
        }

    }

    /**
     * starts the servergame
     */
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

    /**
     * ends the servergame
     * @param win is a boolean if you have won or not
     */
    public void endGame(boolean win) {
        Map<Integer, String> scores = new HashMap<>();
        for (Player player : players) {
            scores.put(player.getScore(), player.getName());
        }

        for (SocketPlayer socket : players) {
            socket.getClient().endGame(win, scores);
        }
    }

    /**
     * Switch the turn to the next player if possible.
     */
    public void doTurn() {
        if (isGameOver()) {
            endGame(true);
        } else {
            turn = (turn + 1) % players.size();

            if (!board.isMovePossible(players.get(turn).getHand()) && (bag.getSize() == 0)) {
                sendPlayerPass();
                // If player can't make a move and the bag is empty, skip/pass turn.
                doTurn();
            }

            sendPlayerTurn();
        }
    }

    /**
     * Informs the SocketPlayers whose turn it is.
     */
    //
    public void sendPlayerTurn() {
        for (SocketPlayer socketPlayer : players) {
            socketPlayer.getClient().sendTurn(players.get(turn).getName());
        }
    }

    /**
     * Sends all SocketPlayers that a Player has passed.
     */
    public void sendPlayerPass() {
        for (SocketPlayer socketPlayer : players) {
            socketPlayer.getClient().sendPass(players.get(turn).getName());
        }
    }

    /**
     * Does a put move
     * @param move is a map with points and blocks
     * @throws InvalidMoveException when an invalid move is tried to do
     * @throws TilesUnownedException if a unowned tile is played
     */
    public void doMovePut(Map<Point, Block> move)
            throws InvalidMoveException, TilesUnownedException {
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
            List<Block> newBlocks = new ArrayList<>();

            if (bag.getSize() < move.size()) {
                for (int i = 0; i < bag.getSize(); i++) {
                    newBlocks.add(bag.takeRandomBlock());
                }
            } else {
                for (int i = 0; i < move.size(); i++) {
                    newBlocks.add(bag.takeRandomBlock());
                }
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

    /**
     * Does a trade move
     * @param blocks is a list with blocks
     * @throws TradeFirstTurnException if it is the first turn you can't trade tiles
     * @throws TilesUnownedException if a unowned tile is played
     * @throws EmptyBagException if there is an empty bag
     */
    public void doMoveTrade(List<Block> blocks)
            throws TradeFirstTurnException, TilesUnownedException, EmptyBagException {
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

        SocketPlayer player = players.get(turn);
        List<Block> newBlocks = new ArrayList<>();

        for (Block block : blocks) {
            player.removeBlock(block);
            Block newBlock = bag.takeRandomBlock();
            player.addBlock(newBlock);
            newBlocks.add(newBlock);
            bag.addBlock(block);
        }

        player.getClient().sendDrawTile(newBlocks);

        for (SocketPlayer socketPlayer : players) {
            socketPlayer.getClient().sendMoveTrade(blocks.size());
        }

        doTurn();
    }

    /**
     * checks if the game is over
     * @return true if the game is over and false if not
     */
    public boolean isGameOver() {
        for (SocketPlayer player : players) {
            if (board.isMovePossible(player.getHand())) {
                return false; // Return false if any Player can make a move.
            }
        }
        System.out.println("No move possible for players.");

        if (board.isMovePossible(bag.getBag())) {
            return false; // Return false if a move is possible with any of the blocks in the bag.
        }
        System.out.println("No move possible with bag, ending game.");

        return true;
    }

    /**
     * gets the client whose turn it is
     * @return the clienthandler whose turn it is
     */
    public ClientHandler getTurnClient() {
        return players.get(turn).getClient();
    }

    /**
     * returns the board
     * @return the board
     */
    public Board getBoard() {
        return board;
    }

}
