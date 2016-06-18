package org.scapy.core.event.listeners;

import org.scapy.core.event.impl.WorldListEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving world list events. World list events are
 * received when the game client processes the world list.
 *
 * @author Martin Tuskevicius
 */
public interface WorldListListener extends EventListener {

    void onListProcess(WorldListEvent event);
}