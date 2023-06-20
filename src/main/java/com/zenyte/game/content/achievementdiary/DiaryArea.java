package com.zenyte.game.content.achievementdiary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 6-11-2018 | 22:18
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@RequiredArgsConstructor
public enum DiaryArea {

    ARDOUGNE("Ardougne", 1),
    LUMBRIDGE_AND_DRAYNOR("Lumbridge & Draynor", 6),
    VARROCK("Varrock", 8),
    FALADOR("Falador", 2),
    MORYTANIA("Morytania", 7),
    KARAMJA("Karamja", 0),
    DESERT("Desert", 5),
    WESTERN_PROVINCES("Western Provinces", 10),
    KANDARIN("Kandarin", 4),
    FREMENNIK("Fremennik", 3),
    WILDERNESS("Wilderness", 9),
    KEBOS("Kourend & Kebos Lowlands", 11);

    private final String areaName;
    private final int index;

    public static final DiaryArea[] VALUES = values();
}
