package com.zenyte.plugins.renewednpc;

import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import lombok.val;

/**
 * @author Kris | 26/11/2018 18:29
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Fungi extends NPCPlugin {

    private static final Animation FIRST_ANIM = new Animation(3335);
    private static final Animation SECOND_ANIM = new Animation(3322);

    @Override
    public void handle() {
        bind("Pick", (player, npc) -> {
            npc.lock();
            val id = npc.getId();
            val transformed = id == 7798 ? 7797 : (id + 2);
            player.setAnimation(FIRST_ANIM);
            WorldTasksManager.schedule(new WorldTask() {
                int ticks = 0;

                @Override
                public void run() {
                    if (ticks == 0) {
                        npc.setTransformation(transformed);
                        npc.setId(id);
                        npc.setAnimation(SECOND_ANIM);
                        npc.setAttackingDelay(System.currentTimeMillis());
                        npc.unlock();
                    } else if (ticks == 1) {
                        npc.setId(transformed);
                        npc.getCombat().setTarget(player);
                        stop();
                    }
                    ticks++;
                }
            }, 0, 1);
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] { 471, 472, 7798 };
    }
}
