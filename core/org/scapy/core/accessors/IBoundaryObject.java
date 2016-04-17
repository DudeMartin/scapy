package org.scapy.core.accessors;

public interface IBoundaryObject {

    int getHash();

    int getOrientation();

    int getPlane();

    IRenderableNode getRenderableNode();

    int getX();

    int getY();
}