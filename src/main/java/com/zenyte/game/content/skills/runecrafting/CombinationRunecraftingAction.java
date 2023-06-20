package com.zenyte.game.content.skills.runecrafting;

import com.zenyte.game.content.achievementdiary.diaries.LumbridgeDiary;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import mgi.types.config.items.ItemDefinitions;

import lombok.val;

/**
 * @author Kris | 19. dets 2017 : 2:48.32
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class CombinationRunecraftingAction extends Action {

	private static final Animation RUNECRAFTING_ANIM = new Animation(791);
	private static final Graphics RUNECRAFTING_GFX = new Graphics(186, 0, 96);
	
	public CombinationRunecraftingAction(final CombinationRunecrafting cRune) {
		this.cRune = cRune;
	}
	
	private final CombinationRunecrafting cRune;
	
	@Override
	public boolean start() {
		if (player.getSkills().getLevel(Skills.RUNECRAFTING) < cRune.getLevelRequired()) {
			player.sendMessage("You need at least level " + cRune.getLevelRequired() + " Runecrafting to combine these runes.");
			return false;
		}
		if (!player.getInventory().containsItem(7936, 1)) {
			player.sendMessage("You need some pure essence in order to combine these runes.");
			return false;
		}
		if (!player.getInventory().containsItem(cRune.getRequiredRuneId(), 1)) {
			player.sendMessage("You need some " + ItemDefinitions.getOrThrow(cRune.getRequiredRuneId()).getName() + "s to combine these runes.");
			return false;
		}
		player.setAnimation(RUNECRAFTING_ANIM);
		player.setGraphics(RUNECRAFTING_GFX);
		return true;
	}

	@Override
	public boolean process() {
		return true;
	}

	@Override
	public int processWithDelay() {
		final int runes = Math.min(player.getInventory().getAmountOf(7936), player.getInventory().getAmountOf(cRune.getRequiredRuneId()));
		val amulet = player.getEquipment().getId(EquipmentSlot.AMULET);
		val isBindingNecklace = amulet == 5521;
		final boolean success = isBindingNecklace || Utils.random(100) > 50;
		player.getInventory().deleteItem(7936, runes);
		player.getInventory().deleteItem(cRune.getRequiredRuneId(), runes);
		val name = ItemDefinitions.getOrThrow(cRune.getRuneId()).getName().toLowerCase();
		if (success) {
			if (isBindingNecklace) {
				val uses = player.getNumericAttribute("binding necklace uses").intValue() + 1;
				player.addAttribute("binding necklace uses", uses % 16);
				if (uses == 16) {
					player.getEquipment().set(EquipmentSlot.AMULET, null);
					player.sendMessage("Your Binding necklace has disintegrated.");
				}
			}
			if (cRune.equals(CombinationRunecrafting.LAVA_RUNE_FIRE)) {
				player.getAchievementDiaries().update(LumbridgeDiary.CRAFT_LAVA_RUNES);
			}
			double setBonus = getRcSetBonus(player);
			player.getInventory().addItem(new Item(cRune.getRuneId(), (int) (runes * 2 * setBonus)));

			player.sendFilteredMessage("You bind the temple's power into " + name + "s.");
            player.sendFilteredMessage("The gods bless your efforts and grant you extra runes.");
		} else {
			player.sendFilteredMessage("You fail to bind the temple's power into " + name + "s.");
		}
		double experience = cRune.getExperience() * runes;
		experience *= getRcSetXpBonus(player);
		player.getSkills().addXp(Skills.RUNECRAFTING, experience);
		return -1;
	}

	private double getRcSetBonus(Player player) {
		int count = 0;
		val equipment = player.getEquipment();
		if (equipment.getId(EquipmentSlot.HELMET) == 32196 || equipment.getId(EquipmentSlot.HELMET) == 32222)
			count++;
		if (equipment.getId(EquipmentSlot.PLATE) == 32204 || equipment.getId(EquipmentSlot.PLATE) == 32224)
			count++;
		if (equipment.getId(EquipmentSlot.HANDS) == 32210 || equipment.getId(EquipmentSlot.HANDS) == 32230)
			count++;
		if (equipment.getId(EquipmentSlot.LEGS) == 32206 || equipment.getId(EquipmentSlot.LEGS) == 32226)
			count++;
		if (equipment.getId(EquipmentSlot.BOOTS) == 32208 || equipment.getId(EquipmentSlot.BOOTS) == 32228)
			count++;
		if (count == 5) {
			return 1.7;
		} else {
			return 1 + (0.1 * count);
		}
	}

	private double getRcSetXpBonus(Player player) {
		int count = 0;
		val equipment = player.getEquipment();
		if (equipment.getId(EquipmentSlot.HELMET) == 32196 || equipment.getId(EquipmentSlot.HELMET) == 32222)
			count++;
		if (equipment.getId(EquipmentSlot.PLATE) == 32204 || equipment.getId(EquipmentSlot.PLATE) == 32224)
			count++;
		if (equipment.getId(EquipmentSlot.HANDS) == 32210 || equipment.getId(EquipmentSlot.HANDS) == 32230)
			count++;
		if (equipment.getId(EquipmentSlot.LEGS) == 32206 || equipment.getId(EquipmentSlot.LEGS) == 32226)
			count++;
		if (equipment.getId(EquipmentSlot.BOOTS) == 32208 || equipment.getId(EquipmentSlot.BOOTS) == 32228)
			count++;
		if (count == 5) {
			return 1.03;
		} else {
			return 1 + (0.005 * count);
		}
	}
}
