package com.zenyte.game.content.partyroom;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.DoubleDoor;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 07/06/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PartyRoomDoors implements ObjectAction {
    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        val close = option.equalsIgnoreCase("Close");
        if (close) {
            if (!FaladorPartyRoom.getPartyRoom().getVariables().isDoorsCloseable()) {
                player.sendMessage("The doors may not currently be closed.");
                return;
            }
        }
        if (option.equalsIgnoreCase("Open") || close) {
            DoubleDoor.handleDoubleDoor(player, object);
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                24059, 24061, 24063, 24064
        };
    }
}
