package com.zenyte.game.world.entity.npc.impl.wilderness;

import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.content.boss.BossRespawnTimer;
import com.zenyte.game.content.combatachievements.combattasktiers.EasyTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MediumTasks;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.GlobalAreaManager;
import lombok.val;

/**
 * @author Tommeh | 2 feb. 2018 : 20:56:22
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ChaosElemental extends NPC implements CombatScript, Spawnable {

	private boolean hasBeenDisarmed;

	private static final Graphics NPC_TELEPORT_GFX = new Graphics(553, 0, 280);
	private static final Graphics NPC_DISARMING_GFX = new Graphics(550, 0, 280);
	private static final Graphics NPC_PRIMARY_GFX = new Graphics(557, 0, 280);

	private static final Graphics PLAYER_TELEPORT_GFX = new Graphics(555, 0, 92);
	private static final Graphics PLAYER_DISARMING_GFX = new Graphics(552, 0, 92);
	private static final Graphics PLAYER_PRIMARY_GFX = new Graphics(558, 0, 92);

	private static final Projectile TELEPORT_PROJ = new Projectile(554, 70, 30, 0, 15, 29, 32, 5);
	private static final Projectile DISARMING_PROJ = new Projectile(551, 70, 30, 0, 15, 29, 32, 5);
	private static final Projectile PRIMARY_PROJ = new Projectile(557, 70, 30, 0, 15, 29, 32, 5);
	
	public ChaosElemental(int id, Location tile, Direction facing, int radius) {
		super(id, tile, facing, radius);
		hasBeenDisarmed = false;
	}
	
	@Override
	public int getRespawnDelay() {
        return BossRespawnTimer.CHAOS_ELEMENTAL.getTimer().intValue();
	}

	@Override
	public int attack(Entity target) {
		val style = Utils.random(3);
		val attack = Utils.random(4);
		val player = (Player) target;
		setAnimation(getCombatDefinitions().getAttackAnim());
		switch (attack) {
		case 0:
			setGraphics(NPC_TELEPORT_GFX);
			WorldTasksManager.schedule(() -> {
			    if (!target.getLocation().withinDistance(getLocation(), 15)) {
			        return;
                }
				Location tile;
				int count = 50;
				while (true) {
					if (--count == 0) {
						tile = new Location(target.getX() + Utils.random(12), target.getY() + Utils.random(12), target.getPlane());
						break;
					}
					tile = new Location(getX() + Utils.random(12), getY() + Utils.random(12), getPlane());
					if (World.isTileFree(tile, 1)) {
						break;
					}
				}
				player.setGraphics(PLAYER_TELEPORT_GFX);
				player.setLocation(tile);
			}, World.sendProjectile(this, target, TELEPORT_PROJ) + 1);
			break;
		case 1:
			setGraphics(NPC_DISARMING_GFX);
			WorldTasksManager.schedule(() -> {
                if (!target.getLocation().withinDistance(getLocation(), 15)) {
                    return;
                }
				if (player.getEquipment().unequipItem(EquipmentSlot.WEAPON.getSlot())) {
                    player.getActionManager().forceStop();
					player.sendMessage("The Chaos Elemental disarms you!");
					player.setGraphics(PLAYER_DISARMING_GFX);
					hasBeenDisarmed = true;
				}
			}, World.sendProjectile(this, target, DISARMING_PROJ));
			break;
		default:
			setGraphics(NPC_PRIMARY_GFX);
            delayHit(this, World.sendProjectile(this, target, PRIMARY_PROJ), player, new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(),
                    style == 0 ? MELEE : style == 1 ? RANGED : MAGIC, target),
                    style == 0 ? HitType.MELEE : style == 1 ? HitType.RANGED : HitType.MAGIC).onLand(hit -> player.setGraphics(PLAYER_PRIMARY_GFX)));
			break;
		}
		return getCombatDefinitions().getAttackSpeed();
	}

	@Override
	public NPC spawn() {
		val players = World.getPlayers();
		for (val player : players) {
			player.putBooleanAttribute("has_taken_damage_from_chaos_elemental", false);
		}
		hasBeenDisarmed = false;
		return super.spawn();
	}

	@Override
	public void onDeath(final Entity source) {
		super.onDeath(source);
		if (source instanceof Player) {
			val player = (Player) source;
			player.getAchievementDiaries().update(WildernessDiary.KILL_THE_CHAOS_ELEMENTAL);
			if (!hasBeenDisarmed && !player.getBooleanAttribute("hard-combat-achievement21")) {
				player.putBooleanAttribute("hard-combat-achievement21", true);
				HardTasks.sendHardCompletion(player, 21);
			}
			if (!player.getBooleanAttribute("has_taken_damage_from_chaos_elemental") && !player.getBooleanAttribute("hard-combat-achievement39")) {
				player.putBooleanAttribute("hard-combat-achievement39", true);
				HardTasks.sendHardCompletion(player, 39);
			}
		}
		hasBeenDisarmed = false;
	}

	@Override
	public boolean validate(int id, String name) {
		return name.equals("chaos elemental");
	}
}