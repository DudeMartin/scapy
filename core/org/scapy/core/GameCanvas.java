package org.scapy.core;

import org.scapy.Application;
import org.scapy.Settings;
import org.scapy.Settings.DefaultSettings;
import org.scapy.core.event.EventDispatcher;
import org.scapy.core.event.impl.PaintEvent;
import org.scapy.core.ui.GameWindow;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private static DateFormat dateFormat;
    private static ExecutorService screenshotService;
    private volatile BufferedImage backBuffer = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    private final AtomicBoolean screenshot = new AtomicBoolean();

    /**
     * Signals that a screenshot should be saved. This method does nothing if
     * the application is running in virtual mode.
     */
    public void takeScreenshot() {
        if (!Application.isVirtualMode()) {
            synchronized (GameCanvas.class) {
                if (dateFormat == null || screenshotService == null) {
                    dateFormat = new SimpleDateFormat("dd.MM.YYYY.HHmm.ss");
                    screenshotService = Executors.newSingleThreadExecutor(new ThreadFactory() {

                        @Override
                        public Thread newThread(Runnable r) {
                            return new Thread(r, "Screenshot Service Thread");
                        }
                    });
                }
            }
            screenshot.set(true);
        }
    }

    /**
     * Cleans up any resources utilized by the screenshot service. This method
     * is called during the shutdown procedure.
     */
    public void cleanupScreenshotResources() {
        synchronized (GameCanvas.class) {
            if (screenshotService != null) {
                screenshotService.shutdown();
            }
        }
    }

    @Override
    public Graphics getGraphics() {
        Graphics graphics = backBuffer.getGraphics();
        EventDispatcher.instance.dispatch(new PaintEvent(graphics));
        super.getGraphics().drawImage(backBuffer, 0, 0, null);
        update(graphics);
        if (screenshot.compareAndSet(true, false)) {
            BufferedImage screenshotImage;
            if (Settings.getBoolean(DefaultSettings.FAST_SCREENSHOT, false)) {
                screenshotImage = backBuffer;
            } else {
                screenshotImage = new BufferedImage(backBuffer.getWidth(), backBuffer.getHeight(), backBuffer.getType());
                Graphics screenshotGraphics = screenshotImage.getGraphics();
                screenshotGraphics.drawImage(backBuffer, 0, 0, null);
                screenshotGraphics.dispose();
            }
            saveScreenshot(screenshotImage);
        }
        graphics.dispose();
        return backBuffer.createGraphics();
    }

    @Override
    public void setSize(int width, int height) {
        int currentWidth = getWidth();
        int currentHeight = getHeight();
        if (width != currentWidth || height != currentHeight) {
            super.setSize(width, height);
            if (width > 0 && height > 0) {
                if (width <= currentWidth && height <= currentHeight) {
                    backBuffer = backBuffer.getSubimage(0, 0, width, height);
                } else {
                    backBuffer.flush();
                    backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                }
            }
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                GameInstance game = Application.getGame();
                JFrame window = GameWindow.getWindow();
                boolean resizable = game.getClientAccessor().isResizableMode();
                if (game.isLoggedIn()) {
                    if (window.isResizable() && !resizable) {
                        window.setResizable(false);
                        window.validate();
                        window.pack();
                    } else if (!window.isResizable() && resizable) {
                        window.setResizable(true);
                    }
                } else if (window.isResizable() && Settings.getBoolean(DefaultSettings.LOGOUT_RESIZE, true)) {
                    window.setResizable(false);
                    window.validate();
                    window.pack();
                }
            }
        });
    }

    @Override
    protected void processEvent(AWTEvent e) {
        EventDispatcher.instance.dispatch(e);
        super.processEvent(e);
    }

    private static synchronized void saveScreenshot(final BufferedImage screenshot) {
        if (!screenshotService.isShutdown()) {
            screenshotService.execute(new Runnable() {

                @Override
                public void run() {
                    String formatName = Settings.get(DefaultSettings.SCREENSHOT_FORMAT, DEFAULT_FORMAT);
                    String extension = formatName.toLowerCase();
                    String fileName = "Screenshot " + dateFormat.format(new Date()) + "." + extension;
                    String filePath = Application.getPath("screenshots", fileName).toString();
                    try {
                        if (!ImageIO.write(screenshot, formatName, new File(filePath))) {
                            String badFormatName = formatName;
                            formatName = DEFAULT_FORMAT;
                            Settings.set(DefaultSettings.SCREENSHOT_FORMAT, formatName);
                            ImageIO.write(screenshot, formatName, new File(filePath.replace(extension, formatName.toLowerCase())));
                            Application.showMessage("Unsupported screenshot format " + badFormatName + ". Defaulted to " + formatName + ".", "Screenshot Warning", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Application.showMessage("Could not save the screenshot.", "Screenshot Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
    }
}