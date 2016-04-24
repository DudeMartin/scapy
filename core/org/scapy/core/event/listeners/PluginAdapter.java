package org.scapy.core.event.listeners;

import org.scapy.core.event.impl.PluginEvent;

/**
 * An <code>abstract</code> implementation of the <code>PluginListener</code>
 * interface. This class implements all of the methods in the interface, though
 * the implementations are empty. Subclasses should override the methods
 * corresponding to the events they are interested in receiving.
 *
 * @author Martin Tuskevicius
 */
public abstract class PluginAdapter implements PluginListener {

    @Override
    public void onStart(PluginEvent event) {

    }

    @Override
    public void onStop(PluginEvent event) {

    }

    @Override
    public void onPause(PluginEvent event) {

    }

    @Override
    public void onResume(PluginEvent event) {

    }
}