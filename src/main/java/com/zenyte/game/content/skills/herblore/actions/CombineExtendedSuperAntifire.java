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
public class CombineExtendedSuperAntifire extends Action {
    // Keep ordered from low dose to high
    public static final Int2IntOpenHashMap POTS = new Int2IntOpenHashMap(new int[]{21987, 21984, 21981, 21978},
            new int[]{22218, 22215, 22212, 22209});
    private final int amount;
    private final int potionToUpgrade;
    private final int upgradedPotion;
    private final int dose;
    private int completed;

    public CombineExtendedSuperAntifire(int amount, int potionToUpgrade) {
        this.amount = amount;
        this.potionToUpgrade = potionToUpgrade;
        this.upgradedPotion = POTS.get(potionToUpgrade);
        this.dose = Potion.EXTENDED_SUPER_ANTIFIRE.getDoses(upgradedPotion);
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
        player.getSkills().addXp(Skills.HERBLORE, 40 * dose);
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
