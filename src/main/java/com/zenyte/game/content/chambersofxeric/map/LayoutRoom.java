package com.zenyte.game.content.chambersofxeric.map;

import com.zenyte.game.util.Direction;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kris | 21/09/2019 22:58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@AllArgsConstructor
final class LayoutRoom {
    private final RaidRoom room;
    private final Direction direction;
}
