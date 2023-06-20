package com.zenyte.plugins.drop.krakens;

import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Tommeh | 25-11-2018 | 17:43
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class KrakenProcessor extends DropProcessor {

    @Override
    public void attach() {
        //Kraken tentacle
        appendDrop(new DisplayedDrop(12004, 1, 1, 300));
        //Trident of the seas (full)
        appendDrop(new DisplayedDrop(11905, 1, 1, 384));
        //Jar of dirt
        appendDrop(new DisplayedDrop(12007, 1, 1, 1000));
    }

    @Override
    public void onDeath(final NPC npc, final Player killer) {
        if (Utils.random(1000) == 0) {
            npc.dropItem(killer, new Item(12007));
        }
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            if (random(384) == 0) {
                return new Item(11905, 1);
            }
            if (random(300) == 0) {
                return new Item(12004);
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] { 494, 496 };
    }
}
