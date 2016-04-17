package org.scapy.core.accessors;

public interface ITile extends INode {

    IBoundaryObject getBoundaryObject();

    IGroundDecoration getGroundDecoration();

    IInteractableObject[] getObjects();

    IWallDecoration getWallDecoration();
}