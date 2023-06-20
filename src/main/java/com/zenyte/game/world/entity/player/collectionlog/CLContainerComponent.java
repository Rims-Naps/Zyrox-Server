package com.zenyte.game.world.entity.player.collectionlog;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author Kris | 13/03/2019 14:22
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@Accessors(fluent = true)
enum CLContainerComponent {

    CONTAINER, CONTAINER_OPTIONS, CONTAINER_TEXT, CONTAINER_SCROLLBAR;
    private final String toString = name().toLowerCase().replaceAll("_", " ");

}
