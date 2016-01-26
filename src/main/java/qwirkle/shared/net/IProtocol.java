package qwirkle.shared.net;

/**
 * IProtocol is the interface for the Qwirkle Protocol implementation. This interface
 * contains definitions for all commands used in the protocol. An implementation has
 * to be made by the groups themselves.
 *
 * <h3>Tiles</h3>
 * <p>Tiles are represented by an integer. There are 6 colors and 6 shapes to be
 * identified, resulting in 36 different combinations. First, we define the colors
 * and shapes as integers from <code>0</code> to <code>5</code>. Using the formula
 * <strong><code>color * 6 + shape</code></strong> we can calculate the integer for
 * the Tile. It does not matter which shape or color is defined by which integer, as
 * long as it is done consistently.</p>
 *
 * <h3>Definitions</h3>
 * <dl>
 *     <dt>Board</dt>
 *     <dd>A 2-dimensional matrix where tiles can be laid upon. The Board has has coordinates x and y, where (0,0) is the origin.</dd>
 * </dl>
 * <dl>
 *     <dt>Tile</dt>
 *     <dd>A piece that can be played on the Board. Represented by a combination of a shape and a color.</dd>
 * </dl>
 * <dl>
 *     <dt>Player</dt>
 *     <dd>One who competes in a game. The player has a hand and a score.</dd>
 * </dl>
 * <dl>
 *     <dt>Hand</dt>
 *     <dd>Owned by a player, contains up to 6 Tiles.</dd>
 * </dl>
 * <dl>
 *     <dt>Deck</dt>
 *     <dd>The Deck contains all Tiles which have not been played yet and are not in a Player's Hand.</dd>
 * </dl>
 *
 * <h3>Changelog</h3>
 * <dl>
 *     <dt>0.5</dt>
 *     <dd>Added {@link qwirkle.shared.net.IProtocol#SERVER_QUEUE}, which the server responds with after a player queues.</dd>
 * </dl>
 * <dl>
 *     <dt>0.4</dt>
 *     <dd>Improved documentation</dd>
 *     <dd>Added regex for Name and Lists</dd>
 *     <dd>Added {@link qwirkle.shared.net.IProtocol.Error#ILLEGAL_STATE}, which is thrown when a client uses a command which is not allowed in that state.</dd>
 * </dl>
 * <dl>
 *     <dt>0.3</dt>
 *     <dd>Added {@link qwirkle.shared.net.IProtocol#SERVER_PASS}</dd>
 * </dl>
 * <dl>
 *     <dt>0.2</dt>
 *     <dd>Added {@link qwirkle.shared.net.IProtocol#VERSION} number</dd>
 *     <dd>Changed {@link qwirkle.shared.net.IProtocol#CLIENT_IDENTIFY} name regex</dd>
 *     <dd>Changed {@link qwirkle.shared.net.IProtocol#CLIENT_IDENTIFY} example</dd>
 *     <dd>Renamed COMMAND_NOT_FOUND to {@link qwirkle.shared.net.IProtocol.Error#INVALID_COMMAND}</dd>
 * </dl>
 *
 * @author Erik Gaal
 * @version 0.3
 * @since 0.1-w01
 */
public interface IProtocol {

    /**
     * <p>String representing the version of the protocol.</p>
     */
    String VERSION = "0.2";

    String NAME_REGEX = "^[A-Za-z0-9-_]{2,16}$";
    String LIST_REGEX = "^\\w+(,\\w+)*$";

    /**
     * <p>Enumeration of the features supported by the protocol.</p>
     */
    enum Feature {
        CHAT, CHALLENGE, LEADERBOARD, LOBBY
    }

    /**
     * <p>Enumeration of the error codes.</p>
     */
    enum Error {
        INVALID_COMMAND, INVALID_PARAMETER,
        NAME_INVALID, NAME_USED,
        QUEUE_INVALID,
        MOVE_TILES_UNOWNED, MOVE_INVALID,
        DECK_EMPTY, TRADE_FIRST_TURN,
        INVALID_CHANNEL,
        CHALLENGE_SELF, ILLEGAL_STATE, NOT_CHALLENGED
    }

    /**
     * <p>Sent by the client when connecting to a server to identify itself.</p>
     * <p>The player name must match regex <code>^[a-zA-Z0-9-_]{2,16}$</code> <code>{@link qwirkle.shared.net.IProtocol.Error#NAME_INVALID }</code></p>
     * <p>The player name must be unique. <code>{@link qwirkle.shared.net.IProtocol.Error#NAME_USED }</code></p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>name</code> - name of the player</dd>
     *     <dd><code>features</code> - list of features supported by the client <em>(see {@link qwirkle.shared.net.IProtocol.Feature})</em></dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>IDENTIFY</strong> PlayerA CHAT,LEADERBOARD</code></dd>
     * </dl>
     */
    String CLIENT_IDENTIFY = "IDENTIFY";

    /**
     * <p>Sent by the client when gracefully quitting a game.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>QUIT</strong></code></dd>
     * </dl>
     */
    String CLIENT_QUIT = "QUIT";

    /**
     * <p>Sent by the client to enter a queue for a n-player game.</p>
     * <p>The player can queue for 2, 3 or 4 player games. <code>{@link qwirkle.shared.net.IProtocol.Error#QUEUE_INVALID }</code></p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>numbers</code> - a list of numbers of players</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>QUEUE</strong> 2,4</code></dd>
     * </dl>
     */
    String CLIENT_QUEUE = "QUEUE";

    /**
     * <p>Sent by the client to put tiles on the board as a move.</p>
     * <p>The player must own the tiles.<code>{@link qwirkle.shared.net.IProtocol.Error#MOVE_TILES_UNOWNED }</code></p>
     * <p>The move must be valid. <code>{@link qwirkle.shared.net.IProtocol.Error#MOVE_INVALID }</code></p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>tile@x,y</code> - list of tilecode at coordinate</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>MOVE_PUT</strong> 0@0,0 6@0,1 12@0,2</code></dd>
     * </dl>
     */
    String CLIENT_MOVE_PUT = "MOVE_PUT";

    /**
     * <p>Sent by the client to trade tiles as a move.</p>
     * <p>The player must own the tiles. <code>{@link qwirkle.shared.net.IProtocol.Error#MOVE_TILES_UNOWNED }</code></p>
     * <p>The deck must contain at least as many tiles as traded. <code>{@link qwirkle.shared.net.IProtocol.Error#DECK_EMPTY }</code></p>
     * <p>The player cannot trade if the board is empty. <code>{@link qwirkle.shared.net.IProtocol.Error#TRADE_FIRST_TURN }</code></p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>tiles</code> - list of tiles</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>MOVE_TRADE</strong> 23 18 7</code></dd>
     * </dl>
     */
    String CLIENT_MOVE_TRADE = "MOVE_TRADE";

    /**
     * <p>Sent by the server to confirm a player connecting.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>features</code> - list of features</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>IDENTIFYOK</strong> CHAT,LOBBY</code></dd>
     * </dl>
     */
    String SERVER_IDENTIFY = "IDENTIFYOK";

    /**
     * <p>Sent by the server to confirm a player queueing.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>numbers</code> - list of number of players</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>QUEUEOK</strong> 2,4</code></dd>
     * </dl>
     */
    String SERVER_QUEUE = "QUEUEOK";

    /**
     * <p>Sent by the server to announce a game starting.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>players</code> - list of players in the game</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>GAMESTART</strong> Alice Bob Carol</code></dd>
     * </dl>
     */
    String SERVER_GAMESTART = "GAMESTART";

    /**
     * <p>Sent by the server to announce a game ending.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>result</code> - WIN or ERROR</dd>
     *     <dd><code>player</code> - list of player with their score</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>GAMEEND</strong> WIN 11,Alice 13,Bob 17,Carol</code></dd>
     * </dl>
     */
    String SERVER_GAMEEND = "GAMEEND";

    /**
     * <p>Sent by the server to announce the current turn.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>player</code> - the player</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>TURN</strong> Alice</code></dd>
     * </dl>
     */
    String SERVER_TURN = "TURN";

    /**
     * <p>Sent by the server to announce the turn which is automatically passed.</p>
     * <p>The server must send this if the player cannot do a valid move.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>player</code> - the player</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>PASS</strong> Bob</code></dd>
     * </dl>
     */
    String SERVER_PASS = "PASS";

    /**
     * <p>Sent by the server to draw a player a new tile.</p>
     * <p>The server must send this to one player.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>tiles</code> - list of tiles</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>DRAWTILE</strong> 23 7 19</code></dd>
     * </dl>
     */
    String SERVER_DRAWTILE = "DRAWTILE";

    /**
     * <p>Sent by the server to confirm putting tiles as a move.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>tile@x,y</code> - list of tile at coordinate</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>MOVEOK_PUT</strong> 0@0,0 6@0,1 12@0,2</code></dd>
     * </dl>
     */
    String SERVER_MOVE_PUT = "MOVEOK_PUT";

    /**
     * <p>Sent y the server to confirm trading tiles as a move.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>amount</code> - amount of tiles traded</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>MOVEOK_TRADE</strong> 3</code></dd>
     * </dl>
     */
    String SERVER_MOVE_TRADE = "MOVEOK_TRADE";

    /**
     * <p>Sent by the server to indicate an error.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>code</code> - the error</dd>
     *     <dd><code>[message]</code> - a human readable message</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>ERROR</strong> 1 Invalid move</code></dd>
     * </dl>
     */
    String SERVER_ERROR = "ERROR";

    /**
     * <p>Sent by the client when chatting.</p>
     * <p>The channel must be <code>global</code> or <code>@playername</code>. <code>{@link qwirkle.shared.net.IProtocol.Error#INVALID_CHANNEL }</code></p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>channel</code> - the channel</dd>
     *     <dd><code>message</code> - the message</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>CHAT</strong> global Hello world!</code></dd>
     *     <dd><code><strong>CHAT</strong> @Bob Hello Bob!</code></dd>
     * </dl>
     */
    String CLIENT_CHAT = "CHAT";

    /**
     * <p>Sent by the server to confirm a chat message.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>channel</code> - the channel</dd>
     *     <dd><code>sender</code> - the sender</dd>
     *     <dd><code>message</code> - the message</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>CHATOK</strong> global Alice Hello world!</code></dd>
     *     <dd><code><strong>CHATOK</strong> @Bob Alice Hello Bob!</code></dd>
     * </dl>
     */
    String SERVER_CHAT = "CHATOK";

    /* Challenge */
    /**
     * <p>Sent by the client to challenge another player.</p>
     * <p>The player cannot challenge itself. <code>{@link qwirkle.shared.net.IProtocol.Error#CHALLENGE_SELF }</code></p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>player</code> - the player</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>CHALLENGE</strong> Bob</code></dd>
     * </dl>
     */
    String CLIENT_CHALLENGE = "CHALLENGE";

    /**
     * <p>Sent by the client to accept a challenge.</p>
     * <p>The player must be challenged by the other player beforehand. <code>{@link qwirkle.shared.net.IProtocol.Error#NOT_CHALLENGED }</code></p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>player</code> - the player</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>CHALLENGE_ACCEPT</strong> Alice</code></dd>
     * </dl>
     */
    String CLIENT_CHALLENGE_ACCEPT = "CHALLENGE_ACCEPT";

    /**
     * <p>Sent by the client to decline a challenge.</p>
     * <p>The player must be challenged by the other player beforehand. <code>{@link qwirkle.shared.net.IProtocol.Error#NOT_CHALLENGED }</code></p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>player</code> - the player</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>CHALLENGE_DECLINE</strong> Alice</code></dd>
     * </dl>
     */
    String CLIENT_CHALLENGE_DECLINE = "CHALLENGE_DECLINE";

    /* Leaderboard */
    /**
     * <p>Sent by the client to request the leaderboard.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>LEADERBOARD</strong></code></dd>
     * </dl>
     */
    String CLIENT_LEADERBOARD = "LEADERBOARD";

    /**
     * <p>Sent by the server to transfer the leaderboard.</p>
     * <p>How the leaderboard is stored may be decided by the groups themselves. One way is to store the amount of wins and losses.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>player,wins,losses</code> - list of players with their scores</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>LEADERBOARDOK</strong> Alice,2,1 Bob,0,3 Carol,1,2</code></dd>
     * </dl>
     */
    String SERVER_LEADERBOARD = "LEADERBOARDOK";

    /**
     * <p>Sent by the client to request the lobby.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>LOBBY</strong></code></dd>
     * </dl>
     */
    String CLIENT_LOBBY = "LOBBY";

    /**
     * <p>Sent by the server to transfer the lobby.</p>
     *
     * <dl>
     *     <dt>Parameters:</dt>
     *     <dd><code>players</code> - list of players</dd>
     * </dl>
     * <dl>
     *     <dt>Example:</dt>
     *     <dd><code><strong>LOBBYOK</strong> Alice Bob Claire Dave</code></dd>
     * </dl>
     */
    String SERVER_LOBBY = "LOBBYOK";
}