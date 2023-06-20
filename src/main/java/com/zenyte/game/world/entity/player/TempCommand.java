package com.zenyte.game.world.entity.player;

import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;

/**
 * @author Kris | 06/04/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class TempCommand {

    public static void run(final Player p, final String[] args) {
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                World.sendMessage(MessageType.FILTERABLE, "period one");
                if (ticks >= 50) {
                    stop();
                }
                ticks++;
            }
        },0,1);
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                World.sendMessage(MessageType.FILTERABLE, "period two");
                if (ticks >= 50) {
                    stop();
                }
                ticks++;
            }
        },0,0);
    }

}
