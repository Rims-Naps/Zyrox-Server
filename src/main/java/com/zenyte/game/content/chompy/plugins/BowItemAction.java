package com.zenyte.game.content.chompy.plugins;

import com.zenyte.game.content.chompy.Chompy;
import com.zenyte.game.content.chompy.ChompyBirdHat;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Christopher
 * @since 3/6/2020
 */
public class BowItemAction extends ItemPlugin {
    @Override
    public void handle() {
        bind("Check", (player, item, container, slotId) -> check(player));
        bind("Check kills", (player, item, container, slotId) -> check(player));
    }

    private void check(final Player player) {
        val kills = player.getNumericAttribute(Chompy.KILL_ATTRIB).intValue();
        val hat = ChompyBirdHat.getBest(kills);
        player.sendMessage("You've scratched up a total of " + kills + " chompy bird kills so far!");
        if (hat != null) {
            val title = hat.getTitle();
            val article = (Utils.startWithVowel(title) ? "an" : "a");
            player.sendMessage("~ You're " + article + " " + title + "! ~");
        }
    }

    @Override
    public int[] getItems() {
        return new int[]{ItemId.OGRE_BOW, ItemId.COMP_OGRE_BOW};
    }
}
