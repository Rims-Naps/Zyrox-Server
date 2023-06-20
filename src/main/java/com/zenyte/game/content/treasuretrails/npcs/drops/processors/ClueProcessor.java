package com.zenyte.game.content.treasuretrails.npcs.drops.processors;

import com.zenyte.game.content.treasuretrails.ClueItem;
import com.zenyte.game.content.treasuretrails.npcs.drops.PredicatedClueDrop;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.IntArray;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.val;
import mgi.Indice;
import mgi.types.config.npcs.NPCDefinitions;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author Kris | 22/11/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public abstract class ClueProcessor extends DropProcessor {

    private final int[] faladorGuardIds = IntArray.of(3269, 3270, 3271, 3272);

    @Override
    public void attach() {
        for (final int i : allIds) {
            val definitions = NPCDefinitions.get(i);
            if (definitions == null) {
                continue;
            }
            val clueNPCs = map().get(definitions.getName().toLowerCase());
            val clueNPC = Utils.findMatching(clueNPCs, n -> {
                final Predicate<NPCDefinitions> predicate = n.getPredicate();
                return predicate == null || predicate.test(definitions);
            });
            if (clueNPC == null) {
                continue;
            }
            val itemId = itemId();
            appendDrop(new DisplayedDrop(itemId, 1, 1, clueNPC.getRate(), (player, npcId) -> npcId == i, i));
            if (itemId != ClueItem.BEGINNER.getScrollBox()) {
                if (definitions.getName().equalsIgnoreCase("Guard") && definitions.getCombatLevel() >= 19 && definitions.getCombatLevel() <= 22) {
                    put(i, itemId, new PredicatedDrop("The drop rate is lowered to 1/115 after completion of the medium Falador diary."));
                } else {
                    val halvedRate = (int) Math.floor(clueNPC.getRate() / 2);
                    put(i, itemId, new PredicatedDrop("The drop rate is lowered to 1/" + halvedRate + " when killed in Wilderness while wielding an imbued ring of wealth."));
                }
            }
        }
    }

    @Override
    public void onDeath(final NPC npc, final Player killer) {
        val definitions = npc.getDefinitions();
        val list = map().get(definitions.getLowercaseName());
        if (list == null || list.isEmpty()) {
            return;
        }
        val constant = Utils.findMatching(list, n -> {
            final Predicate<NPCDefinitions> predicate = n.getPredicate();
            return predicate == null || predicate.test(definitions);
        });
        if (constant == null) {
            return;
        }
        val itemId = itemId();
        val ring = killer.getEquipment().getItem(EquipmentSlot.RING);
        val isImbuedRingOfWealth = ring != null && ring.getName().startsWith("Ring of wealth (i");
        val boosted = itemId != ClueItem.BEGINNER.getScrollBox() && isImbuedRingOfWealth
                && WildernessArea.isWithinWilderness(npc.getX(), npc.getY());
        double rate = ArrayUtils.contains(faladorGuardIds, npc.getId()) ? (constant.getRate() * 0.9) : boosted ? (constant.getRate() / 2) : constant.getRate();
        if (itemId == ClueItem.EASY.getScrollBox() && killer.getBooleanAttribute("Obtained easy Ca Rewards")) {
            rate *= 0.95;
        }
        if (itemId == ClueItem.MEDIUM.getScrollBox() && killer.getBooleanAttribute("Obtained medium Ca Rewards")) {
            rate *= 0.95;
        }
        if (itemId == ClueItem.HARD.getScrollBox() && killer.getBooleanAttribute("Obtained hard Ca Rewards")) {
            rate *= 0.95;
        }
        if (itemId == ClueItem.ELITE.getScrollBox() && killer.getBooleanAttribute("Obtained elite Ca Rewards")) {
            rate *= 0.95;
        }
        if (Utils.randomDouble() < 1F / rate) {
            npc.dropItem(killer, new Item(itemId));
        }
    }

    protected abstract int itemId();
    protected abstract Map<String, List<PredicatedClueDrop>> map();

    @Override
    public int[] ids() {
        val set = new IntOpenHashSet();
        loop : for (int i = 0; i < Utils.getIndiceSize(Indice.NPC_DEFINITIONS); i++) {
            val definitions = NPCDefinitions.get(i);
            if (definitions == null || definitions.getCombatLevel() == 0) {
                continue;
            }
            val name = definitions.getName().toLowerCase();
            val clueNPCs = map().get(name);
            if (clueNPCs == null) {
                continue;
            }
            for (val npc : clueNPCs) {
                val predicate = npc.getPredicate();
                if (predicate == null || predicate.test(definitions)) {
                    set.add(i);
                    continue loop;
                }
            }
        }
        return set.toIntArray();
    }
}
