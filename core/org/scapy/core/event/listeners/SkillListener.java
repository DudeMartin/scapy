package org.scapy.core.event.listeners;

import org.scapy.core.event.impl.SkillEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving skill events. Skill events are received
 * when a change in at least one skill's experience or level is detected.
 *
 * @author Martin Tuskevicius
 */
public interface SkillListener extends EventListener {

    void onSkillChange(SkillEvent event);
}