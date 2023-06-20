package com.zenyte.plugins.drop.slayer;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Cresinkel
 */
public class CaveCrawlerProcessor extends DropProcessor {

    @Override
    public void attach() {
        //Bronze Boots
        appendDrop(new DisplayedDrop(4119, 1, 1, 128));
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            if (random(127) == 0) {
                return new Item(4119);
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] { 406, 407, 408, 409, 7389};
    }
}
