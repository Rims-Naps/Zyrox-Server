package com.zenyte.game.content.skills.agility.faladorrooftop;

import com.zenyte.game.content.skills.agility.MarkOfGrace;
import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Noele | Apr 29, 2018 : 12:30:07 PM
 * @see https://noeles.life || noele@zenyte.com
 */
public class RoughWall implements Obstacle {

    private static final Animation CLIMB = new Animation(828);
    private static final Location START = new Location(3036, 3341, 0);
    private static final Location FINISH = new Location(3036, 3342, 3);

    @Override
    public void startSuccess(final Player player, final WorldObject object) {
        player.faceObject(object);
        player.setAnimation(CLIMB);
        WorldTasksManager.schedule(() -> {
            player.setLocation(FINISH);
            MarkOfGrace.spawn(player, FaladorRooftopCourse.MARK_LOCATIONS, 50, 50);
        }, 1);
        player.addAttribute("SeersTrapdoor", 1);
    }

    @Override
    public int getLevel(final WorldObject object) {
        return 50;
    }

    @Override
    public int[] getObjectIds() {
        return new int[] { 10833 };
    }

    @Override
    public int getDuration(final boolean success, final WorldObject object) {
        return 2;
    }

    @Override
    public double getSuccessXp(final WorldObject object) {
        return 10;
    }

    @Override
    public Location getRouteEvent(final Player player, final WorldObject object) {
        return START;
    }
}
