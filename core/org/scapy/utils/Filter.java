package org.scapy.utils;

public interface Filter<T> {

    boolean matches(T test);

    final class DefaultFilter<T> implements Filter<T> {

        @Override
        public boolean matches(T test) {
            return true;
        }
    }
}