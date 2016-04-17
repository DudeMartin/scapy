package org.scapy.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

class VirtualGamepack extends Gamepack {

    private byte[] gamepackBytes;

    VirtualGamepack(byte[] gamepackBytes) throws IOException {
        this.gamepackBytes = gamepackBytes;
        initializeClasses();
        this.gamepackBytes = null;
    }

    @Override
    protected void initializeClasses() throws IOException {
        try (JarInputStream in = new JarInputStream(new ByteArrayInputStream(gamepackBytes))) {
            JarEntry entry;
            while ((entry = in.getNextJarEntry()) != null) {
                if (entry.getName().endsWith(".class")) {
                    ClassReader reader = new ClassReader(in);
                    ClassNode node = new ClassNode();
                    reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    classes.put(node.name.replace('/', '.'), node);
                }
                in.closeEntry();
            }
        }
    }
}