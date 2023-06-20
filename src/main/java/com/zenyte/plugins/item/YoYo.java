package com.zenyte.plugins.item;

import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import lombok.val;

/**
 * @author Kris | 15/05/2019 23:32
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class YoYo extends ItemPlugin {
    @Override
    public void handle() {
        bind("Play", (player, item, slotId) -> {
            val emoteDelay = player.getNumericTemporaryAttribute("emote_delay").longValue();
            if (emoteDelay > Utils.currentTimeMillis()) {
                return;
            }
            val animation = new Animation(1457);
            player.setAnimation(animation);
            player.addTemporaryAttribute("emote_delay", Utils.currentTimeMillis() + animation.getCeiledDuration());
        });
        bind("Loop", (player, item, slotId) -> {
            val emoteDelay = player.getNumericTemporaryAttribute("emote_delay").longValue();
            if (emoteDelay > Utils.currentTimeMillis()) {
                return;
            }
            val animation = new Animation(1458);
            player.setAnimation(animation);
            player.addTemporaryAttribute("emote_delay", Utils.currentTimeMillis() + animation.getCeiledDuration());
        });
        bind("Walk", (player, item, slotId) -> {
            val emoteDelay = player.getNumericTemporaryAttribute("emote_delay").longValue();
            if (emoteDelay > Utils.currentTimeMillis()) {
                return;
            }
            val animation = new Animation(1459);
            player.setAnimation(animation);
            player.addTemporaryAttribute("emote_delay", Utils.currentTimeMillis() + animation.getCeiledDuration());
        });
        bind("Crazy", (player, item, slotId) -> {
            val emoteDelay = player.getNumericTemporaryAttribute("emote_delay").longValue();
            if (emoteDelay > Utils.currentTimeMillis()) {
                return;
            }
            val animation = new Animation(1460);
            player.setAnimation(animation);
            player.addTemporaryAttribute("emote_delay", Utils.currentTimeMillis() + animation.getCeiledDuration());
        });
    }

    @Override
    public int[] getItems() {
        return new int[] {
                4079, 10733
        };
    }
}
