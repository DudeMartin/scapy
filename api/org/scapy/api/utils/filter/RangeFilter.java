package org.scapy.api.utils.filter;

import org.scapy.utils.Filter;
import org.scapy.utils.Preconditions;

/**
 * A <code>Filter</code> implementation that matches objects based on a range of
 * values.
 *
 * @param <T> the filter input type.
 * @author Martin Tuskevicius
 */
public abstract class RangeFilter<T> implements Filter<T> {

    /**
     * The lower matching bound, inclusive.
     */
    public final int lower;

    /**
     * The upper matching bound, exclusive.
     */
    public final int upper;

    /**
     * Creates a new range filter.
     *
     * @param lower the lower matching bound (inclusive).
     * @param upper the upper matching bound (exclusive).
     * @throws IllegalArgumentException if <code>upper</code> is less than
     *                                  <code>lower</code>.
     */
    public RangeFilter(int lower, int upper) {
        Preconditions.check(upper >= lower, "The upper bound cannot be lower than the lower bound.", IllegalArgumentException.class);
        this.lower = lower;
        this.upper = upper;
    }

    /**
     * Creates a new range filter that matches only a specific value.
     *
     * @param value the value to match.
     */
    public RangeFilter(int value) {
        this(value, value);
    }

    /**
     * Checks if the provided value is within the bounds.
     *
     * @param value the value to check.
     * @return <code>true</code> if the value is within the bounds,
     *         <code>false</code> otherwise.
     */
    protected final boolean withinBounds(int value) {
        return value == lower || (value >= lower && value < upper);
    }
}