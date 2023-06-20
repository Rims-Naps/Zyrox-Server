package com.zenyte.game.content.skills.prayer.ectofuntus;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.ItemChat;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.val;

/**
 * @author Kris | 23/06/2019 12:44
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BonesOnEctofuntusLoader implements ItemOnObjectAction {
    @Override
    public void handleItemOnObjectAction(final Player player, final Item item, final int slot, final WorldObject object) {
        if (item.getId() == 22124) {
            if (player.getSkills().getLevelForXp(Skills.PRAYER) < 70) {
                player.getDialogueManager().start(new ItemChat(player, item, "You need a Prayer level of at least 70 to grind superior dragon bones."));
                return;
            }
        }
        player.getActionManager().setAction(new BoneGrinding(BoneGrinding.Stage.ADDING_BONES, item));
    }

    @Override
    public Object[] getItems() {
        val list = new IntOpenHashSet();
        for (val bonemeal : BoneGrinding.Bonemeal.values) {
            for (val item : bonemeal.getBones().getBones()) {
                list.add(item.getId());
            }
        }
        return list.toArray();
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                16654
        };
    }
}
