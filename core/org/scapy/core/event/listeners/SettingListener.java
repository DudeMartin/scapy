package org.scapy.core.event.listeners;

import org.scapy.core.event.impl.SettingEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving setting events. Setting events are
 * received when a change in at least one setting's value is detected.
 *
 * @author Martin Tuskevicius
 */
public interface SettingListener extends EventListener {

    void onSettingChange(SettingEvent event);
}