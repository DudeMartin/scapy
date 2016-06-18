package org.scapy.api.utils.filter;

import org.scapy.utils.Filter;
import org.scapy.utils.Preconditions;

/**
 * A <code>Filter</code> implementation that matches objects based on a string
 * sequence.
 *
 * @param <T> the filter input type.
 * @author Martin Tuskevicius
 */
public abstract class StringFilter<T> implements Filter<T> {

    /**
     * The sequence to match.
     */
    public final String sequence;

    /**
     * If the sequence should be matched exactly. A value of <code>true</code>
     * indicates that inputs should be checked using the <code>equals</code>
     * method; a value of <code>false</code> indicates that inputs should be
     * checked using the <code>contains</code> method.
     */
    public final boolean exact;

    /**
     * Creates a new string filter.
     *
     * @param sequence the sequence to match.
     * @param exact    if the sequence should be matched exactly.
     * @throws NullPointerException if <code>sequence</code> is <code>null</code>.
     */
    public StringFilter(String sequence, boolean exact) {
        Preconditions.checkNull(sequence);
        this.sequence = sequence;
        this.exact = exact;
    }

    /**
     * Creates a new string filter that matches inputs exactly.
     *
     * @param sequence the sequence to match.
     * @throws NullPointerException if <code>sequence</code> is <code>null</code>.
     */
    public StringFilter(String sequence) {
        this(sequence, true);
    }

    /**
     * Check if the provided string matches the sequence.
     *
     * @param input the input string.
     * @return <code>true</code> if the string matches, <code>false</code>
     *         otherwise.
     */
    protected final boolean matchesSequence(String input) {
        return exact ? sequence.equals(input) : sequence.contains(input);
    }
}