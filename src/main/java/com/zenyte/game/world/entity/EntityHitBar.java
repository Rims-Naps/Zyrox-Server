package com.zenyte.game.world.entity;

import com.zenyte.game.world.entity.player.Player;
import lombok.val;

public class EntityHitBar extends HitBar {

	public EntityHitBar(final Entity entity) {
		this.entity = entity;
	}

	private final transient Entity entity;

	@Override
	public int getPercentage() {
		float hp = entity.getHitpoints();
		if (hp <= 0) {
			return 0;
		}
		final float maxHp = entity.getMaxHitpoints();
		if (maxHp == 0) {
			return 0;
		}
		if (hp > maxHp) {
			hp = maxHp;
		}
		val multiplier = getMultiplier();
		final float mod = maxHp / (multiplier);
		return Math.min((int) ((hp + mod) / mod), multiplier);
	}

	@Override
	public int getType() {
		switch(getSize()) {
		case 4:
			return 5;
		case 5:
			return 1;
		case 6:
			return 4;
		case 7:
			return 2;
		case 8:
			return 3;
			default:
				return 0;
		}
	}
	
	public int getMultiplier() {
		switch(getSize()) {
		case 4:
			return 60;
		case 5:
			return 100;
		case 6:
		case 7:
			return 120;
		case 8:
			return 160;
			default:
				return 30;
		}
	}

	protected int getSize() {
	    return entity.getSize();
    }
	
	@Override
	public boolean display(final Player player) {
		return true;
	}

}
