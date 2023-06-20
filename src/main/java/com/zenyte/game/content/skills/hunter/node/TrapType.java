package com.zenyte.game.content.skills.hunter.node;

import com.google.common.base.Preconditions;
import com.zenyte.game.util.IntArray;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NpcId;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Kris | 25/03/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
public enum TrapType {

    BIRD_SNARE(10006, 1, 9344, 9346, 9345, NpcId.SNARE_TRAP_ACTIVE, NpcId.SNARE_TRAP_INACTIVE, 2646, 2632, 2637, 2625),
    BOX_TRAP(10008, 27, 9385, -1, 9380, NpcId.BOX_TRAP_ACTIVE, NpcId.BOX_TRAP_INACTIVE, 2636, 2632, 2627, 362),
    DEADFALL(-1, 23, 19215, 20652, 19217, NpcId.DEADFALL_TRAP_ACTIVE, NpcId.DEADFALL_TRAP_INACTIVE, 2645, 2632, 2631, 524),
    PITFALL(-1, 31, -1, -1, IntArray.range(19253, 19268), NpcId.PITFALL_TRAP_ACTIVE, NpcId.PITFALL_TRAP_INACTIVE, 2639, 2649, 2638, 667),
    NET_TRAP_SWAMP_LIZARD(-1, 29, 8973, 8974, 9343, NpcId.NET_TRAP_ACTIVE, NpcId.NET_TRAP_INACTIVE, 2644, 2632, 795, 2652),
    NET_TRAP_ORANGE_SALAMANDER(-1, 47, 8973, 8974, 8731, NpcId.NET_TRAP_ACTIVE, NpcId.NET_TRAP_INACTIVE, 2644, 2632, 795, 2652),
    NET_TRAP_RED_SALAMANDER(-1, 59, 8973, 8974, 8992, NpcId.NET_TRAP_ACTIVE, NpcId.NET_TRAP_INACTIVE, 2644, 2632, 795, 2652),
    NET_TRAP_BLACK_SALAMANDER(-1, 67, 8973, 8974, 9002, NpcId.NET_TRAP_ACTIVE, NpcId.NET_TRAP_INACTIVE, 2644, 2632, 795, 2652);

    private final int itemId, level, collapsedObjectId, collapsedAnimatedObjectId, activeDummyNpcId, inactiveDummyNpcId;
    private final int setupSound, takeSound, successfulCatchSound, creatureDeathSound;
    private final int[] objectIds;
    private final String formattedString;

    /**
     * Valid dummy npc ids: (Driven by varbit 3075)
     * 1613, 1618, 1633, 1634, 3292, 3293, 3294, 3295, 3296, 3297, 3298, 3299, 3300, 3301, 3302, 3303, 3304, 5729, 5730, 5731, 5732, 7210, 7211
     * Addendum: NPC 7211 is now in use by implings!
     */

    public int getObjectId() {
        Preconditions.checkArgument(objectIds.length != 0);
        Preconditions.checkArgument(objectIds.length == 1, "Trap has more than singular object id provided.");
        return objectIds[0];
    }

    TrapType(final int itemId, final int level, final int collapsedObjectId, final int collapsedAnimatedObjectId, final int objectId, final int activeDummyNpcId,
             final int inactiveDummyNpcId, final int setupSound, final int takeSound, final int successfulCatchSound, final int creatureDeathSound) {
        this(itemId, level, collapsedObjectId, collapsedAnimatedObjectId, IntArray.of(objectId), activeDummyNpcId, inactiveDummyNpcId, setupSound, takeSound, successfulCatchSound, creatureDeathSound);
    }

    TrapType(final int itemId, final int level, final int collapsedObjectId, final int collapsedAnimatedObjectId, @NotNull final int[] objectIds, final int activeDummyNpcId,
             final int inactiveDummyNpcId, final int setupSound, final int takeSound, final int successfulCatchSound, final int creatureDeathSound) {
        this.itemId = itemId;
        this.level = level;
        this.objectIds = objectIds;
        this.collapsedObjectId = collapsedObjectId;
        this.collapsedAnimatedObjectId = collapsedAnimatedObjectId;
        this.activeDummyNpcId = activeDummyNpcId;
        this.inactiveDummyNpcId = inactiveDummyNpcId;
        this.setupSound = setupSound;
        this.takeSound = takeSound;
        this.successfulCatchSound = successfulCatchSound;
        this.creatureDeathSound = creatureDeathSound;
        this.formattedString = name().toLowerCase().replace("_", " ");
    }

    public boolean isNetTrap() {
        return this == NET_TRAP_SWAMP_LIZARD || this == NET_TRAP_ORANGE_SALAMANDER || this == NET_TRAP_RED_SALAMANDER || this == NET_TRAP_BLACK_SALAMANDER;
    }

    @Getter private static final List<TrapType> values = Collections.unmodifiableList(Arrays.asList(values()));
    //Iterate array to find values, no point in using a map here. Less efficient.

    public static final Optional<TrapType> findByItem(final int id) {
        return Optional.ofNullable(Utils.findMatching(values, value -> value.itemId == id));
    }

    @Override
    public String toString() {
        return formattedString;
    }

}