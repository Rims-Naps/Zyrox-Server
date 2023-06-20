package com.zenyte.game.content.skills.slayer;

import java.util.function.Predicate;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;

import lombok.AllArgsConstructor;
import lombok.val;

/**
 * @author Kris | 30. aug 2018 : 15:41:53
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@AllArgsConstructor
public enum SlayerEquipment {

	SLAYER_HELM(EquipmentSlot.HELMET, item -> item.getName().toLowerCase().contains("slayer helm")),
	EARMUFFS(EquipmentSlot.HELMET, item -> item.getId() == 4166 || item.getName().toLowerCase().contains("slayer helm")),
	NOSE_PEG(EquipmentSlot.HELMET, item -> item.getId() == 4168 || item.getName().toLowerCase().contains("slayer helm")),
	FACE_MASK(EquipmentSlot.HELMET, item -> item.getId() == 4164 || item.getName().toLowerCase().contains("slayer helm")),
	SPINY_HELMET(EquipmentSlot.HELMET, item -> item.getId() == 4551 || item.getName().toLowerCase().contains("slayer helm")),
	MIRROR_SHIELD(EquipmentSlot.SHIELD, item -> item.getId() == 4156),
	LIT_BUG_LANTERN(EquipmentSlot.SHIELD, item -> item.getId() == 7053),
	WITCHWOOD_ICON(EquipmentSlot.AMULET, item -> item.getId() == 8923),
	INSULATED_BOOTS(EquipmentSlot.BOOTS, item -> item.getId() == 7159);

	private final EquipmentSlot slot;
	private final Predicate<Item> predicate;

	/**
	 * Whether or not the player is wielding the given Slayer equipment.
	 * 
	 * @param player
	 *            the player whom to check.
	 * @return whether the player is wearing the equipment or not.
	 */
	public boolean isWielding(final Player player) {
		val item = player.getEquipment().getItem(slot);
		if (item == null) {
			return false;
		}
		return predicate.test(item);
	}
}
