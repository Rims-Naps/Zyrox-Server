package com.zenyte.plugins.drop.slayer;

import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessorLoader;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 09/10/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class DagannothProcessor extends DropProcessor {
    @Override
    public void attach() {
        //Archer helm
        appendDrop(new DisplayedDrop(3749, 1, 1, 256));
        //Berserker helm
        appendDrop(new DisplayedDrop(3751, 1, 1, 256));
        //Warrior helm
        appendDrop(new DisplayedDrop(3753, 1, 1, 256));
        //Farseer helm
        appendDrop(new DisplayedDrop(3755, 1, 1, 256));
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        val random = random(256);
        if (random <= 3) {
            return new Item(3749 + (random * 2));
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] {
                2259, 3185, 5942, 5943
        };
    }
}
