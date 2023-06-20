package com.zenyte.plugins.renewednpc;

import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.dialogue.impl.NPCChat;
import lombok.val;

/**
 * @author Kris | 09/09/2019 14:27
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ThakkradSigmundson extends NPCPlugin {

    private static final int YAKHIDE = 10818;
    private static final int CURED_YAKHIDE = 10820;

    @Override
    public void handle() {
        bind("Craft-goods", (player, npc) -> {
            val inventory = player.getInventory();
            val yakAmount = inventory.getAmountOf(YAKHIDE);
            if (yakAmount < 1) {
                player.getDialogueManager().start(new NPCChat(player, npc.getId(), "You haven't got any yak-hides to cure."));
                return;
            }
            val coinsAmount = inventory.getAmountOf(995);
            if (coinsAmount < 5) {
                player.getDialogueManager().start(new NPCChat(player, npc.getId(), "Each yak-hide costs 5 coins to cure!"));
                return;
            }
            val affordableAmount = coinsAmount / 5;
            player.sendInputInt("How many yak-hides would you like to cure for 5 gp each? (1-" + Math.min(yakAmount, affordableAmount) + ")", value -> {
                val validatedAmount = Math.min(value, Math.min(inventory.getAmountOf(995) / 5, inventory.getAmountOf(YAKHIDE)));
                if (validatedAmount < 1) {
                    return;
                }
                inventory.deleteItem(YAKHIDE, validatedAmount);
                inventory.deleteItem(995, validatedAmount * 5);
                inventory.addItem(CURED_YAKHIDE, validatedAmount);
                player.getDialogueManager().finish();
                player.getDialogueManager().start(new NPCChat(player, npc.getId(), "There you go."));
            });
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                1881
        };
    }
}
