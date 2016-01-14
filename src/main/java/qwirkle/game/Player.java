package qwirkle.game;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO: Finish Javadoc for Player class.
 *
 * @author Mathay Kahraman
 * @author Tom Leemreize
 */
public class Player {

    private String name;
    private int score;
    private Set<Block> hand;

    /**
     * Constructs a new player, which sets the name, a new hand and sets the score on 0.
     *
     * @param name name for the Player
     */
    public Player(String name) {
        setName(name);
        hand = new HashSet<>();
        score = 0;
    }

    /**
     * Adds a Block to the Player's hand.
     *
     * @param block Block to be added to the Player's hand
     */
    public void addBlock(Block block) {
        hand.add(block);
    }

    /**
     * Removes a Block from the Player's hand.
     *
     * @param block Block to be removed from the Player's hand
     */
    public void removeBlock(Block block) {
        //TODO: Check whether the Block exists in the Player's hand.
        hand.remove(block);
    }

    public Set<Block> getHand() {
        return hand;
    }

    /**
     * Sets the name of an player.
     *
     * @param name String to which the Player's name should be set
     */
    public void setName(String name) {
        //TODO: Check whether a Player's name meets the requirements.
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
}