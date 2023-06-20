package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author Tommeh | 28 jul. 2018 | 18:47:03
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class RunClientScript implements GamePacketEncoder {

	private int scriptId;
	private Object[] arguments;
	private String type;
	private boolean blank;

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "Script: " +scriptId + ", params: " + Arrays.toString(arguments));
    }


    public RunClientScript(int scriptId, boolean blank, Object... arguments) {
		this.scriptId = scriptId;
		this.blank = blank;
		this.arguments = arguments;
		char[] chars = new char[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			chars[i] = arguments[i] instanceof String ? 's' : 'i';
			type += arguments[i] instanceof String ? arguments[i].toString().length() + 1 : 4;
		}
		type = new String(chars);
	}
	
	public RunClientScript(int scriptId, boolean blank) {
		this.scriptId = scriptId;
		this.blank = blank;
		this.arguments = null;
		this.type = "";
	}

	@Override
	public GamePacketOut encode() {
	    val prot = ServerProt.RUNCLIENTSCRIPT;
		val buffer = new RSBuffer(prot);
		buffer.writeString(blank ? "" : type);
		if (!blank) {
			for (int i = arguments.length - 1; i >= 0; i--) {
				if (arguments[i] instanceof String) {
					buffer.writeString((String) arguments[i]);
				} else {
					buffer.writeInt((Integer) arguments[i]);
				}
			}
		}
		buffer.writeInt(scriptId);
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
