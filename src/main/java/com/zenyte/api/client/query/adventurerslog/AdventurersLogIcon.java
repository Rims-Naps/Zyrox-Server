package com.zenyte.api.client.query.adventurerslog;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kris | 15/05/2019 14:28
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@AllArgsConstructor
public enum AdventurersLogIcon {
    RARE_DROP(null),
    LEVEL_99(null),
    OVERALL_SKILLING("overall.png"),
    MAX_XP(null),
    PET_DROP(null),
    HCIM_DEATH("hardcore_ironman.png"),
    DIARY_COMPLETION("diary.png"),
    QUEST_COMPLETION("quest.png"),
    MINIGAME("minigame.png")
    ;

    private final String link;
}
