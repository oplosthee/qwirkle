package main.java.qwirkle.game;

import java.util.HashSet;
import java.util.Set;

public class Player {

    private String name;
    private int score;
    private Set<Block> hand;

    //Constructor zet de naam op een nieuwe naam, geeft een nieuwe lege hand mee en zet de score op 0.
    public Player(String name) {
        setName(name);
        hand = new HashSet<>();
        score = 0;
    }

    //1 block neerleggen.
    public void addBlock2(Block block) { //Tom IntelliJ zeurt er over dat het 2 methods zijn met de zelfde naam, fix het.
        hand.remove(block); //is het remove of add? ik zou denken remove, want je haalt ze uit je hand op het board.
    }

    //Meerdere blokken tegelijkertijd zetten.
    public void addBlock(HashSet<Block> blockSet) {
        hand.remove(blockSet); //kan dit zo of moet dit met een loop? Ik denk zelf wel met een loop.
    }

    //Verwijder een blok die je hebt neergelegd.
    public void removeBlock(Block block) {
        hand.add(block); //hier dus weer dezelfde vraag, hierm oet add denk ik, want je pakt ze van het board.
    }

    //Geef de speler een naam.
    public void setName(String name) {
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