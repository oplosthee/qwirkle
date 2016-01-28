package qwirkle.game;

public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public String determineMove() {
        System.out.println("This is where the player would make a move.");
        return "TODO!";
    }

}
