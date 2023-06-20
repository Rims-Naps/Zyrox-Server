package mgi.tools.parser;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.io.Files;
import com.moandjiezana.toml.Toml;
import com.zenyte.Constants;
import com.zenyte.Game;
import com.zenyte.Scanner;
import com.zenyte.game.content.achievementdiary.AchievementDiaries;
import com.zenyte.game.content.achievementdiary.DiaryArea;
import com.zenyte.game.content.treasuretrails.stash.StashUnit;
import com.zenyte.game.ui.testinterfaces.BountyHunterStoreInterface;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.MapUtils;
import com.zenyte.game.world.region.XTEALoader;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import mgi.custom.*;
import mgi.custom.christmas.ChristmasMapPacker;
import mgi.custom.christmas.ChristmasObject;
import mgi.custom.easter.EasterMapPacker;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.cache.Cache;
import mgi.tools.jagcached.cache.Group;
import mgi.types.Definitions;
import mgi.types.config.enums.EnumDefinitions;
import mgi.types.config.npcs.NPCDefinitions;
import mgi.types.worldmap.WorldMapDefinitions;
import mgi.utilities.ByteBuffer;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tommeh | 16/01/2020 | 01:06
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Slf4j
public class TypeParser {

    private static final Int2ObjectMap<List<String>> optionsMap = new Int2ObjectOpenHashMap<>(10 * 1024);
    @Getter private static final List<Definitions> definitions = new ArrayList<>();
    public static final Kryo KRYO = new Kryo();

    public static void main(final String[] args) throws IOException {
        String type = "";
        if (args.length > 0) {
            type = args[0];
        }

        val startTime = System.nanoTime();

        if (type.equals("--unzip")) {
            FileUtils.cleanDirectory(new File("data/cache"));
            FileUtils.cleanDirectory(new File("data/cache-original"));
            val zipFile = new ZipFile("data/cache-original.zip");
            zipFile.extractAll("data/cache");
            zipFile.extractAll("data/cache-original");
        }

        Game.load();
        XTEALoader.load();
        Definitions.loadDefinitions(Definitions.LOW_PRIORITY_DEFINITIONS);

        initializeKryo();

        parse(new File("assets/types"));

        pack(NPCDefinitions.class);

        repackNPCOptions();

        packDynamicConfigs();
        packHighRevision();
        pack(ArrayUtils.addAll(Definitions.HIGH_PRIORITY_DEFINITIONS, Definitions.LOW_PRIORITY_DEFINITIONS));

        packClientBackground();

        packModels();
        packClientScripts();
        packInterfaces();
        packMaps();

        Game.getCacheMgi().close();
        //log.info("Cache repack took " + Utils.nanoToMilli(System.nanoTime() - startTime) + " milliseconds!");
    }

    private static void initializeKryo() {
        for (val d : Definitions.LOW_PRIORITY_DEFINITIONS) {
            KRYO.register(d);
        }
        for (val d : Definitions.HIGH_PRIORITY_DEFINITIONS) {
            KRYO.register(d);
        }
        KRYO.register(int[].class);
        KRYO.register(short[].class);
        KRYO.register(String[].class);
        KRYO.register(Int2ObjectOpenHashMap.class);
    }

    private static void repackNPCOptions() {
        new NPCDefinitions().load();
        new Scanner().scan(NPCPlugin.class);
        Game.setCacheMgi(Cache.openCache("./data/cache-original/"));

        new NPCDefinitions().load();
        for (val npc : NPCDefinitions.definitions) {
            if (npc == null) {
                continue;
            }
            val list = optionsMap.computeIfAbsent(npc.getId(), n -> new ArrayList<>());
            val options = npc.getOptions();
            for (val option : options) {
                if (option == null) {
                    list.add(null);
                    continue;
                }
                val plugin = NPCPlugin.getHandler(npc.getId(), option);
                list.add(plugin == null ? null : option);
            }
        }
        Game.setCacheMgi(Cache.openCache("./data/cache/"));

        new NPCDefinitions().load();

        for (val npc : NPCDefinitions.definitions) {
            if (npc == null) {
                continue;
            }
            val options = optionsMap.get(npc.getId());
            if (options == null) {
                continue;
            }
            assert options.size() == 5;
            npc.setOptions(options.toArray(new String[0]));
            npc.pack();
        }
        log.info("Finished repacking npc options.");
    }

    private static void parse(final File folder) {
        File f = null;
        try {
            for (val file : folder.listFiles()) {
                f = file;
                if (file.getPath().endsWith("exclude")) {
                    continue;
                }
                if (file.isDirectory()) {
                    parse(file);
                } else {
                    if (!Files.getFileExtension(file.getName()).equals("toml")) {
                        continue;
                    }

                    val toml = new Toml().read(file);
                    if (file.getPath().startsWith(Paths.get("assets", "types", "component").toString())) {
                        val reader = TypeReader.readers.get("component");
                        definitions.addAll(reader.read(toml));
                    } else {
                        for (val entry : toml.entrySet()) {
                            val reader = TypeReader.readers.get(entry.getKey());
                            if (reader == null) {
                                System.err.println(TypeReader.readers);
                                throw new RuntimeException("Could not find a reader for: " + entry.getKey());
                            }
                            val value = entry.getValue();
                            val types = new ArrayList<Toml>();
                            if (value instanceof Toml) {
                                types.add((Toml) value);
                            } else {
                                types.addAll((ArrayList<Toml>) value);
                            }
                            for (val type : types) {
                                val properties = type.toMap();
                                definitions.addAll(reader.read(properties));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Something went wrong in " + f.getPath());
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static void pack(final Class<?>... types) {
        val filtered = definitions.stream().filter(d -> ArrayUtils.contains(types, d.getClass()))
                .collect(Collectors.toCollection(ArrayList::new));
        filtered.forEach(Definitions::pack);

        if (!filtered.isEmpty()) {
            log.info("Finished packing " + filtered.size() + " type" + (filtered.size() == 1 ? "" : "s."));
        }
    }

    private static void packClientBackground() throws IOException {
        val desktop = java.nio.file.Files.readAllBytes(Paths.get("assets/sprites/background/background_desktop.png"));
        val mobile = java.nio.file.Files.readAllBytes(Paths.get("assets/sprites/background/background_mobile.png"));
        val logo = java.nio.file.Files.readAllBytes(Paths.get("assets/sprites/background/background_logo.png"));
        val cache = Game.getCacheMgi();

        val desktopArchive = cache.getArchive(ArchiveType.BINARY);
        desktopArchive.findGroupByID(0).findFileByID(0).setData(new ByteBuffer(desktop));

        val mobileArhive = desktopArchive.findGroupByID(2);
        mobileArhive.findFileByID(0).setData(new ByteBuffer(mobile));
        mobileArhive.addFile(new mgi.tools.jagcached.cache.File(new ByteBuffer(logo)));
    }

    private static void packHighRevision() throws IOException {
        //new DiceBagPacker().pack();
        //new TrickPacker().pack();
        new ThanksgivingPacker().pack();
        new ChristmasMapPacker().pack();
        new HighDefinitionPets().packFull();
        new EasterMapPacker().packAll();
        new CustomTeleport().packAll();
        new TrickEmote().packAll();
        new DiceBag().packAll();
        new MusicEnumPacker().pack();
        FramePacker.write();
        AnimationBase.pack();
        //new HalloweenMapPacker().pack();
    }

    private static void packDynamicConfigs() {

        EnumDefinitions enumDef;

        enumDef = new EnumDefinitions();
        enumDef.setId(1974);
        enumDef.setKeyType("int");
        enumDef.setValueType("namedobj");
        enumDef.setDefaultInt(-1);
        enumDef.setValues(new HashMap<>());

        int id = 0;
        for (val reward : BountyHunterStoreInterface.Reward.values()) {
            enumDef.getValues().put(id++, reward.getId());
        }
        definitions.add(enumDef);

        val diaries = AchievementDiaries.ALL_DIARIES;
        for (val diaryEnum : diaries) {
            val values = new HashMap<Integer, Object>();
            DiaryArea area = null;
            for (val diary : diaryEnum) {
                if (diary.autoCompleted()) {
                    continue;
                }
                val complexity = diary.type();
                area = diary.area();
                values.put(complexity.ordinal(), (int) (values.get(complexity.ordinal()) == null ? 0 : values.get(complexity.ordinal())) + 1);
            }
            enumDef = new EnumDefinitions();
            enumDef.setId(2501 + area.getIndex());
            enumDef.setKeyType("int");
            enumDef.setValueType("int");
            enumDef.setDefaultInt(-1);
            enumDef.setValues(values);

            definitions.add(enumDef);
        }

        for (var enumId : new int[]{812, 817}) {
            enumDef = EnumDefinitions.get(enumId);
            enumDef.getValues().put(enumDef.getLargestIntKey() + 1, "Silent Knight");
            enumDef.getValues().put(enumDef.getLargestIntKey() + 2, "Smorgasbord");
            definitions.add(enumDef);
        }

        enumDef = EnumDefinitions.get(818);
        enumDef.getValues().put(enumDef.getLargestIntKey() + 1, enumDef.getLargestIntKey() + 2);
        enumDef.getValues().put(enumDef.getLargestIntKey() + 2, enumDef.getLargestIntKey() + 3);
        definitions.add(enumDef);


        enumDef = EnumDefinitions.get(819);
        enumDef.getValues().put(enumDef.getLargestIntKey() + 1, enumDef.getValues().get(1));
        enumDef.getValues().put(enumDef.getLargestIntKey() + 2, enumDef.getValues().get(1));
        definitions.add(enumDef);
    }

    private static void packModels() {
        try {
            packModel(38000, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte_portal_model.dat")));
            packModel(38001, java.nio.file.Files.readAllBytes(Paths.get("assets/models/tournament_supplies.dat")));
            packModel(38002, java.nio.file.Files.readAllBytes(Paths.get("assets/models/pets/cute_creature.dat")));
            packModel(38003, java.nio.file.Files.readAllBytes(Paths.get("assets/models/pets/stray_dog.dat")));
            packModel(38004, java.nio.file.Files.readAllBytes(Paths.get("assets/models/pets/evil_creature.dat")));
            packModel(38005, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bonds/cyan_bond.dat")));
            packModel(38006, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bonds/red_bond.dat")));
            packModel(50000, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte_teletab_50000.dat")));
            packModel(50001, java.nio.file.Files.readAllBytes(Paths.get("assets/models/healing fountain.dat")));
            packModel(52505, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Boots(drop)b.dat")));
            packModel(52506, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Bootsb.dat")));
            packModel(52507, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Cape(drop)b.dat")));
            packModel(52508, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Capeb.dat")));
            packModel(52509, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Gloves(drop)b.dat")));
            packModel(52510, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Glovesb.dat")));
            packModel(52511, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Helmet(drop)b.dat")));
            packModel(52512, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Helmetb.dat")));
            packModel(52513, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Platebody(drop)b.dat")));
            packModel(52514, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Platebodyb.dat")));
            packModel(52515, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Platelegs(drop)b.dat")));
            packModel(52516, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Platelegsb.dat")));

            packModel(52517, java.nio.file.Files.readAllBytes(Paths.get("assets/models/starter/Starter bow ground.dat")));
            packModel(52518, java.nio.file.Files.readAllBytes(Paths.get("assets/models/starter/Starter bow.dat")));

            packModel(52519, java.nio.file.Files.readAllBytes(Paths.get("assets/models/starter/Starter staff ground.dat")));
            packModel(52520, java.nio.file.Files.readAllBytes(Paths.get("assets/models/starter/Starter staff.dat")));

            packModel(52521, java.nio.file.Files.readAllBytes(Paths.get("assets/models/starter/Starter sword ground.dat")));
            packModel(52522, java.nio.file.Files.readAllBytes(Paths.get("assets/models/starter/Starter sword.dat")));
            packModel(52523, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Rare drop table.dat")));

            //Jonas
            packModel(52524, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/jonas/34041.dat")));
            packModel(52525, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/jonas/34044.dat")));
            packModel(52526, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/jonas/34046.dat")));
            packModel(52527, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/jonas/34047.dat")));
            //Grim reaper
            packModel(52528, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/grim reaper/28985.dat")));
            packModel(52529, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/grim reaper/34166.dat")));
            packModel(52530, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/grim reaper/34167.dat"))); //TODO keep note for matt
            //Thanksgiving
            packModel(52531, java.nio.file.Files.readAllBytes(Paths.get("assets/models/thanksgiving/thanksgiving turkey model.dat")));
            packModel(52532, java.nio.file.Files.readAllBytes(Paths.get("assets/models/thanksgiving/thanksgiving poof model.dat")));
            //Christmas scythe
            packModel(52533, java.nio.file.Files.readAllBytes(Paths.get("assets/models/christmas scythe inv.dat")));
            packModel(52534, java.nio.file.Files.readAllBytes(Paths.get("assets/models/christmas scythe wield.dat")));
            packModel(2450, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Treasure trails reward casket.dat")));
            if (Constants.CHRISTMAS) {
                Iterator<File> it = FileUtils.iterateFiles(new File("assets/christmas/christmas-y entities models/"), null, false);
                val sortedMap = new Int2ObjectAVLTreeMap<File>();
                while (it.hasNext()) {
                    val file = it.next();
                    val originalId = Integer.parseInt(file.getName().replace(".dat", ""));
                    sortedMap.put(originalId, file);
                }
                for (val entry : sortedMap.int2ObjectEntrySet()) {
                    val file = entry.getValue();
                    val bytes = IOUtils.toByteArray(new FileInputStream(file));
                    packModel(Integer.parseInt(file.getName().replace(".dat", "")), bytes);
                }
            }
            //Scroll boxes
            packModel(53000, java.nio.file.Files.readAllBytes(Paths.get("assets/models/scroll boxes/39028.dat")));
            packModel(53001, java.nio.file.Files.readAllBytes(Paths.get("assets/models/scroll boxes/39029.dat")));
            packModel(53002, java.nio.file.Files.readAllBytes(Paths.get("assets/models/scroll boxes/39030.dat")));
            packModel(53003, java.nio.file.Files.readAllBytes(Paths.get("assets/models/scroll boxes/39031.dat")));
            packModel(53004, java.nio.file.Files.readAllBytes(Paths.get("assets/models/scroll boxes/39032.dat")));
            packModel(53005, java.nio.file.Files.readAllBytes(Paths.get("assets/models/scroll boxes/39033.dat")));

            packModel(57577, java.nio.file.Files.readAllBytes(Paths.get("assets/models/clue progresser/1.dat")));
            packModel(57578, java.nio.file.Files.readAllBytes(Paths.get("assets/models/clue progresser/2.dat")));
            packModel(57579, java.nio.file.Files.readAllBytes(Paths.get("assets/models/clue progresser/3.dat")));
            packModel(57580, java.nio.file.Files.readAllBytes(Paths.get("assets/models/clue progresser/4.dat")));
            packModel(57581, java.nio.file.Files.readAllBytes(Paths.get("assets/models/clue progresser/5.dat")));


            //Twisted Ancestral
            packModel(50216, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twisted ancestral/TwistedAncestralRobeBottomMale.dat")));
            packModel(50217, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twisted ancestral/TwistedAncestralHatMale.dat")));
            packModel(50218, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twisted ancestral/TwistedAncestralRobeTopMale0.dat")));
            packModel(50219, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twisted ancestral/TwistedAncestralRobeTopMale1.dat")));
            packModel(50220, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twisted ancestral/TwistedAncestralRobeBottomFemale.dat")));
            packModel(50221, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twisted ancestral/TwistedAncestralHatFemale.dat")));
            packModel(50222, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twisted ancestral/TwistedAncestralRobeTopFemale0.dat")));
            packModel(50223, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twisted ancestral/TwistedAncestralRobeTopFemale1.dat")));
            packModel(50224, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twisted ancestral/TwistedAncestralRobeBottomGround.dat")));
            packModel(50225, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twisted ancestral/TwistedAncestralHatGround.dat")));
            packModel(50226, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twisted ancestral/TwistedAncestralRobeTopGround.dat")));
            packModel(50227, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twisted ancestral/TwistedAncestralKitGround.dat")));

            //Dagon
            packModel(50228, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Dagon/hat/23911.dat")));
            packModel(50229, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Dagon/hat/38911.dat")));
            packModel(50230, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Dagon/hat/38914.dat")));
            packModel(50231, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Dagon/hat/38918.dat")));

            //Dagon Top
            packModel(50232, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Dagon/top/38912.dat")));
            packModel(50233, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Dagon/top/38915.dat")));
            packModel(50234, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Dagon/top/38917.dat")));

            //Dagon Bottom
            packModel(50235, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Dagon/bottom/38910.dat")));
            packModel(50236, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Dagon/bottom/38913.dat")));
            packModel(50237, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Dagon/bottom/38916.dat")));

            /*
            //Harmonised Nightmare Staff
            packModel(50238, java.nio.file.Files.readAllBytes(Paths.get("assets/models/staffs/nightmarestaff/39058.dat")));
            packModel(50239, java.nio.file.Files.readAllBytes(Paths.get("assets/models/staffs/nightmarestaff/39062.dat")));
            packModel(50240, java.nio.file.Files.readAllBytes(Paths.get("assets/models/staffs/nightmarestaff/39070.dat")));
            */

            //Larran's Chest
            packModel(50241, java.nio.file.Files.readAllBytes(Paths.get("assets/models/larran/6615.dat")));
            packModel(50242, java.nio.file.Files.readAllBytes(Paths.get("assets/models/larran/6615.dat")));
            packModel(50249, java.nio.file.Files.readAllBytes(Paths.get("assets/models/larran/32070.dat")));
            packModel(50250, java.nio.file.Files.readAllBytes(Paths.get("assets/models/larran/37277.dat")));
            packModel(50251, java.nio.file.Files.readAllBytes(Paths.get("assets/models/larran/37278.dat")));
            packModel(50252, java.nio.file.Files.readAllBytes(Paths.get("assets/models/larran/37276.dat")));
            packModel(50253, java.nio.file.Files.readAllBytes(Paths.get("assets/models/larran/37279.dat")));

            //Faceguard
            packModel(50243, java.nio.file.Files.readAllBytes(Paths.get("assets/models/faceguard/38857.dat")));
            packModel(50244, java.nio.file.Files.readAllBytes(Paths.get("assets/models/faceguard/38858.dat")));
            packModel(50245, java.nio.file.Files.readAllBytes(Paths.get("assets/models/faceguard/38873.dat")));
            packModel(50246, java.nio.file.Files.readAllBytes(Paths.get("assets/models/faceguard/38897.dat")));

            //Jaw
            packModel(50247, java.nio.file.Files.readAllBytes(Paths.get("assets/models/faceguard/jaw/2429.dat")));
            packModel(50248, java.nio.file.Files.readAllBytes(Paths.get("assets/models/faceguard/jaw/38900.dat")));

            //Crystal Tree
            packModel(50254, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystaltrees/37357.dat")));

            //Crystal Shards
            packModel(50255, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalshards/37961.dat")));
            packModel(50256, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalshards/37957.dat")));
            packModel(50257, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalshards/37972.dat")));
            packModel(50258, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalshards/37974.dat")));
            packModel(50259, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalshards/37979.dat")));
            packModel(50260, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalshards/37956.dat")));
            packModel(50261, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalshards/37956.dat")));

            //Crystal Dust
            packModel(50262, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystaldust/2742.dat")));

            //Divine Super Combat #1
            packModel(50263, java.nio.file.Files.readAllBytes(Paths.get("assets/models/divinepotions/combat4.dat")));

            //Crystal Armors
            packModel(50264, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalarmor/38027.dat")));
            packModel(50265, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalarmor/38079.dat")));
            packModel(50266, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalarmor/38101.dat")));
            packModel(50267, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalarmor/38164.dat")));
            packModel(50268, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalarmor/38203.dat")));
            packModel(50269, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalarmor/38237.dat")));
            packModel(50270, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalarmor/38633.dat")));
            packModel(50271, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalarmor/38636.dat")));
            packModel(50272, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalarmor/38761.dat")));
            packModel(50273, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalarmor/38765.dat")));
            packModel(50274, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalarmor/38766.dat")));
            packModel(50275, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalarmor/38029.dat")));

            //Crystal Imp
            packModel(50276, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalimp/26367.dat")));
            packModel(50277, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalimp/37991.dat")));
            packModel(50292, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalimp/37992.dat")));

            //Elven Chest
            packModel(50278, java.nio.file.Files.readAllBytes(Paths.get("assets/models/elven/37905.dat")));
            packModel(50279, java.nio.file.Files.readAllBytes(Paths.get("assets/models/elven/37910.dat")));
            packModel(50291, java.nio.file.Files.readAllBytes(Paths.get("assets/models/elven/37976.dat")));

            //Crystal Tools
            packModel(50280, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystaltools/37790.dat")));
            packModel(50281, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystaltools/37950.dat")));
            packModel(50282, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystaltools/38271.dat")));
            packModel(50283, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystaltools/38275.dat")));
            packModel(50284, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystaltools/38279.dat")));
            packModel(50285, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystaltools/38281.dat")));
            packModel(50286, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystaltools/38283.dat")));
            packModel(50287, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystaltools/38285.dat")));
            packModel(50288, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystaltools/38745.dat")));
            packModel(50289, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystaltools/38746.dat")));
            packModel(50290, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystaltools/38748.dat")));


            //Bow of Fer
            packModel(50293, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalweps/42602.dat")));
            packModel(50294, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalweps/42605.dat")));

            //Shooting Stars
            packModel(50295, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ShootingStar/41604.dat")));
            packModel(50296, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ShootingStar/41605.dat")));
            packModel(50297, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ShootingStar/41606.dat")));
            packModel(50296, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ShootingStar/41612.dat")));
            packModel(50297, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ShootingStar/41613.dat")));

            //Shooting Stars
            packModel(50298, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ShootingStar/count1.dat")));
            packModel(50299, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ShootingStar/count25.dat")));
            packModel(50300, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ShootingStar/count75.dat")));
            packModel(50500, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ShootingStar/70star.dat")));
            packModel(50501, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ShootingStar/80star.dat")));
            packModel(50502, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ShootingStar/90star.dat")));
            packModel(50503, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ShootingStar/emptystar.dat")));

            //Divine Super Combat #2
            packModel(50303, java.nio.file.Files.readAllBytes(Paths.get("assets/models/divinepotions/combat3.dat")));
            packModel(50304, java.nio.file.Files.readAllBytes(Paths.get("assets/models/divinepotions/combat2.dat")));
            packModel(50305, java.nio.file.Files.readAllBytes(Paths.get("assets/models/divinepotions/combat1.dat")));

            //Dragonstone Armors
            packModel(50306, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38020.dat")));
            packModel(50307, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38053.dat")));
            packModel(50308, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38064.dat")));
            packModel(50309, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38096.dat")));
            packModel(50310, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38118.dat")));
            packModel(50311, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38156.dat")));
            packModel(50312, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38186.dat")));
            packModel(50313, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38199.dat")));
            packModel(50314, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38231.dat")));
            packModel(50315, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38255.dat")));
            packModel(50316, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38619.dat")));
            packModel(50317, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38750.dat")));
            packModel(50318, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38751.dat")));
            packModel(50319, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38752.dat")));
            packModel(50320, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38755.dat")));
            packModel(50321, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dragonstonearmor/38757.dat")));

            //Twisted Outfit 3
            packModel(50322, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedoutfits/38961.dat")));
            packModel(50323, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedoutfits/38964.dat")));
            packModel(50324, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedoutfits/38968.dat")));
            packModel(50325, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedoutfits/38969.dat")));
            packModel(50326, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedoutfits/38973.dat")));
            packModel(50327, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedoutfits/38978.dat")));
            packModel(50328, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedoutfits/38979.dat")));
            packModel(50329, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedoutfits/38998.dat")));
            packModel(50330, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedoutfits/39036.dat")));
            packModel(50331, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedoutfits/39038.dat")));
            packModel(50332, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedoutfits/39041.dat")));
            packModel(50333, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedoutfits/39044.dat")));

            //Twisted Slayer Helm
            packModel(50334, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedslayerhelm/38958.dat")));
            packModel(50335, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedslayerhelm/38960.dat")));
            packModel(50336, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedslayerhelm/38970.dat")));
            packModel(50337, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedslayerhelm/38997.dat")));
            packModel(50338, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedslayerhelm/39138.dat")));

            //Blade of Saeldor
            packModel(50339, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalweps/37980.dat")));
            packModel(50340, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalweps/38270.dat")));
            packModel(50341, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalweps/38280.dat")));
            packModel(50342, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedoutfits/38975.dat")));

            packModel(50343, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalfish/37955.dat")));
            packModel(50344, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalfish/37981.dat")));
            packModel(50346, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalfish/37968.dat")));

            //Crystal Rabbit
            packModel(50345, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalrabbit/38569.dat")));

            //200m Capes
            packModel(50347, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/attack 200mb.dat")));
            packModel(50348, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/attack 200m(drop)b.dat")));
            packModel(50349, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Defence 200mb.dat")));
            packModel(50350, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Defence 200m(drop)b.dat")));
            packModel(50351, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/strength 200mb.dat")));
            packModel(50352, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Strength 200m(drop)b.dat")));


            //Brown Graceful
            packModel(50400, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/40909.dat")));
            packModel(50401, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/40913.dat")));
            packModel(50402, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/40915.dat")));
            packModel(50403, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/40916.dat")));
            packModel(50404, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/40921.dat")));
            packModel(50421, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/40922.dat")));
            packModel(50405, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/40926.dat")));
            packModel(50406, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/40935.dat")));
            packModel(50407, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/40937.dat")));
            packModel(50408, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/40938.dat")));
            packModel(50409, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/40942.dat")));
            packModel(50410, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/40943.dat")));
            packModel(50411, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/40947.dat")));
            packModel(50412, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/41031.dat")));
            packModel(50413, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/41035.dat")));
            packModel(50414, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/41066.dat")));
            packModel(50415, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/41069.dat")));
            packModel(50416, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/41070.dat")));
            packModel(50417, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/41071.dat")));
            packModel(50418, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/41072.dat")));
            packModel(50419, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/41073.dat")));
            packModel(50420, java.nio.file.Files.readAllBytes(Paths.get("assets/models/browngraceful/40931.dat")));


//Black Graceful
            packModel(50422, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39638.dat")));
            packModel(50423, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39641.dat")));
            packModel(50424, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39642.dat")));
            packModel(50425, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39643.dat")));
            packModel(50426, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39644.dat")));
            packModel(50427, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39645.dat")));
            packModel(50428, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39741.dat")));
            packModel(50429, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39748.dat")));
            packModel(50430, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39753.dat")));
            packModel(50431, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39758.dat")));
            packModel(50432, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39760.dat")));
            packModel(50433, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39762.dat")));
            packModel(50434, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39763.dat")));
            packModel(50435, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39766.dat")));
            packModel(50436, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39772.dat")));
            packModel(50437, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39773.dat")));
            packModel(50438, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39781.dat")));
            packModel(50439, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39782.dat")));
            packModel(50440, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39784.dat")));
            packModel(50441, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39830.dat")));
            packModel(50442, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39839.dat")));
            packModel(50443, java.nio.file.Files.readAllBytes(Paths.get("assets/models/blackgraceful/39780.dat")));

            //Donator Capes
            packModel(50444, java.nio.file.Files.readAllBytes(Paths.get("assets/models/donatorcapes/Dropb.dat")));
            packModel(50445, java.nio.file.Files.readAllBytes(Paths.get("assets/models/donatorcapes/Wearb.dat")));


            //200M Capes
            packModel(50446, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/agility 200mb.dat")));
            packModel(50447, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Agility 200m(drop)b.dat")));
            packModel(50448, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Contruction 200mb.dat")));
            packModel(50449, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Contruction 200m(drop)b.dat")));
            packModel(50450, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/cooking_200mb.dat")));
            packModel(50451, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Cooking 200m(drop)b.dat")));
            packModel(50452, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/crafting 200mb.dat")));
            packModel(50453, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Crafting 200m(drop)b.dat")));
            packModel(50454, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/farming 200mb.dat")));
            packModel(50455, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Farming 200m(drop)b.dat")));
            packModel(50456, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/fishing 200mb.dat")));
            packModel(50457, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Fishing 200m(drop)b.dat")));
            packModel(50458, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/fletching 200mb.dat")));
            packModel(50459, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Fletching 200m(drop)b.dat")));
            packModel(50460, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/firemaking 200mb.dat")));
            packModel(50461, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Firemaking 200m(drop)b.dat")));
            packModel(50462, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/herblore 200mb.dat")));
            packModel(50463, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Herblore 200m(drop)b.dat")));
            packModel(50464, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/hitpoints 200mb.dat")));
            packModel(50465, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Hitpoints 200m(drop)b.dat")));
            packModel(50466, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/hunter 200mb.dat")));
            packModel(50467, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Hunter 200m(drop)b.dat")));
            packModel(50468, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/magic 200mb.dat")));
            packModel(50469, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Magic 200m(drop)b.dat")));
            packModel(50470, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/mining 200mb.dat")));
            packModel(50471, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Mining 200m(drop)b.dat")));
            packModel(50472, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/prayer 200mb.dat")));
            packModel(50473, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Prayer 200m(drop)b.dat")));
            packModel(50474, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/ranging 200mb.dat")));
            packModel(50475, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Ranging 200m(drop)b.dat")));
            packModel(50476, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/runecrafting 200mb.dat")));
            packModel(50477, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Runecrafting 200m(drop)b.dat")));
            packModel(50478, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/slayer 200mb.dat")));
            packModel(50479, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Slayer 200m(drop)b.dat")));
            packModel(50480, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/smithing 200mb.dat")));
            packModel(50481, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Smithing 200m(drop)b.dat")));
            packModel(50482, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/thieving 200mb.dat")));
            packModel(50483, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Thieving 200m(drop)b.dat")));
            packModel(50484, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/woodcutting 200mb.dat")));
            packModel(50485, java.nio.file.Files.readAllBytes(Paths.get("assets/models/capes/Woodcutting 200m(drop)b.dat")));

            // Halloween
            packModel(50486, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Jack O Kraken.dat")));
            packModel(50487, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Candy bucket wear.dat")));
            packModel(50488, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Candy bucket drop.dat")));

            //Slayer Helms
            packModel(50490, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42673.dat")));
            packModel(50491, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42675.dat")));
            packModel(50492, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42692.dat")));
            packModel(50493, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42704.dat")));
            packModel(50495, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42701.dat")));
            packModel(50496, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42680.dat")));
            packModel(50497, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42690.dat")));
            packModel(50498, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42700.dat")));
            packModel(50499, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42676.dat")));
            packModel(50520, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42684.dat")));
            packModel(50521, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42703.dat")));
            packModel(50522, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42765.dat")));
            packModel(50523, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42687.dat")));
            packModel(50524, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42705.dat")));
            packModel(50525, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42681.dat")));
            packModel(50526, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42682.dat")));
            packModel(50527, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42708.dat")));
            packModel(50528, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42674.dat")));
            packModel(50529, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42689.dat")));
            packModel(50547, java.nio.file.Files.readAllBytes(Paths.get("assets/models/coffinandweildablehammer/42724.dat")));
            packModel(50548, java.nio.file.Files.readAllBytes(Paths.get("assets/models/coffinandweildablehammer/42734.dat")));
            packModel(50549, java.nio.file.Files.readAllBytes(Paths.get("assets/models/coffinandweildablehammer/42737.dat")));

           //Golden Prospector
            packModel(50530, java.nio.file.Files.readAllBytes(Paths.get("assets/models/goldenprospector/41576.dat")));
            packModel(50531, java.nio.file.Files.readAllBytes(Paths.get("assets/models/goldenprospector/41577.dat")));
            packModel(50532, java.nio.file.Files.readAllBytes(Paths.get("assets/models/goldenprospector/41578.dat")));
            packModel(50533, java.nio.file.Files.readAllBytes(Paths.get("assets/models/goldenprospector/41579.dat")));
            packModel(50534, java.nio.file.Files.readAllBytes(Paths.get("assets/models/goldenprospector/41580.dat")));
            packModel(50535, java.nio.file.Files.readAllBytes(Paths.get("assets/models/goldenprospector/41581.dat")));
            packModel(50536, java.nio.file.Files.readAllBytes(Paths.get("assets/models/goldenprospector/41582.dat")));
            packModel(50537, java.nio.file.Files.readAllBytes(Paths.get("assets/models/goldenprospector/41583.dat")));
            packModel(50538, java.nio.file.Files.readAllBytes(Paths.get("assets/models/goldenprospector/41584.dat")));
            packModel(50539, java.nio.file.Files.readAllBytes(Paths.get("assets/models/goldenprospector/41585.dat")));
            packModel(50540, java.nio.file.Files.readAllBytes(Paths.get("assets/models/goldenprospector/41586.dat")));
            packModel(50541, java.nio.file.Files.readAllBytes(Paths.get("assets/models/goldenprospector/41587.dat")));
            packModel(50542, java.nio.file.Files.readAllBytes(Paths.get("assets/models/goldenprospector/41588.dat")));
            packModel(50543, java.nio.file.Files.readAllBytes(Paths.get("assets/models/goldenprospector/41590.dat")));

            //Star NPC & Fragment
            packModel(50544, java.nio.file.Files.readAllBytes(Paths.get("assets/models/goldenprospector/41602.dat")));
            packModel(50545, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ShootingStar/41595.dat")));
            packModel(50546, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42685.dat")));

            //Golden Coffin
            packModel(50550, java.nio.file.Files.readAllBytes(Paths.get("assets/models/coffinandweildablehammer/41734.dat")));
            packModel(50551, java.nio.file.Files.readAllBytes(Paths.get("assets/models/coffinandweildablehammer/41750.dat")));
            packModel(50552, java.nio.file.Files.readAllBytes(Paths.get("assets/models/coffinandweildablehammer/41776.dat")));

            //Weildable Hammer
            packModel(50553, java.nio.file.Files.readAllBytes(Paths.get("assets/models/coffinandweildablehammer/42015.dat")));
            packModel(50554, java.nio.file.Files.readAllBytes(Paths.get("assets/models/coffinandweildablehammer/42016.dat")));

            //Sentinal
            packModel(50555, java.nio.file.Files.readAllBytes(Paths.get("assets/models/sentinal/sentinal.dat")));

            //Inquisitor
            packModel(50556, java.nio.file.Files.readAllBytes(Paths.get("assets/models/inquisitor/39049.dat")));
            packModel(50557, java.nio.file.Files.readAllBytes(Paths.get("assets/models/inquisitor/39050.dat")));
            packModel(50558, java.nio.file.Files.readAllBytes(Paths.get("assets/models/inquisitor/39051.dat")));
            packModel(50559, java.nio.file.Files.readAllBytes(Paths.get("assets/models/inquisitor/39052.dat")));
            packModel(50560, java.nio.file.Files.readAllBytes(Paths.get("assets/models/inquisitor/39053.dat")));
            packModel(50561, java.nio.file.Files.readAllBytes(Paths.get("assets/models/inquisitor/39054.dat")));
            packModel(50562, java.nio.file.Files.readAllBytes(Paths.get("assets/models/inquisitor/39057.dat")));
            packModel(50563, java.nio.file.Files.readAllBytes(Paths.get("assets/models/inquisitor/39060.dat")));
            packModel(50564, java.nio.file.Files.readAllBytes(Paths.get("assets/models/inquisitor/39068.dat")));
            packModel(50565, java.nio.file.Files.readAllBytes(Paths.get("assets/models/inquisitor/39074.dat")));
            packModel(50566, java.nio.file.Files.readAllBytes(Paths.get("assets/models/inquisitor/39075.dat")));
            packModel(50567, java.nio.file.Files.readAllBytes(Paths.get("assets/models/inquisitor/39076.dat")));


            //Bloodfury
            packModel(50568, java.nio.file.Files.readAllBytes(Paths.get("assets/models/amuletofbloodfury/39659.dat")));
            packModel(50569, java.nio.file.Files.readAllBytes(Paths.get("assets/models/amuletofbloodfury/39672.dat")));
            packModel(50570, java.nio.file.Files.readAllBytes(Paths.get("assets/models/amuletofbloodfury/39765.dat")));
            packModel(50571, java.nio.file.Files.readAllBytes(Paths.get("assets/models/amuletofbloodfury/39785.dat")));

            //Blade of saeldor recolors part 1
            packModel(50572, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorzenytecolorsinv3.dat")));
            packModel(50573, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorzenytecolorsmale3.dat")));
            packModel(50574, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorzenytecolorsfemale3.dat")));

            packModel(50575, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorblackinv.dat")));
            packModel(50576, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorblackmale.dat")));
            packModel(50577, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorblackfemale.dat")));

            packModel(50578, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorpurpleinv.dat")));
            packModel(50579, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorpurplemale.dat")));
            packModel(50580, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorpurplefemale.dat")));

            packModel(50581, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorwhiteinv.dat")));
            packModel(50582, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorwhitemale.dat")));
            packModel(50583, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorwhitefemale.dat")));

            packModel(50584, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorredinv2.dat")));
            packModel(50585, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorredmale2.dat")));
            packModel(50586, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorredfemale2.dat")));

            packModel(50587, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorgreeninv.dat")));
            packModel(50588, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorgreenmale.dat")));
            packModel(50589, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorgreenfemale.dat")));

            packModel(50590, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldoryellowinv.dat")));
            packModel(50591, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldoryellowmale.dat")));
            packModel(50592, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldoryellowfemale.dat")));

            packModel(50593, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorblueinv2.dat")));
            packModel(50594, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorbluemale2.dat")));
            packModel(50595, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldorbluefemale2.dat")));


            //Bow of faerdhinen recolors
            packModel(50596, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinenpurpleinv.dat")));
            packModel(50597, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinenpurpleworn.dat")));

            packModel(50598, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinengreeninv.dat")));
            packModel(50599, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinengreenworn.dat")));

            packModel(50600, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinendarkgreeninv.dat")));
            packModel(50601, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinendarkgreenworn.dat")));

            packModel(50602, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinenredinv.dat")));
            packModel(50603, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinenredworn.dat")));

            packModel(50604, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinenblueinv.dat")));
            packModel(50605, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinenblueworn.dat")));

            packModel(50606, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinenyellowinv.dat")));
            packModel(50607, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinenyellowworn.dat")));

            packModel(50608, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinenwhiteinv.dat")));
            packModel(50609, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinenwhiteworn.dat")));

            packModel(50610, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinenblackinv.dat")));
            packModel(50611, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinenblackworn.dat")));

            packModel(50612, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinenzenytecolorsinv.dat")));
            packModel(50613, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bowoffaerdhinenrecolors/bowoffaerdhinenzenytecolorsworn.dat")));

            //Prif crystals
            packModel(50614, java.nio.file.Files.readAllBytes(Paths.get("assets/models/prifcrystals/cadarncrystal.dat")));
            packModel(50615, java.nio.file.Files.readAllBytes(Paths.get("assets/models/prifcrystals/ithellcrystal.dat")));
            packModel(50616, java.nio.file.Files.readAllBytes(Paths.get("assets/models/prifcrystals/iorwerthcrystal.dat")));
            packModel(50617, java.nio.file.Files.readAllBytes(Paths.get("assets/models/prifcrystals/trahaearncrystal.dat")));
            packModel(50618, java.nio.file.Files.readAllBytes(Paths.get("assets/models/prifcrystals/crwyscrystal.dat")));
            packModel(50619, java.nio.file.Files.readAllBytes(Paths.get("assets/models/prifcrystals/meilyrcrystal.dat")));
            packModel(50620, java.nio.file.Files.readAllBytes(Paths.get("assets/models/prifcrystals/hefincrystal.dat")));
            packModel(50621, java.nio.file.Files.readAllBytes(Paths.get("assets/models/prifcrystals/amloddcrystal.dat")));
            packModel(50622, java.nio.file.Files.readAllBytes(Paths.get("assets/models/prifcrystals/calencrystal.dat")));
            packModel(50623, java.nio.file.Files.readAllBytes(Paths.get("assets/models/prifcrystals/zenytecrystal.dat")));

            //Blade of saeldor recolors part 2
            packModel(50624, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldordarkgreeninv.dat")));
            packModel(50625, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldordarkgreenmale.dat")));
            packModel(50626, java.nio.file.Files.readAllBytes(Paths.get("assets/models/saeldorrecolors/saeldordarkgreenfemale.dat")));

            //Crystal Crowns
            packModel(50627, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownoriginalinv.dat")));
            packModel(50628, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownoriginalmale.dat")));
            packModel(50629, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownoriginalfemale.dat")));

            packModel(50630, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownwhiteinv.dat")));
            packModel(50631, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownwhitemale.dat")));
            packModel(50632, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownwhitefemale.dat")));

            packModel(50633, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownblackinv.dat")));
            packModel(50634, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownblackmale.dat")));
            packModel(50635, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownblackfemale.dat")));

            packModel(50636, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownblueinv.dat")));
            packModel(50637, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownbluemale.dat")));
            packModel(50638, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownbluefemale.dat")));

            packModel(50639, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownredinv.dat")));
            packModel(50640, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownredmale.dat")));
            packModel(50641, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownredfemale.dat")));

            packModel(50642, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crowngreeninv.dat")));
            packModel(50643, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crowngreenmale.dat")));
            packModel(50644, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crowngreenfemale.dat")));

            packModel(50645, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crowndarkgreeninv.dat")));
            packModel(50646, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crowndarkgreenmale.dat")));
            packModel(50647, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crowndarkgreenfemale.dat")));

            packModel(50648, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownzenyteinv.dat")));
            packModel(50649, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownzenytemale.dat")));
            packModel(50650, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownzenytefemale.dat")));

            packModel(50651, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownpurpleinv.dat")));
            packModel(50652, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownpurplemale.dat")));
            packModel(50653, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownpurplefemale.dat")));

            packModel(50654, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownyellowinv.dat")));
            packModel(50655, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownyellowmale.dat")));
            packModel(50656, java.nio.file.Files.readAllBytes(Paths.get("assets/models/crystalcrowns/crownyellowfemale.dat")));

            //Mythical Max Cape
            packModel(50657, java.nio.file.Files.readAllBytes(Paths.get("assets/models/mythmax/mythmaxinv.dat")));
            packModel(50658, java.nio.file.Files.readAllBytes(Paths.get("assets/models/mythmax/mythmaxmale.dat")));
            packModel(50659, java.nio.file.Files.readAllBytes(Paths.get("assets/models/mythmax/mythmaxfemale.dat")));

            packModel(50660, java.nio.file.Files.readAllBytes(Paths.get("assets/models/mythmax/mythmaxhoodinv.dat")));
            packModel(50661, java.nio.file.Files.readAllBytes(Paths.get("assets/models/mythmax/mythmaxhoodmale.dat")));
            packModel(50662, java.nio.file.Files.readAllBytes(Paths.get("assets/models/mythmax/mythmaxhoodfemale.dat")));

            //Abyssal Pearls
            packModel(50663, java.nio.file.Files.readAllBytes(Paths.get("assets/models/rcminigame/36129.dat")));
            packModel(50664, java.nio.file.Files.readAllBytes(Paths.get("assets/models/rcminigame/36105.dat")));
            packModel(50665, java.nio.file.Files.readAllBytes(Paths.get("assets/models/rcminigame/36118.dat")));
            packModel(50666, java.nio.file.Files.readAllBytes(Paths.get("assets/models/rcminigame/36153.dat")));
            packModel(50667, java.nio.file.Files.readAllBytes(Paths.get("assets/models/rcminigame/36131.dat")));

            //Hat of the eye red
            packModel(50669, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bloodbark/bloodbarkhelminv.dat")));
            packModel(50670, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bloodbark/bloodbarkhelmmale.dat")));
            packModel(50671, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bloodbark/bloodbarkhelmfemale.dat")));

            //DHCB recolors
            packModel(50672, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dhcb/blackdhcbinv.dat")));
            packModel(50673, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dhcb/blackdhcbmale.dat")));
            packModel(50674, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dhcb/blackdhcbfemale.dat")));

            packModel(50675, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dhcb/bluedhcbinv.dat")));
            packModel(50676, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dhcb/bluedhcbmale.dat")));
            packModel(50677, java.nio.file.Files.readAllBytes(Paths.get("assets/models/dhcb/bluedhcbfemale.dat")));

            //Robe top of the eye red
            packModel(50678, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bloodbark/bloodbarkbodyinv.dat")));
            packModel(50679, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bloodbark/bloodbarkbodymale.dat")));
            packModel(50680, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bloodbark/bloodbarkbodyfemale.dat")));

            //Robe bottoms of the eye red
            packModel(50681, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bloodbark/bloodbarklegsinv.dat")));
            packModel(50682, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bloodbark/bloodbarklegsmale.dat")));
            packModel(50683, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bloodbark/bloodbarklegsfemale.dat")));

            //Boots of the eye red
            packModel(50684, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bloodbark/bloodbarkbootsinv.dat")));
            packModel(50685, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bloodbark/bloodbarkbootsmale.dat")));
            packModel(50686, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bloodbark/bloodbarkbootsfemale.dat")));

            //Gloves of the eye red
            packModel(50687, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bloodbark/bloodbarkglovesinv.dat")));
            packModel(50688, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bloodbark/bloodbarkglovesmale.dat")));
            packModel(50689, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bloodbark/bloodbarkglovesfemale.dat")));

            //Hat of the eye green
            packModel(50690, java.nio.file.Files.readAllBytes(Paths.get("assets/models/swampbark/swampbarkhelminv.dat")));
            packModel(50691, java.nio.file.Files.readAllBytes(Paths.get("assets/models/swampbark/swampbarkhelmmale.dat")));
            packModel(50692, java.nio.file.Files.readAllBytes(Paths.get("assets/models/swampbark/swampbarkhelmfemale.dat")));

            //Robe top of the eye green
            packModel(50693, java.nio.file.Files.readAllBytes(Paths.get("assets/models/swampbark/swampbarkbodyinv.dat")));
            packModel(50694, java.nio.file.Files.readAllBytes(Paths.get("assets/models/swampbark/swampbarkbodymale.dat")));
            packModel(50695, java.nio.file.Files.readAllBytes(Paths.get("assets/models/swampbark/swampbarkbodyfemale.dat")));

            //Robe bottoms of the eye green
            packModel(50696, java.nio.file.Files.readAllBytes(Paths.get("assets/models/swampbark/swampbarklegsinv.dat")));
            packModel(50697, java.nio.file.Files.readAllBytes(Paths.get("assets/models/swampbark/swampbarklegsmale.dat")));
            packModel(50698, java.nio.file.Files.readAllBytes(Paths.get("assets/models/swampbark/swampbarklegsfemale.dat")));

            //Boots of the eye green
            packModel(50699, java.nio.file.Files.readAllBytes(Paths.get("assets/models/swampbark/swampbarkbootsinv.dat")));
            packModel(50700, java.nio.file.Files.readAllBytes(Paths.get("assets/models/swampbark/swampbarkbootsmale.dat")));
            packModel(50701, java.nio.file.Files.readAllBytes(Paths.get("assets/models/swampbark/swampbarkbootsfemale.dat")));

            //Gloves of the eye green
            packModel(50702, java.nio.file.Files.readAllBytes(Paths.get("assets/models/swampbark/swampbarkglovesinv.dat")));
            packModel(50703, java.nio.file.Files.readAllBytes(Paths.get("assets/models/swampbark/swampbarkglovesmale.dat")));
            packModel(50704, java.nio.file.Files.readAllBytes(Paths.get("assets/models/swampbark/swampbarkglovesfemale.dat")));

            //3rd birthday cake
            packModel(50705, java.nio.file.Files.readAllBytes(Paths.get("assets/models/3rdbirthdaycake.dat")));

            //Ring of endurance
            packModel(50706, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ringofendurance.dat")));

            //Vampyric slayer helmet (i)
            packModel(50707, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42725.dat")));
            packModel(50708, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42735.dat")));
            packModel(50709, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42738.dat")));
            packModel(50710, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42783_primary_male_head.dat")));
            packModel(50711, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42783_primary_female_head.dat")));

            //Vampyric slayer helmet
            packModel(50712, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42725_inv.dat")));
            packModel(50713, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42735_primary_male.dat")));
            packModel(50714, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42738_primary_female.dat")));
            packModel(50715, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42783_primary_male_head.dat")));
            packModel(50716, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42783_primary_female_head.dat")));

            //Tzkal slayer helmet
            packModel(50717, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42726_inv.dat")));
            packModel(50718, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42733_primary_male.dat")));
            packModel(50719, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42736_primary_female.dat")));
            packModel(50720, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42782_primary_male_head.dat")));
            packModel(50721, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42782_primary_female_head.dat")));

            //Tzkal slayer helmet (i)
            packModel(50722, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42726_inv.dat")));
            packModel(50723, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42733_primary_male.dat")));
            packModel(50724, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42736_primary_female.dat")));
            packModel(50725, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42782_primary_male_head.dat")));
            packModel(50726, java.nio.file.Files.readAllBytes(Paths.get("assets/models/slayerhelms/42782_primary_female_head.dat")));

            //Ghommals hilt 6
            packModel(50727, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42786_inv.dat")));
            packModel(50728, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42750_primary_male.dat")));
            packModel(50729, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42753_primary_female.dat")));

            //Ghommals hilt 5
            packModel(50730, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42788_inv.dat")));
            packModel(50731, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42760_primary_male.dat")));
            packModel(50732, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42756_primary_female.dat")));

            //Ghommals hilt 4
            packModel(50733, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42791_inv.dat")));
            packModel(50734, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42755_primary_male.dat")));
            packModel(50735, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42749_primary_female.dat")));

            //Ghommals hilt 3
            packModel(50736, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42787_inv.dat")));
            packModel(50737, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42766_primary_male.dat")));
            packModel(50738, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42761_primary_female.dat")));

            //Ghommals hilt 2
            packModel(50739, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42790_inv.dat")));
            packModel(50740, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42745_primary_male.dat")));
            packModel(50741, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42768_primary_female.dat")));

            //Ghommals hilt 1
            packModel(50742, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42789_inv.dat")));
            packModel(50743, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42759_primary_male.dat")));
            packModel(50744, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ghommalshilts/42754_primary_female.dat")));

            //Combat achievements book model
            packModel(50745, java.nio.file.Files.readAllBytes(Paths.get("assets/models/42785_inv.dat")));

            //Combat achievements lamp base model
            packModel(50746, java.nio.file.Files.readAllBytes(Paths.get("assets/models/3348_inv.dat")));

            //Tangleroot additions
            packModel(50747, java.nio.file.Files.readAllBytes(Paths.get("assets/models/tangleroot/39571_inv.dat")));
            packModel(50748, java.nio.file.Files.readAllBytes(Paths.get("assets/models/tangleroot/39572_inv.dat")));
            packModel(50749, java.nio.file.Files.readAllBytes(Paths.get("assets/models/tangleroot/39573_inv.dat")));
            packModel(50750, java.nio.file.Files.readAllBytes(Paths.get("assets/models/tangleroot/39574_inv.dat")));
            packModel(50751, java.nio.file.Files.readAllBytes(Paths.get("assets/models/tangleroot/39575_inv.dat")));

            //Dark Acorn
            packModel(50752, java.nio.file.Files.readAllBytes(Paths.get("assets/models/39661_inv.dat")));

            //Ziggy
            packModel(50753, java.nio.file.Files.readAllBytes(Paths.get("assets/models/14390_inv.dat")));

            //Red
            packModel(50754, java.nio.file.Files.readAllBytes(Paths.get("assets/models/37361_inv.dat")));

            //Phoenix additions
            packModel(50755, java.nio.file.Files.readAllBytes(Paths.get("assets/models/phoenix/39146_inv.dat")));
            packModel(50756, java.nio.file.Files.readAllBytes(Paths.get("assets/models/phoenix/39147_inv.dat")));
            packModel(50757, java.nio.file.Files.readAllBytes(Paths.get("assets/models/phoenix/39148_inv.dat")));
            packModel(50758, java.nio.file.Files.readAllBytes(Paths.get("assets/models/phoenix/39149_inv.dat")));

            //Great blue heron
            packModel(50759, java.nio.file.Files.readAllBytes(Paths.get("assets/models/41628_inv.dat")));

            //Celestial ring
            packModel(50760, java.nio.file.Files.readAllBytes(Paths.get("assets/models/celestialring/41594_inv.dat")));
            //Elven signet
            packModel(50761, java.nio.file.Files.readAllBytes(Paths.get("assets/models/elvensignet/37946_inv.dat")));
            //Celestial signet
            packModel(50762, java.nio.file.Files.readAllBytes(Paths.get("assets/models/celestialsignet/41591_inv.dat")));

            //Imcando Hammer
            packModel(50763, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ImcandoHammer/42149_inv.dat")));
            packModel(50764, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ImcandoHammer/42016_primary_male.dat")));
            packModel(50765, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ImcandoHammer/42015_primary_female.dat")));

            //Holy Sanguinesti Staff
            packModel(50766, java.nio.file.Files.readAllBytes(Paths.get("assets/models/holysanguinesti/42292_inv.dat")));
            packModel(50767, java.nio.file.Files.readAllBytes(Paths.get("assets/models/holysanguinesti/42271_primary_male.dat")));
            packModel(50768, java.nio.file.Files.readAllBytes(Paths.get("assets/models/holysanguinesti/42276_primary_female.dat")));

            //Well of goodwill
            packModel(50769, java.nio.file.Files.readAllBytes(Paths.get("assets/models/wellofgoodwill/10460.dat")));

            //Crystal tree stump
            packModel(50770, java.nio.file.Files.readAllBytes(Paths.get("assets/models/37920.dat")));

            //Halloween Scythe
            packModel(50771, java.nio.file.Files.readAllBytes(Paths.get("assets/models/halloween2022/halloweenscythe6.dat")));
            packModel(50772, java.nio.file.Files.readAllBytes(Paths.get("assets/models/halloween2022/halloweenscytheinv.dat")));

            //Golden Scythe
            packModel(50773, java.nio.file.Files.readAllBytes(Paths.get("assets/models/halloween2022/golden2.dat")));
            packModel(50774, java.nio.file.Files.readAllBytes(Paths.get("assets/models/halloween2022/goldeninv.dat")));

            //Nightmare Orbs and Staves
            packModel(50775, java.nio.file.Files.readAllBytes(Paths.get("assets/models/orbsandstaves/39508_inv.dat")));
            packModel(50776, java.nio.file.Files.readAllBytes(Paths.get("assets/models/orbsandstaves/39510_inv.dat")));
            packModel(50777, java.nio.file.Files.readAllBytes(Paths.get("assets/models/orbsandstaves/39509_inv.dat")));
            packModel(50778, java.nio.file.Files.readAllBytes(Paths.get("assets/models/orbsandstaves/39073_inv.dat")));
            packModel(50779, java.nio.file.Files.readAllBytes(Paths.get("assets/models/orbsandstaves/39056_primary_male.dat")));
            packModel(50780, java.nio.file.Files.readAllBytes(Paths.get("assets/models/orbsandstaves/39063_primary_female.dat")));
            packModel(50781, java.nio.file.Files.readAllBytes(Paths.get("assets/models/orbsandstaves/39070_inv.dat")));
            packModel(50782, java.nio.file.Files.readAllBytes(Paths.get("assets/models/orbsandstaves/39058_primary_male.dat")));
            packModel(50783, java.nio.file.Files.readAllBytes(Paths.get("assets/models/orbsandstaves/39062_primary_female.dat")));
            packModel(50784, java.nio.file.Files.readAllBytes(Paths.get("assets/models/orbsandstaves/39072_inv.dat")));
            packModel(50785, java.nio.file.Files.readAllBytes(Paths.get("assets/models/orbsandstaves/39055_primary_male.dat")));
            packModel(50786, java.nio.file.Files.readAllBytes(Paths.get("assets/models/orbsandstaves/39066_primary_female.dat")));
            packModel(50787, java.nio.file.Files.readAllBytes(Paths.get("assets/models/orbsandstaves/39071_inv.dat")));
            packModel(50788, java.nio.file.Files.readAllBytes(Paths.get("assets/models/orbsandstaves/39061_primary_male.dat")));
            packModel(50789, java.nio.file.Files.readAllBytes(Paths.get("assets/models/orbsandstaves/39065_primary_female.dat")));

            //Crystal seeds
            packModel(50790, java.nio.file.Files.readAllBytes(Paths.get("assets/models/37990_inv.dat")));
            packModel(50791, java.nio.file.Files.readAllBytes(Paths.get("assets/models/37950_inv.dat")));
            packModel(50792, java.nio.file.Files.readAllBytes(Paths.get("assets/models/42601_inv.dat")));

            //Trailblazer outfit
            packModel(50793, java.nio.file.Files.readAllBytes(Paths.get("assets/models/trailblazeroutfit/41089_inv.dat")));
            packModel(50794, java.nio.file.Files.readAllBytes(Paths.get("assets/models/trailblazeroutfit/40911_primary_male.dat")));
            packModel(50795, java.nio.file.Files.readAllBytes(Paths.get("assets/models/trailblazeroutfit/40934_primary_female.dat")));
            packModel(50796, java.nio.file.Files.readAllBytes(Paths.get("assets/models/trailblazeroutfit/41032_primary_male_head.dat")));
            packModel(50797, java.nio.file.Files.readAllBytes(Paths.get("assets/models/trailblazeroutfit/41036_primary_female_head.dat")));

            packModel(50798, java.nio.file.Files.readAllBytes(Paths.get("assets/models/trailblazeroutfit/41074_inv.dat")));
            packModel(50799, java.nio.file.Files.readAllBytes(Paths.get("assets/models/trailblazeroutfit/40923_primary_male.dat")));
            packModel(50800, java.nio.file.Files.readAllBytes(Paths.get("assets/models/trailblazeroutfit/40946_primary_female.dat")));

            packModel(50801, java.nio.file.Files.readAllBytes(Paths.get("assets/models/trailblazeroutfit/41092_inv.dat")));
            packModel(50802, java.nio.file.Files.readAllBytes(Paths.get("assets/models/trailblazeroutfit/40918_primary_male.dat")));
            packModel(50803, java.nio.file.Files.readAllBytes(Paths.get("assets/models/trailblazeroutfit/40941_primary_female.dat")));

            packModel(50804, java.nio.file.Files.readAllBytes(Paths.get("assets/models/trailblazeroutfit/41082_inv.dat")));
            packModel(50805, java.nio.file.Files.readAllBytes(Paths.get("assets/models/trailblazeroutfit/40928_primary_male.dat")));
            packModel(50806, java.nio.file.Files.readAllBytes(Paths.get("assets/models/trailblazeroutfit/40950_primary_female.dat")));

            //Jalrek-Jad
            packModel(50807, java.nio.file.Files.readAllBytes(Paths.get("assets/models/jalrekjad/41568_inv.dat")));

            //Baby mole-rat
            packModel(50808, java.nio.file.Files.readAllBytes(Paths.get("assets/models/babymolerat/42012_inv.dat")));


            //Comp cape
            packModel(50809, java.nio.file.Files.readAllBytes(Paths.get("assets/models/completionistcape/invmodel.dat")));
            packModel(50810, java.nio.file.Files.readAllBytes(Paths.get("assets/models/completionistcape/malemodel.dat")));
            packModel(50811, java.nio.file.Files.readAllBytes(Paths.get("assets/models/completionistcape/femalemodel.dat")));
            packModel(50812, java.nio.file.Files.readAllBytes(Paths.get("assets/models/completionistcape/hoodinv.dat")));
            packModel(50813, java.nio.file.Files.readAllBytes(Paths.get("assets/models/completionistcape/malehood.dat")));
            packModel(50814, java.nio.file.Files.readAllBytes(Paths.get("assets/models/completionistcape/femalehood.dat")));

            //Twisted horns
            packModel(50815, java.nio.file.Files.readAllBytes(Paths.get("assets/models/twistedhorns/39138_inv.dat")));

            //Ash sanctifier
            packModel(50816, java.nio.file.Files.readAllBytes(Paths.get("assets/models/ashsanctifier/42582_inv.dat")));

            TypeParser.packModel(57576, org.apache.commons.compress.utils.IOUtils.toByteArray(new FileInputStream(new File("assets/dice bag/item_model.dat"))));

        } catch (IOException e) {
            log.error(Strings.EMPTY, e);
        }
    }

    public static void packModel(final int id, final byte[] bytes) {
        Game.getCacheMgi().getArchive(ArchiveType.MODELS).addGroup(new Group(id, new mgi.tools.jagcached.cache.File(new ByteBuffer(bytes))));
    }

    public static void packSound(final int id, final byte[] bytes) {
        Game.getCacheMgi().getArchive(ArchiveType.SYNTHS).addGroup(new Group(id, new mgi.tools.jagcached.cache.File(new ByteBuffer(bytes))));
    }

    private static void packClientScripts() throws IOException {
        packClientScript(73, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/bank_command/73.cs2")));
        packClientScript(386, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/tourny_fog/386.cs2")));
        packClientScript(393, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/skill_tab_construction/393.cs2")));
        packClientScript(395, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/skill_tab_construction/395.cs2")));
        packClientScript(687, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/ironman_setup/687.cs2")));
        packClientScript(1004, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/experience_drops_multiplier.cs2")));
        packClientScript(1261, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/tourny_fog/1261.cs2")));
        packClientScript(1705, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/edgeville_map_link/1705.cs2")));
        packClientScript(2066, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/broadcast_custom_links/2066.cs2")));
        packClientScript(2094, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/ironman_setup/2094.cs2")));
        packClientScript(2096, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/ironman_setup/2096.cs2")));
        packClientScript(2186, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/tourny_viewer/2186.cs2")));
        packClientScript(2200, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/achievement_diary_sizes/2200.cs2")));

        packClientScript(699, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/emote_tab/699.cs2")));
        packClientScript(701, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/emote_tab/701.cs2")));
        packClientScript(702, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/emote_tab/702.cs2")));

        for (int id = 3343; id < 3352; id++) {
            packClientScript(id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/pop_up/" + id + ".cs2")));
        }

        for (int id = 3500; id <= 3505; id++) {
            packClientScript(id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/game_noticeboard/" + id + ".cs2")));
        }
        for (int id = 0; id < 16; id++) {
            packClientScript(10000 + id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/teleport_menu/new/" + (10000 + id))));
        }
        packClientScript(10100, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/ironman_setup/10100.cs2")));

        for (int i = 10034; i <= 10048; i++) {
            packClientScript(i, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/wheel_of_fortune/" + i + ".cs2")));
        }

        for (int id = 10102; id <= 10121; id++) {
            packClientScript(id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/drop_viewer/" + id + ".cs2")));
        }

        for (int id = 10200; id <= 10202; id++) {
            packClientScript(id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/game_settings/" + id + ".cs2")));
        }

        for (int id = 10300; id <= 10306; id++) {
            packClientScript(id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/daily_challenges/" + id + ".cs2")));
        }

        for (int id = 10400; id <= 10405; id++) {
            packClientScript(id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/tourny_info/" + id + ".cs2")));
        }

        for (int id = 10500; id <= 10518; id++) {
            packClientScript(id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/tourny_presets/" + id + ".cs2")));
        }

        packClientScript(10600, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/tourny_viewer/10600.cs2")));
        packClientScript(10700, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/hide_roofs/10700.cs2")));

        for (int i = 10800; i <= 10810; i++) {
            packClientScript(i, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/ge_offers/" + i + ".cs2")));
        }
        packClientScript(336, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/godwars_dungeon/336.cs2")));
        packClientScript(342, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/godwars_dungeon/342.cs2")));
        for (int i = 10900; i <= 10912; i++) {
            packClientScript(i, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/eco_presets/" + i + ".cs2")));
        }

        packClientScript(1311, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/tog_sidepanel_timer.cs2")));

    }

    public static void packClientScript(final int id, final byte[] bytes) {
        Game.getCacheMgi().getArchive(ArchiveType.CLIENTSCRIPTS).addGroup(new Group(id, new mgi.tools.jagcached.cache.File(new ByteBuffer(bytes))));
    }

    private static void packInterfaces() throws IOException {
        val cache = Game.getCacheMgi();
        val group1 = new Group(660);
        for (int i = 0; i <= 8; i++) {
            group1.addFile(new mgi.tools.jagcached.cache.File(new ByteBuffer(java.nio.file.Files.readAllBytes(Paths.get("assets/interfaces/660/" + i)))));
        }
        cache.getArchive(ArchiveType.INTERFACES).addGroup(group1);

        val group2 = new Group(700);
        for (int i = 0; i < 38; i++) {
            group2.addFile(new mgi.tools.jagcached.cache.File(new ByteBuffer(java.nio.file.Files.readAllBytes(Paths.get("assets/interfaces/700/" + i)))));
        }
        cache.getArchive(ArchiveType.INTERFACES).addGroup(group2);
    }

    public static void packMap(final int id, final byte[] landscape, final byte[] map) {
        val cache = Game.getCacheMgi();
        val archive = cache.getArchive(ArchiveType.MAPS);
        val xteas = XTEALoader.getXTEAs(id);
        val regionX = id >> 8;
        val regionY = id & 0xFF;

        val mapGroup = archive.findGroupByName("m" + regionX + "_" + regionY);

        val landGroup = archive.findGroupByName("l" + regionX + "_" + regionY, xteas);

        if (map != null) {
            if (landGroup != null) {
                landGroup.findFileByID(0).setData(new ByteBuffer(map));
            } else {
                val newLandGroup = new Group(archive.getFreeGroupID(), new mgi.tools.jagcached.cache.File(new ByteBuffer(map)));
                newLandGroup.setName("l" + regionX + "_" + regionY);
                archive.addGroup(newLandGroup);
            }
        }

        if (landscape != null) {
            if (mapGroup != null) {
                mapGroup.findFileByID(0).setData(new ByteBuffer(landscape));
            } else {
                val newMapGroup = new Group(archive.getFreeGroupID() + 1, new mgi.tools.jagcached.cache.File(new ByteBuffer(landscape)));
                newMapGroup.setName("m" + regionX + "_" + regionY);
                newMapGroup.setXTEA(xteas);
                archive.addGroup(newMapGroup);
            }
        }

    }

    private static void packMaps() throws IOException {
        packMap(9261, java.nio.file.Files.readAllBytes(Paths.get("assets/map/island_l_regular.dat")), MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/island_m_regular.dat")),
                o -> {
                    if (o.getId() == 46087) {
                        o.setId(46089);
                    }
                    return false;
                }));
        packMap(10388, java.nio.file.Files.readAllBytes(Paths.get("assets/map/yanille/328.dat")),
                java.nio.file.Files.readAllBytes(Paths.get("assets/map/yanille/329.dat")));
        packMap(11567, null, MapUtils.inject(11567, null,
                new WorldObject(187, 10, 1, new Location(2919, 3054, 0))));
        packMap(11595, null, MapUtils.inject(11595, null,
                new WorldObject(26254, 10, 0, new Location(2931, 4822, 0)),
                new WorldObject(26254, 10, 0, new Location(2896, 4821, 0)),
                new WorldObject(26254, 10, 1, new Location(2900, 4845, 0)),
                new WorldObject(26254, 10, 3, new Location(2920, 4848, 0))));
        packMap(12342, java.nio.file.Files.readAllBytes(Paths.get("assets/map/home28_l.dat")), MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/home28_m.dat")),
                o -> {
                    o.setId(ChristmasObject.redirectedIds.getOrDefault(o.getId(), o.getId()));
                    if (o.getId() == 46087) {
                        o.setId(46089);
                    } else if (o.getId() == 11784) {
                        o.setId(35009);
                    } else if (o.getId() == 11785) {
                        o.setId(35010);
                    } else if (o.getId() == 15617) {
                        o.setId(46030);
                    }
                    return o.hashInRegion() == new Location(3092, 3487, 0).hashInRegion()
                            || o.hashInRegion() == new Location(3094, 3489, 0).hashInRegion()
                            || o.hashInRegion() == new Location(3095, 3488, 0).hashInRegion()
                            || o.hashInRegion() == new Location(3097, 3488, 0).hashInRegion()
                            || o.hashInRegion() == new Location(3100, 3486, 0).hashInRegion()
                            || o.hashInRegion() == new Location(3127, 3496, 0).hashInRegion()
                            // Easter modifications
                                   /*|| o.hashInRegion() == new Location(3087, 3470, 0).hashInRegion()
                                   || o.hashInRegion() == new Location(3087, 3472, 0).hashInRegion()
                                   || o.hashInRegion() == new Location(3086, 3473, 0).hashInRegion()
                                   || o.hashInRegion() == new Location(3092, 3469, 0).hashInRegion()

                                   || o.hashInRegion() == new Location(3091, 3475, 0).hashInRegion()
                                   || o.hashInRegion() == new Location(3090, 3475, 0).hashInRegion()
                                   || o.hashInRegion() == new Location(3089, 3475, 0).hashInRegion()
                                   || o.hashInRegion() == new Location(3088, 3475, 0).hashInRegion()
                                   || o.hashInRegion() == new Location(3087, 3475, 0).hashInRegion()
                                   || o.hashInRegion() == new Location(3087, 3476, 0).hashInRegion()*/
                            ;
                },
                new WorldObject(40070, 10, 3, new Location(3099, 3505, 0)),//Ticket dispenser
                new WorldObject(40047, 10, 3, new Location(3081, 3487, 0)),//Well of goodwill
                new WorldObject(0, 10, 0, new Location(3117, 3474, 0)),//Blocking tile behind combat dummy
                new WorldObject(0, 10, 0, new Location(3117, 3481, 0)),//Blocking tile behind combat dummy
                new WorldObject(StashUnit.EDGEVILLE_GENERAL_STORE.getObjectId(), 10, 0, new Location(3079, 3484, 0)),
                new WorldObject(35008, 10, 3, new Location(3092, 3508, 1)),//Lectern w/ study option at home.
                new WorldObject(14108, 22, 0, new Location(3101, 3487, 0)),//Map hyperlink for edgeville dungeon.
                new WorldObject(172, 10, 2, new Location(3091, 3511, 0)),//Crystal chest
                new WorldObject(7389, 22, 0, new Location(3097, 3503, 0)),//Map icon for portal.
                new WorldObject(7389, 22, 0, new Location(3097, 3488, 0)),//Map icon for spiritual tree.
                new WorldObject(673, 22, 0, new Location(3119, 3511, 0)),//Map icon for emblem trader.
                new WorldObject(35003, 10, 1, new Location(3096, 3487, 0)),//Spiritual fairy ring.
                new WorldObject(35000, 10, 2, new Location(3095, 3503, 0)),//Portal
                new WorldObject(35001, 10, 0, new Location(3090, 3486, 0)),//Box of Restoration
                new WorldObject(26756, 10, 0, new Location(3085, 3509, 0)),//Wilderness statistics

                new WorldObject(2774, 22, 0, new Location(3114, 3506, 0)),//pottery wheel icon
                new WorldObject(2771, 22, 0, new Location(3109, 3505, 0)),//water source icon
                new WorldObject(2743, 22, 0, new Location(3116, 3502, 0)),//anvil icon
                new WorldObject(2742, 22, 0, new Location(3113, 3497, 0)),//furnace icon
                new WorldObject(35023, 4, 3, new Location(3085, 3486, 0)),//Daily board
                new WorldObject(35024, 10, 0, new Location(3093, 3511, 0)),//Magic storage unit
                new WorldObject(10562, 10, 0, new Location(3085, 3482, 0)), //Bank chest inside general store
                //new WorldObject(46092, 10, 0, new Location(2068, 5402, 0)), //Christmas cupboard




                // Easter modifications
                /*new WorldObject(EasterConstants.WARREN_ENTRANCE, 10, 0, new Location(3089, 3469, 0)),
                new WorldObject(20132, 22, 0, new Location(3089, 3474, 0)),*/ // event mapicon

                //new WorldObject(ChristmasConstants.CHRISTMAS_CUPBOARD_ID, 10, 0, ChristmasConstants.homeChristmasCupboardLocation),//Christmas cupboard
                //new WorldObject(20132, 22, 0, new Location(3092, 3503, 0)),//Event map icon
                new WorldObject(1579, 22, 2, 3100, 3487, 0),//Trapdoor
                new WorldObject(2734, 22, 0, 3095, 3483, 0),//Missing mapicon
                new WorldObject(2747, 22, 0, 3092, 3485, 0),//Missing mapicon
                new WorldObject(2771, 22, 1, 3100, 3489, 0),//Missing mapicon
                new WorldObject(2772, 22, 2, 3102, 3494, 0),//Missing mapicon
                //new WorldObject(2774, 22, 0, 3108, 3497, 0),//Missing mapicon
                //new WorldObject(2742, 22, 0, 3112, 3501, 0),//Missing mapicon
                //new WorldObject(5118, 22, 1, 3113, 3509, 0),//Missing mapicon
                //new WorldObject(23590, 22, 0, 3117, 3516, 0),//Missing mapicon
                new WorldObject(26301, 22, 3, 3102, 3506, 0),//Missing mapicon
                new WorldObject(33163, 22, 0, 3098, 3511, 0),//Missing mapicon
                new WorldObject(2752, 22, 2, 3091, 3509, 0),//Missing mapicon
                new WorldObject(16458, 22, 0, 3090, 3498, 0),//Missing mapicon
                new WorldObject(2738, 22, 0, 3086, 3493, 0),//Missing mapicon
                new WorldObject(2756, 22, 1, 3084, 3507, 0),//Missing mapicon
                new WorldObject(2758, 22, 1, 3079, 3510, 0),//Missing mapicon
                new WorldObject(2753, 22, 1, 3078, 3507, 0),//Missing mapicon
                new WorldObject(2750, 22, 0, 3075, 3502, 0),//Missing mapicon
                new WorldObject(2766, 22, 0, 3078, 3499, 0),//Missing mapicon
                new WorldObject(2760, 22, 0, 3077, 3492, 0),//Missing mapicon
                new WorldObject(2768, 22, 2, 3073, 3491, 0),//Missing mapicon
                new WorldObject(2735, 22, 0, 3077, 3488, 0),//Missing mapicon
                new WorldObject(2733, 22, 0, 3082, 3485, 0),//Missing mapicon
                new WorldObject(35002, 10, 0, new Location(3096, 3511, 0))));//Mounted max cape
        packMap(13109, null, MapUtils.inject(13109, null,
                new WorldObject(187, 10, 1, new Location(3322, 3428, 0))));
        packMap(14477, java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/m56_141.dat")), java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/l56_141.dat")));
        packMap(14478, java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/m56_142.dat")), java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/l56_142.dat")));
        packMap(14733, java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/m57_141.dat")), java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/l57_141.dat")));
        packMap(14734, java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/m57_142.dat")), java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/l57_142.dat")));
        packMap(15245, java.nio.file.Files.readAllBytes(Paths.get("assets/map/tournament/2.dat")), java.nio.file.Files.readAllBytes(Paths.get("assets/map/tournament/3.dat")));
        packMap(15248, java.nio.file.Files.readAllBytes(Paths.get("assets/map/tournament/0.dat")), MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/tournament/1.dat")), null,
                new WorldObject(35005, 10, 3, new Location(3806, 9245, 0)),
                new WorldObject(35006, 10, 1, new Location(3813, 9256, 0)),
                new WorldObject(35007, 10, 0, new Location(3799, 9256, 0))));

        packMap(13139, null, MapUtils.inject(13139, null,
                new WorldObject(35020, 10, 0, new Location(3279, 5345, 2)),
                new WorldObject(35020, 10, 0, new Location(3312, 5344, 2))));

        packMap(13395, null, MapUtils.inject(13395, null,
                new WorldObject(35020, 10, 0, new Location(3343, 5346, 2))));

        packMap(12093, null, MapUtils.inject(12093, null,
                new WorldObject(40000, 10, 0, new Location(3018, 3955, 1))));

        //LLetya
        packMap(9265, null, MapUtils.inject(9265, o -> {
                    return o.getId() == ObjectId.TREE_8742;
                },
                new WorldObject(40006, 10, 3, new Location(2343, 3171, 1)), new WorldObject(40007, 10, 3, new Location(2343, 3172, 1)),
                new WorldObject(40052, 10, 2, new Location(2322, 3191, 0))));
        //Tirannwyn
        packMap(9008, null, MapUtils.inject(9008, o -> {
                    return o.getId() == ObjectId.DENSE_FOREST || o.getId() == ObjectId.DENSE_FOREST_3938 || o.getId() == ObjectId.DENSE_FOREST_3939 ||
                            o.getId() == ObjectId.DENSE_FOREST_3998 || o.getId() == ObjectId.DENSE_FOREST_3999 || o.getId() == ObjectId.STICKS;
                },
                new WorldObject(40050, 10, 0, new Location(2290, 3118, 0)), new WorldObject(40050, 10, 0, new Location(2296, 3120, 0)),
                new WorldObject(40050, 10, 0, new Location(2288, 3122, 0)), new WorldObject(40050, 10, 0, new Location(2294, 3119, 0)),
                new WorldObject(40050, 10, 0, new Location(2290, 3126, 0)), new WorldObject(40050, 10, 0, new Location(2289, 3130, 0))));
        //Tirannwyn
        packMap(9009, null, MapUtils.inject(9009, o -> {
            return o.getId() == ObjectId.DENSE_FOREST || o.getId() == ObjectId.DENSE_FOREST_3938 || o.getId() == ObjectId.DENSE_FOREST_3939 ||
                    o.getId() == ObjectId.DENSE_FOREST_3998 || o.getId() == ObjectId.DENSE_FOREST_3999 || o.getId() == ObjectId.STICKS;
        }));
        packMap(9010, null, MapUtils.inject(9010, o -> {
            return o.getId() == ObjectId.DENSE_FOREST || o.getId() == ObjectId.DENSE_FOREST_3938 || o.getId() == ObjectId.DENSE_FOREST_3939 ||
                    o.getId() == ObjectId.DENSE_FOREST_3998 || o.getId() == ObjectId.DENSE_FOREST_3999 || o.getId() == ObjectId.STICKS;
        }));
        //Tirannwyn
        packMap(8752, null, MapUtils.inject(8752, o -> {
            return o.getId() == ObjectId.DENSE_FOREST || o.getId() == ObjectId.DENSE_FOREST_3938 || o.getId() == ObjectId.DENSE_FOREST_3939 ||
                    o.getId() == ObjectId.DENSE_FOREST_3998 || o.getId() == ObjectId.DENSE_FOREST_3999 || o.getId() == ObjectId.STICKS;
        }));
        //Tirannwyn
        packMap(8753, null, MapUtils.inject(8753, o -> {
                    return o.getId() == ObjectId.DENSE_FOREST || o.getId() == ObjectId.DENSE_FOREST_3938 || o.getId() == ObjectId.DENSE_FOREST_3939 ||
                            o.getId() == ObjectId.DENSE_FOREST_3998 || o.getId() == ObjectId.DENSE_FOREST_3999 || o.getId() == ObjectId.STICKS;
                },
                new WorldObject(40050, 10, 0, new Location(2178, 3182, 0)), new WorldObject(40050, 10, 0, new Location(2191, 3185, 0)),
                new WorldObject(40050, 10, 0, new Location(2180, 3177, 0)), new WorldObject(40050, 10, 0, new Location(2195, 3183, 0))));
        packMap(8754, null, MapUtils.inject(8754, o -> {
            return o.getId() == ObjectId.DENSE_FOREST || o.getId() == ObjectId.DENSE_FOREST_3938 || o.getId() == ObjectId.DENSE_FOREST_3939 ||
                    o.getId() == ObjectId.DENSE_FOREST_3998 || o.getId() == ObjectId.DENSE_FOREST_3999 || o.getId() == ObjectId.STICKS;
        }));
        packMap(8496, null, MapUtils.inject(8496, null,
                new WorldObject(40050, 10, 0, new Location(2149, 3114, 0)), new WorldObject(40050, 10, 0, new Location(2148, 3112, 0)),
                new WorldObject(40050, 10, 0, new Location(2151, 3102, 0)), new WorldObject(40050, 10, 0, new Location(2156, 3082, 0)),
                new WorldObject(40050, 10, 0, new Location(2165, 3100, 0)), new WorldObject(40050, 10, 0, new Location(2165, 3097, 0)),
                new WorldObject(40050, 10, 0, new Location(2151, 3106, 0)), new WorldObject(40050, 10, 0, new Location(2156, 3086, 0))
        ));

        //Singing Bowl
        packMap(9265, null, MapUtils.inject(9265, null,
                new WorldObject(40008, 10, 0, new Location(2327, 3166, 2))));

        //Elven Chest
        packMap(11154, null, MapUtils.inject(11154, null,
                new WorldObject(40002, 10, 1, new Location(2759, 9377, 0))));


        //Mythical Chest
        packMap(9772, null, MapUtils.inject(9772, null,
                new WorldObject(40056, 10, 1, new Location(2445, 2850, 0))));

        //Crystal Rocks
        packMap(11154, null, MapUtils.inject(11154, null,
                new WorldObject(7466, 10, 2, new Location(2767, 9369, 0)), new WorldObject(7466, 10, 2, new Location(2770, 9366, 0)),
                new WorldObject(7466, 10, 2, new Location(2768, 9361, 0)), new WorldObject(7466, 10, 2, new Location(2767, 9360, 0)),
                new WorldObject(7466, 10, 2, new Location(2766, 9361, 0)), new WorldObject(7466, 10, 2, new Location(2762, 9367, 0)),
                new WorldObject(7466, 10, 2, new Location(2770, 9365, 0)), new WorldObject(7466, 10, 2, new Location(2771, 9364, 0)),
                new WorldObject(7466, 10, 2, new Location(2770, 9363, 0))));

        //Christmas 2022 Event
        /*
        packMap(10558, null, MapUtils.inject(10558, null,
                new WorldObject(46089, 10, 0, new Location(2655, 3998, 1))));
        packMap(10558, null, MapUtils.inject(10558, null,
                new WorldObject(46077, 10, 0, new Location(2656, 3998, 1))));
        */


        //200m Capes

        packMap(12342, null, MapUtils.inject(12342, null,
                new WorldObject(40035, 10, 0, new Location(3090, 3507, 2))));

        //GODWARS_CHESTS

        packMap(12190, null, MapUtils.inject(12190, null,
                new WorldObject(38500, 10, 0, new Location(3064, 10151, 3))));

        //Stars
        packMap(12852, null, MapUtils.inject(12852, null,
                new WorldObject(40040, 10, 0, new Location(3228, 3367, 0))));
        packMap(10803, null, MapUtils.inject(10803, null,
                new WorldObject(40040, 10, 0, new Location(2732, 3283, 0))));
        packMap(9776, null, MapUtils.inject(9776, null,
                new WorldObject(40040, 10, 0, new Location(2457, 3089, 0))));
        packMap(14131, null, MapUtils.inject(14131, null,
                new WorldObject(40041, 10, 0, new Location(3565, 3289, 0))));
        packMap(9265, null, MapUtils.inject(9265, null,
                new WorldObject(40041, 10, 0, new Location(2340, 3164, 0))));
        packMap(12088, null, MapUtils.inject(12088, null,
                new WorldObject(40042, 10, 0, new Location(3029, 3631, 0))));


        //Wilderness Resource Area
        packMap(12605, null, MapUtils.inject(12605, null,
                new WorldObject(40062, 10, 0, new Location(3194, 3927, 0)), new WorldObject(40062, 10, 0, new Location(3193, 3926, 0)),
                new WorldObject(40062, 10, 0, new Location(3194, 3925, 0)), new WorldObject(40062, 10, 0, new Location(3192, 3925, 0)),
                new WorldObject(40062, 10, 0, new Location(3192, 3927, 0)),

                new WorldObject(40063, 10, 0, new Location(3195, 3933, 0)), new WorldObject(40063, 10, 0, new Location(3193, 3933, 0)),
                new WorldObject(40063, 10, 0, new Location(3194, 3934, 0)), new WorldObject(40063, 10, 0, new Location(3195, 3935, 0)),
                new WorldObject(40063, 10, 0, new Location(3193, 3935, 0)),

                new WorldObject(40064, 10, 0, new Location(3191, 3942, 0)), new WorldObject(40064, 10, 0, new Location(3190, 3943, 0)),
                new WorldObject(40064, 10, 0, new Location(3192, 3943, 0)), new WorldObject(40064, 10, 0, new Location(3192, 3941, 0)),
                new WorldObject(40064, 10, 0, new Location(3190, 3941, 0)),

                new WorldObject(40065, 10, 0, new Location(3183, 3940, 0)), new WorldObject(40065, 10, 0, new Location(3182, 3941, 0)),
                new WorldObject(40065, 10, 0, new Location(3184, 3941, 0)),

                new WorldObject(40066, 10, 0, new Location(3175, 3939, 0)), new WorldObject(40066, 10, 0, new Location(3175, 3941, 0)),
                new WorldObject(40066, 10, 0, new Location(3176, 3940, 0)), new WorldObject(40066, 10, 0, new Location(3177, 3941, 0)),
                new WorldObject(40066, 10, 0, new Location(3177, 3939, 0)),

                new WorldObject(40067, 10, 0, new Location(3177, 3927, 0)), new WorldObject(40067, 10, 0, new Location(3177, 3925, 0)),
                new WorldObject(40067, 10, 0, new Location(3176, 3926, 0)), new WorldObject(40067, 10, 0, new Location(3175, 3925, 0)),
                new WorldObject(40067, 10, 0, new Location(3175, 3927, 0)),

                new WorldObject(40068, 10, 0, new Location(3181, 3935, 0)), new WorldObject(40068, 10, 0, new Location(3174, 3933, 0)),
                new WorldObject(40068, 10, 0, new Location(3193, 3938, 0)), new WorldObject(40068, 10, 0, new Location(3194, 3929, 0)),

                new WorldObject(40069, 10, 0, new Location(3174, 3928, 0)), new WorldObject(40069, 10, 0, new Location(3189, 3925, 0))
        ));
        packMap(12855, null, MapUtils.inject(12855, null,
                new WorldObject(40091, 10, 0, new Location(3257, 3541, 0))));

        packMap(4674, java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Armadyl/0.dat")),
                MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Armadyl/1.dat")), o -> {
                    if (o.getId() == 20843) {
                        o.setId(35016);
                    } else if (o.getId() == 26769) {
                        o.setId(35013);
                    } else if (o.getId() == 23708) {
                        o.setId(35019);
                    }
                    return false;
                }));

        packMap(4675, java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Bandos/0.dat")),
                MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Bandos/1.dat")), o -> {
                    if (o.getId() == 9368) {
                        o.setId(35014);
                    } else if (o.getId() == 26769) {
                        o.setId(35013);
                    } else if (o.getId() == 23708) {
                        o.setId(35019);
                    }
                    return o.hashInRegion() == new Location(1191, 4306, 0).hashInRegion();
                }, new WorldObject(35019, 10, 0, new Location(1189, 4313, 0))));

        packMap(4676, java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Zamorak/0.dat")),
                MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Zamorak/1.dat")), o -> {
                    if (o.getId() == 14845) {
                        o.setId(35015);
                    } else if (o.getId() == 26769) {
                        o.setId(35013);
                    } else if (o.getId() == 23708) {
                        o.setId(35019);
                    }
                    //Removes object which produces ambient waterfall sound and the stash unit.
                    return o.getId() == 16399 || o.getId() == 29054;
                }));

        packMap(4677, java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Saradomin/0.dat")),
                MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Saradomin/1.dat")), o -> {
                    if (o.getId() == 26740) {
                        o.setId(35017);
                    } else if (o.getId() == 21120) {
                        o.setId(35018);
                    } else if (o.getId() == 23708) {
                        o.setId(35019);
                    }
                    return o.getId() == 26375 || (o.getXInRegion() == (1203 & 0x3F) && o.getYInRegion() == (4422 & 0x3F));
                }, new WorldObject(17030, 22, 0, 1195, 4440, 0)));

        packMap(11346, java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Armadyl/1858.dat")),
                MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Armadyl/1859.dat")), o -> {
                            if (o.getId() == 20843) {
                                o.setId(35016);
                            } else if (o.getId() == 26769) {
                                o.setId(35013);
                            } else if (o.getId() == 23708) {
                                o.setId(35019);
                            }
                            return false;
                        }, new WorldObject(26502, 10, 3, 2839, 5295, 2),
                        new WorldObject(0, 10, 0, 2840, 5294, 2),
                        new WorldObject(0, 10, 0, 2838, 5294, 2)));

        packMap(11347, java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Bandos/1860.dat")),
                MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Bandos/1861.dat")), o -> {
                    if (o.getId() == 9368) {
                        o.setId(35014);
                    } else if (o.getId() == 26769) {
                        o.setId(35013);
                    } else if (o.getId() == 23708) {
                        o.setId(35019);
                    }
                    return o.hashInRegion() == new Location(2856, 5357, 2).hashInRegion();
                }, new WorldObject(35019, 10, 0, new Location(2854, 5364, 2))));

        packMap(11602, java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Saradomin/1862.dat")),
                MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Saradomin/1863.dat")), o -> {
                    if (o.getId() == 26740) {
                        o.setId(35017);
                    } else if (o.getId() == 21120) {
                        o.setId(35018);
                    } else if (o.getId() == 23708) {
                        o.setId(35019);
                    }
                    return false;
                }, new WorldObject(17030, 22, 0, 2923, 5272, 0)));

        packMap(11603, MapUtils.processTiles(new ByteBuffer(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Zamorak/1856.dat"))), tile -> {
                    if (tile.getUnderlayId() == 23) {
                        tile.setUnderlayId((byte) 0);
                    }
                    if (tile.getOverlayId() == 33) {
                        tile.setOverlayId((byte) 0);
                    }
                }).getBuffer(),
                MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Zamorak/1857.dat")), o -> {
                    if (o.getId() == 14845) {
                        o.setId(35015);
                    } else if (o.getId() == 26769) {
                        o.setId(35013);
                    } else if (o.getId() == 23708) {
                        o.setId(35019);
                    }
                    return false;
                }));
        //Bank deposit boxes
        packMap(14642, null, MapUtils.inject(14642, null,
                new WorldObject(32665, 10, 1, new Location(3655, 3229, 0)))); //meiyerditch
        packMap(11568, null, MapUtils.inject(11568, null,
                new WorldObject(29108, 10, 1, new Location(2902, 3119, 0)))); //karambwan
        packMap(6972, null, MapUtils.inject(6972, null,
                new WorldObject(29090, 10, 4, new Location(1767, 3847, 0)))); //dense essence
        packMap(10042, null, MapUtils.inject(10042, null,
                new WorldObject(26254, 10, 4, new Location(2524, 3768, 0)))); //waterbirth island
        packMap(10307, null, MapUtils.inject(10307, null,
                new WorldObject(26254, 10, 2, new Location(2595, 4316, 0)))); //puro puro
        packMap(7226, null, MapUtils.inject(7226, null,
                new WorldObject(29108, 10, 1, new Location(1817, 3772, 0)))); //anglers
        packMap(13877, null, MapUtils.inject(13877, null,
                new WorldObject(29105, 10, 2, new Location(3478, 3416, 0)))); //mortmyre fungus

        //Tob Rewards chest
        packMap(14642, null, MapUtils.inject(14642, null,
                new WorldObject(40053, 10, 4, new Location(3650, 3217, 0))));




        val godwarsDefs = WorldMapDefinitions.decode("godwars");
        godwarsDefs.updateFullChunks(11602, 11601, 0, 1, 4);
        godwarsDefs.updateFullChunks(11603, 2);
        godwarsDefs.updateFullChunks(11346, 2);
        godwarsDefs.updateFullChunks(11347, 2);
        godwarsDefs.encode("godwars");

        val defs = WorldMapDefinitions.decode("main");
        defs.update(9261, 0);
        defs.update(12342, 0);
        defs.encode("main");

        log.info("Finished repacking maps.");
    }


}
