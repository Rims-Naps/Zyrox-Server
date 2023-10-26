package com.zenyte.game.world.entity.player.teleportsystem;

import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.magic.spells.teleports.TeleportType;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.TextUtils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.PlainChat;
import com.zenyte.plugins.item.DiceItem;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static com.zenyte.game.world.entity.player.teleportsystem.TeleportCategory.BOSSES;
import static com.zenyte.game.world.entity.player.teleportsystem.TeleportCategory.CITIES;
import static com.zenyte.game.world.entity.player.teleportsystem.TeleportCategory.DUNGEONS;
import static com.zenyte.game.world.entity.player.teleportsystem.TeleportCategory.MINIGAMES;
import static com.zenyte.game.world.entity.player.teleportsystem.TeleportCategory.MISC;
import static com.zenyte.game.world.entity.player.teleportsystem.TeleportCategory.SKILLING;
import static com.zenyte.game.world.entity.player.teleportsystem.TeleportCategory.TRAINING;
import static com.zenyte.game.world.entity.player.teleportsystem.TeleportCategory.WILDERNESS;

/**
 * @author Tommeh | 13-11-2018 | 17:43
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
public enum PortalTeleport implements Teleport {

    LUMBRIDGE(CITIES, "Lumbridge Castle", "Location: Lumbridge Castle", UnlockType.DEFAULT, new Location(3222, 3219, 0)),
    DRAYNOR_VILLAGE(CITIES, "Draynor Village Market", "Location: Draynor Village Market", UnlockType.DEFAULT, new Location(3080, 3250, 0)),
    VARROCK(CITIES, "Varrock Square", "Location: Varrock Square", UnlockType.DEFAULT, new Location(3213, 3424, 0)),
    RELLEKKA(CITIES, "Rellekka Market", "Location: Rellekka Market", UnlockType.DEFAULT, new Location(2643, 3677, 0)),
    ARDOUGNE(CITIES, "Ardougne Market", "Location: Ardougne Market", UnlockType.DEFAULT, new Location(2661, 3305, 0)),
    CAMELOT(CITIES, "Camelot Castle", "Location: Camelot Castle", UnlockType.DEFAULT, new Location(2757, 3478, 0)),
    KOUREND(CITIES, "Kourend Castle", "Location: Kourend Castle", UnlockType.DEFAULT, new Location(1643, 3674, 0)),
    LLETYA(CITIES, "Lletya Center", "Location: Lletya Center", UnlockType.DEFAULT, new Location(2332, 3172, 0)),
    YANILLE(CITIES, "Yanille Western Wing", "Location: Yanille Western Wing", UnlockType.DEFAULT, new Location(2544, 3092, 0)),
    BURTHORPE(CITIES, "Burthorpe Center", "Location: Burthorpe Center", UnlockType.DEFAULT, new Location(2899, 3545, 0)),
    TAVERLEY(CITIES, "Taverley Center", "Location: Taverley Center", UnlockType.DEFAULT, new Location(2896, 3455, 0)),
    FALADOR(CITIES, "Falador Western Wing", "Location: Falador Western Wing", UnlockType.DEFAULT, new Location(2965, 3378, 0)),
    RIMMINGTON(CITIES, "Rimmington Center", "Location: Rimmington Center", UnlockType.DEFAULT, new Location(2957, 3216, 0)),
    BRIMHAVEN(CITIES, "Brimhaven Coast", "Location: Brimhaven Coast", UnlockType.DEFAULT, new Location(2762, 3166, 0)),
    SHILO_VILLAGE(CITIES, "Shilo Village Entrance", "Location: Shilo Village Entrance", UnlockType.DEFAULT, new Location(2865, 2952, 0)),
    POLLNIVNEACH(CITIES, "Pollnivneach Center", "Location: Pollnivneach Center", UnlockType.DEFAULT, new Location(3359, 2968, 0)),
    NARDAH(CITIES, "Nardah Center", "Location: Nardah Center", UnlockType.DEFAULT, new Location(3434, 2917, 0)),
    CANIFIS(CITIES, "Canifis Center", "Location: Canifis Center", UnlockType.DEFAULT, new Location(3494, 3489, 0)),
    MORT_TON(CITIES, "Mort'ton Center", "Location: Mort'ton Center", UnlockType.DEFAULT, new Location(3488, 3287, 0)) {
        @Override
        public String toString() {
            return "Mort'ton";
        }
    },
    AL_KHARID(CITIES, "Al-Kharid Palace Entrance", "Location: Al-Kharid Palace Entrance", UnlockType.DEFAULT, new Location(3293, 3186, 0)),
    MOS_LE_HARMLESS(CITIES, "Mos Le'Harmless Western Wing", "Location: Mos Le'Harmless Western Wing", UnlockType.DEFAULT, new Location(3683, 2972, 0)),
    CATHERBY(CITIES, "Catherby", "Location: Catherby Shore", UnlockType.DEFAULT, new Location(2803, 3434, 0)),
    SOPHANEM(CITIES, "Sophanem", "Location: Sophanem", UnlockType.DEFAULT, new Location(3304, 2789, 0)),

    CHICKENS(TRAINING, "North of Lumbridge", "Location: North of Lumbridge", UnlockType.DEFAULT, new Location(3238, 3294, 0)),
    GOBLINS(TRAINING, "Lumbridge", "Location: Lumbridge", UnlockType.DEFAULT, new Location(3260, 3228, 0)),
    ROCK_CRABS(TRAINING, "Keldagrim Entrance", "Location: Keldagrim Entrance", UnlockType.DEFAULT, new Location(2710, 3704, 0)),
    AMMONITE_CRABS(TRAINING, "Fossil Island Northern Coast", "Location: Fossil Island Northern Coast", UnlockType.DEFAULT, new Location(3703, 3879, 0)),
    SAND_CRABS(TRAINING, "Great Kourend, Hosidius House", "Location: Great Kourend, Hosidius House", UnlockType.DEFAULT, new Location(1779, 3476, 0)),
    SWAMP_CRABS(TRAINING, "Slepe", "Location: Slepe", UnlockType.DEFAULT, new Location(3743, 3327, 0)),
    TROLLS(TRAINING, "Trollheim", "Location: Trollheim", UnlockType.DEFAULT, new Location(2861, 3591, 0)),
    YAKS(TRAINING, "Neitiznot", "Location: Neitiznot", UnlockType.DEFAULT, new Location(2332, 3803, 0)),
    EXPERIMENTS(TRAINING, "Experiments", "Location: Experiments Cavern", UnlockType.DEFAULT, new Location(3576, 9927,
            0)),
    MONKEY_GUARDS(TRAINING, "Ape Atoll, Marim", "Location: Ape Atoll, Marim", UnlockType.DEFAULT, new Location(2786,
            2786, 0)),
    OGRES(TRAINING, "Combat Training Camp", "Location: Combat Training Camp", UnlockType.DEFAULT, new Location(2518,
            3366, 0)),
    ICE_TROLLS(TRAINING, "Fremennik Isles", "Location: Fremennik Isles", UnlockType.DEFAULT, new Location(2346, 3832,
            0)),
    Lobstrosity_Pen(TRAINING, "Lobstrosity Pen", "Location: Underwater", UnlockType.DEFAULT, new Location(3833, 10247,
            1)),
    SLAYER_TOWER(TRAINING, "Slayer Tower", "Location: Slayer Tower", UnlockType.DEFAULT, new Location(3428, 3532, 0)),
    BANDIT_CAMP(TRAINING, "Bandit camp", "Location: Desert Bandit Camp", UnlockType.DEFAULT, new Location(3174, 3002,
            0)),
    ELF_CAMP(TRAINING, "Elf camp", "Location: Elf camp", UnlockType.DEFAULT, new Location(2203, 3253, 0)),
    BASILISKS(TRAINING, "Basilisks", "Location: Basement of Legend's Guild", UnlockType.DEFAULT, new Location(2730, 9773, 0)),


    DUEL_ARENA(MINIGAMES, "Duel Arena Lobby", "Location: Duel Arena Lobby", UnlockType.DEFAULT, new Location(3367,
            3267, 0)),
    FIGHT_CAVES(MINIGAMES, "Fight Caves", "Location: Fight Caves", UnlockType.DEFAULT, new Location(2444, 5170, 0)),
    PEST_CONTROL(MINIGAMES, "Void Knights' Outpost", "Location: Void Knights' Outpost", UnlockType.DEFAULT,
            new Location(2658, 2672, 0)),
    BARROWS(MINIGAMES, "Barrows", "Location: Barrows", UnlockType.SCROLL, new Location(3565, 3306, 0)),
    CLAN_WARS(MINIGAMES, "Clan Wars", "Location: Clan Wars", UnlockType.DEFAULT, new Location(3370, 3162, 0)),
    WARRIORS_GUILD(MINIGAMES, "Warriors' Guild", "Location: Warriors' Guild entrance", UnlockType.DEFAULT,
            new Location(2880, 3546, 0)),
    CHAMBERS_OF_XERIC(MINIGAMES, "Chambers of Xeric", "Location: Mount Quidamortem", UnlockType.DEFAULT,
            new Location(1255, 3559, 0)),
    TITHE_FARM(MINIGAMES, "Tithe Farm", "Location: Hosidius House, Zeah", UnlockType.DEFAULT, new Location(1791, 3591, 0)),
    THEATRE_OF_BLOOD(MINIGAMES, "Theatre of Blood", "Location: Morytania", UnlockType.DEFAULT,
            new Location(3657, 3219, 0)),
    INFERNO(MINIGAMES, "Inferno", "Location: Mor Ul Rek", UnlockType.SCROLL, new Location(2496, 5102, 0)),

    GIANT_MOLE(BOSSES, "Falador Park Underground", "Location: Falador Park Underground", UnlockType.DEFAULT,
            new Location(1752, 5235, 0)),
    HIGH_REVENANTS(WILDERNESS, "Revenants", "Location: Revenants, level 39 entrance.", UnlockType.DEFAULT, new Location(3129, 3831, 0)),
    LOW_REVENANTS(WILDERNESS, "Revenants", "Location: Revenants, level 17 entrance.", UnlockType.DEFAULT, new Location(3076, 3650, 0)),
    KALPHITE_QUEEN(BOSSES, "Desert, Kalphite Lair Entrance", "Location: Desert, Kalphite Lair Entrance",
            UnlockType.DEFAULT, new Location(3230, 3109, 0)),
    GODWARS(BOSSES, "Godwars Dungeon", "Location: Godwars Dungeon", UnlockType.SCROLL, new Location(2912, 3747, 0)),
    CORPOREAL_BEAST(BOSSES, "Corporeal Beast Dungeon", "Location: Corporeal Beast Dungeon", UnlockType.DEFAULT, new Location(2967, 4383, 2)),
    ZULRAH(BOSSES, "Zul-Andra", "Locaton: Zul-Andra", UnlockType.SCROLL, new Location(2200, 3055, 0)),
    VORKATH(BOSSES, "Ungael", "Location: Ungael", UnlockType.DEFAULT, new Location(2277, 4036, 0)),
    KRAKEN(BOSSES, "South-West of Piscatoris Fishing C.", "Location: South-West of Piscatoris Fishing " + "Colony", UnlockType.SCROLL, new Location(2282,
            3614, 0)),
    THERMONUCLEAR_SMOKE_DEVIL(BOSSES, "South of Castle-Wars", "Location: South of Castle-Wars", UnlockType.DEFAULT, new Location(2411, 3055, 0)),
    CERBERUS(BOSSES, "Taverley Dungeon, Keymaster", "Location: Taverley Dungeon, Keymaster", UnlockType.SCROLL, new Location(1310, 1249, 0)),
    DAGANNOTH_KINGS(BOSSES, "Waterbirth island", "Location: Depths of Waterbirth Island", UnlockType.SCROLL, new Location(1913, 4368, 0)),


    CORSAIR_CAVE(DUNGEONS, "Corsair Cove Dungeon", "Location: Corsair Cove Dungeon", UnlockType.DEFAULT, new Location(1933, 9009, 1)),
    ASGARNIAN_ICE_CAVES(DUNGEONS, "Asgarnian Ice Caves", "Location: Asgarnian Ice Caves", UnlockType.DEFAULT, new Location(3009, 9549, 0)),
    TAVERLEY_DUNGEON(DUNGEONS, "Taverley Dungeon", "Location: Taverley Dungeon", UnlockType.DEFAULT, new Location(2884, 9799, 0)),
    ANCIENT_CAVERN(DUNGEONS, "Ancient Cavern", "Location: Ancient Cavern", UnlockType.DEFAULT, new Location(1764, 5365, 1)),
    KARAMJA_UNDERGROUND(DUNGEONS, "Karamja Underground", "Location: Karamja Underground", UnlockType.DEFAULT, new Location(2861, 9571, 0)),
    BRIMHAVEN_DUNGEON(DUNGEONS, "Brimhaven Dungeon", "Location: Brimhaven Dungeon", UnlockType.DEFAULT, new Location(2708, 9564, 0)),
    SMOKE_DUNGEON(DUNGEONS, "Smoke Dungeon", "Location: Smoke Dungeon", UnlockType.DEFAULT, new Location(3207, 9378, 0)),
    APE_ATOLL_DUNGEON(DUNGEONS, "Ape Atoll Dungeon", "Location: Ape Atoll Dungeon", UnlockType.DEFAULT, new Location(2766, 9103, 0)),
    CHASM_OF_FIRE(DUNGEONS, "Chasm of Fire", "Location: Chasm of Fire", UnlockType.DEFAULT, new Location(1435, 10079, 3)),
    FREMENNIK_SLAYER_DUNGEON(DUNGEONS, "Fremennik Slayer Dungeon", "Location: Fremennik Slayer Dungeon", UnlockType.DEFAULT, new Location(2807, 10002, 0)),
    STRONGHOLD_SLAYER_CAVE(DUNGEONS, "Gnome Stronghold", "Location: Gnome Stronghold", UnlockType.DEFAULT, new Location(2427, 9824, 0)),
    CRASH_SITE_CAVERN(DUNGEONS, "Crash Site Cavern", "Location: Gnome Stronghold", UnlockType.DEFAULT, new Location(2126, 5646, 0)),
    WYVERN_CAVE(DUNGEONS, "Fossil Island Underground", "Location: Fossil Island Underground", UnlockType.DEFAULT, new Location(3604, 10230, 0)),
    CRABCLAW_CAVES(DUNGEONS, "Kourend Underground", "Location: Kourend Underground", UnlockType.DEFAULT, new Location(1647, 9847, 0)),
    KOUREND_CATACOMBS(DUNGEONS, "Kourend Catacombs", "Location: Kourend Catacombs", UnlockType.DEFAULT, new Location(1666, 10048, 0)),
    LITHKREN_VAULT(DUNGEONS, "Lithkren Vault", "Location: Lithkren Vault", UnlockType.DEFAULT, new Location(1568, 5063, 0)),
    MOURNER_TUNNELS(DUNGEONS, "Mourner Tunnels", "Location: Mourner Tunnels", UnlockType.DEFAULT, new Location(2032, 4636, 0)),
    DORGESH_KAAN_DUNGEON(DUNGEONS, "Dorgesh-Kaan Dungeon", "Location: Dorgesh-Kaan Dungeon", UnlockType.DEFAULT, new Location(2715, 5240, 0)),
    BRINE_RAT_CAVERN(DUNGEONS, "Brine Rat Cavern", "Location: Brine Rat Cavern", UnlockType.DEFAULT, new Location(2693, 10123, 0)),
    OBSERVATORY_DUNGEON(DUNGEONS, "Observatory Dungeon", "Location: Observatory Dungeon", UnlockType.DEFAULT, new Location(2335, 9350, 0)),
    WATERFALL_DUNGEON(DUNGEONS, "Waterfall Dungeon", "Location: Waterfall Dungeon", UnlockType.DEFAULT, new Location(2575, 9861, 0)),
    EDGEVILLE_DUNGEON(DUNGEONS, "Edgeville Dungeon", "Location: Edgeville Dungeon Center", UnlockType.DEFAULT, new Location(3132, 9912, 0)),
    EVIL_CHICKEN_LAIR(DUNGEONS, "Evil Chicken Lair", "Location: Evil Chicken Lair", UnlockType.DEFAULT, new Location(2461, 4356, 0)),

    MAGE_BANK(WILDERNESS, "Mage Arena Bank", "Location: Mage Arena Bank", UnlockType.DEFAULT, new Location(2539, 4716, 0)),
    FORINTHRY_DUNGEON(WILDERNESS, "Forinthry Dungeon", "Location: Forinthry Dungeon, level 17 wilderness.", UnlockType.DEFAULT, new Location(3068, 3652, 0)),
    WESTERN_DRAGONS(WILDERNESS, "Western Dragons", "Location: Western dragons, level 10 wilderness.", UnlockType.DEFAULT, new Location(2979, 3595, 0)),
    EASTERN_DRAGONS(WILDERNESS, "Eastern Dragons", "Location: Eastern dragons, level 19 wilderness.", UnlockType.DEFAULT, new Location(3346, 3666, 0)),
    CALLISTO(WILDERNESS, "Callisto in the Wilderness", "Location: 43 Wilderness ", UnlockType.SCROLL, new Location(3326, 3863, 0)),
    VENENATIS(WILDERNESS, "Venenatis in the Wilderness", "Location: 25 Wilderness ", UnlockType.SCROLL, new Location(3314, 3719, 0)),
    KING_BLACK_DRAGON(WILDERNESS, "King Black Dragon Lair in the Wilderness", "Location: 40 Wilderness ", UnlockType.SCROLL, new Location(3012, 3811, 0)),
    VETION(WILDERNESS, "Vetion in the Wilderness", "Location: 29 Wilderness ", UnlockType.SCROLL, new Location(3260, 3751, 0)),
    CHAOS_ALTAR(WILDERNESS, "Chaos Altar in the Wilderness ", "Location: 38 Wilderness ", UnlockType.SCROLL, new Location(2952, 3821, 0)),
    RESOURCE_AREA(WILDERNESS, "Resource Area in the Wilderness ", "Location: 38 Wilderness ", UnlockType.SCROLL, new Location(3184, 3945, 0)),


    COOKS_GUILD(SKILLING, "Cooks' Guild Entrance", "Location: Cooks' Guild Entrance", UnlockType.DEFAULT, new Location(3143, 3442, 0)),
    CRAFTING_GUILD(SKILLING, "Crafting Guild Entrance", "Location: Crafting Guild Entrance", UnlockType.DEFAULT, new Location(2933, 3290, 0)),
    FISHING_GUILD(SKILLING, "Fishing Guild Entrance", "Location: Fishing Guild Entrance", UnlockType.DEFAULT, new Location(2611, 3392, 0)),
    WOODCUTTING_GUILD(SKILLING, "Woodcutting Guild Entrance", "Location: Woodcutting Guild Entrance", UnlockType.DEFAULT, new Location(1659, 3505, 0)),
    PISCATORIS_FISHING_COLONY(SKILLING, "Piscatoris Fishing Colony", "Location: Piscatoris Fishing Colony", UnlockType.DEFAULT, new Location(2344, 3650, 0)),
    TREE_GNOME_STRONGHOLD(SKILLING, "Tree Gnome Stronghold", "Location: Tree Gnome Stronghold", UnlockType.DEFAULT, new Location(2461, 3382, 0)),
    FELDIP_HILLS(SKILLING, "Feldip Hills", "Location: Feldip Hills", UnlockType.DEFAULT, new Location(2541, 2926, 0)),
    PURE_ESSENCE_MINE(SKILLING, "Pure Essence Mine", "Location: Pure Essence Mine", UnlockType.DEFAULT, new Location(2910, 4833, 0)),
    FARMING_GUILD(SKILLING, "Farming Guild", "Location: Farming Guild", UnlockType.DEFAULT, new Location(1249, 3719, 0)),
    HARMONY_ISLAND(SKILLING, "Harmony Island", "Location: Harmony Island", UnlockType.DEFAULT, new Location(3800, 2829, 0)),
    SEAWEED_PATCH(SKILLING, "Underwater Seaweed Patch", "Location: Underwater", UnlockType.DEFAULT, new Location(3731, 10279, 1)),
    PORT_PISCARILIUS(SKILLING, "Port Piscarilius", "Port Piscarilius Northern Dock", UnlockType.DEFAULT, new Location(1825, 3777, 0)),
    MINING_GUILD(SKILLING, "Mining Guild", "Mining Guild", UnlockType.DEFAULT, new Location(3048, 9763, 0)),
    CRYSTAL_MINE(SKILLING, "Crystal Mines", "Crystal Mines", UnlockType.DEFAULT, new Location(2305, 3280, 0)),

    BRAINDEATH_ISLAND(MISC, "Braindeath Island", "Location: Braindeath Island Distillery", UnlockType.DEFAULT, new Location(2149, 5097, 0)),
    LUNAR_ISLE(MISC, "Lunar Isle", "Location: Lunar Isle", UnlockType.DEFAULT, new Location(2105, 3914, 0)),
    BARBARIAN_OUTPOST(MISC, "Barbarian Outpost", "Location: Barbarian Outpost", UnlockType.DEFAULT, new Location(2548, 3569, 0)),
    WATERBIRTH_ISLAND(MISC, "Waterbirth Island", "Location: Waterbirth Island", UnlockType.DEFAULT, new Location(2528, 3740, 0)),
    CRANDOR(MISC, "Crandor Island", "Location: Crandor Island", UnlockType.DEFAULT, new Location(2834, 3259, 0)),
    ISAFDAR(MISC, "Isafdar", "Location: Isafdar, Ilfeen", UnlockType.DEFAULT, new Location(2223, 3211, 0)),
    GAMBLING(MISC, "Gambling", "Location: Castle-Wars", UnlockType.DEFAULT, new Location(2441, 3090, 0)) {
        @Override
        public void onArrival(final Player player) {
            player.getDialogueManager().start(new PlainChat(player, DiceItem.GAMBLE_WARNING));
        }
    },
    WATSON(MISC, "Watson's house", "Location: Watson's house, Great Kourend", UnlockType.DEFAULT, new Location(1636, 3577, 0)),

    BLAST_FURNACE(MINIGAMES, "Blast Furnace", "Location: Keldagrim", UnlockType.DEFAULT, new Location(2931, 10196, 0)),
    WINTERTODT(BOSSES, "Wintertodt Camp", "Location: Northern Tundras of Great Kourend", UnlockType.DEFAULT, new Location(1624, 3929, 0)),
    TYRAS_CAMP(MISC, "Tyras Camp", "Location: Tyras Camp", UnlockType.DEFAULT, new Location(2186, 3147, 0)),


    ;



    public static final PortalTeleport[] values = values();
    private static final EnumMap<TeleportCategory, List<PortalTeleport>> teleports = new EnumMap<>(TeleportCategory.class);

    static {
        for (val teleport : values) {
            teleports.computeIfAbsent(teleport.getCategory(), f -> new ArrayList<>()).add(teleport);
        }
    }

    private final TeleportCategory category;
    private final String smallDescription, largeDescription;
    private final UnlockType unlockType;
    private final Location location;

    PortalTeleport(final TeleportCategory category, final String smallDescription, final String largeDescription, final UnlockType unlockType, final Location location) {
        this.category = category;
        this.smallDescription = smallDescription;
        this.largeDescription = largeDescription + "<br>Unlocked by: " + unlockType.formatted;
        this.unlockType = unlockType;
        this.location = location;
    }

    public static List<PortalTeleport> get(final TeleportCategory category) {
        return teleports.get(category);
    }

    @Override
    public String toString() {
        return TextUtils.capitalize(name().toLowerCase().replace('_', ' '));
    }

    @Override
    public TeleportType getType() {
        return TeleportType.ZENYTE_PORTAL_TELEPORT;
    }

    @Override
    public Location getDestination() {
        return location;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public double getExperience() {
        return 0;
    }

    @Override
    public int getRandomizationDistance() {
        return 2;
    }

    @Override
    public Item[] getRunes() {
        return null;
    }

    @Override
    public int getWildernessLevel() {
        return WILDERNESS_LEVEL;
    }

    @Override
    public boolean isCombatRestricted() {
        return false;
    }

}
