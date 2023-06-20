/**
 * 
 */
package com.zenyte.game.content.skills.agility.faladorrooftop;

import com.zenyte.game.content.skills.agility.MarkOfGrace;
import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.RenderAnimation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Noele | Apr 29, 2018 : 12:38:48 PM
 * @see https://noeles.life || noele@zenyte.com
 */
public class Tightrope implements Obstacle {
	
	private static final Map<Integer, TightropeInfo> ROPES = new HashMap<Integer, TightropeInfo>();
	private static final RenderAnimation WALK = new RenderAnimation(763, 762, 762, 762, 762, 762, -1);
	
	static {
		for (TightropeInfo entry : TightropeInfo.VALUES)
			ROPES.put(entry.getId(), entry);
	}

	@Override
	public void startSuccess(final Player player, final WorldObject object) {
		if (ROPES.get(object.getId()) == null)
			return;

		final TightropeInfo rope = ROPES.get(object.getId());
		final boolean special = rope.getId() == 11361;
		player.setRunSilent(true);
		player.faceObject(object);
		WorldTasksManager.schedule(new WorldTask() {
			private int ticks;

			@Override
			public void run() {
				if (ticks == 0)
					player.addWalkSteps(object.getX(), special ? 3362 : object.getY(), -1, false);
				else if (ticks == 1) {
					player.addWalkSteps(rope.getFinish().getX(), rope.getFinish().getY(), -1, false);
					player.getAppearance().setRenderAnimation(WALK);
				}

				if (player.getLocation().getPositionHash() == rope.getFinish().getPositionHash()) {
					player.getAppearance().resetRenderAnimation();
					MarkOfGrace.spawn(player, FaladorRooftopCourse.MARK_LOCATIONS, 50, 50);
					player.setRunSilent(false);
					stop();
				}
				ticks++;
			}
		}, 0, 0);
	}

	@Override
	public int[] getObjectIds() {
		return new int[]{10834, 11361, 11364};
	}

	@Override
	public int getLevel(final WorldObject object) {
		return 50;
	}

	private enum TightropeInfo {

		FIRST(10834, 17, 10, new Location(3039, 3343, 3), new Location(3047, 3343, 3)),
		SECOND(11361, 45, 9, new Location(3035, 3362, 3), new Location(3027, 3354, 3)),
		THIRD(11364, 40, 7, new Location(3027, 3353, 3), new Location(3020, 3353, 3)),
		;

		public static final TightropeInfo[] VALUES = values();
		@Getter
		private int id;
		@Getter
		private double xp;
		@Getter
		private int delay;
		@Getter
		private Location start;
		@Getter
		private Location finish;

		TightropeInfo(final int id, final double xp, final int delay, final Location start, final Location finish) {
			this.id = id;
			this.xp = xp;
			this.delay = delay;
			this.start = start;
			this.finish = finish;
		}
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		final TightropeInfo rope = ROPES.get(object.getId());
		return rope == null ? 8 : rope.getDelay();
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		final TightropeInfo rope = ROPES.get(object.getId());
		return rope == null ? 10 : rope.getXp();
	}
	
	@Override
	public Location getRouteEvent(final Player player, final WorldObject object) {
		final TightropeInfo rope = ROPES.get(object.getId());
		return rope == null ? object : rope.getStart();
	}
}
