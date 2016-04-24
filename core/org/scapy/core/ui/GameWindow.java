package org.scapy.core.ui;

import org.scapy.Application;
import org.scapy.Settings;
import org.scapy.Settings.DefaultSettings;
import org.scapy.core.GameInstance;
import org.scapy.core.event.EventDispatcher;
import org.scapy.core.event.impl.PluginEvent;
import org.scapy.core.event.listeners.PluginAdapter;
import org.scapy.core.plugin.Plugin;

import javax.swing.*;
import java.applet.Applet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

/**
 * The application user interface.
 *
 * @author Martin Tuskevicius
 */
public final class GameWindow extends JFrame {

    private static volatile GameWindow window;

    /**
     * Returns the instance of <code>GameWindow</code>.
     *
     * @return the single instance of this class, or <code>null</code> if the
     *         window has not been initialized yet.
     */
    public static GameWindow getWindow() {
        return window;
    }

    static {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    }

    private final PluginLoaderDialog pluginLoader = new PluginLoaderDialog();
    private final PluginSettingsDialog pluginSettings = new PluginSettingsDialog();
    private final JMenuBar menuBar = new JMenuBar();
    private final JCheckBoxMenuItem developerItem = new JCheckBoxMenuItem("Developer mode");
    private final JMenuItem screenshotItem = new JMenuItem("Screenshot");
    private final JMenuItem defaultWorldItem = new JMenuItem("Default world");
    private final JMenu pluginMenu = new JMenu("Plugin");
    private final JMenuItem loadItem = new JMenuItem("Load");
    private final JMenuItem settingsItem = new JMenuItem("Settings");
    private final JMenu debugMenu = new JMenu("Debug");
    private final JPanel gamePanel = new GamePanel();

    /**
     * Creates a new game window.
     *
     * @throws IllegalStateException if an instance of <code>GameWindow</code>
     *                               already exists.
     */
    public GameWindow() {
        if (window != null) {
            throw new IllegalStateException("The game window should only be created once.");
        }
        window = this;
        if (Application.isVirtualMode()) {
            defaultWorldItem.setEnabled(false);
        }
        if (Settings.getBoolean(DefaultSettings.DEVELOPER_MODE, false)) {
            developerItem.setSelected(true);
            debugMenu.setEnabled(true);
        } else {
            debugMenu.setEnabled(false);
        }
        screenshotItem.setEnabled(false);
        pluginMenu.setEnabled(false);
        debugMenu.setVisible(false);
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(developerItem);
        fileMenu.add(screenshotItem);
        fileMenu.add(defaultWorldItem);
        pluginMenu.add(loadItem);
        pluginMenu.add(settingsItem);
        menuBar.add(fileMenu);
        menuBar.add(pluginMenu);
        menuBar.add(debugMenu);
        setJMenuBar(menuBar);
        add(gamePanel);
        addMenuListeners();
        addWindowListener(new WindowEvents());
        EventDispatcher.instance.addListener(new PluginEvents());
        setResizable(false);
        pack();
    }

    /**
     * Adds a game applet to this window. This method is called by <code>GameInstance</code>.
     *
     * @param gameApplet the applet to add.
     * @throws UnsupportedOperationException if an applet is already added.
     */
    public void addGameApplet(final Applet gameApplet) {
        synchronized (gamePanel.getTreeLock()) {
            if (gamePanel.getComponentCount() == 1) {
                throw new UnsupportedOperationException("Only one game applet can be displayed.");
            }
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                gamePanel.add(gameApplet);
                screenshotItem.setEnabled(!Application.isVirtualMode());
                pluginMenu.setEnabled(true);
                debugMenu.setVisible(true);
                pack();
            }
        });
    }

    /**
     * Adds an item to the developer's debug menu.
     *
     * @param debugItem the item to add to the debug menu.
     */
    public void addDebugItem(final JMenuItem debugItem) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                debugMenu.add(debugItem);
            }
        });
    }

    /**
     * Cleans up this window by disposing any screen resources held by it. This
     * method is called during the shutdown procedure.
     */
    public void cleanup() {
        dispose();
        pluginLoader.dispose();
        pluginSettings.dispose();
    }

    private void addMenuListeners() {
        developerItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = developerItem.isSelected();
                Settings.set(DefaultSettings.DEVELOPER_MODE, selected);
                debugMenu.setEnabled(selected);
            }
        });
        screenshotItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Application.getGame().getCanvas().takeScreenshot();
            }
        });
        defaultWorldItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog(GameWindow.this, "Please enter a default world number.", "Enter Default World", JOptionPane.INFORMATION_MESSAGE);
                int world;
                try {
                    world = Integer.parseInt(input);
                } catch (NumberFormatException ex) {
                    world = -1;
                }
                if (world <= 0) {
                    JOptionPane.showMessageDialog(GameWindow.this, "Please enter a valid, positive integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Settings.set(DefaultSettings.INITIAL_WORLD, world);
            }
        });
        loadItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pluginLoader.setVisible(true);
                pluginLoader.toFront();
            }
        });
        settingsItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pluginSettings.setVisible(true);
            }
        });
    }

    private class WindowEvents extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            GameInstance game = Application.getGame();
            int option = 0;
            if (game != null && game.isLoggedIn()) {
                option = JOptionPane.showConfirmDialog(GameWindow.this,
                        "You are currently logged in. Are you sure you want to quit?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION);
            }
            if (option == 0) {
                Application.shutdown();
            }
        }
    }

    private class PluginEvents extends PluginAdapter {

        @Override
        public void onStart(final PluginEvent e) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    Plugin plugin = e.getSource();
                    pluginSettings.addPlugin(plugin);
                    JMenu pluginMenu = plugin.getMenu();
                    if (pluginMenu != null) {
                        menuBar.add(pluginMenu);
                    }
                }
            });
        }

        @Override
        public void onStop(final PluginEvent e) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        Plugin plugin = e.getSource();
                        pluginSettings.removePlugin(plugin);
                        JMenu pluginMenu = plugin.getMenu();
                        if (pluginMenu != null) {
                            menuBar.remove(pluginMenu);
                        }
                    }
                });
            } catch (InterruptedException | InvocationTargetException ignored) {}
        }
    }
}