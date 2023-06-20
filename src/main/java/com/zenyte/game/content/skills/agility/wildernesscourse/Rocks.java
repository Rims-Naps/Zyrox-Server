package com.zenyte.game.content.skills.agility.wildernesscourse;

import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Tommeh | 25 feb. 2018 : 18:35:58
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public final class Rocks implements Obstacle {

	private static final Animation CLIMBING_ANIM = new Animation(740);

	@Override
	public String getEndMessage(final boolean success) {
		return "You reach the top.";
	}

	@Override
	public void startSuccess(final Player player, final WorldObject object) {
		player.setAnimation(CLIMBING_ANIM);
		player.setForceMovement(new ForceMovement(new Location(object.getX(), object.getY() - 3, object.getPlane()), 120, ForceMovement.SOUTH));
		WorldTasksManager.schedule(() -> {
			player.getDailyChallengeManager().update(SkillingChallenge.COMPLETE_LAPS_WILDERNESS_AGILITY_COURSE);
			player.getAchievementDiaries().update(WildernessDiary.COMPLETE_AGILITY_COURSE_LAP);
			player.setAnimation(Animation.STOP);
			player.setLocation(new Location(object.getX(), object.getY() - 3, object.getPlane()));
		}, 3);
		player.addAttribute("Wildy_lap_count", player.getNumericAttribute("Wildy_lap_count").intValue() + 1);
		val laps = player.getNumericAttribute("Wildy_lap_count").intValue();
		player.sendMessage("You have ran "+ Colour.RED.wrap(laps) + (laps == 1 ? " lap " : " laps ") + "on the Wilderness Course.");
	}


	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 4;
	}

	@Override
	public int getLevel(final WorldObject object) {
		return 52;
	}

	@Override
	public int[] getObjectIds() {
		return new int[] { 23640 };
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		return 0;
	}

}
