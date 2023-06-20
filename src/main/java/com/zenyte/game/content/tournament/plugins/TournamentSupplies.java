package com.zenyte.game.content.tournament.plugins;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Tommeh | 01/06/2019 | 14:50
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class TournamentSupplies implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("View")) {
            GameInterface.TOURNAMENT_PRESETS.open(player);
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 35006, 35007 };
    }
}
