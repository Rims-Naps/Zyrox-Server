package com.zenyte.plugins.drop.kalphitequeen;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 22/11/2018 15:49
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class KalphiteQueenProcessor extends DropProcessor {

    @Override
    public void attach() {
        //Dragon chainbody
        appendDrop(new DisplayedDrop(3140, 1, 1, 128));
        //Dragon 2h sword
        appendDrop(new DisplayedDrop(7158, 1, 1, 256));
        //Kq head
        appendDrop(new DisplayedDrop(7981, 1, 1, 100));
        //Kq head (tattered)
        appendDrop(new DisplayedDrop(22671, 1, 1, 256));
        //Jar of Sand
        appendDrop(new DisplayedDrop(ItemId.JAR_OF_SAND, 1, 1, 2000));
        put(22671, new PredicatedDrop("Players will receive the tattered head only on their 256th kill."));
    }

    public void onDeath(final NPC npc, final Player killer) {
        if (Utils.random(1999) == 0) {
            npc.dropItem(killer, new Item(ItemId.JAR_OF_SAND));
        }
        if (killer.getKillcount(npc) == 256) {
            npc.dropItem(killer, new Item(22671));
        }
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            if (random(99) == 0) {
                return new Item(7981);
            }
            if (random(127) == 0) {
                return new Item(3140);
            }
            if (random(255) == 0) {
                return new Item(7158);
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] { 128, 963, 965, 4303, 4304, 6500, 6501 };
    }
}
