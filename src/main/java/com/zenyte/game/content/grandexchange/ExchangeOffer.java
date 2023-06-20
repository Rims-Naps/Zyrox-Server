package com.zenyte.game.content.grandexchange;

import com.google.gson.annotations.Expose;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

import java.util.Optional;

/**
 * @author Tommeh | 26 nov. 2017 : 21:36:04
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
@ToString
public class ExchangeOffer {

	@Expose @Getter
	private final String username;
	@Expose @Getter
	private final Item item;
	@Expose @Getter
	private final int slot, price;
	@Expose @Getter @Setter
	private int amount;
	@Expose @Getter
	private final ExchangeType type;
	@Expose @Getter @Setter
	private boolean updated, aborted, cancelled;
	@Expose @Getter @Setter 
	private Container container;
	@Getter @Setter private int totalPrice;
	@Getter private final long time;
	@Getter private long lastUpdateTime = System.currentTimeMillis();

	public ExchangeOffer(final String username, final Item item, final int price, final int slot, final ExchangeType state) {
		this.username = username;
		this.item = item;
		this.price = price;
		this.slot = slot;
		type = state;
		container = new Container(ContainerPolicy.ALWAYS_STACK, ContainerType.GE_COLLECTABLES_CONTAINERS[slot], Optional.empty());
		time = System.currentTimeMillis();
	}

	public void refreshUpdateTime() {
		lastUpdateTime = System.currentTimeMillis();
	}
	
	public int getRemainder() {
		return item.getAmount() - amount;
	}

	public void cancel() {
		if (isCompleted()) {
			return;
		}
		if (type.equals(ExchangeType.BUYING)) {
			container.add(new Item(995, getRemainder() * price));
		} else {
			container.add(new Item(item.getId(), getRemainder()));
		}
		aborted = true;
	}

	public void refreshItems() {
		val player = World.getPlayerByUsername(username);
		if (player == null) {
			return;
		}
		container.setFullUpdate(true);
		container.refresh(player);
	}

	public boolean isCompleted() {
		return cancelled || amount >= item.getAmount();
	}

	public void updateAndInform() {
	    update();
	    inform();
    }

	public void update() {
		val player = World.getPlayerByUsername(username);
		if (player == null) {
			return;
		}
		player.getPacketDispatcher().sendGrandExchangeOffer(this);
		refreshItems();
	}

	public void inform() {
        val player = World.getPlayerByUsername(username);
        if (player == null) {
            return;
        }
        player.getMusic().playJingle(86);
        if (isCompleted()) {
            if (type == ExchangeType.BUYING) {
                player.sendMessage(Colour.RS_GREEN.wrap("Grand Exchange: Finished buying " + amount + " x " + item.getName() + "."));
            } else {
                player.sendMessage(Colour.RS_GREEN.wrap("Grand Exchange: Finished selling " + amount + " x " + item.getName() + "."));
            }
        } else {
            if (type == ExchangeType.BUYING) {
                player.sendMessage(Colour.TURQOISE.wrap("Grand Exchange: Bought " + amount + " / " + item.getAmount() + " x " + item.getName() + "."));
            } else {
                player.sendMessage(Colour.TURQOISE.wrap("Grand Exchange: Sold " + amount + " / " + item.getAmount() + " x " + item.getName() + "."));
            }
        }
    }

	public int getStage() {
		if (cancelled) {
			return 0;
		}
		if (aborted || isCompleted()) {
			return type == ExchangeType.BUYING ? 5 : 13;
		}
		return type == ExchangeType.BUYING ? 2 : 10;
	}
	
}
