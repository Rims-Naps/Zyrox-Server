package com.zenyte.network.login.codec;

import com.zenyte.game.HardwareInfo;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Device;
import com.zenyte.network.NetworkConstants;
import com.zenyte.network.io.ByteBufUtil;
import com.zenyte.network.io.security.ISAACCipher;
import com.zenyte.network.io.security.ISAACCipherPair;
import com.zenyte.network.login.packet.LoginPacketIn;
import com.zenyte.network.login.packet.inc.LoginType;
import com.zenyte.utils.Ordinal;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.List;

/**
 * @author Tommeh | 27 jul. 2018 | 19:21:02
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>}
 */
public class LoginDecoder extends ByteToMessageDecoder {

    @Ordinal
    @AllArgsConstructor
    public enum AuthType {
        TRUSTED_COMPUTER,
        TRUSTED_AUTHENTICATION,
        NORMAL,
        UNTRUSTED_AUTHENTICATION;

        private static final AuthType[] values = values();
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        if (in.readableBytes() < 3) {
            return;
        }
        in.markReaderIndex();
        val type = LoginType.get(in.readUnsignedByte());
        val size = in.readUnsignedShort();

        if (in.readableBytes() < size) {
            return;
        }
        val version = in.readInt();
        val subVersion = in.readInt();
        //in.readUnsignedByte(); Commenting out; We didn't use this before however we use this same exact index in buffer to determine mac address length.
        val macLength = in.readUnsignedByte();
        val device = macLength == 2 ? Device.MOBILE : Device.DESKTOP;
        StringBuilder macBuilder = new StringBuilder();

        if (device.equals(Device.DESKTOP)) {
            val mac = new byte[macLength];
            for (int i = 0; i < macLength; i++) {
                mac[i] = (byte) in.readUnsignedByte();
            }
            for (int i = 0; i < mac.length; i++) {
                macBuilder.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
        }
        val rsaBuf = ByteBufUtil.encipherRSA(in, NetworkConstants.RSA_EXPONENT, NetworkConstants.RSA_MODULUS);
        val rsaKey = rsaBuf.readUnsignedByte();

        val xteaKeys = new int[4];
        val serverKeys = new int[4];
        for (int i = 0; i < 4; i++) {
            xteaKeys[i] = rsaBuf.readInt();
            serverKeys[i] = xteaKeys[i] + 50;
        }
        val serverSeed = rsaBuf.readLong();
        AuthType authType = null;
        int authenticatorCode = -1, pcIdentifier = -1;
        val previousXteaKeys = new int[4];
        String password = "";
        if (type.equals(LoginType.RECONNECT_LOGIN_CONNECTION)) {
            for (int i = 0; i < 4; i++) {
                previousXteaKeys[i] = rsaBuf.readInt();
            }
        } else {
            authType = AuthType.values[rsaBuf.readUnsignedByte()];
            if (authType == AuthType.NORMAL) {
                rsaBuf.skipBytes(4);
            } else if (authType == AuthType.UNTRUSTED_AUTHENTICATION || authType == AuthType.TRUSTED_AUTHENTICATION) {
                authenticatorCode = rsaBuf.readUnsignedMedium();
                rsaBuf.skipBytes(1);
            } else if (authType == AuthType.TRUSTED_COMPUTER) {
                pcIdentifier = rsaBuf.readInt();
            }

            rsaBuf.skipBytes(1);
            password = ByteBufUtil.readString(rsaBuf);
        }
        val xteaBuf = ByteBufUtil.decipherXTEA(in, xteaKeys);
        val username = ByteBufUtil.readString(xteaBuf);
        val clientProperties = xteaBuf.readUnsignedByte();
        val lowMemory = (clientProperties & 0x1) == 1;
        val mode = clientProperties >> 1;
        val width = xteaBuf.readUnsignedShort();
        val height = xteaBuf.readUnsignedShort();
        val cacheUID = new byte[24];
        xteaBuf.readBytes(cacheUID, 0, 24);

        val sessionToken = ByteBufUtil.readString(xteaBuf);
        val affiliateId = xteaBuf.readInt();
        val hardwareInfo = new HardwareInfo(xteaBuf);
        val supportsJs = xteaBuf.readUnsignedByte() == 1;
        if (device.equals(Device.MOBILE)) {
            xteaBuf.readUnsignedByte();
        }
        xteaBuf.readInt();
        val crc = new int[Math.min(0xFF, xteaBuf.readableBytes() / 4)];
        for (int i = 0; i < crc.length; i++) {
            crc[i] = xteaBuf.readInt();
        }
        val isaacPair = new ISAACCipherPair(new ISAACCipher(serverKeys), new ISAACCipher(xteaKeys));
        out.add(new LoginPacketIn(type, version, subVersion, Utils.formatUsername(username), password, mode, crc,
                sessionToken, authenticatorCode, pcIdentifier, authType, hardwareInfo, isaacPair, rsaKey,
                xteaKeys, previousXteaKeys, macBuilder.toString(), device));
    }

}
