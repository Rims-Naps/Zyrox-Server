package com.zenyte.plugins.dialogue;

import com.zenyte.game.constants.GameConstants;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.partyroom.FaladorPartyRoom;
import com.zenyte.game.content.skills.magic.spells.teleports.TeleportCollection;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Tommeh | 27 mei 2018 | 15:15:57
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public final class FrejaD extends Dialogue {

	public FrejaD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		npc("Coming soon..");
	}
}
