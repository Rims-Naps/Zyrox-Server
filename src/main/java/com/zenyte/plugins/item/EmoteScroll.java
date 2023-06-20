package com.zenyte.plugins.item;

import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Emote;
import com.zenyte.plugins.dialogue.OptionsMenuD;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Kris | 15/05/2019 23:49
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class EmoteScroll extends ItemPlugin {

    private static final Emote[] unlockableEmotes = new Emote[] {
            Emote.GLASS_BOX, Emote.CLIMB_ROPE, Emote.LEAN, Emote.GLASS_WALL,
            Emote.ZOMBIE_WALK, Emote.ZOMBIE_DANCE, Emote.SCARED, Emote.RABBIT_HOP, Emote.TRICK
    };

    @Override
    public void handle() {
        bind("Read", (player, item, inventorySlot) -> {
            val options = new ArrayList<Emote>();
            for (val unlockableEmote : unlockableEmotes) {
                if (player.getEmotesHandler().isUnlocked(unlockableEmote)) {
                    continue;
                }
                options.add(unlockableEmote);
            }
            if (options.isEmpty()) {
                player.sendMessage("You've already unlocked all the current unlockable options.");
                return;
            }
            val optionNameList = new ArrayList<String>(options.size());
            for (val option : options) {
                optionNameList.add(Utils.formatString(option.toString()));
            }
            player.getDialogueManager().start(new OptionsMenuD(player, "Select an emote to unlock", optionNameList.toArray(new String[0])) {
                @Override
                public void handleClick(final int slotId) {
                    if (player.getInventory().getItem(inventorySlot) != item) {
                        return;
                    }
                    player.getInventory().deleteItem(item);
                    player.getEmotesHandler().unlock(options.get(slotId));
                    if(options.get(slotId).getSlot() == 48) {
                        player.addAttribute("Halloween event 2019", 1);
                    }
                    player.sendMessage(Colour.RS_GREEN.wrap("Congratulations, you've unlocked the " + optionNameList.get(slotId) + " emote!"));
                }

                @Override
                public boolean cancelOption() {
                    return true;
                }
            });
        });
    }

    @Override
    public int[] getItems() {
        return new int[] {
                2678
        };
    }
}
