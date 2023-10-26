package com.zenyte.game.util;

import com.google.common.base.Preconditions;
import com.zenyte.Game;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.pathfinding.Flags;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleLists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import mgi.Indice;
import mgi.types.config.GraphicsDefinitions;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import pl.allegro.finance.tradukisto.ValueConverters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URL;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public final class Utils {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Utils.class);
    public static final byte[] DIRECTION_DELTA_X = new byte[] {-1, 0, 1, -1, 1, -1, 0, 1};
    public static final byte[] DIRECTION_DELTA_Y = new byte[] {-1, -1, -1, 0, 0, 1, 1, 1};
    public static final byte[] NPC_DIRECTION_DELTA_X = new byte[] {-1, 0, 1, -1, 1, -1, 0, 1};
    public static final byte[] NPC_DIRECTION_DELTA_Y = new byte[] {1, 1, 1, 0, 0, -1, -1, -1};
    private static final String[] tensNames = {"", " ten", " twenty", " thirty", " forty", " fifty", " sixty", " seventy", " eighty", " ninety"};
    public static final String[] numNames = {"", " one", " two", " three", " four", " five", " six", " seven", " eight", " nine", " ten", " eleven", " twelve", " thirteen", " fourteen", " fifteen", " sixteen", " seventeen", " eighteen", " nineteen"};
    private static final int[] FONT_494_CHAR_WIDTHS = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 5, 9, 7, 11, 7, 4, 3, 3, 7, 7, 4, 5, 3, 4, 7, 4, 7, 6, 5, 6, 7, 6, 7, 7, 3, 4, 6, 6, 6, 6, 10, 7, 7, 7, 7, 5, 5, 7, 7, 3, 6, 6, 5, 8, 7, 7, 7, 7, 7, 7, 5, 7, 7, 9, 7, 5, 7, 4, 4, 4, 7, 7, 4, 6, 6, 5, 6, 6, 5, 6, 6, 3, 5, 5, 3, 7, 6, 6, 6, 6, 5, 6, 4, 6, 7, 7, 7, 6, 6, 3, 3, 3, 8, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 7, 7, 8, 7, 3, 8, 5, 10, 6, 7, 7, 4, 10, 4, 5, 7, 5, 5, 5, 6, 7, 3, 4, 4, 5, 7, 9, 9, 9, 6, 7, 7, 7, 7, 7, 7, 9, 7, 6, 6, 6, 6, 3, 3, 4, 3, 7, 7, 7, 7, 7, 7, 7, 6, 7, 7, 7, 7, 7, 7, 5, 7, 6, 6, 6, 6, 6, 6, 9, 5, 6, 6, 6, 6, 3, 3, 4, 3, 6, 6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 6, 6, 6, 5, 6};
    private static final Object ALGORITHM_LOCK = new Object();
    public static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Random r = new Random();

    public static final <T> T random(List<T> list) {
        final int size = list.size();
        return size < 1 ? null
                : size == 1 ? list.get(0)
                : list.get(randomNoPlus(size));
    }

    public static int randomNoPlus(final int i) {
        return randomNoPlus(ThreadLocalRandom.current(), i);
    }

    public static int randomNoPlus(final Random random, final int i) {
        return random.nextInt(i);
    }

    public static int random(final Random random, final int i) {
        return random.nextInt(i + 1);
    }

    public static int random(final int i) {
        return random(r, i);
    }

    public static final double randomDouble(final Random random) {
        return random.nextDouble();
    }

    public static final double randomDouble() {
        return randomDouble(r);
    }

    public static final boolean interpolateSuccess(final int minChance, final int maxChance, final int minLevel, final int maxLevel, final int currentLevel) {
        return Utils.random(255) <= ((minChance * (maxLevel - currentLevel) + maxChance * (currentLevel - minLevel)) / (maxLevel - minLevel));
    }

    /**
     * Generates a unique user id out of {@link System#currentTimeMillis()} & {@code seed},
     * where the seed is an integer consisting of up to 22 bits, and the timestamp will be a
     * long consisting of 42 bits.
     * <p>The random UUID generation method is valid until <b>Wed May 15 07:35:11 GMT 2109</b>,
     * which is when the 42 bits will finally be all used up.</p>
     * <p>
     * Used {@see https://discordapp.com/developers/docs/reference#snowflakes} as inspiration.
     *
     * @param seed the 22-bit integer seed used for unique id generation, to prevent generating
     *             same unique ids when the method is called in the same millisecond.
     * @return a unique user id.
     */
    public static final long generateSnowflake(final int seed) {
        return ((System.currentTimeMillis() & 4398046511103L) << 22) | (seed & 4194303);
    }

    /**
     * Gets the seed out of the snowflake UUID generated by {@link Utils#generateSnowflake(int)}.
     *
     * @param snowflake the snowflake UUID.
     * @return the 22-bit integer seed used for the UUID generation.
     */
    public static final int getSeed(final long snowflake) {
        return (int) (snowflake & 4194303);
    }

    /**
     * Gets the timestamp out of the snowflake UUID generated by {@link Utils#generateSnowflake(int)}.
     *
     * @param snowflake the snowflake UUID.
     * @return the 42-bit long seed used in the UUID generation, which is {@link System#currentTimeMillis()}.
     */
    public static final long getTimestamp(final long snowflake) {
        return (snowflake >> 22) & 4398046511103L;
    }

    public static byte charToByteCp1252(char var0) {
        byte var1;
        if (var0 > 0 && var0 < 128 || var0 >= 160 && var0 <= 255) {
            var1 = (byte) var0;
        } else if (var0 == 8364) {
            var1 = -128;
        } else if (var0 == 8218) {
            var1 = -126;
        } else if (var0 == 402) {
            var1 = -125;
        } else if (var0 == 8222) {
            var1 = -124;
        } else if (var0 == 8230) {
            var1 = -123;
        } else if (var0 == 8224) {
            var1 = -122;
        } else if (var0 == 8225) {
            var1 = -121;
        } else if (var0 == 710) {
            var1 = -120;
        } else if (var0 == 8240) {
            var1 = -119;
        } else if (var0 == 352) {
            var1 = -118;
        } else if (var0 == 8249) {
            var1 = -117;
        } else if (var0 == 338) {
            var1 = -116;
        } else if (var0 == 381) {
            var1 = -114;
        } else if (var0 == 8216) {
            var1 = -111;
        } else if (var0 == 8217) {
            var1 = -110;
        } else if (var0 == 8220) {
            var1 = -109;
        } else if (var0 == 8221) {
            var1 = -108;
        } else if (var0 == 8226) {
            var1 = -107;
        } else if (var0 == 8211) {
            var1 = -106;
        } else if (var0 == 8212) {
            var1 = -105;
        } else if (var0 == 732) {
            var1 = -104;
        } else if (var0 == 8482) {
            var1 = -103;
        } else if (var0 == 353) {
            var1 = -102;
        } else if (var0 == 8250) {
            var1 = -101;
        } else if (var0 == 339) {
            var1 = -100;
        } else if (var0 == 382) {
            var1 = -98;
        } else if (var0 == 376) {
            var1 = -97;
        } else {
            var1 = 63;
        }
        return var1;
    }

    public static final boolean collides(final Location first, final int firstSize, final Location second, final int secondSize, final int extraDistance) {
        final int distanceX = first.getX() - second.getX();
        final int distanceY = first.getY() - second.getY();
        final int size1 = firstSize + extraDistance;
        final int size2 = secondSize + extraDistance;
        return distanceX < (size2) && distanceX > (-size1) && distanceY < (size2) && distanceY > (-size1);
    }

    public static final Optional<Location> findEmptySquare(@NotNull final Location center, final int seekRadius, final int emptyRadius, final Optional<Predicate<Location>> optionalPredicate) {
        assert emptyRadius > 0;
        assert seekRadius >= emptyRadius;
        final java.util.function.Predicate<com.zenyte.game.world.entity.Location> predicate = optionalPredicate.orElse(null);
        final int px = center.getX();
        final int py = center.getY();
        final int plane = center.getPlane();
        int radius = 0;
        if (isSquareFree(px, py, plane, emptyRadius)) {
            final com.zenyte.game.world.entity.Location tile = new Location(px, py, plane);
            if (predicate == null || predicate.test(tile)) {
                return Optional.of(tile);
            }
        }
        while (++radius <= seekRadius) {
            for (int y = py - radius + 1; y < py + radius; y++) {
                if (!isSquareFree(px - radius, y, plane, emptyRadius)) {
                    continue;
                }
                final com.zenyte.game.world.entity.Location tile = new Location(px - radius, y, plane);
                if (predicate != null && !predicate.test(tile)) continue;
                return Optional.of(tile);
            }
            for (int x = px - radius; x < px + radius; x++) {
                if (!isSquareFree(x, py + radius, plane, emptyRadius)) {
                    continue;
                }
                final com.zenyte.game.world.entity.Location tile = new Location(x, py + radius, plane);
                if (predicate != null && !predicate.test(tile)) continue;
                return Optional.of(tile);
            }
            for (int y = py + radius; y >= py - radius; y--) {
                if (!isSquareFree(px + radius, y, plane, emptyRadius)) {
                    continue;
                }
                final com.zenyte.game.world.entity.Location tile = new Location(px + radius, y, plane);
                if (predicate != null && !predicate.test(tile)) continue;
                return Optional.of(tile);
            }
            for (int x = px + radius - 1; x >= px - radius; x--) {
                if (!isSquareFree(x, py - radius, plane, emptyRadius)) {
                    continue;
                }
                final com.zenyte.game.world.entity.Location tile = new Location(x, py - radius, plane);
                if (predicate != null && !predicate.test(tile)) continue;
                return Optional.of(tile);
            }
        }
        return Optional.empty();
    }

    public static final boolean isSquareFree(final int x, final int y, final int z, final int radius) {
        assert radius > 0;
        if (radius == 1) {
            return ((World.getMask(z, x, y) & (Flags.OBJECT | Flags.FLOOR | Flags.FLOOR_DECORATION)) == 0);
        }
        for (int a = 0; a < radius; a++) {
            final int mask = World.getMask(z, x, y + a);
            if ((mask & (Flags.OBJECT | Flags.FLOOR | Flags.FLOOR_DECORATION | Flags.WALL_EAST)) != 0) {
                return false;
            }
        }
        for (int a = 0; a < radius; a++) {
            final int mask = World.getMask(z, x + a, y);
            if ((mask & (Flags.OBJECT | Flags.FLOOR | Flags.FLOOR_DECORATION | Flags.WALL_NORTH)) != 0) {
                return false;
            }
        }
        for (int a = 0; a < radius; a++) {
            final int mask = World.getMask(z, (x + radius) - 1, y + a);
            if ((mask & (Flags.OBJECT | Flags.FLOOR | Flags.FLOOR_DECORATION | Flags.WALL_WEST)) != 0) {
                return false;
            }
        }
        for (int a = 0; a < radius; a++) {
            final int mask = World.getMask(z, x + a, (y + radius) - 1);
            if ((mask & (Flags.OBJECT | Flags.FLOOR | Flags.FLOOR_DECORATION | Flags.WALL_SOUTH)) != 0) {
                return false;
            }
        }
        for (int a = 1; a < (radius - 2); a++) {
            for (int b = 1; b < (radius - 2); b++) {
                final int mask = World.getMask(z, x + a, y + b);
                if ((mask & (Flags.OBJECT | Flags.FLOOR | Flags.FLOOR_DECORATION)) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    @SafeVarargs
    public static <T, V> Map<V, T> populateMap(final T[] array, final Map<V, T> map, final Function<T, V>... funs) {
        for (final T t : array) {
            for (final java.util.function.Function<T, V> fun : funs) {
                map.put(fun.apply(t), t);
            }
        }
        return map;
    }

    public static <T, V> Map<V, T> populateMap(final T[] array, final Map<V, T> map, final Function<T, V> func) {
        for (final T t : array) {
            map.put(func.apply(t), t);
        }
        return map;
    }

    public static <V, T> void populateObjectListMap(T[] array, Map<V, ObjectList<T>> map, Function<T, V> func) {
        final java.util.HashSet<V> keys = Arrays.stream(array).map(func).collect(HashSet<V>::new, HashSet::add, HashSet::addAll);
        keys.forEach(key -> {
            ObjectList<T> values = new ObjectArrayList<>();
            for (T value : array) {
                if (func.apply(value) == key) {
                    values.add(value);
                }
            }
            map.put(key, values);
        });
    }

    public static <T, V> Map<V, T> populateMap(final List<T> list, final Map<V, T> map, final Function<T, V> func) {
        for (final T t : list) {
            map.put(func.apply(t), t);
        }
        return map;
    }

    public static final <T> T random(final T[] array) {
        return array[random(array.length - 1)];
    }

    /**
     * Returns a string whose value is the concatenation of this
     * string repeated {@code count} times.
     * <p>
     * If this string is empty or count is zero then the empty
     * string is returned.
     *
     * @param count number of times to repeat
     * @return A string composed of this string repeated
     * {@code count} times or the empty string if this
     * string is empty or count is zero
     * @throws IllegalArgumentException if the {@code count} is
     *                                  negative.
     * @since 11
     */
    public static String repeatString(final String string, final int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count is negative: " + count);
        }
        final byte[] value = string.getBytes();
        if (count == 1) {
            return string;
        }
        final int len = value.length;
        if (len == 0 || count == 0) {
            return "";
        }
        if (len == 1) {
            final byte[] single = new byte[count];
            Arrays.fill(single, value[0]);
            return new String(single);
        }
        if (Integer.MAX_VALUE / count < len) {
            throw new OutOfMemoryError("Repeating " + len + " bytes String " + count + " times will produce a String exceeding maximum size.");
        }
        final int limit = len * count;
        final byte[] multiple = new byte[limit];
        System.arraycopy(value, 0, multiple, 0, len);
        int copied = len;
        for (; copied < limit - copied; copied <<= 1) {
            System.arraycopy(multiple, 0, multiple, copied, copied);
        }
        System.arraycopy(multiple, 0, multiple, copied, limit - copied);
        return new String(multiple);
    }

    public static int secureRandom(final int i) {
        return SECURE_RANDOM.nextInt(i + 1);
    }

    public static final BufferedWriter getBufferedWriter(final String name, final boolean append) throws IOException {
        return new BufferedWriter(new FileWriter(new File(name), append));
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static final <T, E> E fillCollection(final E collection, final T... values) {
        if (!(collection instanceof Collection)) {
            return collection;
        }
        try {
            for (final T value : values) {
                ((Collection<T>) collection).add(value);
            }
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
        return collection;
    }

    @SafeVarargs
    public static final <T> T getRandomElement(final T... elements) {
        if (elements.length == 0) {
            throw new IllegalStateException("Array cannot be empty!");
        }
        return elements[random(elements.length - 1)];
    }

    public static <T> ArrayList<T> getArrayList(final T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

    /**
     * Finds the first value in the array that matches the predicate. If none is found, returns null.
     *
     * @param array     the array to loop.
     * @param predicate the predicate to test against each value.
     * @return a matching value.
     */
    public static final <T> T findMatching(final T[] array, final Predicate<T> predicate) {
        return findMatching(array, predicate, null);
    }

    /**
     * Finds the first value in the array that matches the predicate. If none is found, returns the default value.
     *
     * @param array        the array to loop.
     * @param predicate    the predicate to test against each value.
     * @param defaultValue the default value to return if no value matches the predicate.
     * @return a matching value.
     */
    public static final <T> T findMatching(final T[] array, final Predicate<T> predicate, final T defaultValue) {
        for (int i = 0; i < array.length; i++) {
            final T object = array[i];
            if (predicate.test(object)) {
                return object;
            }
        }
        return defaultValue;
    }

    public static final <T> T findMatching(final Collection<T> list, final Predicate<T> predicate) {
        return findMatching(list, predicate, null);
    }

    public static final <T> T findMatching(final Collection<T> list, final Predicate<T> predicate, final T defaultValue) {
        for (final T object : list) {
            if (predicate.test(object)) {
                return object;
            }
        }
        return defaultValue;
    }

    public static final <T> T findMatching(final Set<T> list, final Predicate<T> predicate) {
        return findMatching(list, predicate, null);
    }

    public static final <T> T findMatching(final Set<T> list, final Predicate<T> predicate, final T defaultValue) {
        for (final T object : list) {
            if (predicate.test(object)) {
                return object;
            }
        }
        return defaultValue;
    }

    public static final String plural(final int value) {
        return value == 1 ? "" : "s";
    }

    @NotNull
    public static <E> E getRandomCollectionElement(final Collection<E> e) {
        final int size = e.size();
        if (size == 0) {
            throw new RuntimeException("Collection cannot be empty.");
        }
        final int random = Utils.random(e.size() - 1);
        int i = 0;
        for (final E value : e) {
            if (i == random) {
                return value;
            }
            i++;
        }
        throw new RuntimeException("Concurrent modification performed on the collection.");
    }

    public static final <T> T getOrDefault(final T primary, final T defaultValue) {
        return primary == null ? defaultValue : primary;
    }

    public static final <T> T computeIfAbsent(final T primary, final Function<Void, T> defaultValue) {
        return primary == null ? defaultValue.apply(null) : primary;
    }

    /**
     * Returns a random reference of the given chance. If the random call value
     * is equal to or less than the input percentage chance, method returns
     * true.
     *
     * @param chance
     *            the percentage chance to roll against.
     * @return whether the random roll is true or false.
     */
    public static final boolean percentage(final int chance) {
        return random(99) < chance;
    }

    public static final void printGraphicsUsingSameAnimation(final int graphics) {
        final int animation = GraphicsDefinitions.get(graphics).getAnimationId();
        for (int i = 0; i < Utils.getIndiceSize(Indice.GRAPHICS_DEFINITIONS); i++) {
            final mgi.types.config.GraphicsDefinitions definitions = GraphicsDefinitions.get(i);
            if (definitions == null) {
                continue;
            }
            if (definitions.getAnimationId() == animation) {
                System.err.println("Graphics: " + i);
            }
        }
    }

    private static final String VOWELS = "aeiouAEIOU";

    public static final boolean startWithVowel(final String string) {
        return VOWELS.indexOf(string.charAt(0)) != -1;
    }

    /**
     * Gets the hex colour code for the level. Method is taken from the client
     * for displaying the combat level of other players.
     *
     * @param level
     *            player's level.
     * @param otherLevel
     *            other player's level
     * @return hex colour prefix.
     */
    public static final String getLevelColour(final int level, final int otherLevel) {
        final int difference = level - otherLevel;
        if (difference <= -1) {
            return "<col=ff0000>";
        }
        if (difference > 0) {
            return "<col=40ff00>";
        }
        if (difference == 1) {
            return "<col=ff7000>";
        }
        return "<col=ffffff>";
    }

    /**
     * Gets the hex colour code for the level.
     * Method is taken from the client for displaying the combat
     * level of other players.
     *
     * @param level      player's level.
     * @param otherLevel other player's level
     * @return hex colour prefix.
     */
    public static final String getPreciseLevelColour(final int level, final int otherLevel) {
        final int difference = level - otherLevel;
        if (difference < -9) return getColourPrefix(16711680);
        else if (difference < -6) return getColourPrefix(16723968);
        else if (difference < -3) return getColourPrefix(16740352);
        else if (difference < 0) return getColourPrefix(16756736);
        else if (difference > 9) return getColourPrefix(65280);
        else if (difference > 6) return getColourPrefix(4259584);
        else if (difference > 3) return getColourPrefix(8453888);
        else if (difference > 0) return getColourPrefix(12648192);
        return getColourPrefix(16776960);
    }

    /**
     * Gets the hex colour from a decimal value.
     *
     * @param value decimal value of the hex colour.
     * @return hex value of the colour along with colour tags.
     */
    private static final String getColourPrefix(final int value) {
        return "<col=" + Integer.toHexString(value) + ">";
    }

    /**
     * Constructs an arraylist of tiles between two points. x1/y1 -> Starting
     * location. x2/y2 -> End location. Draws as straight of a line as possible
     * between the given locations
     */
    public static final List<Location> calculateLine(int x1, int y1, final int x2, final int y2, final int plane) {
        final List<Location> tiles = new ArrayList<Location>();
        final int dx = Math.abs(x2 - x1);
        final int dy = Math.abs(y2 - y1);
        final int sx = (x1 < x2) ? 1 : -1;
        final int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;
        while (true) {
            tiles.add(new Location(x1, y1, plane));
            if (x1 == x2 && y1 == y2) {
                break;
            }
            final int e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x1 = x1 + sx;
            }
            if (e2 < dx) {
                err = err + dx;
                y1 = y1 + sy;
            }
        }
        return tiles;
    }

    public static final IntArrayList calculateLineHash(final IntArrayList tiles, int x1, int y1, final int x2, final int y2, final int plane) {
        if (!tiles.isEmpty()) {
            tiles.clear();
        }
        final int dx = Math.abs(x2 - x1);
        final int dy = Math.abs(y2 - y1);
        final int sx = (x1 < x2) ? 1 : -1;
        final int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;
        while (true) {
            tiles.add(Location.getHash(x1, y1, plane));
            if (x1 == x2 && y1 == y2) {
                break;
            }
            final int e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x1 = x1 + sx;
            }
            if (e2 < dx) {
                err = err + dx;
                y1 = y1 + sy;
            }
        }
        return tiles;
    }

    /**
     * Used for sanitising wikipedia values.
     */
    public static final String sanitiseValue(final String input) {
        String output = input;
        output = output.replace("coins", "");
        output = output.replace("coin", "");
        output = output.replace(",", "");
        output = output.replace("~", "");
        output = output.replace("(melee)", "");
        output = output.replace("(range)", "");
        output = output.replace("(mage)", "");
        output = output.replace("(magic)", "");
        output = output.replace("(Melee)", "");
        output = output.replace("(Range)", "");
        output = output.replace("(Mage)", "");
        output = output.replace("(Magic)", "");
        output = output.replace("kg", "");
        output = output.replace("Not sold", "-1");
        output = output.replace("Yes", "true");
        output = output.replace("No", "false");
        output = output.replace("(info)", "");
        output = output.replace("reward points (Nightmare Zone)", "");
        output = output.replace("%", "");
        output = output.replace(" ", "");
        output = output.replace("(approx)", "");
        output = output.replace("•", "");
        output = output.replace("(during quest)", "");
        output = output.trim();
        return output;
    }

    public static final byte[] encryptUsingMD5(final byte[] buffer) {
        synchronized (ALGORITHM_LOCK) {
            try {
                final MessageDigest algorithm = MessageDigest.getInstance("MD5");
                algorithm.update(buffer);
                final byte[] digest = algorithm.digest();
                algorithm.reset();
                return digest;
            } catch (final Throwable e) {
                log.error(Strings.EMPTY, e);
            }
            return null;
        }
    }

    private static final Object DEFAULT = new Object();

    public static final List<String> printFields(final Object object) {
        final List<String> strings = new ArrayList<String>(object.getClass().getDeclaredFields().length);
        for (final Field field : object.getClass().getDeclaredFields()) {
            if ((field.getModifiers() & 8) != 0) {
                continue;
            }
            try {
                final Object val = getValue(object, field);
                if (val == DEFAULT) {
                    continue;
                }
                final String[] fieldName = field.getName().split("(?=[A-Z])");
                final StringBuilder fieldBuilder = new StringBuilder();
                fieldBuilder.append(Utils.formatString(fieldName[0]));
                for (int i = 1; i < fieldName.length; i++) {
                    fieldBuilder.append(" " + (fieldName[i].length() == 1 ? fieldName[i].toUpperCase() : fieldName[i].toLowerCase()));
                }
                strings.add(fieldBuilder.toString() + ": " + val);
                System.out.println(fieldBuilder.toString() + ": " + val);
            } catch (final Throwable e) {
                log.error(Strings.EMPTY, e);
            }
        }
        return strings;
    }

    public static DoubleLists.UnmodifiableList linearInterpolate(final double x0, final double y0, final double x1, final double y1, final IntStream inputs) {
        return linearInterpolate(x0, y0, x1, y1, inputs.asDoubleStream().toArray());
    }

    /**
     * Gets the interpolated y value of an x coordinate given two or more known points.
     * For e.g. Blackjack success rates linearly increase starting at a 31% chance at level 1 to a 94% chance at level 99.
     * Our X values are our levels and our Y values are our chance as percentage (x0 = 1, y0 = 31, x1 = 99, y1 = 94).
     * We can then use this to find out percentage at other levels.
     * @param inputs the x values we wish to find y values for.
     * @return The interpolated values for the x coordinates given as input.
     */
    public static DoubleLists.UnmodifiableList linearInterpolate(final double x0, final double y0, final double x1, final double y1, final double... inputs) {
        Preconditions.checkArgument(x1 - x0 != 0, "Can not interpolate value for vertical line. Ensure that both x coordinates are not equal.");
        final it.unimi.dsi.fastutil.doubles.DoubleArrayList list = new DoubleArrayList();
        for (double input : inputs) {
            list.add(linearInterpolate(x0, y0, x1, y1, input));
        }
        return (DoubleLists.UnmodifiableList) DoubleLists.unmodifiable(list);
    }

    public static double linearInterpolate(final double x0, final double y0, final double x1, final double y1, final double input) {
        Preconditions.checkArgument(x1 - x0 != 0, "Can not interpolate value for vertical line. Ensure that both x coordinates are not equal.");
        return y0 + (input - x0) * ((y1 - y0) / (x1 - x0));
    }

    public static final String formatTime(final long minutes, final long seconds) {
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    private static final Object getValue(final Object object, final Field field) throws Throwable {
        field.setAccessible(true);
        final Class<?> type = field.getType();
        if (field.get(object) == null) {
            return DEFAULT;
        }
        if (type == boolean.class && (boolean) (field.get(object)) == false) {
            return DEFAULT;
        }
        if (type == int.class && (int) (field.get(object)) == 0) {
            return DEFAULT;
        }
        if (type == int[][].class) {
            return Arrays.toString((int[][]) field.get(object));
        } else if (type == int[].class) {
            return Arrays.toString((int[]) field.get(object));
        } else if (type == byte[].class) {
            return Arrays.toString((byte[]) field.get(object));
        } else if (type == short[].class) {
            return Arrays.toString((short[]) field.get(object));
        } else if (type == double[].class) {
            return Arrays.toString((double[]) field.get(object));
        } else if (type == float[].class) {
            return Arrays.toString((float[]) field.get(object));
        } else if (type == String[].class) {
            if (field.get(object) == null) {
                return "null";
            }
            return "[" + String.join(", ", (String[]) field.get(object)) + "]";
        } else if (type == Object[].class) {
            return Arrays.toString((Object[]) field.get(object));
        }
        return field.get(object);
    }

    public static final double getRandomDouble(final double maxValue) {
        return (randomDouble() * maxValue);
    }

    public static final double getRandomDouble(final int minValue, final int maxValue) {
        final int random = random(minValue, maxValue - 1);
        final double randomDouble = r.nextDouble();
        return random + randomDouble;
    }

    public static final double getRandomDouble(final double minValue, final double maxValue) {
        final int random = random((int) minValue, (int) maxValue - 1);
        final double randomDouble = r.nextDouble();
        double value = random + randomDouble;
        if (value > maxValue) value = maxValue;
        if (value < minValue) value = minValue;
        return value;
    }

    public static final int random(final Random random, final int min, final int max) {
        final int n = Math.abs(max - min);
        return Math.min(min, max) + (n == 0 ? 0 : random(random, n));
    }

    public static final int random(final int min, final int max) {
        return random(r, min, max);
    }

    public static final Random getRandom() {
        return r;
    }

    public static final String checkPlural(final String input) {
        return (input.endsWith("s") && !input.toLowerCase().equals("bass") ? "some " : getAOrAn(input) + " ") + input;
    }

    public static final int getShiftedValue(final int total, final int id) {
        final int val = ((total >> id) & 1);
        if (val == 1) {
            return total & (1 << id ^ -1);
        }
        return total | 1 << id;
    }

    public static final int getShiftedValue(final int total, final int id, final boolean toggled) {
        final int val = ((total >> id) & 1);
        if (val == 1 && !toggled) {
            return total & (1 << id ^ -1);
        } else if (val == 0 && toggled) {
            return total | 1 << id;
        }
        return total;
    }

    public static final boolean getShiftedBoolean(final int total, final int id) {
        return ((total >> id) & 1) == 1;
    }

    public static final int getDistance(final int coordX1, final int coordY1, final int coordX2, final int coordY2) {
        final int deltaX = coordX2 - coordX1;
        final int deltaY = coordY2 - coordY1;
        return ((int) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)));
    }

    public static final int getFaceDirection(final Location destination, final Location location) {
        return ((int) ((Math.atan2(-(destination.getX() - location.getX()), -(destination.getY() - location.getY())) * 325.949)) & 2047);
    }

    public static final int getFaceDirection(final double xOffset, final double yOffset) {
        return ((int) ((Math.atan2(-xOffset, -yOffset) * 325.949)) & 2047);
    }

    public static int getMapArchiveId(final int regionX, final int regionY) {
        return regionX | regionY << 7;
    }

    public static final int getMoveDirection(final int xOffset, final int yOffset) {
        if (xOffset < 0) {
            if (yOffset < 0) {
                return 0;
            } else if (yOffset > 0) {
                return 5;
            } else {
                return 3;
            }
        } else if (xOffset > 0) {
            if (yOffset < 0) {
                return 2;
            } else if (yOffset > 0) {
                return 7;
            } else {
                return 4;
            }
        } else {
            if (yOffset < 0) {
                return 1;
            } else if (yOffset > 0) {
                return 6;
            } else {
                return -1;
            }
        }
    }

    public static final int getNPCWalkingDirection(final int dir) {
        final int dx = Utils.NPC_DIRECTION_DELTA_X[dir];
        final int dy = Utils.NPC_DIRECTION_DELTA_Y[dir];
        if (dx == 0 && dy == 1) {
            return 6;
        } else if (dx == 1 && dy == 1) {
            return 7;
        } else if (dx == -1 && dy == 0) {
            return 3;
        } else if (dx == 1 && dy == 0) {
            return 4;
        } else if (dx == -1 && dy == -1) {
            return 0;
        } else if (dx == 0 && dy == -1) {
            return 1;
        } else if (dx == 1 && dy == -1) {
            return 2;
        } else {
            return 5;
        }
    }

    public static int getPlayerRunningDirection(final int dx, final int dy) {
        if (dx == -2 && dy == -2) {
            return 0;
        }
        if (dx == -1 && dy == -2) {
            return 1;
        }
        if (dx == 0 && dy == -2) {
            return 2;
        }
        if (dx == 1 && dy == -2) {
            return 3;
        }
        if (dx == 2 && dy == -2) {
            return 4;
        }
        if (dx == -2 && dy == -1) {
            return 5;
        }
        if (dx == 2 && dy == -1) {
            return 6;
        }
        if (dx == -2 && dy == 0) {
            return 7;
        }
        if (dx == 2 && dy == 0) {
            return 8;
        }
        if (dx == -2 && dy == 1) {
            return 9;
        }
        if (dx == 2 && dy == 1) {
            return 10;
        }
        if (dx == -2 && dy == 2) {
            return 11;
        }
        if (dx == -1 && dy == 2) {
            return 12;
        }
        if (dx == 0 && dy == 2) {
            return 13;
        }
        if (dx == 1 && dy == 2) {
            return 14;
        }
        if (dx == 2 && dy == 2) {
            return 15;
        }
        return -1;
    }

    public static byte[] cryptRSA(final byte[] data, final BigInteger exponent, final BigInteger modulus) {
        return new BigInteger(data).modPow(exponent, modulus).toByteArray();
    }

    public static int getPlayerWalkingDirection(final int dx, final int dy) {
        if (dx == -1 && dy == -1) {
            return 0;
        }
        if (dx == 0 && dy == -1) {
            return 1;
        }
        if (dx == 1 && dy == -1) {
            return 2;
        }
        if (dx == -1 && dy == 0) {
            return 3;
        }
        if (dx == 1 && dy == 0) {
            return 4;
        }
        if (dx == -1 && dy == 1) {
            return 5;
        }
        if (dx == 0 && dy == 1) {
            return 6;
        }
        if (dx == 1 && dy == 1) {
            return 7;
        }
        return -1;
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static final boolean isOnRange(final int x1, final int y1, final int size1, final int x2, final int y2, final int size2, final int maxDistance) {
        final int distanceX = x1 - x2;
        final int distanceY = y1 - y2;
        return distanceX <= size2 + maxDistance && distanceX >= -size1 - maxDistance && distanceY <= size2 + maxDistance && distanceY >= -size1 - maxDistance;
    }

    public static final boolean isDiagonal(final int x1, final int y1, final int size1, final int x2, final int y2, final int size2) {
        // if (px == x - 1 && py == y - 1 || px == x + 4 && py == y - 1
        // || px == x + 4 && py == y + 4 || px == x - 1 && py == y + 4) {
        return x1 == (x2 - size1) && y1 == (y2 - size1) || x1 == (x2 + size2) && y1 == (y2 - size1) || x1 == (x2 + size2) && y1 == (y2 + size2) || x1 == (x2 - size1) && y1 == (y2 + size2);
        // if (distanceX > size2|| distanceX < -size1 || distanceY > size2 ||
        // distanceY < -size1) {
        // return false;
        // }
        // return true;
    }

    public static final boolean isOnRangeExcludingDiagonal(final int x1, final int y1, final int size1, final int x2, final int y2, final int size2, final int maxDistance) {
        final int distanceX = x1 - x2;
        final int distanceY = y1 - y2;
        return distanceX < size2 + maxDistance && distanceX > -size1 - maxDistance && distanceY < size2 + maxDistance && distanceY > -size1 - maxDistance;
    }

    public static Class<?>[] getClasses(final String packageName) throws ClassNotFoundException, IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final String path = packageName.replace('.', '/');
        final Enumeration<URL> resources = classLoader.getResources(path);
        final List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            dirs.add(new File(resources.nextElement().getFile().replaceAll("%20", " ")));
        }
        final List<Class<?>> classes = new ArrayList<Class<?>>();
        for (int i = dirs.size() - 1; i >= 0; i--) {
            final File directory = dirs.get(i);
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    public static final List<Class<?>> findClasses(final File directory, final String packageName) {
        final List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        final File[] files = directory.listFiles();
        for (int i = files.length - 1; i >= 0; i--) {
            final File file = files[i];
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
                continue;
            }
            final String name = file.getName();
            if (name.endsWith(".class")) {
                try {
                    classes.add(Class.forName(packageName + '.' + name.substring(0, name.length() - 6)));
                } catch (final ClassNotFoundException e) {
                    log.error(Strings.EMPTY, e);
                }
            }
        }
        return classes;
    }

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(".###");

    /**
     * Converts nanoseconds to milliseconds, keeping three numbers past decimal
     * point.
     *
     * @param nanoseconds the value in nanoseconds
     * @return the value in milliseconds.
     */
    public static final double nanoToMilli(final long nanoseconds) {
        final double num = nanoseconds / 1000000.0F;
        return Double.parseDouble(DECIMAL_FORMAT.format(num));
    }

    public static String formatUsername(final String username) {
        return username.toLowerCase().replace(" ", "_");
    }

    public static String formatString(String str) {
        if (str == null || str.isEmpty()) return str;
        char[] array = str.toLowerCase().toCharArray();
        // Uppercase first letter.
        array[0] = Character.toUpperCase(array[0]);
        if (array[0] == '_') {
            array[0] = ' ';
        }
        // Uppercase all letters that follow a whitespace character.
        for (int i = 1; i < array.length; i++) {
            if (Character.isWhitespace(array[i - 1])) {
                array[i] = Character.toUpperCase(array[i]);
            } else if (array[i - 1] == '_') {
                array[i - 1] = ' ';
            }
        }
        return new String(array);
    }

    /**
     * <<<<<<< HEAD
     * Converts a string to a name format, therefore clearing it of all the
     * invalid characters. The only characters permitted are a-z, A-Z, 0-9, _
     * =======
     * Converts a string to a name format, therefore clearing it of all the invalid characters. The only characters permitted are a-z, A-Z,
     * 0-9, _
     * >>>>>>> 391d69086a88d0b94a9f1f6932ab25fb5ac9115b
     *
     * @param string the string to convert.
     * @return a suitable string.
     */
    public static final String convertToNameFormat(@NotNull final String string) {
        if (string == null) {
            throw new NullPointerException("string is marked non-null but is null");
        }
        final java.lang.StringBuilder builder = new StringBuilder();
        for (final char c : string.toCharArray()) {
            if (Character.isLetter(c) || Character.isDigit(c) || c == '_' || c == ' ') {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public static final int getAngle(final int xOffset, final int yOffset) {
        return ((int) (Math.atan2(-xOffset, -yOffset) * 2607.5945876176133)) & 16383;
    }

    public static final int getAngle(final Location from, final Location target) {
        double angle = Math.toDegrees(Math.atan2(target.getY() - from.getY(), -(target.getX() - from.getX())));
        if (angle < 0) {
            angle += 360;
        }
        return (int) angle;
    }

    public static final boolean collides(final int x1, final int y1, final int size1, final int x2, final int y2, final int size2) {
        final int distanceX = x1 - x2;
        final int distanceY = y1 - y2;
        return distanceX < size2 && distanceX > -size1 && distanceY < size2 && distanceY > -size1;
    }

    public static final int[][] getCoordOffsetsNear(final int size) {
        final int[] xs = new int[4 + (4 * size)];
        final int[] xy = new int[xs.length];
        xs[0] = -size;
        xy[0] = 1;
        xs[1] = 1;
        xy[1] = 1;
        xs[2] = -size;
        xy[2] = -size;
        xs[3] = 1;
        xy[2] = -size;
        for (int fakeSize = size; fakeSize > 0; fakeSize--) {
            xs[(4 + ((size - fakeSize) * 4))] = -fakeSize + 1;
            xy[(4 + ((size - fakeSize) * 4))] = 1;
            xs[(4 + ((size - fakeSize) * 4)) + 1] = -size;
            xy[(4 + ((size - fakeSize) * 4)) + 1] = -fakeSize + 1;
            xs[(4 + ((size - fakeSize) * 4)) + 2] = 1;
            xy[(4 + ((size - fakeSize) * 4)) + 2] = -fakeSize + 1;
            xs[(4 + ((size - fakeSize) * 4)) + 3] = -fakeSize + 1;
            xy[(4 + ((size - fakeSize) * 4)) + 3] = -size;
        }
        return new int[][] {xs, xy};
    }

    public static final void shuffleIntegerArray(final int[] array) {
        final Random rnd = ThreadLocalRandom.current();
        for (int i = array.length - 1; i > 0; i--) {
            final int index = rnd.nextInt(i + 1);
            final int a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

    public static String getFormattedNumber(final double amount, final char seperator) {
        final String str = new DecimalFormat("#,###,###").format(amount);
        final char[] rebuff = new char[str.length()];
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            if (c >= '0' && c <= '9') {
                rebuff[i] = c;
            } else {
                rebuff[i] = seperator;
            }
        }
        return new String(rebuff);
    }

    public static String asWords(final int number) {
        return ValueConverters.ENGLISH_INTEGER.asWords(number);
    }

    public static String format(final int number) {
        return NumberFormat.getNumberInstance().format(number);
    }

    public static String format(final long number) {
        return NumberFormat.getNumberInstance().format(number);
    }

    public static String format(final float number) {
        return NumberFormat.getNumberInstance().format(number);
    }

    public static Item[] concatenate(final Item[]... arrays) {
        final java.util.ArrayList<com.zenyte.game.item.Item> list = new ArrayList<Item>();
        for (final com.zenyte.game.item.Item[] array : arrays) {
            Arrays.stream(array).filter(Objects::nonNull).forEach(list::add);
        }
        final Item[] items = new Item[list.size()];
        return list.toArray(items);
    }

    public static final <T> Collection<T> concatenate(final T... elements) {
        return new LinkedList<T>(Arrays.asList(elements));
    }

    public static Item[] concatenate(final Collection<Item> arg0, final Collection<Item> arg1) {
        final ArrayList<Item> list = new ArrayList<Item>();
        for (final Item item : arg0) {
            list.add(item);
        }
        for (final Item item : arg1) {
            list.add(item);
        }
        final Item[] items = new Item[list.size()];
        return list.toArray(items);
    }

    public static boolean equals(final Object a, final Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        try {
            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } catch (final NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Gets the size of the indice.
     *
     * @param indice indice which to retrieve.
     * @return amount of archives/files (depending on indice type).
     */
    public static int getIndiceSize(final Indice indice, final int archiveId) {
        final mgi.tools.jagcached.cache.Cache cache = Game.getCacheMgi();
        if (indice.getArchive() == -1 && archiveId == -1) {
            return cache.getArchive(indice.getIndice()).groupCount();
        }
        return cache.getArchive(indice.getIndice()).findGroupByID(archiveId == -1 ? indice.getArchive() : archiveId).fileCount();
    }

    public static int getIndiceSize(final Indice indice) {
        return getIndiceSize(indice, -1);
    }

    private static String convertLessThanOneThousand(int number) {
        String soFar;
        if (number % 100 < 20) {
            soFar = numNames[number % 100];
            number /= 100;
        } else {
            soFar = numNames[number % 10];
            number /= 10;
            soFar = tensNames[number % 10] + soFar;
            number /= 10;
        }
        if (number == 0) {
            return soFar;
        }
        return numNames[number] + " hundred" + soFar;
    }

    public static String convert(final long number) {
        // 0 to 999 999 999 999
        if (number == 0) {
            return "zero";
        }
        String snumber = Long.toString(number);
        // pad with "0"
        final String mask = "000000000000";
        final DecimalFormat df = new DecimalFormat(mask);
        snumber = df.format(number);
        // XXXnnnnnnnnn
        final int billions = Integer.parseInt(snumber.substring(0, 3));
        // nnnXXXnnnnnn
        final int millions = Integer.parseInt(snumber.substring(3, 6));
        // nnnnnnXXXnnn
        final int hundredThousands = Integer.parseInt(snumber.substring(6, 9));
        // nnnnnnnnnXXX
        final int thousands = Integer.parseInt(snumber.substring(9, 12));
        String tradBillions;
        switch (billions) {
            case 0:
                tradBillions = "";
                break;
            case 1:
                tradBillions = convertLessThanOneThousand(billions) + " billion ";
                break;
            default:
                tradBillions = convertLessThanOneThousand(billions) + " billion ";
        }
        String result = tradBillions;
        String tradMillions;
        switch (millions) {
            case 0:
                tradMillions = "";
                break;
            case 1:
                tradMillions = convertLessThanOneThousand(millions) + " million ";
                break;
            default:
                tradMillions = convertLessThanOneThousand(millions) + " million ";
        }
        result = result + tradMillions;
        String tradHundredThousands;
        switch (hundredThousands) {
            case 0:
                tradHundredThousands = "";
                break;
            case 1:
                tradHundredThousands = "one thousand ";
                break;
            default:
                tradHundredThousands = convertLessThanOneThousand(hundredThousands) + " thousand ";
        }
        result = result + tradHundredThousands;
        String tradThousand;
        tradThousand = convertLessThanOneThousand(thousands);
        result = result + tradThousand;
        // remove extra spaces!
        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }

    public static String suffixOrdinal(final int i) {
        final String[] sufixes = new String[] {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];
        }
    }

    public static String getAOrAn(final String s) {
        if (s == null) {
            return null;
        }
        if (s.length() == 0) {
            return "";
        }
        final char fc = Character.toLowerCase(s.charAt(0));
        if (fc == 'a' || fc == 'e' || fc == 'i' || fc == 'u' || fc == 'o') {
            return "an";
        } else {
            final char sc = (s.length() > 1) ? s.charAt(1) : '\000';
            if (fc == 'x' && !(sc == 'a' || sc == 'e' || sc == 'i' || sc == 'o' || sc == 'u')) {
                return "an";
            } else {
                return "a";
            }
        }
    }

    public static int rgbToHSL16(int red, int green, int blue) {
        final int rgb = ((red & 255) << 16) | ((green & 255) << 8) | (blue & 255);
        return rgbToHSL16(rgb);
    }

    public static int rgbToHSL16(int rgb) {
        double r = (double) (rgb >> 16 & 255) / 256.0;
        double g = (double) (rgb >> 8 & 255) / 256.0;
        double b = (double) (rgb & 255) / 256.0;
        double min = r;
        if (g < min) {
            min = g;
        }
        if (b < min) {
            min = b;
        }
        double d_13_ = r;
        if (g > d_13_) {
            d_13_ = g;
        }
        if (b > d_13_) {
            d_13_ = b;
        }
        double d_14_ = 0.0;
        double d_15_ = 0.0;
        double d_16_ = (min + d_13_) / 2.0;
        if (min != d_13_) {
            if (d_16_ < 0.5) {
                d_15_ = (d_13_ - min) / (d_13_ + min);
            }
            if (d_16_ >= 0.5) {
                d_15_ = (d_13_ - min) / (2.0 - d_13_ - min);
            }
            if (r == d_13_) {
                d_14_ = (g - b) / (d_13_ - min);
            } else if (d_13_ == g) {
                d_14_ = 2.0 + (b - r) / (d_13_ - min);
            } else if (d_13_ == b) {
                d_14_ = 4.0 + (r - g) / (d_13_ - min);
            }
        }
        d_14_ /= 6.0;
        int h = (int) (256.0 * d_14_);
        int s = (int) (256.0 * d_15_);
        int l = (int) (256.0 * d_16_);
        if (s < 0) {
            s = 0;
        } else if (s > 255) {
            s = 255;
        }
        if (l < 0) {
            l = 0;
        } else if (l > 255) {
            l = 255;
        }
        if (l > 243) {
            s >>= 4;
        } else if (l > 217) {
            s >>= 3;
        } else if (l > 192) {
            s >>= 2;
        } else if (l > 179) {
            s >>= 1;
        }
        return ((l >> 1) + (((h & 255) >> 2 << 10) + (s >> 5 << 7)));
    }

    public static int getTextWidth(final int font, final String text) {
        if (font != 494) {
            return -1;
        }
        if (text == null) {
            return 0;
        } else {
            int var2 = -1;
            int var3 = -1;
            int var4 = 0;
            for (int var5 = 0; var5 < text.length(); ++var5) {
                char var6 = text.charAt(var5);
                if (var6 == '<') {
                    var2 = var5;
                } else {
                    if (var6 == '>' && var2 != -1) {
                        String var7 = text.substring(var2 + 1, var5);
                        var2 = -1;
                        if (var7.equals("lt")) {
                            var6 = '<';
                        } else {
                            if (!var7.equals("gt")) {
                                if (var7.startsWith("img=")) {
                                    try {
                                        int var8 = parseInt(var7.substring(4), 10, true);
                                        //var4 += CROWNS.getSprites().get(var8).getOriginalWidth();
                                        var3 = -1;
                                    } catch (Exception var10) {
                                    }
                                }
                                continue;
                            }
                            var6 = '>';
                        }
                    }
                    if (var6 == 160) {
                        var6 = ' ';
                    }
                    if (var2 == -1) {
                        var4 += FONT_494_CHAR_WIDTHS[(char) (charToByteCp1252(var6) & 255)];
						/*if (this.field3765 != null && var3 != -1) {
							var4 += this.field3765[var6 + (var3 << 8)];
						}*/
                        var3 = var6;
                    }
                }
            }
            return var4;
        }
    }

    public static int parseInt(CharSequence var0, int var1, boolean var2) {
        if (var1 >= 2 && var1 <= 36) {
            boolean var3 = false;
            boolean var4 = false;
            int var5 = 0;
            int var6 = var0.length();
            for (int var7 = 0; var7 < var6; ++var7) {
                char var8 = var0.charAt(var7);
                if (var7 == 0) {
                    if (var8 == '-') {
                        var3 = true;
                        continue;
                    }
                    if (var8 == '+') {
                        continue;
                    }
                }
                int var10;
                if (var8 >= '0' && var8 <= '9') {
                    var10 = var8 - '0';
                } else if (var8 >= 'A' && var8 <= 'Z') {
                    var10 = var8 - '7';
                } else {
                    if (var8 < 'a' || var8 > 'z') {
                        throw new NumberFormatException();
                    }
                    var10 = var8 - 'W';
                }
                if (var10 >= var1) {
                    throw new NumberFormatException();
                }
                if (var3) {
                    var10 = -var10;
                }
                int var9 = var10 + var5 * var1;
                if (var9 / var1 != var5) {
                    throw new NumberFormatException();
                }
                var5 = var9;
                var4 = true;
            }
            if (!var4) {
                throw new NumberFormatException();
            } else {
                return var5;
            }
        } else {
            throw new IllegalArgumentException("");
        }
    }
}