package com.zenyte.game.world.entity.npc.impl.wilderness;

import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MediumTasks;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.network.handshake.packet.inc.HandshakeRequest;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Tommeh | 3 feb. 2018 : 21:26:57
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ChaosFanatic extends NPC implements CombatScript, Spawnable {

	private boolean hasBeenHitBySpecial;

	private static final Projectile AUTO_ATTACK_PROJ = new Projectile(554, 42, 30, 55, 15, 29, 32, 5);
	private static final Projectile SPECIAL_ATTACK_PROJ = new Projectile(551, 42, 30, 55, 55, 60, 32, 5);

	private static final Graphics FIRE_WAVE_GFX = new Graphics(157);
	private static final Graphics SPECIAL_ATTACK_GFX = new Graphics(552);

	private static final String[] FORCECHAT = new String[] { "Burn!", "WEUGH!", "Devilish Oxen Roll!",
			"All your wilderness are belong to them!", "AhehHeheuhHhahueHuUEehEahAH",
			"I shall call him squidgy and he shall be my squidgy!" };

	private static final byte[][][] OFFSETS = new byte[][][] { new byte[][] { new byte[] { 1, 0 }, new byte[] { 0, 1 } },
			new byte[][] { new byte[] { 0, -1 }, new byte[] { 1, 0 } }, new byte[][] { new byte[] { -1, 0 }, new byte[] { 0, -1 } },
			new byte[][] { new byte[] { -1, 0 }, new byte[] { 0, 1 } } };

	private int index;
	
	public ChaosFanatic(int id, Location tile, Direction facing, int radius) {
		super(id, tile, facing, radius);
        this.attackDistance = 10;
        this.radius = 20;
        this.maxDistance = 20;
		hasBeenHitBySpecial = false;
	}
	
	@Override
	public int getRespawnDelay() {
		return 50;
	}

	@Override
	public int attack(Entity target) {
		if (!(target instanceof Player)) {
			return 0;
		}
		val player = (Player) target;
		if (index == 5) {
			val tiles = new ArrayList<Location>();
			val location = new Location(target.getLocation());
			val r = Utils.random(OFFSETS.length - 1);
			for (int i = 0; i <= 1; i++) {
				tiles.add(new Location(location.getX() + OFFSETS[r][0][i], location.getY() + OFFSETS[r][1][i]));
			}
			tiles.add(location);
			getCombatDefinitions().setAttackStyle("Magic");
			for (final Location tile : tiles) {
				World.sendProjectile(this, tile, SPECIAL_ATTACK_PROJ);
			}
			WorldTasksManager.schedule(() -> {
				for (val tile : tiles) {
					World.sendGraphics(tile.equals(tiles.get(2)) ? SPECIAL_ATTACK_GFX : FIRE_WAVE_GFX, tile);
					if (target.getX() == tile.getX() && target.getY() == tile.getY()) {
						delayHit(this, 0, target,
								new Hit(this, tile.equals(tiles.get(2)) ? getCombatDefinitions().getMaxHit() : Utils.random(12, 22),
										HitType.REGULAR));
						hasBeenHitBySpecial = true;
					}
				}
			}, SPECIAL_ATTACK_PROJ.getTime(this, location));
			index = 0;
		} else {
			delayHit(this, World.sendProjectile(this, target, AUTO_ATTACK_PROJ), target, new Hit(this, 0, HitType.MAGIC).onLand(hit -> {
                if (player.getEquipment().unequipItem(EquipmentSlot.WEAPON.getSlot())) {
                    player.getActionManager().forceStop();
                    player.sendMessage("The Chaos Fanatic disarms you!");
                }
            }));
			index++;
		}
		setForceTalk(new ForceTalk(FORCECHAT[Utils.random(FORCECHAT.length - 1)]));
		setAnimation(getCombatDefinitions().getAttackAnim());
		return getCombatDefinitions().getAttackSpeed();
	}
	
	@Override
	public void onDeath(final Entity source) {
		super.onDeath(source);
		if (source instanceof Player) {
			val player = (Player) source;
			player.getAchievementDiaries().update(WildernessDiary.KILL_CRAZY_ARCHEAOLOGIST, 0x2);
			if (!hasBeenHitBySpecial && !player.getBooleanAttribute("medium-combat-achievement25")) {
				player.putBooleanAttribute("medium-combat-achievement25", true);
				MediumTasks.sendMediumCompletion(player, 25);
			}
			if (!player.getAttributes().containsKey("restored_prayer_via_pot_at_chaosfanatic_kc")) {
				player.getAttributes().put("restored_prayer_via_pot_at_chaosfanatic_kc", player.getNotificationSettings().getKillcount("chaos fanatic"));
			}
			if (player.getNumericAttribute("restored_prayer_via_pot_at_chaosfanatic_kc").intValue() + 9 <= player.getNotificationSettings().getKillcount("chaos fanatic")
					&& player.getNumericAttribute("joined_wildy_at_chaosfanatic_kc").intValue() + 9 <= player.getNotificationSettings().getKillcount("chaos fanatic")
					&& !player.getBooleanAttribute("hard-combat-achievement43")) {
				player.putBooleanAttribute("hard-combat-achievement43", true);
				HardTasks.sendHardCompletion(player, 43);
			}
			hasBeenHitBySpecial = false;
		}
	}
	
	@Override
	public boolean validate(int id, String name) {
		return name.equals("chaos fanatic");
	}
	
}