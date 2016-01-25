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

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public boolean isAllowedNeighbour(Block block) {
        return block == null || (block.getColor().equals(getColor()) ^ block.getShape().equals(getShape()));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Block && ((Block) obj).getColor() == getColor() && ((Block) obj).getShape() == getShape();
    }

    @Override
    public String toString() {
        return String.format("Shape: %s - Color: %s", shape, color);
    }

}
