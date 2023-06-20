package com.zenyte.plugins.dialogue;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

public class ItemChat extends Dialogue {
	
	private String message;
	private Item item;

	public ItemChat(Player player, Item item, String message) {
		super(player);
		this.message = message;
		this.item = item;
	}

	@Override
	public void buildDialogue() {
		item(item, message);
	}
}