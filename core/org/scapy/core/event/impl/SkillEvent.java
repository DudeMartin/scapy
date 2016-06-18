package org.scapy.core.event.impl;

import org.scapy.core.event.GameEvent;
import org.scapy.core.event.Listenable;
import org.scapy.core.event.PolledEvent;
import org.scapy.core.event.listeners.SkillListener;

import java.util.EventListener;

@PolledEvent
@Listenable(listener = SkillListener.class)
public class SkillEvent extends GameEvent {

    public final int[][] experienceChanges;
    public final int[][] levelChanges;

    public SkillEvent(int[][] experienceChanges, int[][] levelChanges) {
        this.experienceChanges = experienceChanges;
        this.levelChanges = levelChanges;
    }

    @Override
    public void dispatch(EventListener listener) {
        ((SkillListener) listener).onSkillChange(this);
    }
}