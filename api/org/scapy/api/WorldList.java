package org.scapy.api;

import org.scapy.api.utils.Filter;
import org.scapy.api.utils.FilterUtilities;
import org.scapy.core.accessors.IWorld;

import java.util.ArrayList;
import java.util.Arrays;

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
        DEADMAN_SEASONAL(SEASONAL.mask | DEADMAN.mask);

        /**
         * The bit mask associated with this world type.
         */
        public final int mask;

        Type(int mask) {
            this.mask = mask;
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
    private static volatile IWorld[] worldList;

    /**
     * Prevents external initialization.
     */
    private WorldList() {

    }

    /**
     * Publishes a new world list. This method is intended for internal use.
     *
     * @param worlds the new world list.
     */
    public static void publish(IWorld[] worlds) {
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
     * Returns an array of worlds that match the filter.
     *
     * @param worldFilter the world filter.
     * @return an array of worlds. This array will have length <code>0</code> if
     *         no worlds match the filter or if the internal world list has not
     *         been initialized. This method will never return <code>null</code>.
     */
    public static IWorld[] get(Filter<IWorld> worldFilter) {
        return isInitialized() ? FilterUtilities.filter(new ArrayList<>(Arrays.asList(worldList.clone())), worldFilter).toArray(EMPTY) : EMPTY;
    }
}