package org.scapy.core.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.EventListener;

/**
 * Indicates the corresponding type of listener for an event.
 *
 * <p>
 * Annotated types of <code>GameEvent</code> are guaranteed to be supplied an
 * instance of the type of listener specified by this annotation when their
 * <code>dispatch</code> methods are invoked. If a subclass of
 * <code>GameEvent</code> is not annotated, then any type of
 * <code>EventListener</code> may be passed to the <code>dispatch</code> method,
 * delegating the responsibility of listener type checking to event
 * implementation rather than the event dispatcher.
 *
 * @author Martin Tuskevicius
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listenable {

    /**
     * Returns the corresponding type of listener for this event type.
     *
     * @return the listener type.
     */
    Class<? extends EventListener> listener();
}