package com.zenyte.game.world.entity.player.login;

import com.zenyte.game.world.entity.player.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kris | 08/05/2019 01:41
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
@Getter
public class CachedEntry {

    private final long time;
    private final Player cachedAccount;

}
