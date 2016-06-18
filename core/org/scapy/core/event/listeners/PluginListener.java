package org.scapy.core.event.listeners;

import org.scapy.core.event.impl.PluginEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving plugin events. Plugin events are
 * received when a plugin state changes.
 *
 * @author Martin Tuskevicius
 */
public interface PluginListener extends EventListener {

    void onStart(PluginEvent event);

    void onStop(PluginEvent event);

    void onPause(PluginEvent event);

    void onResume(PluginEvent event);
}