package com.zenyte.game.world.entity.player.container.impl;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.TextUtils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import lombok.Getter;
import lombok.val;

import java.util.Optional;

public class PriceChecker {

	public static final int SIZE = 28;
	public static final int INTERFACE = 464;
	public static final int INVENTORY_INTERFACE = 238;

	@Getter private final transient Container container;

	private final transient Player player;

	public PriceChecker(final Player player) {
		this.player = player;
		container = new Container(ContainerPolicy.NORMAL, ContainerType.PRICE_CHECKER, Optional.of(player));
	}

	public void openPriceChecker() {
        GameInterface.PRICE_CHECKER.open(player);
	}

	public void deposit(final Player player, final Container container, final int slotId, final int amount) {
	    if (!player.getInterfaceHandler().isPresent(GameInterface.PRICE_CHECKER)) {
	        return;
        }
		if (!container.get(slotId).isTradable()) {
		    player.sendMessage("You can't trade that.");
			return;
		}
		this.container.deposit(player, container, slotId, amount);
		this.container.refresh(this.player);
		container.refresh(this.player);
		refreshPrices();
	}

	public void withdraw(final Player player, final Container container, final int slot, final int amount) {
        if (!player.getInterfaceHandler().isPresent(GameInterface.PRICE_CHECKER)) {
            return;
        }
		this.container.withdraw(player, container, slot, amount);
		this.container.refresh(this.player);
		container.refresh(this.player);
		refreshPrices();
	}

	public Item get(final int slot) {
		return container.get(slot);
	}

	private void refreshPrices() {
		long totalValue = 0;
		val params = new Object[container.getContainerSize()];
		for (int i = 0; i < params.length; i++) {
			val item = container.get(i);
			if (item == null) {
				params[i] = 0;
				continue;
			}
			val valueOfStack = item.getSellPrice();
			params[i] = item.getId() == 995 ? 1 : valueOfStack;
			totalValue += item.getSellPrice() * item.getAmount();
		}
        val totalVal = totalValue;
        GameInterface.PRICE_CHECKER.getPlugin().ifPresent(plugin -> {
            val dispatcher = player.getPacketDispatcher();
            dispatcher.sendClientScript(785, params);
            dispatcher.sendClientScript(600, 1, 1, 15, (plugin.getInterface().getId() << 16) | (plugin.getComponent("Item name in search")));
            dispatcher.sendComponentItem(INTERFACE, plugin.getComponent("Item sprite in search"), -1, 1);
            dispatcher.sendComponentText(INTERFACE, plugin.getComponent("Item name in search"), "Total guide price:<br>"
                    + (totalVal > Integer.MAX_VALUE ? "<col=ED1509>Lots!" : "<col=ffffff>" + TextUtils.formatCurrency((int) totalVal))
                    + "</col>");
        });
	}

	public void returnItems() {
		val size = container.getContainerSize();
		val inventory = player.getInventory().getContainer();
		for (int i = 0; i < size; i++) {
			val item = get(i);
			if (item == null) {
				continue;
			}
			inventory.add(item).onFailure(it -> {
				player.sendMessage(Colour.RED.toString() + it.getAmount() + " x " + it.getName()
						+ " have been dropped on the ground due to lack of space!");
				World.spawnFloorItem(it, player);
			});
		}
		container.clear();
		inventory.refresh(player);
	}

}
