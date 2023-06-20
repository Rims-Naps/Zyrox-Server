package com.zenyte.game.content.skills.agility.varrockrooftop;

import com.zenyte.game.content.achievementdiary.diaries.VarrockDiary;
import com.zenyte.game.content.skills.agility.MarkOfGrace;
import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

public class FinishCourse implements Obstacle {
	
	private static final Animation LAND = new Animation(2586);

	@Override
	public void startSuccess(final Player player, final WorldObject object) {
		final Location ledge = new Location(player.getX(), player.getY()+1, 3);
		final Location finish = new Location(player.getX(), player.getY()+2, 0);
		player.setFaceLocation(ledge);
		WorldTasksManager.schedule(new WorldTask() {
			private int ticks;
			
			@Override
			public void run() {
				if(ticks == 0) {
					player.setAnimation(Animation.JUMP);
					player.setForceMovement(new ForceMovement(player.getLocation(), 15, ledge, 35, ForceMovement.NORTH));
				} else if(ticks == 1) {
					player.setLocation(ledge);
					player.setAnimation(LAND);
				} else if(ticks == 2) {
					player.getDailyChallengeManager().update(SkillingChallenge.COMPLETE_LAPS_VARROCK_COURSE);
					player.getAchievementDiaries().update(VarrockDiary.COMPLETE_AGILITY_COURSE_LAP);
					player.setLocation(finish);
					MarkOfGrace.spawn(player, VarrockRooftopCourse.MARK_LOCATIONS, 40, 30);
					player.addAttribute("Varrock_lap_count", player.getNumericAttribute("Varrock_lap_count").intValue() + 1);
					val laps = player.getNumericAttribute("Varrock_lap_count").intValue();
					player.sendMessage("You have ran "+ Colour.RED.wrap(laps) + (laps == 1 ? " lap " : " laps ") + "on the Varrock Rooftop Course.");
					stop();
				}				
				ticks++;
			}
		}, 0, 0);
	}
	
	@Override
	public int getLevel(final WorldObject object) {
		return 30;
	}

	@Override
	public int[] getObjectIds() {
		return new int[] { 10817 };
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 3;
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		return 125;
	}
	
}
