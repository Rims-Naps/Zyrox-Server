package com.zenyte.game.content.minigame.duelarena.interfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;
import lombok.var;

/**
 * @author Tommeh | 30-11-2018 | 18:06
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class DuelArenaScoreboardInterface extends Interface {

    @Override
    protected void attach() {
        put(16, "Duels Won");
        put(17, "Duels Lost");
        put(20, "Scoreboard");
    }

    @Override
    public void open(Player player) {
        var index = 71;
        player.getInterfaceHandler().sendInterface(getInterface());
        for (val result : World.LATEST_DUELS) {
            player.getPacketDispatcher().sendComponentText(getInterface(), index--, result);
        }
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Duels Won"), "My Wins: " + player.getNumericAttribute("DuelsWon").intValue());
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Duels Lost"), "My Losses: " + player.getNumericAttribute("DuelsLost").intValue());
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Scoreboard"), "Scoreboard");
    }

    @Override
    protected void build() {

    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.DUEL_SCOREBOARD;
    }
}
