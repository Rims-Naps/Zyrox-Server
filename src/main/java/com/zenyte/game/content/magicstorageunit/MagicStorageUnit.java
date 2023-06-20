package com.zenyte.game.content.magicstorageunit;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.events.InitializationEvent;
import com.zenyte.plugins.events.LoginEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import mgi.types.config.enums.Enums;
import mgi.types.config.items.ItemDefinitions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * @author Kris | 15/09/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class MagicStorageUnit {

    public static final int STORAGE_UNIT_UIM_UNLOCK_COST = 1_000_000;
    public static final int STORAGE_UNIT_NORMAL_UNLOCK_COST = 2_500_000;

    private final Map<Integer, List<Integer>> storedSets;
    @Getter @Setter private int unlockPayment;

    public MagicStorageUnit() {
        this.storedSets = new Int2ObjectOpenHashMap<>();
    }

    @Subscribe
    public static final void onInitialization(final InitializationEvent event) {
        val player = event.getPlayer();
        val savedPlayer = event.getSavedPlayer();
        val storageUnit = savedPlayer.getMagicStorageUnit();
        if (storageUnit != null && storageUnit.storedSets != null) {
            val unit = player.getMagicStorageUnit();
            unit.storedSets.putAll(storageUnit.storedSets);
            unit.unlockPayment = storageUnit.unlockPayment;
        }
    }

    @Subscribe
    public static final void onLogin(final LoginEvent event) {
        event.getPlayer().getMagicStorageUnit().refeshVarbit(event.getPlayer());
    }

    public void refeshVarbit(@NotNull final Player player) {
        player.getVarManager().sendBit(16001, unlockPayment == 0 ? 0 : 1);
    }

    public List<Integer> getStoredItemIds(final int displayedItem) {
        return storedSets.get(displayedItem);
    }

    public void remove(final int displayedItem) {
        storedSets.remove(displayedItem);
    }

    public void store(@NotNull final Player player, final int itemId) {
        val optionalSets = StorageUnitCollection.getSingleton().findElement(itemId);
        if (!optionalSets.isPresent()) {
            throw new IllegalStateException();
        }
        val sets = optionalSets.get();
        for (val set : sets) {
            val list = buildHeldItemsList(player, set);
            if (list == null) {
                continue;
            }
            if (storedSets.get(set.getDisplayItem()) != null) {
                player.sendMessage("You've already stored that item set in your magic storage unit.");
                return;
            }
            storedSets.put(set.getDisplayItem(), list);
            val storageEnum = Enums.COSTUME_STORAGE_UNIT_ENUM;
            val name = storageEnum.getValue(set.getDisplayItem()).orElseThrow(RuntimeException::new);
            player.sendMessage("You add the " + name + " to the magic storage unit.");
            val inventory = player.getInventory();
            for (val id : list) {
                inventory.deleteItem(id, 1);
            }
            return;
        }
        notifyImpartialCollection(player, sets.get(0));
    }

    private void notifyImpartialCollection(@NotNull final Player player, final StorageUnitElement element) {
        val storageEnum = Enums.COSTUME_STORAGE_UNIT_ENUM;
        val name = storageEnum.getValue(element.getDisplayItem()).orElseThrow(RuntimeException::new);
        val inventory = player.getInventory();
        val singular = element.singular();
        player.sendMessage("You need the following items to add the " + name + " in your magic storage unit:");
        for (val piece : element.getPieces()) {
            final int[] ids = piece.getIds();
            if (ids.length == 1 || singular || ids.length > 2) {
                val id = ids[0];
                val colourPrefix = inventory.containsItem(id, 1) ? Colour.GREEN : Colour.RED;
                player.sendMessage(colourPrefix.wrap("1 x " + ItemDefinitions.nameOf(id)));
            } else if (ids.length == 2){
                val firstId = ids[0];
                val secondId = ids[1];
                val firstColourPrefix = inventory.containsItem(firstId, 1) ? Colour.GREEN : Colour.RED;
                val secondColourPrefix = inventory.containsItem(secondId, 1) ? Colour.GREEN : Colour.RED;
                player.sendMessage(firstColourPrefix.wrap("1 x " + ItemDefinitions.nameOf(firstId)) + " or " + secondColourPrefix.wrap("1 x " + ItemDefinitions.nameOf(secondId)));
            }
        }
    }

    private IntList buildHeldItemsList(@NotNull final Player player, @NotNull final StorageUnitElement element) {
        val list = new IntArrayList();
        val inventory = player.getInventory();
        for (val piece : element.getPieces()) {
            val ids = piece.getIds();
            int containedCount = 0;
            for (int id : ids) {
                if (inventory.containsItem(id, 1)) {
                    containedCount++;
                    list.add(id);
                }
            }
            if (containedCount == 0) {
                return null;
            }
        }
        return list;
    }

}
