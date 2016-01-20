package qwirkle.game;

import java.awt.*;
import java.util.Set;

/**
 * TODO: Finish Javadoc for Move class.
 *
 * @author Mathay Kahraman
 * @author Tom Leemreize
 */
public class Move {

    public enum Type {
        PUT, TRADE, PASS
    }

    private Player player;
    private Type type;
    private Set<Point> positions;
    private Set<Block> blocks;


    public Move(Player player, Type type, Set<Point> positions, Set<Block> blocks) {
        this.player = player;
        this.type = type;
        this.positions = positions;
        this.blocks = blocks;
    }

    public Type getType() {
        return type;
    }

    public Player getPlayer() {
        return player;
    }

    public Set<Point> getPositions() {
        return positions;
    }

    public Set<Block> getBlocks() {
        return blocks;
    }

}
