package com.zenyte.game.content.rottenpotato.handler.player;

import com.zenyte.game.content.minigame.inferno.instance.Inferno;
import com.zenyte.game.content.rottenpotato.handler.PlayerRottenPotatoActionHandler;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import lombok.val;

public class TeleTo implements PlayerRottenPotatoActionHandler {

    @Override
    public void execute(Player user, Player target) {
        val raid = target.getRaid();
        if (raid.isPresent() && !user.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
            user.sendMessage("You cannot teleport to a player in a raid as a non-administrator.");
            return;
        }
        if (target.getArea() instanceof Inferno && !user.getPrivilege().eligibleTo(Privilege.SPAWN_ADMINISTRATOR)) {
            user.sendMessage("You cannot teleport to a player in the Inferno.");
            return;
        }
        target.log(LogLevel.INFO, user.getName() + " force teleported to you at " + target.getLocation() + ".");
        user.setLocation(target.getLocation());
    }

    @Override
    public String option() {
        return "Teleport to player.";
    }

    @Override
    public Privilege getPrivilege() {
        return Privilege.MODERATOR;
    }
}
