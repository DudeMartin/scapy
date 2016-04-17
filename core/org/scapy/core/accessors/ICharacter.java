package org.scapy.core.accessors;

public interface ICharacter extends IRenderableNode {

    int getInteractingIndex();

    int getOrientation();

    String getOverheadText();

    int[] getQueueX();

    int[] getQueueY();

    int getX();

    int getY();

    int getCurrentHealth();

    int getMaxHealth();
}