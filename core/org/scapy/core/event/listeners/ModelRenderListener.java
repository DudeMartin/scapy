package org.scapy.core.event.listeners;

import org.scapy.core.event.impl.ModelRenderEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving model render events. Model render events
 * are received when the game engine begins to render a model in the game world.
 * Events received by this listener do not signify that a model has finished
 * rendering, or if it even will; they simply signify that an attempt to render
 * was initiated by the engine.
 *
 * @author Martin Tuskevicius
 */
public interface ModelRenderListener extends EventListener {

    void onModelRender(ModelRenderEvent event);
}