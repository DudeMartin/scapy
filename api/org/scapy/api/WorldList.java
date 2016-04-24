package org.scapy.api;

import org.scapy.api.utils.FilterUtilities;
import org.scapy.core.accessors.IWorld;
import org.scapy.utils.Filter;
import org.scapy.utils.Filter.DefaultFilter;
import org.scapy.utils.Preconditions;
import org.scapy.utils.WebUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A facility for reading the world list.
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
         * A high risk, player-versus-player world.
         */
        PVP_HIGH_RISK(PVP.mask | HIGH_RISK.mask),

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
        SEASONAL(1 << 30),

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
            int worldMask = world.getType();
            int typeMask = type.mask;
            return (worldMask == typeMask) || ((worldMask & typeMask) != 0);
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

    /**
     * A <code>Filter</code> implementation that matches worlds based on their
     * number.
     */
    public static final class NumberFilter implements Filter<IWorld> {

        /**
         * The world number to match.
         */
        public final int number;

        /**
         * Creates a new world number filter.
         *
         * @param number the number to match.
         */
        public NumberFilter(int number) {
            this.number = number;
        }

        @Override
        public boolean matches(IWorld test) {
            return test.getNumber() == number;
        }
    }

    /**
     * A <code>Filter</code> implementation that matches worlds based on their
     * location.
     */
    public static final class LocationFilter implements Filter<IWorld> {

        /**
         * The world location to match.
         */
        public final Location location;

        /**
         * Creates a new world location filter.
         *
         * @param location the location to match.
         * @throws NullPointerException if <code>location</code> is
         *                              <code>null</code>.
         */
        public LocationFilter(Location location) {
            Objects.requireNonNull(location);
            this.location = location;
        }

        @Override
        public boolean matches(IWorld test) {
            return test.getLocation() == location.mask;
        }
    }

    /**
     * A <code>Filter</code> implementation that matches worlds based on their
     * type.
     */
    public static final class TypeFilter implements Filter<IWorld> {

        /**
         * The world type to match.
         */
        public final Type type;

        /**
         * Creates a new world type filter.
         *
         * @param type the type to match.
         * @throws NullPointerException if <code>type</code> is <code>null</code>.
         */
        public TypeFilter(Type type) {
            Objects.requireNonNull(type);
            this.type = type;
        }

        @Override
        public boolean matches(IWorld test) {
            return Type.isType(test, type);
        }
    }

    /**
     * A <code>Filter</code> implementation that matches worlds based on their
     * activity.
     */
    public static final class ActivityFilter implements Filter<IWorld> {

        /**
         * The world activity to match.
         */
        public final String activity;

        /**
         * If this filter should match the world activity exactly. If this value
         * is <code>false</code>, then this filter checks if the world activity
         * string contains <code>activity</code>, rather than if it equals it.
         */
        public final boolean exact;

        /**
         * Creates a new world activity filter.
         *
         * @param activity the activity to match.
         * @param exact    if this filter should match the world activity
         *                 exactly.
         * @throws NullPointerException if <code>activity</code> is
         *                              <code>null</code>.
         */
        public ActivityFilter(String activity, boolean exact) {
            Objects.requireNonNull(activity);
            this.activity = activity;
            this.exact = exact;
        }

        /**
         * Creates a new world activity filter that matches worlds without an
         * official activity.
         */
        public ActivityFilter() {
            this("-", true);
        }

        @Override
        public boolean matches(IWorld test) {
            return exact ? test.getActivity().equals(activity) : test.getActivity().contains(activity);
        }
    }

    private static final String WORLD_LIST_ADDRESS = "http://oldschool.runescape.com/slu";
    private static final Pattern WORLD_LIST_PATTERN = Pattern.compile("<tr class='server-list.*?world=(\\d+).*?__row.*?(\\d+) players.*?__row.*?>(.+?)</td>.*?__row.*?>([a-z ]+)</td>.*?__row.*?>(.+?)</td>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final IWorld[] EMPTY = new IWorld[0];
    private static final Filter<IWorld> DEFAULT_FILTER = new DefaultFilter<IWorld>();
    private static volatile IWorld[] worldList;

    /**
     * Prevents external initialization.
     */
    private WorldList() {

    }

    /**
     * Updates the internal world list. This method is intended for internal
     * use.
     *
     * @param worlds the new world list.
     * @throws NullPointerException if <code>worlds</code> is <code>null</code>.
     */
    public static void update(IWorld[] worlds) {
        Objects.requireNonNull(worlds);
        worldList = worlds;
    }

    /**
     * Downloads, updates, and returns a new world list.
     *
     * @return the new world list.
     * @throws IOException if an I/O error occurs.
     */
    public static IWorld[] download() throws IOException {
        Matcher matcher = WORLD_LIST_PATTERN.matcher(WebUtilities.downloadPageSource(WORLD_LIST_ADDRESS));
        List<IWorld> worlds = new ArrayList<>();
        while (matcher.find()) {
            worlds.add(processWorldListing(matcher));
        }
        return (worldList = worlds.toArray(EMPTY));
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

    private static IWorld processWorldListing(Matcher worldMatcher) {
        final int number = Integer.parseInt(worldMatcher.group(1));
        final int playerCount = Integer.parseInt(worldMatcher.group(2));
        String location = worldMatcher.group(3);
        String type = worldMatcher.group(4);
        final String activity = worldMatcher.group(5);
        int temporaryTypeMask = 0;
        switch (type) {
            case "Free":
                temporaryTypeMask |= Type.FREE.mask;
                break;
            case "Members":
                temporaryTypeMask |= Type.MEMBERS.mask;
                break;
            default:
                throw new UnsupportedOperationException("Unexpected world type: " + location + ".");
        }
        switch (activity) {
            case "PVP World":
                temporaryTypeMask |= Type.PVP.mask;
                break;
            case "Bounty World":
                temporaryTypeMask |= Type.BOUNTY.mask;
                break;
            case "1500 skill total":
                temporaryTypeMask |= Type.TOTAL_1500.mask;
                break;
            case "High Risk World":
                temporaryTypeMask |= Type.HIGH_RISK.mask;
                break;
            case "PVP World - High Risk":
                temporaryTypeMask |= Type.PVP_HIGH_RISK.mask;
                break;
            case "2000 skill total":
                temporaryTypeMask |= Type.TOTAL_2000.mask;
                break;
            case "1250 skill total":
                temporaryTypeMask |= Type.TOTAL_1250.mask;
                break;
            case "1750 skill total":
                temporaryTypeMask |= Type.TOTAL_1750.mask;
                break;
            case "Deadman":
                temporaryTypeMask |= Type.DEADMAN.mask;
                break;
            case "Deadman Seasonal":
                temporaryTypeMask |= Type.DEADMAN_SEASONAL.mask;
                break;
        }
        final int locationMask;
        switch (location) {
            case "United States":
                locationMask = Location.UNITED_STATES.mask;
                break;
            case "United Kingdom":
                locationMask = Location.UNITED_KINGDOM.mask;
                break;
            case "Germany":
                locationMask = Location.GERMANY.mask;
                break;
            default:
                throw new UnsupportedOperationException("Unexpected world location: " + location + ".");
        }
        final int typeMask = temporaryTypeMask;
        return new IWorld() {

            @Override
            public int getNumber() {
                return number;
            }

            @Override
            public int getType() {
                return typeMask;
            }

            @Override
            public String getActivity() {
                return activity;
            }

            @Override
            public int getLocation() {
                return locationMask;
            }

            @Override
            public int getPlayerCount() {
                return playerCount;
            }
        };
    }
}