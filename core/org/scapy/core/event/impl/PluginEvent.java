package org.scapy.core.event.impl;

import org.scapy.core.event.GameEvent;
import org.scapy.core.event.Listenable;
import org.scapy.core.event.listeners.PluginListener;
import org.scapy.core.plugin.Plugin;

import java.util.EventListener;

@Listenable(listener = PluginListener.class)
public class PluginEvent extends GameEvent {

    public enum PluginState {

        STARTED,
        STOPPED,
        PAUSED,
        RESUMED
    }

    public final PluginState state;

    public PluginEvent(Plugin plugin, PluginState state) {
        super(plugin);
        this.state = state;
    }

    @Override
    public void dispatch(EventListener listener) {
        PluginListener pluginListener = (PluginListener) listener;
        switch (state) {
            case STARTED:
                pluginListener.onStart(this);
                break;
            case STOPPED:
                pluginListener.onStop(this);
                break;
            case PAUSED:
                pluginListener.onPause(this);
                break;
            case RESUMED:
                pluginListener.onResume(this);
                break;
        }
    }

    @Override
    public final Plugin getSource() {
        return (Plugin) super.getSource();
    }
}