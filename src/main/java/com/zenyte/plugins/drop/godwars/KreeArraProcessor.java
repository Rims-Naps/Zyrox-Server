package com.zenyte.plugins.drop.godwars;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Tommeh | 25-11-2018 | 16:37
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class KreeArraProcessor extends DropProcessor {

    @Override
    public void attach() {
        //Armour
        appendDrop(new DisplayedDrop(11826, 1, 1, 288));
        appendDrop(new DisplayedDrop(11828, 1, 1, 288));
        appendDrop(new DisplayedDrop(11830, 1, 1, 288));
        //Armadyl hilt
        appendDrop(new DisplayedDrop(11810, 1, 1, 381));
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
            if (random(381) == 0) {
                return new Item(11810);
            }
            if ((random = random(288)) < 3) {
                return new Item(11826 + (random * 2));
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] { 3162, 6492 };
    }
}
