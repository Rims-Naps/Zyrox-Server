package com.zenyte.game.content.minigame.barrows;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 30/11/2018 23:13
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BarrowsLadder implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Climb-up")) {
            val barrows = player.getBarrows();
            val hiddenWight = barrows.getHiddenWight();
            player.setLocation(hiddenWight.getBySarcophagus());
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                20673, 20674, 20675, 20676, 20677
        };
    }
}
