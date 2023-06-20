package com.zenyte.plugins.object;

import com.zenyte.game.content.achievementdiary.diaries.*;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentUtils;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 28. apr 2018 : 16:21.37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 *      profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 *      profile</a>}
 */
public final class AltarOPlugin implements ObjectAction {

	public static final Animation PRAY_ANIM = new Animation(645);

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
		if (option.equals("Pray-at") || option.equals("Pray")) {
			if (EquipmentUtils.containsFullInitiate(player) && object.getId() == 410) {
				player.getAchievementDiaries().update(FaladorDiary.PRAY_ALTAR_OF_GUTHIX);
			} else if (EquipmentUtils.containsFullProselyte(player)) {
				player.getAchievementDiaries().update(FaladorDiary.RECHARGE_PRAYER);
			} else if (object.getId() == 10389) {
				player.getAchievementDiaries().update(DesertDiary.PRAY_AT_ELIDINIS_STATUETTE);
			} else if (player.getPrayerManager().isActive(Prayer.SMITE)) {
				player.getAchievementDiaries().update(VarrockDiary.PRAY_AT_VARROCK_ALTAR);
				player.getAchievementDiaries().update(LumbridgeDiary.RECHARGE_PRAYER);
			}
			player.getAchievementDiaries().update(WildernessDiary.PRAY_AT_CHAOS_ALTAR);
			player.getAchievementDiaries().update(ArdougneDiary.USE_EAST_ARDOUGNE_ALTAR);
			if (player.getPrayerManager().getPrayerPoints() >= player.getSkills().getLevelForXp(Skills.PRAYER)) {
				player.sendMessage("You already have full prayer points.");
				return;
			}
			val toRestore = player.getSkills().getLevelForXp(Skills.PRAYER) - player.getPrayerManager().getPrayerPoints();
			if (toRestore >= 85 && object.getId() == 20377) {
				player.getAchievementDiaries().update(DesertDiary.RESTORE_85_PRAYER_POINTS);
			}
			player.lock();
			player.sendMessage("You pray to the gods...");
			player.setAnimation(PRAY_ANIM);
			player.sendSound(2674);
			WorldTasksManager.schedule(() -> {
				player.getPrayerManager().restorePrayerPoints(99);
				player.sendMessage("... and recharge your prayer.");
				player.unlock();
			});
		}
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { "Altar of Guthix", "Chaos altar", "Altar", 20377 };
	}

}
