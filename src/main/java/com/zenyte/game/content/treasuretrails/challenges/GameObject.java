package com.zenyte.game.content.treasuretrails.challenges;

import com.zenyte.game.world.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Kris | 07/04/2019 13:46
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public final class GameObject {
    private final int id;
    private final Location tile;
    private String option;
}
