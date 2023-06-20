package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.EventMouseClickEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

import java.util.function.Predicate;

/**
 * @author Tommeh | 28 jul. 2018 | 19:55:14
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class EventMouseClickDecoder implements ClientProtDecoder<EventMouseClickEvent> {

    private static final Predicate<Player> predicate = Player::isNulled;

	@Override
	public EventMouseClickEvent decode(Player player, int opcode, RSBuffer buffer) {
	    val a = buffer.readUnsignedShort();
	    val b = buffer.readUnsignedShort();
	    val c = buffer.readUnsignedShort();
	    val observers = player.getBotObservers();
	    if (!observers.isEmpty()) {
	        observers.removeIf(predicate);
	        for (val observer : observers) {
	            observer.sendMessage("Click by user " + player.getName() + ": delay: " + a + ", x: " + b + ", y: " + c);
            }
        }
		return new EventMouseClickEvent();
	}
}
