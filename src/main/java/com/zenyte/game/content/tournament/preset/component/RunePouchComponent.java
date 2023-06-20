package com.zenyte.game.content.tournament.preset.component;

import com.zenyte.game.content.skills.magic.Rune;
import com.zenyte.game.content.tournament.preset.RuneEntry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Tommeh | 22/07/2019 | 22:10
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@RequiredArgsConstructor
public class RunePouchComponent {

    public static final RunePouchComponent VENGEANCE = RunePouchComponent.of(RuneEntry.of(Rune.ASTRAL, 1000), RuneEntry.of(Rune.DEATH, 1000), RuneEntry.of(Rune.EARTH, 1000));
    public static final RunePouchComponent ICE_BARRAGE = RunePouchComponent.of(RuneEntry.of(Rune.WATER, 1000), RuneEntry.of(Rune.DEATH, 1000), RuneEntry.of(Rune.BLOOD, 1000));

    @Getter private final Set<RuneEntry> entries;

    public static RunePouchComponent of(final RuneEntry... entries) {
        val set = new HashSet<RuneEntry>(3);
        for (val entry : entries) {
            set.add(entry);
        }
        return new RunePouchComponent(set);
    }
}
