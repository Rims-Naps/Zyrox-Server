package com.zenyte.plugins.events;

import com.zenyte.game.content.multicannon.Multicannon;
import com.zenyte.plugins.Event;
import lombok.Data;

/**
 * @author Kris | 09/10/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public class CannonRemoveEvent implements Event {
    private final Multicannon cannon;
}
