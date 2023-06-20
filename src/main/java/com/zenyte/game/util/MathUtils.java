package com.zenyte.game.util;

import java.security.SecureRandom;
import java.util.Random;

public class MathUtils {

    public static final byte[] DIRECTION_DELTA_X = new byte[]{-1, 0, 1, -1, 1, -1, 0, 1};
    public static final byte[] DIRECTION_DELTA_Y = new byte[]{1, 1, 1, 0, 0, -1, -1, -1};

    private static final long INIT_MILLIS = System.currentTimeMillis();
    private static final long INIT_NANOS = System.nanoTime();

    private static long millisSinceClassInit() {
        return (System.nanoTime() - INIT_NANOS) / 1_000_000;
    }

    public static long currentTimeMillis() {
        return INIT_MILLIS + millisSinceClassInit();
    }

    public static final int getFaceDirection(final int xOffset, final int yOffset) {
        return ((int) (Math.atan2(-xOffset, -yOffset) * 2607.5945876176133)) & 0x3fff;
    }

    /**
     * Distance formula used to calculate the distance betwen two entities.
     *
     * @param sx
     * @param sy
     * @param dx
     * @param dy
     * @return
     */
    public static int distance(final int sx, final int sy, final int dx, final int dy) {
        final int deltaX = sx - dx;
        final int deltaY = sy - dy;
        return Math.abs(deltaX) + Math.abs(deltaY);
    }

    /**
     * A random instance served as a utility.
     */
    public static final Random random = new Random();

    /**
     * Gets a gaussian distributed randomized value between 0 and the
     * {@code maximum} value. <br>
     * The mean (average) is maximum / 2.
     *
     * @param meanModifier The modifier used to determine the mean.
     * @param r            The random instance.
     * @param maximum      The maximum value.
     * @return The randomized value.
     */
    public static double getGaussian(final double meanModifier, final double maximum) {
        final Random random = new SecureRandom();
        final double mean = maximum * meanModifier;
        final double deviation = mean * 1.79;
        double value = 0;
        do {
            value = Math.floor(mean + random.nextGaussian() * deviation);
        } while (value < 0 || value > maximum);
        return value;
    }

    /**
     * Returns a random integer with min as the inclusive lower bound and max as
     * the exclusive upper bound.
     *
     * @param min The inclusive lower bound.
     * @param max The exclusive upper bound.
     * @return Random integer min <= n < max.
     */
    public static int random(final int min, final int max) {
        final int n = Math.abs(max - min);
        return Math.min(min, max) + (n == 0 ? 0 : random.nextInt(n));
    }

    /**
     * Returns a random value between 1 and the specified range.
     *
     * @param range
     * @return
     */
    public static int random(final int range) {
        SecureRandom rnd = new SecureRandom();
        final int ran = rnd.nextInt(range + 1);
        rnd = null;
        return ran;
    }

    public static final double getRandomDouble(final double maxValue) {
        return (Utils.randomDouble() * (maxValue + 1));
    }

    public static int getDaysFromMillis(final long time) {
        final int seconds = (int) ((System.currentTimeMillis() - time) / 1000);
        final int minutes = seconds / 60;
        final int hours = minutes / 60;
        return (hours / 24);
    }

    public static int getHoursFromMillis(final long time) {
        final int seconds = (int) ((System.currentTimeMillis() - time) / 1000);
        final int minutes = seconds / 60;
        return (minutes / 60);
    }

    public int getHours(final long ms) {
        return (int) ((ms / (1000 * 60 * 60)) % 24);
    }

    public int getMinutes(final long ms) {
        return (int) ((ms / (1000 * 60)) % 60);
    }

    public int getSeconds(final long ms) {
        return (int) ((ms / 1000) % 60);
    }

    public static String getMinutesDisplay(final int timer) {
        String display = "";
        final int minutes = (timer / 60);
        final int seconds = (timer - minutes * 60);
        if (minutes > 0) {
            display += minutes + " minutes ";
        } else {
            display += seconds + "s";
        }
        return display;
    }

    public static int direction(final int dx, final int dy) {
        if (dx < 0) {
            if (dy < 0) {
                return 5;
            } else if (dy > 0) {
                return 0;
            } else {
                return 3;
            }
        } else if (dx > 0) {
            if (dy < 0) {
                return 7;
            } else if (dy > 0) {
                return 2;
            } else {
                return 4;
            }
        } else {
            if (dy < 0) {
                return 2;
            } else if (dy > 0) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    public static final int getMoveDirection(final int xOffset, final int yOffset) {
        if (xOffset < 0) {
            if (yOffset < 0)
                return 5;
            else if (yOffset > 0)
                return 0;
            else
                return 3;
        } else if (xOffset > 0) {
            if (yOffset < 0)
                return 7;
            else if (yOffset > 0)
                return 2;
            else
                return 4;
        } else {
            if (yOffset < 0)
                return 6;
            else if (yOffset > 0)
                return 1;
            else
                return -1;
        }
    }
}
