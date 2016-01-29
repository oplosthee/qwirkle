package qwirkle.game;

import org.omg.CORBA.ORBPackage.InvalidName;
import qwirkle.client.ClientView;
import qwirkle.game.exception.InvalidMoveException;
import qwirkle.server.ServerGame;
import qwirkle.server.exception.InvalidNameException;
import qwirkle.shared.net.IProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mathay Kahraman
 * @author Tom Leemreize
 */
public abstract class Player {

    private String name;
    private int score;
    private List<Block> hand;
    private ServerGame game;
    public ClientView view;
    public List<Block> lastMove; // Stores the last move a player made, used to restore the player's hand when the move was invalid.

    /**
     * Constructs a new player, which sets the name, a new hand and sets the score on 0.
     *
     * @param name name for the Player
     */
    public Player(String name, ClientView view) {
        lastMove = new ArrayList<>();
        setName(name);
        hand = new ArrayList<>();
        score = 0;
        this.view = view;
    }

    /**
     * Adds a Block to the Player's hand.
     *
     * @param block Block to be added to the Player's hand
     */
    public void addBlock(Block block) {
        hand.add(block);
    }

    public void addBlock(List<Block> block) {
        hand.addAll(block);
    }

    /**
     * Removes a Block from the Player's hand.
     *
     * @param block Block to be removed from the Player's hand
     */
    public void removeBlock(Block block) {
        if (getHand().contains(block)) {
            hand.remove(block);
        }
    }

    public List<Block> getHand() {
        return hand;
    }

    /**
     * Sets the name of an player.
     *
     * @param name String to which the Player's name should be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the Player.
     *
     * @return the name of the Player
     */
    public String getName() {
        return this.name;
    }

    /**
     * Adds score the the Player's current score.
     *
     * @param score score to be added to the Player's current score
     */
    public void addScore(int score) {
        this.score = this.score + score;
    }

    /**
     * Returns the Player's current score.
     *
     * @return the score of this Player
     */
    public int getScore() {
        return score;
    }

    public void setGame(ServerGame game) {
        this.game = game;
    }

    public ServerGame getGame() {
        return game;
    }

    public abstract String determineMove();

}