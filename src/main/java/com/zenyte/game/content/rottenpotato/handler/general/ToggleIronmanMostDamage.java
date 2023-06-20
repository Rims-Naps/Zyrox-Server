package com.zenyte.game.content.rottenpotato.handler.general;

import com.zenyte.game.content.rottenpotato.PotatoToggles;
import com.zenyte.game.content.rottenpotato.RottenPotatoActionType;
import com.zenyte.game.content.rottenpotato.handler.BasicRottenPotatoActionHandler;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;

public class ToggleIronmanMostDamage implements BasicRottenPotatoActionHandler
{

    @Override
    public String option()
    {
        return "Ironman 70% Damage Restriction: Currently " + (PotatoToggles.IRONMAN_DMG_RESTRICTION ? "enabled." : "disabled.");
    }

    @Override
    public Privilege getPrivilege()
    {
        return Privilege.MODERATOR;
    }

    @Override
    public void execute(Player player)
    {
        PotatoToggles.IRONMAN_DMG_RESTRICTION = !PotatoToggles.IRONMAN_DMG_RESTRICTION;
        World.sendMessage(MessageType.UNFILTERABLE, "The 70% damage requirement for ironmen has been " + (PotatoToggles.IRONMAN_DMG_RESTRICTION ? "re-enabled with the conclusion of an event!" : "disabled for the next hour for an event!"));
        WorldTasksManager.schedule(() -> {
            PotatoToggles.IRONMAN_DMG_RESTRICTION = true;
        }, 6000, 0);
    }

    @Override
    public RottenPotatoActionType type()
    {
        return RottenPotatoActionType.ITEM;
    }
}
