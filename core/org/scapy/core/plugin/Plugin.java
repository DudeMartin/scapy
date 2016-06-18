package org.scapy.core.plugin;

import org.scapy.core.event.EventDispatcher;
import org.scapy.core.event.impl.PluginEvent;
import org.scapy.core.event.impl.PluginEvent.PluginState;

import javax.swing.JMenu;
import javax.swing.JPanel;

public abstract class Plugin implements Runnable {

    private final Object lock = new Object();
    private volatile boolean stopped;
    private volatile boolean paused;

    @Override
    public final void run() {
        EventDispatcher events = EventDispatcher.instance;
        events.dispatch(new PluginEvent(this, PluginState.STARTED));
        boolean freshPause;
        long time;
        try {
            while (!stopped) {
                freshPause = true;
                synchronized (lock) {
                    while (paused) {
                        if (freshPause) {
                            events.dispatch(new PluginEvent(this, PluginState.PAUSED));
                            freshPause = false;
                        }
                        try {
                            lock.wait();
                        } catch (InterruptedException expected) {
                            paused = false;
                        } finally {
                            if (!paused) {
                                events.dispatch(new PluginEvent(this, PluginState.RESUMED));
                                freshPause = true;
                            }
                        }
                    }
                }
                time = execute();
                if (time < 0) {
                    stopped = true;
                } else if (time > 0) {
                    sleep(time);
                }
            }
        } finally {
            events.dispatch(new PluginEvent(this, PluginState.STOPPED));
            PluginManager.instance.removePlugin(this);
        }
    }

    protected abstract long execute();

    public JPanel getSettingsPanel() {
        return null;
    }

    public JMenu getMenu() {
        return null;
    }

    protected final void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    protected final void sleepExact(long time) {
        long start = System.currentTimeMillis();
        sleep(time);
        long actualSleep = System.currentTimeMillis() - start;
        if (actualSleep < time) {
            sleepExact(time - actualSleep);
        }
    }

    public final void stop() {
        stopped = true;
        if (paused) {
            resume();
        }
    }

    public final void pause() {
        paused = true;
    }

    public final void resume() {
        paused = false;
        synchronized (lock) {
            lock.notify();
        }
    }

    public final boolean isStopped() {
        return stopped;
    }

    public final boolean isPaused() {
        return !stopped && paused;
    }

    public final PluginManifest manifest() {
        return getClass().getAnnotation(PluginManifest.class);
    }

    public final String name() {
        return manifest().name();
    }
}