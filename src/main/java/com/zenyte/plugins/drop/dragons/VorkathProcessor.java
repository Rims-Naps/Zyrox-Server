package com.zenyte.plugins.drop.dragons;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Tommeh | 25-11-2018 | 18:23
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class VorkathProcessor extends DropProcessor {

    @Override
    public void onDeath(NPC npc, Player killer) {
        if (killer.getKillcount(npc) == 49) {
            npc.dropItem(killer, new Item(21907));
        }
        if (random(49) == 0) {
            npc.dropItem(killer, new Item(ItemId.CRYSTAL_OF_AMLODD));
        }
    }

    @Override
    public void attach() {
        //Vorkath's head
        appendDrop(new DisplayedDrop(21907, 1, 1, 50));
        //Dragonbone necklace
        appendDrop(new DisplayedDrop(22111, 1, 1, 750));
        //Skeletal visage
        appendDrop(new DisplayedDrop(22006, 1, 1, 2000));
        //Jar of decay
        appendDrop(new DisplayedDrop(22106, 1, 1, 3000));
        appendDrop(new DisplayedDrop(22869,1,1,3125));

        //Amlodd crystal
        appendDrop(new DisplayedDrop(ItemId.CRYSTAL_OF_AMLODD,1,1,50));

        put(21907, new PredicatedDrop("Vorkath's head is always dropped on the 50th Vorkath kill."));
        put(1751, new PredicatedDrop("Vorkath always rolls twice on the main drop table.<br>The below drop rates are for a single roll."));
    }

    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            //All drop rates are doubled in here because vorkath rolls on the table twice every kill.
            int random;
            if (random(5000) == 0) {
                return new Item(11286);
            }
            if (random(4000) == 0) {
                return new Item(22006);
            }
            if (random(6000) == 0) {
                return new Item(22106);
            }
            if (random(2000) == 0) {
                return new Item(22111);
            }
            if (random(100) == 0) {
                return new Item(21907);
            }
            if (random(3125) == 0) {
                return new Item(ItemId.CELASTRUS_SEED);
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] { 8058, 8059, 8060, 8061 };
    }
}
