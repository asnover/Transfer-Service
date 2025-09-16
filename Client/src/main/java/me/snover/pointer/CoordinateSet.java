package me.snover.pointer;

/**
 * This class is a representation of a location in the world by use of its coordinates after being deserialized.
 */
public class CoordinateSet {
    private final int X;
    private final int Y;
    private final int Z;
    public CoordinateSet(int X, int Y, final int Z) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public int getZ() {
        return Z;
    }
}