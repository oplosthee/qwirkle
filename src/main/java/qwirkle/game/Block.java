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

    public Block(int blockCode) {
        this.shape = Shape.values()[blockCode % Shape.values().length];
        this.color = Color.values()[blockCode / Color.values().length];
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public int getCode() {
        return (this.getColor().ordinal() * 6) + this.getShape().ordinal();
    }

    public boolean isAllowedNeighbor(Block block) {
        return block == null || (block.getColor().equals(getColor()) ^ block.getShape().equals(getShape()));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Block && ((Block) obj).getColor() == getColor() && ((Block) obj).getShape() == getShape();
    }

    @Override
    public String toString() {
        String shapeCode = "" + shape.toString().charAt(0) + shape.toString().charAt(1);
        String colorCode = "" + color.toString().charAt(0) + color.toString().charAt(1);
        return String.format("%s%s", shapeCode, colorCode);
    }

}
