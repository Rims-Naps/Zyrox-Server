package com.zenyte.game.packet;

import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Kris | 03/03/2019 23:42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SetTradingPost implements GamePacketEncoder {
    @Override
    public GamePacketOut encode() {
        val prot = ServerProt.SET_TRADINGPOST;
        val buffer = new RSBuffer(prot);
        buffer.writeByte(1);//init tp, else write no more bytes and TP is nulled.
        buffer.writeLong(0);
        buffer.writeLong(0);

        buffer.writeShort(0);//id
        buffer.writeByte(0);//false

        buffer.writeShort(0);//size of events

        /*
         * For each event, write:
         *       this.string1 = var1.readString();
         *       this.string2 = var1.readString();
         *       this.world = var1.method6080();
         *       this.field16 = var1.method6084();
         *       int var4 = var1.method6201();
         *       int var5 = var1.method6201();
         *       this.grandExchangeOffer = new GrandExchangeOffer();
         *       this.grandExchangeOffer.method2887(2);
         *       this.grandExchangeOffer.method2869(var2);
         */
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
