package com.zenyte.game.content.skills.smithing;

import com.zenyte.game.content.achievementdiary.diaries.FremennikDiary;
import com.zenyte.game.content.achievementdiary.diaries.KourendDiary;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.PlainChat;
import com.zenyte.plugins.item.CoalBag;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;

/**
 * @author Tommeh | 25 aug. 2018 | 19:05:36
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class Smelting extends Action {
	
	public static final SoundEffect soundEffect = new SoundEffect(2725);
	public static final Animation ANIMATION = new Animation(3243);
	public static final Item GOLDSMITH_GAUNTLETS = new Item(776);
	public static final Item COAL = new Item(453);
	private static final Item RING_OF_FORGING = new Item(2568);
	private static final Location EDGEVILLE_FURNACE = new Location(3111, 3497, 0);
	
	private final SmeltableBar data;
	private final int amount;
	private final WorldObject object;
	private int cycle, ticks;
	
	public static int getAmountOfCoalInCoalBag(final Player player) {
		val coalBag = player.getInventory().getContainer().getFirst(CoalBag.ITEM.getId());
		return coalBag == null ? 0 : coalBag.getValue().getNumericAttribute("coal").intValue();
	}
	
	public static boolean hasMaterials(final SmeltableBar data, final Player player, final boolean firstAttempt, final boolean messageOnFailure) {
		final Item[] materials = new Item[data.getMaterials().length];
		for (int i = 0; i < data.getMaterials().length; i++) {
			materials[i] = new Item(data.getMaterials()[i].getId(), data.getMaterials()[i].getAmount());
		}

		val coalInCoalBag = getAmountOfCoalInCoalBag(player);
		val inventory = player.getInventory();
		for (Item material : materials) {
			if (material.getId() == COAL.getId()) {
				val amount = material.getAmount();
				material.setAmount(Math.max(0, amount - coalInCoalBag));
			}
		}

		if (firstAttempt) {
			for (Item material : materials) {
				if (!inventory.containsItems(material)) {
					if (messageOnFailure) {
						player.getDialogueManager().start(new PlainChat(player, SmeltableBar.getInvalidMaterialsMessage(data)));
					}
					return false;
				}
			}
		} else {
			if (!inventory.containsItems(materials)) {
				if (messageOnFailure) {
					player.sendMessage(SmeltableBar.getInvalidMaterialsMessage(data));
				}
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean start() {
		return data != null && SmeltableBar.hasRequiredLevel(player, data) && hasMaterials(data, player, true, true);
	}

	@Override
	public boolean process() {
		if (cycle < amount) {
			return hasMaterials(data, player, false, true);
		}
		return false;
	}

	public static void handleDaily(final Player player, final SmeltableBar bar, final int amount) {
		val daily = player.getDailyChallengeManager();
		if (bar.equals(SmeltableBar.IRON_BAR)) {
			daily.update(SkillingChallenge.SMELT_IRON_BARS, amount);
		} else if (bar.equals(SmeltableBar.GOLD_BAR)) {
			daily.update(SkillingChallenge.SMELT_GOLD_BARS, amount);
		} else if (bar.equals(SmeltableBar.MITHRIL_BAR)) {
			daily.update(SkillingChallenge.SMELT_MITHRIL_BARS, amount);
		} else if (bar.equals(SmeltableBar.ADAMANTITE_BAR)) {
			daily.update(SkillingChallenge.SMELT_ADAMANT_BARS, amount);
		} else if (bar.equals(SmeltableBar.RUNITE_BAR)) {
			daily.update(SkillingChallenge.SMELT_RUNITE_BARS, amount);
		}
	}

	private static void handleDiary(final Player player, final SmeltableBar bar) {
		val dairies = player.getAchievementDiaries();
		if (bar.equals(SmeltableBar.SILVER_BAR)) {
			dairies.update(FremennikDiary.CRAFT_A_TIARA, 0x2);
		} else if (bar.equals(SmeltableBar.ADAMANTITE_BAR)) {
			dairies.update(KourendDiary.SMELT_AN_ADAMANTITE_BAR);
		}
	}

	private boolean isSuccessful(final Player player) {
		val equipment = player.getEquipment();
		if (data.equals(SmeltableBar.IRON_BAR)) {
			val ringItem = equipment.getItem(EquipmentSlot.RING);
			val hasRing = ringItem != null && ringItem.getId() == RING_OF_FORGING.getId();
			if (hasRing) {
				player.getChargesManager().removeCharges(ringItem, 1, equipment.getContainer(),
						EquipmentSlot.RING.getSlot());
			}
			return hasRing || Utils.random(1) == 0;
		}
		return true;
	}

	@Override
	public int processWithDelay() {
		var amount = 1;
		val body = player.getEquipment().getId(EquipmentSlot.PLATE);
		if (object.getPositionHash() == EDGEVILLE_FURNACE.getPositionHash() && body >= 13104 && body <= 13107) {
			val limit = body == 13104 ? SmeltableBar.STEEL_BAR : body == 13105 ? SmeltableBar.MITHRIL_BAR : body == 13106 ? SmeltableBar.ADAMANTITE_BAR : SmeltableBar.RUNITE_BAR;
			if (Utils.random(10) == 0 && data.ordinal() <= limit.ordinal()) {
				amount++;
			}
		}
		if (ticks == 0) {
			player.sendFilteredMessage(SmeltableBar.getProcessMessage(data));
			player.setAnimation(ANIMATION);
			player.sendSound(soundEffect);
		} else if (ticks == 5) {
			val inventory = player.getInventory();
			val skills = player.getSkills();
			var items = new Item[data.getMaterials().length];
			var coalToRemove = 0;
			for (int index = 0; index < items.length; index++) {
				val material = data.getMaterials()[index];
				var materialAmount = material.getAmount() * amount;

				if (material.getId() == COAL.getId()) {
					val originalAmount = materialAmount;
					materialAmount = Math.min(inventory.getAmountOf(COAL.getId()), materialAmount);
					coalToRemove = originalAmount == materialAmount ? 0 : originalAmount - materialAmount;
				}

				items[index] = new Item(material.getId(), materialAmount);
			}
			val finalCoalToRemove = coalToRemove;
			inventory.deleteItemsIfContains(items, () -> {
				val coalBagAmount = getAmountOfCoalInCoalBag(player);
				if (coalBagAmount > 0 && finalCoalToRemove > 0) {
					inventory.getContainer().getFirst(CoalBag.ITEM.getId()).getValue().setAttribute("coal", coalBagAmount - finalCoalToRemove);
				}
				val amt = items[0].getAmount();
				val success = isSuccessful(player);
				if (success) {
					handleDaily(player, data, amt);
					handleDiary(player, data);
					inventory.addItem(data.getProduct().getId(), amt);
					player.sendFilteredMessage("You retrieve a bar of " + data.toString().replace("bar", ""));
					if (data.equals(SmeltableBar.GOLD_BAR) && (player.getEquipment().getId(EquipmentSlot.HANDS) == GOLDSMITH_GAUNTLETS.getId() || SkillcapePerk.SMITHING.isEffective(player))) {
						skills.addXp(Skills.SMITHING, (data.getXp() + 33.7) * amt);
					} else {
						skills.addXp(Skills.SMITHING, amt * data.getXp());
					}
				} else {
					player.sendFilteredMessage("The ore is too impure and you fail to refine it.");
				}
				cycle++;
			});
			return ticks = 0;
		}
		ticks++;
		return 0;
	}
}
