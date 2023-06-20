package com.zenyte.plugins.object;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.ItemChat;
import com.zenyte.plugins.dialogue.PlainChat;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;
import lombok.var;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class MotherlodeMineSack implements ObjectAction {

    private static final Item PAYDIRT = new Item(12011, 1);

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        var paydirt = player.getPaydirt().size();
        if(object.getName().toLowerCase().equals("empty sack") || paydirt == 0) {
            player.getDialogueManager().start(new PlainChat(player, "The sack is empty."));
            return;
        }

        if(option.equals("Search")) {
            if (!player.getInventory().hasFreeSlots()) {
                player.getDialogueManager().start(new ItemChat(player, PAYDIRT, "Your inventory is too full to collect any ore."));
                return;
            }
            val removed = new IntArrayList();
            for (int i = 0; i < paydirt; i++) {
                val id = player.getPaydirt().getInt(i);
                val succeeded = player.getInventory().addItem(new Item(id, 1)).getSucceededAmount();
                if (succeeded > 0) {
                    removed.add(id);
                    continue;
                }
                break;
            }
            val dirt = player.getPaydirt();
            for (val taken : removed) {
                dirt.rem(taken);
            }

            val finalAmount = player.getPaydirt().size();
            player.getVarManager().sendBit(5558, player.getPaydirt().size());
            player.getDialogueManager().start(new ItemChat(player, PAYDIRT, "You collect your ore from the sack.<br> There is "+ finalAmount +" paydirt left in the sack."));
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 26677, 26678, 26688 };
    }
}
