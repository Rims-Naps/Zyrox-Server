package com.zenyte.plugins.object;

import com.zenyte.game.content.achievementdiary.diaries.DesertDiary;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.GlobalAreaManager;
import lombok.val;

/**
 * @author Tommeh | 7 apr. 2018 | 22:14:06
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class KalphiteHiveObject implements ObjectAction {
	
	private static final Location INSIDE_DUNGEON_LOCATION = new Location(3305, 9497, 0);
	private static final Location OUTSIDE_DUNGEON_LOCATION = new Location(3321, 3122, 0);
	
	private static final Location INSIDE_LAIR_LOCATION = new Location(3484, 9510, 2);
	private static final Location OUTSIDE_LAIR_LOCATION = new Location(3226, 3109, 0);
	private static final Location INSIDE_BOSS_LOCATION = new Location(3508, 9494, 0);
	private static final Location OUTSIDE_BOSS_LOCATION = new Location(3509, 9496, 2);

	@Override
	public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
		if (object.getId() == 3827) {
			player.getAchievementDiaries().update(DesertDiary.ENTER_KALPHITE_HIVE);
			player.useStairs(828, INSIDE_LAIR_LOCATION, 1, 2);
		} else if (object.getId() == 3829) {
			player.useStairs(828, OUTSIDE_LAIR_LOCATION, 1, 2);
		} else if (object.getId() == 3832)  {
			player.useStairs(828, OUTSIDE_BOSS_LOCATION, 1, 2);
		} else if (object.getId() == 23609) {
			if (optionId == 1) {
				player.setLocation(INSIDE_BOSS_LOCATION);
			} else if (optionId == 2) {
				player.sendMessage("You peek through the crack...");
				WorldTasksManager.schedule(() -> {
                    val playerCount = GlobalAreaManager.get("Kalphite Queen Lair").getPlayers().size();
					player.sendMessage("Standard cave: " + (playerCount == 0 ? "No adventurers." : playerCount + (playerCount == 1 ? " adventurer." : " adventurers.")));
				}, 2);
			}
		} else if (object.getId() == 26712) {
			player.setLocation(OUTSIDE_DUNGEON_LOCATION);
		} else if (object.getId() == 30180) {
			player.setLocation(INSIDE_DUNGEON_LOCATION);
		}
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 3827, 3828, 3829, 3831, 3832, 23609, 26712, 30180 };
	}

}
