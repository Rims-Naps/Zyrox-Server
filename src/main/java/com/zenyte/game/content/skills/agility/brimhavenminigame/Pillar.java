package com.zenyte.game.content.skills.agility.brimhavenminigame;

import com.zenyte.game.content.achievementdiary.diaries.ArdougneDiary;
import com.zenyte.game.content.skills.agility.MarkOfGrace;
import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.content.skills.agility.ardougnerooftop.ArdougneRooftopCourse;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Cresinkel
 */
public class Pillar implements Shortcut {

    private static final Location START_LOC_1 = new Location(2805, 9577, 3);
    private static final Location START_LOC_2 = new Location(2805, 9570, 3);
    private static final Location START_LOC_3 = new Location(2761, 9555, 3);
    private static final Location START_LOC_4 = new Location(2761, 9548, 3);
    private static final Location START_LOC_5 = new Location(2785, 9568, 3);
    private static final Location START_LOC_6 = new Location(2792, 9568, 3);

    @Override
    public void startSuccess(final Player player, final WorldObject object) {
        player.setFaceLocation(object);
        int forcemovement = 0;
        if (player.getLocation().withinDistance(2805,9579, 4)) {
            forcemovement = 1;
        } else if (player.getLocation().withinDistance(2805,9568, 4)){
            forcemovement = 2;
        } else if (player.getLocation().withinDistance(2761,9557, 4)){
            forcemovement = 3;
        } else if (player.getLocation().withinDistance(2761,9546, 4)){
            forcemovement = 4;
        } else if (player.getLocation().withinDistance(2783,9568, 4)){
            forcemovement = 5;
        } else if (player.getLocation().withinDistance(2794,9568, 4)) {
            forcemovement = 6;
        }
        int finalForcemovement = forcemovement;
        WorldTasksManager.schedule(new WorldTask() {
            int ticks;
            @Override
            public void run() {
                switch (ticks++) {
                    case 0:
                    case 2:
                    case 4:
                    case 6:
                    case 8:
                    case 10:
                    case 12:
                        player.setAnimation(Animation.JUMP);
                        player.sendSound(new SoundEffect(2461, 10, 64));
                        break;
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 9:
                    case 11:
                    case 13:
                        if (finalForcemovement == 1 || finalForcemovement == 3) {
                            player.setLocation(player.getLocation().transform(0, -1, 0));
                        } else if (finalForcemovement == 2 || finalForcemovement == 4){
                            player.setLocation(player.getLocation().transform(0, +1, 0));
                        } else if (finalForcemovement == 5) {
                            player.setLocation(player.getLocation().transform(+1, 0, 0));
                        } else {
                            player.setLocation(player.getLocation().transform(-1, 0, 0));
                        }
                        break;
                }
            }
        }, 1, 0);
    }

    @Override
    public Location getRouteEvent(final Player player, final WorldObject object) {
        if (player.getLocation().withinDistance(2805,9579, 4)) {
            return START_LOC_1;
        } else if (player.getLocation().withinDistance(2805,9568, 4)){
            return START_LOC_2;
        } else if (player.getLocation().withinDistance(2761,9557, 4)){
            return START_LOC_3;
        } else if (player.getLocation().withinDistance(2761,9546, 4)){
            return START_LOC_4;
        } else if (player.getLocation().withinDistance(2783,9568, 4)){
            return START_LOC_5;
        } else {
            return START_LOC_6;
        }
    }

    @Override
    public double getSuccessXp(WorldObject object) {
        return 18;
    }

    @Override
    public int getLevel(WorldObject object) {
        return 1;
    }

    @Override
    public int[] getObjectIds() {
        return new int[] { 3578 };
    }

    @Override
    public int getDuration(boolean success, WorldObject object) {
        return 14;
    }

}
