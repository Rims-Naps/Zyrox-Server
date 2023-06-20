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
 * @author Tommeh | 25-11-2018 | 18:29
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ChaosElementalProcessor extends DropProcessor {

    @Override
    public void attach() {
        //Dragon 2h sword
        appendDrop(new DisplayedDrop(ItemId.DRAGON_2H_SWORD, 1, 1, 32)); //96
        //Dragon pickaxe
        appendDrop(new DisplayedDrop(ItemId.DRAGON_PICKAXE, 1, 1, 120)); //192
        for(val i : PVPEquipment.values()) {
            appendDrop(new DisplayedDrop(i.getItemId(), i.getQuantity(), i.getQuantity(), (int) (250.0 / ((double)i.getWeight() / (double) PVPEquipment.getTotalWeighting()))));
        }
    }

    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            if (random(120) == 0) {
                return new Item(ItemId.DRAGON_PICKAXE);
            }
            if (random(32) == 0) {
                return new Item(ItemId.DRAGON_2H_SWORD);
            }
            if(random(250) == 0) {
                return PVPEquipment.roll();
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] { 2054, 6505 };
    }
}
