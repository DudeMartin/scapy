package org.scapy.api.wrapper;

import org.scapy.core.accessors.INpcDefinition;

public class NpcDefinition extends AbstractWrapper<INpcDefinition> {

    public NpcDefinition(INpcDefinition accessor) {
        super(accessor);
    }

    public String getName() {
        return accessor().getName();
    }

    public String[] getActions() {
        return accessor().getActions();
    }

    public int getId() {
        return accessor().getId();
    }
}