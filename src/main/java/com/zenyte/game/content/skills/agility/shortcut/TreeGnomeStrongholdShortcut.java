package com.zenyte.game.content.skills.agility.shortcut;

import com.zenyte.game.content.achievementdiary.diaries.WesternProvincesDiary;
import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 10/05/2019 22:23
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class TreeGnomeStrongholdShortcut implements Shortcut {
    @Override
    public int getLevel(final WorldObject object) {
        return 37;
    }

    @Override
    public int[] getObjectIds() {
        return new int[] {
                16534, 16535
        };
    }
    private static final Animation CLIMB = new Animation(740);

    @Override
    public int getDuration(final boolean success, final WorldObject object) {
        return 8;
    }

    @Override
    public Location getRouteEvent(final Player player, final WorldObject object) {
        return object.getId() == 16534 ? new Location(2486, 3515, 0) : new Location(2489, 3521, 0);
    }

    @Override
    public void startSuccess(final Player player, final WorldObject object) {
        val time = object.getId() == 16534 ? 3 : 5;
        Location destination = new Location(2489, 3516, 0);
        ForceMovement forceMovement = new ForceMovement(new Location(2489, 3516, 0), 30 * time,
                ForceMovement.SOUTH);
        player.setAnimation(CLIMB);
        player.setForceMovement(forceMovement);
        WorldTasksManager.schedule(() -> {
            player.setLocation(destination);
            val t = object.getId() == 16534 ? 5 : 3;
            val dest = object.getId() != 16534 ? new Location(2486, 3515, 0) : new Location(2489, 3521, 0);
            val fm = new ForceMovement(dest, t * 30, ForceMovement.SOUTH);
            player.setAnimation(CLIMB);
            player.setForceMovement(fm);
            WorldTasksManager.schedule(() -> {
                player.getAchievementDiaries().update(WesternProvincesDiary.TAKE_GRAND_TREE_AGILITY_SHORTCUT);
                player.setAnimation(Animation.STOP);
                player.setLocation(dest);
            }, t - 1);
        }, time - 1);
    }

    @Override
    public double getSuccessXp(final WorldObject object) {
        return 0;
    }
}
