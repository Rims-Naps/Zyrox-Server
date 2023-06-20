package com.zenyte.plugins.drop.slayer;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Tommeh | 25-11-2018 | 19:36
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class CaveHorrorProcessor extends DropProcessor {

    @Override
    public void attach() {
        //Black mask
        appendDrop(new DisplayedDrop(8901, 1, 1, 384));
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            if (random(384) == 0) {
                return new Item(8901);
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] { 3209, 3210, 3211, 3212, 3213 };
    }
}
