package org.scapy.core.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginManifest {

    /**
     * Returns the plugin name.
     *
     * @return the plugin name.
     */
    String name();

    /**
     * Returns the list of plugin authors.
     *
     * @return the list of plugin authors, or an empty list by default.
     */
    String[] authors() default {};

    /**
     * Returns the plugin description.
     *
     * @return the plugin description.
     */
    String description();

    /**
     * Returns the plugin version.
     *
     * @return the plugin version, or <code>1.0</code> by default.
     */
    double version() default 1.0;
}