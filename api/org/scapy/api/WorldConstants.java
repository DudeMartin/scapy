package org.scapy.api;

import org.scapy.core.accessors.IWorld;
import org.scapy.utils.Preconditions;

import java.util.EnumSet;

/**
 * A collection of world constants.
 *
 * @author Martin Tuskevicius
 */
public final class WorldConstants {

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
        PVP(1 << 2),

        /**
         * A Bounty Hunter world.
         */
        BOUNTY(1 << 5),

        /**
         * A world whose minimum total skill requirement is 1,500.
         */
        TOTAL_1500(1 << 7),

        /**
         * A high risk world.
         */
        HIGH_RISK(1 << 10),

        /**
         * A world whose minimum total skill requirement is 2,000.
         */
        TOTAL_2000(1 << 18),

        /**
         * A world whose minimum total skill requirement is 1,250.
         */
        TOTAL_1250(1 << 27),

        /**
         * A world whose minimum total skill requirement is 1,750.
         */
        TOTAL_1750(1 << 28),

        /**
         * A Deadman Mode world.
         */
        DEADMAN(1 << 29),

        /**
         * A seasonal world.
         */
        SEASONAL(1 << 30);

        /**
         * The bit flag associated with this world type.
         */
        public final int flag;

        Type(int flag) {
            this.flag = flag;
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
            Preconditions.checkNull(world, type);
            int worldFlags = world.getType();
            int typeFlag = type.flag;
            return (type == FREE && (worldFlags & 1) == 0) || ((worldFlags & typeFlag) != 0);
        }

        /**
         * Returns an <code>EnumSet</code> of <code>Type</code> objects that
         * characterize the type of the specified world.
         *
         * @param world the world.
         * @return a set of world type constants; it may be empty.
         * @throws NullPointerException if <code>world</code> is <code>null</code>.
         */
        public static EnumSet<Type> getType(IWorld world) {
            EnumSet<Type> types = EnumSet.noneOf(Type.class);
            for (Type type : Type.values()) {
                if (isType(world, type)) {
                    types.add(type);
                }
            }
            return types;
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
         * The bit flag associated with this server location.
         */
        public final int flag;

        Location(int flag) {
            this.flag = flag;
        }

        /**
         * Returns a <code>Location</code> object corresponding to the provided
         * world's server location.
         *
         * @param world the world.
         * @return a world location constant.
         * @throws IllegalArgumentException if no constant matches the provided
         *                                  world's location.
         */
        public static Location getLocation(IWorld world) {
            for (Location location : Location.values()) {
                if (world.getLocation() == location.flag) {
                    return location;
                }
            }
            throw new IllegalArgumentException("Unrecognized world location.");
        }
    }

    /**
     * Prevents external initialization.
     */
    private WorldConstants() {

    }
}