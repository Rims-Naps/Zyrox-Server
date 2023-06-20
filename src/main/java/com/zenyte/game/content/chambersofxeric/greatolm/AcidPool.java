package com.zenyte.game.content.chambersofxeric.greatolm;

import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.object.WorldObject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Kris | 15. jaan 2018 : 21:16.16
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * The acid pool object that Olm shoots out through {@link com.zenyte.game.content.chambersofxeric.greatolm.scripts.AcidSpray}
 * & {@link com.zenyte.game.content.chambersofxeric.greatolm.scripts.AcidDrip} attacks.
 */
@EqualsAndHashCode(callSuper = true)
public final class AcidPool extends WorldObject {

	public AcidPool(final Location tile) {
		super(30032, 10, 0, tile);
	}

    /**
     * The duration of the acid pool; By nature, the pools only remain on the ground for 23 ticks.
     */
	@Getter private int ticks = 17;
	
	public boolean process() {
		switch(--ticks) {
		case 16:
			World.spawnObject(this);
			return true;
		case 0:
			World.removeObject(this);
			return false;
			default:
				return true;
		}
	}

}
