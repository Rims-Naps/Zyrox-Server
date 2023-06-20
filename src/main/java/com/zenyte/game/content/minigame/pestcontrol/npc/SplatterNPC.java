package com.zenyte.game.content.minigame.pestcontrol.npc;

import com.zenyte.game.content.minigame.pestcontrol.PestControlInstance;
import com.zenyte.game.content.minigame.pestcontrol.PestNPC;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.pathfinding.events.npc.NPCObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.CharacterLoop;
import lombok.val;

import java.util.List;

/**
 * @author Kris | 26. juuni 2018 : 19:02:04
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class SplatterNPC extends PestNPC {

	private static final int LOCATING = 0, RAVAGING = 1, IN_COMBAT = 2;
	public static final Graphics[] SPLATTER_GFX = new Graphics[] { new Graphics(649), new Graphics(650), new Graphics(651),
			new Graphics(652), new Graphics(653) };
	public static final SoundEffect SOUND = new SoundEffect(847, 5);

	public SplatterNPC(final PestControlInstance instance, final PestPortalNPC portal, final int id, final Location tile) {
		super(instance, portal, id, tile);
		if (Utils.random(5) == 0) {
			state = LOCATING;
		}
	}

	@Override
	public boolean isEntityClipped() {
		return false;
	}

	private WorldObject trackedObject;
	private int state = IN_COMBAT;

	@Override
	public void processNPC() {
		if (getAttackedBy() != null) {
			state = IN_COMBAT;
		}
		if (state == IN_COMBAT) {
			super.processNPC();
			return;
		}

		if (state == RAVAGING) {
			if (!World.containsObjectWithId(trackedObject, trackedObject.getId())) {
				trackedObject = null;
				state = LOCATING;
				return;
			}
			sendDeath();
			return;
		}

		if (trackedObject == null) {
			val ravagableObjects = instance.getRavagableObjects();

			if (ravagableObjects.isEmpty()) {
				state = IN_COMBAT;
				return;
			}

			val x = getX();
			val y = getY();

			ravagableObjects.sort((final WorldObject o1, final WorldObject o2) -> {
				return o1.getDistance(x, y) > o2.getDistance(x, y) ? 1 : -1;
			});
			setRouteEvent(new NPCObjectEvent(this, new ObjectStrategy(trackedObject = ravagableObjects.get(0)), () -> state = RAVAGING));

		} else if (!hasWalkSteps()) {
			state = IN_COMBAT;
		}
	}

	@Override
	public void sendDeath() {
		val defs = getCombatDefinitions();
		resetWalkSteps();
		combat.removeTarget();
		setAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			private int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setAnimation(defs.getDeathAnim());
					World.sendGraphics(SPLATTER_GFX[getId() - 1689], new Location(getLocation()));
					World.sendSoundEffect(SplatterNPC.this, SOUND);
				} else if (loop == deathDelay) {

					val targets = getNearbyEntities();

					val location = SplatterNPC.this.getLocation();
					val objects = instance.getRavagableObjects();
					if (!objects.isEmpty()) {
						objects.forEach(object -> {
							if (object.withinDistance(location, 1)) {
								instance.destroyRavagableObject(object);
							}
						});
					}

					for (val e : targets) {
						if (!e.getLocation().withinDistance(location, 1) || e instanceof PestPortalNPC) {
							continue;
						}
						e.applyHit(new Hit(SplatterNPC.this, Utils.random(10, 25), HitType.REGULAR));
					}

					reset();
					finish();
					stop();
					return;
				}
				loop++;
			}
		}, 0, 0);
	}

	private List<Entity> getNearbyEntities() {
		if (!possibleTargets.isEmpty()) {
			possibleTargets.clear();
		}

		possibleTargets.addAll(CharacterLoop.find(getLocation(), getSize() + 1, Entity.class, e ->
		    !e.isDead() && !e.isFinished() && e.isRunning() && (!(e instanceof NPC) || ((NPC) e).isAttackable())));
        possibleTargets.remove(instance.getVoidKnight());
		return possibleTargets;
	}

}
