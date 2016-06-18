package org.scapy.api.utils.filter;

import org.scapy.utils.Filter;
import org.scapy.utils.Preconditions;

/**
 * A <code>Filter</code> implementation that inverts the matching criteria of a
 * provided filter.
 *
 * <p>
 * Consider the code sample below:
 *
 * <pre>
 * Filter&lt;String&gt; emptyFilter = new Filter&lt;String&gt;() {
 *
 *      public boolean matches(String test) {
 *          return test.isEmpty();
 *      }
 * };
 * Filter&lt;String&gt; notEmptyFilter = new Filter&lt;String&gt;() {
 *
 *      public boolean matches(String test) {
 *          return !test.isEmpty();
 *      }
 * };
 * </pre>
 *
 * Using this class, the latter filter could alternatively be implemented as:
 *
 * <pre>
 * notEmptyFilter = new InverseFilter&lt;String&gt;(emptyFilter);
 * </pre>
 *
 * @param <T> the filter input type.
 * @author Martin Tuskevicius
 */
public final class InverseFilter<T> implements Filter<T> {

    /**
     * The filter to invert.
     */
    public final Filter<T> filter;

    /**
     * Creates a new inverse filter.
     *
     * @param filter the filter.
     * @throws NullPointerException if <code>filter</code> is <code>null</code>.
     */
    public InverseFilter(Filter<T> filter) {
        Preconditions.checkNull(filter);
        this.filter = filter;
    }

    @Override
    public boolean matches(T test) {
        return !filter.matches(test);
    }
}