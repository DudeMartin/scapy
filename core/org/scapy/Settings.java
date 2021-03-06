package org.scapy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Properties;

/**
 * A utility class for persistent storage of application-wide settings. If the
 * application is running in virtual mode, then settings are not retained
 * between executions. The methods in this class are thread-safe.
 *
 * @author Martin Tuskevicius
 */
public final class Settings {

    /**
     * A collection of default setting names.
     */
    public static final class DefaultSettings {

        public static final String INITIAL_WORLD = "initialWorld";
        public static final String HOOK_REPOSITORY = "hookRepository";
        public static final String DEVELOPER_MODE = "developerMode";
        public static final String FAST_SCREENSHOT = "fastScreenshot";
        public static final String SCREENSHOT_FORMAT = "screenshotFormat";
        public static final String LOGOUT_RESIZE = "logoutResize";

        /**
         * Prevents external initialization.
         */
        private DefaultSettings() {

        }
    }

    private static final NumberFormat FORMAT = NumberFormat.getInstance();
    private static final Properties settings = new Properties();
    private static Path settingsPath;

    /**
     * Prevents external initialization.
     */
    private Settings() {

    }

    /**
     * Checks if a setting exists.
     *
     * @param name the setting name.
     * @return <code>true</code> if the setting exists, <code>false</code>
     *         otherwise.
     */
    public static boolean exists(String name) {
        return settings.containsKey(name);
    }

    /**
     * Retrieves the value of a setting.
     *
     * @param name the setting name.
     * @return the setting value, or <code>null</code> if the setting does not
     *         exist.
     */
    public static String get(String name) {
        return settings.getProperty(name);
    }

    /**
     * Retrieves the value of a setting.
     *
     * @param name         the setting name.
     * @param defaultValue the value to return if the setting does not exist.
     * @return the setting value.
     */
    public static String get(String name, String defaultValue) {
        return settings.getProperty(name, defaultValue);
    }

    /**
     * Retrieves the boolean value of a setting.
     *
     * @param name         the setting name.
     * @param defaultValue the value to return if the setting does not exist.
     * @return the setting value, returned as a <code>boolean</code>. If the
     *         setting exists, this method only returns <code>true</code> if
     *         the setting value equals <code>"true"</code>, ignoring case.
     */
    public static boolean getBoolean(String name, boolean defaultValue) {
        String value = get(name);
        return (value == null) ? defaultValue : Boolean.parseBoolean(value);
    }

    /**
     * Retrieves the numeric value of a setting.
     *
     * @param name the setting name.
     * @return the setting value, returned as a type of <code>Number</code>, or
     *         <code>null</code> if the setting does not exist.
     * @throws NumberFormatException if the value associated with <code>name</code>
     *                               is not a properly formatted number.
     */
    public static Number getNumeric(String name) {
        String value = get(name);
        try {
            return FORMAT.parse(value);
        } catch (NullPointerException e) {
            return null;
        } catch (ParseException e) {
            throw new NumberFormatException("This setting's value is not a properly formatted number.");
        }
    }

    /**
     * Retrieves the numeric value of a setting.
     *
     * @param name         the setting name.
     * @param defaultValue the value to return if the setting does not exist.
     * @return the setting value, returned as a type of <code>Number</code>.
     * @throws NumberFormatException if the value associated with <code>name</code>
     *                               is not a properly formatted number.
     */
    public static Number getNumeric(String name, Number defaultValue) {
        String value = get(name, FORMAT.format(defaultValue));
        try {
            return FORMAT.parse(value);
        } catch (ParseException e) {
            throw new NumberFormatException("This setting's value is not a properly formatted number.");
        }
    }

    /**
     * Sets a setting value.
     *
     * @param name  the setting name.
     * @param value the setting value.
     */
    public static void set(String name, String value) {
        settings.setProperty(name, value);
    }

    /**
     * Sets a setting value. The parameter <code>value</code> is converted to
     * its string representation.
     *
     * @param name  the setting name.
     * @param value the setting value.
     */
    public static void set(String name, Object value) {
        set(name, value.toString());
    }

    /**
     * Loads the stored settings from the settings file. The settings are
     * initially loaded when application starts up. Further invocations of this
     * method <em>reload</em> the settings from the file. Setting entries from
     * the file overwrite currently loaded ones (if such mappings exist). This
     * method does nothing if the application is running in virtual mode.
     *
     * @throws IOException if an I/O error occurs.
     */
    public static void load() throws IOException {
        if (!Application.isVirtualMode()) {
            settingsPath = Application.getPath(Application.NAME + ".settings");
            if (Files.exists(settingsPath)) {
                try (InputStream in = Files.newInputStream(settingsPath)) {
                    settings.load(in);
                }
            }
        }
    }

    static void save() throws IOException {
        if (settingsPath != null) {
            try (OutputStream out = Files.newOutputStream(settingsPath)) {
                settings.store(out, "User-defined application settings.");
            }
        }
    }
}