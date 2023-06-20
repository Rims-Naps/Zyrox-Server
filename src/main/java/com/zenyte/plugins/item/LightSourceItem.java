package com.zenyte.plugins.item;

import com.zenyte.game.content.achievementdiary.diaries.FaladorDiary;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.region.area.darkcaves.DarkArea;
import com.zenyte.game.world.region.area.darkcaves.FaladorMoleLairArea;
import com.zenyte.game.world.region.area.darkcaves.LumbridgeSwampCavesArea;
import com.zenyte.game.world.region.area.darkcaves.MosLeHarmlessCavernArea;
import mgi.types.config.items.ItemDefinitions;
import com.zenyte.plugins.object.OldFirePit;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Kris | 27. aug 2018 : 00:57:12
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class LightSourceItem extends ItemPlugin implements ItemOnItemAction {

    public static final int NONE = 0x1;
    public static final int PROTECTED = 0x2;
    public static final int FLAMMABLE = 0x4;

    @RequiredArgsConstructor
    public enum LightSource {

        BLACK_CANDLE(38, 32, 1, 1, NONE),
        BRUMA_TORCH(-1, 20720, 1, 2, FLAMMABLE),
        KANDARIN_HEADGEAR_1(-1, 13137, 1, 2, PROTECTED),
        KANDARIN_HEADGEAR_2(-1, 13138, 1, 2, PROTECTED),
        KANDARIN_HEADGEAR_3(-1, 13139, 1, 2, PROTECTED),
        KANDARIN_HEADGEAR_4(-1, 13140, 1, 2, PROTECTED),
        FIREMAKING_CAPE(-1, 9804, 1, 3, PROTECTED),
        BULLSEYE_LANTERN(4548, 4550, 49, 3, PROTECTED),
        CANDLE(36, 33, 1, 1, NONE),
        WHITE_CANDLE_LANTERN(4529, 4531, 4, 1, PROTECTED | FLAMMABLE),
        BLACK_CANDLE_LANTERN(4532, 4534, 4, 1, PROTECTED | FLAMMABLE),
        EMERALD_LANTERN(9064, 9065, 49, 3, PROTECTED),
        OIL_LAMP(4522, 4524, 12, 2, PROTECTED | FLAMMABLE),
        TORCH(596, 594, 1, 1, FLAMMABLE),
        MINING_HELMET(5014, 5013, 1, 2, PROTECTED),
        OIL_LANTERN(4537, 4539, 26, 2, PROTECTED),
        SAPPHIRE_LANTERN(4701, 4702, 49, 2, PROTECTED),
        //BUG_LANTERN(7051, 7053, 33, 2, PROTECTED)
        ;

        static final SoundEffect LIGHT_SOUND = new SoundEffect(3226);
    
        @Getter
        private final int unlitId, litId, level, brightness, mask;


        public static final Int2ObjectOpenHashMap<LightSource> MAPPED_SOURCES =
                new Int2ObjectOpenHashMap<>();

        private static final Int2ObjectOpenHashMap<LightSource> LIT_MAPPED_SOURCES =
                new Int2ObjectOpenHashMap<>();

        static {
            for (val value : values()) {
                MAPPED_SOURCES.put(value.litId, value);
                LIT_MAPPED_SOURCES.put(value.litId, value);
                if (value.unlitId != -1) {
                    MAPPED_SOURCES.put(value.unlitId, value);
                }
            }
            for (val id : SkillcapePerk.FIREMAKING.getCapes()) {
                MAPPED_SOURCES.put(id, FIREMAKING_CAPE);
                LIT_MAPPED_SOURCES.put(id, FIREMAKING_CAPE);
            }
        }

        public static final LightSource getSource(final Item item) {
            if (item == null) {
                return null;
            }
            return MAPPED_SOURCES.get(item.getId());
        }

        public static final LightSource[] getLitLightSources(final Player player, final int flag) {
            val list = new ArrayList<LightSource>();
            val inventory = player.getInventory();
            val equipment = player.getEquipment();
            for (val entry : LIT_MAPPED_SOURCES.int2ObjectEntrySet()) {
                val source = entry.getValue();
                val litId = entry.getIntKey();
                if (flag != -1 && (source.mask & flag) == 0) {
                    continue;
                }
                if (inventory.containsItem(litId, 1) || equipment.containsItem(litId, 1)) {
                    list.add(source);
                }
            }
            return list.toArray(new LightSource[0]);
        }

        public static final int getLitSourcesCount(final Player player) {
            int count = 0;
            val inventory = player.getInventory();
            val equipment = player.getEquipment();
            for (val entry : LIT_MAPPED_SOURCES.int2ObjectEntrySet()) {
                val litId = entry.getIntKey();
                count += inventory.getAmountOf(litId);
                count += equipment.getAmountOf(litId);
            }
            return count;
        }

        public static final int getBrightness(final Player player) {
            if ((player.inArea(FaladorMoleLairArea.class) && OldFirePit.FirePit.GIANT_MOLE_FIRE.isBuilt(player))
                    || (player.inArea(LumbridgeSwampCavesArea.class) && OldFirePit.FirePit.LUMBRIDGE_SWAMP_CAVES_FIRE.isBuilt(player))
                    || (player.inArea(MosLeHarmlessCavernArea.class) && OldFirePit.FirePit.MOS_LE_HARMLESS_FIRE.isBuilt(player))) {
                return 2;
            }
            int count = 0;
            val inventory = player.getInventory();
            val equipment = player.getEquipment();
            for (val entry : LIT_MAPPED_SOURCES.int2ObjectEntrySet()) {
                val source = entry.getValue();
                val litId = entry.getIntKey();
                val brightness = source.brightness;
                count += inventory.getAmountOf(litId) * brightness;
                count += equipment.getAmountOf(litId) * brightness;
            }
            return count;
        }

        public static final void extinguish(final Player player, int amount, final LightSource... sources) {
            val inventory = player.getInventory().getContainer();
            val equipment = player.getEquipment().getContainer();
            loop:
            for (val source : sources) {
                if (source.unlitId == -1) {
                    continue;
                }
                for (int i = 0; i < 28; i++) {
                    val item = inventory.get(i);
                    if (item == null) {
                        continue;
                    }
                    if (item.getId() == source.litId) {
                        item.setId(source.unlitId);
                        inventory.refresh(i);
                        if (--amount <= 0) {
                            break loop;
                        }
                    }
                }
                for (int i = 0; i < 14; i++) {
                    val item = equipment.get(i);
                    if (item == null) {
                        continue;
                    }
                    if (item.getId() == source.litId) {
                        item.setId(source.unlitId);
                        equipment.refresh(i);
                        if (--amount <= 0) {
                            break loop;
                        }
                    }
                }
            }
            inventory.refresh(player);
            equipment.refresh(player);
        }
    }

    @Override
    public void handle() {
        bind("Extinguish", (player, item, slotId) -> {
            val source = LightSource.MAPPED_SOURCES.get(item.getId());
            if (source == null) {
                player.sendMessage("Nothing interesting happens...");
                return;
            }
            val sources = LightSource.getLitSourcesCount(player);
            if (sources == 1) {
                if (player.getArea() instanceof DarkArea) {
                    player.sendMessage("Extinguishing the " + item.getName().toLowerCase() + " would leave you without a light source.");
                    return;
                }
            }
            player.sendMessage("You extinguish the " + item.getName().toLowerCase() + ".");
            player.getInventory().set(slotId, new Item(source.unlitId));
        });
    }

    @Override
    public int[] getItems() {
        val ids = LightSource.MAPPED_SOURCES.keySet().toIntArray();
        val list = new IntArrayList();
        for (val id : ids) {
            val defs = ItemDefinitions.get(id);
            if (defs != null) {
                if (defs.containsOption("Extinguish")) {
                    list.add(id);
                }
            }
        }
        return list.toIntArray();
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        val list = new ArrayList<ItemPair>();
        val tinderbox = 590;
        for (val entry : LightSource.MAPPED_SOURCES.int2ObjectEntrySet()) {
            val source = entry.getValue();
            if (source.unlitId == -1) {
                continue;
            }
            list.add(ItemPair.of(source.unlitId, tinderbox));
        }
        return list.toArray(new ItemPair[0]);
    }

    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
        val tinderbox = from.getId() == 590 ? from : to;
        val lightSource = tinderbox == from ? to : from;
        val source = LightSource.MAPPED_SOURCES.get(lightSource.getId());
        if (source == null) {
            return;
        }
        if (player.getSkills().getLevel(Skills.FIREMAKING) < source.level) {
            player.sendMessage("You need a level of at least " + source.level + " Firemaking to light this.");
            return;
        }
        if (source.equals(LightSource.BULLSEYE_LANTERN)) {
            player.getAchievementDiaries().update(FaladorDiary.LIGHT_BULLSEYE_LANTERN);
        }
        player.getInventory().set(lightSource == from ? fromSlot : toSlot, new Item(source.litId));
        player.getPacketDispatcher().sendSoundEffect(LightSource.LIGHT_SOUND);
        player.sendMessage("You light the " + lightSource.getName().toLowerCase() + ".");
    }

}
