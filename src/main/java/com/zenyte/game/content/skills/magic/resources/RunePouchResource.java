package com.zenyte.game.content.skills.magic.resources;

import com.zenyte.game.content.skills.magic.RuneContainer;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;

import lombok.val;

/**
 * @author Kris | 7. juuli 2018 : 01:28:05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class RunePouchResource implements RuneResource {

	private static final Item RUNE_POUCH = new Item(12791);
	
	@Override
	public RuneContainer getContainer() {
		return RuneContainer.RUNE_POUCH;
	}

	@Override
	public int getAmountOf(final Player player, final int runeId, final int amountRequired) {
		if (!player.getInventory().containsItem(RUNE_POUCH)) {
			return 0;
		}
		return Math.min(player.getRunePouch().getAmountOf(runeId), amountRequired);
	}

	@Override
	public void consume(final Player player, final int runeId, final int amount) {
		val pouch = player.getRunePouch();
		pouch.getContainer().remove(new Item(runeId, amount));
		pouch.getContainer().refresh(player);
	}

	@Override
	public int[] getAffectedRunes() {
		return null;
	}

}
