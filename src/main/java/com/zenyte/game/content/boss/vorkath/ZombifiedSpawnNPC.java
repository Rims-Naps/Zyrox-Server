package com.zenyte.game.content.boss.vorkath;

import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.pathfinding.RouteFinder;
import com.zenyte.game.world.entity.pathfinding.strategy.TileStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.entity.player.action.combat.magic.CombatSpell;
import lombok.val;

/**
 * @author Kris | 7. veebr 2018 : 15:34.06
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class ZombifiedSpawnNPC extends NPC {

	private static final Graphics GFX = new Graphics(163, 0, 60);
	private static final SoundEffect preExplodeSound = new SoundEffect(232, 10);
	private static final SoundEffect explodeSound = new SoundEffect(208, 10);
	
	ZombifiedSpawnNPC(final Location tile, final VorkathNPC vorkath, final Player target) {
		super(8062, tile, Direction.SOUTH, 5);
		freeze(1);
		setForceMultiArea(true);
		setSpawned(true);
		this.target = target;
		this.vorkath = vorkath;
		this.supplyCache = false;
		this.getTemporaryAttributes().remove("no crumble");
	}
	
	private final Player target;
	private final VorkathNPC vorkath;
	
	@Override
	public void processNPC() {
		if (isDead() || target.isDead() || target.isFinished()) {
			return;
		}
		if (vorkath.isDead() || vorkath.isFinished() || !getLocation().withinDistance(target.getLocation(), 30)
                || getLocation().withinDistance(target.getLocation(), 1)) {
			val damage = Math.round(getHitpoints() * 1.579f);
			sendDeath();
			if (vorkath.isDead() || vorkath.isFinished() || !getLocation().withinDistance(target.getLocation(), 30))
			    return;
			CombatUtilities.delayHit(this, -1, target, new Hit(damage, HitType.REGULAR));
			target.putBooleanAttribute("hitByZombifiedSpawn", true);
			return;
		}
		if (getLocation().withinDistance(target.getLocation(), 2)) {
			preExplodeSound.sendGlobal(getLocation());
		}
		if (!isFrozen() && !hasWalkSteps()) {
			setFaceEntity(target);
			val steps = RouteFinder.findRoute(getX(), getY(), getPlane(), getSize(), new TileStrategy(target.getX(),
                    target.getY()), true);
			val bufferX = steps.getXBuffer();
			val bufferY = steps.getYBuffer();
			resetWalkSteps();
			for (int i = steps.getSteps() - 1; i >= 0; i--) {
				if (!addWalkStepsInteract(bufferX[i], bufferY[i], 25, 1, true)) {
					break;
				}
			}
		}
	}
	
	@Override
	public void applyHit(final Hit hit) {
		super.applyHit(hit);
		if (hit.getWeapon() == CombatSpell.CRUMBLE_UNDEAD) {
			target.sendMessage("You become unfrozen as you kill the spawn.");
			this.getTemporaryAttributes().put("no crumble", false);
		}
	}

	@Override
	public float getXpModifier(final Hit hit) {
		if (hit.getWeapon() == CombatSpell.CRUMBLE_UNDEAD) {
			hit.setDamage(getHitpoints());
		}
		return 1;
	}
	
	@Override
	public void sendDeath() {
		target.resetFreeze();
		vorkath.setDamageModifier(1);
		vorkath.setDelay(3);
		explodeSound.sendGlobal(getLocation());
		super.sendDeath();
		World.sendGraphics(GFX, new Location(getLocation()));
		finish();
	}

	@Override
	public void onDeath(final Entity source) {
		super.onDeath(source);
		if (source instanceof Player) {
			val player = (Player) source;
			if (!this.getTemporaryAttributes().containsKey("no crumble") && !player.getBooleanAttribute("elite-combat-achievement64")) {
				player.putBooleanAttribute("elite-combat-achievement64", true);
				EliteTasks.sendEliteCompletion(player, 64);
			}
		}
	}



}
