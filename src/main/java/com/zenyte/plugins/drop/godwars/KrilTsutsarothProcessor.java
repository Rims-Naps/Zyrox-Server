package com.zenyte.plugins.drop.godwars;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Tommeh | 25-11-2018 | 16:49
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class KrilTsutsarothProcessor extends DropProcessor {

    @Override
    public void attach() {
        //Steam battlestaff
        appendDrop(new DisplayedDrop(11787, 1, 1, 96));
        //Zamorakian spear
        appendDrop(new DisplayedDrop(11824, 1, 1, 96));
        //Staff of the dead
        appendDrop(new DisplayedDrop(11791, 1, 1, 381));
        //Zamorak hilt
        appendDrop(new DisplayedDrop(11816, 1, 1, 381));
        //Godsword shards
        appendDrop(new DisplayedDrop(11818, 1, 1, 576));
        appendDrop(new DisplayedDrop(11820, 1, 1, 576));
        appendDrop(new DisplayedDrop(11822, 1, 1, 576));
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            int random;
            if ((random = random(576)) < 3) {
                return new Item(11818 + (random * 2));
            }
            if ((random = random(381)) < 2) {
                return new Item(random == 0 ? 11791 : 11816);
            }
            if ((random = random(96)) < 2) {
                return new Item(random == 0 ? 11787 : 11824);
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] { 3129, 6495 };
    }
}
