package org.scapy.core.event.impl;

import org.scapy.core.event.GameEvent;
import org.scapy.core.event.Listenable;
import org.scapy.core.event.listeners.RegionDrawListener;

import java.util.EventListener;

@Listenable(listener = RegionDrawListener.class)
public class RegionDrawEvent extends GameEvent {

    @Override
    public void dispatch(EventListener listener) {
        ((RegionDrawListener) listener).onRegionDraw(this);
    }
}