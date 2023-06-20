package com.zenyte.game.util;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import mgi.types.config.items.ItemDefinitions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

@AllArgsConstructor
public class Examine {

    @Getter
    private int id;
    @Getter
    private String examine;

    public static final void sendItemExamine(final Player player, final Item item) {
        if (item == null) {
            return;
        }
        sendItemExamine(player, item.getId());
    }

    public static final void sendItemExamine(final Player player, final int id) {
        val definitions = ItemDefinitions.getOrThrow(id);
        if (definitions.isNoted()) {
            player.sendMessage("Swap this note at any bank for the equivalent item", MessageType.EXAMINE_ITEM);
            return;
        }
        final String examine = definitions.getExamine();
        if (examine == null || examine.isEmpty()) {
            return;
        }
        player.sendMessage(examine, MessageType.EXAMINE_ITEM);
    }

}
