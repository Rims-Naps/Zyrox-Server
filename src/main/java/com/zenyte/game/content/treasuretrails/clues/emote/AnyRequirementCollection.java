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
public class AnyRequirementCollection implements ItemRequirement {
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String name;
    private final ItemRequirement[] requirements;

    public AnyRequirementCollection(String name, ItemRequirement... requirements) {
        this.name = name;
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
            if (requirement.fulfilledBy(items)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public IntArrayList getFulfillingItemsIndexes(Item[] items) {
        for (val requirement : requirements) {
            val indexes = requirement.getFulfillingItemsIndexes(items);
            if (indexes != null) {
                return indexes;
            }
        }
        return null;
    }
}

