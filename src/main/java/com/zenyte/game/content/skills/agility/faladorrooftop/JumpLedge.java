package com.zenyte.game.content.skills.agility.faladorrooftop;

import com.zenyte.game.content.skills.agility.MarkOfGrace;
import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Noele | Apr 30, 2018 : 8:37:42 AM
 * @see https://noeles.life || noele@zenyte.com
 */
public class JumpLedge implements Obstacle {

    private static final Animation JUMP = new Animation(1603);

    @Override
    public void startSuccess(final Player player, final WorldObject object) {
        val axis = object.getId() == 11366 || object.getId() == 11370;
        val direction = axis ? (object.getId() == 11366 ? ForceMovement.WEST : ForceMovement.EAST) : ForceMovement.SOUTH;
        val offset = axis ? (object.getId() == 11366 ? player.getX() - 2 : player.getX() + 2) : (object.getName().equals("Gap") ? player.getY() - 4 : player.getY() - 2);
        val finish = axis ? new Location(offset, player.getY(), 3) : new Location(player.getX(), offset, 3);
        player.setFaceLocation(finish);
        WorldTasksManager.schedule(new WorldTask() {
            private int ticks;

            @Override
            public void run() {
                if (ticks == 0) {
                    player.setAnimation(JUMP);
                    player.setForceMovement(new ForceMovement(finish, 45, direction));
                } else if (ticks == 2) {
                    player.setLocation(finish);
                    MarkOfGrace.spawn(player, FaladorRooftopCourse.MARK_LOCATIONS, 50, 50);
                    stop();
                }
                ticks++;
            }
        }, 0, 0);
    }

    @Override
    public int getLevel(final WorldObject object) {
        return 50;
    }

    @Override
    public int getDuration(final boolean success, final WorldObject object) {
        return 3;
    }

    @Override
    public double getSuccessXp(final WorldObject object) {
        return object.getId() == 11365 ? 25 : 10;
    }

    @Override
    public int[] getObjectIds() {
        return new int[] { 11365, 11366, 11367, 11368, 11369, 11370 };
    }

}
