package com.zenyte.game.world.entity.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kris | 25/05/2019 01:12
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@AllArgsConstructor
public enum LogLevel {

    SPAM(0),
    LOW_PACKET(50),
    HIGH_PACKET(200),
    INFO(500),
    ERROR(1000);

    private final int priority;

}
