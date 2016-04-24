package org.scapy.core.event.listeners;

import org.scapy.core.event.impl.GameLoopEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving game loop events. Game loop events are
 * received when the game engine executes a game loop. An important cautionary
 * note about this listener is that game loops happen <em>very</em> frequently.
 * As a result, it is critically important that the code handling events passed
 * to this listener is non-blocking and generally returns quickly. If it does
 * not, the performance of the game will noticeably suffer as the internal game
 * loop has to wait until <code>GameLoopEvent</code> objects are handled.
 *
 * @author Martin Tuskevicius
 */
public interface GameLoopListener extends EventListener {

    void onGameLoop(GameLoopEvent event);
}