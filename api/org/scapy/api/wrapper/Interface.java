package org.scapy.api.wrapper;

import org.scapy.core.accessors.IInterfaceComponent;

public class Interface extends AbstractWrapper<IInterfaceComponent[]> {

    public final int index;

    public Interface(IInterfaceComponent[] accessors, int index) {
        super(accessors);
        this.index = index;
    }

    public InterfaceComponent[] getComponents() {
        InterfaceComponent[] components = new InterfaceComponent[accessor().length];
        for (int i = 0; i < accessor().length; i++) {
            IInterfaceComponent accessor = accessor()[i];
            if (accessor != null) {
                components[i] = new InterfaceComponent(accessor);
            }
        }
        return components;
    }

    public InterfaceComponent getComponent(int index) {
        IInterfaceComponent accessor = accessor()[index];
        return (accessor == null) ? null : new InterfaceComponent(accessor);
    }

    public InterfaceComponent getChild(int componentIndex, int childIndex) {
        IInterfaceComponent accessor = accessor()[index];
        if (accessor != null) {
            accessor = accessor.getComponents()[childIndex];
            return (accessor == null) ? null : new InterfaceComponent(accessor);
        }
        return null;
    }
}