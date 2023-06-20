package com.zenyte.game.content.rottenpotato;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.zenyte.game.content.rottenpotato.handler.RottenPotatoActionHandler;
import com.zenyte.game.content.rottenpotato.handler.general.*;
import com.zenyte.game.content.rottenpotato.handler.npc.KillNpc;
import com.zenyte.game.content.rottenpotato.handler.player.*;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christopher
 * @since 3/27/2020
 */
public enum RottenPotatoAction {
    BAN(RottenPotatoItemOption.PUNISHMENT, Ban.class),
    IP_BAN(RottenPotatoItemOption.PUNISHMENT, IPBan.class),
    MAC_BAN(RottenPotatoItemOption.PUNISHMENT, MacBan.class),
    MUTE(RottenPotatoItemOption.PUNISHMENT, Mute.class),
    IP_MUTE(RottenPotatoItemOption.PUNISHMENT, IPMute.class),
    MAC_MUTE(RottenPotatoItemOption.PUNISHMENT, MacMute.class),
    KICK(RottenPotatoItemOption.PUNISHMENT, Kick.class),
    CLEAR_CONTRACT(RottenPotatoItemOption.UTILITY, RemoveFarmContract.class),
    TELE_TO(RottenPotatoItemOption.UTILITY, TeleTo.class),
    TELE_TO_ME(RottenPotatoItemOption.UTILITY, TeleToMe.class),
    TOGGLE_IM_DAMAGE_RESTRICTION(RottenPotatoItemOption.UTILITY, ToggleIronmanMostDamage.class),
    TOGGLE_ARMA_KC(RottenPotatoItemOption.UTILITY, ToggleArmadylKillcount.class),
    TOGGLE_BANDOS_KC(RottenPotatoItemOption.UTILITY, ToggleBandosKillcount.class),
    TOGGLE_SARA_KC(RottenPotatoItemOption.UTILITY, ToggleSaradominKillcount.class),
    TOGGLE_ZAMMY_KC(RottenPotatoItemOption.UTILITY, ToggleZamorakKillcount.class),
    KILL_NPC(RottenPotatoItemOption.NONE, KillNpc.class);

    public static final Multimap<RottenPotatoItemOption, RottenPotatoActionHandler> itemOptionMap =
            HashMultimap.create();
    public static final Multimap<RottenPotatoActionType, RottenPotatoActionHandler> actionTypeMap =
            HashMultimap.create();
    private static final RottenPotatoAction[] actions = values();

    static {
        for (RottenPotatoAction action : actions) {
            itemOptionMap.put(action.itemOption, action.actionHandler);
            actionTypeMap.put(action.actionHandler.type(), action.actionHandler);
        }
    }

    private final RottenPotatoItemOption itemOption;
    private final RottenPotatoActionHandler actionHandler;

    RottenPotatoAction(RottenPotatoItemOption itemOption, Class<? extends RottenPotatoActionHandler> actionHandler) {
        this.itemOption = itemOption;
        try {
            this.actionHandler = actionHandler.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Could not create instance for " + actionHandler.getName(), e);
        }
    }

    public static List<RottenPotatoActionHandler> getActions(final Player player, final RottenPotatoItemOption itemOption) {
        val handlers = new ArrayList<RottenPotatoActionHandler>();
        for (RottenPotatoActionHandler handler : itemOptionMap.get(itemOption)) {
            if (player.getPrivilege().eligibleTo(handler.getPrivilege())) {
                handlers.add(handler);
            }
        }
        return handlers;
    }

    public static List<RottenPotatoActionHandler> getActions(final Player player, final RottenPotatoActionType actionType) {
        val handlers = new ArrayList<RottenPotatoActionHandler>();
        for (RottenPotatoActionHandler handler : actionTypeMap.get(actionType)) {
            if (player.getPrivilege().eligibleTo(handler.getPrivilege())) {
                handlers.add(handler);
            }
        }
        return handlers;
    }
}
