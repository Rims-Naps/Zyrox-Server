package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.item.trident.SeaTrident;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.val;

/**
 * @author Christopher
 * @since 2/20/2020
 */
public final class SeasTridentChargingAction implements PairedItemOnItemPlugin {
    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
        val tridentItem = SeaTrident.allTridentsMap.keySet().contains(from.getId()) ? from : to;
        val tridentDef = SeaTrident.allTridentsMap.get(tridentItem.getId());
        val tridentSlot = from == tridentItem ? fromSlot : toSlot;
        if (tridentDef.charge(player, tridentItem, tridentSlot)) {
            if (tridentDef == SeaTrident.SEA_TRIDENT && tridentItem.getCharges() >= tridentDef.maxCharges()) {
                tridentItem.setCharges(0);
                tridentItem.setId(SeaTrident.SEA_TRIDENT_FULL.getChargedId());
            }
            player.getInventory().refresh(tridentItem == from ? fromSlot : toSlot);
        }
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        val pairs = new ObjectOpenHashSet<ItemPair>();
        for (Item rune : SeaTrident.chargingMaterials) {
            for (Integer tridentId : SeaTrident.allTridentsMap.keySet()) {
                pairs.add(ItemPair.of(rune.getId(), tridentId));
            }
        }
        return pairs.toArray(new ItemPair[0]);
    }
}
