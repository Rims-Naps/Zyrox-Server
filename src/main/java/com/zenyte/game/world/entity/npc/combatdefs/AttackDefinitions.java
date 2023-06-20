package com.zenyte.game.world.entity.npc.combatdefs;

import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

/**
 * @author Kris | 18/11/2018 02:52
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@Setter
public class AttackDefinitions {

    //TODO don't forget to update this on construction.
    private transient AttackType defaultMeleeType = AttackType.CRUSH;

    private AttackType type = AttackType.CRUSH;
    private int maxHit;
    private Animation animation = Animation.STOP;
    private SoundEffect startSound;
    private SoundEffect impactSound;
    private Projectile projectile;
    private Graphics impactGraphics;
    private Graphics drawbackGraphics;

    public static AttackDefinitions construct(final AttackDefinitions clone) {
        val defs = new AttackDefinitions();
        if (clone == null) {
            return defs;
        }
        defs.type = clone.type;
        defs.maxHit = clone.maxHit;
        defs.animation = clone.animation;
        defs.startSound = clone.startSound;
        defs.impactSound = clone.impactSound;
        defs.projectile = clone.projectile;
        defs.impactGraphics = clone.impactGraphics;
        defs.drawbackGraphics = clone.drawbackGraphics;
        if (clone.type.isMelee()) {
            defs.defaultMeleeType = clone.type;
        }
        return defs;
    }

}
