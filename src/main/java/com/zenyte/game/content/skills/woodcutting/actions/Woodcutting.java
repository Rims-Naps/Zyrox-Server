package com.zenyte.game.content.skills.woodcutting.actions;

import com.zenyte.game.content.achievementdiary.diaries.DesertDiary;
import com.zenyte.game.content.achievementdiary.diaries.FaladorDiary;
import com.zenyte.game.content.achievementdiary.diaries.FremennikDiary;
import com.zenyte.game.content.achievementdiary.diaries.KaramjaDiary;
import com.zenyte.game.content.achievementdiary.diaries.KourendDiary;
import com.zenyte.game.content.achievementdiary.diaries.LumbridgeDiary;
import com.zenyte.game.content.achievementdiary.diaries.MorytaniaDiary;
import com.zenyte.game.content.achievementdiary.diaries.VarrockDiary;
import com.zenyte.game.content.achievementdiary.diaries.WesternProvincesDiary;
import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.content.skills.firemaking.Firemaking;
import com.zenyte.game.content.skills.woodcutting.AxeDefinitions;
import com.zenyte.game.content.skills.woodcutting.TreeDefinitions;
import com.zenyte.game.content.treasuretrails.ClueItem;
import com.zenyte.game.content.treasuretrails.clues.SherlockTask;
import com.zenyte.game.content.vote.BoosterPerks;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.VarManager;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.entity.player.perk.PerkWrapper;
import com.zenyte.game.world.object.WorldObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import mgi.types.config.items.ItemDefinitions;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Kris | 13. dets 2017 : 6:07.25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 *      profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 *      profile</a>
 */
public class Woodcutting extends Action {
	public static final int SULLIUSCEP_INDEX_VARBIT = 5808;

	public static final Graphics BURN_GFX = new Graphics(86, 0, 180);
	private static final int BIRD_NEST_CHANCE = 128;
	public static final SoundEffect TREE_FALL_SOUND = new SoundEffect(2734);
	
	private final WorldObject tree;
	private final TreeDefinitions definitions;
	private AxeResult axe;
	private final String logName;
	private int ticks;
	private final Runnable onFall;

	static  {
		VarManager.appendPersistentVarbit(SULLIUSCEP_INDEX_VARBIT);
	}

    public Woodcutting(final WorldObject tree, final TreeDefinitions definitions) {
        this(tree, definitions, null);
    }

	public Woodcutting(final WorldObject tree, final TreeDefinitions definitions, final Runnable onFall) {
		this.tree = tree;
		this.definitions = definitions;
		val defs = ItemDefinitions.get(definitions.getLogsId());
		logName = defs == null ? "null" : defs.getName().toLowerCase();
		this.onFall = onFall;
	}

	@Override
	public boolean start() {
		val optionalAxe = getAxe(player);
		if (!optionalAxe.isPresent()) {
            player.sendMessage("You do not have an axe which you have the woodcutting level to use.");
		    return false;
        }
		this.axe = optionalAxe.get();
		if (definitions.getLevel() > player.getSkills().getLevel(Skills.WOODCUTTING)) {
            player.sendMessage("You need a Woodcutting level of at least " + definitions.getLevel() + " to chop down this tree.");
			return false;
		}
		if (!check()) {
			return false;
		}
		player.sendFilteredMessage("You swing your axe at the " + tree.getName(player).toLowerCase() + ".");
		delay(axe.getDefinitions().getCutTime());
		return true;
	}

	private boolean check() {
		if (definitions.getLevel() > player.getSkills().getLevel(Skills.WOODCUTTING)) {
			player.sendMessage("You need a Woodcutting level of at least " + definitions.getLevel() + " to chop down this tree.");
			return false;
		}
		if (!player.getInventory().hasFreeSlots()) {
			player.sendFilteredMessage("Not enough space in your inventory.");
			return false;
		}
		return tree.exists();
	}

    @AllArgsConstructor
    @Getter
    public static final class AxeResult {
        private final AxeDefinitions definitions;
        private final Container container;
        private final int slot;
        private final Item item;
    }

    public static final Optional<AxeResult> getAxe(final Player player) {
        val level = player.getSkills().getLevel(Skills.WOODCUTTING);
        val inventory = player.getInventory().getContainer();
        val weapon = player.getEquipment().getId(EquipmentSlot.WEAPON);
        val values = AxeDefinitions.VALUES;
        AxeResult bestOptionalMatch = null;
        for (val def : values) {
            if (level < def.getLevelRequired())
                continue;
            if (weapon == def.getItemId()) {
            	if (def == AxeDefinitions.INFERNAL && player.getWeapon().getCharges() <= 0) {
            		bestOptionalMatch = new AxeResult(def, player.getEquipment().getContainer(), 3, player.getWeapon());
				} else {
					return Optional.of(new AxeResult(def, player.getEquipment().getContainer(), 3, player.getWeapon()));
				}
            }
            for (int slot = 0; slot < 28; slot++) {
                val item = inventory.get(slot);
                if (item == null || item.getId() != def.getItemId()) {
                    continue;
                }
				if (def == AxeDefinitions.INFERNAL && item.getCharges() <= 0 && bestOptionalMatch == null) {
					bestOptionalMatch = new AxeResult(def, player.getInventory().getContainer(), slot, item);
				} else {
					return Optional.of(new AxeResult(def, player.getInventory().getContainer(), slot, item));
				}
            }
        }
        return Optional.ofNullable(bestOptionalMatch);
    }

	@Override
	public boolean process() {
		if (ticks++ % 4 == 0) player.setAnimation(axe.getDefinitions().getEmote());
		return check();
	}

    public boolean success() {
	    assert definitions.getSpeed() > 0;
	    val level = player.getSkills().getLevel(Skills.WOODCUTTING) + (player.inArea("Woodcutting Guild") ? 7 : 0);
        val advancedLevels = level - definitions.getSpeed();
        return Math.min(Math.round(advancedLevels * 0.8F) + 20, 70) > Utils.random(100);
    }

	@Override
	public int processWithDelay() {
	    if (!success()) {
	        return axe.getDefinitions().getCutTime();
        }
		addLog();
		if (Utils.random(definitions.getFallChance() + (BoosterPerks.isActive(player, BoosterPerks.WOODCUTTING) ? (definitions.getFallChance() > 1 ? 1 : 0) : 0) - 1) == 0) {
			if (definitions == TreeDefinitions.SULLIUSCEP_TREE) {
				val currentIndex = player.getVarManager().getBitValue(SULLIUSCEP_INDEX_VARBIT);
				player.getVarManager().sendBit(SULLIUSCEP_INDEX_VARBIT, currentIndex < 5 ? currentIndex + 1 : 0);
			} else {
				player.getPacketDispatcher().sendSoundEffect(TREE_FALL_SOUND);
				if (onFall == null) {
					final WorldObject stump = new WorldObject(TreeDefinitions.getStumpId(tree.getId()), tree.getType(),
							tree.getRotation(), tree.getX(), tree.getY(), tree.getPlane());
					World.spawnObject(stump);
					WorldTasksManager.schedule(() -> World.spawnObject(tree), definitions.getRespawnDelay());
				} else {
					onFall.run();
				}
			}
			player.setAnimation(Animation.STOP);
			return -1;
		}
		if (!player.getInventory().hasFreeSlots()) {
			player.setAnimation(Animation.STOP);
			player.sendFilteredMessage("Not enough space in your inventory.");
			return -1;
		}
		return axe.getDefinitions().getCutTime();
	}

	private void addLog() {
		if (definitions.equals(TreeDefinitions.WILLOW_TREE)) {
			player.getDailyChallengeManager().update(SkillingChallenge.CHOP_WILLOW_LOGS);
			player.getAchievementDiaries().update(LumbridgeDiary.CHOP_WILLOWS);
			player.getAchievementDiaries().update(FaladorDiary.CHOP_BURN_WILLOW_LOGS, 0x1);
		} else if (definitions.equals(TreeDefinitions.TEAK_TREE)) {
			if (tree.getX() == 3510 && tree.getY() == 3073) {
				player.getAchievementDiaries().update(DesertDiary.CHOP_TEAK_LOGS);
			}
			player.getAchievementDiaries().update(KaramjaDiary.CUT_A_TEAK_LOG);
			player.getAchievementDiaries().update(WesternProvincesDiary.CHOP_AND_BURN_TEAK_LOGS, 0x1);
		} else if (definitions.equals(TreeDefinitions.MAHOGANY_TREE)) {
			player.getDailyChallengeManager().update(SkillingChallenge.CHOP_MAHOGANY_LOGS);
			player.getAchievementDiaries().update(KaramjaDiary.CUT_A_MAHOGANY_LOG);
			player.getAchievementDiaries().update(WesternProvincesDiary.CHOP_AND_BURN_MAHOGANY_LOGS, 0x1);
			player.getAchievementDiaries().update(MorytaniaDiary.CHOP_AND_BURN_MAHOGANY_LOGS, 0x1);
			player.getAchievementDiaries().update(KourendDiary.CHOP_SOME_MAHOGANY);
		} else if (definitions.equals(TreeDefinitions.YEW_TREE)) {
			player.getAchievementDiaries().update(VarrockDiary.CHOP_AND_BURN_YEW_LOGS, 0x1);
            SherlockTask.CHOP_YEW_TREE.progress(player);
		} else if (definitions.equals(TreeDefinitions.OAK)) {
			player.getAchievementDiaries().update(LumbridgeDiary.CHOP_AND_BURN_LOGS, 0x1);
			player.getAchievementDiaries().update(FremennikDiary.CHOP_AND_BURN_OAK_LOGS, 0x1);
		} else if (definitions.equals(TreeDefinitions.MAGIC_TREE)) {
			player.getDailyChallengeManager().update(SkillingChallenge.CHOP_MAGIC_LOGS);
			player.getAchievementDiaries().update(LumbridgeDiary.CHOP_MAGIC_LOGS);
			player.getAchievementDiaries().update(WildernessDiary.CUT_AND_BURN_MAGIC_LOGS, 0x1);
		} else if (definitions.equals(TreeDefinitions.REDWOOD_TREE)) {
			player.getDailyChallengeManager().update(SkillingChallenge.CHOP_REDWOOD_LOGS);
			player.getAchievementDiaries().update(KourendDiary.CHOP_REDWOODS);
		} else if (definitions.equals(TreeDefinitions.TREE) && tree.getName().equalsIgnoreCase("dying tree")) {
			player.getAchievementDiaries().update(VarrockDiary.CHOP_DOWN_DYING_TREE);
		} else if (definitions.equals(TreeDefinitions.CRYSTAL_TREE)) {
			player.getDailyChallengeManager().update(SkillingChallenge.CHOP_CRYSTAL_TREES);
		}
		
		player.getSkills().addXp(Skills.WOODCUTTING, definitions.getXp());
		awardNest();
		//Incinerate the logs
        if (definitions.getLogsId() != -1 && axe.getItem().getCharges() > 0 && axe.getDefinitions() == AxeDefinitions.INFERNAL && Utils.random(2) == 0) {
            player.getChargesManager().removeCharges(axe.getItem(), 1, axe.getContainer(), axe.getSlot());
            player.setGraphics(BURN_GFX);
            val fm = Objects.requireNonNull(Firemaking.MAP.get(definitions.getLogsId()));
            player.sendSound(2596);
            player.getSkills().addXp(Skills.FIREMAKING, fm.getXp() / 2F);
        } else {
            if (definitions.getLogsId() != -1) {
				player.sendFilteredMessage("You get some " + logName + ".");
				var amount = player.getPerkManager().isValid(PerkWrapper.LUMBERJACK) && Utils.random(100) <= 20 ? 2 : 1;
				if (amount == 2) {
					player.getPerkManager().consume(PerkWrapper.LUMBERJACK);
				}
				if (definitions.getLogsId() == 1511 && player.getEquipment().getItem(EquipmentSlot.HELMET) != null && player.getEquipment().getItem(EquipmentSlot.HELMET).getName().contains("Kandarin headgear")) {
					amount += 1;
				}
				player.getInventory().addItem(definitions.getLogsId(), amount).onFailure(remainder -> World.spawnFloorItem(remainder, player));
			}
        }
		if (Utils.random(999) == 0 && player.getSkills().getLevelForXp(Skills.WOODCUTTING) >= 34) {
			Item item = null;
			if (!player.containsItem(ItemId.LUMBERJACK_HAT)) {
				player.getInventory().addOrDrop(item = new Item(ItemId.LUMBERJACK_HAT));
				player.sendMessage("You find a lumberjack's hat in a branch!");
			} else if (!player.containsItem(ItemId.LUMBERJACK_TOP)) {
				player.getInventory().addOrDrop(item = new Item(ItemId.LUMBERJACK_TOP));
				player.sendMessage("You find a lumberjack's shirt in a branch!");
			} else if (!player.containsItem(ItemId.LUMBERJACK_LEGS)) {
				player.getInventory().addOrDrop(item = new Item(ItemId.LUMBERJACK_LEGS));
				player.sendMessage("You find a lumberjack's pants in a branch!");
			} else if (!player.containsItem(ItemId.LUMBERJACK_BOOTS)) {
				player.getInventory().addOrDrop(item = new Item(ItemId.LUMBERJACK_BOOTS));
				player.sendMessage("You find a lumberjack's boots in a branch!");
			}
			if (item != null) {
				player.setForceTalk(new ForceTalk("Ooh! I found something."));
				player.getCollectionLog().add(item);
			}
		}
		ClueItem.roll(player, definitions.getClueNestBaseChance(), player.getSkills().getLevel(Skills.WOODCUTTING), ClueItem::getClueNest);
	}

	private void awardNest() {
        if (definitions == TreeDefinitions.REDWOOD_TREE || definitions == TreeDefinitions.SULLIUSCEP_TREE) {
            return;
        }

		val isWearingWoodcuttingCape = SkillcapePerk.WOODCUTTING.isEffective(player);

		// woodcutting cape grants 10% higher chance to drop a nest, hence * 0.9
		if (Utils.random((int) (BIRD_NEST_CHANCE * (isWearingWoodcuttingCape ? 0.9 : 1))) == 0) {
			val nest = BirdNests.Nests.rollRandomNest(true);
			//Nests are uncommon and considering the afk-ness of the skill, they should remain on the ground for a longer period of time.
			World.spawnFloorItem(new Item(nest.getNestItemId()), player, 500, 0);
			player.sendMessage("<col=FF0000>A bird's nest falls out of the tree.</col>");
		}

	}

	@Override
	public void stop() {
	    player.setAnimation(Animation.STOP);
	}

}
