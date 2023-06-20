package com.zenyte.plugins.itemonnpc;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnNPCAction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.impl.NPCChat;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Tommeh | 2-2-2019 | 23:04
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ItemOnPilesNPCAction implements ItemOnNPCAction {

    private static final int[] ALLOWED_ITEMS = {
            11934, 440, 453, 444, 447, 449, 451, 1515, 1513, //resources
            11934, 2349, 2351, 2357, 2359, 2361, 2363 //products from resources
    };

    @Override
    public void handleItemOnNPCAction(final Player player, final Item item, final int slot, final NPC npc) {
        if (!ArrayUtils.contains(ALLOWED_ITEMS, item.getId())) {
            player.getDialogueManager().start(new NPCChat(player, npc.getId(), "Sorry, I wasn't expecting anyone to want to convert<br><br>that sort of item, so I haven't any banknotes for it."));
        } else {
            if (!player.getInventory().containsItem(995, 25)) {
                player.getDialogueManager().start(new NPCChat(player, npc.getId(), "I'm afraid you don't have enough gold on you right now."));
                return;
            }
            var amount = player.getInventory().getAmountOf(item.getId());
            if (!player.getInventory().containsItem(995, amount * 25)) {
                amount = player.getInventory().getAmountOf(995) / 25;
            }
            val result = amount;
            val price = result * 25;
            player.getDialogueManager().start(new Dialogue(player) {

                @Override
                public void buildDialogue() {
                    options("Banknote " + result + " x " + item.getName() + "?", "Yes - " + Utils.format(price) + " gp", "Cancel").onOptionOne(() -> {
                        player.getInventory().deleteItem(item.getId(), result);
                        player.getInventory().deleteItem(995, price);
                        player.getInventory().addItem(item.getDefinitions().getNotedId(), result);
                        setKey(5);
                    });
                    item(5, item, "Piles converts your " + (result == 1 ? "item" : "items") + " to " + (result == 1 ? "a banknote." : "banknotes."));
                }
            });
        }
    }

    @Override
    public Object[] getItems() {
        return new Object[] { -1 };
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 13 };
    }
}
