package com.zenyte.game.world.entity.npc.combat;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Toxins.ToxinType;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

public class Default implements CombatScript {

    public static final Graphics SPLASH_GRAPHICS = new Graphics(85, 0, 124);

    @Override
    public int attack(final Entity target) {
        throw new IllegalStateException();
    }

	public int attack(final NPC npc, final Entity target) {
		val defs = npc.getCombatDefinitions();
		val style = defs.getAttackStyle();
		val attDefs = defs.getAttackDefinitions();
		val proj = attDefs.getProjectile();
		val drawback = attDefs.getDrawbackGraphics();
		if (drawback != null) {
		    npc.setGraphics(drawback);
        }
		int delay = proj == null ? 1 : World.sendProjectile(npc, target, proj);
		int damage = -1;
		if (target instanceof Player) {
		    val sound = attDefs.getStartSound();
		    if (sound != null) {
                ((Player) target).sendSound(sound);
            }
        }

		if (style.isMelee()) {
			delayHit(npc, delay = 0, target, new Hit(npc, getRandomMaxHit(npc, defs.getMaxHit(), MELEE, target),
                    HitType.MELEE));
		} else if (style.isMagic()) {
            damage = getRandomMaxHit(npc, defs.getMaxHit(), MAGIC, target);
            if (damage > 0)
			    delayHit(npc, delay, target, new Hit(npc, damage, HitType.MAGIC));
		} else if (style.isRanged()) {
			delayHit(npc, delay, target, new Hit(npc, getRandomMaxHit(npc, defs.getMaxHit(), RANGED, target),
                    HitType.RANGED));
		}
        if (damage == 0 && (attDefs.getImpactGraphics() != null || attDefs.getImpactSound() != null)) {
            WorldTasksManager.schedule(() -> {
                target.setGraphics(SPLASH_GRAPHICS);
                if (attDefs.getImpactGraphics() != null) {
                    target.setGraphics(attDefs.getImpactGraphics());
                }
                if (attDefs.getImpactSound() != null) {
                    World.sendSoundEffect(target, attDefs.getImpactSound());
                }
            }, delay);
        }
		npc.setAnimation(defs.getAttackAnim());
		if (defs.getToxinDefinitions() != null && defs.getToxinDefinitions().getType() == ToxinType.POISON && Utils.random(3) == 0) {
			target.getToxins().applyToxin(ToxinType.POISON, defs.getToxinDefinitions().getDamage());
		}
		return defs.getAttackSpeed();
	}

}
