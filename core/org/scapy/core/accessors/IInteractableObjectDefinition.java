package org.scapy.core.accessors;

public interface IInteractableObjectDefinition extends ICacheableNode {

    IInteractableObjectDefinition transform();

    String[] getActions();

    String getName();
}