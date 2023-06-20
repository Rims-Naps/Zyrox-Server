package com.zenyte.game.content.skills.agility.shortcut;

import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Kris | 10/05/2019 17:46
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ZeahHosidiusSteppingStone implements Shortcut {

    private static final Location NORTH = new Location(1722, 3512, 0);
    private static final Location SOUTH = new Location(1722, 3507, 0);

    @Override
    public void startSuccess(final Player player, final WorldObject object) {
        final boolean direction = player.getY() >= 3512;
        player.setFaceLocation(object);
        WorldTasksManager.schedule(new WorldTask() {

            private int ticks;

            @Override
            public void run() {
                if(ticks == 0) {
                    player.setAnimation(Animation.JUMP);
                    player.setForceMovement(new ForceMovement(player.getLocation(), 15, object, 35, direction ? ForceMovement.SOUTH : ForceMovement.NORTH));
                } else if(ticks == 1) {
                    player.setLocation(object);
                } else if(ticks == 2) {
                    player.setAnimation(Animation.JUMP);
                    player.setForceMovement(new ForceMovement(player.getLocation(), 15, direction ? SOUTH : NORTH, 35, direction ? ForceMovement.SOUTH : ForceMovement.NORTH));
                } else if (ticks == 3) {
                    player.setLocation(direction ? SOUTH : NORTH);
                    stop();
                }

                ticks++;
            }

        }, 0, 0);
    }

    @Override
    public int getLevel(final WorldObject object) {
        return 45;
    }

    @Override
    public int[] getObjectIds() {
        return new int[] { 29728 };
    }

    @Override
    public int getDuration(final boolean success, final WorldObject object) {
        return 4;
    }

    @Override
    public double getSuccessXp(final WorldObject object) {
        return 0;
    }

    @Override
    public Location getRouteEvent(final Player player, final WorldObject object) {
        return player.inArea("Great Kourend: Hosidius House") ? SOUTH : NORTH;
    }

}
