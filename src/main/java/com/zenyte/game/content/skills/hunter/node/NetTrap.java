package com.zenyte.game.content.skills.hunter.node;

import com.google.common.base.Preconditions;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.object.ObjectId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Kris | 01/04/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@AllArgsConstructor
public enum NetTrap {
    GREEN(ObjectId.YOUNG_TREE_9341, ObjectId.YOUNG_TREE_9257, TrapType.NET_TRAP_SWAMP_LIZARD),
    ORANGE(ObjectId.YOUNG_TREE_8732, ObjectId.YOUNG_TREE, TrapType.NET_TRAP_ORANGE_SALAMANDER),
    RED(ObjectId.YOUNG_TREE_8990, ObjectId.YOUNG_TREE_8989, TrapType.NET_TRAP_ORANGE_SALAMANDER),
    BLACK(ObjectId.YOUNG_TREE_9000, ObjectId.YOUNG_TREE_8999, TrapType.NET_TRAP_BLACK_SALAMANDER);

    private final int youngTree, bentYoungTree;
    private final TrapType type;

    @Getter private static final List<NetTrap> values = Collections.unmodifiableList(Arrays.asList(values()));

    @Getter private static final Animation treeBendingDownAnimation = new Animation(5266);
    @Getter private static final Animation treeRisingUpWithNetAnimation = new Animation(5268);
    @Getter private static final Animation treeRisingUpWithoutNetAnimation = new Animation(5270);

    @NotNull
    public static NetTrap findTrap(final int objectId) throws IllegalArgumentException {
        val value = Utils.findMatching(values, v -> v.youngTree == objectId || v.bentYoungTree == objectId
                || v.type.getObjectId() == objectId);
        Preconditions.checkArgument(value != null);
        return value;
    }

}
