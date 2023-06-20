package com.zenyte.game.tasks;

import com.zenyte.game.util.TimeUnit;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * @author Kris | 4. apr 2018 : 21:33.45
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
@Slf4j
public class WorldTasksManager {

	private static final Map<WorldTask, WorldTaskInformation> PENDING_ADD_TASKS = new Object2ObjectOpenHashMap<>();
	private static final Set<WorldTask> PENDING_REMOVE_TASKS = new ObjectOpenHashSet<>();
	static final Map<WorldTask, WorldTaskInformation> MAIN_TASKS = new Object2ObjectOpenHashMap<>();

	@Synchronized
	public static void processTasks() {
		MAIN_TASKS.putAll(PENDING_ADD_TASKS);
		PENDING_ADD_TASKS.clear();
		for (val entry : MAIN_TASKS.entrySet()) {
			val value = entry.getValue();
			if (value.continueCount > 0) {
				value.continueCount--;
				continue;
			}
			val key = entry.getKey();
			try {
				key.run();
			} catch (final Exception e) {
				log.error(Strings.EMPTY, e);
                PENDING_REMOVE_TASKS.add(key);
				continue;
			}
			if (value.continueMaxCount != -1) {
				value.continueCount = value.continueMaxCount;
				continue;
			}
			PENDING_REMOVE_TASKS.add(key);
		}
		MAIN_TASKS.keySet().removeAll(PENDING_REMOVE_TASKS);
		PENDING_REMOVE_TASKS.clear();
	}

	public static final int count() {
		return MAIN_TASKS.size();
	}

	@Synchronized
	public static void schedule(final WorldTask task) {
		if (task == null) {
			return;
		}
		PENDING_ADD_TASKS.put(task, new WorldTaskInformation(0, -1));
	}

	@Synchronized
	public static void schedule(final WorldTask task, final int delayCount) {
		if (task == null || delayCount < 0) {
			return;
		}
		PENDING_ADD_TASKS.put(task, new WorldTaskInformation(delayCount, -1));
	}

	@Synchronized
	public static void schedule(final WorldTask task, final int delayCount, final int periodCount) {
		if (task == null || delayCount < 0 || periodCount < 0) {
			return;
		}
		PENDING_ADD_TASKS.put(task, new WorldTaskInformation(delayCount, periodCount));
	}

	/**
	 * Schedules the task if the delay is above zero, otherwise executes it immediately.
	 * 
	 * @param task
	 *            the task to execute.
	 * @param delay
	 *            the delay in {@link TimeUnit#TICKS } until the task is executed.
	 */
	@Synchronized
	public static final void scheduleOrExecute(@NotNull final WorldTask task, final int delay) {
		if (delay < 0) {
			task.run();
		} else {
			PENDING_ADD_TASKS.put(task, new WorldTaskInformation(delay, -1));
		}
	}

    /**
     * Schedules the task if the delay is above zero, otherwise executes it immediately.
     *
     * @param task
     *            the task to execute.
     * @param delay
     *            the delay in {@link TimeUnit#TICKS } until the task is executed.
     */
    @Synchronized
    public static final void scheduleOrExecute(@NotNull final WorldTask task, final int delay,
                                               final int additionalDelay) {
        if (delay < 0) {
            task.run();
        } else {
            PENDING_ADD_TASKS.put(task, new WorldTaskInformation(delay, additionalDelay));
        }
    }

	static final class WorldTaskInformation {

		int continueMaxCount;
		private int continueCount;

		WorldTaskInformation(final int continueCount, final int continueMaxCount) {
			this.continueCount = continueCount;
			this.continueMaxCount = continueMaxCount;
		}
	}
}