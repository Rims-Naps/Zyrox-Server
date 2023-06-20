package com.zenyte.game.content.area.abandonedmine.object;

import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public class AbandonedMineTunnelObject implements ObjectAction {

    private static final Animation CRAWL = new Animation(844, 20);
    private static final Location MINE_ENTRANCE_INSIDE = new Location(3436, 9637, 0);
    private static final Location MINE_ENTRANCE_OUTSIDE = new Location(3441, 3232, 0);
    private static final Location MINE_EXIT_INSIDE = new Location(3405, 9631, 0);
    private static final Location MINE_EXIT_OUTSIDE = new Location(3429, 3233, 0);
    private static final Location MINE_EXIT2_INSIDE = new Location(3409, 9623, 0);
    private static final Location MINE_EXIT2_OUTSIDE = new Location(3428, 3225, 0);
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if(option.equals("Crawl-down")) {
            WorldTasksManager.schedule(new WorldTask() {
                private int ticks;

                @Override
                public void run() {
                    if (ticks == 0) {
                        player.setAnimation(CRAWL);
                    } else if (ticks == 1) {
                        player.setLocation(object.getId() == 4913 ? MINE_ENTRANCE_INSIDE : (object.getId() == 4914 ? MINE_EXIT_INSIDE : MINE_EXIT2_INSIDE));
                        player.setAnimation(Animation.STOP);
                        stop();
                    }
                    ticks++;
                }

            }, 0, 0);
        } else if(option.equals("Crawl-through")) {
            player.faceObject(object);
            WorldTasksManager.schedule(new WorldTask() {
                private int ticks;

                @Override
                public void run() {
                    if (ticks == 0) {
                        player.setAnimation(CRAWL);
                    } else if (ticks == 1) {
                        player.setLocation(object.getId() == 4920 ? MINE_ENTRANCE_OUTSIDE : (object.getId() == 4921 ? MINE_EXIT_OUTSIDE : MINE_EXIT2_OUTSIDE) );
                        player.setAnimation(Animation.STOP);
                        stop();
                    }
                    ticks++;
                }
            }, 0, 0);
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 4913, 4914, 4915, 4920, 4921, 15830};
    }
}
