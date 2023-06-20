package com.zenyte.plugins.drop.dagannoths;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Tommeh | 25-11-2018 | 19:06
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class DagannothRexProcessor extends DropProcessor {

    @Override
    public void attach() {
        //Dragon axe
        appendDrop(new DropProcessor.DisplayedDrop(6739, 1, 1, 128));
        //Rock-shell plate
        appendDrop(new DropProcessor.DisplayedDrop(6129, 1, 1, 128));
        //Rock-shell legs
        appendDrop(new DropProcessor.DisplayedDrop(6130, 1, 1, 128));
        //Ring of life
        appendDrop(new DropProcessor.DisplayedDrop(2570, 1, 1, 128));
        //Berserker ring
        appendDrop(new DropProcessor.DisplayedDrop(6737, 1, 1, 128));
        //Warrior ring
        appendDrop(new DropProcessor.DisplayedDrop(6735, 1, 1, 128));
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            int random;
            if ((random = random(128)) < 6) {
                val id = random == 0 ? 6739 : random == 1 ? 6129 : random == 2 ? 6130 : random == 3 ? 2570 : random == 4 ? 6737 : 6735;
                return new Item(id);
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] { 2267, 6498 };
    }
}
