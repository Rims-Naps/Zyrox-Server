package com.zenyte.game.world.entity.player.dialogue;

import com.zenyte.game.item.Item;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.world.entity.player.Player;

/**
 * Created by Arham 4 on 2/15/2016.
 * <p>
 * Represents a singular message with an Item in it.
 */
public class ItemMessage implements Message {

    private final Item item;
    private final String message;
    private Runnable runnable;

    public ItemMessage(final Item item, final String message) {
        this.item = item;
        this.message = message;
    }

    /**
     * DO NOT OVERRIDE
     */
    @Override
    public void display(final Player player) {
    	player.getInterfaceHandler().sendInterface(InterfacePosition.DIALOGUE, 193);
		player.getPacketDispatcher().sendComponentItem(193, 1, item.getId(), 400);
		player.getPacketDispatcher().sendComponentText(193, 2, message);
		player.getPacketDispatcher().sendClientScript(2868, continueMessage(player));
		player.getPacketDispatcher().sendComponentSettings(193, 0, -1, 0);
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