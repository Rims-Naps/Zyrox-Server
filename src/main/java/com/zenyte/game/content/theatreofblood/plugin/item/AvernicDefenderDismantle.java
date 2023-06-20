package com.zenyte.game.content.theatreofblood.plugin.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Cresinkel
 */

public class AvernicDefenderDismantle extends ItemPlugin {

    private static final Item DRAGON_DEFENDER = new Item(ItemId.DRAGON_DEFENDER);
    private static final Item AVERNIC_DEFENDER = new Item(ItemId.AVERNIC_DEFENDER);

    @Override
    public void handle() {
        bind("Dismantle", (player, item, slotId) -> {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    item(ItemId.AVERNIC_DEFENDER, "<col=FF0040>Warning!</col><br>Dismantling the Avernic defender will not give you the hilt back.");
                    options("Are you sure you wish to do this?", "Yes, dismantle the Avernic defender.", "No, I'll keep my Avernic defender.")
                            .onOptionOne(() -> {
                                player.getInventory().deleteItem(AVERNIC_DEFENDER);
                                player.getInventory().addItem(DRAGON_DEFENDER);
                            });
                }
            });
        });
    }

    @Override
    public int[] getItems() {
        return new int[]{ItemId.AVERNIC_DEFENDER};
    }
}
