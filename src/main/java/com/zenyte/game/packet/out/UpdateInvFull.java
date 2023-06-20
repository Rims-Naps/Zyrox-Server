package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ItemContainer;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:57:47
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class UpdateInvFull implements GamePacketEncoder {

	private int key, interfaceId, componentId;
	private ItemContainer container;
	private Container newContainer;
	private ContainerType type;

    @Override
    public void log(@NotNull final Player player) {
        if (type == null) {
            return;
        }
        log(player, "Type: " + type.getName());
    }

	public UpdateInvFull(final int key, final int interfaceId, final int componentId, final ItemContainer items) {
		this.interfaceId = interfaceId;
		this.componentId = componentId;
		this.key = key;
		container = items;
	}

	public UpdateInvFull(final int key, final int interfaceId, final int componentId, final Container items) {
		this.interfaceId = interfaceId;
		this.componentId = componentId;
		this.key = key;
		newContainer = items;
	}

	public UpdateInvFull(final Container items) {
		newContainer = items;
	}

	public UpdateInvFull(final Container items, final ContainerType type) {
		newContainer = items;
		this.type = type;
	}

	@Override
	public GamePacketOut encode() {
	    val prot = ServerProt.UPDATE_INV_FULL;
		val buffer = new RSBuffer(prot);
		if (newContainer != null) {
			val type = this.type == null ? newContainer.getType() : this.type;
			if (key > 0) {
				buffer.writeInt(interfaceId << 16 | componentId);
				buffer.writeShort(key);
			} else {
				buffer.writeInt(type.getInterfaceId() << 16 | type.getComponentId());
				buffer.writeShort(type.getId());
			}
			val size = newContainer.getContainerSize();
			buffer.writeShort(size);
			for (int i = 0; i < size; i++) {
				val item = newContainer.get(i);
				buffer.writeShort(item == null ? 0 : (item.getId() + 1));
				val amount = item == null ? 0 : item.getAmount();
				buffer.writeByte(amount > 254 ? 255 : amount);
				if (amount > 254) {
					buffer.writeIntV2(amount);
				}

			}
			Container.AWAITING_RESET_CONTAINERS.add(newContainer);
			return new GamePacketOut(prot, buffer);
		}
		buffer.writeInt(interfaceId << 16 | componentId);
		buffer.writeShort(key);
		val items = container.getItems();
		val size = items.length;
		buffer.writeShort(size);
		for (int i = 0; i < size; i++) {
			val item = items[i];
			buffer.writeShort(item == null ? 0 : (item.getId() + 1));
			val amount = item == null ? 0 : item.getAmount();
			buffer.writeByte(amount > 254 ? 255 : amount);
			if (amount > 254) {
				buffer.writeIntV2(amount);
			}
		}
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
