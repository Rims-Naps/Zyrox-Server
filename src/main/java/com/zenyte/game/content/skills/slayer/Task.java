package com.zenyte.game.content.skills.slayer;

import com.zenyte.game.world.region.Area;
import lombok.Getter;

/**
 * @author Kris | 5. nov 2017 : 21:22.52
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
@Getter
public final class Task {

	private final SlayerMaster slayerMaster;
	private final int weight, minimumAmount, maximumAmount;
	private final Class<? extends Area>[] areas;

	public Task(final SlayerMaster master, final int weight, final int minimumAmount, final int maximumAmount) {
		this(master, weight, minimumAmount, maximumAmount, null);
	}
	
	public Task(final SlayerMaster master, final int weight, final int minimumAmount, final int maximumAmount, final Class<? extends Area>... areas) {
		this.slayerMaster = master;
		this.weight = weight;
		this.minimumAmount = minimumAmount;
		this.maximumAmount = maximumAmount;
		this.areas = areas;
	}
}
