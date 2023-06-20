package com.zenyte.network.login.packet;

import com.zenyte.game.HardwareInfo;
import com.zenyte.game.world.entity.player.Device;
import com.zenyte.network.PacketIn;
import com.zenyte.network.io.security.ISAACCipherPair;
import com.zenyte.network.login.codec.LoginDecoder;
import com.zenyte.network.login.packet.inc.LoginType;
import lombok.Getter;

/**
 * @author Tommeh | 27 jul. 2018 | 19:47:40
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
public class LoginPacketIn implements PacketIn {
	
	private final LoginType type;
	private final int version, subVersion, authenicatorCode, pcIdentifier, rsaKey;
	private final LoginDecoder.AuthType authType;
	private final String username;
	private final String password;
	private final String macAddress;
	private final int mode;
	private final int[] crc;
	private final int[] xteaKeys, previousXteaKeys;
	private final String sessionToken;
	private final HardwareInfo hardwareInfo;
	private final ISAACCipherPair isaacCipherPair;
	private final Device device;
	
	public LoginPacketIn(final LoginType type, final int version, int subVersion, final String username,
                         final String password, final int mode, final int[] crc, final String sessionToken,
                         final int authenicatorCode, final int pcIdentifier, final LoginDecoder.AuthType trusted,
                         final HardwareInfo hardwareInfo, final ISAACCipherPair isaacCipherPair, final int rsaKey,
                         final int[] xteaKeys, final int[] previousXteaKeys, final String macAddress, final Device device) {
		this.type = type;
		this.version = version;
		this.subVersion = subVersion;
		this.username = username;
		this.password = password;
		this.mode = mode;
		this.crc = crc;
		this.sessionToken = sessionToken;
		this.authenicatorCode = authenicatorCode;
		this.pcIdentifier = pcIdentifier;
		this.authType = trusted;
		this.hardwareInfo = hardwareInfo;
		this.isaacCipherPair = isaacCipherPair;
		this.rsaKey = rsaKey;
		this.xteaKeys = xteaKeys;
		this.previousXteaKeys = previousXteaKeys;
		this.macAddress = macAddress;
		this.device = device;
	}

}
