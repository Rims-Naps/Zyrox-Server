package com.zenyte.game.content.rottenpotato.handler.general;

import com.zenyte.game.content.rottenpotato.PotatoToggles;
import com.zenyte.game.content.rottenpotato.handler.BasicRottenPotatoActionHandler;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;

public class ToggleBandosKillcount implements BasicRottenPotatoActionHandler
{

    @Override
    public void execute(Player player)
    {
        PotatoToggles.BANDOS_KC_REQUIRED = !PotatoToggles.BANDOS_KC_REQUIRED;
        World.sendMessage(MessageType.UNFILTERABLE, Colour.RS_GREEN.wrap("[Server Alert]") + "The Bandos GWD KC requirement has been " + (PotatoToggles.BANDOS_KC_REQUIRED ? "re-enabled with the conclusion of an event!" : "disabled for the next hour for an event!"));
        WorldTasksManager.schedule(() -> {
            PotatoToggles.BANDOS_KC_REQUIRED = true;
        }, 1000, 0);
    }

    @Override
    public String option()
    {
        return "Toggle Bandos KC Requirement: Currently " + (PotatoToggles.BANDOS_KC_REQUIRED ? "enabled." : "disabled.");
    }

    @Override
    public Privilege getPrivilege()
    {
        return Privilege.GLOBAL_MODERATOR;
    }
}

