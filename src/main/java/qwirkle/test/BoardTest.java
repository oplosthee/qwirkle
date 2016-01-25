package qwirkle.test;

import org.junit.Before;
import org.junit.Test;

import qwirkle.game.Block;
import qwirkle.game.Board;
import qwirkle.game.exception.InvalidMoveException;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BoardTest {

    private Board board;
    private Map<Point, Block> move;

    @Before
    public void init() {
        board = new Board();
        move = new HashMap<>();
    }

    @Test
    public void testPlaceBlock_singleBlock() throws InvalidMoveException {

        // Create three blocks next to each other.
        Block testBlock0 = new Block(Block.Shape.SQUARE, Block.Color.BLUE);

        Point testPos0 = new Point(0,0);

        // Add the blocks to a new move.
        move.put(testPos0, testBlock0);

        board.placeBlock(move);
    }

    @Test
    public void testPlaceBlock_validMove() throws InvalidMoveException {

        // Create three blocks next to each other.
        Block testBlock0 = new Block(Block.Shape.SQUARE, Block.Color.BLUE);
        Block testBlock1 = new Block(Block.Shape.SQUARE, Block.Color.RED);
        Block testBlock2 = new Block(Block.Shape.SQUARE, Block.Color.ORANGE);

        Point testPos0 = new Point(0,0);
        Point testPos1 = new Point(1,0);
        Point testPos2 = new Point(2,0);

        // Add the blocks to a new move.
        move.put(testPos0, testBlock0);
        move.put(testPos1, testBlock1);
        move.put(testPos2, testBlock2);

        board.placeBlock(move);
    }

    @Test (expected = InvalidMoveException.class)
    public void testPlaceBlock_brokenLine() throws InvalidMoveException {

        // Create two blocks next to each other, and one block one position away from the other blocks.
        Block testBlock0 = new Block(Block.Shape.SQUARE, Block.Color.BLUE);
        Block testBlock1 = new Block(Block.Shape.SQUARE, Block.Color.RED);
        Block testBlock2 = new Block(Block.Shape.SQUARE, Block.Color.ORANGE);

        Point testPos0 = new Point(0,0);
        Point testPos1 = new Point(1,0);
        Point testPos2 = new Point(3,0);

        // Add the blocks to a new move.
        move.put(testPos0, testBlock0);
        move.put(testPos1, testBlock1);
        move.put(testPos2, testBlock2);

        // Place the blocks on the board.
        board.placeBlock(move);
    }

    @Test (expected = InvalidMoveException.class)
    public void testPlaceBlock_duplicateBlock() throws InvalidMoveException {

        // Create three blocks next to each other.
        Block testBlock0 = new Block(Block.Shape.SQUARE, Block.Color.BLUE);
        Block testBlock1 = new Block(Block.Shape.SQUARE, Block.Color.RED);
        Block testBlock2 = new Block(Block.Shape.SQUARE, Block.Color.RED);

        Point testPos0 = new Point(0,0);
        Point testPos1 = new Point(1,0);
        Point testPos2 = new Point(2,0);

        // Add the blocks to a new move.
        move.put(testPos0, testBlock0);
        move.put(testPos1, testBlock1);
        move.put(testPos2, testBlock2);

        board.placeBlock(move);
    }

    @Test (expected = InvalidMoveException.class)
    public void testPlaceBlock_differentSet() throws InvalidMoveException {

        // Create three blocks next to each other.
        Block testBlock0 = new Block(Block.Shape.SQUARE, Block.Color.BLUE);
        Block testBlock1 = new Block(Block.Shape.SQUARE, Block.Color.RED);
        Block testBlock2 = new Block(Block.Shape.CIRCLE, Block.Color.ORANGE);

        Point testPos0 = new Point(0,0);
        Point testPos1 = new Point(1,0);
        Point testPos2 = new Point(2,0);

        // Add the blocks to a new move.
        move.put(testPos0, testBlock0);
        move.put(testPos1, testBlock1);
        move.put(testPos2, testBlock2);

        board.placeBlock(move);
    }
    
}
