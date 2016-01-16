package qwirkle.game;

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

    private Game game;
    private Player player;
    private Type type;
    private Set<Block> blocks;


    public Move(Game game, Player player, Type type, Set<Block> blocks) {
        this.game = game;
        this.player = player;
        this.type = type;
        this.blocks = blocks;
    }

    public boolean doMove() {
        switch(type) {
            case PUT:
                //TODO: Implement PUT Move Type.
                return true;
            case TRADE:
                //TODO: Implement TRADE Move Type.
                return true;
            case PASS:
                //TODO: Implement PASS Move Type.
                return true;
            default:
                return false;
        }
    }

}
