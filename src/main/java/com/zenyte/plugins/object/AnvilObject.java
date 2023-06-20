package com.zenyte.plugins.object;

import com.zenyte.game.content.skills.smithing.BarbarianWeapon;
import com.zenyte.game.content.skills.smithing.Smithing;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dialogue.impl.NPCChat;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.PlainChat;
import com.zenyte.plugins.dialogue.skills.AncientForgeD;
import com.zenyte.plugins.dialogue.skills.BarbarianSmithingD;
import com.zenyte.plugins.dialogue.skills.BluriteSmithingD;
import com.zenyte.plugins.interfaces.SmithingInterface;

import lombok.val;

public class AnvilObject implements ObjectAction {

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
		if (object.getId() == 32215) {
			player.getDialogueManager().start(new AncientForgeD(player));
			return;
		}
		if (object.getName().equals("Barbarian anvil")) {
			BarbarianWeapon weapon = null;
			for (int i = 0; i < 28; i++) {
				val item = player.getInventory().getItem(i);
				if (item == null) {
					continue;
				}
				weapon = BarbarianWeapon.get(item.getId());
				if (weapon != null) {
					break;
				}
			}
			if (weapon != null) {
				player.getDialogueManager().start(new BarbarianSmithingD(player, weapon));
			} else {
				player.sendMessage("You don't have the right materials with you right now.");
			}
			return;
		}
		if (object.getId() == ObjectId.ANVIL_6150) {
			if (player.getSkills().getLevel(Skills.SMITHING) < 60) {
				player.getDialogueManager().start(new NPCChat(player, NpcId.BLAST_FURNACE_FOREMAN, "Ay mate, you need a Smithing level of at least 60 before I let you use those anvils."));
				return;
			}
		}
		if (!player.getInventory().containsAnyOf(Smithing.BARS) || (!player.getInventory().containsItems(Smithing.HAMMER) && player.getEquipment().getItem(EquipmentSlot.WEAPON).getId() != 30907) || Smithing.getBarInInventory(player) == null) {
			if (!player.getInventory().containsAnyOf(Smithing.BARS) || Smithing.getBarInInventory(player) == null) {
				player.getDialogueManager().start(new PlainChat(player, "You should select an item from your inventory and use it on the anvil."));
			} else if (!player.getInventory().containsItems(Smithing.HAMMER) && player.getEquipment().getItem(EquipmentSlot.WEAPON).getId() != 30907) {
				player.getDialogueManager().start(new PlainChat(player, "You need a hammer to work the metal with."));
			} else {
				player.getDialogueManager().start(new PlainChat(player, "You should select an item from your inventory and use it on the anvil."));
			}
		} else {
			if (Smithing.getBarInInventory(player) != null && Smithing.getBarInInventory(player).getId() == 9467) {
				player.getDialogueManager().start(new BluriteSmithingD(player, object.getId()));
			} else {
				SmithingInterface.openInterface(player, SmithingInterface.getTierForBar(Smithing.getBarInInventory(player)), object.getId());
			}
		}
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { "Anvil", "Barbarian anvil" };
	}

}
