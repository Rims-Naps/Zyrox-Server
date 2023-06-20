package com.zenyte.game.world.entity.npc.drop.matrix;

import com.zenyte.Constants;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;

import java.io.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

@Slf4j
public class NPCDrops {

    @AllArgsConstructor
    @Getter
    public static final class DropTable {
        @Setter private int npcId;
        private transient int weight;
        private final Drop[] drops;
    }

    @Getter
    public static final class DisplayedNPCDrop {

        private int itemId, minAmount, maxAmount;
        private BiFunction<Player, Integer, Double> function;
        @Setter private BiPredicate<Player, Integer> predicate;

        public DisplayedNPCDrop(final int itemId, final int minAmount, final int maxAmount, final double weight, final int tableWeight) {
            this.itemId = itemId;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.function = (player, npcId) -> weight == 100_000 ? 1 : (weight / tableWeight * 100D);
        }

        public DisplayedNPCDrop(final int itemId, final int minAmount, final int maxAmount, final BiFunction<Player, Integer, Double> function) {
            this.itemId = itemId;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.function = function;
        }
    }

    @AllArgsConstructor
    @Getter
    public static final class DisplayedDropTable {
        @Setter private int npcId;
        private final List<DisplayedNPCDrop> drops;
    }

    public static Int2ObjectOpenHashMap<DropTable> drops;
    public static Int2ObjectOpenHashMap<DisplayedDropTable> displayedDrops;
    public static Int2ObjectOpenHashMap<List<ItemDrop>> dropsByItem;


    public static Drop[] getDrops(final int npcId) {
		val table = drops.get(npcId);
		if (table == null)
		    return null;
		return table.drops;
	}

	public static final DropTable getTable(final int npcId) {
	    return drops.get(npcId);
    }

    public static final List<ItemDrop> getTableForItem(final int itemId) {
        return dropsByItem.get(itemId);
    }

    public static final boolean equalsIgnoreRates(final int npc1, final int npc2) {
	    val processorA = DropProcessorLoader.get(npc1);
	    val processorB = DropProcessorLoader.get(npc2);
	    val tableA = drops.get(npc1);
	    val tableB = drops.get(npc2);
	    if (tableA == tableB && Objects.equals(processorA, processorB)) {
	        return true;
        }
	    if (tableA == null || tableB == null) {
	        return false;
        }
	    if (tableA.drops.length != tableB.drops.length) {
	        return false;
        }
	    for (int i = 0; i < tableA.drops.length; i++) {
	        val drop = tableA.drops[i];
	        if (Utils.findMatching(tableB.drops, d -> d.getItemId() == drop.getItemId() && d.getMinAmount() == drop.getMinAmount() && d.getMaxAmount() == drop.getMaxAmount()) != null) {
	            continue;
            }
	        return false;
        }
	    return Objects.equals(processorA, processorB);
    }

    /**
     * Do not make a subscribable event out of this as it needs to be executed before those.
     */
	public static final void init() {
        try {
            val reader = new BufferedReader(new FileReader("data/npcs/drops.json"));
            val definitions = World.getGson().fromJson(reader, DropTable[].class);
            drops = new Int2ObjectOpenHashMap<>((int) Math.ceil(definitions.length / 0.75F));
            displayedDrops = new Int2ObjectOpenHashMap<>((int) Math.ceil(definitions.length / 0.75F));
            dropsByItem = new Int2ObjectOpenHashMap<>(ItemDefinitions.definitions.length);
            for (val definition : definitions) {
                int weight = 0;
                for (val drop : definition.getDrops()) {
                    if (drop.getRate() == 100_000) {
                        continue;
                    }
                    weight += drop.getRate();
                }
                Arrays.sort(definition.getDrops(), Comparator.comparingInt(Drop::getRate));
                definition.weight = weight;
                drops.put(definition.npcId, definition);

                val dropsList = new ObjectArrayList<DisplayedNPCDrop>();
                for (val drop : definition.drops) {
                    if (drop.getItemId() == ItemId.TOOLKIT) {
                        continue;
                    }
                    dropsList.add(new DisplayedNPCDrop(drop.getItemId(), drop.getMinAmount(), drop.getMaxAmount(), drop.getRate(), weight));
                }
                val clone = new DisplayedDropTable(definition.npcId, dropsList);
                displayedDrops.put(definition.npcId, clone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

    public void save() {
	    if (!Constants.WORLD_PROFILE.isDevelopment()) {
	        throw new IllegalStateException("Saving drops may only be done on development worlds as it reflects on the actual in-use drops.");
        }
        try(val writer = new BufferedWriter(new FileWriter(new File("data/npcs/drops.json")))) {
            writer.write(World.getGson().toJson(new ArrayList<>(drops.values())));
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes the drops, sending all of the "always" drops and picking one random drop.
     * @param drops the droptable.
     * @param consumer the consumer executed on the lucky drops.
     */
	public static void forEach(final DropTable drops, final Consumer<Drop> consumer) {
        val weight = drops.getWeight();
        val randomRate = Utils.random(Math.max(100_000, weight));
        val array = drops.getDrops();
        int currentWeight = 0;
        for (int i = array.length - 1; i >= 0; i--) {
            val drop = array[i];
            val rate = drop.getRate();
            if (rate == 100_000) {
                consumer.accept(drop);
                continue;
            }
            if ((currentWeight += rate) >= randomRate) {
                //Treat toolkits as if they're "nothing".
                if (drop.getItemId() == ItemId.TOOLKIT) {
                    return;
                }
                consumer.accept(drop);
                return;
            }
        }
    }
}