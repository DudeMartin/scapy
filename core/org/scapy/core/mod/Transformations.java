package org.scapy.core.mod;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.scapy.core.accessors.IInteractableObjectDefinition;
import org.scapy.core.accessors.IItemDefinition;
import org.scapy.core.accessors.INpcDefinition;

import java.lang.reflect.Modifier;
import java.util.List;

class Transformations implements Opcodes {

    static void implementInterface(ClassNode clazz, String interfaceName) {
        clazz.interfaces.add(interfaceName);
    }

    static void addGetter(ClassNode clazz,
                          String getterName,
                          String fieldOwner,
                          String fieldName,
                          String getterDescriptor,
                          String fieldDescriptor,
                          boolean isStatic,
                          int multiplier) {
        MethodNode getter = new MethodNode(ACC_PUBLIC, getterName, getterDescriptor, null, null);
        if (!isStatic) {
            getter.visitVarInsn(ALOAD, 0);
        }
        getter.visitFieldInsn(isStatic ? GETSTATIC : GETFIELD, fieldOwner, fieldName, fieldDescriptor);
        if (multiplier != 1) {
            getter.visitLdcInsn(multiplier);
            getter.visitInsn(IMUL);
        }
        getter.visitInsn(Type.getType(fieldDescriptor).getOpcode(IRETURN));
        clazz.methods.add(getter);
    }

    static void addSortWorldsCallback(ClassNode clazz, String targetName, String targetDescriptor) {
        for (MethodNode method : (List<MethodNode>) clazz.methods) {
            if (method.name.equals(targetName) && method.desc.equals(targetDescriptor)) {
                InsnList callbackInstructions = new InsnList();
                callbackInstructions.add(new VarInsnNode(ALOAD, 0));
                callbackInstructions.add(Callbacks.generateInstruction("onSortWorlds"));
                method.instructions.insert(callbackInstructions);
            }
        }
    }

    static void addSpawnGroundItemCallback(ClassNode clazz, String targetName, String targetDescriptor) {
        for (MethodNode method : (List<MethodNode>) clazz.methods) {
            if (method.name.equals(targetName) && method.desc.equals(targetDescriptor)) {
                InsnList callbackInstructions = new InsnList();
                callbackInstructions.add(new VarInsnNode(ILOAD, 2));
                callbackInstructions.add(new VarInsnNode(ILOAD, 3));
                callbackInstructions.add(new VarInsnNode(ILOAD, 1));
                callbackInstructions.add(Callbacks.generateInstruction("onGroundItemSpawn"));
                method.instructions.insert(callbackInstructions);
            }
        }
    }

    static void addDespawnGroundItemCallback(ClassNode clazz, String targetName, String targetDescriptor) {
        for (MethodNode method : (List<MethodNode>) clazz.methods) {
            if (method.name.equals(targetName) && method.desc.equals(targetDescriptor)) {
                InsnList callbackInstructions = new InsnList();
                callbackInstructions.add(new VarInsnNode(ILOAD, 2));
                callbackInstructions.add(new VarInsnNode(ILOAD, 3));
                callbackInstructions.add(new VarInsnNode(ILOAD, 1));
                callbackInstructions.add(Callbacks.generateInstruction("onGroundItemDespawn"));
                method.instructions.insert(callbackInstructions);
            }
        }
    }

    static void addRenderModelCallback(ClassNode clazz, String targetName, String targetDescriptor) {
        for (MethodNode method : (List<MethodNode>) clazz.methods) {
            if (method.name.equals(targetName) && method.desc.equals(targetDescriptor)) {
                InsnList callbackInstructions = new InsnList();
                LabelNode labelNode = new LabelNode();
                callbackInstructions.add(new VarInsnNode(ALOAD, 0));
                callbackInstructions.add(Callbacks.generateInstruction("onRenderModel"));
                callbackInstructions.add(new InsnNode(ICONST_0));
                callbackInstructions.add(new JumpInsnNode(IF_ICMPNE, labelNode));
                callbackInstructions.add(new InsnNode(RETURN));
                callbackInstructions.add(labelNode);
                method.instructions.insert(callbackInstructions);
            }
        }
    }

    static void addChatMessageCallback(ClassNode clazz, String targetName, String targetDescriptor) {
        for (MethodNode method : (List<MethodNode>) clazz.methods) {
            if (method.name.equals(targetName) && method.desc.equals(targetDescriptor)) {
                InsnList callbackInstructions = new InsnList();
                callbackInstructions.add(new InsnNode(DUP));
                callbackInstructions.add(Callbacks.generateInstruction("onChatMessage"));
                for (AbstractInsnNode instruction : method.instructions.toArray()) {
                    if (instruction.getOpcode() == ARETURN) {
                        method.instructions.insertBefore(instruction, callbackInstructions);
                    }
                }
            }
        }
    }

    static void addGameLoopCallback(ClassNode clazz, String targetName, String targetDescriptor) {
        for (MethodNode method : (List<MethodNode>) clazz.methods) {
            if (method.name.equals(targetName) && method.desc.equals(targetDescriptor)) {
                method.instructions.insert(Callbacks.generateInstruction("onGameLoop"));
            }
        }
    }

    static void addDisplayChatMessage(ClassNode clazz,
                                      String targetOwner,
                                      String targetName,
                                      String targetDescriptor,
                                      String dummyValue) {
        MethodNode caller = new MethodNode(ACC_PUBLIC, "displayChatMessage", generateDescriptor(null, int.class, String.class, String.class), null, null);
        caller.visitVarInsn(ILOAD, 1);
        caller.visitVarInsn(ALOAD, 2);
        caller.visitVarInsn(ALOAD, 3);
        caller.visitLdcInsn(getDummy(targetDescriptor, dummyValue));
        caller.visitMethodInsn(INVOKESTATIC, targetOwner, targetName, targetDescriptor, false);
        caller.visitInsn(RETURN);
        clazz.methods.add(caller);
    }

    static void addDefinitionGetter(ClassNode clazz,
                                    Class<?> definitionClass,
                                    String callerName,
                                    String targetOwner,
                                    String targetName,
                                    String targetDescriptor,
                                    String dummyValue) {
        MethodNode caller = new MethodNode(ACC_PUBLIC, callerName, generateDescriptor(definitionClass, int.class), null, null);
        caller.visitVarInsn(ILOAD, 1);
        caller.visitLdcInsn(getDummy(targetDescriptor, dummyValue));
        caller.visitMethodInsn(INVOKESTATIC, targetOwner, targetName, targetDescriptor, false);
        caller.visitInsn(ARETURN);
        clazz.methods.add(caller);
    }

    static void addWorldToScreen(ClassNode clazz,
                                 String targetOwner,
                                 String targetName,
                                 String targetDescriptor,
                                 String dummyValue) {
        MethodNode caller = new MethodNode(ACC_PUBLIC, "worldToScreen", generateDescriptor(null, int.class, int.class, int.class), null, null);
        caller.visitVarInsn(ILOAD, 1);
        caller.visitVarInsn(ILOAD, 2);
        caller.visitVarInsn(ILOAD, 3);
        caller.visitLdcInsn(getDummy(targetDescriptor, dummyValue));
        caller.visitMethodInsn(INVOKESTATIC, targetOwner, targetName, targetDescriptor, false);
        caller.visitInsn(RETURN);
        clazz.methods.add(caller);
    }

    static void addGetTileHeight(ClassNode clazz,
                                 String targetOwner,
                                 String targetName,
                                 String targetDescriptor,
                                 String dummyValue) {
        MethodNode caller = new MethodNode(ACC_PUBLIC, "getTileHeight", generateDescriptor(int.class, int.class, int.class, int.class), null, null);
        caller.visitVarInsn(ILOAD, 1);
        caller.visitVarInsn(ILOAD, 2);
        caller.visitVarInsn(ILOAD, 3);
        caller.visitLdcInsn(getDummy(targetDescriptor, dummyValue));
        caller.visitMethodInsn(INVOKESTATIC, targetOwner, targetName, targetDescriptor, false);
        caller.visitInsn(IRETURN);
        clazz.methods.add(caller);
    }

    static void addObjectDefinitionTransformer(ClassNode clazz,
                                               ClassNode targetClass,
                                               String targetOwner,
                                               String targetName,
                                               String targetDescriptor,
                                               String dummyValue) {
        MethodNode caller = new MethodNode(ACC_PUBLIC, "transform", generateDescriptor(IInteractableObjectDefinition.class), null, null);
        caller.visitVarInsn(ALOAD, 0);
        caller.visitLdcInsn(getDummy(targetDescriptor, dummyValue));
        caller.visitMethodInsn(INVOKEVIRTUAL, targetOwner, targetName, targetDescriptor, false);
        caller.visitInsn(ARETURN);
        clazz.methods.add(caller);
        addDefinitionLocks(targetClass, targetName, targetDescriptor);
    }

    static void addNpcDefinitionTransformer(ClassNode clazz,
                                            ClassNode targetClass,
                                            String targetOwner,
                                            String targetName,
                                            String targetDescriptor,
                                            String dummyValue) {
        MethodNode caller = new MethodNode(ACC_PUBLIC, "transform", generateDescriptor(INpcDefinition.class), null, null);
        caller.visitVarInsn(ALOAD, 0);
        caller.visitLdcInsn(getDummy(targetDescriptor, dummyValue));
        caller.visitMethodInsn(INVOKEVIRTUAL, targetOwner, targetName, targetDescriptor, false);
        caller.visitInsn(ARETURN);
        clazz.methods.add(caller);
        addDefinitionLocks(targetClass, targetName, targetDescriptor);
    }

    static void addItemDefinitionTransformer(ClassNode clazz,
                                               ClassNode targetClass,
                                               String targetOwner,
                                               String targetName,
                                               String targetDescriptor,
                                               String dummyValue) {
        MethodNode caller = new MethodNode(ACC_PUBLIC, "transform", generateDescriptor(IItemDefinition.class, int.class), null, null);
        caller.visitVarInsn(ALOAD, 0);
        caller.visitVarInsn(ILOAD, 1);
        caller.visitLdcInsn(getDummy(targetDescriptor, dummyValue));
        caller.visitMethodInsn(INVOKEVIRTUAL, targetOwner, targetName, targetDescriptor, false);
        caller.visitInsn(ARETURN);
        clazz.methods.add(caller);
        addDefinitionLocks(targetClass, targetName, targetDescriptor);
    }

    static void changeSuperclass(ClassNode clazz, String newSuperclass) {
        for (MethodNode method : (List<MethodNode>) clazz.methods) {
            for (AbstractInsnNode instruction : method.instructions.toArray()) {
                if (instruction.getOpcode() == INVOKESPECIAL) {
                    MethodInsnNode methodInstruction = (MethodInsnNode) instruction;
                    if (methodInstruction.owner.equals(clazz.superName)) {
                        methodInstruction.owner = newSuperclass;
                        break;
                    }
                }
            }
        }
        clazz.superName = newSuperclass;
    }

    static boolean isStatic(ClassNode clazz, String fieldName) {
        for (FieldNode field : (List<FieldNode>) clazz.fields) {
            if (field.name.equals(fieldName) && Modifier.isStatic(field.access)) {
                return true;
            }
        }
        return false;
    }

    private static void addDefinitionLocks(ClassNode clazz, String targetName, String targetDescriptor) {
        for (MethodNode method : (List<MethodNode>) clazz.methods) {
            if (method.name.equals(targetName) && method.desc.equals(targetDescriptor)) {
                InsnList instructions = method.instructions;
                InsnList lockInstructions = new InsnList();
                lockInstructions.add(new VarInsnNode(ALOAD, 0));
                lockInstructions.add(new InsnNode(MONITORENTER));
                instructions.insert(lockInstructions);
                for (AbstractInsnNode instruction : instructions.toArray()) {
                    if (instruction.getOpcode() == ARETURN) {
                        lockInstructions.clear();
                        lockInstructions.add(new VarInsnNode(ALOAD, 0));
                        lockInstructions.add(new InsnNode(MONITOREXIT));
                        instructions.insertBefore(instruction, lockInstructions);
                    }
                }
                lockInstructions.clear();
                lockInstructions.add(new VarInsnNode(ALOAD, 0));
                lockInstructions.add(new InsnNode(MONITOREXIT));
                instructions.insert(instructions.getLast(), lockInstructions);
            }
        }
    }

    private static String generateDescriptor(Class<?> returnType, Class<?>... parameterTypes) {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        for (Class<?> parameterType : parameterTypes) {
            builder.append(Type.getDescriptor(parameterType));
        }
        builder.append(')');
        builder.append(returnType == null ? 'V' : Type.getDescriptor(returnType));
        return builder.toString();
    }

    private static Object getDummy(String methodDescriptor, String dummyValue) {
        char type = methodDescriptor.charAt(methodDescriptor.indexOf(")") - 1);
        switch (type) {
            case 'I':
                return Integer.parseInt(dummyValue);
            case 'S':
                return Short.parseShort(dummyValue);
            case 'B':
                return Byte.parseByte(dummyValue);
            default:
                return dummyValue;
        }
    }
}