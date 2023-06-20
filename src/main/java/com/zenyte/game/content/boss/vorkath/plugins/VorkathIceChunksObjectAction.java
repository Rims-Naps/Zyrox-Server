package com.zenyte.game.content.boss.vorkath.plugins;

import com.zenyte.game.content.boss.vorkath.VorkathInstance;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.game.world.region.dynamicregion.OutOfSpaceException;
import com.zenyte.plugins.dialogue.OptionDialogue;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 28. jaan 2018 : 19:00.52
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public final class VorkathIceChunksObjectAction implements ObjectAction {

    private static final Location OUTSIDE_TILE = new Location(2272, 4052, 0);
    private static final Animation CLIMB_OVER_WALL_ANIM = new Animation(839);

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (player.getArea() instanceof VorkathInstance) {
            player.getDialogueManager().start(new OptionDialogue(player, "Would you like to leave the instance?", new String[]{"Yes, leave it permanently.", "No, stay in it."}, new Runnable[]{() -> quit(player), null}));
            return;
        }
        WorldTasksManager.schedule(() -> {
            player.lock();
            try {
                val area = MapBuilder.findEmptyChunk(8, 8);
                val instance = new VorkathInstance(player, area);
                instance.constructRegion();
            } catch (OutOfSpaceException e) {
                log.error(Strings.EMPTY, e);
            }
        });
    }

    private void quit(final Player player) {
        val area = player.getArea();
        if (!(area instanceof VorkathInstance)) {
            return;
        }
        val instance = (VorkathInstance) area;
        player.lock();
        WorldTasksManager.schedule(new WorldTask() {
            private int ticks;

            @Override
            public void run() {
                switch (ticks++) {
                    case 0:
                        player.setAnimation(CLIMB_OVER_WALL_ANIM);
                        player.setForceMovement(new ForceMovement(player.getLocation(), 15, instance.getLocation(OUTSIDE_TILE), 45, ForceMovement.SOUTH));
                        return;
                    case 2:
                        player.setLocation(OUTSIDE_TILE);
                        player.unlock();
                        stop();
                        return;
                }
            }
        }, 0, 0);
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{31990};
    }

}
