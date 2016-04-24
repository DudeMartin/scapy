package org.scapy.core.event.impl;

import org.scapy.core.accessors.IRenderableNode;
import org.scapy.core.event.GameEvent;
import org.scapy.core.event.Listenable;
import org.scapy.core.event.listeners.ModelRenderListener;

import java.util.EventListener;

@Listenable(listener = ModelRenderListener.class)
public class ModelRenderEvent extends GameEvent {

    public final IRenderableNode model;

    public ModelRenderEvent(IRenderableNode model) {
        this.model = model;
    }

    @Override
    public void dispatch(EventListener listener) {
        ((ModelRenderListener) listener).onModelRender(this);
    }
}