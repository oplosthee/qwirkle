package qwirkle.game;

import qwirkle.game.exception.InvalidMoveException;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Board {

    private Map<Point, Block> board;

    public Board() {
        board = new HashMap<>();
    }

    public void setBlock(Set<Point> positions, Set<Block> blocks) throws InvalidMoveException {

        for (Block block : blocks) {
            for (Point position : positions) {
                if (!isValidMove(position, block)) {
                    throw new InvalidMoveException();
                }
            }
        }

        for (Block block : blocks) {
            for (Point position : positions) {
                board.put(position, block);
            }
        }
    }

    public boolean isValidMove(Point position, Block block) {
        //TODO: Implement checking whether a Block can be placed on empty Point.
        return board.get(position) == null;
    }

    public Map<Point, Block> getBoard() {
        return board;
    }

}
