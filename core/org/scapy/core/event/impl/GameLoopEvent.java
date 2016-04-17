package org.scapy.core.event.impl;

import org.scapy.core.event.GameEvent;
import org.scapy.core.event.Listenable;
import org.scapy.core.event.listeners.GameLoopListener;

import java.util.EventListener;

@Listenable(listener = GameLoopListener.class)
public class GameLoopEvent extends GameEvent {

    @Override
    public void dispatch(EventListener listener) {
        ((GameLoopListener) listener).onGameLoop(this);
    }
}
