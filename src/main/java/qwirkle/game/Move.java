package qwirkle.game;

import java.awt.*;
import java.util.Map;
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
    private Set<Block> tradeBlocks;
    private Map<Point, Block> putBlocks;

    public Move(Player player, Type type, Set<Block> tradeBlocks) {
        this.player = player;
        this.type = type;
        this.tradeBlocks = tradeBlocks;
    }

    public Move(Player player, Type type, Map<Point, Block> putBlocks) {
        this.player = player;
        this.type = type;
        this.putBlocks = putBlocks;
    }

    public Type getType() {
        return type;
    }

    public Player getPlayer() {
        return player;
    }

    public Map<Point, Block> getPutBlocks() {
        return putBlocks;
    }

    public Set<Block> getTradeBlocks() {
        return tradeBlocks;
    }

}
