package org.scapy.api.wrapper;

import org.scapy.utils.Preconditions;

abstract class AbstractWrapper<T> implements Wrapper<T> {

    private final T accessor;

    AbstractWrapper(T accessor) {
        Preconditions.checkNull(accessor);
        this.accessor = accessor;
    }

    @Override
    public T accessor() {
        return accessor;
    }

    @Override
    public int hashCode() {
        return (accessor() == null) ? 0 : accessor().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Wrapper) {
            Wrapper<?> wrapper = (Wrapper<?>) obj;
            return wrapper.accessor() == accessor();
        }
        return false;
    }
}