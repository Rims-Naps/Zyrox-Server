package com.zenyte.game.content.object;

import lombok.Getter;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public enum OpenStorageDefinitions {

    CHEST_25387(25387, 25388),
    ;

    @Getter
    private final int closed;

    @Getter
    private final int open;

    OpenStorageDefinitions(final int closed, final int open) {
        this.closed = closed;
        this.open = open;
    }
}
