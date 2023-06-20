package com.zenyte.game.world.entity.npc.impl.wilderness.vetion;

import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.content.boss.BossRespawnTimer;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Tommeh | 9 feb. 2018 : 23:21:33
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class Vetion extends NPC implements CombatScript, Spawnable {

	private enum VetionPhase {
		PHASE_1,
		PHASE_2
	}

	private static final Animation AUTO_ATTACK_ANIM = new Animation(5499);
	private static final Animation SHOCKWAVE_ATTACK_ANIM = new Animation(5507);

	private static final Graphics SPECIAL_ATTACK_GFX = new Graphics(281);

	private static final Projectile SPECIAL_ATTACK_PROJ = new Projectile(280, 69, 30, 55, 65, 60, 32, 5);

	private static final byte[][][] OFFSETS = new byte[][][] { new byte[][] { new byte[] { 1, 0 }, new byte[] { 0, 1 } },
			new byte[][] { new byte[] { 0, -1 }, new byte[] { 1, 0 } }, new byte[][] { new byte[] { -1, 0 }, new byte[] { 0, -1 } },
			new byte[][] { new byte[] { -1, 0 }, new byte[] { 0, 1 } } };

	private static final ForceTalk[] NPC_CHAT = new ForceTalk[] { new ForceTalk("Kill, my pets!"),
			new ForceTalk("Bahh! Go, Dogs!!"), new ForceTalk("GRRRRRRRRRRRR"), new ForceTalk("Do it again!!") };

	private SkeletonHellhound[] hellhounds;
	private boolean[] spawnedHounds;
	private VetionPhase phase;


	public Vetion(final int id, final Location tile, final Direction facing, final int radius) {
		super(id, tile, facing, radius);
		resetNPC();
	}

	@Override
	public float getXpModifier(final Hit hit) {
		return isDamageable() ? 1 : 0;
	}

	@Override
	protected boolean preserveStatsOnTransformation() {
		return true;
	}

	@Override
	public int getRespawnDelay() {
		return BossRespawnTimer.VETION.getTimer().intValue();
	}
	
	@Override
	public boolean isTolerable() {
		return false;
	}
	
	@Override
	public boolean isEntityClipped() {
		return false;
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		if (!isDamageable()) {
			val source = hit.getSource();
			hit.setDamage(0);
			if (source instanceof Player) {
				((Player) source).sendMessage("You must kill the Skeletal Hellhounds first in order to harm Vet'ion!");
			}
		}
		super.handleIngoingHit(hit);
	}

	@Override
	protected void spawnDrop(final Item item, final Location tile, final Player killer) {
		if (item.getId() == ItemId.DARK_CRAB) {
			dropItem(killer, new Item(ItemId.SUPER_RESTORE4, 3));
		}
		super.spawnDrop(item, tile, killer);
	}

	@Override
	public void processNPC() {
	    if (combatDefinitions.getAttackStyle().isMelee() && !this.hasWalkSteps()) {
	        combatDefinitions.setAttackStyle(AttackType.MAGIC);
        }
		super.processNPC();
		int ticks = 0;
		if (!isUnderCombat()) {
			if (++ticks >= 50) {
				for (int i = 0; i < 2; i++) {
					if (hellhounds[i] != null) {
						hellhounds[i].finish();
					}
				}
				resetNPC();
				setTransformation(6611);
				ticks = 0;
			}
		} else if (ticks != 0) {
			ticks = 0;
		}
		if (getHitpoints() > (getMaxHitpoints() / 2)) {
			return;
		}
		if (isDead() || spawnedHounds[getId() == 6611 ? 0 : 1]) {
			return;
		}
		val target = getCombat().getTarget();
		val tiles = new Location[2];
		val id = getId() == 6611 ? 6613 : 6614;
		hellhounds = new SkeletonHellhound[2];
		for (int i = 0; i < 2; i++) {
			int count = 50;
			while (true) {
				if (--count == 0) {
					tiles[i] = new Location(getLocation());
					break;
				}
				tiles[i] = new Location(getX() + Utils.random(4), getY() + Utils.random(4), getPlane());
				if (World.isTileFree(tiles[i], 2)) {
					break;
				}
			}
			if (target != null) {
				hellhounds[i] = new SkeletonHellhound(id, tiles[i], Direction.SOUTH, 0);
				hellhounds[i].spawn();
				hellhounds[i].getCombat().setTarget(target);
				if (i == 0) {
					hellhounds[i].setForceTalk(NPC_CHAT[2]);
				}
			}
		}
		setForceTalk(getId() == 6611 ? NPC_CHAT[0] : NPC_CHAT[1]);
		spawnedHounds[getId() == 6611 ? 0 : 1] = true;
	}

	@Override
	public void sendDeath() {
		if (getHitpoints() == 0 && getId() == 6611) {
			setTransformation(6612);
			heal(getMaxHitpoints());
			setForceTalk(NPC_CHAT[3]);
			phase = VetionPhase.PHASE_2;

		} else {
			super.sendDeath();
			setId(6611);
			resetNPC();
		}
	}

	@Override
	public void onDeath(final Entity source) {
		super.onDeath(source);
		if (id == 6612 && source instanceof Player) {
			val player = (Player) source;
			player.getAchievementDiaries().update(WildernessDiary.KILL_CALLISTO, 0x4);
		}
	}
	
	@Override
	public int attack(Entity target) {
	    val shockwave = getTemporaryAttributes().get("VetionShockwaveDelay");
	    val shockwaveTime = !(shockwave instanceof Number) ? 0 : ((Number) shockwave).longValue();
		val attack = Utils.random(shockwaveTime < Utils.currentTimeMillis() ? 6 : 5);

		if (attack == 6) {
			setAnimation(SHOCKWAVE_ATTACK_ANIM);
			for (val entity : getPossibleTargets(EntityType.PLAYER)) {
				if (entity.getLocation().withinDistance(getMiddleLocation(), 3)) {
					delayHit(this, 1, entity, new Hit(this, getRandomMaxHit(this, 45, MELEE, target), HitType.REGULAR).onLand(hit -> ((Player) entity).sendMessage("Vet'ion pummels the ground sending a shattering earthquake shockwave through you.")));
				}
			}
			getTemporaryAttributes().put("VetionShockwaveDelay", 6000 + Utils.currentTimeMillis());
		} else {
			setAnimation(AUTO_ATTACK_ANIM);
			val targets = getPossibleTargets(getEntityType());
			targets.removeIf(entity -> !Utils.isOnRange(getX(), getY(), getSize(), entity.getX(), entity.getY(), entity.getSize(), 3));
			val melee = Utils.random(2) == 0;
            if (melee && !combatDefinitions.getAttackStyle().isMelee()) {
                combatDefinitions.setAttackStyle(AttackType.MELEE);
            }
			if (targets.isEmpty() || !melee) {
			    if (Utils.random(5) == 0) {
			        calcFollow(target, -1, true, false, false);
                }
				val tiles = new ArrayList<Location>();
				val location = new Location(target.getLocation());
				val random = Utils.random(OFFSETS.length - 1);
				for (int i = 0; i <= 1; i++) {
					tiles.add(new Location(location.getX() + OFFSETS[random][0][i], location.getY() + OFFSETS[random][1][i]));
				}
				tiles.add(location);
				for (val tile : tiles) {
					World.sendProjectile(this, tile, SPECIAL_ATTACK_PROJ);
				}
				WorldTasksManager.schedule(() -> {
					for (val tile : tiles) {
						World.sendGraphics(SPECIAL_ATTACK_GFX, tile);
						if (target.getX() == tile.getX() && target.getY() == tile.getY()) {
							delayHit(this, 0, target, new Hit(this, Utils.random(10, 30), HitType.REGULAR));
						}
					}
				}, SPECIAL_ATTACK_PROJ.getTime(this, location));
			} else {
				delayHit(this, 1, target, new Hit(this, getRandomMaxHit(this, 30, MELEE, target), HitType.MELEE));
			}
		}
		return getCombatDefinitions().getAttackSpeed();
	}

	private boolean isDamageable() {
		if (hellhounds == null) {
			return true;
		}
		for (val hellhound : hellhounds) {
			if (hellhound != null && !hellhound.isFinished()) {
				return false;
			}
		}
		return true;
	}

	private void resetNPC() {
		hellhounds = null;
		spawnedHounds = new boolean[2];
		phase = VetionPhase.PHASE_1;
	}


	@Override
	public boolean validate(final int id, final String name) {
		return id == 6611 || id == 6612;
	}

}
