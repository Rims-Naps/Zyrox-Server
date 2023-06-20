package com.zenyte.game.world.entity.player.collectionlog;

import com.zenyte.game.content.treasuretrails.ClueLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.function.Function;

/**
 * @author Kris | 24/03/2019 13:51
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
public enum CollectionLogCategory {
    ABYSSAL_SIRE(player -> get(player, "Abyssal Sire")),
    ALCHEMICAL_HYDRA(player -> get(player, "Alchemical Hydra")),
    BARROWS_CHESTS(player -> get(player, "Barrows")),
    BRYOPHYTA(player -> get(player, "Bryophyta")),
    CALLISTO(player -> get(player, "Callisto")),
    CERBERUS(player -> get(player, "Cerberus")),
    CHAOS_ELEMENTAL(player -> get(player, "Chaos Elemental")),
    CHAOS_FANATIC(player -> get(player, "Chaos Fanatic")),
    COMMANDER_ZILYANA(player -> get(player, "Commander Zilyana")),
    CORPOREAL_BEAST(player -> get(player, "Corporeal Beast")),
    CRAZY_ARCHAEOLOGIST(player -> get(player, "Crazy Archaeologist")),
    DAGANNOTH_KINGS(player -> get(player, "Dagannoth Rex") + get(player, "Dagannoth Supreme") + get(player, "Dagannoth Prime")),
    THE_FIGHT_CAVES(player -> get(player, "TzTok-Jad")),
    GENERAL_GRAARDOR(player -> get(player, "General Graardor")),
    GIANT_MOLE(player -> get(player, "Giant Mole")),
    GROTESQUE_GUARDIANS(player -> get(player, "Grotesque Guardians")),
    HESPORI(player -> get(player, "Hespori")),
    THE_INFERNO(player -> get(player, "TzKal-Zuk")),
    KALPHITE_QUEEN(player -> get(player, "Kalphite Queen")),
    KING_BLACK_DRAGON(player -> get(player, "King Black Dragon")),
    KRAKEN(player -> get(player, "Kraken")),
    KREEARRA(player -> get(player, "Kree'Arra")),
    KRIL_TSUTSAROTH(player -> get(player, "K'ril Tsutsaroth")),
    OBOR(player -> get(player, "Obor")),
    SCORPIA(player -> get(player, "Scorpia")),
    SKOTIZO(player -> get(player, "Skotizo")),
    THERMONUCLEAR_SMOKE_DEVIL(player -> get(player, "Thermonuclear smoke devil")),
    VENENATIS(player -> get(player, "Venenatis")),
    VETION(player -> get(player, "Vet'ion")),
    VORKATH(player -> get(player, "Vorkath")),
    WINTERTODT(player -> get(player, "Wintertodt")),
    ZULRAH(player -> get(player, "Zulrah")),
    CHAMBERS_OF_XERIC(player -> player.getNumericAttribute("chambersofxeric").intValue() + player.getNumericAttribute("challengechambersofxeric").intValue()),
    THEATRE_OF_BLOOD(player -> player.getNumericAttribute("theatreofblood").intValue()),
    BEGINNER_TREASURE_TRAILS(player -> player.getNumericAttribute("completed beginner treasure trails").intValue()),
    EASY_TREASURE_TRAILS(player -> player.getNumericAttribute("completed easy treasure trails").intValue()),
    MEDIUM_TREASURE_TRAILS(player -> player.getNumericAttribute("completed medium treasure trails").intValue()),
    HARD_TREASURE_TRAILS(player -> player.getNumericAttribute("completed hard treasure trails").intValue()),
    ELITE_TREASURE_TRAILS(player -> player.getNumericAttribute("completed elite treasure trails").intValue()),
    MASTER_TREASURE_TRAILS(player -> player.getNumericAttribute("completed master treasure trails").intValue()),
    SHARED_TREASURE_TRAIL_REWARDS(player -> {
        int count = 0;
        for (val tier : ClueLevel.values()) {
            count += player.getNumericAttribute("completed " + tier.toString().toLowerCase() + " treasure trails").intValue();
        }
        return count;
    }),
    BARBARIAN_ASSAULT(player -> 0),
    CASTLE_WARS(null),
    FISHING_TRAWLER(null),
    GNOME_RESTAURANT(null),
    MAGIC_TRAINING_ARENA(null),
    PEST_CONTROL(null),
    ROGUES_DEN(null),
    SHADES_OF_MORTTON(null),
    TEMPLE_TREKKING(null),
    TITHE_FARM(null),
    TROUBLE_BREWING(null),
    AERIAL_FISHING(null),
    ALL_PETS(null),
    CHAMPIONS_CHALLENGE(null),
    CHAOS_DRUIDS(null),
    CHOMPY_BIRD_HUNTING(null),
    CREATURE_CREATION(null),
    CYCLOPES(null),
    FOSSIL_ISLAND_NOTES(null),
    GLOUGHS_EXPERIMENTS(player -> get(player, "Demonic gorilla") + get(player, "Tortured gorilla")),
    MOTHERLODE_MINE(null),
    MY_NOTES(null),
    RANDOM_EVENTS(null),
    REVENANTS(player -> get(player, "Revenant")),
    ROOFTOP_AGILITY(null),
    SHAYZIEN_ARMOUR(null),
    SKILLING_PETS(null),
    SLAYER(null),
    TZHAAR(null),
    MISCELLANEOUS(null);

    //TODO ffs.
    final Function<Player, Integer> function;

    static final CollectionLogCategory[] values = values();

    static final Function<Player, Integer> getFunction(final String title) {
        val filtered = title.toUpperCase().replaceAll("'", "").replaceAll(" ", "_");
        return valueOf(filtered).function;
    }

    private static final int get(final Player player, final String name) {
        return player.getNotificationSettings().getKillcount(name);
    }
}
