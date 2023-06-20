package com.zenyte.plugins.interfaces;

import com.zenyte.game.content.skills.smithing.Smithing;
import com.zenyte.game.item.Item;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.ui.UserInterface;
import com.zenyte.game.util.Examine;
import com.zenyte.game.world.entity.player.Player;

import lombok.val;

/**
 * @author Kris | 10. nov 2017 : 19:29.13
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class SmithingInterface implements UserInterface {

	public static final int INTERFACE = 312;

	public static int getTierForBar(final Item item) {
		val id = item.getId();
		return id == 2349 ? 1 : id == 2351 ? 2 : id == 2353 ? 3 : id == 2359 ? 4 : id == 2361 ? 5 : id == 2363 ? 6 : id == 9467 ? 7 : 0;
	}

	public static void openInterface(final Player player, final int tier, final int objectId) {
		player.getVarManager().sendVar(210, tier);
		player.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, INTERFACE);
		player.getTemporaryAttributes().put("SmithingTier", tier);
		player.getTemporaryAttributes().put("AnvilObjectId", objectId);
	}
	
	@Override
	public void handleComponentClick(final Player player, final int interfaceId, final int componentId, final int slotId, final int itemId, final int optionId, final String option) {
		val tier = player.getNumericTemporaryAttribute("SmithingTier").intValue();
		val objectId = player.getNumericTemporaryAttribute("AnvilObjectId").intValue();
		val amount = optionId == 1 ? 1 : optionId == 2 ? 5: optionId == 3 ? 10 : optionId == 5 ? 28 : 0;
		if (optionId == 4) {
			player.sendInputInt("Enter amount:", value -> player.getActionManager().setAction(new Smithing(objectId, tier - 1, componentId - 2, value)));
		} else if (optionId == 10) {
			Examine.sendItemExamine(player, Smithing.PRODUCTS[tier - 1][componentId - 2].getId());
		} else {
			player.getActionManager().setAction(new Smithing(objectId, tier - 1, componentId - 2, amount));
		}	

	}

	@Override
	public int[] getInterfaceIds() {
		return new int[] { INTERFACE };
	}

}
