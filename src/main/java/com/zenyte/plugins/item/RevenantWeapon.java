package com.zenyte.plugins.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.game.item.pluginextensions.ChargeExtension;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.ContainerWrapper;
import lombok.val;

/**
 * @author Kris | 19/01/2019 22:24
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class RevenantWeapon extends ItemPlugin implements PairedItemOnItemPlugin, ChargeExtension {
    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[] {
                new ItemPair(22547, 21820),
                new ItemPair(22550, 21820),
                new ItemPair(22542, 21820),
                new ItemPair(22545, 21820),
                new ItemPair(22552, 21820),
                new ItemPair(22555, 21820)
        };
    }

    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        val ether = from.getId() == 21820 ? from : to;
        val weapon = ether == from ? to : from;
        val existing = weapon.getCharges();
        val toAdd = Math.min(17000 - existing, ether.getAmount());
        if (toAdd < 1000 && existing == 0) {
            player.sendMessage("You need to charge the " + weapon.getName() + " with at least 1000 revenant ether.");
            return;
        }
        val deleted = player.getInventory().deleteItem(new Item(ether.getId(), toAdd));
        weapon.setCharges(existing + deleted.getSucceededAmount());
        if (weapon.getCharges() >= 1000 && (weapon.getId() == ItemId.CRAWS_BOW_U || weapon.getId() == ItemId.VIGGORAS_CHAINMACE_U || weapon.getId() == ItemId.THAMMARONS_SCEPTRE_U)) {
            weapon.setId(weapon.getId() + 3);
            player.getInventory().refreshAll();
        }
        player.sendMessage("You charge the " + weapon.getName() + " with " + toAdd + " revenant ether.");
    }

    @Override
    public void handle() {
        bind("Uncharge", (player, item, container, slotId) -> {
            player.getInventory().addItem(new Item(21820, item.getCharges())).onFailure(it -> World.spawnFloorItem(it, player));
            item.setCharges(0);
            val name = item.getName();
            item.setId(item.getId() - 3);
            container.refresh(slotId);
            player.sendMessage("You remove the ether from the " + name + ".");
        });
    }

    @Override
    public int[] getItems() {
        return new int[] {
                22547, 22550, 22542, 22545, 22552, 22555
        };
    }

    @Override
    public void removeCharges(Player player, Item item, ContainerWrapper wrapper, int slotId, int amount) {
        val charges = item.getCharges();
        item.setCharges(charges - amount);
        if (item.getCharges() <= 1000) {
            item.setId(item.getId() == 22550 ? 22547 : item.getId() == 22545 ? 22542 : 22552);
            wrapper.refresh(slotId);
        }
        if (charges > 1000 && item.getCharges() <= 1000) {
            player.sendMessage("Your " + item.getName() + " has ran out of charges.");
        }
    }

    @Override
    public void checkCharges(final Player player, final Item item) {
        val name = item.getName();
        if (item.getCharges() <= 1000) {
            val activationString = item.getCharges() > 0 ? "has been activated" : "has not been activated";
            player.sendMessage("Your " + item.getName() + " " + activationString + " and has dropped below 1,000 charges.");
            return;
        }

        val deg = DegradableItem.ITEMS.get(item.getId());
        if (deg == null) {
            return;
        }
        val percentage = FORMATTER.format(
                Math.max(0, item.getCharges() - 1000) / (float) (DegradableItem.getFullCharges(item.getId()) - 1000) * 100);
        player.sendMessage(
                "Your " + name + " " + (name.contains("legs") ? "have " : "has ") + percentage.replace(".0", "") + "% charges remaining.");
    }
}
