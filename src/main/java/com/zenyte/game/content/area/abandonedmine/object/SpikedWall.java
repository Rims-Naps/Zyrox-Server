package com.zenyte.game.content.area.abandonedmine.object;

import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public class SpikedWall implements ObjectAction {


    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        player.lock(4);
        WorldTasksManager.schedule(new WorldTask() {
            int ticks = 0;
            @Override
            public void run() {
                if(ticks == 0) {
                    player.setAnimation(new Animation(881));
                    player.sendMessage("You search the wall for traps...");
                }
                if(ticks == 2) {
                    player.sendMessage("... and succeed! You quickly walk past.");
                }
                if(ticks == 3) {
                    SpikeTrap trap = SpikeTrap.getFromCoords(object.getX(), object.getY());
                    player.addWalkSteps(trap.forceTo.getX(), trap.forceTo.getY(), -1, false);
                    stop();
                }
                ticks++;
            }
        }, 0, 0);
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {20588, 20596};
    }
}
