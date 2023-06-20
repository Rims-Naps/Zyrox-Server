package com.zenyte.game.content.skills.agility;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;

/**
 * An agility object that can be succeded.
 */
public interface Succeedable {

	/**
	 * Defines success behaviour of an agility object.
	 * @param player the player interacting with the agility object
	 * @param object the object player is interacting with.
     */
	public void startSuccess(final Player player, final WorldObject object);
	public default void endSuccess(final Player player, final WorldObject object) {
		
	}
	
	public double getSuccessXp(final WorldObject object);
	

}
