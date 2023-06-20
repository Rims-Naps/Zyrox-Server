package com.zenyte.game.content.follower.impl;

import com.zenyte.Constants;
import com.zenyte.game.content.follower.Follower;
import com.zenyte.game.content.follower.Pet;
import com.zenyte.game.content.follower.PetWrapper;
import com.zenyte.game.content.minigame.wintertodt.RewardCrate;
import com.zenyte.game.content.skills.slayer.RegularTask;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.followers.*;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Tommeh | 23-11-2018 | 18:04
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public enum BossPet implements Pet {

    KALPHITE_PRINCESS_FIRST(12647, 6638, 3000, KalphitePrincessD.class, 963, 965),
    KALPHITE_PRINCESS_SECOND(12654, 6637, 3000, KalphitePrincessD.class),
    PET_CHAOS_ELEMENTAL(11995, 2055, 300, PetChaosElementalD.class, 2054),
    PET_DAGANNOTH_PRIME(12644, 6629, 5000, PetDagannothPrimeD.class, 2266),
    PET_DAGANNOTH_REX(12645, 6630, 5000, PetDagannothRexD.class, 2267),
    PET_DAGANNOTH_SUPREME(12643, 6628, 5000, PetDagannothSupremeD.class, 2265),
    PET_DARK_CORE(12816, 318, 5000, PetDarkCoreD.class, 319),

    PET_GENERAL_GRAARDOR(12650, 6632, 5000, PetGeneralGraardorD.class, 2215),
    PET_KRIL_TSUTSAROTH(12652, 6634, 5000, PetKrilTsutsarothD.class, 3129),
    PET_KREE_ARRA(12649, 6631, 5000, PetKreeArraD.class, 3162),
    PET_ZILYANA(12651, 6633, 5000, PetZilyanaD.class, 2205),
    PET_KRAKEN(12655, 6640, 3000, PetKrakenD.class, 494),
    PET_SMOKE_DEVIL(12648, 6639, 3000, PetSmokeDevilD.class, 499),
    PET_SMOKE_DEVIL_REGULAR(22663, 8483, 3000, PetSmokeDevilD.class),

    GREEN_PET_SNAKELING(12921, 2130, 4000, PetSnakelingD.class, 2042, 2043, 2044),
    RED_PET_SNAKELING(12939, 2131, 4000, PetSnakelingD.class),
    TURQOISE_PET_SNAKELING(12940, 2132, 4000, PetSnakelingD.class),
    PRINCE_BLACK_DRAGON(12653, 6636, 3000, PrinceBlackDragonD.class, 239),
    SCORPIAS_OFFSPRING(13181, 5561, 2000, ScorpiasOffspringD.class, 6615),
    VENENATIS_OFFSPRING(13177, 5557, 2000, VenenatisSpiderlingD.class, 6610),
    CALLISTO_CUB(13178, 5558, 2000, CallistoCubD.class, 6609),
    PURPLE_VETION(13179, 5559, 2000, VetionD.class, 6611, 6612),
    ORANGE_VETION(13180, 5560, 2000, VetionD.class),
    VORKI(21992, 8029, 2000, VorkiD.class, 8061),
    HELLPUPPY(13247, 3099, 3000, HellpuppyD.class, 5862),
    BABY_MOLE(12646, 6635, 3000, BabyMoleD.class, 5779, 6499),
    BABY_MOLE_RAT(32365, 14052, 1, BabyMoleD.class),
    NOON(21748, 7892, 3000, NoonD.class, 7888),
    MIDNIGHT(21750, 7893, 3000, MidnightD.class),

    PET_PENANCE_QUEEN(12703, 6674, 1000, PetPenanceQueenD.class),
    ABYSSAL_ORPHAN(13262, 5884, 2560, AbyssalOrphanD.class),
    TZREK_JAD(13225, 5893, 200, TzRekJadD.class),
    JALREK_JAD(32363, 14037, 1 ,TzRekJadD.class),
    SKOTOS(21273, 7671, 50, SkotosD.class, 7286),

    OLMLET(20851, 7520, 65, OlmletD.class),
    JAL_NIB_REK(21291, 7675, 100, JalNibRekD.class),

    PET_CORPOREAL_CRITTER(22318, 8010, 5000, PetDarkCoreD.class, 319),

    PUPPADILE(22376, 8201, 1, PuppadileD.class),
    TEKTINY(22378, 8202, 1, TektinyD.class),
    ENRAGED_TEKTINY(32269, 14038, 1, TektinyD.class),
    VANGUARD(22380, 8203, 1, VanguardD.class),
    VASA_MINIRIO(22382, 8204, 1, VasaMinirioD.class),
    VESPINA(22384, 8205, 1, VespinaD.class),
    FLYING_VESPINA(32267, 14036, 1, VespinaD.class),
    LIL_ZIK(22473, 8337),
    IKKLE_HYDRA_GREEN(22746, 8492, 3000, IkkleHydraD.class, 8621),
    IKKLE_HYDRA_BLUE(22748, 8493, 3000, IkkleHydraD.class),
    IKKLE_HYDRA_RED(22750, 8494, 3000, IkkleHydraD.class),
    IKKLE_HYDRA_LAST(22752, 8495, 3000, IkkleHydraD.class),
    TZREK_ZUK(22319, 8011, 1, TzRekZukD.class),


    CRAWLING_HAND(4133, 16000, 3000, SlayerMonsterD.class, 448, 449, 450, 451, 452, 453, 454, 455, 456, 457),
    CAVE_BUG(4521, 16001, 3000, SlayerMonsterD.class, 481, 483),
    CAVE_CRAWLER(4134, 16002, 3000, SlayerMonsterD.class, 406, 407, 408, 409),
    BANSHEE(4135, 16003, 3000, SlayerMonsterD.class, 414, 7272, 7390, 7391),
    CAVE_SLIM(4520, 16004, 3000, SlayerMonsterD.class, 480),
    ROCKSLUG(4136, 16005, 3000, SlayerMonsterD.class, 421, 422, 7392),
    DESERT_LIZARDS(6695, 16006, 3000, SlayerMonsterD.class, 459, 460, 461),
    COCKATRICE(4137, 16007, 3000, SlayerMonsterD.class, 419, 420, 7393),
    PYREFIENDS(4138, 16008, 3000, SlayerMonsterD.class, 433, 434, 435, 436, 3139),
    MOGRES(6661, 16009, 3000, SlayerMonsterD.class, 2592),
    HARPIE_BUG_SWARM(7050, 16010, 3000, SlayerMonsterD.class, 464),
    KILLER_WATTS(7160, 16011, 3000, SlayerMonsterD.class, 469, 470),
    MOLANISKS(10997, 16012, 3000, SlayerMonsterD.class, 1),
    BASILISK(4139, 16013, 3000, SlayerMonsterD.class, 417, 418),
    SEA_SNAKE(5089, 16014, 3000, SlayerMonsterD.class, 1097, 1098, 1101),
    TERROR_DOG(10591, 16015, 3000, SlayerMonsterD.class, 6473, 6474),
    FEVER_SPIDERS(6709, 16016, 3000, SlayerMonsterD.class, 626),
    SULPHUR_LIZARDS(23043, 16017, 3000, SlayerMonsterD.class, 8614),
    INFERNAL_MAGES(4140, 16018, 3000, SlayerMonsterD.class, 443, 444, 445, 446, 447),
    BRINE_RAT(11047, 16019, 3000, SlayerMonsterD.class, 4501),
    BLOODVELD(4141, 16020, 3000, SlayerMonsterD.class, 484, 451, 486, 487, 3138, 7276, 7397, 7398),
    JELLY(4142, 16021, 3000, SlayerMonsterD.class, 437, 438, 439, 440, 441, 442, 7277, 739, 7400),
    TUROTH(4143, 16022, 3000, SlayerMonsterD.class, 426, 427, 428, 430, 431, 432),
    ZYGOMITE(7420, 16023, 3000, SlayerMonsterD.class, 473, 474, 7797),
    CAVE_HORROR(8900, 16024, 3000, SlayerMonsterD.class, 3209, 3210, 3211, 3212, 3213),
    ABERRANT_SPECTRE(4144, 16025, 3000, SlayerMonsterD.class, 2, 3, 4, 5, 6, 7, 7279, 7402, 7403),
    DUST_DEVIL(4145, 16026, 3000, SlayerMonsterD.class, 423, 7249),
    PET_TALONED_WYVERN(21507, 16027, 3000, SlayerMonsterD.class, 7793),
    KURASK(4146, 16029, 3000, SlayerMonsterD.class, 410, 411, 7405),
    GARGOYLE(4147, 16033, 3000, SlayerMonsterD.class, 412, 413, 1543, 7407, 7408),
    SKELETAL_WYVERN(6811, 16032, 3000, SlayerMonsterD.class, 465, 466, 467, 468),
    BRUTUAL_BLACK_DRAGON(21391, 16034, 3000, SlayerMonsterD.class, 7275, 8092, 8093),
    PET_NECHRYAEL(4148, 16035, 3000, SlayerMonsterD.class, 8, 11, 7278, 7411),
    PET_DRAKE(23041, 16037, 3000, SlayerMonsterD.class, 8612, 8613),
    PET_DARK_BEAST(6637, 16038, 3000, SlayerMonsterD.class, 4005, 7250),
    PET_ANCIENT_WYVERN(21508, 16036, 3000, SlayerMonsterD.class, 7795),

    PHOENIX(20693, 7370, RewardCrate.PHOENIX_PET_CHANCE, PhoenixD.class),
    BLUE_PHOENIX(32289, 14047, RewardCrate.PHOENIX_PET_CHANCE, PhoenixD.class),
    PURPLE_PHOENIX(32291, 14048, RewardCrate.PHOENIX_PET_CHANCE, PhoenixD.class),
    GREEN_PHOENIX(32293, 14049, RewardCrate.PHOENIX_PET_CHANCE, PhoenixD.class),
    WHITE_PHOENIX(32295, 14050, RewardCrate.PHOENIX_PET_CHANCE, PhoenixD.class);

    public static final Int2IntMap metamorphosisMap = new Int2IntOpenHashMap();

    public static final BossPet[] snakelings = {
            GREEN_PET_SNAKELING, RED_PET_SNAKELING,
            TURQOISE_PET_SNAKELING
    };

    public static final BossPet[] kalphiteQueens = {
            KALPHITE_PRINCESS_FIRST, KALPHITE_PRINCESS_SECOND
    };

    public static final BossPet[] vetions = {
            ORANGE_VETION, PURPLE_VETION
    };

    public static final BossPet[] grotesqueGuardians = {
            NOON, MIDNIGHT
    };

    public static final BossPet[] hydras = {
            IKKLE_HYDRA_GREEN, IKKLE_HYDRA_BLUE,
            IKKLE_HYDRA_RED, IKKLE_HYDRA_LAST
    };
    public static final BossPet[] infernoPets = {
            JAL_NIB_REK, TZREK_ZUK
    };


    static {
        metamorphosisMap.put(6635, 14052);
        metamorphosisMap.put(14052, 6635);
        metamorphosisMap.put(5893, 14037);
        metamorphosisMap.put(14037, 5893);
        metamorphosisMap.put(6639, 8483);
        metamorphosisMap.put(8483, 6639);
        metamorphosisMap.put(6638, 6637);
        metamorphosisMap.put(6637, 6638);
        metamorphosisMap.put(2130, 2131);
        metamorphosisMap.put(2131, 2132);
        metamorphosisMap.put(2132, 2130);
        metamorphosisMap.put(5559, 5560);
        metamorphosisMap.put(5560, 5559);
        metamorphosisMap.put(7893, 7892);
        metamorphosisMap.put(7892, 7893);
        metamorphosisMap.put(7675, 8011);
        metamorphosisMap.put(8011, 7675);
        metamorphosisMap.put(8492, 8493);
        metamorphosisMap.put(8493, 8494);
        metamorphosisMap.put(8494, 8495);
        metamorphosisMap.put(8495, 8492);
        metamorphosisMap.put(8010, 318);
        metamorphosisMap.put(318, 8010);

        //Chambers of Xeric
        metamorphosisMap.put(7520, 8201);
        metamorphosisMap.put(8201, 8202);
        metamorphosisMap.put(8202, 14038);
        metamorphosisMap.put(14038, 8203);
        metamorphosisMap.put(8203, 8204);
        metamorphosisMap.put(8204, 8205);
        metamorphosisMap.put(8205, 14036);
        metamorphosisMap.put(14036, 7520);

        //Chameleons
        metamorphosisMap.put(15158, 15159);
        metamorphosisMap.put(15159, 15160);
        metamorphosisMap.put(15160, 15161);
        metamorphosisMap.put(15161, 15162);
        metamorphosisMap.put(15162, 15163);
        metamorphosisMap.put(15163, 15164);
        metamorphosisMap.put(15164, 15165);
        metamorphosisMap.put(15165, 15166);
        metamorphosisMap.put(15166, 15167);
        metamorphosisMap.put(15167, 15158);
    }

    BossPet(final int itemId, final int petId) {
        this(itemId, petId, -1,null, -1);
    }

    BossPet(final int itemId, final int petId, final int rarity, final Class<? extends Dialogue> dialogue) {
        this(itemId, petId, rarity, dialogue, -1);
    }

    BossPet(final int itemId, final int petId, final int rarity, final Class<? extends Dialogue> dialogue, final int... bossIds) {
        this.itemId = itemId;
        this.petId = petId;
        this.rarity = rarity;
        this.dialogue = dialogue;
        this.bossIds = bossIds;
    }

    @Getter private final int itemId, petId;
    private final int rarity;
    @Getter private final Class<? extends Dialogue> dialogue;
    @Getter private final int[] bossIds;

    public static final BossPet[] VALUES = values();
    public static final Int2ObjectOpenHashMap<BossPet> BOSS_PETS_BY_NPC_ID = new Int2ObjectOpenHashMap<>(VALUES.length);
    public static final Int2ObjectOpenHashMap<BossPet> BOSS_PETS_BY_BOSS_NPC_ID = new Int2ObjectOpenHashMap<>(VALUES.length);
    public static final Int2ObjectOpenHashMap<BossPet> BOSS_PETS_BY_PET_NPC_ID = new Int2ObjectOpenHashMap<>(VALUES.length);
    public static final Int2ObjectOpenHashMap<BossPet> BOSS_PETS_BY_ITEM_ID = new Int2ObjectOpenHashMap<>(VALUES.length);

    public int getRarity(final Player player, final int bossId) {
        int finalRarity = rarity;

        // boosted drop rate calculation for events; disabled for raids
        if(Constants.BOOSTED_BOSS_PETS && !this.equals(OLMLET))
            finalRarity = (int) (rarity - (rarity * Constants.BOOSTED_BOSS_PET_RATE));

        if (this.equals(PET_CHAOS_ELEMENTAL)) {
           return bossId == 2054 ? (Constants.BOOSTED_BOSS_PETS ? 255 : 300) : (Constants.BOOSTED_BOSS_PETS ? 850 : 1000);
        } else if (this.equals(TZREK_JAD)) {
            return player != null && (player.getSlayer().getAssignment() != null && player.getSlayer().getAssignment().getTask().equals(RegularTask.TZTOK_JAD)) ? 100 : 200;
        }
        if(petId > 16000 && player != null) {
            if(player.getSlayer().getAssignment() == null) {
                return 3000;
            }
            int[] monsterIds = getBossIds();
            int[] taskIds = player.getSlayer().getAssignment().getTask().getMonsterIds();
            for(int mId : monsterIds) {
                for(int tId : taskIds) {
                    if(mId == tId) {
                        return 2000;
                    }
                }
            }
            return 3000;
        }
        return finalRarity;
    }

    public static BossPet getByNPC(final int bossId) {
        return BOSS_PETS_BY_NPC_ID.get(bossId);
    }

    public static BossPet getByBossNPC(final int bossId) {
        return BOSS_PETS_BY_BOSS_NPC_ID.get(bossId);
    }

    public static BossPet getByItem(final int itemId) {
        return BOSS_PETS_BY_ITEM_ID.get(itemId);
    }

    static {
        for (val pet : VALUES) {
            for (val bossId : pet.getBossIds()) {
                BOSS_PETS_BY_NPC_ID.put(bossId, pet);
                if (bossId == -1) {
                    continue;
                }
                BOSS_PETS_BY_BOSS_NPC_ID.put(bossId, pet);
            }

            BOSS_PETS_BY_PET_NPC_ID.put(pet.petId, pet);
            BOSS_PETS_BY_ITEM_ID.put(pet.itemId, pet);
        }
    }

    @Override
    public int itemId() {
        return itemId;
    }

    @Override
    public int petId() {
        return petId;
    }

    @Override
    public String petName() {
        return name();
    }

    @Override
    public boolean hasPet(Player player) {
        if (isSnakeling(this)) {
            val ids = new int[snakelings.length];
            for (int index = 0; index < snakelings.length; index++) {
                ids[index] = snakelings[index].itemId();
            }
            return (PetWrapper.checkFollower(player) && ArrayUtils.contains(snakelings, player.getFollower().getPet())) || player.containsAny(ids);
        }
        if (isKQ(this)) {
            val ids = new int[kalphiteQueens.length];
            for (int index = 0; index < kalphiteQueens.length; index++) {
                ids[index] = kalphiteQueens[index].itemId();
            }
            return (PetWrapper.checkFollower(player) && ArrayUtils.contains(kalphiteQueens, player.getFollower().getPet())) || player.containsAny(ids);
        }
        if (isVetion(this)) {
            val ids = new int[vetions.length];
            for (int index = 0; index < vetions.length; index++) {
                ids[index] = vetions[index].itemId();
            }
            return (PetWrapper.checkFollower(player) && ArrayUtils.contains(vetions, player.getFollower().getPet())) || player.containsAny(ids);
        }
        if (isGrotesqueGuardian(this)) {
            val ids = new int[grotesqueGuardians.length];
            for (int index = 0; index < grotesqueGuardians.length; index++) {
                ids[index] = grotesqueGuardians[index].itemId();
            }
            return (PetWrapper.checkFollower(player) && ArrayUtils.contains(grotesqueGuardians, player.getFollower().getPet())) || player.containsAny(ids);
        }
        if (this.equals(PET_DARK_CORE) || this.equals(PET_CORPOREAL_CRITTER)) {
            return (PetWrapper.checkFollower(player) && (player.getFollower().getPet().equals(PET_DARK_CORE) || player.getFollower().getPet().equals(PET_CORPOREAL_CRITTER))) || player.containsItem(PET_DARK_CORE.getItemId()) || player.containsItem(PET_CORPOREAL_CRITTER.getItemId());
        }
        if (this.equals(JAL_NIB_REK) || this.equals(TZREK_ZUK)) {
            val ids = new int[infernoPets.length];
            for (int index = 0; index < infernoPets.length; index++) {
                ids[index] = infernoPets[index].itemId();
            }
            return (PetWrapper.checkFollower(player) && ArrayUtils.contains(infernoPets, player.getFollower().getPet())) || player.containsAny(ids);
        }
        return (PetWrapper.checkFollower(player) && player.getFollower().getPet().equals(this)) || player.containsItem(this.getItemId());
    }

    @Override
    public Class<? extends Dialogue> dialogue() {
        return dialogue;
    }

    @Override
    public boolean roll(final Player player, final int rarity) {
        if (rarity == -1 || Utils.random(rarity) != 0) {
            return false;
        }
        val item = new Item(itemId);
        player.getCollectionLog().add(item);
        if (hasPet(player)) {
            player.sendMessage("<col=ff0000>You have a funny feeling like you would have been followed...</col>");
            return false;
        }
        if (player.getFollower() != null)
        {
            if (player.getInventory().addItem(item).isFailure())
            {
                if (player.getBank().add(item).isFailure())
                {
                    player.sendMessage("There was not enough space in your bank, and therefore the pet was lost.");
                    return false;
                } else
                {
                    player.sendMessage("<col=ff0000>You have a funny feeling like you're being followed - The pet has been added to your bank.</col>");
                }
            } else
            {
                player.sendMessage("<col=ff0000>You feel something weird sneaking into your backpack.</col>");
            }

        } else {
            player.sendMessage("<col=ff0000>You have a funny feeling like you're being followed.</col>");
            player.setFollower(new Follower(petId, player));
        }
        WorldBroadcasts.broadcast(player, BroadcastType.PET, this);
        return true;
    }

    public static boolean isKQ(final Pet pet) {
        for (val p : kalphiteQueens) {
            if (p.equals(pet)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSnakeling(final Pet pet) {
        for (val p : snakelings) {
            if (p.equals(pet)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isGrotesqueGuardian(final Pet pet) {
        for (val p : grotesqueGuardians) {
            if (p.equals(pet)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVetion(final Pet pet) {
        for (val p : vetions) {
            if (p.equals(pet)) {
                return true;
            }
        }
        return false;
    }
}
