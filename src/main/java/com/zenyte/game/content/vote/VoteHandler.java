package com.zenyte.game.content.vote;

import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

import java.util.List;

public class VoteHandler {

    private static final String NONE = Colour.BRICK.wrap("There are currently no votes for you to claim!");

    public static void claim(final Player player, final int amount) {
        WorldTasksManager.schedule(() -> {
            if (player == null)
                return;
            else
                player.getDialogueManager().finish();

            if (amount == 0)
                player.sendMessage(NONE);
            else
                player.sendMessage(Colour.RS_GREEN.wrap("Claimed " + amount + " votes!"));

            // todo, handle voting rewards here
        });
    }

}
