package com.zenyte.game.packet.out;

import com.zenyte.game.content.grandexchange.ExchangeOffer;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;

import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:15:48
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class GrandExchangeOffer implements GamePacketEncoder {
    @Override
    public void log(@NotNull final Player player) {
        log(player, "Slot: " + offer.getSlot() + ", stage: " + offer.getStage() + ", id: " + offer.getItem().getId() + ", item amount: " + offer.getItem().getAmount() + ", price:" +
                " " + offer.getPrice() + ", total amount: " + offer.getAmount() + ", total price: " + offer.getTotalPrice());
    }

    private final ExchangeOffer offer;

	@Override
	public GamePacketOut encode() {
	    val prot = ServerProt.GRAND_EXCHANGE_OFFER;
		val buffer = new RSBuffer(prot);
		buffer.writeByte(offer.getSlot());
		buffer.writeByte(offer.getStage());
		buffer.writeShort(offer.getItem().getId());
		buffer.writeInt(offer.getPrice());
		buffer.writeInt(offer.getItem().getAmount());
		buffer.writeInt(offer.getAmount());
		buffer.writeInt(offer.getTotalPrice());
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
