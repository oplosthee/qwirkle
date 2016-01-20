package qwirkle.game;

import qwirkle.game.exception.GameFullException;
import qwirkle.game.exception.InvalidMoveException;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Game {

    private Board board;
    private Bag bag;
    private Set<Player> players;
    private static final int MAXIMUM_PLAYER_AMOUNT = 4;

    public Game() {
        this.board = new Board();
        this.bag = new Bag();
        players = new HashSet<>();
    }

    public boolean addPlayer(Player player) throws GameFullException {
        if (players.size() <= MAXIMUM_PLAYER_AMOUNT) {
            return players.add(player);
        } else {
            throw new GameFullException();
        }
    }

    public boolean doMove(Move move) {
        switch(move.getType()) {
            case PUT:
                return placeBlock(move.getPlayer(), move.getPositions(), move.getBlocks());
            case TRADE:
                //TODO: Implement TRADE Move Type.
                return true;
            case PASS:
                //TODO: Implement PASS Move Type.
                return true;
            default:
                return false;
        }
    }

    public boolean placeBlock(Player player, Set<Point> positions, Set<Block> blocks) {
        try  {
            board.setBlock(positions, blocks);

            //TODO: Add score calculation.
            player.addScore(1000);
            return true;
        } catch (InvalidMoveException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Block takeBlockFromBag() {
        return bag.takeRandomBlock();
    }
    
    public Set<Block> takeHandFromBag() {
        Set<Block> hand = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            hand.add(bag.takeRandomBlock());
        }

        return hand;
    }

}
