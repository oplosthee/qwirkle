package qwirkle.client;

import qwirkle.game.Block;
import qwirkle.game.Board;
import qwirkle.game.Player;

import java.awt.*;
import java.text.NumberFormat;

public class ClientGame {

    private Board board;
    private Player player;

    public ClientGame(Player player) {
        this.board = new Board();
        this.player = player;
    }

    public void doMove(String[] moves) {
        for (String move : moves) {
            try {
                int blockId = Integer.parseInt(move.split("@")[0]);
                int blockX = Integer.parseInt(move.split("@")[1].split(",")[0]);
                int blockY = Integer.parseInt(move.split("@")[1].split(",")[1]);

                board.setBlock(new Point(blockX, blockY), new Block(blockId));
            } catch (NumberFormatException e) {
                System.out.println("Error: Tried to parse something that was not an integer!");
            }
        }
    }

    public void drawTile(String[] blocks) {
        for (String blockCode : blocks) {
            try {
                player.addBlock(new Block(Integer.parseInt(blockCode)));
            } catch (NumberFormatException e) {
                System.out.println("Error: Tried to parse something that was not an integer!");
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Board getBoard() {
        return board;
    }

}
