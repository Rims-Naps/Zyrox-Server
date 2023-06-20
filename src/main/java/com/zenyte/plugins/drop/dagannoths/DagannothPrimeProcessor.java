package com.zenyte.plugins.drop.dagannoths;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Tommeh | 25-11-2018 | 19:09
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class DagannothPrimeProcessor extends DropProcessor {

    @Override
    public void attach() {
        //Dragon axe
        appendDrop(new DropProcessor.DisplayedDrop(6739, 1, 1, 128));
        //Mud battlestaff
        appendDrop(new DropProcessor.DisplayedDrop(6562, 1, 1, 128));
        //Skeletal top
        appendDrop(new DropProcessor.DisplayedDrop(6139, 1, 1, 128));
        //Skeletal bottoms
        appendDrop(new DropProcessor.DisplayedDrop(6141, 1, 1, 128));
        //Farseer helm
        appendDrop(new DropProcessor.DisplayedDrop(3755, 1, 1, 128));
        //Seers ring
        appendDrop(new DropProcessor.DisplayedDrop(6731, 1, 1, 128));
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            int random;
            if ((random = random(128)) < 6) {
                val id = random == 0 ? 6739 : random == 1 ? 6562 : random == 2 ? 6139 : random == 3 ? 6141 : random == 4 ? 3755 : 6731;
                return new Item(id);
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] { 2266, 6497 };
    }
}
