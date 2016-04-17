package org.scapy.core.event.impl;

import org.scapy.core.event.GameEvent;
import org.scapy.core.event.Listenable;
import org.scapy.core.event.listeners.GroundItemListener;

import java.util.EventListener;

@Listenable(listener = GroundItemListener.class)
public class GroundItemEvent extends GameEvent {

    public final int x;
    public final int y;
    public final int plane;
    public final boolean spawn;

    public GroundItemEvent(int x, int y, int plane, boolean spawn) {
        this.x = x;
        this.y = y;
        this.plane = plane;
        this.spawn = spawn;
    }

    @Override
    public void dispatch(EventListener listener) {
        ((GroundItemListener) listener).onGroundItem(this);
    }
}