package com.zenyte.game.world.entity.npc.combatdefs;

import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

/**
 * @author Kris | 18/11/2018 02:54
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@Setter
public class SpawnDefinitions {
    private int respawnDelay = 25;
    private Animation deathAnimation = Animation.STOP;
    private Animation spawnAnimation = Animation.STOP;
    private SoundEffect deathSound;
    private SoundEffect spawnSound;

    public static SpawnDefinitions construct(final SpawnDefinitions clone) {
        val defs = new SpawnDefinitions();
        if (clone == null)
            return defs;
        defs.respawnDelay = clone.respawnDelay;
        defs.deathAnimation = clone.deathAnimation;
        defs.spawnAnimation = clone.spawnAnimation;
        defs.deathSound = clone.deathSound;
        defs.spawnSound = clone.spawnSound;
        return defs;
    }

}