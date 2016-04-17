package org.scapy.core.event.listeners;

import org.scapy.core.event.impl.ChatMessageEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving chat message events. Chat message events
 * are received even if they are invisible to the user. For example, if the
 * user's public chat is turned off, chat message events from that channel will
 * be received despite the fact that the user cannot see the messages in the
 * game client.
 *
 * @author Martin Tuskevicius
 */
public interface ChatMessageListener extends EventListener {

    void onChatMessage(ChatMessageEvent event);
}