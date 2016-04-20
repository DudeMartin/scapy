package org.scapy.core.mod;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.scapy.core.GameCanvas;
import org.scapy.core.Gamepack;
import org.scapy.core.accessors.IClient;
import org.scapy.core.accessors.IInteractableObjectDefinition;
import org.scapy.core.accessors.IItemDefinition;
import org.scapy.core.accessors.INpcDefinition;

import java.awt.*;
import java.util.Map;
import java.util.Scanner;

public final class Injector {

    private static final String ACCESSOR_BASE;

    static {
        String internalName = Type.getInternalName(IClient.class);
        ACCESSOR_BASE = internalName.substring(0, internalName.lastIndexOf('/') + 1);
    }

    private Injector() {

    }

    public static void inject(Gamepack gamepack) throws Exception {
        Map<String, ClassNode> classes = gamepack.classes;
        Scanner hookData = Hooks.getData(gamepack.getRevision());
        processHookData(hookData, classes);
        for (ClassNode clazz : classes.values()) {
            if (clazz.superName.equals(Type.getInternalName(Canvas.class))) {
                Transformations.changeSuperclass(clazz, Type.getInternalName(GameCanvas.class));
                return;
            }
        }
    }

    private static void processHookData(Scanner scanner, Map<String, ClassNode> classes) {
        String currentAccessor = null;
        ClassNode currentClass = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.isEmpty()) {
                String[] parts = line.substring(1).split(" ");
                switch (line.charAt(0)) {
                    case '@':
                        if (parts.length < 2) {
                            throw new HookDataException("Malformed accessor line.");
                        }
                        currentAccessor = parts[0];
                        currentClass = classes.get(parts[1]);
                        if (currentClass == null) {
                            throw new HookDataException("Unknown class specified.");
                        }
                        Transformations.implementInterface(currentClass, ACCESSOR_BASE + currentAccessor);
                        break;
                    case '-':
                        processGetterLine(parts, currentClass, classes);
                        break;
                    case '=':
                        processCallerLine(parts, currentAccessor, currentClass, classes);
                        break;
                    default:
                        throw new HookDataException("Unknown line type.");
                }
            }
        }
    }

    private static void processGetterLine(String[] parts, ClassNode currentClass, Map<String, ClassNode> classes) {
        if (parts[1].equals("BROKEN")) {
            return;
        } else if (parts.length < 5) {
            throw new HookDataException("Malformed getter line.");
        }
        String getterName       = parts[0];
        String fieldOwner       = parts[1];
        String fieldName        = parts[2];
        String getterDescriptor = parts[3];
        String fieldDescriptor  = parts[4];
        int multiplier          = 1;
        if (parts.length >= 6) {
            multiplier = Integer.parseInt(parts[5]);
        }
        if (getterDescriptor.startsWith("I") && getterDescriptor.length() > 1) {
            StringBuilder builder = new StringBuilder();
            builder.append("()");
            builder.append(fieldDescriptor.substring(0, fieldDescriptor.indexOf('L') + 1));
            builder.append(ACCESSOR_BASE);
            builder.append(getterDescriptor);
            builder.append(';');
            getterDescriptor = builder.toString();
        } else {
            getterDescriptor = "()" + getterDescriptor;
        }
        boolean isStatic = Transformations.isStatic(classes.get(fieldOwner), fieldName);
        Transformations.addGetter(currentClass, getterName, fieldOwner, fieldName, getterDescriptor, fieldDescriptor, isStatic, multiplier);
    }

    private static void processCallerLine(String[] parts, String currentAccessor, ClassNode currentClass, Map<String, ClassNode> classes) {
        if (parts[1].equals("BROKEN")) {
            return;
        } else if (parts.length < 5) {
            throw new HookDataException("Malformed caller line.");
        }
        String callerName       = parts[0];
        String targetOwner      = parts[1];
        String targetName       = parts[2];
        String targetDescriptor = parts[3];
        String dummyValue       = parts[4];
        ClassNode targetClass   = classes.get(targetOwner);
        switch (callerName) {
            case "sortWorlds":
                Transformations.addSortWorldsCallback(targetClass, targetName, targetDescriptor);
                break;
            case "spawnGroundItem":
                Transformations.addSpawnGroundItemCallback(targetClass, targetName, targetDescriptor);
                break;
            case "despawnGroundItem":
                Transformations.addDespawnGroundItemCallback(targetClass, targetName, targetDescriptor);
                break;
            case "renderModel":
                Transformations.addRenderModelCallback(targetClass, targetName, targetDescriptor);
                break;
            case "createChatMessage":
                Transformations.addChatMessageCallback(targetClass, targetName, targetDescriptor);
                break;
            case "processGameLogic":
                Transformations.addGameLoopCallback(targetClass, targetName, targetDescriptor);
                break;
            case "displayChatMessage":
                Transformations.addDisplayChatMessage(currentClass, targetOwner, targetName, targetDescriptor, dummyValue);
                break;
            case "getInteractableObjectDefinition":
                Transformations.addDefinitionGetter(currentClass, IInteractableObjectDefinition.class, callerName, targetOwner, targetName, targetDescriptor, dummyValue);
                break;
            case "getItemDefinition":
                Transformations.addDefinitionGetter(currentClass, IItemDefinition.class, callerName, targetOwner, targetName, targetDescriptor, dummyValue);
                break;
            case "getNpcDefinition":
                Transformations.addDefinitionGetter(currentClass, INpcDefinition.class, callerName, targetOwner, targetName, targetDescriptor, dummyValue);
                break;
            case "worldToScreen":
                Transformations.addWorldToScreen(currentClass, targetOwner, targetName, targetDescriptor, dummyValue);
                break;
            case "getTileHeight":
                Transformations.addGetTileHeight(currentClass, targetOwner, targetName, targetDescriptor, dummyValue);
                break;
            case "transform":
                switch (currentAccessor) {
                    case "IInteractableObjectDefinition":
                        Transformations.addObjectDefinitionTransformer(currentClass, targetClass, targetOwner, targetName, targetDescriptor, dummyValue);
                        break;
                    case "IItemDefinition":
                        Transformations.addItemDefinitionTransformer(currentClass, targetClass, targetOwner, targetName, targetDescriptor, dummyValue);
                        break;
                    case "INpcDefinition":
                        Transformations.addNpcDefinitionTransformer(currentClass, targetClass, targetOwner, targetName, targetDescriptor, dummyValue);
                        break;
                }
                break;
        }
    }
}