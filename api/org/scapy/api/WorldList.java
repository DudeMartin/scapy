package org.scapy.api;

import org.scapy.api.utils.FilterUtilities;
import org.scapy.core.accessors.IWorld;
import org.scapy.utils.Filter;
import org.scapy.utils.Filter.DefaultFilter;
import org.scapy.utils.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * A facility for reading the in-game world list. One important detail about
 * this class is that the internal world list gets initialized (and updated
 * thereafter) whenever the game client itself loads the world list. As a
 * consequence, in order for this class to reflect the latest world information,
 * the game client has to download a new world list. The user may manually force
 * this update by opening the world selection screen at the login interface, or
 * by opening the world switcher while logged in.
 *
 * @author Martin Tuskevicius
 */
public final class WorldList {

    /**
     * Represents a world type.
     */
    public enum Type {

        /**
         * A free-to-play world.
         */
        FREE(0),

        /**
         * A pay-to-play world.
         */
        MEMBERS(1),

        /**
         * A player-versus-player world.
         */
        PVP((1 << 2) | MEMBERS.mask),

        /**
         * A Bounty Hunter world.
         */
        BOUNTY((1 << 5) | MEMBERS.mask),

        /**
         * A world whose minimum total skill requirement is 1,500.
         */
        TOTAL_1500((1 << 7) | MEMBERS.mask),

        /**
         * A high risk world.
         */
        HIGH_RISK((1 << 10) | MEMBERS.mask),

        /**
         * A high risk, player-versus-player world.
         */
        PVP_HIGH_RISK(PVP.mask | HIGH_RISK.mask),

        /**
         * A world whose minimum total skill requirement is 2,000.
         */
        TOTAL_2000((1 << 18) | MEMBERS.mask),

        /**
         * A world whose minimum total skill requirement is 1,250.
         */
        TOTAL_1250((1 << 27) | MEMBERS.mask),

        /**
         * A world whose minimum total skill requirement is 1,750.
         */
        TOTAL_1750((1 << 28) | MEMBERS.mask),

        /**
         * A Deadman Mode world.
         */
        DEADMAN((1 << 29) | MEMBERS.mask),

        /**
         * A seasonal world.
         */
        SEASONAL((1 << 30) | MEMBERS.mask),

        /**
         * A seasonal, Deadman Mode world.
         */
        DEADMAN_SEASONAL(DEADMAN.mask | SEASONAL.mask);

        /**
         * The bit mask associated with this world type.
         */
        public final int mask;

        Type(int mask) {
            this.mask = mask;
        }

        /**
         * Determines if the specified world is of the provided type.
         *
         * @param world the world.
         * @param type  the world type.
         * @return <code>true</code> if the world is of the type,
         *         <code>false</code> otherwise.
         * @throws NullPointerException if <code>world</code> or <code>type</code>
         *                              is <code>null</code>.
         */
        public static boolean isType(IWorld world, Type type) {
            Preconditions.check(world != null && type != null, null, NullPointerException.class);
            return (world.getType() & type.mask) != 0;
        }
    }

    /**
     * Represents a server location.
     */
    public enum Location {

        /**
         * The server is located in the United States.
         */
        UNITED_STATES(0b0),

        /**
         * The server is located in the United Kingdom.
         */
        UNITED_KINGDOM(0b1),

        /**
         * The server is located in Germany.
         */
        GERMANY(0b111);

        /**
         * The bit mask associated with this server location.
         */
        public final int mask;

        Location(int mask) {
            this.mask = mask;
        }
    }

    private static final IWorld[] EMPTY = new IWorld[0];
    private static final Filter<IWorld> DEFAULT_FILTER = new DefaultFilter<IWorld>();
    private static volatile IWorld[] worldList;

    /**
     * Prevents external initialization.
     */
    private WorldList() {

    }

    /**
     * Updates the internal world list. This method is intended for internal use.
     *
     * @param worlds the new world list.
     */
    public static void update(IWorld[] worlds) {
        if (worlds != null) {
            worldList = worlds;
        }
    }

    /**
     * Checks if the internal world list has been initialized.
     *
     * @return <code>true</code> if the list has been initialized,
     *         <code>false</code> otherwise.
     */
    public static boolean isInitialized() {
        return worldList != null;
    }

    /**
     * Returns an array of worlds that match the criteria of the specified
     * filter. The returned array is a (shallow) copy of the internal world list.
     * Any changes made to the return array will not be reflected in the
     * internal list.
     *
     * <p>
     * If the internal world list has not been initialized or if no worlds match
     * the filter, then this method will return an array of length
     * <code>0</code>; it will never return <code>null</code>.
     *
     * @param worldFilter the world filter.
     * @return an array of worlds.
     * @throws NullPointerException if the internal world list is initialized
     *                              and <code>worldFilter</code> is
     *                              <code>null</code>.
     */
    public static IWorld[] get(Filter<IWorld> worldFilter) {
        if (isInitialized()) {
            Objects.requireNonNull(worldFilter);
            return FilterUtilities.filter(new ArrayList<>(Arrays.asList(worldList.clone())), worldFilter).toArray(EMPTY);
        }
        return EMPTY;
    }

    /**
     * Returns a shallow copy of the internal world list. If the internal world
     * list has not been initialized, an array of length <code>0</code> is
     * returned.
     *
     * @return an array of worlds.
     */
    public static IWorld[] get() {
        return get(DEFAULT_FILTER);
    }
}