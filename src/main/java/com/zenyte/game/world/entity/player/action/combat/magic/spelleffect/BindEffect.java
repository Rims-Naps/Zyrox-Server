package com.zenyte.game.world.entity.player.action.combat.magic.spelleffect;

import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Kris | 20/03/2019 22:02
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BindEffect implements SpellEffect {

    public BindEffect(final int tickDuration) {
        this.tickDuration = tickDuration;
    }

    private final int tickDuration;

    @Override
    public void spellEffect(final Entity player, final Entity target, final int damage) {
        if (!target.freezeWithNotification(tickDuration, 5)) {
            return;
        }
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                if (ticks++ > tickDuration || target.isNulled() || (player != null && player.isNulled())) {
                    stop();
                    return;
                }
                if (target instanceof Player && player instanceof Player && target.getLocation().getDistance(player.getLocation()) > 12) {
                    target.resetFreeze();
                    stop();
                }
            }
        }, 0, 0);
    }

}