package com.zenyte.game.content.minigame.warriorsguild.plugins;

import com.zenyte.game.content.minigame.warriorsguild.dummyroom.DummyRoom;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.GlobalAreaManager;
import lombok.val;

/**
 * @author Kris | 23/03/2019 17:01
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class DummyObject implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equalsIgnoreCase("Hit") || option.equalsIgnoreCase("View")) {
            val area = GlobalAreaManager.get("Warriors' Guild Dummy Room");
            if (!(area instanceof DummyRoom)) {
                return;
            }
            ((DummyRoom) area).handleObject(player, object);
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                23958, 23959, 23960, 23961, 23962, 23963, 23964, 24908
        };
    }
}
