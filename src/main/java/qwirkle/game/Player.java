package main.java.qwirkle.game;

import java.util.HashSet;
import java.util.Set;

public class Player {

    private String name;
    private int score;
    private Set<Block> hand;

    /**
     * Constructs a new player, which sets the name, a new hand and sets the score on 0.
     * @param name
     */
    public Player(String name) {
        setName(name);
        hand = new HashSet<>();
        score = 0;
    }

    /**
     * Puts 1 block on the board and removes the block out of your hand.
     * @param block
     */
    public void addBlock2(Block block) { //Tom IntelliJ zeurt er over dat het 2 methods zijn met de zelfde naam, fix het.
        hand.remove(block); //is het remove of add? ik zou denken remove, want je haalt ze uit je hand op het board.
    }

    /**
     * Puts multiple blocks on the board and removes the blocks out of your hand.
     * @param blockSet
     */
    public void addBlock(HashSet<Block> blockSet) {
        hand.remove(blockSet); //kan dit zo of moet dit met een loop? Ik denk zelf wel met een loop.
    }

    /**
     * Removes a block from the board, which you just putted on the board.
     * @param block
     */
    public void removeBlock(Block block) {
        hand.add(block); //hier dus weer dezelfde vraag, hierm oet add denk ik, want je pakt ze van het board.
    }

    /**
     * Sets the name of an player.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the player.
     * @return the name of the player.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Edits the score with the amount score.
     * @param score
     */
    public void addScore(int score) {
        this.score = this.score + score;
    }
}