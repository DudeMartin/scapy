package org.scapy.core.event.listeners;

import org.scapy.core.event.impl.PluginEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving plugin events. Plugin events indicate
 * that the state of a plugin has changed.
 *
 * @author Martin Tuskevicius
 */
public interface PluginListener extends EventListener {

    void onStart(PluginEvent e);

    void onStop(PluginEvent e);

    void onPause(PluginEvent e);

    void onResume(PluginEvent e);
}