package qwirkle.client;

import qwirkle.game.Block;
import qwirkle.game.Board;
import qwirkle.game.Player;

import java.awt.*;

public class ClientGame {

    private Board board;
    private Player player;

    public ClientGame(Player player) {
        this.board = new Board();
        this.player = player;
    }

    public void doMove(String[] moves) {
        for (String move : moves) {
            int blockId = Integer.parseInt(move.split("@")[0]);
            int blockX = Integer.parseInt(move.split("@")[1].split(",")[0]);
            int blockY = Integer.parseInt(move.split("@")[1].split(",")[1]); // TODO: Exception handling, this could go wrong in many ways.

            board.setBlock(new Point(blockX, blockY), new Block(blockId));
        }
    }

    public void drawTile(String[] blocks) {
        for (String blockCode : blocks) {
            //TODO: parseInt error handling.
            player.addBlock(new Block(Integer.parseInt(blockCode)));
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Board getBoard() {
        return board;
    }

}
