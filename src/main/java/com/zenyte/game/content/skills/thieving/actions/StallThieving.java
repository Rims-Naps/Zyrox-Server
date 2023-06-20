package com.zenyte.game.content.skills.thieving.actions;

import com.zenyte.game.content.achievementdiary.diaries.ArdougneDiary;
import com.zenyte.game.content.achievementdiary.diaries.FremennikDiary;
import com.zenyte.game.content.achievementdiary.diaries.KourendDiary;
import com.zenyte.game.content.achievementdiary.diaries.VarrockDiary;
import com.zenyte.game.content.skills.thieving.Stall;
import com.zenyte.game.content.skills.thieving.StallType;
import com.zenyte.game.content.treasuretrails.clues.SherlockTask;
import com.zenyte.game.item.ImmutableItem;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.game.world.region.area.apeatoll.Greegree;
import lombok.val;

/**
 * @author Kris | 21. okt 2017 : 19:17.05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 * profile</a>}
 */
public class StallThieving extends Action {

	private static final Animation THIEVING_ANIM = new Animation(881);
	private static final ForceTalk FORCE_TALK = new ForceTalk("Hey! Get your hands off there!");
	private static final ForceTalk DOG_FORCE_TALK = new ForceTalk("Woof! Woof! Woof!");

	public static final boolean handleStall(final Player player, final WorldObject object) {
		val stall = Stall.getStall(object.getId());
		if (stall == null) {
			return false;
		}
		player.getActionManager().setAction(new StallThieving(stall, object));
		return true;
	}

	public StallThieving(final Stall stall, final WorldObject object) {
		this.stall = stall;
		this.object = object;
	}

	private final Stall stall;
	private final WorldObject object;

	@Override
	public boolean start() {
		if (player.getSkills().getLevel(Skills.THIEVING) < stall.getType().getLevel()) {
			player.sendMessage("You need a Thieving level of at least " + stall.getType().getLevel() + " to steal from this stall.");
			return false;
		}
		if (!player.getInventory().hasFreeSlots()) {
			player.sendMessage("You need some free inventory slots to steal from this stall.");
			return false;
		}
		if (player.isUnderCombat(0)) {
			player.sendMessage("You can't do this while in combat.");
			return false;
		}
		if (!World.containsObjectWithId(object, object.getId())) {
			return false;
		}
		if (Stall.APE_ATOLL_STALLS.contains(stall) && Greegree.MAPPED_VALUES.containsKey(player.getEquipment().getId(EquipmentSlot.WEAPON))) {
			player.sendMessage("I wouldn't like to blow my cover by getting caught stealing.");
			return false;
		}
		player.setAnimation(THIEVING_ANIM);
		player.lock();
		delay(2);
		return true;
	}

	@Override
	public boolean process() {
		return true;
	}

	@Override
	public int processWithDelay() {
		player.unlock();
		player.getSkills().addXp(Skills.THIEVING, stall.getType().getExperience());
		if (stall.getType().equals(StallType.GEM_STALL)) {
            SherlockTask.STEAL_GEM_FROM_ARDOUGNE_MARKET.progress(player);
        }
		addLoot();
		checkGuards();
		spawnEmptyStall();
		return -1;
	}

	private final void spawnEmptyStall() {
		if (World.containsObjectWithId(object, object.getId())) {
			final WorldObject obj = new WorldObject(object);
			obj.setId(stall.getEmptyId());
			World.spawnObject(obj);
			WorldTasksManager.schedule(() -> World.spawnObject(object), Math.max(0, stall.getType().getTime() / 5));
		}
	}

	private final void checkGuards() {
	    val list = CharacterLoop.find(player.getLocation(), 5, NPC.class, n -> {
	        val name = n.getDefinitions().getName().toLowerCase();
                    return !n.isDead() && (name.contains("guard") || name.contains("tzhaar-ket"))
							&& n.getDefinitions().containsOption("Attack")
							&& !n.isProjectileClipped(player.getLocation(), false);
				}
        );
	    if (list.isEmpty()) {
			return;
		} //TODO 10% extra chance on not getting caught for ardy cloak 1
	    val npc = list.get(0);
        npc.getCombat().setTarget(player);
		npc.setForceTalk(npc.getName(player).contains("dog") ? DOG_FORCE_TALK : FORCE_TALK);
	}

	private final void addLoot() {
		if (stall.equals(Stall.ARDOUGNE_AND_KOUREND_BAKERS_STALL)) {
			player.getAchievementDiaries().update(ArdougneDiary.STEAL_CAKE);
		} else if (stall.equals(Stall.VARROCK_AND_KOUREND_TEA_STALL)) {
			player.getAchievementDiaries().update(VarrockDiary.STEAL_FROM_TEA_STALL);
		} else if (stall.equals(Stall.KELDAGRIM_BAKERY_STALL) || stall.equals(Stall.KELDAGRIM_CRAFTING_STALL)) {
			player.getAchievementDiaries().update(FremennikDiary.STEAL_FROM_CRAFTING_STALL);
		} else if (stall.equals(Stall.RELLEKKA_FISH_STALL)) {
			player.getAchievementDiaries().update(FremennikDiary.STEAL_FROM_RELLEKKA_FISH_STALLS);
		} else if (stall.equals(Stall.KELDAGRIM_GEM_STALL)) {
			player.getAchievementDiaries().update(FremennikDiary.STEAL_FROM_GEM_STALL);
		} else if (stall.equals(Stall.KOUREND_FRUIT_STALL)) {
			player.getAchievementDiaries().update(KourendDiary.STEAL_FROM_FOOD_STALL);
		}  else if (stall.equals(Stall.TZHAAR_GEM_SHOP_COUNTER)) {
			player.getDailyChallengeManager().update(SkillingChallenge.PICKPOCKET_GEM_STALL_TZHAARS);
		}
		if (stall.getType().isRandomize()) {
			final ImmutableItem random = stall.getType().getItems()[Utils.random(stall.getType().getItems().length - 1)];
			if (random == null) {
				return;
			}
			player.getInventory().addItem(new Item(random.getId(), Utils.random(random.getMinAmount(), random.getMaxAmount())));
		} else {
			final double random = Utils.getRandomDouble(100);
			double currentRoll = 0;
			ImmutableItem loot = null;
			final ImmutableItem[] lootArr = stall.getType().getItems();
			for (int i = lootArr.length - 1; i >= 0; i--) {
				final ImmutableItem l = lootArr[i];
				if (l == null) {
					continue;
				}
				if ((currentRoll += l.getRate()) >= random) {
					loot = l;
					break;
				}
			}
			if (loot == null) {
				return;
			}
			player.getInventory().addItem(new Item(loot.getId(), Utils.random(loot.getMinAmount(), loot.getMaxAmount())));
		}
	}

	@Override
	public void stop() {}

}
