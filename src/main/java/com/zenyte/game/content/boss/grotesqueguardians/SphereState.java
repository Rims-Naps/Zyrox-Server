package com.zenyte.game.content.boss.grotesqueguardians;

import com.zenyte.utils.Ordinal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 01/08/2019 | 21:35
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Ordinal
@RequiredArgsConstructor
public enum SphereState {
    LOW(0, 2, 1437),
    MEDIUM(1, 10, 1438),
    HIGH(2, 20, 1439);

    private static final SphereState[] all = values();
    @Getter
    private final int identifier, damage, projectile;

    public SphereState next() {
        return all[ordinal() + 1];
    }
}
