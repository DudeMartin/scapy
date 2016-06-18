package org.scapy.api.wrapper;

import org.scapy.api.Game;

import java.util.Objects;

/**
 * An immutable representation of a tile in the game world.
 *
 * @author Martin Tuskevicius
 */
public final class Tile implements Positionable {

    /**
     * The world X-coordinate.
     */
    public final int x;

    /**
     * The world Y-coordinate.
     */
    public final int y;

    /**
     * The plane.
     */
    public final int plane;

    /**
     * Creates a new representation of a tile in the game world.
     *
     * @param x     the world X-coordinate.
     * @param y     the world Y-coordinate.
     * @param plane the plane.
     */
    public Tile(int x, int y, int plane) {
        this.x = x;
        this.y = y;
        this.plane = plane;
    }

    @Override
    public int getPreciseLocalX() {
        return (getLocalX() << 7) + 64;
    }

    @Override
    public int getPreciseLocalY() {
        return (getLocalY() << 7) + 64;
    }

    @Override
    public int getLocalX() {
        return getWorldX() - Game.clientAccessor().getBaseX();
    }

    @Override
    public int getLocalY() {
        return getWorldY() - Game.clientAccessor().getBaseY();
    }

    @Override
    public int getWorldX() {
        return x;
    }

    @Override
    public int getWorldY() {
        return y;
    }

    @Override
    public int getPlane() {
        return plane;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, plane);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Tile) {
            Tile other = (Tile) obj;
            return other.x == x && other.y == y && other.plane == plane;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Tile: [World X: " + x + " World Y: " + y + " Plane: " + plane + "].";
    }
}