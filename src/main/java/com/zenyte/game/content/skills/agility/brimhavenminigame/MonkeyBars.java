package com.zenyte.game.content.skills.agility.brimhavenminigame;

import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.RenderAnimation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Cresinkel
 */
public class MonkeyBars implements Shortcut {
    @Override
    public int getLevel(WorldObject object) {
        return 1;
    }

    @Override
    public int[] getObjectIds() {return new int[] {3564};}

    @Override
    public int getDuration(boolean success, WorldObject object) {
        return 8;
    }

    private final RenderAnimation render = new RenderAnimation(745, 745, 744, 745, 745, 745, 744);
    private final Animation start = new Animation(742);
    private final Animation end = new Animation(743);

    @Override
    public void startSuccess(final Player player, final WorldObject object) {
        val destination = new Location(object.getX() == 2774 ? 2782 : object.getX() == 2781 ? 2773 : object.getX() + 1, object.getY() == 9577 ? 9569 : object.getY() == 9570 ? 9578 : object.getY() == 9566 ? 9558 : object.getY() == 9559 ? 9567 : 9546, object.getPlane());
        player.setFaceLocation(destination);
        player.getAppearance().setRenderAnimation(render);
        player.setAnimation(start);
        player.addWalkSteps(destination.getX(), destination.getY(), 9, false);
    }

    @Override
    public void endSuccess(final Player player, final WorldObject object) {
        player.faceObject(object);
        player.setAnimation(end);
        WorldTasksManager.schedule(() -> player.getAppearance().resetRenderAnimation());
    }

    @Override
    public Location getRouteEvent(final Player player, final WorldObject object) {
        return new Location(object.getX() == 2774 ? 2773 : object.getX() == 2781 ? 2782 : object.getX() + 1, object.getY() == 9545 ? 9546 : object.getY() == 9577 ? 9578 : object.getY() == 9559 ? 9558 : object.getY() == 9566 ? 9567 : 9569, object.getPlane());
    }

    @Override
    public double getSuccessXp(WorldObject object) {
        return 14;
    }
}
