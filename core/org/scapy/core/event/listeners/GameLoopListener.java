package org.scapy.core.event.listeners;

import org.scapy.core.event.impl.GameLoopEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving game loop events.
 *
 * @author Martin Tuskevicius
 */
public interface GameLoopListener extends EventListener {

    void onGameLoop(GameLoopEvent event);
}