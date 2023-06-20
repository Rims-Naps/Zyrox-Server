package com.zenyte.game.content.skills.runecrafting.abyss;

import com.zenyte.game.content.skills.firemaking.FiremakingAction;
import com.zenyte.game.content.skills.firemaking.FiremakingTool;
import com.zenyte.game.content.skills.mining.MiningDefinitions;
import com.zenyte.game.content.skills.mining.MiningDefinitions.PickaxeDefinitions;
import com.zenyte.game.content.skills.woodcutting.AxeDefinitions;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.action.combat.magic.CombatSpell;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.cutscene.FadeScreen;
import com.zenyte.plugins.object.AbyssObstacleObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

/**
 * @author Tommeh | 29 jul. 2018 | 21:30:19
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
@AllArgsConstructor
public enum ObstacleType {

	TENDRILS((player, object, destination) -> {
		player.sendMessage("You attempt to chop your way through...");
		val chance = player.getSkills().getLevel(Skills.WOODCUTTING) + 1;
		AxeDefinitions axe = null;
		for (val a : AxeDefinitions.VALUES) {
			if (a.equals(AxeDefinitions.NO_LEVEL)) {
				continue;
			}
			if (player.getInventory().containsItem(a.getItemId(), 1)) {
				if (player.getSkills().getLevel(Skills.WOODCUTTING) >= a.getLevelRequired()) {
					axe = a;
					break;
				}
			}
			if (player.getEquipment().getId(EquipmentSlot.WEAPON.getSlot()) == a.getItemId()) {
				if (player.getSkills().getLevel(Skills.WOODCUTTING) >= a.getLevelRequired()) {
					axe = a;
					break;
				}
			}
		}
		val def = axe;
		if (axe == null) {
			player.sendMessage("You need a axe for which you have the required Woodcutting level to chop the tendrils.");
			player.unlock();
			return;
		}
		player.setAnimation(axe.getEmote());
		WorldTasksManager.schedule(new WorldTask() {
			int ticks;

			@Override
			public void run() {
				player.setAnimation(def.getEmote());
				switch (ticks++) {
				case 1:
				case 2:
					if (Utils.random(100) < chance) {
						player.getVarManager().sendBit(625, 12 + ticks);
						if (ticks == 2) {
							player.sendMessage("...and manage to break through the tendrils.");
						}
					} else {
						player.sendMessage("...but fail to break-up the tendrils.");
						player.unlock();
						player.getReceivedHits().clear();
						player.getTemporaryAttributes().put("abyss obstacle delay", System.currentTimeMillis() + 600);
						stop();
					}
					break;
				case 4:
					player.setAnimation(Animation.STOP);
					player.getVarManager().sendBit(625, 0);
					player.setLocation(destination);
					player.getSkills().addXp(Skills.WOODCUTTING, 25);
					player.unlock();
                    player.getReceivedHits().clear();
                    player.blockIncomingHits();
					stop();
					break;
				}
			}
		}, 0, 1);
	}),
	ROCKS((player, object, destination) -> {
		player.sendMessage("You attempt to mine your way through...");
		val chance = player.getSkills().getLevel(Skills.MINING) + 1;
		PickaxeDefinitions pickaxe = null;
		for (val id : MiningDefinitions.tools.keySet()) {
			if (player.getInventory().containsItem(id, 1)) {
				if (player.getSkills().getLevel(Skills.MINING) >= PickaxeDefinitions.getDef(id).getLevel()) {
					pickaxe = PickaxeDefinitions.getDef(id);
					break;
				}
			}
			if (player.getEquipment().getId(EquipmentSlot.WEAPON.getSlot()) == id) {
				if (player.getSkills().getLevel(Skills.MINING) >= PickaxeDefinitions.getDef(id).getLevel()) {
					pickaxe = PickaxeDefinitions.getDef(id);
					break;
				}
			}
		}
		if (pickaxe == null) {
			player.sendMessage("You need a pickaxe for which you have the required Mining level to mine this rock.");
			player.unlock();
			return;
		}
		player.setAnimation(pickaxe.getAnim());
		WorldTasksManager.schedule(new WorldTask() {
			int ticks;

			@Override
			public void run() {
				switch (ticks++) {
				case 1:
				case 2:
					if (Utils.random(100) < chance) {
						player.getVarManager().sendBit(625, 10 + ticks);
						if (ticks == 2) {
							player.sendMessage("...and manage to break through the rock.");
							player.setAnimation(Animation.STOP);
						}
					} else {
						player.sendMessage("...but fail to break-up the rock.");
						player.unlock();
                        player.getReceivedHits().clear();
                        player.getTemporaryAttributes().put("abyss obstacle delay", System.currentTimeMillis() + 600);
						stop();
					}
					break;
				case 4:
					player.getVarManager().sendBit(625, 0);
					player.setLocation(destination);
					player.getSkills().addXp(Skills.MINING, 25);
                    player.getReceivedHits().clear();
                    player.blockIncomingHits();
					player.unlock();
					stop();
					break;
				}
			}
		}, 0, 1);

	}),
	EYES((player, object, destination) -> {
		val chance = player.getSkills().getLevel(Skills.THIEVING) + 1;
		player.sendMessage("You use your thieving skills to misdirect the eyes...");
		WorldTasksManager.schedule(new WorldTask() {
			int ticks;

			@Override
			public void run() {
				switch (ticks++) {
				case 1:
					if (Utils.random(100) < chance) {
						player.setAnimation(AbyssObstacleObject.EYES_ANIMATION);
					} else {
						player.sendMessage("...but fail to get their attention.");
						player.unlock();
                        player.getReceivedHits().clear();
                        player.getTemporaryAttributes().put("abyss obstacle delay", System.currentTimeMillis() + 600);
						stop();
					}
					break;
				case 2:
					player.getVarManager().sendBit(625, 18);
					break;
				case 4:
					player.getVarManager().sendBit(625, 19);
					break;
				case 5:
					player.sendMessage("...and sneak past while they're not looking.");
					break;
				case 6:
					player.getVarManager().sendBit(625, 0);
					player.setLocation(destination);
					player.getSkills().addXp(Skills.THIEVING, 25);
                    player.getReceivedHits().clear();
                    player.blockIncomingHits();
					player.unlock();
					stop();
					break;
				}
			}
		}, 0, 1);
	}),
	GAP((player, object, destination) -> {
		val chance = player.getSkills().getLevel(Skills.AGILITY) + 1;
		player.sendMessage("You attempt to squeeze through the narrow gap...");
		player.setAnimation(AbyssObstacleObject.GAP_START_ANIMATION);
		WorldTasksManager.schedule(new WorldTask() {
			int ticks;

			@Override
			public void run() {
				if (ticks++ == 1) {
					if (Utils.random(100) < chance) {
						new FadeScreen(player, () -> {
							player.getVarManager().sendBit(625, 0);
							player.setAnimation(AbyssObstacleObject.GAP_END_ANIMATION);
							player.setLocation(destination);
							player.getSkills().addXp(Skills.AGILITY, 25);
							player.sendMessage("...and you manage to crawl through.");
							player.getReceivedHits().clear();
							player.blockIncomingHits();
						}).fade(1);
					} else {
						player.sendMessage("...but fail to crawl through.");
						player.lock(1);
						player.getReceivedHits().clear();
						player.blockIncomingHits();
					}
					stop();
				}
			}
		}, 0, 1);
	}),
	BOIL((player, object, destination) -> {
		val chance = player.getSkills().getLevel(Skills.FIREMAKING) + 1;
		player.sendMessage("You attempt to set the blockade on fire...");
		WorldTasksManager.schedule(new WorldTask() {
			int ticks;

			@Override
			public void run() {
				switch (ticks++) {
				case 1:
					if (!player.getInventory().containsItem(FiremakingAction.TINDERBOX)) {
						player.sendMessage("...but you don't have a tinderbox to burn it!");
						player.unlock();
                        player.getReceivedHits().clear();
                        player.getTemporaryAttributes().put("abyss obstacle delay", System.currentTimeMillis() + 600);
						stop();
						return;
					}
					player.setAnimation(FiremakingTool.TINDERBOX.getAnimation());
					if (Utils.random(100) >= chance) {
						player.sendMessage("...but fail to burn it of your way.");
						player.unlock();
                        player.getReceivedHits().clear();
                        player.getTemporaryAttributes().put("abyss obstacle delay", System.currentTimeMillis() + 600);
						stop();
						return;
					}
					break;
				case 3:
					player.getVarManager().sendBit(625, 16);
					player.setAnimation(Animation.STOP);
					break;
				case 6:
					player.getPacketDispatcher().sendGraphics(CombatSpell.FIRE_WAVE.getHitGfx(), object);
					player.getVarManager().sendBit(625, 17);
					break;
				case 7:
					player.sendMessage("..and manage to burn it down and get past.");
					break;
				case 8:
					player.getVarManager().sendBit(625, 0);
					player.setLocation(destination);
					player.getSkills().addXp(Skills.FIREMAKING, 25);
					player.unlock();
                    player.getReceivedHits().clear();
                    player.blockIncomingHits();
					stop();
					break;
				}
			}
		}, 0, 1);
	}),
	PASSAGE((player, object, destination) -> {
		player.lock(1);
		player.blockIncomingHits();
        player.getReceivedHits().clear();
        player.getTemporaryAttributes().put("abyss obstacle delay", System.currentTimeMillis() + 600);
		player.setLocation(destination);
	});

	@Getter
	private final ObstacleScript script;

}
