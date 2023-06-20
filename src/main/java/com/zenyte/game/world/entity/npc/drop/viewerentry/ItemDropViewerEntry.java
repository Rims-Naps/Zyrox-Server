package com.zenyte.game.world.entity.npc.drop.viewerentry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 05/10/2019 | 19:02
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
@RequiredArgsConstructor
public class ItemDropViewerEntry implements DropViewerEntry {

    private final int item;
    private final int minAmount, maxAmount;
    private final double rate;
    private final String info;

    @Override
    public int getMinAmount() {
        return minAmount;
    }

    @Override
    public int getMaxAmount() {
        return maxAmount;
    }

    @Override
    public double getRate() {
        return rate;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public boolean isPredicated() {
        return !info.isEmpty();
    }
}
