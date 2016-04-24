package org.scapy;

import org.scapy.core.GameInstance;
import org.scapy.core.event.EventDispatcher;
import org.scapy.core.mod.HookDataException;
import org.scapy.core.plugin.PluginManager;
import org.scapy.core.ui.GameWindow;
import org.scapy.utils.Preconditions;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Permission;
import java.util.InputMismatchException;

/**
 * The main application class.
 *
 * @author Martin Tuskevicius
 */
public final class Application {

    /**
     * The name of this application.
     */
    public static final String NAME = "scapy";

    private static boolean forceVirtual;
    private static volatile GameInstance game;
    private static volatile boolean forcefulShutdown;
    private static volatile boolean userShutdown;

    /**
     * Prevents external initialization.
     */
    private Application() {

    }

    /**
     * Starts the application.
     *
     * @param args command-line arguments.
     * @throws Exception if an unexpected error occurs.
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Starting...");
        if (args.length > 0 && args[0].equalsIgnoreCase("virtual")) {
            forceVirtual = true;
        } else if (hasFilePermissions()) {
            try {
                initializeDirectories();
            } catch (Exception expected) {
                forceVirtual = true;
            }
        }
        try {
            Settings.initialize();
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Could not load previous settings.", "Settings Warning", JOptionPane.WARNING_MESSAGE);
        }
        SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                JFrame window = new GameWindow();
                window.setTitle(NAME);
                window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                window.setVisible(true);
            }
        });
        System.out.println("Loading the game...");
        try {
            game = new GameInstance();
        } catch (Exception e) {
            e.printStackTrace();
            forcefulShutdown = true;
            if (!userShutdown) {
                String errorMessage;
                if (e instanceof InputMismatchException) {
                    errorMessage = "Malformed hook data file. The repository may be out of date.";
                } else if (e instanceof HookDataException) {
                    errorMessage = "A problem with the hook data file occurred. " + e.getMessage();
                } else if (e instanceof ReflectiveOperationException) {
                    errorMessage = "Could not initialize the main game class.";
                } else if (e instanceof IOException) {
                    errorMessage = "A transmission error occurred while loading the game.";
                } else {
                    errorMessage = "Could not load the game.";
                }
                showMessage(errorMessage, "Load Error", JOptionPane.ERROR_MESSAGE);
                shutdown();
            }
            return;
        }
        if (!userShutdown) {
            System.out.println("Started.");
        }
    }

    /**
     * Gracefully shuts down this application. Invoking this method multiple
     * times has no additional effect.
     */
    public static synchronized void shutdown() {
        if (userShutdown) {
            return;
        }
        if (SwingUtilities.isEventDispatchThread()) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    shutdown();
                }
            }).start();
            return;
        }
        userShutdown = true;
        System.out.println("Shutting down...");
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                GameWindow.getWindow().setVisible(false);
            }
        });
        while (game == null && !forcefulShutdown) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}
        }
        if (game != null) {
            game.stop();
            game.getCanvas().cleanupScreenshotResources();
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (GameWindow.getWindow().isDisplayable()) {
                    GameWindow.getWindow().cleanup();
                }
            }
        });
        PluginManager.instance.stopAll(10000);
        EventDispatcher.instance.clearListeners();
        try {
            Settings.save();
        } catch (IOException e) {
            System.err.println("Could not save the settings.");
            e.printStackTrace();
        }
    }

    /**
     * Checks if this application has security permissions to operate on certain
     * files.
     *
     * @return <code>true</code> if this application has file permissions,
     *         <code>false</code> otherwise.
     */
    public static boolean hasFilePermissions() {
        SecurityManager manager = System.getSecurityManager();
        if (manager != null) {
            try {
                StringBuilder pathBuilder = new StringBuilder(32);
                pathBuilder.append(System.getProperty("user.home"));
                pathBuilder.append(File.separatorChar);
                pathBuilder.append('-');
                Permission filePermission = new FilePermission(pathBuilder.toString(), "read,write");
                try {
                    manager.checkPermission(filePermission);
                } catch (SecurityException e) {
                    pathBuilder.deleteCharAt(pathBuilder.length() - 1);
                    pathBuilder.append(NAME);
                    pathBuilder.append(File.separatorChar);
                    pathBuilder.append('-');
                    filePermission = new FilePermission(pathBuilder.toString(), "read,write");
                    try {
                        manager.checkPermission(filePermission);
                    } catch (SecurityException ex) {
                        return false;
                    }
                }
            } catch (SecurityException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if this application is running in virtual mode.
     *
     * @return <code>true</code> if this application is running in virtual mode,
     *         <code>false</code> otherwise.
     */
    public static boolean isVirtualMode() {
        return forceVirtual || !hasFilePermissions();
    }

    /**
     * Returns the path to a file within this application's directory.
     *
     * @param relative the relative paths within the directory.
     * @return the application path.
     * @throws IllegalStateException if the application is running in virtual
     *                               mode.
     */
    public static Path getApplicationPath(String... relative) {
        Preconditions.check(!isVirtualMode(), "The application cannot be running in virtual mode.", IllegalStateException.class);
        String[] relativeParts = new String[relative.length + 1];
        relativeParts[0] = NAME;
        System.arraycopy(relative, 0, relativeParts, 1, relative.length);
        return Paths.get(System.getProperty("user.home"), relativeParts);
    }

    /**
     * Returns the path to this application's directory.
     *
     * @return the path to the directory.
     * @throws IllegalStateException if the application is running in virtual
     *                               mode.
     */
    public static Path getDirectory() {
        return getApplicationPath();
    }

    /**
     * Returns the <code>GameInstance</code> object associated with this
     * application.
     *
     * @return the game, or <code>null</code> if it has not been started yet.
     */
    public static GameInstance getGame() {
        return game;
    }

    /**
     * Displays a message dialog and blocks the currently executing thread until
     * it is dismissed. The dialog is created and displayed on the event
     * dispatch thread.
     *
     * @param message the message to display.
     * @param title   the dialog title.
     * @param type    the message type.
     * @see JOptionPane#showMessageDialog(Component, Object, String, int)
     */
    public static void showMessage(final String message, final String title, final int type) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    JOptionPane.showMessageDialog(GameWindow.getWindow(), message, title, type);
                }
            });
        } catch (InterruptedException | InvocationTargetException ignored) {}
    }

    private static boolean checkReadWritePrivileges(Path path) {
        return Files.isReadable(path) && Files.isWritable(path);
    }

    private static void initializeDirectories() throws IOException {
        Path mainDirectory = getDirectory();
        if (!Files.exists(mainDirectory)) {
            Files.createDirectory(mainDirectory);
        }
        if (!checkReadWritePrivileges(mainDirectory)) {
            forceVirtual = true;
        }
        Path dataDirectory = mainDirectory.resolve("data");
        if (!Files.exists(dataDirectory)) {
            Files.createDirectory(dataDirectory);
        }
        if (!checkReadWritePrivileges(dataDirectory)) {
            forceVirtual = true;
        }
        Path screenshotDirectory = mainDirectory.resolve("screenshots");
        if (!Files.exists(screenshotDirectory)) {
            Files.createDirectory(screenshotDirectory);
        }
        if (!checkReadWritePrivileges(screenshotDirectory)) {
            forceVirtual = true;
        }
    }
}