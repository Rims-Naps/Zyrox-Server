package com.zenyte.plugins.drop.dagannoths;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Tommeh | 25-11-2018 | 19:00
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class DagannothSupremeProcessor extends DropProcessor {

    @Override
    public void attach() {
        //Dragon axe
        appendDrop(new DropProcessor.DisplayedDrop(6739, 1, 1, 128));
        //Seercull
        appendDrop(new DropProcessor.DisplayedDrop(6724, 1, 1, 128));
        //Spined body
        appendDrop(new DropProcessor.DisplayedDrop(6133, 1, 1, 128));
        //Spined chaps
        appendDrop(new DropProcessor.DisplayedDrop(6135, 1, 1, 128));
        //Archer helm
        appendDrop(new DropProcessor.DisplayedDrop(3749, 1, 1, 128));
        //Archers ring
        appendDrop(new DropProcessor.DisplayedDrop(6733, 1, 1, 128));
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            int random;
            if ((random = random(128)) < 6) {
                val id = random == 0 ? 6739 : random == 1 ? 6724 : random == 2 ? 6133 : random == 3 ? 6135 : random == 4 ? 3749 : 6733;
                return new Item(id);
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] { 2265, 6496 };
    }
}
