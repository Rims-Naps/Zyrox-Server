package com.zenyte.game.world.entity.masks;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kris | 6. nov 2017 : 14:36.04
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public final class RenderAnimation implements RenderType {

	public static final int STAND = 808, STAND_TURN = 823, WALK = 819, ROTATE180 = 820, ROTATE90 = 821, ROTATE270 =
            822, RUN = 824;
	public static final RenderAnimation DEFAULT_RENDER = new RenderAnimation(STAND, STAND_TURN, WALK, ROTATE180, ROTATE90, ROTATE270, RUN);
	
	@Getter private final int stand, standTurn, walk, rotate180, rotate90, rotate270, run;
	
	public RenderAnimation(final int stand, final int walk, final int run) {
		this(stand, STAND_TURN, walk, ROTATE180, ROTATE90, ROTATE270, run);
	}
	
}
