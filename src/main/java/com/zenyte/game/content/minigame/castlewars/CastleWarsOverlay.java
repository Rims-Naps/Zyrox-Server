package com.zenyte.game.content.minigame.castlewars;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class CastleWarsOverlay extends Interface {

    @AllArgsConstructor
    public enum CWarsOverlayVarbit {

        DOOR_HEALTH(136, 100, false),
        DOOR_LOCK(137, 0, false),
        SARADOMIN_FLAG(143, 0, true),
        SARADOMIN_SCORE(145, 0, true),
        ZAMORAK_FLAG(153, 0, true),
        ZAMORAK_SCORE(155, 0, true),
        ROCKS_NS(138, 0, false),
        ROCKS_EW(139, 0, false),
        CATAPULT(140, 0, false),
        ;

        @Getter private final int id;
        @Getter private final int defaultV;
        @Getter private final boolean universal;

        public final static CWarsOverlayVarbit[] VALUES = values();

    }

    @Override
    protected void attach() {}

    @Override
    public void open(final Player player) {
        player.getInterfaceHandler().sendInterface(this);
    }


    public static void processVarbits(final Player player) {
        val team = CastleWars.getTeam(player);
        val currentTime = Math.round((int) Math.floor(CastleWarsArea.getTicks() / 100));

        for(final CWarsOverlayVarbit varbit : CWarsOverlayVarbit.VALUES) {

            // skip the sending of any duplicate values
            if(player.getVarManager().getBitValue(varbit.getId()) == CastleWars.getVarbit(team, varbit))
                continue;

            player.getVarManager().sendBit(varbit.getId(), CastleWars.getVarbit(team, varbit));
        }

        if(player.getVarManager().getValue(CastleWarsLobbyOverlay.TIMER_VARP) != currentTime) {
            player.getVarManager().sendVar(CastleWarsLobbyOverlay.TIMER_VARP, currentTime);
        }
    }

    @Override
    protected void build() {}

    @Override
    public GameInterface getInterface() {
        return GameInterface.CASTLE_WARS_OVERLAY;
    }
}
