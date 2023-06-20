package com.zenyte.game.content.skills.thieving.actions;

import com.zenyte.game.content.achievementdiary.diaries.ArdougneDiary;
import com.zenyte.game.content.achievementdiary.diaries.FaladorDiary;
import com.zenyte.game.content.achievementdiary.diaries.LumbridgeDiary;
import com.zenyte.game.content.achievementdiary.diaries.WesternProvincesDiary;
import com.zenyte.game.content.skills.thieving.CoinPouch;
import com.zenyte.game.content.skills.thieving.PocketData;
import com.zenyte.game.content.skills.thieving.Thieving;
import com.zenyte.game.content.treasuretrails.clues.SherlockTask;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author Kris | 21. okt 2017 : 12:41.29
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class Pickpocket extends Action {

	private static final Animation STUN_ANIM = new Animation(422);
	private static final Animation PICKPOCKET_ANIM = new Animation(881);
	private static final Animation BLOCK_ANIM = new Animation(424);
	private static final Graphics STUN_GFX = new Graphics(80, 5, 60);
	
	private final PocketData data;
	private final NPC npc;

    @Override
    protected void onInterruption() {

    }

    /**
     * Checks if the player is wearing the Rogue equipment, which increases the chance of player
     * receiving double loot when pickpocketing. The chance of receiving double loot is 100% when
     * wearing the full set.
     * @return whether or not to double the loot.
     */
	private boolean isDoubleLoot() {
	    int count = 0;
	    val equipment = player.getEquipment();
	    if (equipment.getId(EquipmentSlot.HELMET) == 5554)
	        count++;
	    if (equipment.getId(EquipmentSlot.PLATE) == 5553)
	        count++;
	    if (equipment.getId(EquipmentSlot.HANDS) == 5556)
	        count++;
	    if (equipment.getId(EquipmentSlot.LEGS) == 5555)
	        count++;
	    if (equipment.getId(EquipmentSlot.BOOTS) == 5557)
	        count++;
	    return Utils.random(99) <= (count * 20);
    }
	
	@Override
	public boolean start() {
	    if (player.isUnderCombat(0)) {
            player.sendMessage("You can't do this while in combat.");
	        return false;
        }
	    if (data == null) {
	        player.sendMessage("You cannot pickpocket that NPC.");
	        return false;
        }
		if (player.getSkills().getLevel(Skills.THIEVING) < data.getLevel()) {
            player.sendMessage("You need a Thieving level of at least " + data.getLevel() + " to pickpocket " + npc.getName(player).toLowerCase() + ".");
			return false;
		} else if (!player.getInventory().hasFreeSlots()) {
		    val loot = data.getLoot();
            // If the pockets only contain coins and the player already has coin pouch(es), we allow them to pickpocket with "full" inventory.
            if (!(loot == null || (loot.length == 1 && loot[0] != null && CoinPouch.ITEMS.keySet().contains(loot[0].getId()) && player.getInventory().containsItem(loot[0].getId(), 1)))) {
                player.sendMessage("You need some free inventory space to pickpocket the " + npc.getName(player).toLowerCase() + ".");
                return false;
            }
		}
		if (npc.isDead() || npc.isFinished()) {
            player.sendMessage("Too late, " + npc.getDefinitions().getName().toLowerCase() + " is already dead.");
			return false;
		}
		if (data.getCoinPouch() != null && player.getInventory().getAmountOf(data.getCoinPouch().getItemId()) >= CoinPouch.MAX_POUCHES_PER_STACK) {
			player.sendMessage("You need to empty your coin pouches before you can continue pickpocketing.");
			return false;
		}
		player.faceEntity(npc);
		player.setAnimation(PICKPOCKET_ANIM);
		player.sendFilteredMessage("You attempt to pick the " + npc.getName(player).toLowerCase() + "'s pocket.");
		delay(1);
		player.lock();
		return true;
	}

	@Override
	public boolean process() {
		return !npc.isDead();
	}

	@Override
	public int processWithDelay() {
		if (!Thieving.success(player, data.getSuccessLevel())) {
			player.sendFilteredMessage("You fail to pick the " + npc.getName(player).toLowerCase() + "'s pocket.");
			npc.setAnimation(STUN_ANIM);
			npc.faceEntity(player);
			player.setAnimation(BLOCK_ANIM);
			val necklace = player.getEquipment().getId(EquipmentSlot.AMULET);
			if (!(necklace == ItemId.DODGY_NECKLACE && Utils.random(3) == 0)) {
				//dodgy necklace uses
				player.setGraphics(STUN_GFX);
				player.sendFilteredMessage("You have been stunned.");
				player.applyHit(new Hit(npc, Utils.random(data.getMinDamage(), data.getMaxDamage()), HitType.REGULAR));
				player.stun(data.getStunTime());
				WorldTasksManager.schedule(() -> player.setGraphics(Graphics.RESET), data.getStunTime() - 1);
			} else {
				val uses = player.getNumericAttribute("dodgy necklace uses").intValue() + 1;
				player.addAttribute("dodgy necklace uses", uses % 10);
				if (uses == 10) {
					player.getEquipment().set(EquipmentSlot.AMULET, null);
					player.sendMessage("Your dodgy necklace protects you. " + Colour.RED.wrap("It then crumbles to dust."));
				} else {
					player.sendFilteredMessage("Your dodgy necklace protects you. " + Colour.RED.wrap("It has " + (10 - uses) + " charge" + (uses == 9 ? "" : "s") + " left."));
				}
			}
			npc.setForceTalk(new ForceTalk(data.getPossibleMessages()[Utils.random(data.getPossibleMessages().length - 1)]));
			stop();
		} else {
			player.sendFilteredMessage("You successfully pick the " + npc.getName(player).toLowerCase() + "'s pocket.");
			if (data.equals(PocketData.HERO)) {
				player.getAchievementDiaries().update(ArdougneDiary.PICKPOCKET_A_HERO);
			} else if (data.equals(PocketData.GUARD)) {
				player.getAchievementDiaries().update(FaladorDiary.PICKPOCKET_FALADOR_GUARD);
			} else if (data.equals(PocketData.MASTER_FARMER)) {
				if (npc.getName(player).contains("Martin")) {
					player.getAchievementDiaries().update(LumbridgeDiary.PICKPOCKET_MASTER_GARDENER);
				}
				player.getDailyChallengeManager().update(SkillingChallenge.PICKPOCKET_MASTER_FARMERS);
				player.getAchievementDiaries().update(ArdougneDiary.PICKPOCKET_MASTER_FARMER);
			} else if (data.equals(PocketData.MAN)) {
				player.getAchievementDiaries().update(LumbridgeDiary.PICKPOCKET_A_MAN);
			} else if (data.equals(PocketData.GNOME)) {
				player.getAchievementDiaries().update(WesternProvincesDiary.PICKPOCKET_A_GNOME);
			} else if (data.equals(PocketData.ELF)) {
				player.getDailyChallengeManager().update(SkillingChallenge.PICKPOCKET_ELVES);
				player.getAchievementDiaries().update(WesternProvincesDiary.PICKPOCKET_ELF);
                SherlockTask.PICKPOCKET_AN_ELF.progress(player);
			} else if (data.equals(PocketData.FEMALE_HAM_MEMBER) || data.equals(PocketData.MALE_HAM_MEMBER)) {
				player.getDailyChallengeManager().update(SkillingChallenge.PICKPOCKET_HAM_MEMBERS);
			}
			player.getSkills().addXp(Skills.THIEVING, data.getExperience());
			if (Utils.random(200) == 0) {
				Item item = null;
			    if (!player.containsItem(5557)) {
			    	item = new Item(5557);
			        player.getInventory().addOrDrop(new Item(5557));
			        player.sendMessage("You managed to find some rogue boots.");
                } else if (!player.containsItem(5556)) {
					item = new Item(5556);
                    player.getInventory().addOrDrop(new Item(5556));
                    player.sendMessage("You managed to find some rogue gloves.");
                } else if (!player.containsItem(5554)) {
					item = new Item(5554);
                    player.getInventory().addOrDrop(new Item(5554));
                    player.sendMessage("You managed to find a rogue mask.");
                } else if (!player.containsItem(5555)) {
					item = new Item(5555);
                    player.getInventory().addOrDrop(new Item(5555));
					player.sendMessage("You managed to find some rogue trousers.");
				} else if (!player.containsItem(5553)) {
					item = new Item(5553);
					player.getInventory().addOrDrop(new Item(5553));
					player.sendMessage("You managed to find a rogue top.");
				}
				if (item != null) {
					player.getCollectionLog().add(item);
				}
			}
			data.generateRandomLoot(isDoubleLoot()).forEach(item -> player.getInventory().addOrDrop(item));
		}
		return -1;
	}

	@Override
	public void stop() {
		npc.setFaceEntity(null);
		delay(3);
		player.unlock();
	}

}
