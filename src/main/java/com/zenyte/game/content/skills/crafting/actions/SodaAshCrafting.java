package com.zenyte.game.content.skills.crafting.actions;

import com.zenyte.game.content.skills.cooking.CookingDefinitions;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;

import lombok.RequiredArgsConstructor;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
@RequiredArgsConstructor
public class SodaAshCrafting extends Action {

    public static final Item SEAWEED = new Item(401);
    public static final Item SODA_ASH = new Item(1781);
    public static final Item GIANT_SEAWEED = new Item(21504);

    private final int amount;
    private final boolean range;
    private int cycle;

    @Override
    public boolean start() {
        return player.getInventory().containsItem(SEAWEED) || ( player.getInventory().containsItem(GIANT_SEAWEED) && player.getInventory().getFreeSlots() >= 5);
    }

    @Override
    public boolean process() {
        if (!(player.getInventory().containsItem(SEAWEED) || ( player.getInventory().containsItem(GIANT_SEAWEED) && player.getInventory().getFreeSlots() >= 5))) {
            return false;
        }

        if (cycle >= amount) {
            return false;
        }

        return true;
    }

    @Override
    public int processWithDelay() {
        player.setAnimation(range ? CookingDefinitions.STOVE : CookingDefinitions.FIRE);
        if(player.getInventory().containsItem(GIANT_SEAWEED) && player.getInventory().getFreeSlots() >= 5) {
            player.getInventory().deleteItemsIfContains(new Item[] { GIANT_SEAWEED },  () -> {
                player.getInventory().addItem(SODA_ASH.getId(), 6);
                player.sendFilteredMessage("You heat the giant seaweed and create 6 soda ash.");
            });
        } else {
            player.getInventory().deleteItemsIfContains(new Item[] { SEAWEED },  () -> {
                player.getInventory().addItem(SODA_ASH);
                player.sendFilteredMessage("You heat the seaweed and create soda ash.");
            });
        }
        cycle++;
        return 1;
    }
}
