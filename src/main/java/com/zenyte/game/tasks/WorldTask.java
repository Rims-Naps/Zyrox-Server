package com.zenyte.game.tasks;

import lombok.val;

public interface WorldTask extends Runnable {

	public default void stop() {
		val info = WorldTasksManager.MAIN_TASKS.get(this);
		if (info != null) {
			info.continueMaxCount = -1;
		}
	}
}
