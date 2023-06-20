package com.zenyte.plugins.item;

import com.zenyte.game.content.skills.prayer.actions.Bones;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.Skills;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import lombok.val;

/**
 * @author Kris | 25. aug 2018 : 22:10:50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class BonesItem extends ItemPlugin {

	@Override
	public void handle() {
		bind("Bury", (player, item, slotId) -> {
			val bone = Bones.getBone(item.getId());
			if (bone == null) {
				return;
			}
			if (bone == Bones.SUPERIOR_DRAGON_BONES) {
				if (player.getSkills().getLevelForXp(Skills.PRAYER) < 70) {
					player.sendMessage("You need a Prayer level of at least 70 to bury the Superior dragon bones");
					return;
				}
			}
			Bones.bury(player, bone, item, slotId);
		});
	}

	@Override
	public int[] getItems() {
		val list = new IntArrayList();
		for (val bones : com.zenyte.game.content.skills.prayer.actions.Bones.VALUES) {
			for (final Item bone : bones.getBones()) {
				list.add(bone.getId());
			}
		}
		return list.toArray(new int[list.size()]);
	}

}
