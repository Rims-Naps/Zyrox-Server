package com.zenyte.plugins.item;

import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import lombok.val;

/**
 * @author Kris | 25. aug 2018 : 22:29:05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class ImbuedHeart extends ItemPlugin {

	private static final Graphics GRAPHICS = new Graphics(1316);
	
	@Override
	public void handle() {
		bind("Invigorate", (player, item, slotId) -> {
			if (player.getVariables().getTime(TickVariable.IMBUED_HEART_COOLDOWN) == 0) {
				val boost = (int) (player.getSkills().getLevel(Skills.MAGIC) * 0.1 + 1);
				player.getSkills().boostSkill(Skills.MAGIC, boost);
				player.setGraphics(GRAPHICS);
				player.getVariables().schedule(700, TickVariable.IMBUED_HEART_COOLDOWN);
			} else {
				val totalSeconds = (int) (player.getVariables().getTime(TickVariable.IMBUED_HEART_COOLDOWN) * 0.6f);
				val seconds = totalSeconds % 60;
				val minutes = totalSeconds / 60;
				System.out.println(minutes + ", " + seconds);
				player.sendMessage("The heart is still drained of its power. Judging by how it feels, it will be ready in around " + (minutes == 0 ? (seconds + " seconds.") : (minutes + " minutes.")));
			}
		});
	}

	@Override
	public int[] getItems() {
		return new int[] { 20724 };
	}

}
