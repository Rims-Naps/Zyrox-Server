package com.zenyte.plugins.dialogue;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Cresinkel
 */
public class BertD extends Dialogue {

	public BertD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		npc("'Ello there " + player.getName() + "!");
		options(TITLE, "Did you already give me some sand today?", "I'll see you another time.").onOptionOne(() -> setKey(5));
		player(5, "Did you already give me some sand today?");
		val success = canGiveSand(player);
		if (success) {
			npc("No I have not, I will get right to it");
			item(ItemId.BUCKET_OF_SAND, "Bert drops off some buckets of sand to your bank.");
			giveSand(player);
		} else  {
			npc("Yep, I dropped it off a while ago. Mebbe come back tomorrow fer some more.");
		}
	}

	private void giveSand(@NotNull Player player) {
		if (!player.getAttributes().containsKey("DAILY_SAND")) {
			for (int i = 84; i > 0; --i) {
				player.getBank().add(new Item(1784));
			}
			player.getAttributes().put("DAILY_SAND", 1);
		}
	}

	private boolean canGiveSand(@NotNull Player player) {
		return !player.getAttributes().containsKey("DAILY_SAND");
	}
}
