package com.zenyte.game.content.rottenpotato.handler.general;

import com.zenyte.game.content.rottenpotato.PotatoToggles;
import com.zenyte.game.content.rottenpotato.handler.BasicRottenPotatoActionHandler;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;

public class ToggleZamorakKillcount implements BasicRottenPotatoActionHandler
{


    @Override
    public void execute(Player player)
    {
        PotatoToggles.ZAMMY_KC_REQUIRED = !PotatoToggles.ZAMMY_KC_REQUIRED;
        World.sendMessage(MessageType.UNFILTERABLE, Colour.RS_GREEN.wrap("[Server Alert]") + "The Zamorak GWD KC requirement has been " + (PotatoToggles.ZAMMY_KC_REQUIRED ? "re-enabled with the conclusion of an event!" : "disabled for the next hour for an event!"));
        WorldTasksManager.schedule(() -> {
            PotatoToggles.ZAMMY_KC_REQUIRED = true;
        }, 1000, 0);
    }

    @Override
    public String option()
    {
        return "Toggle Zamorak KC Requirement: Currently " + (PotatoToggles.ZAMMY_KC_REQUIRED ? "enabled." : "disabled.");
    }

    @Override
    public Privilege getPrivilege()
    {
        return Privilege.GLOBAL_MODERATOR;
    }
}