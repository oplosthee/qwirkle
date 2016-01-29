package qwirkle.game;

import qwirkle.client.ClientView;
import qwirkle.util.ProtocolFormatter;

import java.util.List;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RobotPlayer extends Player {

    private int difficulty;

    /**
     * Constructs a new player, which sets the name, a new hand and sets the score on 0.
     *
     * @param name name for the Player
     */
    public RobotPlayer(String name, int difficulty, ClientView view) {
        super(name, view);
        this.difficulty = difficulty;
    }

    @Override
    public String determineMove() {
        int[] boundaries = getGame().getBoard().getBoundaries();

        Block moveBlock = null;
        Point movePoint = null;
        int moveScore = 0;
        long endTime = (System.currentTimeMillis() / 1000) + difficulty;

        for (Block block : getHand()) {
            for (int x = boundaries[0]; x < boundaries[2] + 1; x++) {
                for (int y = boundaries[1]; y < boundaries[3] + 1; y++) {

                    if ((System.currentTimeMillis() / 1000) > endTime) {
                        return getMove(moveBlock, movePoint);
                    }

                    Point point = new Point(x,y);
                    Map<Point, Block> move = new HashMap<>();
                    move.put(point, block);
                    if (getGame().getBoard().isValidMove(move)) {
                        int currentMoveScore = getGame().getBoard().getPoints(move);
                        if (currentMoveScore > moveScore ) {
                            moveBlock = block;
                            movePoint = point;
                            moveScore = currentMoveScore;
                        }
                    }
                }
            }
        }

        return getMove(moveBlock, movePoint);
    }

    public String getMove(Block block, Point point) {
        if (block == null) {
            List<Block> tradeBlocks = getHand().stream().collect(Collectors.toList());
            ProtocolFormatter.moveTrade(tradeBlocks);
        }

        Map<Point, Block> move = new HashMap<>();
        move.put(point, block);

        return ProtocolFormatter.clientMovePut(move);
    }
}
