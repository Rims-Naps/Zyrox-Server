package com.zenyte.plugins.renewednpc;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnNPCAction;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.*;

/**
 * @author Kris | 26/11/2018 18:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Banker extends NPCPlugin implements ItemOnNPCAction {
    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new BankerD(player, npc)));
        bind("Bank", (player, npc) -> GameInterface.BANK.open(player));
        bind("Collect", (player, npc) -> GameInterface.GRAND_EXCHANGE_COLLECTION_BOX.open(player));
    }

    //TODO: Route event.

    @Override
    public int[] getNPCs() {
        return new int[] {
                398, 399, 1600, 2119, 2292, 2293, 2368, 2369,
                3003, 3318, 3887, 3888, 4054, 4055, 4293, 4762, 6084, 6859,
                6860, 6861, 6862, 6863, 6864, 6939, 6940, 6941, 6942, 6969, 6970, 7057, 7058,
                7059, 7060, 7077, 7078, 7079, 7080, 7081, 7082, 8321, 8322, 7417, 6765, 7121, 6439
        };
    }

    @Override
    public void handleItemOnNPCAction(final Player player, final Item item, final int slot, final NPC npc) {
        if (item.getId() == 995 || item.getId() == 13204) {
            if (item.getId() == 995 && item.getAmount() < 1000) {
                player.sendMessage("You need at least 1,000 coins to convert the coins into platinum token(s).");
                return;
            }
            player.getDialogueManager().start(new CurrencyConversionD(player, item, slot));
            return;
        }
        if (item.getDefinitions().isNoted()) {
            player.getDialogueManager().start(new UnnoteD(player, item));
        } else {
            if (item.getDefinitions().getNotedId() == -1) {
                player.getDialogueManager().start(new PlainChat(player, "You cannot turn this into banknotes, try another item."));
                return;
            }
            player.getDialogueManager().start(new NoteD(player, item));
        }
    }

    @Override
    public Object[] getItems() {
        return new Object[] {
                -1
        };
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                398, 399, 1600, 2119, 2292, 2293, 2368, 2369,
                3003, 3318, 3887, 3888, 4054, 4055, 4293, 4762, 6084, 6859,
                6860, 6861, 6862, 6863, 6864, 6939, 6940, 6941, 6942, 6969, 6970, 7057, 7058,
                7059, 7060, 7077, 7078, 7079, 7080, 7081, 7082, 8321, 8322, 7417
        };
    }
}
