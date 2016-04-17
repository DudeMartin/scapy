package org.scapy.core.utils;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

public class VerifyClassWriter extends ClassWriter {

    public VerifyClassWriter(int flags) {
        super(flags);
    }

    public VerifyClassWriter() {
        this(COMPUTE_FRAMES);
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        return Type.getInternalName(Object.class);
    }

    public static byte[] toBytes(ClassNode clazz) {
        ClassWriter writer = new VerifyClassWriter();
        clazz.accept(writer);
        return writer.toByteArray();
    }
}