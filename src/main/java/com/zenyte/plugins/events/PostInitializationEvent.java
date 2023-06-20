package com.zenyte.plugins.events;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.Event;
import lombok.Data;

/**
 * @author Corey
 * @since 16/08/19
 */
@Data
public class PostInitializationEvent implements Event {
    private final Player player;
}
