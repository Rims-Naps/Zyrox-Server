package com.zenyte.plugins.drop.championscrolls;

import com.zenyte.game.item.Item;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Cresinkel
 */

public class JogreDropProcessor extends DropProcessor {
    @Override
    public void attach() {
        appendDrop(new DisplayedDrop(6804, 1, 1, 2500));
    }

    public void onDeath(final NPC npc, final Player killer) {
        if (Utils.random(2499) == 0) {
            npc.dropItem(killer, new Item(6804));
            killer.sendMessage(Colour.BRICK.wrap("A Champion's scroll falls to the ground as you slay your opponent."));
        }
    }

    @Override
    public int[] ids() {
        return new int[]{
              2094, 2234
        };
    }
}
