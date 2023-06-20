package com.zenyte.game.item;

import com.zenyte.game.util.Utils;
import lombok.Getter;

/**
 * An immutable class that stores item id, minimum amount, maximum amount 
 * and if necessary, the rarity.
 * @author Kris | 21. okt 2017 : 12:50.41
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class ImmutableItem {

	public ImmutableItem(final int id) {
		this(id, 1, 1, 0);
	}
	
	public ImmutableItem(final int id, final int amount) {
		this(id, amount, amount, 0);
	}
	
	public ImmutableItem(final int id, final int minAmount, final int maxAmount) {
		this(id, minAmount, maxAmount, 0);
	}
	
	public ImmutableItem(final int id, final int minAmount, final int maxAmount, final double rate) {
		this.id = id;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.rate = rate;
	}
	
	public Item generateResult() {
		return new Item(getId(), Utils.random(getMinAmount(), getMaxAmount()));
	}
	
	@Getter
	private final int id, minAmount, maxAmount;
	@Getter
	private final double rate;
	
}
