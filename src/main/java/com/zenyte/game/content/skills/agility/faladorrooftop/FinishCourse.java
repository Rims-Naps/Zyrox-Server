package com.zenyte.game.content.skills.agility.faladorrooftop;

import com.zenyte.game.content.achievementdiary.diaries.FaladorDiary;
import com.zenyte.game.content.skills.agility.MarkOfGrace;
import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Noele | May 1, 2018 : 1:37:39 AM
 * @see https://noeles.life || noele@zenyte.com
 */
public class FinishCourse implements Obstacle {

	private static final Animation LEAP = new Animation(1603);
	private static final Animation JUMP = new Animation(2583);
	private static final Animation LAND = new Animation(2588);
	
	@Override
	public void startSuccess(final Player player, final WorldObject object) {
		final Location ledge = new Location(player.getX()+3, player.getY(), 3);
		final Location finish = new Location(ledge.getX()+2, player.getY(), 0);
		player.setFaceLocation(finish);
		WorldTasksManager.schedule(new WorldTask() {

			private int ticks;
			
			@Override
			public void run() {
				if(ticks == 0) {
					player.setAnimation(LEAP);
					player.setForceMovement(new ForceMovement(ledge, 45, ForceMovement.EAST));
				} else if(ticks == 2) {
					player.setLocation(ledge);
				} else if(ticks == 3) {
					player.setAnimation(JUMP);
				} else if(ticks == 4) {
					player.getAchievementDiaries().update(FaladorDiary.COMPLETE_ROOFTOP_COURSE);
					player.setAnimation(LAND);
					player.setLocation(finish);
					MarkOfGrace.spawn(player, FaladorRooftopCourse.MARK_LOCATIONS, 50, 50);
					player.addAttribute("Falador_lap_count", player.getNumericAttribute("Falador_lap_count").intValue() + 1);
					val laps = player.getNumericAttribute("Falador_lap_count").intValue();
					player.sendMessage("You have ran "+ Colour.RED.wrap(laps) + (laps == 1 ? " lap " : " laps ") + "on the Falador Rooftop Course.");
					stop();
				}
				ticks++;
			}
			
		}, 0, 0);
	}
	
	@Override
	public int getLevel(final WorldObject object) {
		return 50;
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 4;
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		return 180;
	}
	
	@Override
	public int[] getObjectIds() {
		return new int[] { 11371 };
	}
}
