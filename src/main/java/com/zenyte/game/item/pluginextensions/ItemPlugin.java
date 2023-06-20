package com.zenyte.game.item.pluginextensions;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemActionHandler;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import mgi.types.config.items.ItemDefinitions;
import lombok.NonNull;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kris | 11. nov 2017 : 16:15.02
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public abstract class ItemPlugin {

	@NonNull
	public static ItemPlugin getPlugin(final int id) {
		return Utils.getOrDefault(ItemActionHandler.INT_ACTIONS.get(id), ItemActionHandler.DEFAULT_ACTION);
	}

	private final Map<String, OptionHandler> delegatedInventoryHandlers = new HashMap<>(3);

	public abstract void handle();

	public void bind(final String option, final OptionHandler handler) {
	    verifyIfOptionExists(option);
	    delegatedInventoryHandlers.put(option.toLowerCase(), handler);
	}

    public void bind(final String option, final BasicOptionHandler handler) {
	    verifyIfOptionExists(option);
	    delegatedInventoryHandlers.put(option.toLowerCase(), handler);
    }

	public OptionHandler getHandler(final String option) {
		return delegatedInventoryHandlers.get(option.toLowerCase());
	}


	public void setDefaultHandlers() {
		setDefault("Drop", (player, item, slotId) -> ItemActionHandler.dropItem(player, "Drop", slotId, 300, 500));
		setDefault("Destroy", (player, item, slotId) -> ItemActionHandler.dropItem(player, "Destroy", slotId, 300, 500));
		setDefault("Check", (player, item, slotId) -> player.getChargesManager().checkCharges(item));
		setDefault("Wear", (player, item, slotId) -> player.getEquipment().wear(slotId));
		setDefault("Wield", (player, item, slotId) -> player.getEquipment().wear(slotId));
		setDefault("Equip", (player, item, slotId) -> player.getEquipment().wear(slotId));
		//setDefault("Hold", (player, item, slotId) -> player.getEquipment().wear(slotId));

		setDefault("Remove", (player, item, slotId) -> {
			player.stopAll(false, !player.getInterfaceHandler().isVisible(GameInterface.EQUIPMENT_STATS.getId()), slotId == EquipmentSlot.WEAPON.getSlot());
			player.getEquipment().unequipItem(slotId);
		});
	}

	protected void setDefault(final String option, final BasicOptionHandler handler) {
		val lowercase = option.toLowerCase();
		if (delegatedInventoryHandlers.containsKey(lowercase)) {
			return;
		}
        delegatedInventoryHandlers.put(lowercase, handler);
	}

	private void verifyIfOptionExists(final String option) {
		ItemDefinitions def = null;
		for (val id : getItems()) {
			val definitions = ItemDefinitions.get(id);
			if (definitions == null) {
				continue;
			}
			if (definitions.containsOption(option) || definitions.containsParamByValue(option)) {
				return;
			}
			def = definitions;
		}
		throw new RuntimeException("None of the items enlisted in " + getClass().getSimpleName() + " contains option " + option + " - " + def.getParameters());
	}

	public abstract int[] getItems();


	@FunctionalInterface
	public interface BasicOptionHandler extends OptionHandler {

	    void handle(final Player player, final Item item, int slotId);

        @Override
        @Deprecated
        default void handle(final Player player, final Item item, final Container container, int slotId) {
            handle(player, item, slotId);
        }

	}

    @FunctionalInterface
    public interface OptionHandler {
        void handle(final Player player, final Item item, final Container container, int slotId);
    }

}
