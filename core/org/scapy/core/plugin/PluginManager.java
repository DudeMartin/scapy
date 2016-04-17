package org.scapy.core.plugin;

import org.scapy.core.event.EventDispatcher;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A facility for managing plugins across the application.
 *
 * @author Martin Tuskevicius
 */
public final class PluginManager {

    /**
     * The single instance of the <code>PluginManager</code> class.
     */
    public static final PluginManager instance = new PluginManager();

    private final List<Plugin> plugins = new ArrayList<>();
    private final ThreadGroup pluginThreads = new ThreadGroup("Plugin Threads");
    private final AtomicInteger stopCounter = new AtomicInteger();
    private final Object stopLock = new Object();

    /**
     * Prevents external initialization.
     */
    private PluginManager() {

    }

    /**
     * Starts a plugin.
     *
     * @param plugin the plugin to start.
     * @throws IllegalArgumentException if another plugin with the same name is
     *                                  currently running.
     */
    public void startPlugin(Plugin plugin) {
        String name = plugin.name();
        synchronized (plugins) {
            if (get(name) != null) {
                throw new IllegalArgumentException("Cannot start multiple plugins with the same name.");
            }
            plugins.add(plugin);
        }
        if (plugin instanceof EventListener) {
            EventListener listener = (EventListener) plugin;
            EventDispatcher.instance.addListener(listener);
        }
        new Thread(pluginThreads, plugin, name + " Plugin Thread").start();
    }

    /**
     * Signals that all currently running plugins should gracefully stop. Any
     * new plugins that are started before this method returns will be
     * unaffected.
     *
     * <p>
     * The <code>timeout</code> parameter specifies how long, in milliseconds,
     * this method should wait for the plugins to gracefully stop. If the
     * timeout is longer than the amount of time it takes for the plugins to
     * stop, then this method returns before the timeout elapses. Conversely, if
     * the timeout is shorter, then this method silently returns anyway, with no
     * guarantee that the plugins have been stopped. In the latter case, the
     * plugins will continue to stop in their separate threads.
     *
     * <p>
     * A <code>timeout</code> value of <code>0</code> may be passed to instruct
     * this method to wait as long as necessary for the plugins to stop. A
     * negative timeout value indicates that this method should not wait for any
     * duration of time.
     *
     * <p>
     * If a non-negative timeout is provided and this method is called from a
     * plugin thread, it will return once the only unstopped plugin is the
     * plugin that called this method, regardless of the (remaining) timeout.
     * Alternatively, this method will also immediately return if the thread
     * executing this method is interrupted while elapsing the timeout.
     *
     * @param timeout the maximum amount of time, in milliseconds, to wait for
     *                the plugins to stop.
     */
    public synchronized void stopAll(long timeout) {
        boolean wait = (timeout >= 0);
        synchronized (plugins) {
            if (wait) {
                stopCounter.set(plugins.size());
            }
            for (Plugin plugin : plugins) {
                plugin.stop();
            }
        }
        if (wait) {
            boolean waitFully = (timeout == 0);
            long startWait;
            synchronized (stopLock) {
                while (stopCounter.get() > 0 && (waitFully || timeout > 0)) {
                    if (stopCounter.get() == 1 && Thread.currentThread().getThreadGroup() == pluginThreads) {
                        break;
                    }
                    try {
                        startWait = System.currentTimeMillis();
                        stopLock.wait(waitFully ? 0 : timeout);
                    } catch (InterruptedException e) {
                        break;
                    }
                    timeout -= (System.currentTimeMillis() - startWait);
                }
            }
        }
    }

    /**
     * Retrieves a plugin whose name equals <code>name</code>.
     *
     * @param name the plugin name.
     * @return the plugin, or <code>null</code> if no such plugin exists.
     */
    public Plugin get(String name) {
        synchronized (plugins) {
            for (Plugin plugin : plugins) {
                if (plugin.name().equals(name)) {
                    return plugin;
                }
            }
        }
        return null;
    }

    void removePlugin(Plugin plugin) {
        synchronized (plugins) {
            plugins.remove(plugin);
        }
        if (plugin instanceof EventListener) {
            EventListener listener = (EventListener) plugin;
            EventDispatcher.instance.removeListener(listener);
        }
        if (stopCounter.get() > 0) {
            stopCounter.decrementAndGet();
            synchronized (stopLock) {
                stopLock.notify();
            }
        }
    }
}