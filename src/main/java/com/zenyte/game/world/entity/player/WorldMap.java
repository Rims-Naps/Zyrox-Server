package com.zenyte.game.world.entity.player;

import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.ui.PaneType;
import com.zenyte.game.util.AccessMask;

import lombok.Getter;
import lombok.Setter;

public class WorldMap {

	private final Player player;
	@Getter
	@Setter
	private PaneType previousPane;
	@Getter
	@Setter
	private boolean visible, fullScreen;

	public WorldMap(final Player player) {
		this.player = player;
	}

	public void updateLocation() {
		player.getPacketDispatcher().sendClientScript(1749, player.getLocation().getPositionHash());
	}

	public void close() {
		visible = false;
		fullScreen = false;
		if (player.getInterfaceHandler().getPane().equals(PaneType.FULL_SCREEN)) {
			player.getInterfaceHandler().sendPane(PaneType.FULL_SCREEN, player.getWorldMap().getPreviousPane());
		}
		player.getInterfaceHandler().closeInterface(InterfacePosition.WORLD_MAP);
	}

	public void sendFullScreenWorldMap() {
		visible = true;
		previousPane = player.getInterfaceHandler().getPane();
		updateLocation();
		player.getInterfaceHandler().sendPane(previousPane, PaneType.FULL_SCREEN);
		player.getInterfaceHandler().sendInterface(InterfacePosition.WORLD_MAP, 595);
		player.getInterfaceHandler().sendInterface(594, 27, PaneType.FULL_SCREEN, false);
		player.getPacketDispatcher().sendComponentSettings(595, 17, 0, 4, AccessMask.CLICK_OP1);
	}

	public void sendFloatingWorldMap() {
		visible = true;
		updateLocation();
		player.getInterfaceHandler().sendInterface(InterfacePosition.WORLD_MAP, 595);
		player.getPacketDispatcher().sendComponentSettings(595, 17, 0, 4, AccessMask.CLICK_OP1);
	}

}
