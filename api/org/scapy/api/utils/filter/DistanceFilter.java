package org.scapy.api.utils.filter;

import org.scapy.api.Calculations;
import org.scapy.api.wrapper.Positionable;
import org.scapy.utils.Preconditions;

/**
 * A <code>Filter</code> implementation that matches <code>Positionable</code>
 * objects based on their distances from each other.
 *
 * @param <T> the filter input type.
 * @author Martin Tuskevicius
 */
public class DistanceFilter<T extends Positionable> extends RangeFilter<T> {

    /**
     * The positionable to calculate the distance to.
     */
    public final Positionable positionable;

    /**
     * Creates a new distance filter.
     *
     * @param positionable the positionable.
     * @param lower        the distance lower bound (inclusive).
     * @param upper        the distance upper bound (exclusive).
     * @throws NullPointerException     if <code>positionable</code> is
     *                                  <code>null</code>.
     * @throws IllegalArgumentException if <code>upper</code> is less than
     *                                  <code>lower</code>, or if either of them
     *                                  are negative.
     */
    public DistanceFilter(Positionable positionable, int lower, int upper) {
        super(lower, upper);
        Preconditions.checkNull(positionable);
        Preconditions.check(lower >= 0 && upper >= 0, "The bounds must be non-negative.", IllegalArgumentException.class);
        this.positionable = positionable;
    }

    /**
     * Creates a new distance filter.
     *
     * @param positionable the positionable.
     * @param value        the distance to match.
     * @throws NullPointerException     if <code>positionable</code> is
     *                                  <code>null</code>.
     * @throws IllegalArgumentException if <code>value</code> is negative.
     */
    public DistanceFilter(Positionable positionable, int value) {
        this(positionable, value, value);
    }

    @Override
    public boolean matches(T test) {
        return test != null && withinBounds(Calculations.tileDistance(test, positionable));
    }
}