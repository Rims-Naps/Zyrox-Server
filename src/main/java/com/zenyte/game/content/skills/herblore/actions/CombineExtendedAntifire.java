package com.zenyte.game.content.skills.herblore.actions;

import com.zenyte.game.content.consumables.drinks.Potion;
import com.zenyte.game.content.vote.BoosterPerks;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.val;

/**
 * @author Cresinkel
 */
public class CombineExtendedAntifire extends Action {
    // Keep ordered from low dose to high
    public static final Int2IntOpenHashMap POTS = new Int2IntOpenHashMap(new int[]{2458, 2456, 2454, 2452},
            new int[]{11957, 11955, 11953, 11951});
    private final int amount;
    private final int potionToUpgrade;
    private final int upgradedPotion;
    private final int dose;
    private int completed;

    public CombineExtendedAntifire(int amount, int potionToUpgrade) {
        this.amount = amount;
        this.potionToUpgrade = potionToUpgrade;
        this.upgradedPotion = POTS.get(potionToUpgrade);
        this.dose = Potion.EXTENDED_ANTIFIRE.getDoses(upgradedPotion);
    }

    @Override
    public boolean start() {
        return check();
    }

    @Override
    public boolean process() {
        return check();
    }

    @Override
    public int processWithDelay() {
        val potion = player.getInventory().getAny(potionToUpgrade);
        if (BoosterPerks.isActive(player, BoosterPerks.HERBLORE)) {
            if (Utils.random(99) < 5) {
                player.sendFilteredMessage("Your Booster Perk saves you your lava scale shards!");
            } else {
                player.getInventory().deleteItem(ItemId.LAVA_SCALE_SHARD, dose);
            }
        } else {
            player.getInventory().deleteItem(ItemId.LAVA_SCALE_SHARD, dose);
        }
        player.getInventory().deleteItem(potion);
        player.getInventory().addOrDrop(upgradedPotion, 1);
        player.getSkills().addXp(Skills.HERBLORE, 27.5 * dose);
        completed++;
        return 1;
    }

    private boolean check() {
        if (completed >= amount) {
            return false;
        }
        return player.carryingItem(potionToUpgrade) && player.getInventory().getAmountOf(ItemId.LAVA_SCALE_SHARD) >= dose;
    }
}
