package com.zenyte.plugins.item;

import com.zenyte.game.content.follower.impl.MiscPet;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.ItemChat;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 04/02/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PetMysteryBox extends ItemPlugin {

    private final IntSet availablePets = new IntOpenHashSet();
    private final IntSet availableDragons = new IntOpenHashSet();

    public PetMysteryBox() {
        for (val pet : MiscPet.values()) {
            if (pet.ordinal() < MiscPet.SPIRIT_KALPHITE.ordinal() || pet.ordinal() > MiscPet.CHAMELEON_10.ordinal()) {
                continue;
            }
           if (pet == MiscPet.BLACK_BABYDRAGON || pet == MiscPet.BLUE_BABYDRAGON || pet == MiscPet.GREEN_BABYDRAGON || pet == MiscPet.RED_BABYDRAGON) {
               availableDragons.add(pet.getItemId());
           } else {
               availablePets.add(pet.getItemId());
           }
        }
    }

    @Override
    public void handle() {
        bind("Open", (player, item, container, slotId) -> {
            val availableDragons = new IntOpenHashSet();
            for (val dragon : this.availableDragons) {
                if (possessesItem(player, dragon)) {
                    continue;
                }
                availableDragons.add(dragon.intValue());
            }
            val availablePets = new IntOpenHashSet();
            for (val pet : this.availablePets) {
                if (possessesItem(player, pet)) {
                    continue;
                }
                availablePets.add(pet.intValue());
            }
            if (availableDragons.isEmpty() && availablePets.isEmpty()) {
                player.sendMessage("You already possess all the pets you can possibly find from the pet mystery box.");
                return;
            }
            val pet = (!availableDragons.isEmpty() && (Utils.random(24) == 0 || availablePets.isEmpty()))
                    ? Utils.getRandomCollectionElement(availableDragons) : Utils.getRandomCollectionElement(availablePets);
            val inventory = player.getInventory();
            inventory.deleteItem(slotId, item);
            val petItem = new Item(pet);
            inventory.addOrDrop(petItem);
            player.getDialogueManager().start(new ItemChat(player, petItem, "You find a " + petItem.getName().toLowerCase() + " from the pet mystery box!"));
        });
    }

    private boolean possessesItem(@NotNull final Player player, final int id) {
        val follower = player.getFollower();
        val pet = follower == null ? null : follower.getPet();
        return (pet != null && pet.itemId() == id) || player.containsItem(id);
    }

    @Override
    public int[] getItems() {
        return new int[] {
                30031
        };
    }
}
