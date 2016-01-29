package qwirkle.client;

import java.io.PrintStream;

public class ClientView {

    private PrintStream printStream;

    public ClientView() {
        this.printStream = new PrintStream(System.out);
    }

    public ClientView(PrintStream stream) {
        this.printStream = stream;
    }

    public void print(String message) {
        printStream.println(message);
    }

    public void close() {
        printStream.close();
    }

}
