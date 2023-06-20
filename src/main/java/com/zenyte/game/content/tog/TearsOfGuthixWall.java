package com.zenyte.game.content.tog;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.ImmutableLocation;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.events.ServerLaunchEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;

/**
 * @author Chris
 * @since September 08 2020
 */
public enum TearsOfGuthixWall {
    NORTH_LEFT(TearsOfGuthix.BLUE, 3257, 9520),
    NORTH_MIDDLE(TearsOfGuthix.BLUE, 3258, 9520),
    NORTH_RIGHT(TearsOfGuthix.BLUE, 3259, 9520),

    EAST_LEFT(TearsOfGuthix.ABSENCE, 3261, 9518),
    EAST_MIDDLE(TearsOfGuthix.ABSENCE, 3261, 9517),
    EAST_RIGHT(TearsOfGuthix.ABSENCE, 3261, 9516),

    SOUTH_LEFT(TearsOfGuthix.GREEN, 3259, 9514),
    SOUTH_MIDDLE(TearsOfGuthix.GREEN, 3258, 9514),
    SOUTH_RIGHT(TearsOfGuthix.GREEN, 3257, 9514);

    public static final ObjectLists.UnmodifiableList<TearsOfGuthixWall> WALLS =
            (ObjectLists.UnmodifiableList<TearsOfGuthixWall>) ObjectLists.unmodifiable(new ObjectArrayList<>(values()));
    private static final long DURATION = TimeUnit.SECONDS.toTicks(9);
    private final ImmutableLocation location;
    private final MutableInt ticks = new MutableInt();
    @Getter
    private TearsOfGuthix currentTears;
    private WorldObject wallObject;
    private int lastTransformationTick = 0;

    TearsOfGuthixWall(final TearsOfGuthix startingTears, final int x, final int y) {
        this.location = new ImmutableLocation(x, y, 2);
        this.currentTears = startingTears;
    }

    @Subscribe
    public static void onServerLaunch(final ServerLaunchEvent event) {
        for (val weepingWall : WALLS) {
            weepingWall.wallObject = World.getRegion(12948, true).getObjectWithType(weepingWall.location, 4);
        }
    }

    public static TearsOfGuthixWall of(@NotNull final Location location) {
        for (val wall : WALLS) {
            if (wall.location.equals(location)) {
                return wall;
            }
        }
        throw new IllegalArgumentException("Could not find a Tear of Guthix wall at " + location);
    }

    public final void process() {
        val currentTick = ticks.getAndIncrement();
        if (currentTears != TearsOfGuthix.ABSENCE && currentTick - lastTransformationTick >= DURATION) {
            transform(TearsOfGuthix.ABSENCE);
        }
    }

    private void transform(@NotNull final TearsOfGuthix nextTear) {
        if (nextTear == TearsOfGuthix.ABSENCE) {
            // If we are transforming this wall to one absent of tears, then we want to move its tears to a currently absent wall.
            // This ensures that tears are constantly shuffled and remain 3-3-3 each.
            val possibleWalls = new ObjectArrayList<>(WALLS);
            possibleWalls.removeIf(wall -> wall.currentTears != TearsOfGuthix.ABSENCE);
            val targetWall = Utils.getRandomCollectionElement(possibleWalls);
            targetWall.transform(currentTears);
        }
        currentTears = nextTear;
        wallObject.setId(currentTears.getObjectId());
        World.spawnObject(wallObject);
        lastTransformationTick = ticks.getValue();
    }
}
