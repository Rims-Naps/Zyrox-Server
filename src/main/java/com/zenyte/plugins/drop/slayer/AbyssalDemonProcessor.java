package com.zenyte.plugins.drop.slayer;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-11-2018 | 18:46
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class AbyssalDemonProcessor extends DropProcessor {
    private static int[] bludgeonParts = new int[] {ItemId.BLUDGEON_CLAW, ItemId.BLUDGEON_SPINE, ItemId.BLUDGEON_AXON};

    @Override
    public void attach() {
        //Abyssal whip
        appendDrop(new DropProcessor.DisplayedDrop(4151, 1, 1, 384));
        //Abyssal dagger
        appendDrop(new DropProcessor.DisplayedDrop(13265, 1, 1, 32768));
        //Abyssal bludgeon parts
        appendDrop(new DropProcessor.DisplayedDrop(ItemId.BLUDGEON_CLAW, 1, 1, 384));
        appendDrop(new DropProcessor.DisplayedDrop(ItemId.BLUDGEON_SPINE, 1, 1, 384));
        appendDrop(new DropProcessor.DisplayedDrop(ItemId.BLUDGEON_AXON, 1, 1, 384));
        //Abyssal head
        appendDrop(new DropProcessor.DisplayedDrop(7979, 1, 1, 3000));

        put(ItemId.BLUDGEON_CLAW, new PredicatedDrop("Always dropped in the order of claw, spine, axon."));
        put(ItemId.BLUDGEON_SPINE, new PredicatedDrop("Always dropped in the order of claw, spine, axon."));
        put(ItemId.BLUDGEON_AXON, new PredicatedDrop("Always dropped in the order of claw, spine, axon."));
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            if (random(32767) == 0) {
                return new Item(13265);
            }
            if (random(2999) == 0) {
                return new Item(7979);
            }
            if (random(383/2) == 0) {
                if(random(2) == 0) {
                    return new Item(findLowestQuantityBludgeonPart(killer));
                } else {
                    return new Item(4151);
                }
            }
        }
        return item;
    }

    public static final int findLowestQuantityBludgeonPart(@NotNull final Player killer) {
        var previousAmount = Integer.MAX_VALUE;
        var previousItem = bludgeonParts[0];
        for (val part : bludgeonParts) {
            val amount = killer.getAmountOf(part);
            if (amount < previousAmount) {
                previousAmount = amount;
                previousItem = part;
            }
        }
        return previousItem;
    }

    @Override
    public int[] ids() {
        return new int[] { 415, 416, 7241 };
    }
}
