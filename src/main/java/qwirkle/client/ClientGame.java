package qwirkle.client;

import qwirkle.game.*;
import qwirkle.game.exception.GameFullException;
import qwirkle.game.exception.InvalidMoveException;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ClientGame {

    private Board board;
    private Bag bag;
    private List<Player> players;
    private int turnIndex;
    private static final int MAXIMUM_PLAYER_AMOUNT = 4;

    public ClientGame() {
        this.board = new Board();
        this.bag = new Bag();
        players = new ArrayList<>();
        turnIndex = 0;
    }

    public boolean addPlayer(Player player) throws GameFullException {
        if (players.size() <= MAXIMUM_PLAYER_AMOUNT) {
            return players.add(player);
        } else {
            throw new GameFullException();
        }
    }

    public boolean doMove(int temporaryOldMovePosition) {
        /*switch(move.getType()) {
            case PUT:
                return placeBlock(move.getPlayer(), move.getPutBlocks());
            case TRADE:
                return true;
            case PASS:
                return true;
            default:
                return false;
        }*/
        return false;
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

    public Board getBoard() {
        return board;
    }

}
