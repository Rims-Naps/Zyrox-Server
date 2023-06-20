package com.zenyte.game.world.entity.player;

import com.zenyte.game.util.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Kris | 22. march 2018 : 0:17.02
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class BossTimer {

	BossTimer(final Player player) {
		this.player = player;
	}
	
	private transient final Player player;
	private final Map<String, Integer> timers = new HashMap<>();
	private transient String currentBoss;
	private transient long currentTracker;
	
	/**
	 * Sets the boss timer data.
	 * @param clone the object to obtain data from.
	 */
	public void setBossTimers(final BossTimer clone) {
		timers.putAll(clone.timers);
	}
	
	/**
	 * Starts the tracking process at the moment of the method being called.
	 * @param currentBoss the name of the boss being tracked.
	 */
	public void startTracking(final String currentBoss) {
		this.currentBoss = currentBoss;
		currentTracker = Utils.currentTimeMillis();
	}
	
	/**
	 * Finishes tracking the current boss and informs the player about the time.
	 * @param currentBoss the name of the current boss. Upon mismatch, method returns.
	 */
	public void finishTracking(final String currentBoss) {
		if (!currentBoss.equals(this.currentBoss))
			return;
		inform(currentBoss, System.currentTimeMillis() - currentTracker);
	}

	public void inform(final String currentBoss, final long duration) {
        final int lengthInSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(duration);
        final int oldRecord = timers.getOrDefault(currentBoss, -1);
        final int minutes = lengthInSeconds / 60;
        final int seconds = lengthInSeconds % 60;
        if (oldRecord == -1 || lengthInSeconds < oldRecord) {
            player.sendMessage("Fight duration: <col=ff0000>" + minutes + ":" + (seconds < 10 ? "0" : "") + seconds + " </col>(new personal best)");
            timers.put(currentBoss, lengthInSeconds);
        } else {
            final int oldMinutes = oldRecord / 60;
            final int oldSeconds = oldRecord % 60;
            player.sendMessage("Fight duration: <col=ff0000>" + minutes + ":" + (seconds < 10 ? "0" : "") + seconds + "</col>. "
                    + "Personal best: " + oldMinutes + ":" + (oldSeconds < 10 ? "0" : "") + oldSeconds);
        }
    }

	public boolean resetBossTimer(final String bossname) {
		if (!timers.containsKey(StringUtils.capitalize(bossname))) { //given player does not have a kill timer for the given boss
			return false;
		}else {
			timers.put(StringUtils.capitalize(bossname), 10000);
			return true;
		}

	}

	public int getBossTimer(final String bossname) {
		return timers.getOrDefault(bossname, -1);
	}
	
}
