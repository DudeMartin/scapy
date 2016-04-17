package org.scapy.core;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

public abstract class Gamepack {

    /**
     * A map of <code>ClassNode</code> objects mapped to their binary class
     * names.
     */
    public final Map<String, ClassNode> classes = new HashMap<>();
    private int cachedRevision = -1;

    /**
     * Restrict implementations to this package.
     */
    Gamepack() {

    }

    public static Gamepack create(Object source) throws IOException {
        if (source instanceof byte[]) {
            return new VirtualGamepack((byte[]) source);
        } else if (source instanceof JarFile) {
            return new PhysicalGamepack((JarFile) source);
        } else if (source instanceof File) {
            return new PhysicalGamepack(new JarFile((File) source));
        } else if (source instanceof String) {
            return new PhysicalGamepack(new JarFile((String) source));
        } else if (source instanceof Path) {
            Path archivePath = (Path) source;
            return new PhysicalGamepack(new JarFile(archivePath.toFile()));
        } else {
            throw new IllegalArgumentException("Unsupported gamepack source.");
        }
    }

    public final int getRevision() {
        if (cachedRevision == -1) {
            ClassNode clientClass = classes.get("client");
            if (clientClass != null) {
                for (MethodNode method : (List<MethodNode>) clientClass.methods) {
                    for (AbstractInsnNode instruction : method.instructions.toArray()) {
                        if (instruction.getNext() != null && instruction.getOpcode() == Opcodes.SIPUSH && instruction.getNext().getOpcode() == Opcodes.SIPUSH) {
                            IntInsnNode widthInstruction = (IntInsnNode) instruction;
                            IntInsnNode heightInstruction = (IntInsnNode) instruction.getNext();
                            if (widthInstruction.operand == 765 && heightInstruction.operand == 503 && heightInstruction.getNext() != null) {
                                IntInsnNode revisionInstruction = (IntInsnNode) heightInstruction.getNext();
                                cachedRevision = revisionInstruction.operand;
                                return revisionInstruction.operand;
                            }
                        }
                    }
                }
            }
        }
        return cachedRevision;
    }

    protected abstract void initializeClasses() throws IOException;
}