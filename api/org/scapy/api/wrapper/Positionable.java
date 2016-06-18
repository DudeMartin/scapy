package org.scapy.api.wrapper;

/**
 * Represents an object that can be positioned in the game world.
 *
 * @author Martin Tuskevicius
 */
public interface Positionable {

    /**
     * Returns the precise X-coordinate within the region. The granularity of
     * this coordinate is finer than one-coordinate-per-tile.
     *
     * @return the precise local X-coordinate.
     */
    int getPreciseLocalX();

    /**
     * Returns the precise Y-coordinate within the region. The granularity of
     * this coordinate is finer than one-coordinate-per-tile.
     *
     * @return the precise local Y-coordinate.
     */
    int getPreciseLocalY();

    /**
     * Returns the X-coordinate within the region.
     *
     * @return the local X-coordinate.
     */
    int getLocalX();

    /**
     * Returns the Y-coordinate within the region.
     *
     * @return the local Y-coordinate.
     */
    int getLocalY();

    /**
     * Returns the X-coordinate within the game world.
     *
     * @return the world X-coordinate.
     */
    int getWorldX();

    /**
     * Returns the Y-coordinate within the game world.
     *
     * @return the world Y-coordinate.
     */
    int getWorldY();

    /**
     * Returns the plane.
     *
     * @return the plane.
     */
    int getPlane();
}