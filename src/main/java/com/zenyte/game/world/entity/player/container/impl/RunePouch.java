package com.zenyte.game.world.entity.player.container.impl;

import com.zenyte.game.content.skills.magic.Rune;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import lombok.Getter;
import lombok.val;

import java.util.Optional;

/**
 * @author Tommeh | 26 mrt. 2018 : 18:08:22
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class RunePouch {

	public static final int INTERFACE = 190;
	public static final Item RUNE_POUCH = new Item(12791);
	public static final Item SECONDARY_RUNE_POUCH = new Item(30006);

	private final transient Player player;
	@Getter private final Container container;

	private final class RunePouchContainer extends Container {
        private RunePouchContainer(final ContainerPolicy policy, final ContainerType type, final Optional<Player> player) {
            super(policy, type, player);
        }

        @Override
        public void set(final int slot, final Item item) {
            super.set(slot, item);
            refresh();
        }

        @Override
        public void refresh(final Player player) {
            super.refresh(player);
            for (int i = 0; i < 3; i++) {
                val item = container.getItems().get(i);
                if (item == null) {
                    player.getVarManager().sendBit(getIdVarbit(i), 0);
                    player.getVarManager().sendBit(getAmountVarbit(i), 0);
                    continue;
                }
                val rune = Rune.getRune(item);
                if (rune == null) {
                    player.getVarManager().sendBit(getIdVarbit(i), 0);
                    player.getVarManager().sendBit(getAmountVarbit(i), 0);
                    continue;
                }
                player.getVarManager().sendBit(getIdVarbit(i), rune.ordinal() + 1);
                player.getVarManager().sendBit(getAmountVarbit(i), Math.min(16000, item.getAmount()));
            }
        }
    }

	public RunePouch(final Player player) {
		this.player = player;
		this.container = new RunePouchContainer(ContainerPolicy.ALWAYS_STACK, ContainerType.RUNE_POUCH, Optional.ofNullable(player));
	}

    public void switchItem(final int fromSlot, final int toSlot) {
        val fromItem = container.get(fromSlot);
        val toItem = container.get(toSlot);
        container.set(fromSlot, toItem);
        container.set(toSlot, fromItem);
        container.setFullUpdate(true);
        container.refresh(player);
    }

	public final void initialize(final RunePouch pouch) {
		if (pouch == null || pouch.container == null) {
			return;
		}
		this.container.setContainer(pouch.container);
	}

	private int getIdVarbit(final int slot) {
		return slot == 0 ? 29 : slot == 1 ? 1622 : slot == 2 ? 1623 : -1;
	}

	private int getAmountVarbit(final int slot) {
		return 1624 + slot;
	}

	public Item getRune(final int slot) {
		return container.get(slot);
	}

	public int getSlot(final int id) {
		return container.getSlotOf(id);
	}

	public int getAmountOf(final int id) {
		if (!player.getInventory().containsItem(RUNE_POUCH) && !player.getInventory().containsItem(SECONDARY_RUNE_POUCH)) {
			return 0;
		}
		return container.getAmountOf(id);
	}

	/*public void add(final Item item) {
		final Rune rune = Rune.getRune(item);
		final int slot = getSlot(rune);
		int amount;
		item.setAmount(Math.min(item.getAmount(), MAX_TOTAL_SIZE));
		if (rune == null) {
			player.sendMessage("You can only enqueue runes to the rune pouch.");
			return;
		}
		for (final Item r : items) {
			if (r == null) {
				continue;
			}
			if (r.getId() == rune.getId()) {
				if (r.getAmount() >= MAX_RUNE_SIZE) {
					player.sendMessage("You can't enqueue any more runes of this type to your pouch.");
					return;
				}
			}
		}
		if (getSize() >= MAX_TOTAL_SIZE) {
			player.sendMessage("You can't enqueue any more runes to your pouch.");
			return;
		}
		if ((item.getAmount() + getAmountOf(rune.getId())) >= MAX_RUNE_SIZE) {
			if (player.getInventory().containsItem(item)) {
				item.setAmount(MAX_RUNE_SIZE - getAmountOf(rune.getId()));
			}
		}
		if (item.getAmount() < 1) {
			return;
		}
		if (slot != -1) {
			final Item r = items[slot];
			amount = r.getAmount() + item.getAmount();
			setRune(slot, new Item(r.getId(), amount));
		} else {
			final int freeSlot = getFreeSlot();
			if (freeSlot == -1) {
				player.sendMessage("A rune pouch can only hold 3 types of runes at once.");
				return;
			} else {
				setRune(freeSlot, item);
			}
		}
		player.getInventory().deleteItem(new Item(item.getId(), item.getAmount()));
	}         */

	/*public void remove(final int slot, int amount, final boolean add) {
		final Item rune = items[slot];
		if (rune == null) {
			setRune(slot, null);
			return;
		}

		final Inventory inventory = player.getInventory();
		final int id = rune.getId();
		final int inInventory = inventory.getAmountOf(id);
		if (inInventory + amount < 0) {
			amount = Integer.MAX_VALUE - inInventory;
			player.sendMessage("You are holding too many items in both your inventory and rune pouch.");
		}
		setRune(slot, new Item(id, rune.getAmount() - amount));
		if (add) {
			final ContainerResult containerResult = inventory.addItem(new Item(id, amount));
			if (containerResult.getResult() == RequestResult.NOT_ENOUGH_SPACE) {
				player.sendMessage("You don't have enough space inventory space.");
			}
		}
	}             */

	public void clear() {
	    container.clear();
        player.getInventory().refreshAll();
        container.refresh(player);
    }

	public void emptyRunePouch() {
		for (int slot = 0; slot <= 2; slot++) {
			final Item rune = container.get(slot);
			if (rune == null || rune.getAmount() <= 0) {
				continue;
			}
			player.getInventory().getContainer().deposit(null, container, slot, rune.getAmount());
		}
		if (!container.isEmpty()) {
		    player.sendMessage("Not enough space in your inventory.");
        }
		player.getInventory().refreshAll();
		container.refresh(player);
	}

}
