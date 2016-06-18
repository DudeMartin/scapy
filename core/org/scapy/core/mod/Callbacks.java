package org.scapy.core.mod;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodInsnNode;
import org.scapy.Application;
import org.scapy.core.accessors.IChatMessage;
import org.scapy.core.accessors.IClient;
import org.scapy.core.accessors.IRenderableNode;
import org.scapy.core.accessors.IWorld;
import org.scapy.core.event.EventDispatcher;
import org.scapy.core.event.impl.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public final class Callbacks {

    public static final AtomicInteger regionDrawListenerCount = new AtomicInteger();
    public static final AtomicInteger modelRenderListenerCount = new AtomicInteger();
    public static final AtomicInteger gameLoopListenerCount = new AtomicInteger();
    public static final AtomicInteger settingListenerCount = new AtomicInteger();
    public static final AtomicInteger skillListenerCount = new AtomicInteger();
    private static final EventDispatcher events = EventDispatcher.instance;
    private static int[] lastSettings;
    private static int[] lastExperiences;
    private static int[] lastLevels;

    private Callbacks() {

    }

    public static void onDrawRegion() {
        if (regionDrawListenerCount.get() > 0) {
            events.dispatch(new RegionDrawEvent());
        }
    }

    public static void onSortWorlds(IWorld[] worlds) {
        events.dispatch(new WorldListEvent(worlds.clone()));
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
        return RenderingFilters.shouldRender(model);
    }

    public static void onChatMessage(IChatMessage message) {
        events.dispatch(new ChatMessageEvent(message));
    }

    public static void onGameLoop() {
        if (gameLoopListenerCount.get() > 0) {
            events.dispatch(new GameLoopEvent());
        }
        if (settingListenerCount.get() > 0) {
            checkSettingChanges();
        }
        if (skillListenerCount.get() > 0) {
            checkSkillChanges();
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

    private static void checkSettingChanges() {
        int[] settings = Application.getGame().getClientAccessor().getGameSettings();
        if (settings != null) {
            if (lastSettings != null) {
                int length = Math.min(settings.length, lastSettings.length);
                int[][] changes = new int[length][3];
                int changeCount = 0;
                for (int i = 0; i < length; i++) {
                    int oldValue = settings[i];
                    int newValue = lastSettings[i];
                    if (oldValue != newValue) {
                        changes[changeCount][0] = i;
                        changes[changeCount][1] = oldValue;
                        changes[changeCount++][2] = newValue;
                    }
                }
                if (changeCount > 0) {
                    events.dispatch(new SettingEvent(Arrays.copyOf(changes, changeCount)));
                }
            }
            lastSettings = settings.clone();
        }
    }

    private static void checkSkillChanges() {
        IClient clientAccessor = Application.getGame().getClientAccessor();
        int[] experiences = clientAccessor.getStatExperiences();
        int[] levels = clientAccessor.getBaseStats();
        if (experiences != null && levels != null) {
            if (lastExperiences != null && lastLevels != null) {
                int length = Math.min(Math.min(experiences.length, lastExperiences.length), Math.min(levels.length, lastLevels.length));
                int[][] experienceChanges = new int[length][3];
                int[][] levelChanges = new int[length][3];
                int experienceChangeCount = 0, levelChangeCount = 0;
                for (int i = 0; i < length; i++) {
                    int oldExperience = lastExperiences[i];
                    int newExperience = experiences[i];
                    if (oldExperience != newExperience) {
                        experienceChanges[experienceChangeCount][0] = i;
                        experienceChanges[experienceChangeCount][1] = oldExperience;
                        experienceChanges[experienceChangeCount++][2] = newExperience;
                    }
                    int oldLevel = lastLevels[i];
                    int newLevel = levels[i];
                    if (oldLevel != newLevel) {
                        levelChanges[levelChangeCount][0] = i;
                        levelChanges[levelChangeCount][1] = oldLevel;
                        levelChanges[levelChangeCount++][2] = newLevel;
                    }
                }
                if (experienceChangeCount > 0 || levelChangeCount > 0) {
                    events.dispatch(new SkillEvent(Arrays.copyOf(experienceChanges, experienceChangeCount), Arrays.copyOf(levelChanges, levelChangeCount)));
                }
            }
            lastExperiences = experiences.clone();
            lastLevels = levels.clone();
        }
    }
}