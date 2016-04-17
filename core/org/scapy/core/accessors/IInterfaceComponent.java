package org.scapy.core.accessors;

public interface IInterfaceComponent extends INode {

    int getBoundsIndex();

    IInterfaceComponent[] getComponents();

    int getHeight();

    int getId();

    int getIndex();

    int[] getItemIds();

    int[] getItemStackSizes();

    int getParentId();

    int getScrollX();

    int getScrollY();

    String getText();

    int getTextureId();

    int getWidth();

    int getX();

    int getY();

    boolean isHidden();
}