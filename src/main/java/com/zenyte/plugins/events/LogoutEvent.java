package com.zenyte.plugins.events;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.Event;
import lombok.Data;

/**
 * @author Kris | 23/03/2019 23:15
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public class LogoutEvent implements Event {

    private final Player player;

}
