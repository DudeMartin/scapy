package org.scapy.api;

import org.scapy.Application;
import org.scapy.core.accessors.IClient;

/**
 * A facility for retrieving general game-related information.
 *
 * @author Martin Tuskevicius
 */
public final class Game {

    /**
     * Prevents external initialization.
     */
    private Game() {

    }

    /**
     * Returns the instance of the <code>IClient</code> accessor.
     *
     * @return the accessor.
     */
    public static IClient clientAccessor() {
        return Application.getGame().getClientAccessor();
    }

    /**
     * Checks if the client is currently logged into a game world.
     *
     * @return <code>true</code> if the client is logged in, <code>false</code>
     *         otherwise.
     */
    public static boolean isLoggedIn() {
        return Application.getGame().isLoggedIn();
    }

    /**
     * Checks if the client is running in resizable mode.
     *
     * @return <code>true</code> if the client is in resizable mode,
     *         <code>false</code> otherwise.
     */
    public static boolean isResizable() {
        return clientAccessor().isResizableMode();
    }
}