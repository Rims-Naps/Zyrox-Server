package com.zenyte.game.content.minigame.motherlode;

import com.zenyte.game.content.skills.mining.MiningDefinitions;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 29/06/2019 16:17
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class OreVein extends WorldObject {

    OreVein(final WorldObject parent) {
        super(parent.getId() - 4, parent.getType(), parent.getRotation(), parent.getX(), parent.getY(), parent.getPlane());
    }

    private WorldTask task;

    public void start() {
        if (task != null || !UpperMotherlodeArea.polygon.contains(this)) {
            return;
        }
        task = () -> {
            if (!exists()) {
                cancel();
                return;
            }
            val emptyId = getId() + 4;
            val empty = new WorldObject(emptyId, getType(), getRotation(), getX(), getY(), getPlane());
            World.spawnObject(empty);
            WorldTasksManager.schedule(() -> {
                if (!empty.exists()) {
                    cancel();
                    return;
                }
                World.spawnObject(this);
                cancel();
            }, MiningDefinitions.OreDefinitions.PAYDIRT.getTime());
        };
        WorldTasksManager.schedule(task, Utils.random(25, 45));
    }

    private final void cancel() {
        task = null;
    }
}
