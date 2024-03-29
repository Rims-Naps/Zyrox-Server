package com.zenyte.plugins.events;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.Event;
import lombok.Data;

/**
 * @author Kris | 26/04/2019 19:46
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public class PlayerResetEvent implements Event {

    private final Player player;

}