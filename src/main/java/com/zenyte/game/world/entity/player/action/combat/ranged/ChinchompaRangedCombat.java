package com.zenyte.game.world.entity.player.action.combat.ranged;

import com.google.common.base.Preconditions;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.player.action.combat.AmmunitionDefinitions;
import com.zenyte.game.world.entity.player.action.combat.CombatType;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.entity.player.action.combat.RangedCombat;
import com.zenyte.game.world.region.area.plugins.PlayerCombatPlugin;
import lombok.val;

/**
 * @author Kris | 1. juuni 2018 : 00:14:34
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class ChinchompaRangedCombat extends RangedCombat {

	public ChinchompaRangedCombat(final Entity target, final AmmunitionDefinitions defs) {
		super(target, defs);
	}

	private static final int SHORT_FUSE = 0, MEDIUM_FUSE = 1, LONG_FUSE = 3;

	@Override
	public int processWithDelay() {
		if (!target.startAttacking(player, CombatType.RANGED)) {
			return -1;
		}
		if (!isWithinAttackDistance()) {
			return 0;
		}
		if (!canAttack()) {
			return -1;
		}
        val area = player.getArea();
        if (area instanceof PlayerCombatPlugin) {
            ((PlayerCombatPlugin) area).onAttack(player, target, "Ranged");
        }
		addAttackedByDelay(player, target);
		val delayUntilImpact = fireProjectile();
		WorldTasksManager.schedule(() -> target.setGraphics(CombatUtilities.CHINCHOMPA_GFX), delayUntilImpact);
		player.setAnimation(CombatUtilities.CHINCHOMPA_THROW_ANIM);

        val projectile = this.ammunition.getProjectile();
        val clientCycles = projectile.getProjectileDuration(player.getLocation(), target.getLocation());
        if (ammunition.getSoundEffect() != null) {
            player.getPacketDispatcher().sendSoundEffect(ammunition.getSoundEffect());
        }
        player.getPacketDispatcher().sendSoundEffect(new SoundEffect(360, 0, clientCycles));
        val accMod = getAccuracyModifier();
        resetFlag();
        val hit = getHit(player, target, accMod, 1, 1, false);
        delayHit(target, delayUntilImpact, hit);
        val damage = hit.getDamage();
        if (damage > 0) {
            addPoisonTask(delayUntilImpact);
        }
		attackTarget(getMultiAttackTargets(player), originalTarget -> {
		    if (this.target == originalTarget) {
		        return true;
            }
		    //Chinchompa hits are rolled against the original target's defence.
            val otherHit = getHit(player, originalTarget, accMod, 1, 1, false);
		    if (damage == 0) {
		        otherHit.setDamage(0);
            }
			delayHit(target, delayUntilImpact, otherHit);
            otherHit.putAttribute("notMainFocusedTarget", true);
			return true;
		});
        dropAmmunition(delayUntilImpact, true);
        checkIfShouldTerminate();
		return getWeaponSpeed();
	}

	private final float getAccuracyModifier() {
        val distance = (int) player.getLocation().getDistance(target.getLocation());
        val style = player.getCombatDefinitions().getStyle();
        Preconditions.checkArgument(style == SHORT_FUSE || style == MEDIUM_FUSE || style == LONG_FUSE);
        float accuracyModifier = 1F;
        if (style == SHORT_FUSE) {
            if (distance >= 7) {
                accuracyModifier = 0.5F;
            } else if (distance >= 4) {
                accuracyModifier = 0.75F;
            }
        } else if (style == MEDIUM_FUSE) {
            if (distance <= 3 || distance >= 7) {
                accuracyModifier = 0.75F;
            }
        } else {
            if (distance <= 3) {
                accuracyModifier = 0.5F;
            } else if (distance <= 6) {
                accuracyModifier = 0.75F;
            }
        }
        return accuracyModifier;
    }

}
