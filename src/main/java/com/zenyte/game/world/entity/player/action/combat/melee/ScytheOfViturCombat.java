package com.zenyte.game.world.entity.player.action.combat.melee;

import com.zenyte.game.content.event.christmas2019.ChristmasConstants;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.degradableitems.DegradeType;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Entity.EntityType;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.CombatType;
import com.zenyte.game.world.entity.player.action.combat.MeleeCombat;
import com.zenyte.game.world.entity.player.action.combat.SpecialAttackScript;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.area.plugins.PlayerCombatPlugin;
import lombok.val;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kris | 21. juuni 2018 : 17:52:33
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class ScytheOfViturCombat extends MeleeCombat {

	public ScytheOfViturCombat(final Entity target) {
		super(target);
	}

	protected Direction direction;

	@Override
	public int processWithDelay() {
		if (!target.startAttacking(player, CombatType.MELEE)) {
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
            ((PlayerCombatPlugin) area).onAttack(player, target, "Melee");
        }
		addAttackedByDelay(player, target);
		sendSoundEffect();
		val weapon = player.getWeapon();
		val christmasScythe = player.getEquipment().getId(EquipmentSlot.WEAPON) == ChristmasConstants.CHRISTMAS_SCYTHE;
		val unchargedScythe = player.getEquipment().getId(EquipmentSlot.WEAPON) == ItemId.SCYTHE_OF_VITUR_UNCHARGED;
		if (weapon != null && weapon.getCharges() > 0 || christmasScythe || unchargedScythe) {
			specialAttack();
		} else {
			val hit = getHit(player, target, 1, 1, 1, false);
			addExtraEffect(hit);
            addPoisonTask(hit.getDamage(), 0);
			delayHit(1, hit);
		}
		animate();
		if (!christmasScythe) {
			player.getChargesManager().removeCharges(DegradeType.OUTGOING_HIT);
		}
		resetFlag();
		checkIfShouldTerminate();
		return getSpeed();
	}

	private final void specialAttack() {
		val targets = getMultiAttackTargets(player);
		int hitcount = 0;
		val christmasScythe = player.getEquipment().getId(EquipmentSlot.WEAPON) == ChristmasConstants.CHRISTMAS_SCYTHE;
		for (val t : targets) {
			hitcount++;
			val hit = getHit(player, t, 1.4, hitcount == 1 ? 1F : hitcount == 2 ? 0.5F : 0.25F, 1, false);
			if (hit.getDamage() > 0) {
				addExtraEffect(hit);
				addPoisonTask(hit.getDamage(), -1);
			}
			delayHit(t, -1, hit);
			if (christmasScythe) {
				break;
			}
		}
		if (christmasScythe) {
			return;
		}
		if (hitcount < 3) {
			for (val t : targets) {
				if (t.getSize() == 1) {
					continue;
				}
				while (hitcount++ < 3) {
					val hit = getHit(player, t, 1.4, hitcount == 1 ? 1F : hitcount == 2 ? 0.5F : 0.25F, 1, false);
					if (hit.getDamage() > 0) {
						addExtraEffect(hit);
						addPoisonTask(hit.getDamage(), -1);
					}
					delayHit(t, -1, hit);
					if (targets.size() == 1 && t.getSize() == 2 && hitcount == 2) {
						break;
					}
				}
				break;
			}
		}
			val middle = target.getLocation();
			val direction =
					(Utils.getFaceDirection((middle.getX() + (target.getSize() / 2F)) - player.getX(),
							(middle.getY() + (target.getSize() / 2F)) - player.getY()) + ((target.getSize() & 0x1) == 0 ? 128 : 0)) & 2047;
			if (direction > 256) {
				if (direction < 768) {
					this.direction = Direction.WEST;
					World.sendGraphics(SpecialAttackScript.SWEEP_SCYTHE_WEST_GFX,
							new Location(player.getLocation().getX() - 1, player.getLocation().getY(), target.getPlane()));
				} else if (direction < 1280) {
					this.direction = Direction.NORTH;
					World.sendGraphics(SpecialAttackScript.SWEEP_SCYTHE_NORTH_GFX,
							new Location(player.getLocation().getX(), player.getLocation().getY() + 1, target.getPlane()));
				} else if (direction < 1792) {
					this.direction = Direction.EAST;
					World.sendGraphics(SpecialAttackScript.SWEEP_SCYTHE_EAST_GFX,
							new Location(player.getLocation().getX() + 1, player.getLocation().getY(), target.getPlane()));
				} else {
					this.direction = Direction.SOUTH;
					World.sendGraphics(SpecialAttackScript.SWEEP_SCYTHE_SOUTH_GFX,
							new Location(player.getLocation().getX(), player.getLocation().getY() - 1, target.getPlane()));
				}
			} else {
				this.direction = Direction.SOUTH;
				World.sendGraphics(SpecialAttackScript.SWEEP_SCYTHE_SOUTH_GFX,
						new Location(player.getLocation().getX(), player.getLocation().getY() - 1, target.getPlane()));
			}
    }

	@Override
	public final Set<Entity> getMultiAttackTargets(final Player player, final int maxDistance,
                                                   final int maxAmtTargets) {
		final Set<Entity> possibleTargets = new HashSet<>();
		possibleTargets.add(target);
		val targets = player.getPossibleTargets(EntityType.NPC);
		val middle = target.getMiddleLocation();
		val x = middle.getX();
		val y = middle.getY();
		val z = middle.getPlane();
		val centerHash = middle.getPositionHash();
		val leftPositionHash = direction == Direction.SOUTH ? Location.hash(x + 1, y, z)
				: direction == Direction.WEST ? Location.hash(x, y - 1, z)
						: direction == Direction.NORTH ? Location.hash(x - 1, y, z) : Location.hash(x, y + 1, z);

		val rightPositionHash = direction == Direction.SOUTH ? Location.hash(x - 1, y, z)
				: direction == Direction.WEST ? Location.hash(x, y + 1, z)
						: direction == Direction.NORTH ? Location.hash(x + 1, y, z) : Location.hash(x, y - 1, z);

		for (val t : targets) {
			if (possibleTargets.size() >= 3) {
				break;
			}
			val hash = t.getLocation().getPositionHash();
			if (hash == leftPositionHash || hash == rightPositionHash || hash == centerHash) {
				possibleTargets.add(t);
			}
		}

		return possibleTargets;
	}

}
