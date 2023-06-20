package com.zenyte.game.packet.in.event;

import com.zenyte.Constants;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 20:07
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Slf4j
public class EventMouseIdleEvent implements ClientProtEvent {
    @Override
    public void log(@NotNull final Player player) {
        this.log(player, Strings.EMPTY);
    }

    @Override
    public void handle(Player player) {
        player.getTemporaryAttributes().put("User deemed inactive", true);
        if (player.getPrivilege().eligibleTo(Privilege.JUNIOR_MODERATOR) && !Constants.WORLD_PROFILE.isDevelopment()) {
            if (player.isLocked() || player.isUnderCombat() || player.getNumericTemporaryAttribute("staff timeout disabled").intValue() == 1) {
                return;
            }
            player.logout(false);
            log.info("Idle logout: " + player.getName());
        }
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
