package org.scapy.core.accessors;

public interface IInteractableObject {

    int getHash();

    int getHeight();

    int getOrientation();

    int getPlane();

    int getRelativeX();

    int getRelativeY();

    IRenderableNode getRenderableNode();

    int getX();

    int getY();
}