package com.zenyte.plugins.drop;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 09/09/2019 17:31
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class RuneDragonProcessor extends DropProcessor {
    @Override
    public void attach() {
        //Dragon platelegs
        appendDrop(new DisplayedDrop(4087, 1, 1, 95));
        //Dragon plateskirt
        appendDrop(new DisplayedDrop(4585, 1, 1, 95));
        //Dragon med helm
        appendDrop(new DisplayedDrop(1149, 1, 1, 95));
        //Dragon bolts (unf)
        appendDrop(new DisplayedDrop(21930, 20, 40, 95));
        //Wrath talisman
        appendDrop(new DisplayedDrop(22118, 1, 1, 95));
        //Dragon limbs
        appendDrop(new DisplayedDrop(21918, 1, 1, 600));
        //Dragon metal lump
        appendDrop(new DisplayedDrop(22103, 1, 1, 3750));

        put(2363, new PredicatedDrop("This drop will be noted if you have the elite Western Provinces diary completed."));
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            if (random(3750) == 0) {
                return new Item(22103);
            }
            if (random(600) == 0) {
                return new Item(21918);
            }
            val random = random(95);
            if (random < 5) {
                if (random == 4) {
                    return new Item(21930, Utils.random(20, 40));
                }
                return new Item(random == 0 ? 4087 : random == 1 ? 4585 : random == 2 ? 1149 : 22118);
            }
        }
        if (item.getId() == 2363 && DiaryReward.WESTERN_BANNER4.eligibleFor(killer)) {
            item.setId(item.getDefinitions().getNotedId());
        }
        return item;
    }


    @Override
    public int[] ids() {
        return new int[] {
                8027, 8031, 8091
        };
    }
}
