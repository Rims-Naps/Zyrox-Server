package com.zenyte.plugins.item;

import com.zenyte.game.content.skills.prayer.actions.Ashes;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import lombok.val;

public class AshesItem extends ItemPlugin {

	@Override
	public void handle() {
		bind("Scatter", (player, item, slotId) -> {
			val bone = Ashes.getAsh(item.getId());
			if (bone == null) {
				return;
			}
			Ashes.bury(player, bone, item, slotId);
		});
	}

	@Override
	public int[] getItems() {
		return new int[] {  31038, 31041, 31044, 31047, 30065 };
	}
}
