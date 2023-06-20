package com.zenyte.game.packet.out;

import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.XTEALoader;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 13:48:56
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class RebuildNormal implements GamePacketEncoder {

	private final Player player;

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "Tile: x: " + player.getX() + ", y: " + player.getY() + ", z: " + player.getPlane());
    }

    @Override
	public boolean prioritized() {
        return true;
    }

	@Override
	public GamePacketOut encode() {
	    val prot = ServerProt.REBUILD_NORMAL;
		val buffer = new RSBuffer(prot);
		player.setForceReloadMap(false);
		val needUpdate = player.isRunning();
		if (!needUpdate) {
			player.getPlayerViewport().init(buffer);
		}
		val location = player.getLocation();
		val mapX = location.getRegionX();
		val mapY = location.getRegionY();

		val isTutIsland = (mapX == 48 || mapX == 49) && mapY == 48 || mapX == 48 && mapY == 148;

		buffer.writeShortLE128(player.getLocation().getChunkY());
		buffer.writeShort128(player.getLocation().getChunkX());
		val xteas = new IntArrayList();
		for (int xCalc = (player.getLocation().getChunkX() - 6) / 8; xCalc <= (player.getLocation().getChunkX() + 6) / 8; xCalc++) {
			for (int yCalc = (player.getLocation().getChunkY() - 6) / 8; yCalc <= (player.getLocation().getChunkY() + 6) / 8; yCalc++) {
				if (!isTutIsland || yCalc != 49 && yCalc != 149 && yCalc != 147 && xCalc != 50 && (xCalc != 49 || yCalc != 47)) {
					val region = yCalc + (xCalc << 8);
					val xtea = XTEALoader.getXTEAs(region);
                    for (int aXtea : xtea) {
                        xteas.add(aXtea);
                    }
				}
			}
		}
		buffer.writeShort(xteas.size() / 4);
		for (val i : xteas) {
			buffer.writeInt(i);
		}
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
