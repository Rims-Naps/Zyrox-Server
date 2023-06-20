package com.zenyte.game.content.minigame.inferno.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 29/11/2019 | 20:06
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
@RequiredArgsConstructor
public class WaveEntry {

    private final WaveNPC npc;
    private final int count;

    public WaveEntry(final WaveNPC npc) {
        this(npc, 1);
    }
}
