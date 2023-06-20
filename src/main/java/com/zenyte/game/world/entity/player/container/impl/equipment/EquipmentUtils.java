package com.zenyte.game.world.entity.player.container.impl.equipment;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.skills.cooking.CookingDefinitions;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Tommeh | 25 sep. 2018 | 16:18:43
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class EquipmentUtils {

	private static final EquipmentSlot[] GRACEFUL_SLOTS = {
			EquipmentSlot.HELMET, EquipmentSlot.PLATE, EquipmentSlot.LEGS,
			EquipmentSlot.CAPE, EquipmentSlot.HANDS, EquipmentSlot.BOOTS
	};

	public static boolean containsFullGraceful(final Player player) {
		for (val slot : GRACEFUL_SLOTS) {
		    if (slot == EquipmentSlot.CAPE) {
		        if (SkillcapePerk.AGILITY.isEffective(player)) {
		            continue;
                }
            }
			val item = player.getEquipment().getItem(slot);
			if (item == null || !item.getName().contains("Graceful")) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean containsFullInitiate(final Player player) {
		val helm = player.getEquipment().getId(EquipmentSlot.HELMET);
		val plate = player.getEquipment().getId(EquipmentSlot.PLATE);
		val legs = player.getEquipment().getId(EquipmentSlot.LEGS);
		return helm == 5574 && plate == 5575 && legs == 5576;
	}
	
	public static boolean containsFullProselyte(final Player player) {
		val helm = player.getEquipment().getId(EquipmentSlot.HELMET);
		val plate = player.getEquipment().getId(EquipmentSlot.PLATE);
		val legs = player.getEquipment().getId(EquipmentSlot.LEGS);
        return helm == 9672 && plate == 9674 && (legs == 9676 || legs == 9678);
	}
	
	public static boolean containsFullDesertRobes(final Player player) {
		val plate = player.getEquipment().getId(EquipmentSlot.PLATE);
		val legs = player.getEquipment().getId(EquipmentSlot.LEGS);
		val boots = player.getEquipment().getId(EquipmentSlot.BOOTS);
		return plate == 1833 && legs == 1835 && boots == 1837;
	}

	public static boolean containsAbyssalBracelet(final Player player) {
		val hands = player.getEquipment().getItem(EquipmentSlot.HANDS);
		if (hands == null) {
			return false;
		}
		return hands.getName().contains("Abyssal bracelet(");
	}

	public static boolean containsCookingGauntlets(final Player player) {
		val hands = player.getEquipment().getId(EquipmentSlot.HANDS);
		return hands == CookingDefinitions.GLOVES.getId();
	}

	public static boolean containsDesertAmulet4(final Player player) {
		val amulet = player.getEquipment().getId(EquipmentSlot.AMULET);
		return amulet == DiaryReward.DESERT_AMULET4.getItem().getId();
	}

	public static boolean containsBonecrusher(final Player player) {
		val amulet = player.getEquipment().getId(EquipmentSlot.AMULET);
		return amulet == ItemId.BONECRUSHER_NECKLACE || player.getInventory().containsAnyOf(ItemId.BONECRUSHER, ItemId.BONECRUSHER_NECKLACE) || player.getEquipment().getId(EquipmentSlot.AMULET) == ItemId.BONECRUSHER_NECKLACE;
	}

}
