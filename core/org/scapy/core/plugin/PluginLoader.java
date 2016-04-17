package org.scapy.core.plugin;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.scapy.core.Gamepack;
import org.scapy.core.utils.DefinableClassLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;

public final class PluginLoader {

	private PluginLoader() {

	}

	public static class PluginLoadException extends RuntimeException {

		private PluginLoadException(String message) {
			super(message);
		}
	}

	public static Plugin load(Gamepack gamepack) throws ReflectiveOperationException {
		ClassLoader loader = new DefinableClassLoader(gamepack.classes, false);
		for (ClassNode clazz : gamepack.classes.values()) {
			List<AnnotationNode> annotations = clazz.visibleAnnotations;
			if (annotations != null) {
				for (AnnotationNode annotation : annotations) {
					if (annotation.desc.equals(Type.getDescriptor(PluginManifest.class))) {
						Class<?> pluginClass = loader.loadClass(clazz.name.replace('/', '.'));
						if (Plugin.class.isAssignableFrom(pluginClass)) {
							if (!Modifier.isAbstract(pluginClass.getModifiers())) {
								for (Constructor constructor : pluginClass.getConstructors()) {
									if (constructor.getParameterTypes().length == 0) {
										return (Plugin) constructor.newInstance();
									}
								}
							}
						}
					}
				}
			}
		}
		throw new PluginLoadException("The plugin archive is missing a valid main class.");
	}
}