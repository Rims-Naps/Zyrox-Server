package com.zenyte.game.content.kebos.alchemicalhydra.processor;

import com.zenyte.game.content.kebos.alchemicalhydra.HydraPhase;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 11/11/2019 | 23:59
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class AlchemicalHydraProcessor extends DropProcessor {

    private static final int[] ringParts = {
            ItemId.HYDRAS_EYE, ItemId.HYDRAS_FANG, ItemId.HYDRAS_HEART
    };

    @Override
    public void attach() {
        appendDrop(new DisplayedDrop(ItemId.DRAGON_THROWNAXE, 500, 1000, 1500));
        appendDrop(new DisplayedDrop(ItemId.DRAGON_KNIFE, 500, 1000, 1500));
        appendDrop(new DisplayedDrop(ItemId.HYDRAS_EYE, 1, 1, 135 * 3));
        appendDrop(new DisplayedDrop(ItemId.HYDRAS_FANG, 1, 1, 135 * 3));
        appendDrop(new DisplayedDrop(ItemId.HYDRAS_HEART, 1, 1, 135 * 3));
        appendDrop(new DisplayedDrop(ItemId.HYDRA_TAIL, 1, 1, 385));
        appendDrop(new DisplayedDrop(ItemId.HYDRA_LEATHER, 1, 1, 385));
        appendDrop(new DisplayedDrop(ItemId.HYDRAS_CLAW, 1, 1, 650));
        appendDrop(new DisplayedDrop(ItemId.ALCHEMICAL_HYDRA_HEADS, 1, 1, 256));
        appendDrop(new DisplayedDrop(ItemId.JAR_OF_CHEMICALS, 1, 1, 2000));
        //Crystal of Iorwerth
        appendDrop(new DisplayedDrop(ItemId.CRYSTAL_OF_IORWERTH, 1, 1, 50));

        put(ItemId.HYDRAS_EYE, new PredicatedDrop("The Hydra's eye has a 1/405 drop rate, or a 1/135 drop rate to receive any of the Brimstone ring pieces."));
        put(ItemId.HYDRAS_FANG, new PredicatedDrop("The Hydra's fang has a 1/405 drop rate, or a 1/135 drop rate to receive any of the Brimstone ring pieces."));
        put(ItemId.HYDRAS_HEART, new PredicatedDrop("The Hydra's heart has a 1/405 drop rate, or a 1/135 drop rate to receive any of the Brimstone ring pieces."));

        put(ItemId.MYSTIC_FIRE_STAFF, new PredicatedDrop("Mystic fire staff is always dropped alongside mystic water staff."));
        put(ItemId.RUNE_PLATEBODY, new PredicatedDrop("Rune platebody is always dropped alongside either rune platelegs or plateskirt, depending on gender.."));
        put(ItemId.MYSTIC_ROBE_TOP_LIGHT, new PredicatedDrop("Mystic robe top (light) is always dropped alongside mystic robe bottoms (light)."));
        put(ItemId.RANGING_POTION3, new PredicatedDrop("Ranging potion(3) is always dropped alongside 2 x super restore(3)."));
    }

    @Override
    public void onDeath(final NPC npc, final Player killer) {
        if (Utils.random(256) == 0) {
            npc.dropItem(killer, new Item(ItemId.ALCHEMICAL_HYDRA_HEADS));
        }
        if (Utils.random(2000) == 0) {
            npc.dropItem(killer, new Item(ItemId.JAR_OF_CHEMICALS));
        }
        if (Utils.random(49) == 0) {
            npc.dropItem(killer, new Item(ItemId.CRYSTAL_OF_IORWERTH));
        }
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (!drop.isAlways()) {
            int random;
            if (random(1500) == 0) {
                return new Item(Utils.random(1) == 0 ? ItemId.DRAGON_THROWNAXE : ItemId.DRAGON_KNIFE, Utils.random(500, 1000));
            }
            if (random(650) == 0) {
                return new Item(ItemId.HYDRAS_CLAW);
            }
            if ((random = random(385)) < 2) {
                return new Item(random == 0 ? ItemId.HYDRA_TAIL : ItemId.HYDRA_LEATHER);
            }
            if ((random(135 * 3)) < 3) {
                return new Item(findLowestQuantityRingPart(killer));
            }
        }
        return item;
    }

    public static final int findLowestQuantityRingPart(@NotNull final Player killer) {
        var previousAmount = Integer.MAX_VALUE;
        var previousItem = ringParts[0];
        for (val part : ringParts) {
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
        return new int[] {HydraPhase.ENRAGED.getPostTransformation()};
    }
}
