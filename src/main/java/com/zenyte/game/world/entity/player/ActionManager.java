package com.zenyte.game.world.entity.player;

import com.zenyte.cores.WorldThread;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public final class ActionManager {

	private final Player player;
	private Action action;
	private int actionDelay;
	@Getter private Class<?> lastActionClass;
	@Getter private long lastActionCancelTick;

	@Getter private transient long lastAction = System.currentTimeMillis();

	public ActionManager(final Player player) {
		this.player = player;
	}

	public void addActionDelay(final int skillDelay) {
		actionDelay += skillDelay;
	}

	public void forceStop() {
		if (action == null) {
			return;
		}
		this.lastActionClass = action.getClass();
		this.lastActionCancelTick = WorldThread.WORLD_CYCLE;
		action.stop();
		action = null;
	}

	public boolean wasInCombatThisTick() {
		if (lastActionClass == null) {
			return false;
		}
		return lastActionCancelTick == WorldThread.WORLD_CYCLE && PlayerCombat.class.isAssignableFrom(lastActionClass);
	}

	public Action getAction() {
		return action;
	}

	public int getActionDelay() {
		return actionDelay;
	}

	public void setActionDelay(final int skillDelay) {
		actionDelay = skillDelay;
	}

	public boolean hasSkillWorking() {
		return action != null;
	}
	
	public void interrupt(final boolean interrupt) {
		if (action == null) {
			return;
		}
		val interruption = action.setInterrupted(interrupt);
		if (!interruption) {
			forceStop();
		}
	}
	
	public void process() {
		if (action != null) {
			if (player.isDead()) {
				forceStop();
			}
		} 
		if (action != null && action.interruptedByDialogue() && player.getInterfaceHandler().containsInterface(InterfacePosition.DIALOGUE)) {
            if (actionDelay > 0) {
                actionDelay--;
            }
			return;
		} else if (action != null && action.isInterrupted()) {
			interrupt(false);
		}
		if (action != null) {
			if (!action.process()) {
				forceStop();
			}
		}
		if (actionDelay > 0) {
			actionDelay--;
			return;
		}
		if (action == null) {
			return;
		}
		try {
            final int delay = action.processWithDelay();
            if (delay == -1) {
                forceStop();
                return;
            }
            actionDelay += delay;
        } catch (Exception e) {
		    e.printStackTrace();
		    forceStop();
        }
	}

	public boolean setAction(final Action action) {
		forceStop();
		action.setPlayer(player);
		if (!action.start()) {
		    action.stop();
			return false;
		}
        this.lastAction = System.currentTimeMillis();
		this.action = action;
		if (action.initiateOnPacketReceive())
		    process();
		return true;
	}
}