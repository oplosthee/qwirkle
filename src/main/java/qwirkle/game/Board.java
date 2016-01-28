package qwirkle.game;

import qwirkle.game.exception.InvalidMoveException;

import java.util.List;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Board {

    private Map<Point, Block> board;

    public Board() {
        board = new HashMap<>();
    }

    public void setBlock(Point point, Block block) {
        board.put(point, block);
    }

    //TODO: Add boolean isMovePossible(List<Block> blocks) method.

    public void placeBlock(Map<Point, Block> blocks) throws InvalidMoveException {
        if (!isValidMove(blocks)) {
            throw new InvalidMoveException();
        }

        for (Map.Entry<Point, Block> entry : blocks.entrySet()) {
            board.put(entry.getKey(), entry.getValue());
        }
    }

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

    public boolean isAllowedInLine(Map<Point, Block> blocks) {
        Map<Point, Block> boardCopy = new HashMap<>(board);
        boardCopy.putAll(blocks);

        //TODO: Improve algorithm (if possible) to check whether a block is allowed in a line (color/shape check).
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
            if (boardCopy.get(new Point(point.x - 1, point.y)) != null && !block.isAllowedNeighbor(boardCopy.get(new Point(point.x - 2, point.y)))) {
                System.out.println("Debug (Board) - isAllowedInLine: Two to left ERROR.");
                return false;
            }
            // Check two blocks above.
            if (boardCopy.get(new Point(point.x, point.y + 1)) != null && !block.isAllowedNeighbor(boardCopy.get(new Point(point.x, point.y + 2)))) {
                System.out.println("Debug (Board) - isAllowedInLine: Two to top ERROR.");
                return false;
            }
            // Check two blocks to the right.
            if (boardCopy.get(new Point(point.x + 1, point.y)) != null && !block.isAllowedNeighbor(boardCopy.get(new Point(point.x + 2, point.y)))) {
                System.out.println("Debug (Board) - isAllowedInLine: Two to right ERROR.");
                return false;
            }
            // Check two blocks below.
            if (boardCopy.get(new Point(point.x, point.y - 1)) != null && !block.isAllowedNeighbor(boardCopy.get(new Point(point.x, point.y - 2)))) {
                System.out.println("Debug (Board) - isAllowedInLine: Two to bottom ERROR.");
                return false;
            }

            // Check whether block already exists in the vertical and horizontal line on which it was placed.
            int occurrences = 0;
            Map<Point, Block> horizontalLine = getHorizontalLine(point, boardCopy);
            for (Map.Entry<Point, Block> lineEntry : horizontalLine.entrySet()) {
                if (lineEntry.getValue().equals(block)) {
                    if (++occurrences > 1) {
                        System.out.println("Debug (Board) - isAllowedInLine: Block already exists in horizontal line.");
                        return false;
                    }
                }
            }

            occurrences = 0;
            Map<Point, Block> verticalLine = getVerticalLine(point, boardCopy);
            for (Map.Entry<Point, Block> lineEntry : verticalLine.entrySet()) {
                if (lineEntry.getValue().equals(block)) {
                    if (++occurrences > 1) {
                        System.out.println("Debug (Board) - isAllowedInLine: Block already exists in vertical line.");
                        return false;
                    }
                }
            }
        }

        return true;
    }

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
            System.out.println("Debug (Board) - isLine: Started horizontal line check.");

            // Check whether all blocks of the move are in the horizontal line.
            for (Map.Entry<Point, Block> entry : blocks.entrySet()) {
                if (getHorizontalLine(point, boardCopy).get(entry.getKey()) != entry.getValue()) {
                    isLine = false;
                }
            }
        } else if (isVertical(blocks)) {
            System.out.println("Debug (Board) - isLine: Started vertical line check.");

            // Check whether all blocks of the move are in the horizontal line.
            for (Map.Entry<Point, Block> entry : blocks.entrySet()) {
                if (getVerticalLine(point, boardCopy).get(entry.getKey()) != entry.getValue()) {
                    isLine = false;
                }
            }
        } else {
            isLine = false;
            System.out.println("Debug (Board) - isLine: Line is not vertical/horizontal.");
        }

        return isLine;
    }

    public boolean hasNeighbors(Map<Point, Block> blocks) {
        Map<Point, Block> boardCopy = new HashMap<>(board);
        boardCopy.putAll(blocks);

        List<Block> neighbors = new ArrayList<>();

        for (Map.Entry<Point, Block> entry : blocks.entrySet()) {
            // Add the neighbor if it is not in the list already.
            getNeighbors(entry.getKey(), boardCopy).stream().filter(neighbor -> !neighbors.contains(neighbor)).forEach(neighbors::add);
        }

        for (Map.Entry<Point, Block> entry : blocks.entrySet()) {
            neighbors.remove(entry.getValue()); // Remove the move from the neighbors, leaving only the blocks that touch the move.
        }

        return neighbors.size() != 0;
    }

    public List<Block> getNeighbors(Point point, Map<Point, Block> boardCopy) {
        List<Block> neighbors = new ArrayList<>();

        //TODO: Improve Neighbor Block getting.
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

    public int getPoints(Map<Point, Block> blocks) {
        List<Map<Point, Block>> lines = new ArrayList<>();
        int score = 0;

        //TODO: Remove this when proper first turn check has been implemented.
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

    public boolean isHorizontal(Map<Point, Block> blocks) {
        java.util.List<Point> points = new ArrayList<>(blocks.keySet());
        return points.get(0).y == points.get(1).y;
    }

    public boolean isVertical(Map<Point, Block> blocks) {
        java.util.List<Point> points = new ArrayList<>(blocks.keySet());
        return points.get(0).x == points.get(1).x;
    }

    public boolean isEmpty() {
        return board.size() == 0;
    }

    @Override
    public String toString() {
        int lowY = 0;
        int highY = 0;
        int lowX = 0;
        int highX = 0;

        String output = "";

        for (Point point : board.keySet()) {
            if (point.x <= lowX) {
                lowX = point.x;
            }
            if (point.y <= lowY) {
                lowY = point.y;
            }
            if (point.x >= highX) {
                highX = point.x;
            }
            if (point.y >= highY) {
                highY = point.y;
            }
        }

        for (int x = lowX - 2; x < highX + 3; x++) {
            for (int y = lowY - 2; y < highY + 3; y++) {
                Block block = board.get(new Point(x, y));
                output += String.format("[%5s]", block == null ? x +","+y : block.toString());
            }
            output += "\n";
        }

        return output;
    }

}
