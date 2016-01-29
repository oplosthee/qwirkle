package qwirkle.client;

import qwirkle.game.Board;

import java.io.PrintStream;
import java.util.Observable;
import java.util.Observer;

public class ClientView implements Observer {

    private PrintStream printStream;
    private Board board;

    public ClientView() {
        this.printStream = new PrintStream(System.out);
    }

    public ClientView(PrintStream stream) {
        this.printStream = stream;
    }

    public void print(String message) {
        printStream.println(message);
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void close() {
        printStream.close();
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
