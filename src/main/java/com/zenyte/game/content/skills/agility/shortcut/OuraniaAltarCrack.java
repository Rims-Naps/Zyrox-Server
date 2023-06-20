package com.zenyte.game.content.skills.agility.shortcut;

import com.zenyte.game.MinimapState;
import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.cutscene.FadeScreen;
import com.zenyte.game.world.object.WorldObject;

import java.util.HashMap;
import java.util.Map;

public class OuraniaAltarCrack implements Shortcut {

	@Override
	public int getLevel(final WorldObject object) {
		return 0;
	}

	@Override
	public int[] getObjectIds() {
		return new int[] {
				29626,
				29627
		};
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 2;
	}

	private static final Animation ENTER = new Animation(746);
	private static final Animation EXIT = new Animation(748);

	@Override
	public void startSuccess(final Player player, final WorldObject object) {
		player.faceObject(object);
		Location destination;
		destination = player.getX() <= 3053 ? new Location(3056, 5585, 0) : new Location(3052, 5588, 0);
		player.getPacketDispatcher().sendMinimapState(MinimapState.MAP_DISABLED);
		new FadeScreen(player, () -> {
			player.getPacketDispatcher().sendMinimapState(MinimapState.ENABLED);
			player.setLocation(destination);
		}).fade(3);
	}

	@Override
	public String getEndMessage(final boolean success) {
		return success ? "You squeeze through the crack." : null;
	}


	@Override
	public double getSuccessXp(final WorldObject object) {
		return 0;
	}
}

