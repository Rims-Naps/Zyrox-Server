package com.zenyte.game.content.godwars.objects;

import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.utils.ProjectileUtils;
import lombok.val;

/**
 * @author Kris | 13/04/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class InstanceCrevice implements Shortcut {

    private static final Animation crawlInAnimation = new Animation(2594);
    private static final Animation crawlOutAnimation = new Animation(2595);

    @Override
    public void startSuccess(final Player player, final WorldObject object) {
        val direction = Direction.mainDirections[(object.getRotation() - 1) & 0x3];
        val closeExit = object.transform(direction, 2);
        val farExit = object.transform(direction, 3);

        //The other crevice will always be either 2 or 3 tiles away from the player, therefore let's depth-test it to find the destination.
        val destination = ProjectileUtils.isProjectileClipped(null, null, closeExit, farExit, true) ? farExit : closeExit;
        val center = object.transform((destination.getX() - object.getX()) / 2, (destination.getY() - object.getY()) / 2, 0);
        player.setMaximumTolerance(true);
        CharacterLoop.forEach(player.getLocation(), 12, NPC.class, npc -> {
            if (npc.getCombat().getTarget() == player) {
                npc.getCombat().removeTarget();
            }
        });

        player.setAnimation(crawlInAnimation);
        player.autoForceMovement(center, 60);
        WorldTasksManager.schedule(() -> player.getAppearance().setHideEquipment(true));
        WorldTasksManager.schedule(() -> player.autoForceMovement(destination, 60), 2);
        WorldTasksManager.schedule(() -> {
            player.getAppearance().setHideEquipment(false);
            player.setAnimation(crawlOutAnimation);
        }, 3);
    }

    @Override
    public void endSuccess(final Player player, final WorldObject object) {
        player.setMaximumTolerance(false);
        player.blockIncomingHits();
        player.getReceivedHits().clear();
    }

    @Override
    public String getEndMessage(final boolean success) {
        return success ? "You climb your way through the narrow crevice." : null;
    }

    @Override
    public int getLevel(final WorldObject object) {
        return 0;
    }

    @Override
    public int[] getObjectIds() {
        return new int[] { 35013 };
    }

    @Override
    public int getDuration(final boolean success, final WorldObject object) {
        return 4;
    }

    @Override
    public double getSuccessXp(final WorldObject object) {
        return 0;
    }
}
