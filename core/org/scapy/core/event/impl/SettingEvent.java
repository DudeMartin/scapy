package org.scapy.core.event.impl;

import org.scapy.core.event.GameEvent;
import org.scapy.core.event.Listenable;
import org.scapy.core.event.PolledEvent;
import org.scapy.core.event.listeners.SettingListener;

import java.util.EventListener;

@PolledEvent
@Listenable(listener = SettingListener.class)
public class SettingEvent extends GameEvent {

    public int[][] changes;

    public SettingEvent(int[][] changes) {
        this.changes = changes;
    }

    @Override
    public void dispatch(EventListener listener) {
        ((SettingListener) listener).onSettingChange(this);
    }
}