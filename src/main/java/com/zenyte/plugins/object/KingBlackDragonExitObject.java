package com.zenyte.plugins.object;

import com.zenyte.game.content.boss.kingblackdragon.KingBlackDragonInstance;
import com.zenyte.game.content.skills.magic.spells.teleports.structures.LeverTeleport;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Tommeh | 29 mei 2018 | 21:00:05
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class KingBlackDragonExitObject implements ObjectAction {

	@Override
	public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        new LeverTeleport(KingBlackDragonInstance.outsideTile, object, "... and teleport out of the Dragon's lair.", null).teleport(player);
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 1817 };
	}

}
