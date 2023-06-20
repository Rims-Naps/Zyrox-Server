package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.FriendListDelEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 jul. 2018 | 20:09:42
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class FriendListDelDecoder implements ClientProtDecoder<FriendListDelEvent> {

    @Override
    public FriendListDelEvent decode(Player player, int opcode, RSBuffer buffer) {
        val name = buffer.readString();
        return new FriendListDelEvent(name);
    }
}
