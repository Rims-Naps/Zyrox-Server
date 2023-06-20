package com.zenyte.game.world.entity.player.container;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import lombok.Getter;
import lombok.val;
import lombok.var;

import java.util.Collection;

/**
 * @author Kris | 25. aug 2018 : 17:12:46
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public abstract class ContainerWrapper {

	protected transient Player player;
	@Getter
	protected Container container;

	public ContainerResult addItem(final int id, final int amount) {
		return addItem(id, amount, 0);
	}

	public ContainerResult addItem(final int id, final int amount, final int charges) {
		return addItem(new Item(id, amount, charges));
	}

	public ContainerResult addItem(final Item item) {
		val result = container.add(item);
		refresh();
		return result;
	}

	public ContainerResult addOrDrop(final int itemId) {
		return addOrDrop(new Item(itemId, 1));
	}

	public ContainerResult addOrDrop(final Item item) {
		val result = container.add(item);
		result.onFailure(res -> World.spawnFloorItem(res, player));
		refresh();
		return result;
	}

	public ContainerResult addOrDrop(final int id, final int amount) {
		val result = container.add(new Item(id, amount));
		result.onFailure(res -> World.spawnFloorItem(res, player));
		refresh();
		return result;
	}

	public MultiResult addOrDrop(final Item... items) {
		val result = container.add(items);
		result.onFailure(res -> World.spawnFloorItem(res, player));
		refresh();
		return result;
	}
	
	public MultiResult addItems(final Item... items) {
		val result = container.add(items);
		refresh();
		return result;
	}

	public ContainerResult deleteItem(final int id, final int amount) {
		return deleteItem(new Item(id, amount));
	}

	public ContainerResult deleteItem(final Item item) {
		val result = container.remove(item);
		refresh();
		return result;
	}
	
	public MultiResult deleteItems(final Item... items) {
		val result = container.remove(items);
		refresh();
		return result;
	}

	public ContainerResult deleteItem(final int slot, final Item item) {
		val result = slot < 0 || slot >= 28 ? container.remove(item) : container.remove(slot, item.getAmount());
		refresh();
		return result;
	}

	public void deleteItemsIfContains(final Collection<Integer> items, final Runnable onSuccess) {
		for (val item : items) {
			if (!container.contains(item, 1)) {
				return;
			}
		}
		for (val item : items) {
			container.remove(container.getSlotOf(item), 1);
		}
		if (onSuccess != null) {
			onSuccess.run();
		}
		this.refresh();
	}

	public void deleteItemsIfContains(final Item[] items, final Runnable onSuccess) {
		for (val item : items) {
			if (!container.contains(item)) {
				return;
			}
		}
		for (val item : items) {
			container.remove(item);
		}
		if (onSuccess != null) {
			onSuccess.run();
		}
		this.refresh();
	}

	public void deleteItemsByAmountIfContains(final Item[] items, final Runnable onSuccess) {
		for (val item : items) {
			if (container.getAmountOf(item.getId()) < item.getAmount()) {
				return;
			}
		}
		for (val item : items) {
			val slots = container.getSlotsOf(item.getId());
			if (slots == null) {
				continue;
			}
			var deletedAmount = 0;
			for (int slot : slots) {
				val amountToDelete = Math.min(item.getAmount() - deletedAmount, container.get(slot).getAmount());
				container.remove(slot, amountToDelete);
				deletedAmount += amountToDelete;
			}
		}
		if (onSuccess != null) {
			onSuccess.run();
		}
		this.refresh();
	}

    public void ifDeleteItem(final Item item, final Runnable onSuccess) {
        if (!container.contains(item)) {
            return;
        }
        container.remove(item);
        if (onSuccess != null) {
            onSuccess.run();
        }
        this.refresh();
    }

	public void replaceItem(final int id, final int amount, final int slot) {
		val item = container.get(slot);
		if (item == null) {
			return;
		}
		container.set(slot, id == -1 ? null : new Item(id, amount));
		refresh();
	}

	public void clear() {
		container.clear();
		refresh();
	}

	public boolean hasSpaceFor(final int... items) {
		var spaceRequired = 0;
		for (final int itemId : items) {
			if (!containsItem(itemId, 1)) {
				spaceRequired++;
			}
		}
		return getFreeSlots() >= spaceRequired;
	}

	public boolean hasSpaceFor(final Item... items) {
		var spaceRequired = 0;
		for (final Item item : items) {
			if (!containsItem(item.getId(), 1)) {
				spaceRequired++;
			}
		}
		return getFreeSlots() >= spaceRequired;
	}

	public boolean hasFreeSlots() {
		return container.getFreeSlotsSize() > 0;
	}

	public int getFreeSlots() {
		return container.getFreeSlotsSize();
	}

	public int getAmountOf(final int itemId) {
		return container.getAmountOf(itemId);
	}
	
	public long getAmountOf(final int... itemIds) {
		long amount = 0;
		for (int i = 0; i < itemIds.length; i++) {
			amount += container.getAmountOf(itemIds[i]);
		}
		return amount;
	}

	public Item getItem(final int slot) {
		return container.get(slot);
	}

	public Item getAny(final int... items) {
		for (int itemId : items) {
			val item = container.getFirst(itemId);
			if (item != null) {
				return item.getValue();
			}
		}
		return null;
	}

	public void set(final int slot, final Item item) {
		container.set(slot, item);
		refresh();
	}
	
    public void switchItem(final int fromSlot, final int toSlot) {
        val fromItem = container.get(fromSlot);
        val toItem = container.get(toSlot);
        container.set(fromSlot, toItem);
        container.set(toSlot, fromItem);
        container.setFullUpdate(true);
        refresh();
    }

	public boolean containsItems(final Collection<Item> items) {
		for (val item : items) {
			if (!container.contains(item)) {
				return false;
			}
		}
		return true;
	}

	public boolean containsAll(final Collection<Integer> items) {
		for (val itemId : items) {
			if (!containsItem(itemId, 1)) {
				return false;
			}
		}
		return true;
	}

	public boolean containsItems(final Item... item) {
		for (int i = 0; i < item.length; i++) {
			if (!container.contains(item[i])) {
				return false;
			}
		}
		return true;
	}

	public boolean containsItem(final int itemId, final int amount) {
		return containsItem(new Item(itemId, amount));
	}

	public boolean containsItem(final int itemId) {
		return containsItem(itemId, 1);
	}

	public boolean containsItem(final Item item) {
		return container.contains(item);
	}

	public boolean containsAnyOf(final int... ids) {
		for (val id : ids) {
			if (container.contains(id, 1))
				return true;
		}
	    return false;
    }
	public boolean containsAnyOf(final Item... items) {
		for (val item : items) {
			if (container.contains(item))
				return true;
		}
		return false;
	}
	public void refresh() {
		container.refresh(player);
	}

	public void refresh(final int... slots) {
		val set = container.getModifiedSlots();
		for (final int i : slots) {
			set.add(i);
		}
		container.refresh(player);
	}

	public void refreshAll() {
		container.setFullUpdate(true);
		container.refresh(player);
	}

	public float getWeight() {
		return container.getWeight();
	}
}
