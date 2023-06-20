package com.zenyte.plugins.item;

import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import lombok.val;

/**
 * @author Kris | 15/05/2019 23:36
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Marionette extends ItemPlugin {
    @Override
    public void handle() {
        bind("Jump", (player, item, slotId) -> {
            val emoteDelay = player.getNumericTemporaryAttribute("emote_delay").longValue();
            if (emoteDelay > Utils.currentTimeMillis()) {
                return;
            }
            val animation = new Animation(3003);
            player.setAnimation(animation);
            val id = item.getId();
            val graphics = new Graphics(507 + (id == 6867 ? 0 : id == 6865 ? 4 : 8));
            player.setGraphics(graphics);
            player.addTemporaryAttribute("emote_delay", Utils.currentTimeMillis() + animation.getCeiledDuration());
        });
        bind("Walk", (player, item, slotId) -> {
            val emoteDelay = player.getNumericTemporaryAttribute("emote_delay").longValue();
            if (emoteDelay > Utils.currentTimeMillis()) {
                return;
            }
            val animation = new Animation(3004);
            player.setAnimation(animation);
            val id = item.getId();
            val graphics = new Graphics(508 + (id == 6867 ? 0 : id == 6865 ? 4 : 8));
            player.setGraphics(graphics);
            player.addTemporaryAttribute("emote_delay", Utils.currentTimeMillis() + animation.getCeiledDuration());
        });
        bind("Bow", (player, item, slotId) -> {
            val emoteDelay = player.getNumericTemporaryAttribute("emote_delay").longValue();
            if (emoteDelay > Utils.currentTimeMillis()) {
                return;
            }
            val animation = new Animation(3005);
            player.setAnimation(animation);
            val id = item.getId();
            val graphics = new Graphics(509 + (id == 6867 ? 0 : id == 6865 ? 4 : 8));
            player.setGraphics(graphics);
            player.addTemporaryAttribute("emote_delay", Utils.currentTimeMillis() + animation.getCeiledDuration());
        });
        bind("Dance", (player, item, slotId) -> {
            val emoteDelay = player.getNumericTemporaryAttribute("emote_delay").longValue();
            if (emoteDelay > Utils.currentTimeMillis()) {
                return;
            }
            val animation = new Animation(3006);
            player.setAnimation(animation);
            val id = item.getId();
            val graphics = new Graphics(510 + (id == 6867 ? 0 : id == 6865 ? 4 : 8));
            player.setGraphics(graphics);
            player.addTemporaryAttribute("emote_delay", Utils.currentTimeMillis() + animation.getCeiledDuration());
        });
    }

    @Override
    public int[] getItems() {
        return new int[] {
                6865, 6866, 6867
        };
    }
}
