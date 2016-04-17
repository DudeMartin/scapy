package org.scapy.core.utils;

import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.Map;

public class DefinableClassLoader extends ClassLoader {

    private final Map<String, ClassNode> classes;

    public DefinableClassLoader(Map<String, ClassNode> classes, boolean clone) {
        this.classes = clone ? new HashMap<>(classes) : classes;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        ClassNode clazz = classes.remove(name);
        if (clazz != null) {
            byte[] classBytes = VerifyClassWriter.toBytes(clazz);
            return defineClass(name, classBytes, 0, classBytes.length);
        }
        return super.findClass(name);
    }
}