package com.zenyte.game.world.entity.player.container;

import com.zenyte.game.item.Item;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Kris | 3. mai 2018 : 19:22:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class ContainerResult {

	public ContainerResult(final Item item, final ContainerState state) {
		this.item = item == null ? null : new Item(item.getId(), item.getAmount(), item.getAttributesCopy());
		this.state = state;
	}
	
	@Getter private final Item item;
	@Getter private final ContainerState state;
	@Getter @Setter private int succeededAmount;
	@Getter @Setter private RequestResult result;
	
	public void onFailure(final ContainerFailure runnable) {
		if (result == RequestResult.SUCCESS || item.getAmount() == succeededAmount) {
			return;
		}
		runnable.execute(new Item(item.getId(), item.getAmount() - succeededAmount, item.getAttributesCopy()));
	}

	public boolean isFailure() {
		return !result.equals(RequestResult.SUCCESS) || item.getAmount() != succeededAmount;
	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("State: ").append(state.toString()).append(", ");
		builder.append("Item: ").append(item.toString()).append(", ");
		builder.append("Succeeded amount: ").append(succeededAmount).append(", ");
		builder.append("Result: ").append(result.toString()).append(".");
		return builder.toString();
	}
	
}
