package main.java.qwirkle.game;

import java.util.HashSet;

public abstract class Player extends Bag {

    private String name;
    private int score;
    private HashSet<Block> blocks;

    //Constructor van het geheel.
    public Player(String name) {
        setName(name);
        takeRandomBlock(); //weet niet of dat nodig is hierbij.
        takeRandomBlock(); //misschien moet het niet in player, maar in het begin van het spel ofzo.
        takeRandomBlock();
        takeRandomBlock();
        takeRandomBlock();
        takeRandomBlock();
        score = 0;
    }

    //1 block neerleggen.
    public void addBlock2(Block block) { //Tom IntelliJ zeurt er over dat het 2 methods zijn met de zelfde naam, fix het.
        //ToDo implement this method
    }

    //Meerdere blokken tegelijkertijd zetten.
    public void addBlock(HashSet<Block> blockSet) {
        //ToDo implement this method
    }

    //Verwijder een blok die je hebt neergelegd.
    public void removeBlock(Block block) {
        //ToDo implement this method
    }

    //Legt een block in de bag en neemt een andere random blok er voor terug uit de bag.
    public void tradeBlock(Block block) {
        addBlock(block);
        takeRandomBlock();
    }

    //Geef de speler een naam.
    public void setName(String name) {
        //ToDo add exceptions
        this.name = name;
    }

    //Vraag de naam van de speler op.
    public String getName() {
        return this.name;
    }

    //Kun je de score mee verhogen.
    public void addScore(int score) {
        this.score = this.score + score;
    }
}