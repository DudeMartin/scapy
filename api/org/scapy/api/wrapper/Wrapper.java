package org.scapy.api.wrapper;

/**
 * Represents a class that is a wrapper for an accessor interface.
 *
 * @param <T> the accessor type.
 * @author Martin Tuskevicius
 */
public interface Wrapper<T> {

    /**
     * Returns this wrapper's internal accessor instance.
     *
     * @return the accessor.
     */
    T accessor();
}