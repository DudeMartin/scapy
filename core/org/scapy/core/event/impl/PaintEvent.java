package org.scapy.core.event.impl;

import org.scapy.Application;
import org.scapy.core.GameCanvas;
import org.scapy.core.event.GameEvent;
import org.scapy.core.event.Listenable;
import org.scapy.core.event.listeners.PaintListener;

import java.awt.*;
import java.util.EventListener;

@Listenable(listener = PaintListener.class)
public class PaintEvent extends GameEvent {

    public final Graphics graphics;

    public PaintEvent(Graphics graphics) {
        super(Application.getGame().getCanvas());
        this.graphics = graphics;
    }

    @Override
    public void dispatch(EventListener listener) {
        ((PaintListener) listener).onPaint(this);
    }

    @Override
    public final GameCanvas getSource() {
        return (GameCanvas) super.getSource();
    }
}