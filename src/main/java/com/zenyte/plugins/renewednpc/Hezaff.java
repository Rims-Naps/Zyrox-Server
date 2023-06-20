package com.zenyte.plugins.renewednpc;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.dialogue.impl.NPCChat;
import lombok.val;

/**
 * @author Kris | 12/04/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Hezaff extends NPCPlugin {

    private static final int ZENYTE_HOME_TELEPORT = 22721;

    @Override
    public void handle() {
        bind("Buy-teletabs", (player, npc) -> {
            val purchases = player.getVariables().getTeletabPurchases();
            if (purchases >= 1000) {
                player.getDialogueManager().start(new NPCChat(player, npc.getId(), "You've already purchased the daily maximum of 1,000 teletabs from me!"));
                return;
            }
            val inventory = player.getInventory();
            if (!inventory.hasFreeSlots() && !inventory.containsItem(ZENYTE_HOME_TELEPORT, 1)) {
                player.getDialogueManager().start(new NPCChat(player, npc.getId(), "You need some free inventory space to purchase these!"));
                return;
            }
            val price = 175;
            val coins = inventory.getAmountOf(ItemId.COINS_995);
            val maxAmountPurchaseable = coins / price;
            val count = inventory.getAmountOf(ZENYTE_HOME_TELEPORT);
            val maxAvailablePurchase = Math.min(Math.min(maxAmountPurchaseable, 1000 - purchases), Integer.MAX_VALUE - count);
            if (maxAvailablePurchase <= 0) {
                player.getDialogueManager().start(new NPCChat(player, npc.getId(), "You can't afford any teletabs!"));
                return;
            }
            player.sendInputInt("How many would you like to purchase for " + price + " each? (0-" + maxAvailablePurchase + ")", value -> {
                val currentCoins = inventory.getAmountOf(ItemId.COINS_995);
                val currentMaxAmountPurchaseable = currentCoins / price;
                val currentCount = inventory.getAmountOf(ZENYTE_HOME_TELEPORT);
                val currentMaxAvailablePurchase = Math.min(Math.min(currentMaxAmountPurchaseable, 1000 - purchases), Integer.MAX_VALUE - currentCount);
                val purchaseQuantity = Math.min(value, currentMaxAvailablePurchase);
                if (purchaseQuantity <= 0) {
                    return;
                }
                player.getVariables().setTeletabPurchases(purchases + purchaseQuantity);
                inventory.deleteItem(new Item(ItemId.COINS_995, purchaseQuantity * price));
                inventory.addOrDrop(new Item(ZENYTE_HOME_TELEPORT, purchaseQuantity));
                player.getDialogueManager().start(new NPCChat(player, npc.getId(), "Pleasure doing business with you!"));
            });
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                10006
        };
    }
}
