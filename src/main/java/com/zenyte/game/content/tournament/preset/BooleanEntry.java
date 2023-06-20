package com.zenyte.game.content.tournament.preset;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kris | 09/06/2019 04:32
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
@Getter
public class BooleanEntry<T> {
    private T t;
    private boolean bool;
}
