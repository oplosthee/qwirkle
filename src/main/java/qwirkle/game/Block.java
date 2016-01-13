package qwirkle.game;

public class Block {

    public enum Shape {
        CIRCLE, CROSS, DIAMOND, SQUARE, STAR, CLOVER
    }

    public enum Color {
        RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE
    }

    private Shape shape;
    private Color color;

    public Block(Shape shape, Color color) {
        this.shape = shape;
        this.color = color;
    }

    @Override
    public String toString() {
        return String.format("Shape: %s - Color: %s", shape, color);
    }

}
