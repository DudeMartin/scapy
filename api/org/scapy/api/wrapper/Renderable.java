package org.scapy.api.wrapper;

import org.scapy.core.accessors.IRenderableNode;

/**
 * A wrapper for the <code>IRenderableNode</code> accessor interface. This
 * accessor represents an object that can be renderered by the game engine.
 *
 * @author Martin Tuskevicius
 */
public class Renderable extends AbstractWrapper<IRenderableNode> {

    /**
     * Creates a new wrapper for the <code>IRenderableNode</code> accessor.
     *
     * @param accessor the accessor instance.
     * @throws NullPointerException if <code>accessor</code> is <code>null</code>.
     */
    public Renderable(IRenderableNode accessor) {
        super(accessor);
    }

    /**
     * Returns the height.
     *
     * @return the height.
     */
    public int getHeight() {
        return accessor().getHeight();
    }
}