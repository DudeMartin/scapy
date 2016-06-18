package org.scapy.api.entities;

import org.scapy.api.Game;
import org.scapy.api.utils.filter.FilterUtilities;
import org.scapy.api.wrapper.Player;
import org.scapy.core.accessors.IPlayer;
import org.scapy.utils.Filter;

import java.util.Arrays;

/**
 * A facility for retrieving local players.
 *
 * @author Martin Tuskevicius
 */
public final class Players {

    /**
     * A <code>Filter</code> implementation that matches all players other than
     * the local one.
     */
    public static final class RemotePlayerFilter implements Filter<Player> {

        /**
         * The single instance of the <code>RemotePlayerFilter</code> class.
         */
        public static final RemotePlayerFilter INSTANCE = new RemotePlayerFilter();

        /**
         * Prevents external initialization.
         */
        private RemotePlayerFilter() {

        }

        @Override
        public boolean matches(Player test) {
            Player local = local();
            return local != null && (test.accessor() != local.accessor() || !test.getName().equals(local.getName()));
        }
    }

    /**
     * Prevents external initialization.
     */
    private Players() {

    }

    /**
     * Returns the local player. This is the player that the user is
     * controlling.
     *
     * @return the local player, possibly <code>null</code>.
     */
    public static Player local() {
        IPlayer accessor = Game.clientAccessor().getLocalPlayer();
        return (accessor == null) ? null : new Player(accessor);
    }

    /**
     * Returns an array of local players.
     *
     * @return the local players, or <code>null</code>.
     */
    public static Player[] players() {
        IPlayer[] accessors = Game.clientAccessor().getPlayers();
        if (accessors != null) {
            Player[] wrappers = new Player[accessors.length];
            int validCount = 0;
            for (IPlayer accessor : accessors) {
                if (accessor != null) {
                    wrappers[validCount++] = new Player(accessor);
                }
            }
            return Arrays.copyOf(wrappers, validCount);
        }
        return null;
    }

    /**
     * Returns a filtered array of local players.
     *
     * @param filter the player filter.
     * @return the filtered local players, or <code>null</code>.
     * @throws NullPointerException if <code>filter</code> is <code>null</code>.
     */
    public static Player[] players(Filter<Player> filter) {
        Player[] wrappers = players();
        return (wrappers == null) ? null : FilterUtilities.filter(wrappers, filter);
    }

    /**
     * Returns an array of local players that excludes the user's player.
     *
     * @return the other players.
     */
    public static Player[] otherPlayers() {
        return players(RemotePlayerFilter.INSTANCE);
    }
}