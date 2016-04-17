package org.scapy.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A collection of world-related utilities.
 *
 * @author Martin Tuskevicius
 */
public final class WorldUtilities {

    public enum WorldType {

        FREE,
        MEMBERS
    }

    public static final class World {

        public final int number;
        public final int players;
        public final String location;
        public final WorldType type;
        public final String activity;

        private World(int number, int players, String location, WorldType type, String activity) {
            this.number = number;
            this.players = players;
            this.location = location;
            this.type = type;
            this.activity = activity;
        }

        public boolean hasActivity() {
            return !activity.equals("-");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o instanceof World) {
                World world = (World) o;
                return world.number == number
                        && world.players == players
                        && world.location.equals(location)
                        && world.type == type
                        && world.activity.equals(activity);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(number, players, location, type, activity);
        }
    }

    public static final class WorldNumberFilter implements Filter<World> {

        public final int number;

        public WorldNumberFilter(int number) {
            this.number = number;
        }

        @Override
        public boolean matches(World test) {
            return test.number == number;
        }
    }

    public static final class WorldTypeFilter implements Filter<World> {

        public final WorldType type;

        public WorldTypeFilter(WorldType type) {
            Objects.requireNonNull(type);
            this.type = type;
        }

        @Override
        public boolean matches(World test) {
            return test.type == type;
        }
    }

    public static final class WorldActivityFilter implements Filter<World> {

        public final String activity;

        public WorldActivityFilter(String activity) {
            Objects.requireNonNull(activity);
            this.activity = activity;
        }

        public WorldActivityFilter() {
            this("-");
        }

        @Override
        public boolean matches(World test) {
            return test.activity.equals(activity);
        }
    }

    public static final int WORLD_OFFSET = 300;
    private static final String WORLD_LIST_ADDRESS = "http://oldschool.runescape.com/slu";
    private static final String WORLD_ADDRESS_FORMAT = "http://oldschool%d.runescape.com/";
    private static final Pattern WORLD_LIST_PATTERN = Pattern.compile("<tr class='server-list.*?world=(\\d+).*?__row.*?(\\d+) players.*?__row.*?>(.+?)</td>.*?__row.*?>([a-z ]+)</td>.*?__row.*?>(.+?)</td>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    private WorldUtilities() {

    }

    public static String getAddress(int number) {
        return String.format(WORLD_ADDRESS_FORMAT, number > WORLD_OFFSET ? (number - WORLD_OFFSET) : number);
    }

    public static List<World> getWorlds() throws IOException {
        Matcher matcher = WORLD_LIST_PATTERN.matcher(WebUtilities.downloadPageSource(WORLD_LIST_ADDRESS));
        List<World> worlds = new ArrayList<>();
        while (matcher.find()) {
            worlds.add(new World(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    matcher.group(3),
                    WorldType.valueOf(matcher.group(4).toUpperCase()),
                    matcher.group(5)));
        }
        return worlds;
    }

    public static boolean exists(int number) throws IOException {
        return !FilterUtilities.filter(getWorlds(), new WorldNumberFilter(number <= WORLD_OFFSET ? (number + WORLD_OFFSET) : number)).isEmpty();
    }
}