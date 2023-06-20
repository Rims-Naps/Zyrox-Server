package com.zenyte.plugins.renewednpc;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.ArianwynD;
import com.zenyte.plugins.dialogue.BertD;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Cresinkel
 */

public class Bert extends NPCPlugin {

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> {
            player.getDialogueManager().start(new BertD(player, npc));
        });
        bind("Sand", ((player, npc) -> {
            val success = canGiveSand(player);
            if (success) {
                giveSand(player);
                player.sendMessage("Bert drops off some buckets of sand to your bank.");
            } else {
                player.sendMessage("Bert already gave you buckets of sand today, come back tomorrow.");
            }

        }));
        bind("Toggle", ((player, npc) -> {
            if (DiaryReward.ARDOUGNE_CLOAK4.eligibleFor(player)) {
                if (player.getAttributes().containsKey("WANTS_SAND")) {
                    player.getAttributes().remove("WANTS_SAND");
                    player.sendMessage("Bert now no longer knows your adress of your bank, and can not deliver anymore buckets of sand.");
                } else {
                    player.getAttributes().put("WANTS_SAND", 1);
                    player.sendMessage("Bert now remembers your adress of your bank, and will deliver buckets of sand daily.");
                }
            } else {
                player.sendMessage("Bert peaks at your ardougne diaries and sees you are undiserving of this feature.");
            }
        }));
    }

    private void giveSand(@NotNull Player player) {
        if (!player.getAttributes().containsKey("DAILY_SAND")) {
            for (int i = 84; i > 0; --i) {
                player.getBank().add(new Item(1784));
            }
            player.getAttributes().put("DAILY_SAND", 1);
        }
    }

    private boolean canGiveSand(@NotNull Player player) {
        return !player.getAttributes().containsKey("DAILY_SAND");
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                5819
        };
    }
}
