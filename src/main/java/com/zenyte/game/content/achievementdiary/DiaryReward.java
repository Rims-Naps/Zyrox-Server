package com.zenyte.game.content.achievementdiary;

import com.zenyte.game.content.MaxCape;
import com.zenyte.game.content.achievementdiary.diaries.*;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Lamp;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.utils.Ordinal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.*;

import static com.zenyte.game.content.achievementdiary.Diary.DiaryComplexity.*;
import static com.zenyte.game.content.achievementdiary.DiaryArea.*;
import static com.zenyte.game.world.entity.player.Lamp.*;


/**
 * @author Tommeh | 5-11-2018 | 20:22
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Ordinal
@Getter
@RequiredArgsConstructor
public enum DiaryReward {

    ARDOUGNE_CLOAK1(ARDOUGNE, new Item(13121), EASY_DIARY_LAMP, "cloak", EASY, ArdougneDiary.values()),
    ARDOUGNE_CLOAK2(ARDOUGNE, new Item(13122), MEDIUM_DIARY_LAMP, "cloak", MEDIUM, ArdougneDiary.values()),
    ARDOUGNE_CLOAK3(ARDOUGNE, new Item(13123), HARD_DIARY_LAMP, "cloak", HARD, ArdougneDiary.values()),
    ARDOUGNE_CLOAK4(ARDOUGNE, new Item(13124), ELITE_DIARY_LAMP, "cloak", ELITE, ArdougneDiary.values()),

    DESERT_AMULET1(DESERT, new Item(13133), EASY_DIARY_LAMP, "amulet", EASY, DesertDiary.values()),
    DESERT_AMULET2(DESERT, new Item(13134), MEDIUM_DIARY_LAMP, "amulet", MEDIUM, DesertDiary.values()),
    DESERT_AMULET3(DESERT, new Item(13135), HARD_DIARY_LAMP, "amulet", HARD, DesertDiary.values()),
    DESERT_AMULET4(DESERT, new Item(13136), ELITE_DIARY_LAMP, "amulet", ELITE, DesertDiary.values()),

    FALADOR_SHIELD1(FALADOR, new Item(13117), EASY_DIARY_LAMP, "shield", EASY, FaladorDiary.values()),
    FALADOR_SHIELD2(FALADOR, new Item(13118), MEDIUM_DIARY_LAMP, "shield", MEDIUM, FaladorDiary.values()),
    FALADOR_SHIELD3(FALADOR, new Item(13119), HARD_DIARY_LAMP, "shield", HARD, FaladorDiary.values()),
    FALADOR_SHIELD4(FALADOR, new Item(13120), ELITE_DIARY_LAMP, "shield", ELITE, FaladorDiary.values()),

    FREMENNIK_SEA_BOOTS1(FREMENNIK, new Item(13129), EASY_DIARY_LAMP, "pair of boots", EASY, FremennikDiary.values()),
    FREMENNIK_SEA_BOOTS2(FREMENNIK, new Item(13130), MEDIUM_DIARY_LAMP, "pair of boots", MEDIUM, FremennikDiary.values()),
    FREMENNIK_SEA_BOOTS3(FREMENNIK, new Item(13131), HARD_DIARY_LAMP, "pair of boots", HARD, FremennikDiary.values()),
    FREMENNIK_SEA_BOOTS4(FREMENNIK, new Item(13132), ELITE_DIARY_LAMP, "pair of boots", ELITE, FremennikDiary.values()),

    KANDARIN_HEADGEAR1(KANDARIN, new Item(13137), EASY_DIARY_LAMP, "headgear", EASY, KandarinDiary.values()),
    KANDARIN_HEADGEAR2(KANDARIN, new Item(13138), MEDIUM_DIARY_LAMP, "headgear", MEDIUM, KandarinDiary.values()),
    KANDARIN_HEADGEAR3(KANDARIN, new Item(13139), HARD_DIARY_LAMP, "headgear", HARD, KandarinDiary.values()),
    KANDARIN_HEADGEAR4(KANDARIN, new Item(13140), ELITE_DIARY_LAMP, "headgear", ELITE, KandarinDiary.values()),

    KARAMJA_GLOVES1(KARAMJA, new Item(11136), EASY_DIARY_LAMP, "pair of gloves", EASY, KaramjaDiary.values()),
    KARAMJA_GLOVES2(KARAMJA, new Item(11138), MEDIUM_DIARY_LAMP, "pair of gloves", MEDIUM, KaramjaDiary.values()),
    KARAMJA_GLOVES3(KARAMJA, new Item(11140), HARD_DIARY_LAMP, "pair of gloves", HARD, KaramjaDiary.values()),
    KARAMJA_GLOVES4(KARAMJA, new Item(13103), ELITE_DIARY_LAMP, "pair of gloves", ELITE, KaramjaDiary.values()),

    EXPLORERS_RING1(LUMBRIDGE_AND_DRAYNOR, new Item(13125), EASY_DIARY_LAMP, "ring", EASY, LumbridgeDiary.values()),
    EXPLORERS_RING2(LUMBRIDGE_AND_DRAYNOR, new Item(13126), MEDIUM_DIARY_LAMP, "ring", MEDIUM, LumbridgeDiary.values()),
    EXPLORERS_RING3(LUMBRIDGE_AND_DRAYNOR, new Item(13127), HARD_DIARY_LAMP, "ring", HARD, LumbridgeDiary.values()),
    EXPLORERS_RING4(LUMBRIDGE_AND_DRAYNOR, new Item(13128), ELITE_DIARY_LAMP, "ring", ELITE, LumbridgeDiary.values()),

    MORYTANIA_LEGS1(MORYTANIA, new Item(13112), EASY_DIARY_LAMP, "pair of legs", EASY, MorytaniaDiary.values()),
    MORYTANIA_LEGS2(MORYTANIA, new Item(13113), MEDIUM_DIARY_LAMP, "pair of legs", MEDIUM, MorytaniaDiary.values()),
    MORYTANIA_LEGS3(MORYTANIA, new Item(13114), HARD_DIARY_LAMP, "pair of legs", HARD, MorytaniaDiary.values()),
    MORYTANIA_LEGS4(MORYTANIA, new Item(13115), ELITE_DIARY_LAMP, "pair of legs", ELITE, MorytaniaDiary.values()),

    VARROCK_ARMOUR1(VARROCK, new Item(13104), EASY_DIARY_LAMP, "armour", EASY, VarrockDiary.values()),
    VARROCK_ARMOUR2(VARROCK, new Item(13105), MEDIUM_DIARY_LAMP, "armour", MEDIUM, VarrockDiary.values()),
    VARROCK_ARMOUR3(VARROCK, new Item(13106), HARD_DIARY_LAMP, "armour", HARD, VarrockDiary.values()),
    VARROCK_ARMOUR4(VARROCK, new Item(13107), ELITE_DIARY_LAMP, "armour", ELITE, VarrockDiary.values()),

    WILDERNESS_SWORD1(WILDERNESS, new Item(13108), EASY_DIARY_LAMP, "sword", EASY, WildernessDiary.values()),
    WILDERNESS_SWORD2(WILDERNESS, new Item(13109), MEDIUM_DIARY_LAMP, "sword", MEDIUM, WildernessDiary.values()),
    WILDERNESS_SWORD3(WILDERNESS, new Item(13110), HARD_DIARY_LAMP, "sword", HARD, WildernessDiary.values()),
    WILDERNESS_SWORD4(WILDERNESS, new Item(13111), ELITE_DIARY_LAMP, "sword", ELITE, WildernessDiary.values()),

    WESTERN_BANNER1(WESTERN_PROVINCES, new Item(13141), EASY_DIARY_LAMP, "banner", EASY, WesternProvincesDiary.values()),
    WESTERN_BANNER2(WESTERN_PROVINCES, new Item(13142), MEDIUM_DIARY_LAMP, "banner", MEDIUM, WesternProvincesDiary.values()),
    WESTERN_BANNER3(WESTERN_PROVINCES, new Item(13143), HARD_DIARY_LAMP, "banner", HARD, WesternProvincesDiary.values()),
    WESTERN_BANNER4(WESTERN_PROVINCES, new Item(13144), ELITE_DIARY_LAMP, "banner", ELITE, WesternProvincesDiary.values()),

    RADAS_BLESSING1(KEBOS, new Item(22941), EASY_DIARY_LAMP, "blessing", EASY, KourendDiary.values()),
    RADAS_BLESSING2(KEBOS, new Item(22943), MEDIUM_DIARY_LAMP, "blessing", MEDIUM, KourendDiary.values()),
    RADAS_BLESSING3(KEBOS, new Item(22945), HARD_DIARY_LAMP, "blessing", HARD, KourendDiary.values()),
    RADAS_BLESSING4(KEBOS, new Item(22947), ELITE_DIARY_LAMP, "blessing", ELITE, KourendDiary.values());

    private final DiaryArea area;
    private final Item item;
    private final Lamp lamp;
    private final String itemName;
    private final Diary.DiaryComplexity complexity;
    private final Diary[] diary;

    public static final DiaryReward[] VALUES = values();
    private static final Map<Integer, DiaryReward> BY_ITEM = new HashMap<>();
    private static final EnumMap<DiaryArea, List<DiaryReward>> MAP = new EnumMap<>(DiaryArea.class);

    private List<Diary> getRequiredTasks() {
        return diary[0].map().get(complexity);
    }

    public boolean eligibleFor(final Player player) {
        for (val diary : getRequiredTasks()) {
            if (diary.autoCompleted()) continue;
            if (player.getAchievementDiaries().getProgress(diary) != diary.objectiveLength()) {
                return false;
            }
        }
        return true;
    }

    public boolean isEquipped(final Player player) {
        if (!eligibleFor(player)) {
            return false;
        }
        return player.getEquipment().isWearing(item);
    }

    public static DiaryReward getPreviousReward(final DiaryReward reward) {
        val rewards = get(reward.getArea());
        for (val r : rewards) {
            if (reward.ordinal() - r.ordinal() == 1) {
                return r;
            }
        }
        return null;
    }

    public static DiaryReward getBestEligibleReward(final Player player, final DiaryArea area) {
        val rewards = get(area);
        for (int i = rewards.size() - 1; i >= 0; i--) {
            val reward = rewards.get(i);
            if (reward == null) {
                continue;
            }
            if (reward.eligibleFor(player)) {
                return reward;
            }
        }
        return null;
    }

    public static DiaryReward getWorstReward(final Collection<DiaryReward> rewards) {
        val list = new ArrayList<DiaryReward>(rewards);
        list.sort(Comparator.comparingInt((DiaryReward reward) -> reward.getComplexity().ordinal()));
        return list.get(0);
    }

    public static DiaryReward get(final Diary.DiaryComplexity complexity, final DiaryArea area) {
        return Utils.findMatching(VALUES, reward -> reward.getComplexity().equals(complexity) && reward.getArea().equals(area));
    }

    public static List<DiaryReward> get(final DiaryArea area) {
        return MAP.get(area);
    }

    public static DiaryReward get(final int id) {
        if (id == MaxCape.ARDOUGNE.getCape()) {
            return ARDOUGNE_CLOAK4;
        }
        return BY_ITEM.get(id);
    }

    static {
        for (val area : DiaryArea.VALUES) {
            MAP.put(area, new ArrayList<>(4));
        }
        for (val reward : VALUES) {
            MAP.get(reward.getArea()).add(reward);
            BY_ITEM.put(reward.getItem().getId(), reward);
        }
    }

}
