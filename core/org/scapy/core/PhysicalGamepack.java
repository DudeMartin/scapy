package org.scapy.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

class PhysicalGamepack extends Gamepack<JarFile> {

    PhysicalGamepack(JarFile archive) throws IOException {
        super(archive);
    }

    @Override
    protected void initializeClasses(JarFile archive) throws IOException {
        try {
            Enumeration<JarEntry> entries = archive.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    ClassReader reader = new ClassReader(archive.getInputStream(entry));
                    ClassNode node = new ClassNode();
                    reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    classes.put(node.name.replace('/', '.'), node);
                }
            }
        } finally {
            archive.close();
        }
    }
}