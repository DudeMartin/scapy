package org.scapy.api.wrapper;

import org.scapy.core.accessors.INpc;

public class Npc extends Character {

    public Npc(INpc accessor) {
        super(accessor);
    }

    public NpcDefinition getDefinition() {
        return new NpcDefinition(accessor().getDefinition());
    }

    @Override
    public INpc accessor() {
        return (INpc) super.accessor();
    }
}