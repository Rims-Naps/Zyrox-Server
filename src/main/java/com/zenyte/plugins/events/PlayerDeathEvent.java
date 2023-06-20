package com.zenyte.plugins.events;

import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.Event;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kris | 27/03/2019 13:41
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public class PlayerDeathEvent implements Event {

    @NotNull private Player player;
    @Nullable private Entity source;

}
