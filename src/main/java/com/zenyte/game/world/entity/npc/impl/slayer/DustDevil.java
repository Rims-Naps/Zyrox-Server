package com.zenyte.game.world.entity.npc.impl.slayer;

import com.zenyte.game.content.achievementdiary.diaries.DesertDiary;
import com.zenyte.game.content.skills.slayer.SlayerEquipment;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import lombok.val;

/**
 * @author Tommeh | 9 dec. 2017 : 23:10:39
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class DustDevil extends NPC implements CombatScript, Spawnable {

	private static final Projectile PROJECTILE = new Projectile(73, 30, 35, 108, 10);

	public DustDevil(final int id, final Location tile, final Direction facing, final int radius) {
		super(id, tile, facing, radius);
	}

	@Override
	public int attack(final Entity target) {
		if (!(target instanceof Player)) {
			return 0;
		}
		val player = (Player) target;
		attackSound();
		setAnimation(getCombatDefinitions().getAttackAnim());
		if (!SlayerEquipment.FACE_MASK.isWielding(player)) {
			getCombatDefinitions().setAttackStyle("Magic");
            delayHit(this, World.sendProjectile(this, target, PROJECTILE), player, new Hit(this, 15, HitType.REGULAR).onLand(hit -> {
                player.getSkills().setLevel(Skills.ATTACK, 0);
                player.getSkills().setLevel(Skills.STRENGTH, 0);
                player.getSkills().setLevel(Skills.RANGED, 0);
                player.getSkills().setLevel(Skills.MAGIC, 0);
                player.getSkills().setLevel(Skills.PRAYER, (int) (player.getSkills().getLevel(Skills.PRAYER) * 0.5064935064935065));
                player.getSkills().setLevel(Skills.DEFENCE, (int) (player.getSkills().getLevel(Skills.DEFENCE) * 0.5064935064935065));
                player.getSkills().setLevel(Skills.AGILITY, (int) (player.getSkills().getLevel(Skills.AGILITY) * 0.5064935064935065));
            }));
		} else {
			getCombatDefinitions().setAttackStyle("Melee");
			delayHit(this, 0, player,
					new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), MELEE, target), HitType.MELEE));
		}
		return getCombatDefinitions().getAttackSpeed();
	}

	@Override
	public void onDeath(final Entity source) {
		super.onDeath(source);
		if (source instanceof Player) {
			val player = (Player) source;
			if (SlayerEquipment.SLAYER_HELM.isWielding(player)) {
				player.getAchievementDiaries().update(DesertDiary.SLAY_DUST_DEVIL);
			}
		}
	}

	@Override
	public boolean validate(final int id, final String name) {
		return name.equals("dust devil");
	}

}
