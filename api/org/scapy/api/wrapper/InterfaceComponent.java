package org.scapy.api.wrapper;

import org.scapy.api.Game;
import org.scapy.api.Interfaces;
import org.scapy.core.accessors.IInterfaceComponent;
import org.scapy.core.accessors.IInterfaceComponentNode;

import java.awt.Rectangle;

public class InterfaceComponent extends AbstractWrapper<IInterfaceComponent> {

    public InterfaceComponent(IInterfaceComponent accessor) {
        super(accessor);
    }

    public int getParentId() {
        int parentId = accessor().getParentId();
        if (parentId == -1) {
            int interfaceId = accessor().getId() >> 16;
            HashTable table = new HashTable(Game.clientAccessor().getInterfaceComponentNodes());
            for (IInterfaceComponentNode node = (IInterfaceComponentNode) table.getFirst(); node != null; node = (IInterfaceComponentNode) table.getNext()) {
                if (node.getId() == interfaceId) {
                    return (int) node.getUid();
                }
            }
        }
        return parentId;
    }

    public int getId() {
        return accessor().getId();
    }

    public int getScrollX() {
        return accessor().getScrollX();
    }

    public int getScrollY() {
        return accessor().getScrollY();
    }

    public int getX() {
        return accessor().getX();
    }

    public int getY() {
        return accessor().getY();
    }

    public int getWidth() {
        return accessor().getWidth();
    }

    public int getHeight() {
        return accessor().getHeight();
    }

    public int getTextureId() {
        return accessor().getTextureId();
    }

    public String getText() {
        String text = accessor().getText();
        return (text == null) ? "" : text;
    }

    public boolean isHidden() {
        int parentId = getParentId();
        if (parentId > 0) {
            InterfaceComponent parent = getParent(parentId);
            if (parent != null && parent.isHidden()) {
                return true;
            }
        }
        return accessor().isHidden();
    }

    public int getBoundsIndex() {
        return accessor().getBoundsIndex();
    }

    public int getIndex() {
        return accessor().getIndex();
    }

    public int getAbsoluteX() {
        int x = getX();
        int parentId = getParentId();
        if (parentId > 0) {
            InterfaceComponent parent = getParent(parentId);
            if (parent != null) {
                x += parent.getAbsoluteX();
                x -= parent.getScrollX();
            }
        } else {
            int boundIndex = getBoundsIndex();
            if (boundIndex > 0) {
                x = Game.clientAccessor().getInterfaceComponentXPositions()[boundIndex];
            }
        }
        return x;
    }

    public int getAbsoluteY() {
        int y = getX();
        int parentId = getParentId();
        if (parentId > 0) {
            InterfaceComponent parent = getParent(parentId);
            if (parent != null) {
                y += parent.getAbsoluteY();
                y -= parent.getScrollY();
            }
        } else {
            int boundIndex = getBoundsIndex();
            if (boundIndex > 0) {
                y = Game.clientAccessor().getInterfaceComponentYPositions()[boundIndex];
            }
        }
        return y;
    }

    public Rectangle getBounds() {
        return new Rectangle(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight());
    }

    public InterfaceComponent[] getChildren() {
        IInterfaceComponent[] accessors = accessor().getComponents();
        if (accessors != null) {
            InterfaceComponent[] children = new InterfaceComponent[accessors.length];
            for (int i = 0; i < accessors.length; i++) {
                if (accessors[i] != null) {
                    children[i] = new InterfaceComponent(accessors[i]);
                }
            }
            return children;
        }
        return null;
    }

    public InterfaceComponent getChild(int index) {
        IInterfaceComponent[] accessors = accessor().getComponents();
        if (accessors != null) {
            IInterfaceComponent accessor = accessors[index];
            return (accessor == null) ? null : new InterfaceComponent(accessor);
        }
        return null;
    }

    public int[] getItemIds() {
        return accessor().getItemIds();
    }

    public int[] getItemStackSizes() {
        return accessor().getItemStackSizes();
    }

    private static InterfaceComponent getParent(int parentId) {
        return Interfaces.getComponent(parentId >> 16, parentId & 0xFF);
    }
}