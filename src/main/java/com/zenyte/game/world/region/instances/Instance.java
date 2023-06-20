package com.zenyte.game.world.region.instances;

import com.zenyte.game.world.entity.Location;

/**
 * @author Kris | 23. jaan 2018 : 20:17.28
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 *      profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 *      profile</a>}
 */
public interface Instance {
	
	public int getMaxPlayers();

	public int getMinimumCombat();

	public InstanceSpeed getSpawnSpeed();

	public InstanceProtection getProtection();

	public Location getEntranceLocation();
	
	public Location getExitLocation();
	
	public void process();
	
}
