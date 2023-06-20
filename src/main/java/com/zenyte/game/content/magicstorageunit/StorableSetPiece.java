package com.zenyte.game.content.magicstorageunit;

import lombok.Getter;

/**
 * @author Kris | 15/09/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
public class StorableSetPiece {
    public StorableSetPiece(final int... ids) {
        this.ids = ids;
    }
    private final int[] ids;
}

