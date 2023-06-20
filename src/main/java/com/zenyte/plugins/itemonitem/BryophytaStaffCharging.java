package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 09/10/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BryophytaStaffCharging implements PairedItemOnItemPlugin {
    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        val runes = from.getId() == 561 ? from : to;
        val staff = runes == from ? to : from;
        if (staff.getCharges() >= 1000) {
            player.sendMessage("Your Bryophyta's staff is already fully charged.");
            return;
        }
        player.sendInputInt("How many nature runes would you like to charge the staff with?", value -> {
            val amountToAdd = Math.min(Math.min(runes.getAmount(), 1000 - staff.getCharges()), value);
            if (amountToAdd <= 0 || player.getInventory().getItem(staff == from ? fromSlot : toSlot) != staff
                    || player.getInventory().getItem(runes == from ? fromSlot : toSlot) != runes) {
                return;
            }
            player.getInventory().deleteItem(new Item(561, amountToAdd));
            staff.setCharges(staff.getCharges() + amountToAdd);
            staff.setId(22370);
            player.getInventory().refreshAll();
            player.sendFilteredMessage("You charge your bryophyta's staff with " + amountToAdd + " nature runes.");
        });
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[] {
                ItemPair.of(561, 22368),
                ItemPair.of(561, 22370)
        };
    }
}
