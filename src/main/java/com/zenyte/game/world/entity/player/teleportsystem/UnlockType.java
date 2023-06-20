package com.zenyte.game.world.entity.player.teleportsystem;

import lombok.AllArgsConstructor;

/**
 * @author Kris | 30/03/2019 18:23
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
public enum UnlockType {

    DEFAULT("Default"),
    VISIT("Visiting"),
    SCROLL("Teleport scroll");

    final String formatted;

}
