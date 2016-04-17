package org.scapy.core.mod;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodInsnNode;
import org.scapy.core.accessors.IChatMessage;
import org.scapy.core.accessors.IRenderableNode;
import org.scapy.core.event.EventDispatcher;
import org.scapy.core.event.impl.ChatMessageEvent;
import org.scapy.core.event.impl.GameLoopEvent;
import org.scapy.core.event.impl.GroundItemEvent;

import java.lang.reflect.Method;

public final class Callbacks {

    private Callbacks() {

    }

    public static void onGroundItemSpawn(int x, int y, int plane) {
        EventDispatcher.instance.dispatch(new GroundItemEvent(x, y, plane, true));
    }

    public static void onGroundItemDespawn(int x, int y, int plane) {
        EventDispatcher.instance.dispatch(new GroundItemEvent(x, y, plane, false));
    }

    public static boolean onRenderModel(IRenderableNode renderable) {
        return true;
    }

    public static void onChatMessage(IChatMessage message) {
        EventDispatcher.instance.dispatch(new ChatMessageEvent(message));
    }

    public static void onGameLoop() {
        EventDispatcher.instance.dispatch(new GameLoopEvent());
    }

    static MethodInsnNode generateInstruction(String callbackMethodName) {
        for (Method callbackMethod : Callbacks.class.getMethods()) {
            if (callbackMethod.getName().equals(callbackMethodName)) {
                return new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Callbacks.class), callbackMethodName, Type.getMethodDescriptor(callbackMethod), false);
            }
        }
        throw new RuntimeException("Bad callback method name.");
    }
}