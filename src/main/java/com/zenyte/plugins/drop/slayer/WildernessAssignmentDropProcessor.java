package com.zenyte.plugins.drop.slayer;

import com.zenyte.game.content.PVPEquipment;
import com.zenyte.game.content.skills.slayer.BossTask;
import com.zenyte.game.content.skills.slayer.RegularTask;
import com.zenyte.game.content.skills.slayer.SlayerMaster;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.combatdefs.NPCCDLoader;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.npc.spawns.NPCSpawnLoader;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import com.zenyte.utils.Ordinal;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import mgi.Indice;
import mgi.types.config.npcs.NPCDefinitions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntPredicate;

/**
 * @author Kris | 20/04/2019 18:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class WildernessAssignmentDropProcessor extends DropProcessor {

    @Ordinal
    @AllArgsConstructor
    public enum Emblem {
        T1(12746, 10000),
        T2(12748, 9000),
        T3(12749, 7000),
        T4(12750, 5000),
        T5(12751, 2500),
        T6(12752, 1000),
        T7(12753, 500),
        T8(12754, 200),
        T9(12755, 100),
        T10(12756, 25);

        @Getter  private final int id, chance;

        private static int total;
        private static final Emblem[] values = values();
        private static final Map<Integer, Emblem> all = new HashMap<>(values.length);

        public int next() {
            val next = values[ordinal() + 1];
            return next == null ? -1 : next.id;
        }

        static {
            for (val value : values) {
                total += value.chance;
                all.put(value.id, value);
            }
        }

        public static Emblem get(final Item item) {
            return all.get(item.getId());
        }

        private static final Emblem get() {
            int random = Utils.random(total);
            int current = 0;
            for (val reward : values) {
                if ((current += reward.chance) >= random) {
                    return reward;
                }
            }
            return null;
        }
    }

    @Override
    public void attach() {
        for (val i : allIds) {
            val definitions = NPCDefinitions.get(i);
            if (definitions == null) {
                continue;
            }
            val combatDefinitions = NPCCDLoader.get(i);
            if (combatDefinitions == null) {
                continue;
            }
            val hitpoints = combatDefinitions.getHitpoints();
            val name = definitions.getName().toLowerCase();
            val emblemChance = 1F / (155 - (hitpoints / 2F));
            val percentage = emblemChance * 100F;
            val fraction = (int)(100F / percentage);
            appendDrop(new DisplayedDrop(12746, 1, 1, fraction, (player, npcId) -> npcId == i, i));
            put(i, 12746, new PredicatedDrop("Only dropped by those found in Wilderness while on a slayer assignment from Krystilia. May occasionally drop as a higher tier."));

            boolean isBossTask = false;
            for (val task : BossTask.VALUES) {
                if (name.equals(task.getTaskName().toLowerCase())) {
                    isBossTask = true;
                    break;
                }
            }
            val enchantmentChance = (1F / (320 - (hitpoints * 0.8F)));
            val enchantmentPercentage = enchantmentChance * 100F;
            val enchantmentFraction = (int)(100F / enchantmentPercentage);

            appendDrop(new DisplayedDrop(21257, 1, 1, isBossTask ? 30 : enchantmentFraction, (player, npcId) -> npcId == i, i));
            put(i, 21257, new PredicatedDrop("Only dropped by those found in Wilderness while on a slayer assignment from Krystilia."));

            val skipChance = 1F / (155 - (hitpoints / 2F));
            val skipPercentage = skipChance * 100F;
            val skipFraction = (int)(100F / skipPercentage);
            appendDrop(new DisplayedDrop(30568, 1, 1, isBossTask ? 30 : skipFraction, (player, npcId) -> npcId == i, i));
            put(i, 30568, new PredicatedDrop("Only dropped by those found in Wilderness while on a slayer assignment from Krystilia."));

            val sinChance = 1F / (155 - (hitpoints / 2F));
            val sinPercentage = sinChance * 100F;
            val sinFraction = (int)(100F / sinPercentage);
            appendDrop(new DisplayedDrop(993, 1, 1, isBossTask ? 30 : sinFraction, (player, npcId) -> npcId == i, i));
            put(i, 993, new PredicatedDrop("Only dropped by those found in Wilderness while on a slayer assignment from Krystilia."));

            if(definitions.getCombatLevel() >= 70) {
                appendDrop(new DisplayedDrop(30669, 1, 1, 3750, (player, npcId) -> npcId == i, i));
                put(i, 30669, new PredicatedDrop("On a Wilderness Slayer task there is a chance to roll on the PVP Equipment table, equal chance for each item."));

                appendDrop(new DisplayedDrop(30540, 1, 1, 50, (player, npcId) -> npcId == i, i));
                put(i, 30540, new PredicatedDrop("Only dropped by those found in Wilderness while on a slayer assignment from Krystilia."));
            }
        }
    }

    @Override
    public void onDeath(final NPC npc, final Player killer) {
        val slayer = killer.getSlayer();
        val assignment = slayer.getAssignment();
        if (assignment == null || assignment.getMaster() != SlayerMaster.KRYSTILIA || !assignment.isValid(killer, npc) || !WildernessArea.isWithinWilderness(npc.getX(), npc.getY())) {
            return;
        }
        val emblemChance = 1F / (155 - (npc.getMaxHitpoints() / 2F));
        if (Utils.randomDouble() <= emblemChance) {
            npc.dropItem(killer, new Item(Objects.requireNonNull(Emblem.get()).id));
        }
        val enchantmentChance = assignment.getTask() instanceof BossTask ? 0.0333F : (1F / (320 - (npc.getMaxHitpoints() * 0.8F)));
        if (Utils.randomDouble() <= enchantmentChance) {
            npc.dropItem(killer, new Item(21257));
        }
        val scrollChance = 1F / (155 - (npc.getMaxHitpoints() / 2F));
        if (Utils.randomDouble() <= scrollChance) {
            npc.dropItem(killer, new Item(ItemId.SLAYER_SKIP_SCROLL));
        }
        val sinkeyChance = 1F / (155 - (npc.getMaxHitpoints() / 2F));
        if (Utils.randomDouble() <= sinkeyChance) {
            npc.dropItem(killer, new Item(ItemId.SINISTER_KEY));
        }
       //if(npc.getCombatLevel() >= 70 && random(3750) == 0) {
         //   PVPEquipment drop = PVPEquipment.values()[random(PVPEquipment.values().length)];
        //    npc.dropItem(killer, new Item(drop.getItemId(), drop.getQuantity()));
        //    killer.sendMessage("You notice a piece of ancient equipment fall from the creature...");
        if(npc.getCombatLevel() >= 70 && random(50) == 0) {
            npc.dropItem(killer, new Item(30540,1));
            killer.sendMessage("You notice a larran key fall from the creature...");
        }
        
    }

    @Override
    public int[] ids() {
        val set = new IntOpenHashSet();
        val names = new ObjectLinkedOpenHashSet<Pair<String, Boolean>>();
        loop : for (val regularTask : RegularTask.VALUES) {
            for (val entry : regularTask.getTaskSet()) {
                if (entry.getSlayerMaster() == SlayerMaster.KRYSTILIA) {
                    for (val name : regularTask.getMonsters()) {
                        names.add(new Pair<>(name.toLowerCase(), true));
                    }
                    continue loop;
                }
            }
        }

        for (val bossTaskTask : BossTask.VALUES) {
            if (!bossTaskTask.isAssignableByKrystilia()) {
                continue;
            }
            names.add(new Pair<>(bossTaskTask.getTaskName().toLowerCase(), true));
        }

        loop : for (int i = 0; i < Utils.getIndiceSize(Indice.NPC_DEFINITIONS); i++) {
            val definitions = NPCDefinitions.get(i);
            if (definitions == null || definitions.getCombatLevel() == 0) {
                continue;
            }
            val name = definitions.getName().toLowerCase();
            for (val validName : names) {
                if (validName.getSecond() ? name.equals(validName.getFirst()) : name.contains(validName.getFirst())) {
                    set.add(i);
                    continue loop;
                }
            }
        }

        val wildyNPCs = new IntOpenHashSet();

        for (val spawn : NPCSpawnLoader.DEFINITIONS) {
            if (WildernessArea.isWithinWilderness(spawn.getX(), spawn.getY())) {
                wildyNPCs.add(spawn.getId());
            }
        }
        wildyNPCs.add(6616);
        wildyNPCs.add(6617);
        wildyNPCs.add(6612);
        set.removeIf((IntPredicate) id -> !wildyNPCs.contains(id));

        return set.toIntArray();
    }

}
