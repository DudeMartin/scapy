package org.scapy.utils;

/**
 * A filter checks if an object meets a certain criteria.
 *
 * <p>
 * The typical use for the <code>Filter</code> class will be to remove (or,
 * "filter out") objects from a collection that do not meet the criteria of the
 * filter. Consider the code below which filters out strings that do not start
 * with <code>"L"</code>.
 *
 * <pre>
 *     List&lt;String&gt; cats = Arrays.asList("Cheetah", "Lion", "Leopard", "Lynx", "Tiger");
 *     Filter&lt;String&gt; catFilter = new Filter&lt;String&gt;() {
 *
 *          public boolean matches(String test) {
 *              return test.startsWith("L");
 *          }
 *     };
 *     FilterUtilities.filter(cats, catFilter);
 * </pre>
 *
 * @param <T> the filter input type.
 * @author Martin Tuskevicius
 */
public interface Filter<T> {

    /**
     * Checks if the specified object matches the criteria of this filter.
     *
     * @param test the object to test.
     * @return <code>true</code> if the object matches the criteria of the
     *         filter, <code>false</code> otherwise.
     */
    boolean matches(T test);

    /**
     * The default <code>Filter</code> implementation that allows all objects to pass.
     *
     * @param <T> the filter input type.
     * @author Martin Tuskevicius
     */
    final class DefaultFilter<T> implements Filter<T> {

        @Override
        public boolean matches(T test) {
            return true;
        }
    }
}