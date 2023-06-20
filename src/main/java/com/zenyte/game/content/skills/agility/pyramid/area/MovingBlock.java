package com.zenyte.game.content.skills.agility.pyramid.area;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.ImmutableLocation;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.plugins.events.ServerLaunchEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.EnumMap;

@RequiredArgsConstructor
@Getter
public enum MovingBlock {
    FIRST_LEVEL_BLOCK(5788, 10872, new ImmutableLocation(3372, 2847, 1), Direction.EAST),
    THIRD_LEVEL_BLOCK(5788, 10873, new ImmutableLocation(3366, 2845, 3), Direction.NORTH);

    public static final MovingBlock[] values = values();
    private final int npcId;
    private final int objectId;
    private final Location spawn;
    private final Direction direction;

    private static final EnumMap<MovingBlock, MovingBlockNPC> map = new EnumMap<>(MovingBlock.class);

    @Subscribe
    public static final void onServerLaunch(final ServerLaunchEvent event) {
        for (val block : values()) {
            val npc = new MovingBlockNPC(block.npcId, block.spawn, block.direction, 0);
            npc.spawn();
            map.put(block, npc);
        }
    }

    static void moveBlocks() {
        for (val entry : map.entrySet()) {
            val key = entry.getKey();
            val value = entry.getValue();
            value.slide(key.direction);
            World.sendSoundEffect(key.spawn, new SoundEffect(1395, 5));
            WorldTasksManager.schedule(() -> value.slide(key.direction.getCounterClockwiseDirection(4)), 6);
        }
    }
}
