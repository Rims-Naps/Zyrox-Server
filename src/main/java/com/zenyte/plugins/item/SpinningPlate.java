package com.zenyte.plugins.item;

import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Emote;
import lombok.val;

/**
 * @author Kris | 15/05/2019 22:50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SpinningPlate extends ItemPlugin {
    @Override
    public void handle() {
        bind("Spin", (player, item, slotId) -> {
            val emoteDelay = player.getNumericTemporaryAttribute("emote_delay").longValue();
            if (emoteDelay > Utils.currentTimeMillis()) {
                return;
            }
            val animation = new Animation(1902 + Utils.random(5));
            val duration = animation.getCeiledDuration();
            val nextEmote = animation.getId() <= 1903 ? Emote.CHEER : Emote.CRY;
            val nextDuration = nextEmote.getAnimation().getCeiledDuration();
            player.addTemporaryAttribute("emote_delay", Utils.currentTimeMillis() + duration + nextDuration);
            player.setAnimation(animation);
            val location = player.getLocation().getPositionHash();
            WorldTasksManager.schedule(() -> {
                if (player.getLocation().getPositionHash() != location) {
                    return;
                }
                player.setAnimation(nextEmote.getAnimation());
            }, duration / 600);
        });
    }

    @Override
    public int[] getItems() {
        return new int[] {
                4613
        };
    }
}
