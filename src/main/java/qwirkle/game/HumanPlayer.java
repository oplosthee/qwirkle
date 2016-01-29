package qwirkle.game;

import qwirkle.client.ClientView;
import qwirkle.shared.net.IProtocol;
import qwirkle.util.ProtocolFormatter;

import java.awt.*;
import java.util.*;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, ClientView view) {
        super(name, view);
    }

    @Override
    public String determineMove() {
        Scanner input = new Scanner(System.in);

        String moveType;

        // Player botPlayer = new RobotPlayer("", 10);
        // botPlayer.setGame(getGame()); --> is null.
        // botPlayer.addBlock(getHand());
        // System.out.println("[HINT] The move the bot would have made in this situation is: " + botPlayer.determineMove());

        while (true) {
            view.print("Enter what kind of move you want to do (PUT/TRADE/QUIT).");

            String currentHand = "";
            for (Block block : getHand()) {
                currentHand += "[" + block.toString() + "(" + block.getCode() + ")]";
            }
            view.print("Your hand is: " + currentHand);

            moveType = input.nextLine();

            if (moveType.equals("QUIT")) {
                return IProtocol.CLIENT_QUIT;
            }
            if (moveType.equals("PUT")) {
                return getPut();
            }
            if (moveType.equals("TRADE")) {
                return getTrade();
            }
        }
    }

    public String getPut() {
        Scanner input = new Scanner(System.in);
        String put;
        Map<Point, Block> move = new HashMap<>();

        while (true) {
            view.print(
                    "Enter your move in format: 'BlockId@PositionX,PositionY'. " +
                    "Enter nothing when done placing a move."
            );

            String currentHand = "";
            for (Block block : getHand()) {
                currentHand += "[" + block.toString() + "(" + block.getCode() + ")]";
            }
            view.print("Your hand is: " + currentHand);

            String currentMove = "";
            for (Map.Entry<Point, Block> entry : move.entrySet()) {
                currentMove += "[" + entry.getValue().toString() +
                        " (" + entry.getKey().x + "," + entry.getKey().y + ")]";
            }
            view.print("Current move: "+ currentMove);

            put = input.nextLine();

            if (put.equals("")) {
                break;
            }

            try {
                String[] args = put.split("@");
                int x = Integer.parseInt(args[1].split(",")[0]);
                int y = Integer.parseInt(args[1].split(",")[1]);

                move.put(new Point(x, y), new Block(Integer.parseInt(args[0])));
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                view.print("Invalid move.");
            }

        }

        move.values().stream().filter(block -> getHand().contains(block)).forEach(block -> {
            getHand().remove(block);
            lastMove.add(block);
        });

        return ProtocolFormatter.clientMovePut(move);
    }

    public String getTrade() {
        Scanner input = new Scanner(System.in);
        String trade;
        java.util.List<Block> tradeBlocks = new ArrayList<>();


        while (true) {
            view.print("Enter what blocks you want to place in format 'BlockId'." +
                    " Enter nothing when done placing a move.");

            String currentHand = "";
            for (Block block : getHand()) {
                currentHand += "[" + block.toString() + "(" + block.getCode() + ")]";
            }
            view.print("Your hand is: " + currentHand);

            view.print("Current move: "+ tradeBlocks);

            trade = input.nextLine();

            if (trade.equals("")) {
                break;
            }

            try {
                tradeBlocks.add(new Block(Integer.parseInt(trade)));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                view.print("Invalid move.");
            }

        }

        tradeBlocks.stream().filter(block -> getHand().contains(block)).forEach(block -> {
            getHand().remove(block);
            lastMove.add(block);
        });

        return ProtocolFormatter.moveTrade(tradeBlocks);
    }

}
