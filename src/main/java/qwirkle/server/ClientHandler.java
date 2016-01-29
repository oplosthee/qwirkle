package qwirkle.server;

import qwirkle.game.Block;
import qwirkle.game.SocketPlayer;
import qwirkle.game.exception.InvalidMoveException;
import qwirkle.server.exception.*;
import qwirkle.shared.net.IProtocol;
import qwirkle.util.ProtocolFormatter;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.List;

public class ClientHandler implements Runnable {

    private Socket client;
    private BufferedReader reader;
    private BufferedWriter writer;
    private ClientPool pool;

    private ServerGame game;

    private String name;
    private boolean connected;

    public ClientHandler(Socket client, ClientPool pool) throws IOException {
        this.client = client;
        this.pool = pool;
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

    }

    @Override
    public void run() {
        connected = true;

        while (connected) {
            try {
                String raw = reader.readLine();
                if (raw != null) {
                    System.out.println("Received: " + raw);
                    parse(raw.split(" "));
                }
            } catch (SocketException e) {
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                disconnect();
            }
        }
    }

    public void endGame(boolean won, Map<Integer, String> scores) {
        game = null;
        sendRaw(ProtocolFormatter.endGame(won, scores));
    }

    public void startGame(ServerGame serverGame, List<SocketPlayer> players) {
        game = serverGame;
        sendRaw(ProtocolFormatter.startGame(players));
    }

    public void setGame(ServerGame game) {
        this.game = game;
    }

    public void sendMovePut(Map<Point, Block> move) {
        sendRaw(ProtocolFormatter.serverMovePut(move));
    }

    public void sendMoveTrade(int amount) {
        sendRaw(IProtocol.SERVER_MOVE_TRADE + " " + amount);
    }

    public void sendTurn(String player) {
        sendRaw(IProtocol.SERVER_TURN + " " + player);
    }

    public void sendPass(String player) {
        sendRaw(IProtocol.SERVER_PASS + " " + player);
    }

    public void sendDrawTile(List<Block> tiles) {
        sendRaw(ProtocolFormatter.drawTile(tiles));
    }

    public void sendError(String error) {
        sendRaw(IProtocol.SERVER_ERROR + " " + error);
    }

    public void sendRaw(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (SocketException e) {
            System.out.println("[Server] Tried to send message to disconnected client.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parse(String[] message) {
        switch (message[0]) {
            case IProtocol.CLIENT_IDENTIFY:
                if (message.length < 1) {
                    break;
                }

                String userName = message[1];

                try {
                    pool.addClient(name, this);
                    name = userName;
                    sendRaw(IProtocol.SERVER_IDENTIFY);
                } catch (InvalidNameException e) {
                    sendError(IProtocol.Error.NAME_INVALID.ordinal() + " Invalid name");
                } catch (UsedNameException e) {
                    sendError(IProtocol.Error.NAME_USED.ordinal() + " Name is already in use");
                }

                break;
            case IProtocol.CLIENT_QUIT:
                disconnect();
                break;
            case IProtocol.CLIENT_MOVE_PUT:
                if (game == null) {
                    break;
                }
                if (game.getTurnClient().equals(this)) {
                    doMovePut(Arrays.copyOfRange(message, 1, message.length));
                } else {
                    sendError(IProtocol.Error.ILLEGAL_STATE.ordinal() + " It is not your turn");
                }
                break;
            case IProtocol.CLIENT_MOVE_TRADE:
                if (game == null) {
                    break;
                }
                if (game.getTurnClient().equals(this)) {
                    doMoveTrade(Arrays.copyOfRange(message, 1, message.length));
                } else {
                    sendError(IProtocol.Error.ILLEGAL_STATE.ordinal() + " It is not your turn");
                }
                break;
            case IProtocol.CLIENT_QUEUE:
                if (message.length < 1) {
                    break;
                } else if (game != null) {
                    sendError(IProtocol.Error.ILLEGAL_STATE.ordinal() +
                            " You cannot queue while you are in a game");
                    break;
                }

                String[] queues = message[1].split(",");

                try {
                    for (String queue : queues) {
                        try {
                            int queueSize = Integer.parseInt(queue);
                            pool.addClientToQueue(this, queueSize);
                        } catch (NumberFormatException e) {
                            System.out.println("Player tried to join invalid queue.");
                        }
                    }
                    sendRaw(IProtocol.SERVER_QUEUE);
                } catch (NumberFormatException e) {
                    sendRaw(String.valueOf(IProtocol.Error.QUEUE_INVALID));
                }
                break;
            default:
                sendRaw(String.valueOf(IProtocol.Error.INVALID_COMMAND.ordinal() +
                        " The server does not recognise this command"));
                break;
        }

    }

    public void doMovePut(String[] params) {
        Map<Point, Block> moves = new HashMap<>();

        for (String move : params) {
            String[] moveArg = move.split("@");
            int blockId = Integer.parseInt(moveArg[0]);
            int moveX = Integer.parseInt(moveArg[1].split(",")[0]);
            int moveY = Integer.parseInt(moveArg[1].split(",")[1]);

            moves.put(new Point(moveX, moveY), new Block(blockId));
        }

        try {
            game.doMovePut(moves);
            System.out.println("[Server] (ClientHandler) - Current game situation:");
            System.out.println(game.getBoard().toString());
        } catch (InvalidMoveException e) {
            sendError(IProtocol.Error.MOVE_INVALID.ordinal() + " Invalid move");
            game.sendPlayerTurn();
        } catch (TilesUnownedException e) {
            sendError(IProtocol.Error.MOVE_TILES_UNOWNED.ordinal() +
                    " Player tried to place unowned tile");
            game.sendPlayerTurn();
        } catch (NullPointerException e) {
            System.out.println("[Server] ClientHandler - Game ended during turn.");
        }
    }

    public void doMoveTrade(String[] params) {
        List<Block> tradeBlocks = new ArrayList<>();

        for (String block : params) {
            tradeBlocks.add(new Block(Integer.parseInt(block)));
        }

        try {
            game.doMoveTrade(tradeBlocks);
        } catch (TradeFirstTurnException e) {
            sendError(IProtocol.Error.TRADE_FIRST_TURN.ordinal() +
                    " You cannot trade on the first turn");
            game.sendPlayerTurn();
        } catch (TilesUnownedException e) {
            sendError(IProtocol.Error.MOVE_TILES_UNOWNED.ordinal() +
                    " Player tried to place unowned tile");
            game.sendPlayerTurn();
        } catch (EmptyBagException e) {
            sendError(IProtocol.Error.DECK_EMPTY.ordinal() +
                    " The bag does not contain this many blocks");
            game.sendPlayerTurn();
        }
    }

    public String getName() {
        return name;
    }

    public void disconnect() {
        try {
            pool.removeFromAllQueues(this);
            if (game != null) {
                game.endGame(false); // End the game the client was in.
            }
            reader.close();
            writer.close();
            client.close();
            System.out.println("[Server] Debug (ClientHandler) - Client has disconnected.");
            pool.removeClient(name);
            connected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
