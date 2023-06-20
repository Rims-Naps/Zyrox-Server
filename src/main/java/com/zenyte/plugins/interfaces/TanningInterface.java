package com.zenyte.plugins.interfaces;

import com.zenyte.game.world.entity.npc.NPC;
import org.apache.commons.lang3.ArrayUtils;

import com.zenyte.game.content.skills.crafting.CraftingDefinitions.Leather;
import com.zenyte.game.content.skills.crafting.actions.Tanning;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.ui.UserInterface;
import com.zenyte.game.util.TextUtils;
import com.zenyte.game.world.entity.player.Player;

import lombok.val;

/**
 * @author Tommeh | 15 mei 2018 | 21:08:00
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public final class TanningInterface implements UserInterface {

	@Override
	public void handleComponentClick(final Player player, final int interfaceId, final int componentId, final int slotId, final int itemId, final int optionId, final String option) {
		val leather = Leather.getLeatherByComponent(componentId);
		if (leather == null) {
			return;
		}
		val index = ArrayUtils.indexOf(leather.getComponents(), componentId);
		val amount = index == 3 ? 1 : index == 2 ? 5 : index == 0 ? 28 : -1;
		if (amount == -1) {
			player.sendInputInt("Enter amount:", amt -> player.getActionManager().setAction(new Tanning(leather, amt)));
		} else {
			player.getActionManager().setAction(new Tanning(leather, amount));
		}
	}

	public static void sendTanningInterface(final Player player) {
		val dispatcher = player.getPacketDispatcher();
		player.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, 324);
		for (int i = 0; i < Leather.VALUES.length; i++) {
			val leather = Leather.getLeather(i);
			dispatcher.sendComponentItem(324, 100 + i, leather.getBase().getId(), 250);
			dispatcher.sendComponentText(324, 108 + i, TextUtils.capitalizeFirstCharacter(leather.getName()));
			dispatcher.sendComponentText(324, 116 + i, leather.getCost() + " coins");
		}
	}

	@Override
	public int[] getInterfaceIds() {
		return new int[] { 324 };
	}

}
