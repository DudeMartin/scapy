package org.scapy.core.event.impl;

import org.scapy.core.accessors.IChatMessage;
import org.scapy.core.event.GameEvent;
import org.scapy.core.event.Listenable;
import org.scapy.core.event.listeners.ChatMessageListener;

import java.util.EventListener;

@Listenable(listener = ChatMessageListener.class)
public class ChatMessageEvent extends GameEvent {

    public final IChatMessage message;

    public ChatMessageEvent(IChatMessage message) {
        this.message = message;
    }

    @Override
    public void dispatch(EventListener listener) {
        ((ChatMessageListener) listener).onChatMessage(this);
    }
}