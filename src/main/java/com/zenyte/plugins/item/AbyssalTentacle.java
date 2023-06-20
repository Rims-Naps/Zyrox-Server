package com.zenyte.plugins.item;

import static com.zenyte.plugins.itemonitem.AbyssalTentacleItemCreationAction.ABYSSAL_TENTACLE;
import static com.zenyte.plugins.itemonitem.AbyssalTentacleItemCreationAction.KRAKEN_TENTACLE;

import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Kris | 25. aug 2018 : 21:49:15
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class AbyssalTentacle extends ItemPlugin {

	@Override
	public void handle() {
		bind("Dissolve", (player, item, slotId) -> {
			player.getDialogueManager().start(new Dialogue(player) {
				@Override
				public void buildDialogue() {
					plain("<col=FF0040>Warning!</col><br>Once the abyssal tentacle is dissolved, you only to get the kraken tentacle back making you lose the abyssal whip.");
					options("Are you sure you wish to do this?", "Yes, dissolve the abyssal tentacle", "No, I'll keep my abyssal tentacle.")
					.onOptionOne(() -> {
						player.getInventory().deleteItem(ABYSSAL_TENTACLE);
						player.getInventory().addItem(KRAKEN_TENTACLE);
					});
				}
			});
		});
	}

	@Override
	public int[] getItems() {
		return new int[] { 12006 };
	}

}
