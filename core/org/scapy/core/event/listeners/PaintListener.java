package org.scapy.core.event.listeners;

import org.scapy.core.event.impl.PaintEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving paint events. Paint events are received
 * before the game client does its own drawing on the canvas.
 *
 * @author Martin Tuskevicius
 */
public interface PaintListener extends EventListener {

    void onPaint(PaintEvent event);
}