package com.zenyte.plugins.dialogue;

import com.zenyte.game.content.minigame.tithefarm.TithePlantType;
import com.zenyte.game.content.minigame.tithefarm.TitheStatus;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class TitheFarmSeedTableD extends Dialogue {

    /* these are seed placeholders for the  */
    private static final Item GOLOVANOVA_SEED = new Item(ItemId.GOLOVANOVA_SEED, 100);
    private static final Item BOLOGANO_SEED = new Item(ItemId.BOLOGANO_SEED, 100);
    private static final Item LOGAVANO_SEED = new Item(ItemId.LOGAVANO_SEED, 100);

    public TitheFarmSeedTableD(final Player player) {
        super(player);
    }

    @Override
    public void buildDialogue() {
        if(player.getInventory().containsAnyOf(ItemId.GOLOVANOVA_SEED, ItemId.BOLOGANO_SEED, ItemId.LOGAVANO_SEED)) {
            player.getInventory().deleteItem(GOLOVANOVA_SEED);
            player.getInventory().deleteItem(BOLOGANO_SEED);
            player.getInventory().deleteItem(LOGAVANO_SEED);

            doubleItem(GOLOVANOVA_SEED, LOGAVANO_SEED, "You return the seeds to the table and reconsider your options.");
            setKey(99);
        }

        options("What kind of crop will you grow?", "Golovanova seed (level 34)", "Bologano seed (level 54)", "Logavano seed (level 74)", "Nevermind")
                .onOptionOne( () -> {
                    if (player.getSkills().getLevel(Skills.FARMING) < 34) {
                        setKey(30);
                    } else {
                        setKey(5);
                        player.getInventory().addItem(GOLOVANOVA_SEED);
                    }
                } )
                .onOptionTwo( () -> {
                    if(player.getSkills().getLevel(Skills.FARMING) < 54) {
                        setKey(33);
                    } else {
                        setKey(10);
                        player.getInventory().addItem(BOLOGANO_SEED);
                    }
                } )
                .onOptionThree( () -> {
                    if(player.getSkills().getLevel(Skills.FARMING) < 74) {
                        setKey(36);
                    } else {
                        setKey(15);
                        player.getInventory().addItem(LOGAVANO_SEED);
                    }
                });

        item(5, GOLOVANOVA_SEED, "You grab some seeds.");
        item(10, BOLOGANO_SEED, "You grab some seeds.");
        item(15, LOGAVANO_SEED, "You grab some seeds.");

        item(30, GOLOVANOVA_SEED, "You need at least level 34 Farming to use Golovanova seeds.");
        item(33, BOLOGANO_SEED, "You need at least level 54 Farming to use Bologano seeds.");
        item(36, LOGAVANO_SEED, "You need at least level 74 Farming to use Logavano seeds.");
    }
}
