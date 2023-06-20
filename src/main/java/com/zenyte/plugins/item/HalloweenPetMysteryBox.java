package com.zenyte.plugins.item;

import com.zenyte.game.content.follower.impl.MiscPet;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.Inventory;
import com.zenyte.plugins.dialogue.ItemChat;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public class HalloweenPetMysteryBox extends ItemPlugin {

    private final IntSet availablePets = new IntOpenHashSet();

    public HalloweenPetMysteryBox() {
        availablePets.add(MiscPet.SPOOKY_MONKEY.getItemId());
        availablePets.add(MiscPet.SPOOKY_BAT.getItemId());
        availablePets.add(MiscPet.SPOOKY_WITCH.getItemId());
        availablePets.add(MiscPet.SPOOKY_COW.getItemId());
        availablePets.add(MiscPet.SPOOKY_DRACULA.getItemId());
        availablePets.add(MiscPet.SPOOKY_CHICKEN.getItemId());
        availablePets.add(MiscPet.SPOOKY_GHOST.getItemId());
        availablePets.add(MiscPet.SPOOKY_KILLER.getItemId());
        availablePets.add(MiscPet.SPOOKY_ZOMBIE.getItemId());
    }


    @Override
    public void handle() {
        bind("Open", (player, item, container, slotId) -> {
            IntSet availablePets = new IntOpenHashSet();
            for(int id : this.availablePets) {
                if(possessesItem(player, id)) {
                    continue;
                }
                availablePets.add(id);
            }
            if(availablePets.isEmpty()) {
                player.sendMessage("You already possess all of the Halloween pets!");
                return;
            }
            int pet = Utils.getRandomCollectionElement(availablePets);
            Inventory inventory = player.getInventory();
            inventory.deleteItem(slotId, item);
            Item petItem = new Item(pet);
            inventory.addOrDrop(petItem);
            player.getDialogueManager().start(new ItemChat(player, petItem, "You find a " + petItem.getName().toLowerCase() + " from the Halloween pet mystery box!"));
        });
    }

    private boolean possessesItem(@NotNull final Player player, final int id) {
        val follower = player.getFollower();
        val pet = follower == null ? null : follower.getPet();
        return (pet != null && pet.itemId() == id) || player.containsItem(id);
    }

    @Override
    public int[] getItems() {
        return new int[] { 30081 };
    }
}
