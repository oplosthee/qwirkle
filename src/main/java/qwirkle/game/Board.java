package qwirkle.game;

import qwirkle.game.exception.InvalidMoveException;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Board extends Observable {

    private Map<Point, Block> board;

    /**
     * Creates a new Board with a HashMap
     */
    public Board() {
        board = new HashMap<>();
    }

    /**
     * it sets a block on a board and notifies the observers
     * @param point this is a x and y coordinate
     * @param block this is a block with a color and a shape
     */
    public void setBlock(Point point, Block block) {
        board.put(point, block);
        setChanged();
        notifyObservers();
    }

    /**
     * sets multiple blocks in one move
     * @param move is a map with a point and a block
     */
    public void setBlock(Map<Point, Block> move) {
        for (Map.Entry<Point, Block> block : move.entrySet()) {
            setBlock(block.getKey(), block.getValue());
        }
    }

    /**
     * Returns true if a move is possible and returns false when a move is not possible
     * @param blocks is list with blocks
     * @return true  when a move is possible and false when not
     */
    public boolean isMovePossible(List<Block> blocks) {
        if (board.size() == 0) {
            return true;
        }

        int[] boundaries = getBoundaries();

        for (Block block : blocks) {
            for (int x = boundaries[0]; x < boundaries[2] + 1; x++) {
                for (int y = boundaries[1]; y < boundaries[3] + 1; y++) {
                    Point point = new Point(x,y);
                    Map<Point, Block> move = new HashMap<>();
                    move.put(point, block);
                    if (isValidMove(move)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * places multiple block on a board
     * @param blocks is map with points and blocks
     * @throws InvalidMoveException if the move is invalid
     */
    public void placeBlock(Map<Point, Block> blocks) throws InvalidMoveException {
        if (!isValidMove(blocks)) {
            throw new InvalidMoveException();
        }

        for (Map.Entry<Point, Block> entry : blocks.entrySet()) {
            board.put(entry.getKey(), entry.getValue());
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Returns true if a move is valid and false when the move is not valid
     * @param blocks is a map with point and blocks
     * @return true if the move is valid and false when the move is not valid
     */
    public boolean isValidMove(Map<Point, Block> blocks) {
        for (Map.Entry<Point, Block> entry : blocks.entrySet()) {
            if (board.get(entry.getKey()) != null) {
                System.out.println("Debug (Board) - Move tried to overwrite existing Block.");
                return false; // Check whether all blocks are placed on an empty spot.
            }
        }

        // Check whether the position of the blocks in the move is allowed.
        if (isEmpty()) {
            return isLine(blocks) && isAllowedInLine(blocks);
        } else {
            return isLine(blocks) && hasNeighbors(blocks) && isAllowedInLine(blocks);
        }
    }

    /**
     * checks if the blocks are allowed in the line
     * @param blocks is map with points and blocks
     * @return true if the blocks are allowed in a line en false if not
     */
    public boolean isAllowedInLine(Map<Point, Block> blocks) {
        Map<Point, Block> boardCopy = new HashMap<>(board);
        boardCopy.putAll(blocks);

        for (Map.Entry<Point, Block> entry : blocks.entrySet()) {
            Block block = entry.getValue();
            Point point = entry.getKey();

            // Check one block to the left.
            if (!block.isAllowedNeighbor(boardCopy.get(new Point(point.x - 1, point.y)))) {
                System.out.println("Debug (Board) - isAllowedInLine: One to left ERROR.");
                return false;
            }
            // Check one block above.
            if (!block.isAllowedNeighbor(boardCopy.get(new Point(point.x, point.y + 1)))) {
                System.out.println("Debug (Board) - isAllowedInLine: One to top ERROR.");
                return false;
            }
            // Check one block to the right.
            if (!block.isAllowedNeighbor(boardCopy.get(new Point(point.x + 1, point.y)))) {
                System.out.println("Debug (Board) - isAllowedInLine: One to right ERROR.");
                return false;
            }
            // Check one block below.
            if (!block.isAllowedNeighbor(boardCopy.get(new Point(point.x, point.y - 1)))) {
                System.out.println("Debug (Board) - isAllowedInLine: One to bottom ERROR.");
                return false;
            }

            // Check two blocks to the left.
            if (boardCopy.get(new Point(point.x - 1, point.y)) != null
                    && !block.isAllowedNeighbor(boardCopy.get(new Point(point.x - 2, point.y)))) {
                System.out.println("Debug (Board) - isAllowedInLine: Two to left ERROR.");
                return false;
            }
            // Check two blocks above.
            if (boardCopy.get(new Point(point.x, point.y + 1)) != null
                    && !block.isAllowedNeighbor(boardCopy.get(new Point(point.x, point.y + 2)))) {
                System.out.println("Debug (Board) - isAllowedInLine: Two to top ERROR.");
                return false;
            }
            // Check two blocks to the right.
            if (boardCopy.get(new Point(point.x + 1, point.y)) != null
                    && !block.isAllowedNeighbor(boardCopy.get(new Point(point.x + 2, point.y)))) {
                System.out.println("Debug (Board) - isAllowedInLine: Two to right ERROR.");
                return false;
            }
            // Check two blocks below.
            if (boardCopy.get(new Point(point.x, point.y - 1)) != null
                    && !block.isAllowedNeighbor(boardCopy.get(new Point(point.x, point.y - 2)))) {
                System.out.println("Debug (Board) - isAllowedInLine: Two to bottom ERROR.");
                return false;
            }

            // Check whether block already exists in the vertical and horizontal line on which it was placed.
            int occurrences = 0;
            Map<Point, Block> horizontalLine = getHorizontalLine(point, boardCopy);
            for (Map.Entry<Point, Block> lineEntry : horizontalLine.entrySet()) {
                if (lineEntry.getValue().equals(block)) {
                    if (++occurrences > 1) {
                        return false;
                    }
                }
            }

            occurrences = 0;
            Map<Point, Block> verticalLine = getVerticalLine(point, boardCopy);
            for (Map.Entry<Point, Block> lineEntry : verticalLine.entrySet()) {
                if (lineEntry.getValue().equals(block)) {
                    if (++occurrences > 1) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * checks if the blocks are in a line.
     * @param blocks is map with points and blocks
     * @return true if is in line en false when not
     */
    public boolean isLine(Map<Point, Block> blocks) {
        if (blocks.size() == 1) {
            return true;
        }

        boolean isLine = true;

        Map<Point, Block> boardCopy = new HashMap<>(board);
        boardCopy.putAll(blocks);

        Point point = blocks.entrySet().stream()
                .findAny()
                .get()
                .getKey();

        if (isHorizontal(blocks)) {

            // Check whether all blocks of the move are in the horizontal line.
            for (Map.Entry<Point, Block> entry : blocks.entrySet()) {
                if (getHorizontalLine(point, boardCopy).get(entry.getKey()) != entry.getValue()) {
                    isLine = false;
                }
            }
        } else if (isVertical(blocks)) {

            // Check whether all blocks of the move are in the horizontal line.
            for (Map.Entry<Point, Block> entry : blocks.entrySet()) {
                if (getVerticalLine(point, boardCopy).get(entry.getKey()) != entry.getValue()) {
                    isLine = false;
                }
            }
        } else {
            isLine = false;
        }

        return isLine;
    }

    /**
     * checks if the blocks have neighbours
     * @param blocks is a map with points and blocks
     * @return true if the blocks has neighbours and false if not
     */
    public boolean hasNeighbors(Map<Point, Block> blocks) {
        Map<Point, Block> boardCopy = new HashMap<>(board);
        boardCopy.putAll(blocks);

        List<Block> neighbors = new ArrayList<>();

        for (Map.Entry<Point, Block> entry : blocks.entrySet()) {
            // Add the neighbor if it is not in the list already.
            getNeighbors(entry.getKey(), boardCopy).stream().filter(neighbor ->
                    !neighbors.contains(neighbor)).forEach(neighbors::add);
        }

        for (Map.Entry<Point, Block> entry : blocks.entrySet()) {
            // Remove the move from the neighbors, leaving only the blocks that touch the move.
            neighbors.remove(entry.getValue());
        }

        return neighbors.size() != 0;
    }

    /**
     * Returns the neighbours of a block
     * @param point is a coordinate with x and y
     * @param boardCopy is map with points and blocks
     * @return a list with the neighbours of a block
     */
    public List<Block> getNeighbors(Point point, Map<Point, Block> boardCopy) {
        List<Block> neighbors = new ArrayList<>();

        Block blockLeft = boardCopy.get(new Point(point.x - 1, point.y));
        Block blockUp = boardCopy.get(new Point(point.x, point.y + 1));
        Block blockRight = boardCopy.get(new Point(point.x + 1, point.y));
        Block blockDown = boardCopy.get(new Point(point.x, point.y - 1));

        if (blockLeft != null) {
            neighbors.add(blockLeft);
        }
        if (blockUp != null) {
            neighbors.add(blockUp);
        }
        if (blockRight != null) {
            neighbors.add(blockRight);
        }
        if (blockDown != null) {
            neighbors.add(blockDown);
        }

        return neighbors;
    }

    /**
     * returns the horizontal line
     * @param point is coordinate with x and y
     * @param boardCopy is a map with points and blocks
     * @return a map with points and blocks
     */
    public Map<Point, Block> getHorizontalLine(Point point, Map<Point, Block> boardCopy) {
        // A Map to store the horizontal line on which the move was on.
        Map<Point, Block> horizontalLine = new HashMap<>();

        // Horizontal line to the left.
        boolean endLeft = false;
        int i = 0;
        while (!endLeft) {
            Point position = new Point(point.x - i, point.y);
            Block block = boardCopy.get(position);

            if (block == null) {
                endLeft = true;
            } else {
                horizontalLine.put(position, block);
                i++;
            }
        }

        // Horizontal line to the right.
        boolean endRight = false;
        i = 1;
        while (!endRight) {
            Point position = new Point(point.x + i, point.y);
            Block block = boardCopy.get(position);

            if (block == null) {
                endRight = true;
            } else {
                horizontalLine.put(position, block);
                i++;
            }
        }

        return horizontalLine;
    }

    /**
     * gets the vertical line of a block
     * @param point is a coordinate with x and y
     * @param boardCopy is a map with points and blocks
     * @return a map with point and blocks
     */
    public Map<Point, Block> getVerticalLine(Point point, Map<Point, Block> boardCopy) {
        // A Map to store the vertical line on which the move was on.
        Map<Point, Block> verticalLine = new HashMap<>();

        // Vertical line to the bottom.
        boolean endDown = false;
        int i = 0;
        while (!endDown) {
            Point position = new Point(point.x, point.y - i);
            Block block = boardCopy.get(position);

            if (block == null) {
                endDown = true;
            } else {
                verticalLine.put(position, block);
                i++;
            }
        }

        // Vertical line to the top.
        boolean endUp = false;
        i = 1;
        while (!endUp) {
            Point position = new Point(point.x, point.y + i);
            Block block = boardCopy.get(position);

            if (block == null) {
                endUp = true;
            } else {
                verticalLine.put(position, block);
                i++;
            }
        }

        return verticalLine;
    }

    /**
     * returns the points
     * @param blocks is a map with points and blocks
     * @return the points
     */
    public int getPoints(Map<Point, Block> blocks) {
        List<Map<Point, Block>> lines = new ArrayList<>();
        int score = 0;

        if (board.size() == 1 && blocks.size() == 1) {
            return 1;
        }

        for (Map.Entry<Point, Block> entry : blocks.entrySet()) {
            Map<Point, Block> verticalLine = getVerticalLine(entry.getKey(), board);
            Map<Point, Block> horizontalLine = getHorizontalLine(entry.getKey(), board);

            if (verticalLine.size() > 1 && !lines.contains(verticalLine)) {
                if (verticalLine.size() > 5) {
                    score = score + 6; // Add qwirkle bonus.
                }
                score = score + verticalLine.size();
                lines.add(verticalLine);
            }

            if (horizontalLine.size() > 1 && !lines.contains(horizontalLine)) {
                if (horizontalLine.size() > 5) {
                    score = score + 6; // Add qwirkle bonus.
                }
                score = score + horizontalLine.size();
                lines.add(horizontalLine);
            }
        }

        return score;
    }

    /**
     * checks if the blocks are horizontal
     * @param blocks is a map with points and blocks
     * @return true if it is horizontal and false if not
     */
    public boolean isHorizontal(Map<Point, Block> blocks) {
        java.util.List<Point> points = new ArrayList<>(blocks.keySet());
        return points.get(0).y == points.get(1).y;
    }

    /**
     * checks if the blocks are vertical
     * @param blocks is a map with points and blocks
     * @return true if it is vertical and false if not
     */
    public boolean isVertical(Map<Point, Block> blocks) {
        java.util.List<Point> points = new ArrayList<>(blocks.keySet());
        return points.get(0).x == points.get(1).x;
    }

    /**
     * checks if the board is empty
     * @return true if the board is empty and false when not
     */
    public boolean isEmpty() {
        return board.size() == 0;
    }

    /**
     * gives the boundaries
     * @return an int array with the boundaries
     */
    public int[] getBoundaries() {
        int[] boundaries = {0, 0, 0, 0};
        //[0]: lowX - [1]: lowY - [2]: highX - [3]: highY
        for (Point point : board.keySet()) {
            if (point.x <= boundaries[0]) {
                boundaries[0] = point.x - 1;
            }
            if (point.y <= boundaries[1]) {
                boundaries[1] = point.y - 1;
            }
            if (point.x >= boundaries[2]) {
                boundaries[2] = point.x + 1;
            }
            if (point.y >= boundaries[3]) {
                boundaries[3] = point.y + 1;
            }
        }

        return boundaries;
    }

    @Override
    public String toString() {
        int[] boundaries = getBoundaries();
        String output = "";

        for (int y = boundaries[1] - 2; y < boundaries[3] + 3; y++) {
            for (int x = boundaries[0] - 2; x < boundaries[2] + 3; x++) {
                Block block = board.get(new Point(x, y * - 1));
                output += String.format("[%5s]", block == null ? x +","+y * - 1 : block.toString());
            }
            output += "\n";
        }

        return output;
    }

}
