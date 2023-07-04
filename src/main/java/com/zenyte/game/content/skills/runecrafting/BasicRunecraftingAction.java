package com.zenyte.game.content.skills.runecrafting;

import com.google.common.base.Preconditions;
import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.achievementdiary.diaries.*;
import com.zenyte.game.content.follower.impl.SkillingPet;
import com.zenyte.game.content.treasuretrails.clues.SherlockTask;
import com.zenyte.game.content.vote.BoosterPerks;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.plugins.itemonitem.ChipDarkEssenceBlockItemAction;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author Kris | 19. dets 2017 : 2:47.03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class BasicRunecraftingAction extends Action {

	private static final Animation RUNECRAFTING_ANIM = new Animation(791);
	private static final Graphics RUNECRAFTING_GFX = new Graphics(186, 0, 96);
	
	public BasicRunecraftingAction(final Runecrafting rune) {
		this.rune = rune;
	}
	
	private final Runecrafting rune;
	
	@Override
	public boolean start() {
		if (player.getSkills().getLevel(Skills.RUNECRAFTING) < rune.getLevel() && rune.getRuneId() != -1) {
			player.sendMessage("You need at least " + rune.getLevel() + " Runecrafting " + "to runecraft " + rune.toString().replace("_", " ").toLowerCase() + "s.");
			return false;
		}
		if (rune.getEssenceType() == 2 && !player.getInventory().containsItem(7936, 1)) {
			if (rune.getRuneId() == -1) {
				player.sendMessage("You need some pure essence to runecraft at the Ourania altar.");
			} else {
				player.sendMessage("You need some pure essence to runecraft " + rune.toString().toLowerCase().replace("_", " ") + "s.");
			}
			return false;
		} else if (rune.getEssenceType() == 1 && !player.getInventory().containsItem(1436, 1) && !player.getInventory().containsItem(7936, 1)) {
			player.sendMessage("You need some " + (rune.getEssenceType() == 1 ? "rune or " : "") + "pure essence to runecraft " + rune.toString().toLowerCase().replace("_", " ") + "s.");
			return false;
		} else if (rune.getEssenceType() == 3 && !player.getInventory().containsItem(7938, 1)) {
			player.sendMessage("You need some dark essence fragments to runecraft " + rune.toString().toLowerCase().replace("_", " ") + "s.");
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
		int runes = rune.getEssenceType() == 2 ? player.getInventory().getAmountOf(7936) : (player.getInventory().getAmountOf(7936) + player.getInventory().getAmountOf(1436));
		if (rune.getEssenceType() == 1) {
			player.getInventory().deleteItem(1436, runes);
		} if (rune.getEssenceType() == 1 || rune.getEssenceType() == 2) {
			player.getInventory().deleteItem(7936, runes);
		} else if (rune.getEssenceType() == 3) {
		    val slot = ChipDarkEssenceBlockItemAction.findFragmentsSlot(player);
		    val slotId = slot.orElseThrow(RuntimeException::new);
		    val item = player.getInventory().getItem(slotId);
            Preconditions.checkArgument(item.getId() == 7938);
            runes = item.getCharges();
			player.getInventory().deleteItem(slotId, item);
		}
		int multiplier = rune.getDoubleRunes() == -1 ? 0 : (int) Math.floor(player.getSkills().getLevel(Skills.RUNECRAFTING) / rune.getDoubleRunes());
		multiplier += 1;
		var amount = runes * multiplier;
		if (DiaryReward.RADAS_BLESSING4.eligibleFor(player) && rune.equals(Runecrafting.BLOOD_RUNE)) {
			amount *= 1.1;
		}
		double experience = 0;
		ArrayList<Integer> ZMIRUNES = new ArrayList<>();
		if (rune.getRuneId() == -1) {
			val list = getZmiRunes(player, amount);
			experience = list.get(list.size() - 1);
			ZMIRUNES.addAll(list);
			ZMIRUNES.remove(ZMIRUNES.size() - 1);
		} else {
			experience = rune.getExperience() * runes;
		}

		if (rune.equals(Runecrafting.COSMIC_RUNE)) {
			SherlockTask.CRAFT_MULTIPLE_COSMIC_RUNES.progress(player);
		}
		if (amount >= 140 && rune.equals(Runecrafting.MIND_RUNE)) {
			player.getAchievementDiaries().update(FaladorDiary.CRAFT_MIND_RUNES);
		} else if (amount >= 252 && rune.equals(Runecrafting.AIR_RUNE)) {
			player.getAchievementDiaries().update(FaladorDiary.CRAFT_AIR_RUNES);
		} else if (rune.equals(Runecrafting.COSMIC_RUNE)) {
			if (amount >= 56) {
				player.getAchievementDiaries().update(LumbridgeDiary.CRAFT_COSMIC_RUNES);
			}
			player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_COSMIC_RUNES, amount * 2);
		} else if (amount >= 56 && rune.equals(Runecrafting.ASTRAL_RUNE)) {
			player.getAchievementDiaries().update(FremennikDiary.CRAFT_ASTRAL_RUNES);
		} else if (rune.equals(Runecrafting.DEATH_RUNE)) {
			player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_DEATH_RUNES, amount * 2);
			player.getAchievementDiaries().update(ArdougneDiary.CRAFT_DEATH_RUNES);
		} else if (rune.equals(Runecrafting.NATURE_RUNE)) {
			if (amount >= 56) {
				player.getAchievementDiaries().update(KaramjaDiary.CRAFT_56_NATURE_RUNES);
			}
            SherlockTask.CRAFT_A_NATURE_RUNE.progress(player);
			player.getAchievementDiaries().update(KaramjaDiary.CRAFT_NATURE_RUNES);
			player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_NATURE_RUNES, amount * 2);
		} else if (rune.equals(Runecrafting.EARTH_RUNE)) {
			if (amount >= 100) {
				player.getAchievementDiaries().update(VarrockDiary.CRAFT_100_EARTH_RUNES);
			}
			player.getAchievementDiaries().update(VarrockDiary.CRAFT_EARTH_RUNES);
		} else if (rune.equals(Runecrafting.WATER_RUNE)) {
			if (amount >= 140) {
				player.getAchievementDiaries().update(LumbridgeDiary.CRAFT_140_WATER_RUNES);
			}
			player.getAchievementDiaries().update(LumbridgeDiary.CRAFT_WATER_RUNES);
			player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_WATER_RUNES, amount * 2);
		} else if (rune.equals(Runecrafting.FIRE_RUNE)) {
			player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_FIRE_RUNES, amount * 2);
		} else if (rune.equals(Runecrafting.LAW_RUNE)) {
			player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_LAW_RUNES, amount * 2);
		} else if (rune.equals(Runecrafting.SOUL_RUNE)) {
			player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_SOUL_RUNES, amount * 2);
		} else if (rune.equals(Runecrafting.WRATH_RUNE)) {
			player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_WRATH_RUNES, amount * 2);
		} else if (rune.equals(Runecrafting.BLOOD_RUNE)) {
			player.getAchievementDiaries().update(KourendDiary.CRAFT_ONE_OR_MORE_BLOOD_RUNES);
		}
		experience *= getRcSetXpBonus(player);
		player.getSkills().addXp(Skills.RUNECRAFTING, experience);
		if (rune.getRuneId() == -1) {
			int hundreds = (int) ((experience - (experience % 100))/100);  //100 base xp gives one roll at getting a pearl, 50% chance per roll
			for (int i = 0; i < hundreds; ++i) {
				if (Utils.random(1) == 0) {
					player.getInventory().addOrDrop(32188, 1);
				}
			}

			for (int id : ZMIRUNES) {
				int zmiAmount = 2;
				if (DiaryReward.ARDOUGNE_CLOAK2.eligibleFor(player)) {
					if ((id == 556 || id == 558 || id == 555 || id == 557 || id == 554 || id == 559 || id == 562 || id == 9075) && Utils.random(3) == 0) {
						zmiAmount += 1;
					} else if (id == 561 && Utils.random(199) < 45) {
						zmiAmount += 1;
					} else if (id == 563 && Utils.random(4) == 0) {
						zmiAmount += 1;
					} else if (id == 560 && Utils.random(199) < 35) {
						zmiAmount += 1;
					} else if (id == 565 && Utils.random(99) < 15) {
						zmiAmount += 1;
					} else if (id == 566 && Utils.random(9) == 0) {
						zmiAmount += 1;
					}
					zmiAmount += BoosterPerks.isActive(player, BoosterPerks.RUNECRAFT) ? Utils.random(100) < 5 ? 5 : 0 : 0;
				}
				zmiAmount *= getRcSetRuneBonus(player);
				player.getInventory().addItem(new Item(id, zmiAmount));
			}
			player.sendFilteredMessage("You bind the Temple's power into runes.");
		} else {
			amount *= getRcSetRuneBonus(player);
			player.getInventory().addItem(new Item(rune.getRuneId(), amount * 2 + (BoosterPerks.isActive(player, BoosterPerks.RUNECRAFT) ? Utils.random(100) < 5 ? Utils.random(amount) : 0 : 0)));
			player.sendFilteredMessage("You bind the Temple's power into " + rune.toString().replace("_", " ").toLowerCase() + "s.");
		}
		player.sendFilteredMessage("The gods bless your efforts and grant you extra runes.");
		if (player.getFollower() != null) {
			val pet = player.getFollower().getPet();
			if (SkillingPet.isRiftGuardian(pet) && !player.getBooleanAttribute("rift_guardian_colour_lock")) {
				player.setPetId(rune.getPet().getPetId());
				player.getFollower().setTransformation(rune.getPet().getPetId());
			}
		}
		val chance = experience > 2_500_000 ? 0.5F : (experience / 5_000_000);
		rune.getPet().roll(player, (int) (1F / chance) - 1);
		return -1;
	}

	private ArrayList<Integer> getZmiRunes(@NotNull Player player, int amount) {
		int experience = 0;
		ArrayList<Integer> ZMIRUNES = new ArrayList<>();
		for (int i = amount; i > 0; --i) {
			int id = runeDistrubution(player);
			ZMIRUNES.add(id);
			double xp = Math.round(getXpForZMIRune(id) * 1.7);
			experience = (int) (experience + xp);
		}
		ZMIRUNES.add(experience);
		return ZMIRUNES;
	}

	private int runeDistrubution(@NotNull Player player) {
		val randomInt = Utils.random(9999);
		if (player.getSkills().getLevel(Skills.RUNECRAFTING) < 10) {
			return randomInt < 2 ? 566 : randomInt < 7 ? 565 : randomInt < 15 ? 560 : randomInt < 30 ? 563 : randomInt < 60 ? 561 : randomInt < 105 ? 9075 : randomInt < 165 ? 562 :
					randomInt < 250 ? 564 : randomInt < 400 ? 559 : randomInt < 700 ? 554 : randomInt < 1300 ? 557 : randomInt < 2500 ? 555 : randomInt < 5000 ? 558 : 556;
		} else if (player.getSkills().getLevel(Skills.RUNECRAFTING) < 20) {
			return randomInt < 3 ? 566 : randomInt < 9 ? 565 : randomInt < 21 ? 560 : randomInt < 45 ? 563 : randomInt < 85 ? 561 : randomInt < 145 ? 9075 : randomInt < 225 ? 562 :
					randomInt < 400 ? 564 : randomInt < 1000 ? 559 : randomInt < 2200 ? 554 : randomInt < 4600 ? 557 : randomInt < 6700 ? 555 : randomInt < 8500 ? 558 : 556;
		} else if (player.getSkills().getLevel(Skills.RUNECRAFTING) < 30) {
			return randomInt < 8 ? 566 : randomInt < 23 ? 565 : randomInt < 55 ? 560 : randomInt < 110 ? 563 : randomInt < 220 ? 561 : randomInt < 430 ? 9075 : randomInt < 850 ? 562 :
					randomInt < 1650 ? 564 : randomInt < 3250 ? 559 : randomInt < 4750 ? 554 : randomInt < 6150 ? 557 : randomInt < 7500 ? 555 : randomInt < 8800 ? 558 : 556;
		} else if (player.getSkills().getLevel(Skills.RUNECRAFTING) < 40) {
			return randomInt < 20 ? 566 : randomInt < 60 ? 565 : randomInt < 120 ? 560 : randomInt < 250 ? 563 : randomInt < 500 ? 561 : randomInt < 1000 ? 9075 : randomInt < 2000 ? 562 :
					randomInt < 4000 ? 564 : randomInt < 5300 ? 559 : randomInt < 6500 ? 554 : randomInt < 7600 ? 557 : randomInt < 8500 ? 555 : randomInt < 9300 ? 558 : 556;
		} else if (player.getSkills().getLevel(Skills.RUNECRAFTING) < 50) {
			return randomInt < 40 ? 566 : randomInt < 120 ? 565 : randomInt < 240 ? 560 : randomInt < 500 ? 563 : randomInt < 1000 ? 561 : randomInt < 2000 ? 9075 : randomInt < 4000 ? 562 :
					randomInt < 5500 ? 564 : randomInt < 6500 ? 559 : randomInt < 7300 ? 554 : randomInt < 8050 ? 557 : randomInt < 8750 ? 555 : randomInt < 9400 ? 558 : 556;
		} else if (player.getSkills().getLevel(Skills.RUNECRAFTING) < 60) {
			return randomInt < 80 ? 566 : randomInt < 250 ? 565 : randomInt < 600 ? 560 : randomInt < 1300 ? 563 : randomInt < 2650 ? 561 : randomInt < 4150 ? 9075 : randomInt < 5250 ? 562 :
					randomInt < 6250 ? 564 : randomInt < 7000 ? 559 : randomInt < 7700 ? 554 : randomInt < 8350 ? 557 : randomInt < 8950 ? 555 : randomInt < 9500 ? 558 : 556;
		} else if (player.getSkills().getLevel(Skills.RUNECRAFTING) < 70) {
			return randomInt < 100 ? 566 : randomInt < 300 ? 565 : randomInt < 700 ? 560 : randomInt < 1500 ? 563 : randomInt < 3050 ? 561 : randomInt < 4450 ? 9075 : randomInt < 5500 ? 562 :
					randomInt < 6450 ? 564 : randomInt < 7200 ? 559 : randomInt < 7900 ? 554 : randomInt < 8500 ? 557 : randomInt < 9050 ? 555 : randomInt < 9550 ? 558 : 556;
		} else if (player.getSkills().getLevel(Skills.RUNECRAFTING) < 80) {
			return randomInt < 200 ? 566 : randomInt < 700 ? 565 : randomInt < 1700 ? 560 : randomInt < 3500 ? 563 : randomInt < 5000 ? 561 : randomInt < 6200 ? 9075 : randomInt < 7100 ? 562 :
					randomInt < 7800 ? 564 : randomInt < 8300 ? 559 : randomInt < 8700 ? 554 : randomInt < 9100 ? 557 : randomInt < 9400 ? 555 : randomInt < 9700 ? 558 : 556;
		} else if (player.getSkills().getLevel(Skills.RUNECRAFTING) < 90) {
			return randomInt < 400 ? 566 : randomInt < 1000 ? 565 : randomInt < 2450 ? 560 : randomInt < 3900 ? 563 : randomInt < 5250 ? 561 : randomInt < 6300 ? 9075 : randomInt < 7100 ? 562 :
					randomInt < 7800 ? 564 : randomInt < 8400 ? 559 : randomInt < 8900 ? 554 : randomInt < 9300 ? 557 : randomInt < 9600 ? 555 : randomInt < 9800 ? 558 : 556;
		} else if (player.getSkills().getLevel(Skills.RUNECRAFTING) < 99) {
			return randomInt < 650 ? 566 : randomInt < 1650 ? 565 : randomInt < 3300 ? 560 : randomInt < 4750 ? 563 : randomInt < 6100 ? 561 : randomInt < 7100 ? 9075 : randomInt < 7800 ? 562 :
					randomInt < 8400 ? 564 : randomInt < 8900 ? 559 : randomInt < 9300 ? 554 : randomInt < 9600 ? 557 : randomInt < 9800 ? 555 : randomInt < 9900 ? 558 : 556;
		} else {
			return randomInt < 900 ? 566 : randomInt < 2200 ? 565 : randomInt < 3750 ? 560 : randomInt < 5200 ? 563 : randomInt < 6550 ? 561 : randomInt < 7500 ? 9075 : randomInt < 8100 ? 562 :
					randomInt < 8600 ? 564 : randomInt < 9000 ? 559 : randomInt < 9300 ? 554 : randomInt < 9600 ? 557 : randomInt < 9800 ? 555 : randomInt < 9900 ? 558 : 556;
		}
	}

	private double getXpForZMIRune(int id) {
		return id == 556 ? 5 : id == 558 ? 5.5 : id == 555 ? 6 : id == 557 ? 6.5 : id == 554 ? 7 : id == 559 ? 7.5 : id == 564 ? 8 : id == 563 ? 9.5 : id == 561 ? 9 :
				id == 562 ? 8.5 : id == 560 ? 10 : id == 565 ? 23 : id == 9075 ? 8.7 : 29.7;
	}

	private double getRcSetRuneBonus(Player player) {
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
