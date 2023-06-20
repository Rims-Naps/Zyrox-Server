package com.zenyte.plugins.equipment;

import com.zenyte.game.item.pluginextensions.ItemPlugin.BasicOptionHandler;
import mgi.types.config.items.ItemDefinitions;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kris | 11. nov 2017 : 16:15.02
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public abstract class EquipmentPlugin {

	private final Map<String, BasicOptionHandler> delegatedHandlers = new HashMap<String, BasicOptionHandler>(3);

	public abstract void handle();

	public void bind(final String option, final BasicOptionHandler handler) {
		verifyIfOptionExists(option);
		delegatedHandlers.put(option.toLowerCase(), handler);
	}

	public BasicOptionHandler getHandler(final String option) {
		return delegatedHandlers.get(option.toLowerCase());
	}

	public final void setDefaultHandlers() {
		setDefault("Check", (player, item, slotId) -> player.getChargesManager().checkCharges(item));
		setDefault("Remove", (player, item, slotId) -> {
			player.stopAll(false, true, true);
			player.getEquipment().unequipItem(slotId);
		});
	}

	private final void setDefault(final String option, final BasicOptionHandler handler) {
		val lowercase = option.toLowerCase();
		if (delegatedHandlers.containsKey(lowercase)) {
			return;
		}
		delegatedHandlers.put(lowercase, handler);
	}

	private void verifyIfOptionExists(final String option) {
		for (val id : getItems()) {
			val definitions = ItemDefinitions.get(id);
			if (definitions == null) {
				continue;
			}
			val params = definitions.getParameters();
			if (params.containsValue(option)) {
				return;
			}
		}
		throw new RuntimeException("None of the items enlisted in " + getClass().getSimpleName() + " contains option " + option + ".");
	}

	public abstract int[] getItems();

}
