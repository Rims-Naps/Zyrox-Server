package com.zenyte.game.content.skills.herblore;

import com.zenyte.game.content.skills.herblore.actions.Combine;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.item.UnmodifiableItem;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.item.Herb;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Chris
 * @since August 19 2020
 */
public class GrimyHerbOnVialOfWater implements ItemOnItemAction {
    private static final UnmodifiableItem vialOfWater = new UnmodifiableItem(ItemId.VIAL_OF_WATER);

    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        if (!SkillcapePerk.HERBLORE.isEffective(player)) {
            return;
        }
        player.lock(1);
        val grimyHerbItem = from.getId() == vialOfWater.getId() ? to : from;
        val grimyHerbSlot = grimyHerbItem == from ? fromSlot : toSlot;

        Herb.clean(player, grimyHerbItem, grimyHerbSlot);

        WorldTasksManager.schedule(() -> {
            val cleanHerb = player.getInventory().getItem(grimyHerbSlot);
            val data = Combine.HerbloreData.getDataByMaterial(cleanHerb, vialOfWater);
            if (data == null) {
                player.sendMessage("Nothing interesting happens");
            } else if (Combine.HerbloreData.hasRequirements(player, data)) {
                player.getActionManager().setAction(new Combine(data, 1));
            }
        });
    }

    @Override
    public int[] getItems() {
        return null;
    }

    public ItemPair[] getMatchingPairs() {
        val pairs = new ArrayList<ItemPair>();
        for (CleanableHerb cleanableHerb : CleanableHerb.cleanableHerbs) {
            pairs.add(ItemPair.of(cleanableHerb.getGrimyId(), vialOfWater.getId()));
        }
        return pairs.toArray(new ItemPair[0]);
    }
}
