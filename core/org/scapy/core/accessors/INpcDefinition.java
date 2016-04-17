package org.scapy.core.accessors;

public interface INpcDefinition extends ICacheableNode {

    INpcDefinition transform();

    String[] getActions();

    String getName();

    int getId();
}