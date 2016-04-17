package org.scapy.core.accessors;

public interface IRegion {

    IInteractableObject[] getObjects();

    ITile[][][] getTiles();
}