package com.zenyte.game.content.skills.hunter.aerialfishing.npc;

import com.zenyte.game.content.skills.hunter.aerialfishing.LakeMolchArea;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import lombok.val;

/**
 * @author Cresinkel
 */

public class FishingSpotNpc extends NPC {

    public FishingSpotNpc(int id, Location tile, Direction facing, int radius) {
        super(id, tile, facing, radius);
    }

    @Override
    public boolean isMovementRestricted() {
        return true;
    }

    @Override
    public NPC spawn() {
        val npc = (FishingSpotNpc) super.spawn();
        WorldTasksManager.schedule(() -> {
            if (isDead() || isFinished()) {
                return;
            }
            LakeMolchArea.fishingSpots--;
            remove();
            LakeMolchArea.spawnSpot();
        }, Utils.random(10,30));
        return npc;
    }
}
