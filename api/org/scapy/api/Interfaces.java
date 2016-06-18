package org.scapy.api;

import org.scapy.api.wrapper.Interface;
import org.scapy.api.wrapper.InterfaceComponent;
import org.scapy.core.accessors.IInterfaceComponent;

public final class Interfaces {

    private Interfaces() {

    }

    public static Interface[] getInterfaces() {
        IInterfaceComponent[][] components = Game.clientAccessor().getInterfaceComponents();
        if (components != null) {
            Interface[] interfaces = new Interface[components.length];
            for (int i = 0; i < components.length; i++) {
                if (components[i] != null) {
                    interfaces[i] = new Interface(components[i], i);
                }
            }
            return interfaces;
        }
        return null;
    }

    public static Interface getInterface(int index) {
        IInterfaceComponent[][] components = Game.clientAccessor().getInterfaceComponents();
        if (components != null) {
            IInterfaceComponent[] inter = components[index];
            return (inter == null) ? null : new Interface(inter, index);
        }
        return null;
    }

    public static InterfaceComponent getComponent(int interfaceIndex, int componentIndex) {
        IInterfaceComponent[][] components = Game.clientAccessor().getInterfaceComponents();
        if (components != null) {
            IInterfaceComponent[] inter = components[interfaceIndex];
            if (inter != null) {
                IInterfaceComponent component = inter[componentIndex];
                return (component == null) ? null : new InterfaceComponent(component);
            }
        }
        return null;
    }

    public static InterfaceComponent getComponentChild(int interfaceIndex, int componentIndex, int childIndex) {
        IInterfaceComponent[][] components = Game.clientAccessor().getInterfaceComponents();
        if (components != null) {
            IInterfaceComponent[] inter = Game.clientAccessor().getInterfaceComponents()[interfaceIndex];
            if (inter != null) {
                IInterfaceComponent component = inter[componentIndex];
                if (component != null) {
                    component = component.getComponents()[childIndex];
                    return (component == null) ? null : new InterfaceComponent(component);
                }
            }
        }
        return null;
    }
}