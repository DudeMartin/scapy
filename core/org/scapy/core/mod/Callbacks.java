package org.scapy.core.mod;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodInsnNode;
import org.scapy.api.WorldList;
import org.scapy.core.accessors.IChatMessage;
import org.scapy.core.accessors.IRenderableNode;
import org.scapy.core.accessors.IWorld;
import org.scapy.core.event.EventDispatcher;
import org.scapy.core.event.impl.ChatMessageEvent;
import org.scapy.core.event.impl.GameLoopEvent;
import org.scapy.core.event.impl.GroundItemEvent;
import org.scapy.core.event.impl.ModelRenderEvent;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public final class Callbacks {

    public static final AtomicInteger modelRenderListenerCount = new AtomicInteger();
    public static final AtomicInteger gameLoopListenerCount = new AtomicInteger();
    private static final EventDispatcher events = EventDispatcher.instance;

    private Callbacks() {

    }

    public static void onSortWorlds(IWorld[] worlds) {
        WorldList.update(worlds);
    }

    public static void onGroundItemSpawn(int x, int y, int plane) {
        events.dispatch(new GroundItemEvent(x, y, plane, true));
    }

    public static void onGroundItemDespawn(int x, int y, int plane) {
        events.dispatch(new GroundItemEvent(x, y, plane, false));
    }

    public static boolean onRenderModel(IRenderableNode model) {
        if (modelRenderListenerCount.get() > 0) {
            events.dispatch(new ModelRenderEvent(model));
        }
        return RenderingFilters.matches(model);
    }

    public static void onChatMessage(IChatMessage message) {
        events.dispatch(new ChatMessageEvent(message));
    }

    public static void onGameLoop() {
        if (gameLoopListenerCount.get() > 0) {
            events.dispatch(new GameLoopEvent());
        }
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