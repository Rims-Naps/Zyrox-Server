package com.zenyte.game.content.skills.agility.shortcut;

import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 26/04/2019 19:04
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class TrollheimWildyRockyHandholds implements Shortcut {

    private static final Location LV_47_BOTTOM = new Location(2950, 3767, 0);

    private static final Animation CLIMB = new Animation(740);

    @Override
    public int getLevel(final WorldObject object) {
        return 60;
    }

    @Override
    public boolean preconditions(final Player player, final WorldObject object) {
        if (object.getId() == 26404) {
            player.sendMessage("You can't go up that way!");
            return false;
        }
        return true;
    }

    @Override
    public int[] getObjectIds() {
        return new int[] {
                26404, 26405, 26406
        };
    }

    @Override
    public int getDuration(final boolean success, final WorldObject object) {
        return 2;
    }

    @Override
    public void startSuccess(final Player player, final WorldObject object) {
        val destination = LV_47_BOTTOM;
        val forceMovement = new ForceMovement(destination, 7 * 30, ForceMovement.WEST);
        player.setAnimation(CLIMB);
        player.setForceMovement(forceMovement);
        WorldTasksManager.schedule(() -> {
            player.setAnimation(Animation.STOP);
            player.setLocation(destination);
        }, 6);
    }

    @Override
    public double getSuccessXp(final WorldObject object) {
        return 0;
    }
}
