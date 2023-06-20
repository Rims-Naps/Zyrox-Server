package com.zenyte.game.world.entity.npc.combatdefs;

import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

/**
 * @author Kris | 18/11/2018 02:53
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@Setter
public class BlockDefinitions {
    private Animation animation = Animation.STOP;
    private SoundEffect sound;

    public static BlockDefinitions construct(final BlockDefinitions clone) {
        val defs = new BlockDefinitions();
        if (clone == null)
            return defs;
        defs.animation = clone.animation;
        if (clone.sound != null) {
            defs.sound = new SoundEffect(clone.sound.getId(), clone.sound.getRadius(), clone.sound.getDelay() + 30);
        }
        return defs;
    }

}
