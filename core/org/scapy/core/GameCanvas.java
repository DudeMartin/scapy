package org.scapy.core;

import org.scapy.Application;
import org.scapy.Settings;
import org.scapy.Settings.DefaultSettings;
import org.scapy.core.event.EventDispatcher;
import org.scapy.core.event.impl.PaintEvent;
import org.scapy.core.ui.GameWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class GameCanvas extends Canvas {

    /**
     * The default width dimension of the game canvas.
     */
    public static final int DEFAULT_WIDTH = 765;

    /**
     * The default height dimension of the game canvas.
     */
    public static final int DEFAULT_HEIGHT = 503;

    private static final String DEFAULT_FORMAT = "PNG";
    private static String formatName;
    private static DateFormat dateFormat;
    private static volatile ExecutorService screenshotService;
    private BufferedImage backBuffer = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    private volatile boolean screenshot;

    /**
     * Signals that a screenshot should be saved. This method does nothing if
     * the application is running in virtual mode.
     */
    public void takeScreenshot() {
        if (!Application.isVirtualMode()) {
            screenshot = true;
        }
    }

    /**
     * Cleans up any resources utilized by the screenshot service. This method
     * is called during the shutdown procedure.
     */
    public void cleanupScreenshotResources() {
        if (screenshotService != null) {
            screenshotService.shutdown();
        }
    }

    @Override
    public Graphics getGraphics() {
        Graphics graphics = backBuffer.getGraphics();
        graphics.setColor(Color.WHITE);
        EventDispatcher.instance.dispatch(new PaintEvent(graphics));
        super.getGraphics().drawImage(backBuffer, 0, 0, null);
        update(graphics);
        if (screenshot) {
            saveScreenshot();
            screenshot = false;
        }
        return backBuffer.createGraphics();
    }

    @Override
    public void setSize(int width, int height) {
        if(width != getWidth() && height != getHeight()) {
            super.setSize(width, height);
            if (width > 0 && height > 0) {
                backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            }
        }
    }

    @Override
    protected void processEvent(AWTEvent e) {
        EventDispatcher.instance.dispatch(e);
        super.processEvent(e);
    }

    private void saveScreenshot() {
        if (formatName == null || dateFormat == null || screenshotService == null) {
            formatName = Settings.get(DefaultSettings.SCREENSHOT_FORMAT, DEFAULT_FORMAT);
            dateFormat = new SimpleDateFormat("dd.MM.YYYY.HHmm.ss");
            screenshotService = Executors.newSingleThreadExecutor(new ThreadFactory() {

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "Screenshot Service Thread");
                }
            });
        }
        screenshotService.submit(new Runnable() {

            @Override
            public void run() {
                String fileName = "Screenshot " + dateFormat.format(new Date()) + '.' + formatName.toLowerCase();
                File screenshotFile = Application.getApplicationPath("screenshots", fileName).toFile();
                try {
                    try {
                        if (!ImageIO.write(backBuffer, formatName, screenshotFile)) {
                            formatName = DEFAULT_FORMAT;
                            Settings.set(DefaultSettings.SCREENSHOT_FORMAT, formatName);
                            ImageIO.write(backBuffer, formatName, screenshotFile);
                            SwingUtilities.invokeAndWait(new Runnable() {

                                @Override
                                public void run() {
                                    JOptionPane.showMessageDialog(GameWindow.getWindow(),
                                            "Unsupported screenshot format. Defaulted to " + formatName + ".",
                                            "Screenshot Warning",
                                            JOptionPane.WARNING_MESSAGE);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        SwingUtilities.invokeAndWait(new Runnable() {

                            @Override
                            public void run() {
                                JOptionPane.showMessageDialog(GameWindow.getWindow(),
                                        "Could not save the screenshot.",
                                        "Screenshot Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    }
                } catch (Exception ignored) {}
            }
        });
    }
}