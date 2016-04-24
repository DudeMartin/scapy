package org.scapy.api.utils;

import org.scapy.utils.Filter;
import org.scapy.utils.Preconditions;

import java.util.Collection;
import java.util.Iterator;

/**
 * A collection of filter-related utilities.
 *
 * @author Martin Tuskevicius
 */
public final class FilterUtilities {

    /**
     * Prevents external initialization.
     */
    private FilterUtilities() {

    }

    /**
     * Joins the criteria of multiple filters together. The returned filter will
     * match an object only if <em>all</em> of the filters match the object.
     *
     * <p>
     * Any <code>null</code> elements in the <code>filters</code> array are
     * ignored. If the array is empty, or all of its elements are
     * <code>null</code>, then the returned filter will match all objects.
     *
     * @param filters the filters.
     * @param <T>     the filter input type.
     * @return a joined filter.
     */
    public static <T> Filter<T> join(final Filter<T>... filters) {
        return new Filter<T>() {

            @Override
            public boolean matches(T test) {
                for (Filter<T> filter : filters) {
                    if (filter != null && !filter.matches(test)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    /**
     * Filters out elements that do not meet the criteria of the filter. This
     * method assumes that the provided <code>Collection</code> is modifiable.
     * In particular, it assumes that its <code>Iterator</code> implementation
     * supports removal.
     *
     * @param collection the collection.
     * @param filter     the filter.
     * @param <T>        the collection element type.
     * @return the same collection with the filtered out elements removed.
     * @throws NullPointerException if <code>collection</code> or <code>filter</code>
     *                              is <code>null</code>.
     */
    public static <T> Collection<T> filter(Collection<T> collection, Filter<T> filter) {
        Preconditions.check(collection != null && filter != null, null, NullPointerException.class);
        for (Iterator<T> iterator = collection.iterator(); iterator.hasNext(); ) {
            if (!filter.matches(iterator.next())) {
                iterator.remove();
            }
        }
        return collection;
    }
}