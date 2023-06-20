package com.zenyte.game.content.skills.farming;

import com.zenyte.game.content.skills.farming.plugins.FarmingStorageInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Kris | 04/02/2019 01:43
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class FarmingStorage {

    @Getter
    @Setter
    private List<Item> storage = new ArrayList<>();
    @Setter private transient Player player;

    FarmingStorage(final Player player) {
        this.player = player;
    }

    FarmingStorage(final Player player, final List<Item> storage) {
        this.player = player;
        this.storage = storage;
    }

    public Item getItem(@NotNull final Storable storable) {
        return Utils.findMatching(storage, s -> storable.equals(s.getId()));
    }

    private int getTotalAmount(@NotNull final Storable storable) {
        int count = 0;
        for (val stored : storage) {
            if (ArrayUtils.contains(storable.getItemIds(), stored.getId())) {
                count += stored.getAmount();
            }
        }
        return count;
    }

    public FarmingStorageInterface.Option getOption(final int option) {
        val list = new ArrayList<FarmingStorageInterface.Option>(6);
        val defaultOption = player.getNumericAttribute("farming equipment default").intValue();
        switch(defaultOption) {
            case 0:
                list.add(FarmingStorageInterface.Option.REMOVE_1);
                break;
            case 1:
                list.add(FarmingStorageInterface.Option.REMOVE_5);
                break;
            case 2:
                list.add(FarmingStorageInterface.Option.REMOVE_ALL);
                break;
            case 3:
                list.add(FarmingStorageInterface.Option.REMOVE_X);
                break;
            default:
                throw new IllegalStateException();
        }
        for (val op : FarmingStorageInterface.Option.values) {
            if (!list.contains(op)) {
                list.add(op);
            }
        }
        val map = new EnumMap<FarmingStorageInterface.Option, FarmingStorageInterface.Option>(FarmingStorageInterface.Option.class);
        for (int i = 0; i < FarmingStorageInterface.Option.values.length; i++) {
            val dynamicOption = list.get(i);
            val staticOption = FarmingStorageInterface.Option.values[i];
            map.put(staticOption, dynamicOption);
        }
        return Objects.requireNonNull(map.get(Objects.requireNonNull(Utils.findMatching(FarmingStorageInterface.Option.values,
                i -> i.getOptionId() == option))));
    }

    public void removeItem(final Storable storable, final Item item, final int amount) {
        if (item == null) {
            val itemName = ItemDefinitions.getOrThrow(storable.getBaseId()).getName().toLowerCase();
            val prefix = storable.getItemIds().length > 1 || storable.getMaximumAmount() > 1 ? "any" :
                    Utils.getAOrAn(itemName);
            player.sendMessage("You haven't got " + prefix + " " + itemName + " stored in here.");
            return;
        }
        //If requesting banknotes.
        if (amount == Integer.MIN_VALUE) {
            val result = player.getInventory().addItem(new Item(item.getDefinitions().getNotedOrDefault(), item.getAmount(), item.getAttributesCopy()));
            item.setAmount(item.getAmount() - result.getSucceededAmount());
            if (item.getAmount() <= 0) {
                storage.remove(item);
            }
            storable.refresh(player, item);
            return;
        }
        if (!player.getInventory().hasFreeSlots()) {
            player.sendMessage("You don't have room to hold that.");
            return;
        }
        val totalAmount = Math.min(amount, Math.min(player.getInventory().getFreeSlots(), item.getAmount()));
        player.getInventory().addItem(new Item(item.getId(), totalAmount, item.getAttributesCopy()))
                .onFailure(i -> World.spawnFloorItem(i, player));
        item.setAmount(item.getAmount() - totalAmount);
        if (item.getAmount() <= 0) {
            storage.remove(item);
        }
        storable.refresh(player, item);
    }

    public void addItem(final Storable storable, final int amount) {
        val storedItems = getStoredItems(storable);
        if (storedItems.isEmpty()) {
            val itemName = ItemDefinitions.getOrThrow(storable.getBaseId()).getName().toLowerCase();
            val anyPrefix = "any";
            val prefix = storable.getItemIds().length > 1 || storable.getMaximumAmount() > 1 ? anyPrefix :
                    Utils.getAOrAn(itemName);
            player.sendMessage("You haven't got " + prefix + " " + itemName + (prefix.equals(anyPrefix) ? "s" : "")
                    + " to store.");
            return;
        }
        val maximumAmount = storable.getMaximumAmount();
        if (getTotalAmount(storable) >= maximumAmount) {
            val amountPrefix = maximumAmount == 1 ? "one" : maximumAmount == 100 ? "a hundred" : "a thousand";
            val name = storable.toString().toLowerCase().replaceAll("_", " ");
            player.sendMessage("You cannot store more than " + amountPrefix + " "
                    + (storable == Storable.SECATEURS ? "pair of " : "") + name
                    + (maximumAmount == 1 ? "" : "s") + " in here.");
            return;
        }
        int remaining = amount;
        for (val storedItem : storedItems) {
            val existingItem = getItem(storable);
            if (storable == Storable.SECATEURS && existingItem != null && storedItem.getDefinitions().getUnnotedOrDefault() != existingItem.getDefinitions().getUnnotedOrDefault()) {
                player.sendMessage("You cannot mix different types of secateurs in the storage.");
                break;
            }
            val transferrable = getStorableAmount(storable, Math.min(remaining, storedItem.getAmount()));
            if (!transferrable.isPresent()) {
                break;
            }
            val transferrableAmount = transferrable.getAsInt();
            val id = storedItem.getDefinitions().getUnnotedOrDefault();
            Item deletedItem = new Item(storedItem.getId(), transferrableAmount);
            if (storable == Storable.BOTTOMLESS_BUCKET) {
                val inventory = player.getInventory();
                for (int i = 0; i < 28; i++) {
                    val it = inventory.getItem(i);
                    if (it != null && it.getId() == storedItem.getId()) {
                        deletedItem = it;
                        break;
                    }
                }
            }
            val attributes = player.getInventory().deleteItem(deletedItem).getItem().getAttributesCopy();
            val item = new Item(id, transferrableAmount, attributes);
            if (existingItem == null) {
                storage.add(item);
            } else {
                existingItem.setAmount(existingItem.getAmount() + transferrableAmount);
            }
            remaining -= transferrableAmount;
        }
        refresh();
    }

    private OptionalInt getStorableAmount(final Storable storable, final int requestedAmount) {
        if (requestedAmount <= 0) {
            return OptionalInt.empty();
        }
        val item = getItem(storable);
        if (item == null) {
            return OptionalInt.of(Math.min(storable.getMaximumAmount(), requestedAmount));
        }
        if (item.getAmount() >= storable.getMaximumAmount()) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(Math.min(storable.getMaximumAmount() - item.getAmount(), requestedAmount));
    }

    public void refresh() {
        for (val storable : Storable.values) {
            storable.refresh(player, getItem(storable));
        }
    }

    @NotNull
    public ArrayList<Item> getStoredItems(final Storable storable) {
        val ids = storable.getItemIds();
        val items = player.getInventory().getContainer().getItems().values();
        val list = new ArrayList<Item>();
        for (int i : ids) {
            for (val item : items) {
                if (item.getId() == i || item.getDefinitions().getNotedId() == i)
                    list.add(item);
            }
        }
        return list;
    }

}