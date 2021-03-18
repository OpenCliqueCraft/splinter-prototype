package net.gardna.splinter.util;

public class Vector2 extends Pair<Double, Double> {
    public Vector2(Double x, Double z) {
        super(x, z);
    }

    public Vector2(int x, int z) {
        super((double) x, (double) z);
    }

    public double getX() {
        return this.left;
    }

    public double getZ() {
        return this.right;
    }
}
