package com.zenyte.plugins.object;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.Door;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;

import lombok.val;

/**
 * @author Kris | 29. mai 2018 : 01:37:05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class CooksGuildEntranceDoor implements ObjectAction {
	private static final int REQUIRED_LEVEL = 32;

	public static boolean hasEntranceEquipment(final Player player) {
		val hat = player.getEquipment().getId(EquipmentSlot.HELMET);
		val hasHat = hat == ItemId.CHEFS_HAT || hat == ItemId.GOLDEN_CHEFS_HAT;
		val hasCape = SkillcapePerk.COOKING.isEffective(player);
		val hasVarrockArmour = DiaryReward.VARROCK_ARMOUR3.isEquipped(player) || DiaryReward.VARROCK_ARMOUR4.isEquipped(player);
		return hasHat || hasCape || hasVarrockArmour;
	}

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
		val cooking = player.getSkills().getLevelForXp(Skills.COOKING);
		if (player.inArea("Cook's Guild") || cooking >= REQUIRED_LEVEL && hasEntranceEquipment(player)) {
			player.lock(3);
			player.addWalkSteps(object.getX(), object.getY());
			WorldTasksManager.schedule(new WorldTask() {
				private int ticks;
				private WorldObject door;
				@Override
				public void run() {
					switch (ticks++) {
						case 0:
							door = Door.handleGraphicalDoor(object, null);
						return;
					case 1:
						if (!player.inArea("Cook's Guild")) {
							player.addWalkSteps(door.getX(), door.getY(), 1, false);
						} else {
							player.addWalkSteps(object.getX(), object.getY(), 1, false);
						}
						return;
					case 3:
						Door.handleGraphicalDoor(door, object);
						stop();
						return;
					}
				}
				
			}, 0, 0);
			return;
		} else {
			World.sendClosestNPCDialogue(player, NpcId.HEAD_CHEF, new Dialogue(player) {
				@Override
				public void buildDialogue() {
					npc("Sorry. Only the finest chefs are allowed in here. Get your cooking level up to " + REQUIRED_LEVEL
							+ " and come back wearing a chef's hat.");
				}
			});
			if (cooking < REQUIRED_LEVEL) {
				player.sendMessage("You need a Cooking level of 32 to enter the Chefs' guild.");
			}
			return;
		}
	}

	@Override
	public Object[] getObjects() {
		return new Object[] {ObjectId.DOOR_24958};
	}
}
