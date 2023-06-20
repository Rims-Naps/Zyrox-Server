package com.zenyte.game.content.skills.hunter.aerialfishing.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import lombok.val;

/**
 * @author Cresinkel
 */

public class AerialFishCutting implements PairedItemOnItemPlugin {

    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        val knifeItem = from.getId() == ItemId.KNIFE ? from : to;
        val fishItem = knifeItem.getId() == from.getId() ? to : from;
        val inven = player.getInventory();
        inven.deleteItem(fishItem);
        inven.addItem(ItemId.FISH_CHUNKS, 1);
        player.getSkills().addXp(Skills.COOKING, getCookingXP(fishItem.getId()));
        player.sendFilteredMessage("You cut a " + fishItem.getName() + " into chunks.");
    }

    private double getCookingXP(int fishItemId) {
        switch (fishItemId) {
            case ItemId.BLUEGILL:
                return AerialFish.BLUEGILL.cookingXP;
            case ItemId.COMMON_TENCH:
                return AerialFish.COMMON_TENCH.cookingXP;
            case ItemId.MOTTLED_EEL:
                return AerialFish.MOTTLED_EEL.cookingXP;
            case ItemId.GREATER_SIREN:
                return AerialFish.GREATER_SIREN.cookingXP;
            default:
                return 1;
        }
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[]{
                ItemPair.of(ItemId.BLUEGILL, ItemId.KNIFE),
                ItemPair.of(ItemId.COMMON_TENCH, ItemId.KNIFE),
                ItemPair.of(ItemId.MOTTLED_EEL, ItemId.KNIFE),
                ItemPair.of(ItemId.GREATER_SIREN, ItemId.KNIFE),
        };
    }
}
