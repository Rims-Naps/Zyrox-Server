package com.zenyte.game.content.area.abandonedmine.object;

import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public class CrystalMineTunnelObject implements ObjectAction {

    private static final Animation CRAWL = new Animation(844, 5);
    private static final Location MINE_ENTRANCE_INSIDE = new Location(2800, 4429, 0);
    private static final Location MINE_ENTRANCE_OUTSIDE = new Location(3435, 9635, 0);

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if(option.equals("Crawl-through")) {
            WorldTasksManager.schedule(new WorldTask() {
                private int ticks;

                @Override
                public void run() {
                    if (ticks == 0) {
                        player.setAnimation(CRAWL);
                    } else if (ticks == 1) {
                        player.setLocation(player.getLocation().getY() > 5000 ? MINE_ENTRANCE_INSIDE : MINE_ENTRANCE_OUTSIDE);
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
        return new Object[] {29332, 29333};
    }
}
