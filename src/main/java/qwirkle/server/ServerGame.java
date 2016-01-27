package qwirkle.server;

import qwirkle.game.*;
import qwirkle.game.exception.InvalidMoveException;
import qwirkle.shared.net.IProtocol;

import java.util.List;
import java.util.*;
import java.awt.*;

public class ServerGame {

    private Board board;
    private Bag bag;
    private List<SocketPlayer> players;
    private int turn;
    private boolean isGameOver;

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
        isGameOver = false;
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
        if (isGameOver) {
            endGame(true);
        } else {
            turn = (turn + 1) % players.size();

            //TODO: Add check to make sure Player can make a move.

            sendPlayerTurn();
        }
    }

    // Inform the SocketPlayers whose turn it is.
    public void sendPlayerTurn() {
        for (SocketPlayer socketPlayer : players) {
            socketPlayer.getClient().sendTurn(players.get(turn).getName());
        }
    }

    public void doMovePut(Map<Point, Block> move) {
        SocketPlayer player = players.get(turn);
        try {
            move.values().stream().filter(block -> !player.getHand().contains(block)).forEach(block -> {
                //TODO: Throw exception.
                player.getClient().sendError(IProtocol.Error.MOVE_TILES_UNOWNED + " Cannot place Blocks which you do not own");
            });

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
            player.getClient().sendError(IProtocol.Error.MOVE_INVALID + " Invalid move");
        }

        doTurn();
    }

    public void doMoveTrade(List<Block> blocks) {
        if (board.isEmpty()) {
            //TODO: Player is not allowed to trade when Board is empty, throw exception. (IProtocol.Error#TRADE_FIRST_TURN)
        }

        for (Block block : blocks) {
            SocketPlayer player = players.get(turn);
            player.removeBlock(block); // TODO: Make sure player actually has the Block.
            player.addBlock(bag.takeRandomBlock());
        }

        for (SocketPlayer socketPlayer : players) {
            socketPlayer.getClient().sendMoveTrade(blocks.size());
        }

    }

    public boolean isGameOver() {
        //TODO: Game end check. (If no possible move with Blocks in Bag + Hands).
        return false;
    }

    public Board getBoard() {
        return board;
    }

}
