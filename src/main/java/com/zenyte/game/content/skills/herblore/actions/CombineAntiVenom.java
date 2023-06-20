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
public class CombineAntiVenom extends Action {
    // Keep ordered from low dose to high
    public static final Int2IntOpenHashMap POTS = new Int2IntOpenHashMap(new int[]{5958, 5956, 5954, 5952},
            new int[]{12911, 12909, 12907, 12905});
    private final int amount;
    private final int potionToUpgrade;
    private final int upgradedPotion;
    private final int dose;
    private int completed;

    public CombineAntiVenom(int amount, int potionToUpgrade) {
        this.amount = amount;
        this.potionToUpgrade = potionToUpgrade;
        this.upgradedPotion = POTS.get(potionToUpgrade);
        this.dose = Potion.ANTI_VENOM.getDoses(upgradedPotion);
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
                player.sendFilteredMessage("Your Booster Perk saves you your zulrah scales!");
            } else {
                player.getInventory().deleteItem(ItemId.ZULRAHS_SCALES, 5 * dose);
            }
        } else {
            player.getInventory().deleteItem(ItemId.ZULRAHS_SCALES, 5 * dose);
        }
        player.getInventory().deleteItem(potion);
        player.getInventory().addOrDrop(upgradedPotion, 1);
        player.getSkills().addXp(Skills.HERBLORE, 30 * dose);
        completed++;
        return 1;
    }

    private boolean check() {
        if (completed >= amount) {
            return false;
        }
        return player.carryingItem(potionToUpgrade) && player.getInventory().getAmountOf(ItemId.ZULRAHS_SCALES) >= 5 * dose;
    }
}
