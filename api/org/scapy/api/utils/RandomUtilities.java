package org.scapy.api.utils;

import org.scapy.utils.Preconditions;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A collection of randomness-related utilities.
 *
 * @author Martin Tuskevicius
 */
public final class RandomUtilities {

    /**
     * A constant specifying the maximum number of standard deviations that the
     * bounds of a normally distributed number can be (exclusive).
     *
     * <p>
     * As the bounds move farther away from the mean, the probability of a
     * normally distributed number falling within the bounds decreases. For
     * example, the probability of a number being more than three standard
     * deviations away is less than 0.3% (see, "empirical rule"). Because
     * pseudorandom, normally distributed numbers are returned simply by
     * generating one and checking if it is within the bounds, the number of
     * iterations needed to return one quickly increases as the bounds move
     * farther away from the mean. This constant establishes a threshold after
     * which it would be too computationally expensive (and generally
     * impractical) to generate a single number.
     */
    public static final double Z_SCORE_THRESHOLD = 5;

    /**
     * Prevents external initialization.
     */
    private RandomUtilities() {

    }

    /**
     * Returns a random element from the collection.
     *
     * @param collection the collection.
     * @param <T>        the element type.
     * @return a random element, or <code>null</code> if the collection is
     *         empty.
     * @throws NullPointerException if <code>collection</code> is <code>null</code>.
     */
    public static <T> T randomElement(Collection<T> collection) {
        Preconditions.checkNull(collection);
        int size = collection.size();
        if (size > 0) {
            int randomIndex = rnd().nextInt(size);
            if (collection instanceof RandomAccess) {
                List<T> list = (List<T>) collection;
                return list.get(randomIndex);
            }
            int current = 0;
            for (T element : collection) {
                if (current++ == randomIndex) {
                    return element;
                }
            }
        }
        return null;
    }

    /**
     * Returns a random element from the array.
     *
     * @param array the array.
     * @param <T>   the element type.
     * @return a random element, or <code>null</code> if the array has length
     *         <code>0</code>.
     * @throws NullPointerException if <code>array</code> is <code>null</code>.
     */
    public static <T> T randomElement(T[] array) {
        return randomElement(Arrays.asList(array));
    }

    /**
     * Returns a pseudorandom integer whose value lies between the provided
     * bounds.
     *
     * @param minimum the lower bound, inclusive.
     * @param maximum the upper bound, exclusive.
     * @return the integer.
     * @throws IllegalArgumentException if <code>maximum</code> is less than
     *                                  <code>minimum</code>.
     */
    public static int random(int minimum, int maximum) {
        Preconditions.check(maximum >= minimum, "The maximum cannot be smaller than the minimum.", IllegalArgumentException.class);
        return (minimum == maximum ? minimum : rnd().nextInt(minimum, maximum));
    }

    /**
     * Returns a pseudorandom integer whose value lies between <code>0</code>
     * and <code>maximum</code>.
     *
     * @param maximum the upper bound, exclusive.
     * @return the integer.
     * @throws IllegalArgumentException if <code>maximum</code> is less than
     *                                  <code>0</code>.
     */
    public static int random(int maximum) {
        return random(0, maximum);
    }

    /**
     * Returns a pseudorandom <code>double</code> whose value lies between the
     * provided bounds.
     *
     * @param minimum the lower bound, inclusive.
     * @param maximum the upper bound, exclusive.
     * @return the <code>double</code>.
     * @throws IllegalArgumentException if <code>maximum</code> is less than
     *                                  <code>minimum</code>.
     */
    public static double randomDouble(double minimum, double maximum) {
        Preconditions.check(maximum >= minimum, "The maximum cannot be smaller than the minimum.", IllegalArgumentException.class);
        return minimum + rnd().nextDouble() * (maximum - minimum);
    }

    /**
     * Returns a pseudorandom <code>double</code> whose value lies between
     * <code>0</code> and <code>maximum</code>.
     *
     * @param maximum the upper bound, exclusive.
     * @return the <code>double</code>.
     * @throws IllegalArgumentException if <code>maximum</code> is less than
     *                                  <code>0</code>.
     */
    public static double randomDouble(double maximum) {
        return randomDouble(0, maximum);
    }

    /**
     * Returns a pseudorandom long integer whose value lies between the provided
     * bounds.
     *
     * @param minimum the lower bound, inclusive.
     * @param maximum the upper bound, exclusive.
     * @return the long integer.
     * @throws IllegalArgumentException if <code>maximum</code> is less than
     *                                  <code>minimum</code>.
     */
    public static long randomLong(long minimum, long maximum) {
        Preconditions.check(maximum >= minimum, "The maximum cannot be smaller than the minimum.", IllegalArgumentException.class);
        return (minimum == maximum ? minimum : rnd().nextLong(minimum, maximum));
    }

    /**
     * Returns a pseudorandom long integer whose value lies between
     * <code>0</code> and <code>maximum</code>.
     *
     * @param maximum the upper bound, exclusive.
     * @return the long integer.
     * @throws IllegalArgumentException if <code>maximum</code> is less than
     *                                  <code>0</code>.
     */
    public static long randomLong(long maximum) {
        return randomLong(0, maximum);
    }

    /**
     * Returns a pseudorandom, normally distributed <code>double</code> around
     * <code>mean</code>.
     *
     * @param minimum the lower bound, inclusive.
     * @param maximum the upper bound, exclusive.
     * @param mean    the normal distribution mean.
     * @param sd      the normal distribution standard deviation.
     * @return the <code>double</code>.
     * @throws IllegalArgumentException if <code>maximum</code> is less than
     *                                  <code>minimum</code>, if <code>sd</code>
     *                                  is not positive, or if the bounds are
     *                                  too many standard deviations away from
     *                                  the mean.
     * @see #Z_SCORE_THRESHOLD
     */
    public static double randomNormal(double minimum, double maximum, double mean, double sd) {
        Preconditions.check(maximum >= minimum, "The maximum cannot be smaller than the minimum.", IllegalArgumentException.class);
        Preconditions.check(sd > 0, "The standard deviation must be positive.", IllegalArgumentException.class);
        double minimumZScore = (minimum - mean) / sd;
        double maximumZScore = (maximum - mean) / sd;
        if (Math.signum(minimumZScore) == Math.signum(maximumZScore) && Math.min(Math.abs(minimumZScore), Math.abs(maximumZScore)) > Z_SCORE_THRESHOLD) {
            throw new IllegalArgumentException("The bounds are too many standard deviations away from the mean.");
        }
        double random;
        do {
            random = rnd().nextGaussian() * sd + mean;
        } while (random < minimum || random >= maximum);
        return random;
    }

    /**
     * Returns a pseudorandom, normally distributed <code>double</code> around
     * the middle of the bounds.
     *
     * @param minimum the lower bound, inclusive.
     * @param maximum the upper bound, exclusive.
     * @param sd      the normal distribution standard deviation.
     * @return the <code>double</code>.
     * @throws IllegalArgumentException if <code>maximum</code> is less than
     *                                  <code>minimum</code>, if <code>sd</code>
     *                                  is not positive, or if the bounds are
     *                                  too many standard deviations away from
     *                                  the mean.
     * @see #Z_SCORE_THRESHOLD
     */
    public static double randomNormal(double minimum, double maximum, double sd) {
        return randomNormal(minimum, maximum, minimum + (maximum - minimum) / 2, sd);
    }

    private static ThreadLocalRandom rnd() {
        return ThreadLocalRandom.current();
    }
}