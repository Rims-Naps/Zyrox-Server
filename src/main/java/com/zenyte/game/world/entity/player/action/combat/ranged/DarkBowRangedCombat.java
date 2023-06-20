package com.zenyte.game.world.entity.player.action.combat.ranged;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.action.combat.AmmunitionDefinitions;
import com.zenyte.game.world.entity.player.action.combat.CombatType;
import com.zenyte.game.world.entity.player.action.combat.RangedCombat;
import com.zenyte.game.world.entity.player.action.combat.SpecialType;
import com.zenyte.game.world.region.area.plugins.PlayerCombatPlugin;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.EnumMap;

/**
 * @author Kris | 1. juuni 2018 : 02:06:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class DarkBowRangedCombat extends RangedCombat {

    @AllArgsConstructor
    private enum Arrow {

        BRONZE_ARROW(AmmunitionDefinitions.BRONZE_ARROW, new Graphics(1104, 0, 96), new Projectile(10, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(10, 40, 36, 41, 35, 5, 11, 5)),

        IRON_ARROW(AmmunitionDefinitions.IRON_ARROW, new Graphics(1105, 0, 96), new Projectile(9, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(9, 40, 36, 41, 35, 5, 11, 5)),

        STEEL_ARROW(AmmunitionDefinitions.STEEL_ARROW, new Graphics(1106, 0, 96), new Projectile(11, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(11, 40, 36, 41, 35, 5, 11, 5)),

        MITHRIL_ARROW(AmmunitionDefinitions.MITHRIL_ARROW, new Graphics(1107, 0, 96), new Projectile(12, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(12, 40, 36, 41, 35, 5, 11, 5)),

        ADAMANT_ARROW(AmmunitionDefinitions.ADAMANT_ARROW, new Graphics(1108, 0, 96), new Projectile(13, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(13, 40, 36, 41, 35, 5, 11, 5)),

        RUNE_ARROW(AmmunitionDefinitions.RUNE_ARROW, new Graphics(1109, 0, 96), new Projectile(15, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(15, 40, 36, 41, 35, 5, 11, 5)),

        //TODO
        BROAD_ARROW(AmmunitionDefinitions.BROAD_ARROW, new Graphics(1112, 0, 96), new Projectile(1384, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(1384, 40, 36, 41, 35, 5, 11, 5)),

        AMETHYST_ARROW(AmmunitionDefinitions.AMETHYST_ARROW, new Graphics(1383, 0, 96), new Projectile(1384, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(1384, 40, 36, 41, 35, 5, 11, 5)),

        DRAGON_ARROW(AmmunitionDefinitions.DRAGON_ARROW, new Graphics(1113, 0, 96), new Projectile(1120, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(1120, 40, 36, 41, 35, 5, 11, 5)),

        LIT_BRONZE_ARROW(AmmunitionDefinitions.LIT_BRONZE_ARROW, new Graphics(1114, 0, 90), new Projectile(10, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(17, 40, 36, 41, 35, 5, 11, 5)),

        LIT_IRON_ARROW(AmmunitionDefinitions.LIT_IRON_ARROW, new Graphics(1114, 0, 90), new Projectile(9, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(17, 40, 36, 41, 35, 5, 11, 5)),

        LIT_STEEL_ARROW(AmmunitionDefinitions.LIT_STEEL_ARROW, new Graphics(1114, 0, 90), new Projectile(11, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(17, 40, 36, 41, 35, 5, 11, 5)),

        LIT_MITHRIL_ARROW(AmmunitionDefinitions.LIT_MITHRIL_ARROW, new Graphics(1114, 0, 90), new Projectile(12, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(17, 40, 36, 41, 35, 5, 11, 5)),

        LIT_ADAMANT_ARROW(AmmunitionDefinitions.LIT_ADAMANT_ARROW, new Graphics(1114, 0, 90), new Projectile(13, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(17, 40, 36, 41, 35, 5, 11, 5)),

        LIT_RUNE_ARROW(AmmunitionDefinitions.LIT_RUNE_ARROW, new Graphics(1114, 0, 90), new Projectile(15, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(17, 40, 36, 41, 35, 5, 11, 5)),

        LIT_AMETHYST_ARROW(AmmunitionDefinitions.LIT_AMETHYST_ARROW, new Graphics(1114, 0, 90), new Projectile(1384, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(17, 40, 36, 41, 35, 5, 11, 5)),

        LIT_DRAGON_ARROW(AmmunitionDefinitions.LIT_DRAGON_ARROW, new Graphics(1114, 0, 90), new Projectile(1120, 40, 36, 41, 7, 5, 11, 5),
                new Projectile(17, 40, 36, 41, 35, 5, 11, 5)),
        ;
        private final AmmunitionDefinitions definitions;
        private final Graphics drawback;
        private final Projectile firstProjectile;
        private final Projectile secondProjectile;
        private static final SoundEffect sound = new SoundEffect(3731, 0, 0);

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private static final EnumMap<AmmunitionDefinitions, Arrow> map;

        static {
            Utils.populateMap(values(), map = new EnumMap<>(AmmunitionDefinitions.class), v -> v.definitions);
        }
    }

	public DarkBowRangedCombat(final Entity target, final AmmunitionDefinitions defs) {
		super(target, defs);
	}
	
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
		val ammo = player.getAmmo();
		if (ammo == null || ammo.getAmount() < 2) {
			player.sendMessage("You need at least two arrows to shoot with this bow.");
			return -1;
		}
        val area = player.getArea();
        if (area instanceof PlayerCombatPlugin) {
            ((PlayerCombatPlugin) area).onAttack(player, target, "Ranged");
        }
		addAttackedByDelay(player, target);
		if (player.getCombatDefinitions().isUsingSpecial()) {
			val delay = useSpecial(player, SpecialType.RANGED);
			if (delay != -1) {
				dropAmmunition(delay - 1, true);
				dropAmmunition(delay - 1, true);
				return delay - 1;
			}
			player.putBooleanAttribute("used_special", true);
		}
		player.putBooleanAttribute("used_special", false);
		animate();
		val arrow = Arrow.map.get(ammunition);
		assert arrow != null : "Arrow for the ammunition: " + ammunition + " isn't defined.";
		val firstArrowDelay = fireProjectile(arrow.firstProjectile);
		val secondArrowDelay = fireProjectile(arrow.secondProjectile);
		val firstHit = getHit(player, target, 1, 1, 1, false);
		val secondHit = getHit(player, target, 1, 1, 1, false);
		if (firstHit.getDamage() > 0 || secondHit.getDamage() > 0) {
			addPoisonTask(firstHit.getDamage() > 0 ? firstArrowDelay : secondArrowDelay);
		}
		player.getPacketDispatcher().sendSoundEffect(Arrow.sound);
		dropAmmunition(firstArrowDelay, false);
		dropAmmunition(secondArrowDelay, false);
		resetFlag();
		delayHit(firstArrowDelay, firstHit);
		delayHit(secondArrowDelay, secondHit);
		player.setGraphics(arrow.drawback);
		checkIfShouldTerminate();
		return getWeaponSpeed();
	}

}
