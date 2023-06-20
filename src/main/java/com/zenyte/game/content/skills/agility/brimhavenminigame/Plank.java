package com.zenyte.game.content.skills.agility.brimhavenminigame;

import com.zenyte.game.content.skills.agility.MarkOfGrace;
import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.content.skills.agility.ardougnerooftop.ArdougneRooftopCourse;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.RenderAnimation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Cresinkel
 */
public class Plank implements Shortcut {

    private static final RenderAnimation RENDER = new RenderAnimation(RenderAnimation.STAND, 762, RenderAnimation.WALK);
    private static final SoundEffect EFFECT1 = new SoundEffect(2495, 4, 0);

    @Override
    public int getLevel(final WorldObject object) {
        return 1;
    }

    @Override
    public int[] getObjectIds() {
        return new int[] { 3570, 3571, 3572 };
    }

    @Override
    public int getDuration(final boolean success, final WorldObject object) {
        return 9;
    }

    @Override
    public void startSuccess(final Player player, final WorldObject object) {
        player.sendSound(EFFECT1);
        player.addWalkSteps(player.getLocation().withinDistance(2803,9590,1) ? player.getX() - 7 : player.getLocation().withinDistance(2770,9557,1) ? player.getX() - 7 : player.getX() + 7, player.getY(), -1, false);
    }

    @Override
    public double getSuccessXp(final WorldObject object) {
        return 6;
    }

    @Override
    public RenderAnimation getRenderAnimation() {
        return RENDER;
    }

}
