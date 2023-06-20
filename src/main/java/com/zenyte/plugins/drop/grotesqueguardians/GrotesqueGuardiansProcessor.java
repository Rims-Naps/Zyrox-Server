package com.zenyte.plugins.drop.grotesqueguardians;

import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Tommeh | 04/08/2019 | 19:50
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class GrotesqueGuardiansProcessor extends DropProcessor {

    @Override
    public void attach() {
        //Granite maul
        appendDrop(new DisplayedDrop(4153, 1, 1, 125));
        //Granite gloves
        appendDrop(new DisplayedDrop(21736, 1, 1, 250));
        //Granite ring
        appendDrop(new DisplayedDrop(21739, 1, 1, 250));
        //Granite hammer
        appendDrop(new DisplayedDrop(21742, 1, 1, 375));
        //Black tourmaline core
        appendDrop(new DisplayedDrop(21730, 1, 1, 500));
        //Jar of stone
        appendDrop(new DisplayedDrop(21745, 1, 1, 2500));
    }

    @Override
    public void onDeath(final NPC npc, final Player killer) {
        if (random(2500) == 0) {
            npc.dropItem(killer, new Item(21745));
        }
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            if (random(500) == 0) {
                return new Item(21730);
            }
            if (random(375) == 0) {
                return new Item(21742);
            }
            if (random(250) == 0) {
                return new Item(Utils.random(1) == 0 ? 21736 : 21739);
            }
            if (random(125) == 0) {
                return new Item(4153);
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] { 7888 };
    }
}
