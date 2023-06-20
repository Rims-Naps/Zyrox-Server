package com.zenyte.game.world.entity.player;

import com.zenyte.Constants;
import com.zenyte.cores.WorldThread;
import com.zenyte.game.content.minigame.castlewars.CastleWars;
import com.zenyte.game.content.minigame.castlewars.CastleWarsArea;
import com.zenyte.game.content.minigame.duelarena.Duel;
import com.zenyte.game.item.Item;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.pathfinding.events.player.EntityEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.action.PeltAction;
import com.zenyte.game.world.entity.player.action.PlayerFollow;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.area.plugins.TradePlugin;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Kris | 23. dets 2017 : 0:42.03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class PlayerHandler {

	private static final Logger LOGGER = LogManager.getLogger(PlayerHandler.class);

	public static final void handle(final Player player, final Player p2, final boolean forcerun, final int optionId) {
		if (!p2.isRunning() || p2.isFinished()) {
			return;
		}
		if (!player.isVisibleInViewport(p2)) {
            player.sendMessage("Unable to find " + p2.getPlayerInformation().getDisplayname() + ".");
            return;
        }
		final String option = Utils.getOrDefault(player.getOptions()[optionId], "null");
		if (option.equals("Attack") || option.equals("Fight")) {
            PlayerCombat.attackEntity(player, p2, null);
            return;
        }

		player.stopAll();
		if (forcerun) {
			if (p2.getPrivilege().equals(Privilege.ADMINISTRATOR)) {
				player.setLocation(new Location(p2.getLocation()));
			}
			player.setRun(true);
			return;
		}
		if (player.isLocked() || player.isFullMovementLocked()) {
			return;
		}
		if (option.equals("Follow")) {
			player.getDelayedActionManager().setAction(new PlayerFollow(p2));
			return;
		}

		if (option.equals("Pelt")) {
			if (player.getNumericTemporaryAttribute("pelt lock").longValue() >= WorldThread.WORLD_CYCLE) {
				return;
			}
			player.addTemporaryAttribute("pelt lock", WorldThread.WORLD_CYCLE + 4);
			player.faceEntity(p2);
			player.getActionManager().setAction(new PeltAction(p2));
			return;
		}

		player.setRouteEvent(new EntityEvent(player, new EntityStrategy(p2), () -> {
			player.stopAll();
			player.faceEntity(p2);
			switch (option) {

				case "Challenge":
					handleChallengeRequest(player, p2);
					return;

				case "Trade with":
					handleTradeRequest(player, p2);
					return;

				case "Take-from":
					handleTakeFlagAttempt(player, p2);
					return;

				case "Slash":
					player.setAnimation(new Animation(7328));
					return;
				case "Whack":
					val weapon = player.getWeapon();
					if (weapon != null) {
						player.setInvalidAnimation(WhackAnimation.getAnimation(weapon));
					}
					return;
				default:
					LOGGER.info("Unhandled player option: " + option);
            }
		}, false));
	}

	private static void handleTakeFlagAttempt(final Player player, final Player p2) {
		if(!CastleWars.isActive()) {
			player.sendMessage("Castle-wars isn't currently active!");
			return;
		}

		if(!CastleWars.isUserPlaying(player) || !CastleWars.isUserPlaying(p2)) {
			player.sendMessage( ( !CastleWars.isUserPlaying(player) ? "You" : "They") + " aren't in a castle-wars game at this moment!");
			return;
		}

		val p1Team = CastleWars.getTeam(player);
		val p2Team = CastleWars.getTeam(p2);

		if(!CastleWarsArea.hasFlag(p2)) {
			player.sendMessage("This player doesn't have a flag for you to take!");
			return;
		}

		if(p1Team != p2Team) {
			player.sendMessage("You must kill the other team in order to take your flag back!");
			return;
		}

		if(p2.getNumericTemporaryAttribute("flag hold time").intValue() < 300) {
			player.sendMessage("You can't take the flag from this user yet!");
			return;
		}

		val flagId = p2.getEquipment().getId(EquipmentSlot.WEAPON);
		if(flagId != 4037 && flagId != 4039) {
			player.sendMessage("This player doesn't have a flag for you to take!");
			return;
		}

		// If player has no weapon currently
		if(player.getEquipment().getId(EquipmentSlot.WEAPON) != -1) {
			if(player.getEquipment().getId(EquipmentSlot.SHIELD) != -1) {
				if (player.getInventory().getFreeSlots() < 2) {
					player.sendMessage("You won't have enough room in for your equipment if you take the flag!");
					return;
				}

				player.getEquipment().unequipItem(EquipmentSlot.SHIELD.getSlot());
			} else {
				if(!player.getInventory().hasFreeSlots()) {
					player.sendMessage("You won't have enough room in for your equipment if you take the flag!");
					return;
				}
			}

			player.getEquipment().unequipItem(EquipmentSlot.WEAPON.getSlot());
			player.getEquipment().refresh();
		}

		p2.getEquipment().set(EquipmentSlot.WEAPON, null);
		p2.getEquipment().refresh();
		p2.sendMessage("You held the flag for more than 3 minutes, and a teammate has taken it from you.");

		player.getEquipment().set(EquipmentSlot.WEAPON, new Item(flagId));
		player.getEquipment().refresh();
		player.sendMessage("You take the flag from your teammate.");
	}

	private static void handleChallengeRequest(final Player player, final Player p2) {
		if (!Constants.DUEL_ARENA) {
			player.sendMessage("The Duel Arena is temporarily unavailable right now.");
			return;
		}
		if (player.getDuel() != null) {
			player.sendMessage("You're already in a duel.");
			return;
		}
		if (p2.getDuel() != null) {
			player.sendMessage("The other player is already in a duel.");
			return;
		}
		if (p2.getTemporaryAttributes().get("DuelTarget") == player) {
			p2.getTemporaryAttributes().remove("DuelTarget");
			player.getInterfaceHandler().closeInterfaces();
			p2.getInterfaceHandler().closeInterfaces();
			val duel = new Duel(player, p2);
			duel.openChallenge();
			return;
		}
		player.getTemporaryAttributes().put("DuelTarget", p2);
		val name = player.getPlayerInformation().getDisplayname();
		player.sendMessage("Challenging " + p2.getPlayerInformation().getDisplayname() + "...");
		p2.getPacketDispatcher().sendChallengeRequest(name + " wishes to duel with you.", name);
		return;
	}

	private static void handleTradeRequest(final Player player, final Player p2) {
		if (player.isIronman() && (!player.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR) && !p2.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR))) {
			player.sendMessage("You're an Iron Man. You stand alone.");
			return;
		}
		if (p2.isIronman() && (!player.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR) && !p2.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR))) {
			player.sendMessage(p2.getName() + " is an Iron Man. He stands alone.");
			return;
		}
		val area = player.getArea();
		if (area instanceof TradePlugin && !((TradePlugin) area).canTrade(player, p2)) {
			return;
		}
		if (p2.getTemporaryAttributes().get("TradeTarget") == player) {
			p2.getTemporaryAttributes().remove("TradeTarget");
			player.getInterfaceHandler().closeInterfaces();
			p2.getInterfaceHandler().closeInterfaces();
			player.getTrade().openTradeScreen(p2);
			p2.getTrade().openTradeScreen(player);
			return;
		}
		if (p2.isLocked() || p2.getInterfaceHandler().containsInterface(InterfacePosition.CENTRAL)) {
			player.sendMessage("Other player is busy at the moment.");
			return;
		}
		player.getTemporaryAttributes().put("TradeTarget", p2);
		player.getTrade().sendTradeRequest(p2);
		return;
	}


}
