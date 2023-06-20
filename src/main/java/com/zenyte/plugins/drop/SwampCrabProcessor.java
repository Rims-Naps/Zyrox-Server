package com.zenyte.plugins.drop;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;

public class SwampCrabProcessor extends DropProcessor {
    @Override
    public void attach() {
        appendDrop(new DisplayedDrop(ItemId.MORT_MYRE_FUNGUS+1,3,3,3));
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            if (random(3) == 0) {
                return new Item(ItemId.MORT_MYRE_FUNGUS + 1); //+1 for noted variant
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[]{8297, 8298};
    }
}
