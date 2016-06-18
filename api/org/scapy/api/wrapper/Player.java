package org.scapy.api.wrapper;

import org.scapy.core.accessors.IPlayer;

public class Player extends Character {

    public Player(IPlayer accessor) {
        super(accessor);
    }

    public String getName() {
        return accessor().getName();
    }

    public int getCombatLevel() {
        return accessor().getCombatLevel();
    }

    @Override
    public IPlayer accessor() {
        return (IPlayer) super.accessor();
    }
}