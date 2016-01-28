package qwirkle.test;

import org.junit.Test;
import qwirkle.game.Block;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockTest {

    @Test
    public void testisAllowedNeighbour_sameBlock() {
        Block testBlock0 = new Block(Block.Shape.SQUARE, Block.Color.BLUE);
        Block testBlock1 = new Block(Block.Shape.SQUARE, Block.Color.BLUE);

        assertFalse(testBlock0.isAllowedNeighbor(testBlock1));
    }

    @Test
    public void testisAllowedNeighbour_sameColor() {
        Block testBlock0 = new Block(Block.Shape.SQUARE, Block.Color.BLUE);
        Block testBlock1 = new Block(Block.Shape.CIRCLE, Block.Color.BLUE);

        assertTrue(testBlock0.isAllowedNeighbor(testBlock1));
    }

    @Test
    public void testisAllowedNeighbour_sameShape() {
        Block testBlock0 = new Block(Block.Shape.SQUARE, Block.Color.BLUE);
        Block testBlock1 = new Block(Block.Shape.SQUARE, Block.Color.RED);

        assertTrue(testBlock0.isAllowedNeighbor(testBlock1));
    }

    @Test
    public void testisAllowedNeighbour_differentBlock() {
        Block testBlock0 = new Block(Block.Shape.SQUARE, Block.Color.BLUE);
        Block testBlock1 = new Block(Block.Shape.CIRCLE, Block.Color.RED);

        assertFalse(testBlock0.isAllowedNeighbor(testBlock1));
    }

}
