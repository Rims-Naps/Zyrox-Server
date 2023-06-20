package com.zenyte.game.content.chambersofxeric.plugins.object;

import com.zenyte.game.content.chambersofxeric.room.FloorEdgeRoom;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

import static com.zenyte.plugins.object.LadderOA.CLIMB_DOWN;

/**
 * @author Kris | 06/07/2019 04:29
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class RaidDownstairsHole implements ObjectAction {
    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        player.getRaid().ifPresent(raid -> raid.ifInRoom(player, FloorEdgeRoom.class, room -> {
            player.lock();
            player.setAnimation(CLIMB_DOWN);
            player.getPacketDispatcher().sendClientScript(1512);
            WorldTasksManager.schedule(() -> {
                if (raid.isDestroyed()) {
                    return;
                }
                player.getPacketDispatcher().sendClientScript(1513);
                val center = room.getBoundTile();
                player.setLocation(center);
                player.lock(2);
                for (int x = -2; x <= 2; x++) {
                    for (int y = -2; y <= 2; y++) {
                        val obj = World.getObjectWithType(center.transform(x, y, 0), 10);
                        if (obj == null || (obj.getId() != 29995 && obj.getId() != 29996)) {
                            continue;
                        }
                        switch(obj.getRotation()) {
                            case 0:
                                WorldTasksManager.schedule(() -> player.addWalkSteps(center.getX(), center.getY() - 1, 1, true));
                                break;
                            case 1:
                                WorldTasksManager.schedule(() -> player.addWalkSteps(center.getX() - 1, center.getY(), 1, true));
                                break;
                            case 2:
                                WorldTasksManager.schedule(() -> player.addWalkSteps(center.getX(), center.getY() + 1, 1, true));
                                break;
                            default:
                                WorldTasksManager.schedule(() -> player.addWalkSteps(center.getX() + 1, center.getY(), 1, true));
                                break;
                        }
                        return;
                    }
                }
            });
        }));
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                29734, 29735
        };
    }
}
