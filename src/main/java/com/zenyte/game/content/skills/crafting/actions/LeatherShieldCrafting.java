package com.zenyte.game.content.skills.crafting.actions;

import com.zenyte.game.content.skills.crafting.CraftingDefinitions.LeatherShieldData;
import com.zenyte.game.content.skills.smithing.Smithing;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.plugins.dialogue.PlainChat;

import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 14 apr. 2018 | 16:28:52
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
@RequiredArgsConstructor
public class LeatherShieldCrafting extends Action {
	
	private final LeatherShieldData data;
	private final int amount;
	private int cycle, ticks;

	@Override
	public boolean start() {
	    if (player.getSkills().getLevel(Skills.CRAFTING) < data.getLevel()) {
			player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
			player.getDialogueManager().start(new PlainChat(player, "You need at least level " + data.getLevel() + " Crafting to make that."));
			return false;
		}
		if (!player.getInventory().containsItem(Smithing.HAMMER) && player.getEquipment().getItem(EquipmentSlot.WEAPON).getId() != 30907) {
			player.sendMessage("You need a hammer to do this.");
			return false;
		}
		if (!player.getInventory().containsItems(data.getMaterials())) {
			return false;
		}
		return true;
	}

	@Override
	public boolean process() {
		if (!player.getInventory().containsItems(data.getMaterials())) {
			return false;
		}
		if (cycle >= amount) {
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay() {
		if (ticks == 0) {
			player.setAnimation(data.getAnimation());
		} else if (ticks == 3) {
			player.getInventory().deleteItemsIfContains(data.getMaterials(), () -> {
				player.getInventory().addItem(data.getProduct());
				player.getSkills().addXp(Skills.CRAFTING, data.getXp());
				cycle++;
			});
			return ticks = 0;
		}
		ticks++;
		return 0;
	}

}
