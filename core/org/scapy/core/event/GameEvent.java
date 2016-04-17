package org.scapy.core.event;

import org.scapy.Application;

import java.util.EventListener;
import java.util.EventObject;

public abstract class GameEvent extends EventObject {

    protected GameEvent(Object source) {
        super(source);
    }

    protected GameEvent() {
        this(Application.getGame());
    }

    public abstract void dispatch(EventListener listener);
}