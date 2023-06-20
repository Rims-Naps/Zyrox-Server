package com.zenyte.game.content.rottenpotato.handler.player;

import com.zenyte.game.content.minigame.inferno.instance.Inferno;
import com.zenyte.game.content.rottenpotato.handler.PlayerRottenPotatoActionHandler;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import lombok.val;

public class TeleToMe implements PlayerRottenPotatoActionHandler {
    @Override
    public void execute(Player user, Player target) {
        val raid = target.getRaid();
        if (raid.isPresent() && !target.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
            user.sendMessage("You cannot teleport non-administrators into a raid.");
            return;
        }
        if (user.getArea() instanceof Inferno && !target.getPrivilege().eligibleTo(Privilege.SPAWN_ADMINISTRATOR)) {
            user.sendMessage("You cannot teleport a player into the Inferno.");
            return;
        }
        target.log(LogLevel.INFO, "Force teleported by " + user.getName() + " to " + user.getLocation() + ".");
        target.setLocation(user.getLocation());
    }

    @Override
    public String option() {
        return "Teleport player to you.";
    }

    @Override
    public Privilege getPrivilege() {
        return Privilege.MODERATOR;
    }
}
