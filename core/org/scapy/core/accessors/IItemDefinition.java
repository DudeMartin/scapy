package org.scapy.core.accessors;

public interface IItemDefinition extends ICacheableNode {

    IItemDefinition transform(int stackAmount);

    String[] getGroundActions();

    String[] getInterfaceActions();

    String getName();
}