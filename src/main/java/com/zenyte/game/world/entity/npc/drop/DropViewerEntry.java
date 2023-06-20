package com.zenyte.game.world.entity.npc.drop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 14-4-2019 | 19:43
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
public class DropViewerEntry {

    private final int id;
    private final int minAmount, maxAmount;
    private final double rate;
    private final String info;

    public DropViewerEntry(final int id, final int minAmount, final int maxAmount, final double rate, final String info) {
        this.id = id;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.rate = rate;
        this.info = info;
    }

    public DropViewerEntry(final int id, final int minAmount, final int maxAmount, final double rate) {
        this(id, minAmount, maxAmount, rate, "");
    }

    public boolean isPredicated() {
        return !info.isEmpty();
    }
}
