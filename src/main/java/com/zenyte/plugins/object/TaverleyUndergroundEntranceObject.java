package com.zenyte.plugins.object;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.cutscene.FadeScreen;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Tommeh | 4 mei 2018 | 00:01:05
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class TaverleyUndergroundEntranceObject implements ObjectAction {

	private static final Location INSIDE_TILE = new Location(1310, 1237, 0);
	private static final Location OUTSIDE_TILE = new Location(2873, 9847, 0);

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
		val id = object.getId();
		player.setAnimation(new Animation(844));
        new FadeScreen(player, () -> {
            player.setLocation(id == 26566 || id == 26564 || id == 26565 ? OUTSIDE_TILE : INSIDE_TILE);
        }).fade(4);
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 26564, 26565, 26566, 26569, 26568, 26567 };
	}

}
