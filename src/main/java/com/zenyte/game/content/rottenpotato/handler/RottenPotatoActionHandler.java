package com.zenyte.game.content.rottenpotato.handler;

import com.zenyte.game.content.rottenpotato.RottenPotatoActionType;
import com.zenyte.game.world.entity.player.Privilege;

/**
 * @author Christopher
 * @since 3/23/2020
 */
public interface RottenPotatoActionHandler {
    String option();

    Privilege getPrivilege();

    RottenPotatoActionType type();
}
