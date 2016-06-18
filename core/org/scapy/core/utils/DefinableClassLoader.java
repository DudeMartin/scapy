package org.scapy.core.utils;

import org.objectweb.asm.tree.ClassNode;
import org.scapy.Application;

import javax.swing.JOptionPane;
import java.util.Map;

public class DefinableClassLoader extends ClassLoader {

    private final Map<String, ClassNode> classes;

    public DefinableClassLoader(Map<String, ClassNode> classes) {
        this.classes = classes;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        ClassNode clazz = classes.remove(name);
        if (clazz != null) {
            byte[] classBytes = VerifyClassWriter.toBytes(clazz);
            try {
                return defineClass(name, classBytes, 0, classBytes.length);
            } catch (ClassFormatError e) {
                Application.showMessage("The hook data specified invalid class modifications.", "Load Error", JOptionPane.ERROR_MESSAGE);
                Application.shutdown();
                throw e;
            }
        }
        return super.findClass(name);
    }
}