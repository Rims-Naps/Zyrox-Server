package com.zenyte.game.content.skills.agility.shortcut;

import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.ImmutableLocation;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.RenderAnimation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Christopher
 * @since 3/25/2020
 */
public class RellekkaBridge implements Shortcut {
    private static final RenderAnimation eastCrossRenderAnim = new RenderAnimation(755, 755, 754, 754, 754, 754, -1);
    private static final RenderAnimation westCrossRenderAnim = new RenderAnimation(757, 757, 756, 756, 756, 756, -1);

    private static final ImmutableLocation EAST_START = new ImmutableLocation(2598, 3608, 0);
    private static final Animation eastFinishAnim = new Animation(758);
    private static final Animation westStartAnim = new Animation(753, 10);

    @Override
    public void startSuccess(Player player, WorldObject object) {
        val isEast = player.getLocation().equals(EAST_START);
        val appearance = player.getAppearance();
        val direction = isEast ? Direction.WEST : Direction.EAST;
        WorldTasksManager.schedule(() -> {
            if (!isEast) {
                player.setAnimation(westStartAnim);
            }
        });
        WorldTasksManager.schedule(() -> {
            appearance.setRenderAnimation(isEast ? eastCrossRenderAnim : westCrossRenderAnim);
            val destination = player.getLocation().transform(direction, 2);
            player.addWalkSteps(destination.getX(), destination.getY(), -1, false);
        }, 1);
        WorldTasksManager.schedule(() -> {
            if (isEast) {
                player.setAnimation(eastFinishAnim);
            }
            appearance.resetRenderAnimation();
        }, 4);
        WorldTasksManager.schedule(() -> {
            val destination = player.getLocation().transform(direction, 1);
            player.addWalkSteps(destination.getX(), destination.getY(), -1, false);
        }, 5);
    }

    @Override
    public Location getRouteEvent(final Player player, final WorldObject object) {
        return new Location(object);
    }

    @Override
    public int getDuration(boolean success, WorldObject object) {
        return 6;
    }

    @Override
    public double getSuccessXp(WorldObject object) {
        return 0;
    }

    @Override
    public int getLevel(WorldObject object) {
        return 1;
    }

    @Override
    public int[] getObjectIds() {
        return new int[]{ObjectId.BROKEN_BRIDGE, ObjectId.BROKEN_BRIDGE_4616};
    }
}
