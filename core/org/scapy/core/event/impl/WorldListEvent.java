package org.scapy.core.event.impl;

import org.scapy.core.accessors.IWorld;
import org.scapy.core.event.GameEvent;
import org.scapy.core.event.Listenable;
import org.scapy.core.event.listeners.WorldListListener;

import java.util.EventListener;

@Listenable(listener = WorldListListener.class)
public class WorldListEvent extends GameEvent {

    public final IWorld[] worlds;

    public WorldListEvent(IWorld[] worlds) {
        this.worlds = worlds;
    }

    @Override
    public void dispatch(EventListener listener) {
        ((WorldListListener) listener).onListProcess(this);
    }
}