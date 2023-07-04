package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessorLoader;
import com.zenyte.game.world.entity.npc.drop.matrix.ItemDrop;
import com.zenyte.game.world.entity.npc.drop.matrix.NPCDrops;
import com.zenyte.game.world.entity.npc.drop.viewerentry.DropViewerEntry;
import com.zenyte.game.world.entity.npc.drop.viewerentry.ItemDropViewerEntry;
import com.zenyte.game.world.entity.npc.drop.viewerentry.NPCDropViewerEntry;
import com.zenyte.game.world.entity.npc.spawns.NPCSpawnLoader;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.var.VarCollection;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import mgi.types.Definitions;
import mgi.types.config.items.ItemDefinitions;
import mgi.types.config.npcs.NPCDefinitions;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingDouble;

/**
 * @author Tommeh | 16-4-2019 | 14:19
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Slf4j
public class DropViewerInterface extends Interface {

    @Override
    protected void attach() {
        put(11, "Select NPC");
        put(13, "Select Item");
        put(19, "View Result");
        put(31, "Rarity Display");
        put(10, "Search button");//TODO for tom: Make it so clicking anywhere in the search box opens this.
    }

    @Override
    public void open(Player player) {
        reset(player);
        player.getInterfaceHandler().sendInterface(this);
    }

    @Override
    public void close(final Player player, final Optional<GameInterface> replacement) {
        player.getPacketDispatcher().sendClientScript(2158);
    }

    @Override
    protected void build() {
        bind("Select NPC", player -> {
            player.addTemporaryAttribute("drop_viewer_search_type", 0);
            val attr = player.getTemporaryAttributes().get("drop_viewer_input");
            if (!(attr instanceof String)) {
                return;
            }
            val input = (String) attr;
            search(player, input);
        });
        bind("Select Item", player -> {
            player.addTemporaryAttribute("drop_viewer_search_type", 1);
            val attr = player.getTemporaryAttributes().get("drop_viewer_input");
            if (!(attr instanceof String)) {
                return;
            }
            val input = (String) attr;
            search(player, input);
        });
        bind("View Result", (player, slotId, itemId, option) -> {
            val attr = player.getTemporaryAttributes().get("drop_viewer_results");
            if (!(attr instanceof List)) {
                return;
            }
            val results = (List<Definitions>) attr;
            val result = results.get(slotId);
            if (result == null) {
                return;
            }
            val entries = getEntries(player, result);
            populateRows(player, true, result, entries);
            player.getPacketDispatcher().sendClientScript(2239);
        });
        bind("Rarity Display", player -> {
            player.toggleBooleanAttribute("drop_viewer_fractions");
            VarCollection.DROP_VIEWER_FRACTIONS.update(player);
            val rowsAttr = player.getTemporaryAttributes().get("drop_viewer_rows");
            if (!(rowsAttr instanceof List)) {
                return;
            }
            val rows = (List<DropViewerEntry>) rowsAttr;
            val resultAttr = player.getTemporaryAttributes().get("drop_viewer_search_result");
            if (!(resultAttr instanceof Definitions)) {
                return;
            }
            val searchResult = (Definitions) resultAttr;
            populateRows(player, false, searchResult, rows);
        });
    }

    private static final Int2IntMap transformedIds = new Int2IntOpenHashMap();
    static {
        //Re-point all the vorkath versions.
        transformedIds.put(8026, 8061);
        transformedIds.put(8058, 8061);
        transformedIds.put(8059, 8061);
        transformedIds.put(8060, 8061);
    }

    public static final void open(@NotNull final Player player, final int npcId) {
        val id = transformedIds.getOrDefault(npcId, npcId);
        val result = NPCDefinitions.get(id);
        val entries = getEntries(player, result);
        if (entries.isEmpty()) {
            player.sendMessage("No drops found for " + NPCDefinitions.getOrThrow(id).getName().toLowerCase() + ".");
            return;
        }
        GameInterface.DROP_VIEWER.open(player);
        populateRows(player, true, result, entries);
        player.getPacketDispatcher().sendClientScript(2239);
    }

    public static List<DropViewerEntry> getEntries(final Player player, final Definitions r) {
        val list = new LinkedList<DropViewerEntry>();
        if (r instanceof NPCDefinitions) {
            val result = (NPCDefinitions) r;
            val id = result.getId();
            val table = NPCDrops.getTable(id);
            val processors = DropProcessorLoader.get(id);
            val rows = new HashMap<Integer, List<ItemDropViewerEntry>>();
            if (table != null) {
                for (val drop : table.getDrops()) {
                    if (drop.getItemId() == ItemId.TOOLKIT) {
                        continue;
                    }
                    rows.computeIfAbsent(drop.getItemId(), l -> new LinkedList<>()).add(new ItemDropViewerEntry(drop.getItemId(), drop.getMinAmount(), drop.getMaxAmount(), drop.getRate() == 100_000 ?
                            100 : drop.getRate() / (float) table.getWeight() * 100, ""));
                }
            }
            if (processors != null) {
                for (val processor : processors) {
                    for (val drop : processor.getBasicDrops()) {
                        if (drop.getPredicate() != null && !drop.getPredicate().test(player, id)) {
                            continue;
                        }
                        var rate = drop.getRate(player, id);
                        rows.computeIfAbsent(drop.getId(), l -> new LinkedList<>()).add(new ItemDropViewerEntry(drop.getId(), drop.getMinAmount(), drop.getMaxAmount(), 1D / rate * 100, ""));
                    }
                    for (val entry : processor.getInfoMap().long2ObjectEntrySet()) {
                        try {
                            val packed = entry.getLongKey();
                            val item = (int) (packed);
                            val npc = (int) (packed >> 32);
                            if (id != npc) {
                                continue;
                            }
                            val tableDropList = rows.get(item);
                            val drop = entry.getValue();
                            if (tableDropList == null) {
                                continue;
                            }
                            rows.remove(item);
                            for (val tableDrop : tableDropList) {
                                rows.computeIfAbsent(item, l -> new LinkedList<>()).add(new ItemDropViewerEntry(tableDrop.getItem(), tableDrop.getMinAmount(), tableDrop.getMaxAmount(),
                                        tableDrop.getRate(), drop.getInformation()));
                            }
                        } catch (Exception e) {
                            log.error(Strings.EMPTY, e);
                        }
                    }
                }
            }
            rows.forEach((key, value) -> list.addAll(value));
        } else {
            val rows = new HashMap<Integer, Object2ObjectMap<String, NPCDropViewerEntry>>();
            val definitionsList = new LinkedList<ItemDefinitions>();
            definitionsList.add((ItemDefinitions) r);
            if (((ItemDefinitions) r).getNotedOrDefault() != ((ItemDefinitions) r).getId()) {
                definitionsList.add(ItemDefinitions.getOrThrow(((ItemDefinitions) r).getNotedOrDefault()));
            }
            for (val def : definitionsList) {
                val drops = NPCDrops.getTableForItem(def.getId());
                if (drops != null) {
                    for (val d : drops) {
                        val drop = d.getDrop();
                        if (!NPCSpawnLoader.dropViewerNPCs.contains(d.getNpcId()) || (drop.getPredicate() != null && !drop.getPredicate().test(player, d.getNpcId()))) {
                            continue;
                        }
                        rows.computeIfAbsent(drop.getItemId(), l -> new Object2ObjectOpenHashMap<>()).put(NPCDefinitions.getOrThrow(d.getNpcId()).getName(),
                                new NPCDropViewerEntry(drop.getItemId(), d.getNpcId(), drop.getMinAmount(), drop.getMaxAmount(), drop.getFunction().apply(player, d.getNpcId()), ""));
                    }
                }
            }
            rows.forEach((key, value) -> list.addAll(value.values()));
        }
        return list.stream().sorted(Collections.reverseOrder(comparingDouble(DropViewerEntry::getRate))).collect(Collectors.toList());
    }

    public static void populateRows(final Player player, final boolean completeRefresh, final Definitions searchResult, final List<DropViewerEntry> rows) {
        var offsetY = 0;
        var index = 0;
        player.addTemporaryAttribute("drop_viewer_rows", rows);
        player.addTemporaryAttribute("drop_viewer_search_result", searchResult);
        if (searchResult != null) {
            if (searchResult instanceof NPCDefinitions) {
                player.getPacketDispatcher().sendClientScript(10103, ((NPCDefinitions) searchResult).getName(), completeRefresh ? 1 : 0); //set layout title
            } else {
                player.getPacketDispatcher().sendClientScript(10103, ((ItemDefinitions) searchResult).getName(),completeRefresh ? 1 : 0); //set layout title
            }
        }
        for (val r : rows) {
            val formatted = Utils.format((int) Math.round(1 / r.getRate() * 100));
            String rarity;
            if (player.getBooleanAttribute("drop_viewer_fractions")) {
                rarity = "1 / " + (formatted.length() > 8 ? formatted.substring(0, 1) + " million" : formatted);
            } else {
                rarity = r.getRate() == 100 ? "Always" : String.format(r.getRate() < 0.001 ? "%.4f" : r.getRate() < 0.01 ? "%.3f" : "%.2f", r.getRate()) + "%";
            }
            if (r instanceof ItemDropViewerEntry) {
                val row = (ItemDropViewerEntry) r;
                player.getPacketDispatcher().sendClientScript(10114, index, offsetY, row.getItem(), row.getInfo(), row.getMinAmount(), row.getMaxAmount(), rarity); //append item row
            } else {
                val row = (NPCDropViewerEntry) r;
                val name = NPCDefinitions.get(row.getNpc()).getName();
                player.getPacketDispatcher().sendClientScript(10121, index, offsetY, row.getItemId(), name, row.getInfo(), row.getMinAmount(), row.getMaxAmount(), rarity); //append npc row
            }
            offsetY += r.isPredicated() ? 70 : 40;
            index++;
        }
        if (searchResult != null) {
            player.getPacketDispatcher().sendClientScript(10115, offsetY, completeRefresh ? 1 : 0); //rebuild scrolllayer/bar
        }
    }

    public static void search(final Player player, final String rawInput) {
        val input = rawInput.toLowerCase();
        player.addTemporaryAttribute("drop_viewer_input", input);
        val type = player.getNumericTemporaryAttribute("drop_viewer_search_type").intValue();
        if (type == 0) {
            val map = new Object2ObjectOpenHashMap<NPCDefinitions, Set<NPCDefinitions>>();
            val results = new LinkedList<NPCDefinitions>();
            loop:
            for (val def : NPCDefinitions.definitions) {
                if (def == null || !def.containsOption("Attack") || NPCSpawnLoader.ignoredMonsters.contains(def.getId()) || def.getCombatLevel() == 0 || NPCDrops.getTable(def.getId()) == null && DropProcessorLoader.get(def.getId()) == null || !NPCSpawnLoader.dropViewerNPCs.contains(def.getId())) {
                    continue;
                }
                if (def.getLowercaseName().contains(input)) {
                    for (val result : results) {
                        if (result.getName().equalsIgnoreCase(def.getName()) && result.getCombatLevel() == def.getCombatLevel()) {
                            if (NPCDrops.equalsIgnoreRates(result.getId(), def.getId())) {
                                map.computeIfAbsent(result, r -> new ObjectOpenHashSet<>(Collections.singleton(result))).add(def);
                                continue loop;
                            }
                        }
                    }
                    map.computeIfAbsent(def, r -> new ObjectOpenHashSet<>(Collections.singleton(def)));
                    results.add(def);
                }
            }

            results.sort((o1, o2) -> {
                String x1 = o1.getName();
                String x2 = o2.getName();
                int sComp = x1.compareToIgnoreCase(x2);

                if (sComp != 0) {
                    return sComp;
                }
                return Integer.compare(o1.getCombatLevel(), o2.getCombatLevel());
            });
            var offsetY = 0;
            player.getPacketDispatcher().sendClientScript(10104, type); //set searchtype (item/npc)
            for (int index = 0; index < results.size(); index++) {
                val def = results.get(index);
                val name = def.getName();
                val cb = Utils.getPreciseLevelColour(player.getCombatLevel(), def.getCombatLevel()) + " (lvl-" + def.getCombatLevel() + ")";
                val allAreas = new ArrayList<String>();

                map.get(def).forEach(definition -> {
                    val areas = NPCSpawnLoader.getFoundLocations(definition.getId());
                    if (areas == null) {
                        return;
                    }
                    areas.forEach(string -> {
                        if (!allAreas.contains(string)) {
                            allAreas.add(string);
                        }
                    });
                });
                //Sorting alphabetically w/ the exception of the 'Undefined' string which will always appear in the bottom.
                allAreas.sort((s1, s2) -> {
                    if (s1.equals("Undefined area")) {
                        return 1;
                    } else if (s2.equals("Undefined area")) {
                        return -1;
                    }
                    return s1.compareTo(s2);
                });

                val builder = new StringBuilder();
                for (val area : allAreas) {
                    builder.append("- ").append(area).append("<br>");
                }

                val width = Utils.getTextWidth(494, name + cb);
                val height = width > 114 ? 22 : 13;
                player.getPacketDispatcher().sendClientScript(10105, index, offsetY, height, width, name, cb, builder.toString()); //append npc search result
                offsetY += height;
            }
            player.getPacketDispatcher().sendClientScript(2239); //disable keyboard input on chatbox
            player.getPacketDispatcher().sendComponentSettings(GameInterface.DROP_VIEWER.getId(), GameInterface.DROP_VIEWER.getPlugin().get().getComponent("View Result"), 0, results.size(), AccessMask.CLICK_OP1);
            player.getPacketDispatcher().sendClientScript(10108, offsetY); //rebuild scrollllayer/bar
            player.addTemporaryAttribute("drop_viewer_results", results);
            if (results.size() == 0) {
                player.getPacketDispatcher().sendClientScript(10109, "No results were found with your search."); //set response msg
            } else {
                val result = results.get(0);
                if (result == null) {
                    return;
                }
                val entries = getEntries(player, result);
                populateRows(player, true, results.get(0), entries); //populate rows of first result
                player.getPacketDispatcher().sendClientScript(10107, 0); //highlight first result
            }
        } else {
            val results = new LinkedList<ItemDefinitions>();
            for (val def : searchableItemDefinitions) {
                if (def.getLowercaseName().contains(input)) {
                    results.add(def);
                }
            }
            var offsetY = 0;
            player.getPacketDispatcher().sendClientScript(10104, type); //set search type (item/npc)
            results.removeIf(def -> {
                val entries = getEntries(player, def);
                return entries == null || entries.isEmpty();
            });
            results.removeIf(result -> {
               if (result.isNoted()) {
                   val unnotedId = result.getUnnotedOrDefault();
                   return results.contains(ItemDefinitions.getOrThrow(unnotedId));
               }
                return false;
            });
            results.sort(Comparator.comparing(ItemDefinitions::getName));
            for (int index = 0; index < results.size(); index++) {
                val def = results.get(index);
                val name = def.getName();
                val width = Utils.getTextWidth(494, name);
                val height = width > 114 ? 22 : 13;
                player.getPacketDispatcher().sendClientScript(10120, index, offsetY, height, width, name); //append item search result
                offsetY += height;
            }
            player.getPacketDispatcher().sendClientScript(2239); //disable keyboard input on chatbox
            player.getPacketDispatcher().sendComponentSettings(GameInterface.DROP_VIEWER.getId(), GameInterface.DROP_VIEWER.getPlugin().get().getComponent("View Result"), 0, results.size(), AccessMask.CLICK_OP1);
            player.getPacketDispatcher().sendClientScript(10108, offsetY); //rebuild scrolllayer/bar
            player.addTemporaryAttribute("drop_viewer_results", results);
            if (results.size() == 0) {
                player.getPacketDispatcher().sendClientScript(10109, "No results were found with your search."); //set response msg
            } else {
                val result = results.get(0);
                if (result == null) {
                    return;
                }
                val entries = getEntries(player, result);
                populateRows(player, true, results.get(0), entries); //populate rows of first result
                player.getPacketDispatcher().sendClientScript(10107, 0); //highlight first result
            }
        }
    }

    private static final List<ItemDefinitions> searchableItemDefinitions = new LinkedList<>();

    public static final void populateDropViewerData() {
        for (val dropProcessorEntry : DropProcessorLoader.getProcessors().int2ObjectEntrySet()) {
            val npcId = dropProcessorEntry.getIntKey();
            val processorsList = dropProcessorEntry.getValue();
            for (val processor : processorsList) {
                for (val displayedDrop : processor.getBasicDrops()) {
                    val table = NPCDrops.displayedDrops.computeIfAbsent(npcId, __ -> new NPCDrops.DisplayedDropTable(npcId, new ObjectArrayList<>()));
                    val drop = new NPCDrops.DisplayedNPCDrop(displayedDrop.getId(), displayedDrop.getMinAmount(), displayedDrop.getMaxAmount(), (player, id) -> 1D / displayedDrop.getRate(player, id) * 100D);
                    table.getDrops().add(drop);
                    if (displayedDrop.getPredicate() != null) {
                        drop.setPredicate(displayedDrop.getPredicate());
                    }
                }
            }
        }

        for (val table : NPCDrops.displayedDrops.int2ObjectEntrySet()) {
            for (val drop : table.getValue().getDrops()) {
                NPCDrops.dropsByItem.computeIfAbsent(drop.getItemId(), __ -> new ObjectArrayList<>()).add(new ItemDrop(table.getIntKey(), drop));
            }
        }
        for (val def : ItemDefinitions.definitions) {
            if (def == null || !NPCDrops.dropsByItem.containsKey(def.getId())) {
                continue;
            }

            searchableItemDefinitions.add(def);
        }
    }

    private void reset(final Player player) {
        player.getTemporaryAttributes().remove("drop_viewer_results");
        player.getTemporaryAttributes().remove("drop_viewer_search_type");
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.DROP_VIEWER;
    }
}
