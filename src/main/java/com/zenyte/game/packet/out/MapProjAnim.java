package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.Position;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:31:35
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class MapProjAnim implements GamePacketEncoder {
	
	private final Player player;
	private final Projectile projectile;
	private final Position fromPosition;
	private final Position target;
	
	private final int duration, offset;

    @Override
    public void log(@NotNull final Player player) {
        val fromTile = fromPosition.getPosition();
        val toTile = target.getPosition();
        this.log(player, "Id: " + projectile.getGraphicsId() + ", height: " + projectile.getStartHeight() + " -> " + projectile.getEndHeight() + ", delay: " + projectile.getDelay() +
                ", angle: " + projectile.getAngle() + ", duration: " + projectile.getDuration() + ", offset: " + projectile.getDistanceOffset() + ", multiplier: " + projectile.getMultiplier() + ", tile: [" + fromTile.getX() + ", " + fromTile.getY() + ", " + fromTile.getPlane() + "] -> [" + toTile.getX() + ", " + toTile.getY() + ", " + toTile.getPlane() + "]");
    }

    public MapProjAnim(final Player player, final Position senderTile, final Position receiverObject,
                       final Projectile projectile, final int duration, final int offset) {
		this.player = player;
		this.projectile = projectile;
		target = receiverObject;
		fromPosition = senderTile;
		this.duration = duration;
		this.offset = offset;
	}

	@Override
	public GamePacketOut encode() {
	    val prot = ServerProt.MAP_PROJANIM;
		val buffer = new RSBuffer(prot);
		val lastTile = player.getLastLoadedMapRegionTile();
		val from = fromPosition.getPosition();
		val to = target.getPosition();

        val srcHash = ((from.getLocalX(lastTile) & 0x7) << 4) | (from.getLocalY(lastTile) & 0x7);
		val delay = projectile.getDelay();
		val projSpeed = duration != Integer.MIN_VALUE ? duration : projectile.getProjectileDuration(from, target);
		val index = target instanceof Player ? (-(((Player) target).getIndex() + 1)) : target instanceof NPC ? (((NPC) target).getIndex() + 1) : 0;

		buffer.write128Byte(projectile.getEndHeight());
		buffer.writeShort(projSpeed);
		buffer.writeShort128(projectile.getGraphicsId());
		buffer.write128Byte(projectile.getStartHeight());
		buffer.writeShortLE(index);
		buffer.writeByte(projectile.getAngle());
		buffer.write128Byte(offset);
		buffer.writeShortLE(delay);
		buffer.writeByte128(srcHash);
		buffer.writeByte128((to.getY() - from.getY()));
		buffer.writeByteC((to.getX() - from.getX()));
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
