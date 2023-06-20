package com.zenyte.game.content.tournament.preset;

import com.zenyte.game.content.skills.magic.Rune;
import lombok.Data;

/**
 * @author Tommeh | 22/07/2019 | 22:11
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Data
public class RuneEntry {

    private final Rune rune;
    private final int amount;

    public static RuneEntry of(final Rune rune, final int amount) {
        return new RuneEntry(rune, amount);
    }
}
