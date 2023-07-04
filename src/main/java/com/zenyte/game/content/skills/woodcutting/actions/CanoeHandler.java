package com.zenyte.game.content.skills.woodcutting.actions;

import com.zenyte.game.content.achievementdiary.diaries.LumbridgeDiary;
import com.zenyte.game.content.achievementdiary.diaries.VarrockDiary;
import com.zenyte.game.content.skills.woodcutting.AxeDefinitions;
import com.zenyte.game.content.skills.woodcutting.CanoeDefinitions;
import com.zenyte.game.content.skills.woodcutting.CanoeLocation;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.cutscene.FadeScreen;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

import java.util.Objects;

/**
 * @author Tommeh | 7 feb. 2018 : 01:52:20
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class CanoeHandler {

	private static final Animation FALLING_TREE_ANIM = new Animation(3304);
	private static final Animation FLOAT_CANOE_ANIM = new Animation(3301);
	
	private static final Location EDGEVILLE_CANOE_LOCATION = new Location(3130, 3508, 0);
	private static final Location LUMBRIDGE_CANOE_LOCATION = new Location(3241, 3235, 0);

	public static void sendCanoeShapingInterface(final Player player, final WorldObject object) {
		player.addWalkSteps(object.getX() + 2, object.getY() + 2);
		WorldTasksManager.schedule(() -> {
			if (object.getRotation() == 2) {
				player.setFaceLocation(new Location(player.getX(), player.getY() - 2, player.getPlane()));
			} else {
				player.setFaceLocation(new Location(player.getX() - 2, player.getY(), player.getPlane()));
			}
		});
		player.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, 52);
		player.getPacketDispatcher().sendComponentVisibility(52, 6, false);
		player.getPacketDispatcher().sendComponentVisibility(52, 32, true);
		player.getPacketDispatcher().sendComponentVisibility(52, 3, false);
		player.getPacketDispatcher().sendComponentVisibility(52, 35, true);
		player.getPacketDispatcher().sendComponentVisibility(52, 13, false);
		player.getPacketDispatcher().sendComponentVisibility(52, 29, true);
		player.getTemporaryAttributes().put("CanoeObject", object);
	}

	public static void chopCanoeStation(final Player player, final WorldObject object) {
        val optionalAxe = Woodcutting.getAxe(player);
        if (!optionalAxe.isPresent()) {
            player.sendMessage("You do not have an axe which you have the woodcutting level to use.");
            return;
        }
		player.lock();
		if (object.getRotation() == 2) {
			player.addWalkSteps(object.getX() + 4, object.getY() + 2);
		} else {
			player.addWalkSteps(object.getX() + 2, object.getY());
		}

		WorldTasksManager.schedule(new WorldTask() {
			int ticks;

			@Override
			public void run() {
				switch (ticks++) {
				case 1:
					if (object.getRotation() == 2) {
						player.setFaceLocation(new Location(player.getX(), player.getY() - 2, player.getPlane()));
					} else {
						player.setFaceLocation(new Location(player.getX() - 2, player.getY(), player.getPlane()));
					}
					break;
				case 2:
					player.setAnimation(optionalAxe.get().getDefinitions().getEmote());
					break;
				case 3:
					player.setAnimation(Animation.STOP);
					player.getVarManager().sendBit(object.getDefinitions().getVarbit(), 2);
					World.sendObjectAnimation(object, FALLING_TREE_ANIM);
					player.getVarManager().sendBit(object.getDefinitions().getVarbit(), 9);
					break;
				case 4:
					player.getVarManager().sendBit(object.getDefinitions().getVarbit(), 10);
					player.unlock();
					stop();
					break;
				}
			}
		}, 0, 1);
	}

	public static void shapeCanoeStation(final Player player, final int componentId) {
        val optionalAxe = Woodcutting.getAxe(player);
        if (!optionalAxe.isPresent()) {
            player.sendMessage("You do not have an axe which you have the woodcutting level to use.");
            return;
        }
        val axe = optionalAxe.get();
		val canoe = componentId >= 3 && componentId <= 13 ? CanoeDefinitions.get(componentId) : CanoeDefinitions.ENTRIES[componentId - 24];
		if (player.getSkills().getLevel(Skills.WOODCUTTING) < canoe.getLevel()) {
			player.sendMessage("You need a Woodcutting level of at least " + canoe.getLevel() + " to shape the tree into that canoe.");
			return;
		}
		val object = (WorldObject) player.getTemporaryAttributes().get("CanoeObject");
		player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
		if (object.getRotation() == 2) {
			player.setFaceLocation(new Location(player.getX(), player.getY() - 2, player.getPlane()));
		} else {
			player.setFaceLocation(new Location(player.getX() - 2, player.getY(), player.getPlane()));
		}
		player.lock();
		WorldTasksManager.schedule(new WorldTask() {
			int ticks;

			@Override
			public void run() {
				if (ticks >= 0 && ticks <= (canoe.ordinal() + 2)) {
					val animation = new Animation(axe.getDefinitions().equals(AxeDefinitions.DRAGON) ? 3292 :
                            3291 - axe.getDefinitions().ordinal() + 1);
					player.setAnimation(animation);
				} else if (ticks == (canoe.ordinal() + 3)) {
					if (canoe.equals(CanoeDefinitions.WAKA) && object.withinDistance(EDGEVILLE_CANOE_LOCATION, 10)) {
						player.getAchievementDiaries().update(VarrockDiary.MAKE_WAKA_CANOE);
					}
					player.getTemporaryAttributes().put("CanoeDefinitions", canoe);
					player.getVarManager().sendBit(object.getDefinitions().getVarbit(), canoe.getBit());
					player.getSkills().addXp(Skills.WOODCUTTING, canoe.getExperience());
					player.unlock();
					stop();
				}
				ticks++;
			}
		}, 0, 1);
	}

	public static void floatCanoe(final Player player, final WorldObject object) {
		val bit = player.getVarManager().getBitValue(object.getDefinitions().getVarbit());
		player.setAnimation(FLOAT_CANOE_ANIM);
		player.getVarManager().sendBit(object.getDefinitions().getVarbit(), 2);
		World.sendObjectAnimation(object, FALLING_TREE_ANIM);
		player.getVarManager().sendBit(object.getDefinitions().getVarbit(), bit + 4);
		WorldTasksManager.schedule(() -> player.getVarManager().sendBit(object.getDefinitions().getVarbit(), bit + 10), 2);
	}

	public static void paddleCanoe(final Player player, final int componentId) {
		val location = CanoeLocation.get(componentId);
		if (location == null) {
		    return;
        }
		var obj = player.getTemporaryAttributes().get("CanoeObject");
		if (!(obj instanceof WorldObject)) {
			return;
		}
		val object = (WorldObject) obj;
		obj = player.getTemporaryAttributes().get("CanoeDefinitions");
		if (!(obj instanceof CanoeDefinitions)) {
			return;
		}
		val canoe = (CanoeDefinitions) obj;
		if (location.getTile().withinDistance(player.getLocation(), 15)) {
		    player.sendMessage("You're already there.");
		    return;
        }
		player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
		player.lock(6);
        new FadeScreen(player, () -> {
            if (Objects.equals(canoe, CanoeDefinitions.WAKA) && player.getLocation().withinDistance(LUMBRIDGE_CANOE_LOCATION, 10) && location.equals(CanoeLocation.EDGEVILLE)) {
                player.getAchievementDiaries().update(LumbridgeDiary.TRAVEL_TO_EDGEVILLE_ON_CANOE);
            }
            player.getVarManager().sendBit(object.getDefinitions().getVarbit(), 0);
            player.setLocation(location.getTile());
            player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
        }).fade(5);
	}

}
