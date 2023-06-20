package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.NotificationSettings;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Kris | 2. nov 2017 : 22:54.49
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class SlayerMonsterD extends Dialogue {

	public SlayerMonsterD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		npc("Take a look over your slayer kill counts.").executeAction(() -> {
			player.getNotificationSettings().sendKillLog(NotificationSettings.SLAYER_NPC_NAMES, true);
		});
	}

}
