package qwirkle.util;

import qwirkle.game.Block;
import qwirkle.game.Player;
import qwirkle.game.SocketPlayer;
import qwirkle.shared.net.IProtocol;

import java.awt.*;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public final class ProtocolFormatter implements IProtocol {

    private static String removeLastComma(String string) {
        if (string.charAt(string.length() - 1) == ',') {
            return string.substring(0, string.length() - 1);
        }
        return string;
    }

    // Client formats

    public static String identify(String name) {
        return MessageFormat.format("{0} {1}", CLIENT_IDENTIFY, name);
    }

    public static String joinQueue(int[] queues) {
        String message = CLIENT_QUEUE + " ";

        for (int queue : queues) {
            message = message + queue + ",";
        }

        return removeLastComma(message);
    }

    public static String clientMovePut(Map<Point, Block> move) {
        String message = CLIENT_MOVE_PUT;

        for (Map.Entry<Point, Block> entry : move.entrySet()) {
            Block block = entry.getValue();
            Point point = entry.getKey();
            message = MessageFormat.format("{0} {1}@{2},{3}", message, block.getCode(), point.x, point.y);
        }

        return message;
    }

    public static String moveTrade(List<Block> blocks) {
        String message = CLIENT_MOVE_TRADE;

        for (Block block : blocks) {
            message = message + " " + block.getCode();
        }

        return message;
    }

    // Server formats

    public static String startGame(List<SocketPlayer> players) {
        String message = SERVER_GAMESTART;

        for (Player player : players) {
            message = message + " " + player.getName();
        }

        return message;
    }

    public static String serverMovePut(Map<Point, Block> move) {
        String message = SERVER_MOVE_PUT;

        for (Map.Entry<Point, Block> entry : move.entrySet()) {
            Block block = entry.getValue();
            Point point = entry.getKey();
            message = MessageFormat.format("{0} {1}@{2},{3}", message, block.getCode(), point.x, point.y);
        }

        return message;
    }

    public static String endGame(boolean won, Map<Integer, String> scores) {
        String message = SERVER_GAMEEND + (won ? " WIN " : " ERROR ");

        for (Map.Entry<Integer, String> entry : scores.entrySet()) {
            String score = entry.getKey().toString();
            String player = entry.getValue();
            message = message + score + "," + player + " ";
        }
        message = removeLastComma(message);

        return message.substring(0, message.length() - 1);
    }

    public static String drawTile(List<Block> tiles) {
        String message = SERVER_DRAWTILE + " ";

        for (Block tile : tiles) {
            message = message + tile.getCode() + " ";
        }

        return message.substring(0, message.length() - 1);
    }

}
