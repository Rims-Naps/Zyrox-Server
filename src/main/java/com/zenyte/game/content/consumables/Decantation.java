package com.zenyte.game.content.consumables;

import com.zenyte.game.content.consumables.drinks.BarbarianMix;
import com.zenyte.game.content.consumables.drinks.GourdPotion;
import com.zenyte.game.content.consumables.drinks.Potion;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Kris | 04/12/2018 19:31
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Decantation implements ItemOnItemAction {

    private static final int vial = 229;
    private static final int gourdVial = 20800;

    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        if (from.getId() == vial || to.getId() == vial || from.getId() == gourdVial || to.getId() == gourdVial || from.getId() == ItemId.EMPTY_CUP || to.getId() == ItemId.EMPTY_CUP) {
            val vial = from.getId() == Decantation.vial || from.getId() == gourdVial || from.getId() == ItemId.EMPTY_CUP ? from : to;
            val potion = vial == from ? to : from;
            val consumable = Consumable.consumables.get(potion.getId());
            if (!(consumable instanceof Drinkable))
                return;
            val drink = (Drinkable) consumable;
            val doses = drink.getDoses(potion.getId());
            val vialReplacement = drink.getItem((int) Math.ceil(doses / 2F));
            val potionReplacement = drink.getItem(doses / 2);
            val inventory = player.getInventory();
            inventory.set(vial == from ? fromSlot : toSlot, vialReplacement);
            inventory.set(potion == from ? fromSlot : toSlot, potionReplacement);
            inventory.refresh();
            player.sendMessage("You divide the liquid between the vessels.");
            return;
        }
        val fromConsumable = Consumable.consumables.get(from.getId());
        if (!(fromConsumable instanceof Drinkable))
            return;
        val toConsumable = Consumable.consumables.get(to.getId());
        if (!(toConsumable instanceof Drinkable))
            return;
        val fromDrink = (Drinkable) fromConsumable;
        val toDrink = (Drinkable) toConsumable;
        val fromDoses = fromDrink.getDoses(from.getId());
        val toDoses = toDrink.getDoses(to.getId());
        val toLength = toDrink.getIds().length;
        if (toDoses == toLength) {
            player.sendMessage("Nothing interesting happens.");
            return;
        }
        val decantedToDoses = Math.min(toLength, toDoses + fromDoses);
        val decantedFromDoses = fromDoses - (decantedToDoses - toDoses);
        val inventory = player.getInventory();
        val fromReplacement = fromDrink.getItem(decantedFromDoses);
        inventory.set(fromSlot, (fromReplacement == null && fromDrink instanceof GourdPotion) ?
                new Item(gourdVial) : (fromReplacement == null && fromDrink == Potion.GUTHIX_REST) ? new Item(ItemId.EMPTY_CUP) : fromReplacement);
        inventory.set(toSlot, toDrink.getItem(decantedToDoses));
        inventory.refresh();
        player.sendMessage("You have combined the liquid into " + decantedToDoses + " doses.");
    }

    public ItemPair[] getMatchingPairs() {
        val pairs = new ArrayList<ItemPair>();
        val decantablePotions = new ArrayList<Drinkable>(1500);
        decantablePotions.addAll(Arrays.asList(Potion.values));
        decantablePotions.addAll(Arrays.asList(BarbarianMix.values));
        decantablePotions.addAll(Arrays.asList(GourdPotion.values));

        for (val drinkable : decantablePotions) {
            val isGourdVial = drinkable instanceof GourdPotion;
            val ids = drinkable.getIds();
            int i, j;
            for (i = 0; i < ids.length; i++) {
                int id = ids[i];
                for (j = 0; j < ids.length; j++) {
                    int secondId = ids[j];
                    pairs.add(ItemPair.of(id, secondId));
                }
                if (i != 0) {
                    pairs.add(ItemPair.of(drinkable == Potion.GUTHIX_REST ? ItemId.EMPTY_CUP : isGourdVial ? gourdVial : vial, id));
                }
            }
        }
        return pairs.toArray(new ItemPair[0]);
    }

    @Override
    public int[] getItems() {
        return new int[0];
    }
}
