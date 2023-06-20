package com.zenyte.game.world.entity.npc.impl.wilderness.revenants;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import mgi.types.config.npcs.NPCDefinitions;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.val;

/**
 * @author Kris | 18/04/2019 19:37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class RevenantDropProcessor extends DropProcessor {

    @Override
    public void attach() {
        val ids = allIds;
        val goodDrops = Revenant.GoodRevenantDrop.values();
        val mediocreDrops = Revenant.MediocreReventantDrop.values();
        for (val id : ids) {
            val level = NPCDefinitions.getOrThrow(id).getCombatLevel();
            val clampedLevel = Math.max(1, Math.min(144, level));
            val chanceA = 2200 / ((int) Math.sqrt(clampedLevel));
            val chanceB = 15 + ((int) Math.pow(level + 60F, 2) / 200);
            for (val drop : goodDrops) {
                if (drop == Revenant.GoodRevenantDrop.REVENANT_WEAPON) {
                    val weight = chanceA * 40;
                    appendDrop(new DisplayedDrop(22557, 1, 1, (int) (weight * 2.5F), (p, npcId) -> npcId == id, id));
                    appendDrop(new DisplayedDrop(22542, 1, 1, weight * 5, (p, npcId) -> npcId == id, id));
                    appendDrop(new DisplayedDrop(22547, 1, 1, weight * 5, (p, npcId) -> npcId == id, id));
                    appendDrop(new DisplayedDrop(22552, 1, 1, weight * 5, (p, npcId) -> npcId == id, id));
                    put(id, 22557, new PredicatedDrop((p, npcId) -> npcId == id, "Drop rate is increased to 1 / " + Utils.format((int) ((chanceA * 14) * 2.5F)) + " while skulled."));
                    put(id, 22542, new PredicatedDrop((p, npcId) -> npcId == id, "Drop rate is increased to 1 / " + Utils.format((int) ((chanceA * 14) * 5F)) + " while skulled."));
                    put(id, 22547, new PredicatedDrop((p, npcId) -> npcId == id, "Drop rate is increased to 1 / " + Utils.format((int) ((chanceA * 14) * 5F)) + " while skulled."));
                    put(id, 22552, new PredicatedDrop((p, npcId) -> npcId == id, "Drop rate is increased to 1 / " + Utils.format((int) ((chanceA * 14) * 5F)) + " while skulled."));
                    continue;
                }
                if (drop.ordinal() <= Revenant.GoodRevenantDrop.ANCIENT_CRYSTAL.ordinal()) {
                    if (drop == Revenant.GoodRevenantDrop.ANCIENT_CRYSTAL) {
                        val weight = chanceA * 14;
                        put(id, drop.getItem().getId(), new PredicatedDrop((p, npcId) -> npcId == id, "Drop rate is decreased to 1 / " + Utils.format(weight) + " while skulled."));
                    } else {
                        val weight = chanceA * 14 / (1 + drop.getRange().getEndInclusive() - drop.getRange().getStart());
                        if (!getInfoMap().containsKey(drop.getItem().getId())) {
                            put(id, drop.getItem().getId(), new PredicatedDrop((p, npcId) -> npcId == id, "Drop rate is increased to 1 / " + Utils.format(weight) + " while skulled."));
                        }
                    }
                }
                val weight = chanceA * 40 / ((drop.getRange().getEndInclusive() - drop.getRange().getStart()) + 1);
                appendDrop(new DisplayedDrop(drop.getItem().getId(), drop.getItem().getMinAmount(), drop.getItem().getMaxAmount(), weight, (p, npcId) -> npcId == id, id));
            }
            for (val drop : mediocreDrops) {
                val weight = (int) ((float) chanceA / (chanceB - 1) * 106F / drop.getWeight());
                appendDrop(new DisplayedDrop(drop.getItem().getId(), drop.getItem().getMinAmount(), drop.getItem().getMaxAmount(), weight, (p, npcId) -> npcId == id, id));
            }
            appendDrop(new DisplayedDrop(21817, 1, 1, (int) ((float) chanceA / (chanceB - 1) * 106F / 15), (p, npcId) -> npcId == id, id));
            appendDrop(new DisplayedDrop(21820, 1, Math.max(1, (int) Math.sqrt(level * 3)), 1, (p, npcId) -> npcId == id, id));
            val coinsChance = (float) chanceA / (chanceA - chanceB);
            if (coinsChance > 0) {
                appendDrop(new DisplayedDrop(995, 50, 500, coinsChance, (p, npcId) -> npcId == id, id));
            }
        }
    }

    @Override
    public int[] ids() {
        val set = new IntOpenHashSet();
        set.add(7881);
        for (int i = 7931; i <= 7940; i++) {
            set.add(i);
        }
        return set.toIntArray();
    }
}
