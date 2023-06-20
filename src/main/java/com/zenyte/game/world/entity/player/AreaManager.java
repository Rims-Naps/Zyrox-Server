package com.zenyte.game.world.entity.player;

import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.area.plugins.DeathPlugin;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Kris | 28. juuni 2018 : 20:10:05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class AreaManager {

	public AreaManager(final Player player) {
		this.player = player;
	}
	
	@SuppressWarnings("unused")
	private final transient Player player;
	@Getter @Setter private transient Area area;
	
	public boolean sendDeath(final Player player, final Entity source) {
		if (!(area instanceof DeathPlugin)) {
			return false;
		}
		return ((DeathPlugin) area).sendDeath(player, source);
	}
	
	
	
}
