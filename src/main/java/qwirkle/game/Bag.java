package qwirkle.game;

import java.util.HashSet;
import java.util.Random;

/**
 * This class holds all available blocks for a game of Qwirkle.
 *
 * @author Tom Leemreize
 * @see HashSet
 */
public class Bag {

    /**
     * Amount of duplicate blocks in a Bag.
     */
    private static final int BLOCK_DUPLICATES_AMOUNT = 3;

    /**
     * Set containing all blocks in a Bag.
     */
    private HashSet<Block> blocks;

    /**
     * Constructs a new Bag containing 108 Block objects.
     */
    public Bag() {
        blocks = new HashSet<>();

        for (Block.Color color : Block.Color.values()) {
            for (Block.Shape shape : Block.Shape.values()) {
                for (int i = 0; i < BLOCK_DUPLICATES_AMOUNT; i++) {
                    blocks.add(new Block(shape, color));
                }
            }
        }
    }

    /**
     * Returns the amount of Blocks remaining in the Bag.
     *
     * @return the amount of Blocks remaining in the Bag
     */
    public int getSize() {
        return blocks.size();
    }

    /**
     * Returns a random Block from the Bag and removes this Block from the bag.
     *
     * @return a random Block from the Bag
     */
    public Block takeRandomBlock() {
        int randomBlock = new Random().nextInt(getSize());
        int currentBlock = 0;

        for (Block block : blocks) {
            if (currentBlock == randomBlock) {
                blocks.remove(block);
                return block;
            }

            currentBlock++;
        }

        return null;
    }

    /**
     * Adds the specified block to this Bag.
     *
     * @param block Block to be added to the Bag
     */
    public void addBlock(Block block) {
        blocks.add(block);
    }

}
