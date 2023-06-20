package com.zenyte.game.content.rottenpotato.handler.player;

import com.zenyte.game.content.rottenpotato.handler.PlayerRottenPotatoActionHandler;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.game.world.entity.player.punishments.PunishmentManager;
import com.zenyte.game.world.entity.player.punishments.PunishmentType;

/**
 * @author Christopher
 * @since 3/23/2020
 */
public class MacBan implements PlayerRottenPotatoActionHandler {
    @Override
    public void execute(Player user, Player target) {
        PunishmentManager.requestPunishment(user, target.getName(), PunishmentType.MAC_BAN);
    }

    @Override
    public String option() {
        return "Mac Ban";
    }

    @Override
    public Privilege getPrivilege() {
        return Privilege.MODERATOR;
    }
}
