package com.zenyte.game.world.entity.npc.drop.matrix;

import java.util.HashMap;

import com.zenyte.game.item.Item;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.ItemContainer;
import mgi.types.config.npcs.NPCDefinitions;
import mgi.types.config.items.ItemDefinitions;

public class DropPrediction implements Runnable {

	private Item[] loots = null;
	private final HashMap<Integer, Integer> rareDrops = new HashMap<Integer, Integer>();

	private void addItems(final Drop drop) {

		final Item item = ItemDefinitions.get(drop.getItemId()).isStackable()
				? new Item(drop.getItemId(), drop.getMinAmount() + getRandom(drop.getExtraAmount()))
				: new Item(drop.getItemId(), drop.getMinAmount() + getRandom(drop.getExtraAmount()));
		if (drop.getRate() <= 25) {
			rareDrops.put(item.getId(), 0);
		}
		for (int i = 0; i < loots.length; i++) {
			if (loots[i] != null && loots[i].getId() == item.getId()) {
				loots[i].setAmount(loots[i].getAmount() + item.getAmount());
				break;
			}
			if (loots[i] == null) {
				loots[i] = item;
				break;
			}
		}
		return;
	}
	
	public static final int getRandom(final int maxValue) {
		return (int) (Utils.randomDouble() * (maxValue + 1));
	}
	
	private final int npcId, amount;
	private final Player player;
	
	public DropPrediction(final Player player, final int npcId, final int amount) {
		this.npcId = npcId;
		this.amount = amount;
		this.player = player;
	}

	private void generateDrop(final Player player, final int npcId, final int amount) {
		final Drop[] drops = NPCDrops.getDrops(npcId);
		if (drops == null) {
			return;
		}
		final Drop[] possibleDrops = new Drop[drops.length];
		int possibleDropsCount = 0;
		for (final Drop drop : drops) {
			if (drop == null) {
				continue;
			}
			if (drop.getRate() == 100) {
				addItems(drop);
			} else {
				final double rate = drop.getRate();
				final double random = Utils.getRandomDouble(100);
				if (random <= rate && random != 100 && random != 0) {
					possibleDrops[possibleDropsCount++] = drop;
				}
			}
		}
		if (possibleDropsCount > 0) {
			addItems(possibleDrops[Utils.random(possibleDropsCount - 1)]);
		}
		return;
	}

	private void sendInterface(final Player player, final int npcId, final int amount) {
		player.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, 12);
		final ItemContainer items = new ItemContainer(loots.length, true);
		items.addAll(loots);
		player.getPacketDispatcher().sendUpdateItemContainer(95, -1, 0, items);
		//player.getPacketDispatcher().sendUpdateItemContainer(95, items);
		rareDrops.forEach((k, v) -> {
			double rate = 0.0000;
			for (final Item item : loots) {
				if (item != null && item.getDefinitions().getPrice() > 5000) {
					if (item.getId() == k) {
						rate = Utils.round(((double) item.getAmount() / amount) * 100, 4);
						player.sendMessage("<col=00ff00>The drop rate for "
								+ ItemDefinitions.get(k).getName() + ", based off of " + amount
								+ " kills is " + rate + "% (1:" + (amount / item.getAmount()) + ").");
					}
				}
			}

		});
	}

	@Override
	public void run() {
		loots = new Item[100];
		for (int i = 0; i < amount; i++) {
			generateDrop(player, npcId, amount);
		}
		long value = 0;
		for (final Item item : loots) {
			if (item == null) {
				continue;
			}
			value += item.getSellPrice() * item.getAmount();
		}
		
		String val;
		if (value < 100000) {
			val = "<col=ffff00>" + value + "</col>";
		} else if (value < 10000000) {
			val = "<col=ffffff>" + (value / 1000) + "K" + "</col>";
		} else {
			val = "<col=00ff80>" + (value / 1000000) + "M" + "</col>";
		}
		player.getPacketDispatcher().sendComponentText(12, 17, "Total value: " + val);
		player.getPacketDispatcher().sendComponentText(12, 22, "NPC: " + NPCDefinitions.get(npcId).getName() + " x " + amount);
		player.getPacketDispatcher().sendComponentText(12, 9, "800");
	/**	for (int i = 18; i < 27; i++) {
			if (i == 22) {
				continue;
			}
			player.getPacketDispatcher().sendComponentVisibility(12, i, true);
		}
		for (int i = 27; i < 35; i++) {
			player.getPacketDispatcher().sendComponentVisibility(12, i, true);
		}*/
		sendInterface(player, npcId, amount);
		return;
	}

}
