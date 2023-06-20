package com.zenyte.game.content.treasuretrails.clues.emote;

import com.zenyte.game.item.Item;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.ToString;
import lombok.val;

/**
 * @author Kris | 20/11/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@ToString
public class AllRequirementsCollection implements ItemRequirement {
    private final ItemRequirement[] requirements;

    public AllRequirementsCollection(ItemRequirement... requirements) {
        this.requirements = requirements;
    }

    @Override
    public boolean fulfilledBy(int itemId) {
        for (ItemRequirement requirement : requirements) {
            if (requirement.fulfilledBy(itemId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean fulfilledBy(Item[] items) {
        for (ItemRequirement requirement : requirements) {
            if (!requirement.fulfilledBy(items)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public IntArrayList getFulfillingItemsIndexes(Item[] items) {
        val list = new IntArrayList();
        for (val req : requirements) {
            val indexes = req.getFulfillingItemsIndexes(items);
            if (indexes == null) {
                return null;
            }
            list.addAll(indexes);
        }
        return list;
    }


}
