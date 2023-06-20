package com.zenyte.plugins.drop.slayer;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;
import lombok.var;

/**
 * @author Kris | 10/06/2019 03:38
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class DemonicGorillaDropProcessor extends DropProcessor {
    @Override
    public void attach() {
        //Zenyte shard
        appendDrop(new DisplayedDrop(19529, 1, 1, 225));
        //Limbs
        appendDrop(new DisplayedDrop(19592, 1, 1, 500));
        //Spring
        appendDrop(new DisplayedDrop(19601, 1, 1, 500));
        //Light frame
        appendDrop(new DisplayedDrop(19586, 1, 1, 750));
        //Heavy frame
        appendDrop(new DisplayedDrop(19589, 1, 1, 1500));
        //Monkey tail
        appendDrop(new DisplayedDrop(19610, 1, 1, 1500));
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            var random = random(1500);
            if (random <= 1) {
                return random == 0 ? new Item(19589) : new Item(19610);
            }
            random = random(750);
            if (random == 0) {
                return new Item(19586);
            }
            random = random(500);
            if (random <= 1) {
                return random == 0 ? new Item(19592) : new Item(19601);
            }
            if ((random(225)) == 0) {
                return new Item(19529);
            }
        }
        return super.drop(npc, killer, drop, item);
    }

    @Override
    public int[] ids() {
        return new int[] {
                7144, 7145, 7146, 7147, 7148, 7149, 7152
        };
    }
}
