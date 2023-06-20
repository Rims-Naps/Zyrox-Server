package com.zenyte.game.world.entity.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 4-2-2019 | 22:13
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@RequiredArgsConstructor
public enum MessageType {

    UNFILTERABLE(0),
    GLOBAL_BROADCAST(14),
    EXAMINE_ITEM(27),
    EXAMINE_NPC(28),
    EXAMINE_OBJECT(29),
    AUTOTYPER(90),
    TRADE_REQUEST(101),
    CHALLENGE_REQUEST(103),
    FILTERABLE(105);

    private final int type;

}
