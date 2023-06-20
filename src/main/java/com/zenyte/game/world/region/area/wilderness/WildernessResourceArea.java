package com.zenyte.game.world.region.area.wilderness;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.LoginPlugin;

/**
 * @author Kris | 24. juuni 2018 : 15:45:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class WildernessResourceArea extends WildernessArea implements LoginPlugin {

	@Override
	public RSPolygon[] polygons() {
		return new RSPolygon[] { new RSPolygon(new int[][] { { 3175, 3945 }, { 3174, 3944 }, { 3174, 3925 }, { 3175, 3924 },
			{ 3196, 3924 }, { 3197, 3925 }, { 3197, 3944 }, { 3196, 3945 } }, 0) };
	}
	
	@Override
	public void login(final Player player) {
		player.sendMessage("Mandrith removed you from the arena. You will need to pay him again to regain entry.");
		player.setLocation(new Location(3184, 3945, 0));
	}

	@Override
	public String name() {
		return "Wilderness Resource Area";
	}

}
