package com.zenyte.plugins.drop.wilderness;

import com.zenyte.game.content.PVPEquipment;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Tommeh | 25-11-2018 | 18:40
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class VenenatisProcessor extends DropProcessor {

    @Override
    public void attach() {
        //Dragon pickaxe
        appendDrop(new DropProcessor.DisplayedDrop(11920, 1, 1, 128));
        //Dragon 2h sword
        appendDrop(new DropProcessor.DisplayedDrop(7158, 1, 1, 192));
        //Treasonous ring ring
        appendDrop(new DropProcessor.DisplayedDrop(12605, 1, 1, 384));

        appendDrop(new DisplayedDrop(ItemId.SUPER_RESTORE4, 3, 3, 14));

        put(ItemId.SUPER_RESTORE4, new DropProcessor.PredicatedDrop("This is always dropped with the Dark Crabs"));

        for(val i : PVPEquipment.values()) {
            appendDrop(new DisplayedDrop(i.getItemId(), i.getQuantity(), i.getQuantity(), (int) (250.0 / ((double)i.getWeight() / (double) PVPEquipment.getTotalWeighting()))));
        }
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            if (random(384) == 0) {
                return new Item(12605);
            }
            if (random(192) == 0) {
                return new Item(7158);
            }
            if (random(128) == 0) {
                return new Item(11920);
            }
            if(random(250) == 0) {
                return PVPEquipment.roll();
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] { 6504, 6610 };
    }
}
