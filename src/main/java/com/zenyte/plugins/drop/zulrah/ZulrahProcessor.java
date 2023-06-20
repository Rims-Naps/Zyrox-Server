package com.zenyte.plugins.drop.zulrah;

import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Tommeh | 25-11-2018 | 18:52
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ZulrahProcessor extends DropProcessor {

    @Override
    public void attach() {
        //Dragon halberd
        appendDrop(new DropProcessor.DisplayedDrop(3204, 1, 1, 125));
        //Dragon med helm
        appendDrop(new DropProcessor.DisplayedDrop(1149, 1, 1, 125));
        //Tanzanite fang
        appendDrop(new DropProcessor.DisplayedDrop(12922, 1, 1, 384));
        //Magic fang
        appendDrop(new DropProcessor.DisplayedDrop(12932, 1, 1, 384));
        //Serpertine visage
        appendDrop(new DropProcessor.DisplayedDrop(12927, 1, 1, 384));
        //Uncut onyx
        appendDrop(new DropProcessor.DisplayedDrop(6571, 1, 1, 384));
        //Jar of swamp
        appendDrop(new DropProcessor.DisplayedDrop(12936, 1, 1, 3000));
        //Mutagens
        appendDrop(new DisplayedDrop(13200, 1, 1, 5000));
        appendDrop(new DisplayedDrop(13201, 1, 1, 5000));

        put(12934, new PredicatedDrop("Zulrah always rolls twice on the main drop table.<br>The below drop rates are for a single roll."));
    }

    @Override
    public void onDeath(final NPC npc, final Player killer) {
        if (Utils.random(3000) == 0) {
            npc.dropItem(killer, new Item(12936));
        }
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            int random;
            if (random(5000) == 0) {
                return new Item(13200 + Utils.random(1));
            }
            //Rare is doubled because zulrah rolls on the drop table twice every kill.
            if ((random = random(768)) < 4) {
                return new Item(random < 3 ? 12922 + (random * 5) : 6571);
            }
            if (random(125) == 0) {
                return new Item(Utils.random(1) == 0 ? 3204 : 1149);
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] { 2042, 2043, 2044 };
    }
}
