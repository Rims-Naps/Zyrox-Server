package com.zenyte.plugins.object;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.OptionDialogue;
import lombok.val;

/**
 * @author Kris | 19. veebr 2018 : 20:07.39
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class SpiralStaircaseObject implements ObjectAction {

	private static final Location homeException = new Location(3118, 3484, 0);

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
		if (option.equals("Climb")) {
			player.getDialogueManager().start(new OptionDialogue(player, "Which way do you want to go?", 
					new String[] { 
							"Climb up", 
							"Climb down", 
							"Cancel." 
					}, 
					new Runnable[] { 
							() -> {
								final int offsets[] = getUpOffsets(object.getRotation());
								player.setLocation(new Location(object.getX() + offsets[0], object.getY() + offsets[1], object.getPlane() + 1));
							},
							() -> {
								if (player.getPlane() >= 2 || object.getDefinitions().containsOption("climb")) {
									player.setLocation(new Location(player.getX(), player.getY(), object.getPlane() - 1));
								} else {
									final int offsets[] = getDownOffsets(object.getRotation());
									player.setLocation(new Location(object.getX() + offsets[0], object.getY() + offsets[1], object.getPlane() - 1));
								}
							}, null
					}));
		} else if (option.equals("Climb-up")) {
			if (object.matches(homeException)) {
				player.setLocation(new Location(3119, 3482, 1));
				return;
			}
			final int[] offsets = getUpOffsets(object.getRotation());
			player.setLocation(new Location(object.getX() + offsets[0], object.getY() + offsets[1], object.getPlane() + 1));
		} else if (option.equals("Climb-down")) {
			val defs = object.getDefinitions();
			if (defs.getSizeX() != 1 || defs.getSizeY() != 1) {
				final int[] offsets = getDownOffsets(object.getRotation());
				player.setLocation(new Location(object.getX() + offsets[0], object.getY() + offsets[1], object.getPlane() - 1));
			} else {
				player.setLocation(new Location(player.getX(), player.getY(), object.getPlane() - 1));
			}
		}
		
	}
	
	private final int[] getUpOffsets(final int rotation) {
		switch(rotation) {
		case 0:
			return new int[] { 2, 0 };
		case 1:
			return new int[] { 0, -1 };
		case 2:
			return new int[] { -1, 1 };
			default:
				return new int[] { 1, 2 };
		}
	}
	
	private final int[] getDownOffsets(final int rotation) {
		switch(rotation) {
		case 0:
			return new int[] { 1, -1 };
		case 1:
			return new int[] { -1, 0 };
		case 2:
			return new int[] { 2, 0 };
			default:
				return new int[] { 2, 1 };
		}
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 2608, 2609, 2610, 3414, 3416, 4568, 4569, 4570,
				9582, 9584, 11358, 11511, 11789, 11790, 11791, 11792, 11793, 11888, 11889, 
				11890, 12536, 12537, 12538, 14735, 14736, 14737, 16671, 16672,
				16673, 16674, 16675, 16676, 16677, 16678, 17143, 17155, 24072, 24074, 24075,
				24076, 24250, 24251, 24252, 24253, 24254, 24255, 24303, 24358,
				24359, 25682, 25683, 25801, 25935 };
	}

}
