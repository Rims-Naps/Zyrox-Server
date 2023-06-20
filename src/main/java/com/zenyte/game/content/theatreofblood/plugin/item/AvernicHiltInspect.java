package com.zenyte.game.content.theatreofblood.plugin.item;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Cresinkel
 */

public class AvernicHiltInspect extends ItemPlugin {

    @Override
    public void handle() {
        bind("Inspect", (player, item, slotId) -> {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    item(22477, "You raise the hilt, inspecting each section carefully. It looks as though it could combine with a powerful parrying dagger.");
                }
            });
        });
    }

    @Override
    public int[] getItems() {
        return new int[]{ItemId.AVERNIC_DEFENDER_HILT};
    }
}
