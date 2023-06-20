package com.zenyte.game.content.skills.crafting.actions;

import com.zenyte.game.content.vote.BoosterPerks;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 23 dec. 2017 : 18:56:41
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
@RequiredArgsConstructor
public class MoltenGlassCrafting extends Action {

	private static final Animation ANIMATION = Animation.SMITH;
	private static final double EXPERIENCE = 20;
	public static final Item MOLTEN_GLASS = new Item(1775);
	private static final Item SODA_ASH = new Item(1781);
	private static final Item BUCKET_OF_SAND = new Item(1783);

	private final int amount;
	private int cycle;

	@Override
	public boolean start() {
		return player.getInventory().containsItems(SODA_ASH, BUCKET_OF_SAND);
	}

	@Override
	public boolean process() {
		if (!player.getInventory().containsItems(SODA_ASH, BUCKET_OF_SAND)) {
			return false;
		}
		if (cycle >= amount) {
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay() {		
		player.setAnimation(ANIMATION);
		player.getInventory().deleteItemsIfContains(new Item[] { SODA_ASH, BUCKET_OF_SAND },  () -> {
			player.getInventory().addItem(MOLTEN_GLASS);
			player.getSkills().addXp(Skills.CRAFTING, EXPERIENCE);
			player.sendFilteredMessage("You heat the sand and soda ash in the furnace to make glass.");
		}); 
		cycle++;
		return BoosterPerks.isActive(player, BoosterPerks.CRAFTING) ? (Utils.random(100) <= 5 ? 0 : 1) : 1;
	}

}
