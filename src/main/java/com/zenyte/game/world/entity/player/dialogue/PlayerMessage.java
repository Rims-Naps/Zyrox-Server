package com.zenyte.game.world.entity.player.dialogue;

import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;

/**
 * Created by Arham 4 on 2/15/2016.
 * <p>
 * Represents a singular message with a Player talking.
 */
public class PlayerMessage implements Message {

    private final Expression expression;
    private final String message;
    private Runnable runnable;
    private boolean cantContinue;

    public PlayerMessage(final Expression expression, final String message) {
        this.expression = expression;
        this.message = message;
    }

    public void setCantContinue() {
    	cantContinue = true;
	}

    /**
     * DO NOT OVERRIDE
     */
    @Override
    public void display(final Player player) {
    	player.getInterfaceHandler().sendInterface(InterfacePosition.DIALOGUE, 217);
		player.getPacketDispatcher().sendClientScript(600, 1, 1, 16, 14221316);
		player.getPacketDispatcher().sendComponentSettings(217, 3, -1, -1, AccessMask.CONTINUE);
		player.getPacketDispatcher().sendComponentPlayerHead(217, 1);
		player.getPacketDispatcher().sendComponentText(217, 2, player.getName());
		if (cantContinue) {
			player.getPacketDispatcher().sendComponentVisibility(217, 3, true);
		} else {
			player.getPacketDispatcher().sendComponentText(217, 3, continueMessage(player));
		}
		player.getPacketDispatcher().sendComponentText(217, 4, message);
		player.getPacketDispatcher().sendComponentAnimation(217, 1, expression.getId());
    }

	@Override
	public void executeAction(final Runnable runnable) {
		this.runnable = runnable;
	}
	
	@Override
	public void execute(final Player player) {
		if (runnable != null) {
			runnable.run();
		}
	}
}