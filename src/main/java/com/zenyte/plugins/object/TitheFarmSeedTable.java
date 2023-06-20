package com.zenyte.plugins.object;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.DoubleItemMessage;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.DoubleItemChat;
import com.zenyte.plugins.dialogue.TitheFarmSeedTableD;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class TitheFarmSeedTable implements ObjectAction {

    public static final Item GOLOVANOVA_SEED = new Item(ItemId.GOLOVANOVA_SEED, 100);
    public static final Item BOLOGANO_SEED = new Item(ItemId.BOLOGANO_SEED, 100);
    public static final Item LOGAVANO_SEED = new Item(ItemId.LOGAVANO_SEED, 100);

    @Override
    public final void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if(option.equals("Search")) {
            if(!player.getInventory().hasFreeSlots()) {
                player.sendMessage("You don't have enough inventory space to grab any seeds!", MessageType.UNFILTERABLE);
                return;
            }

            /* if a player already has seeds in their inventory */
            if(player.getInventory().containsAnyOf(ItemId.GOLOVANOVA_SEED, ItemId.BOLOGANO_SEED, ItemId.LOGAVANO_SEED)) {
                player.getInventory().deleteItem(GOLOVANOVA_SEED);
                player.getInventory().deleteItem(BOLOGANO_SEED);
                player.getInventory().deleteItem(LOGAVANO_SEED);

                player.getDialogueManager().start(new DoubleItemChat(player, GOLOVANOVA_SEED, LOGAVANO_SEED, "You return the seeds to the table and reconsider your options."));
                return;
            }

            player.getDialogueManager().start(new TitheFarmSeedTableD(player));
        }
    }

    @Override
    public final Object[] getObjects() {
        return new Object[] { ObjectId.SEED_TABLE };
    }
}
