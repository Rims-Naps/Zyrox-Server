package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.SetAppearanceEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Kris | 27. veebr 2018 : 2:00.50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 * profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 * profile</a>}
 */
public final class SetAppearanceDecoder implements ClientProtDecoder<SetAppearanceEvent> {

    @Override
    public SetAppearanceEvent decode(Player player, int opcode, RSBuffer buffer) {
        val gender = buffer.readUnsignedByte();
        val appearance = new short[7];
        for (int i = 0; i < 7; i++) {
            if (i == 1 && gender == 1) {
                appearance[i] = 1000;
                buffer.readByte();
                continue;
            }
            appearance[i] = buffer.readUnsignedByte();
        }
        val colours = new byte[] { buffer.readByte(), buffer.readByte(), buffer.readByte(), buffer.readByte(), buffer.readByte() };
        return new SetAppearanceEvent(gender, appearance, colours);
    }
}
