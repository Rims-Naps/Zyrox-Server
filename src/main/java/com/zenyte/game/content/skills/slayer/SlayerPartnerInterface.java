package com.zenyte.game.content.skills.slayer;

import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.ui.UserInterface;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.PlainChat;

import lombok.val;

/**
 * @author Kris | 30. aug 2018 : 19:18:25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class SlayerPartnerInterface implements UserInterface {

	@Override
	public void handleComponentClick(final Player player, final int interfaceId, final int componentId, final int slotId, final int itemId,
			final int optionId, final String option) {
		if (componentId != 7) {
			return;
		}
		val partner = player.getSlayer().getPartner();
		if (partner == null) {
			player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
			player.sendInputName("Who would you like as your partner?", name -> {
				val target = World.getPlayerByDisplayname(name);
				val value = target == null ? 2 : target.getIntSetting(Setting.PRIVATE_FILTER);
				if (target == null || target == player || value == 2 || value == 1 && !target.getSocialManager().containsFriend(player.getUsername())) {
					player.sendMessage("That player is offline, or has privacy mode enabled.");
					return;
				}
				if (!target.getBooleanSetting(Setting.ACCEPT_AID)) {
					player.sendMessage(target.getName() + " is not accepting aid.");
					return;
				}
				if (target.isLocked() || target.isUnderCombat() || target.getInterfaceHandler().containsInterface(InterfacePosition.CENTRAL)
						|| target.getInterfaceHandler().containsInterface(InterfacePosition.CHATBOX)) {
					player.sendMessage("That player is currently busy.");
					return;
				}
				val chat = new PlainChat(player, "Sending partnership offer...", false);
				val requestingPlayer = player;
				player.getDialogueManager().start(chat);
				target.getDialogueManager().start(new Dialogue(target) {
					@Override
					public void buildDialogue() {
						options("Slayer Partner offer: " + requestingPlayer.getName() + "(" + requestingPlayer.getSkills().getLevelForXp(Skills.SLAYER)
								+ ")", new DialogueOption[] { new DialogueOption("Accept.", () -> {
									if (player.isLocked() || player.isFinished() || player.isUnderCombat()
											|| player.getInterfaceHandler().containsInterface(InterfacePosition.CENTRAL)) {
										player.sendMessage("The other player is currently busy.");
										return;
									}
									if (requestingPlayer.getDialogueManager().getLastDialogue() == chat) {
										requestingPlayer.getDialogueManager().finish();
									}
									player.sendMessage(requestingPlayer.getName() + "(" + requestingPlayer.getSkills().getLevelForXp(Skills.SLAYER) + ") is now your Slayer Partner.");
									requestingPlayer.sendMessage(player.getName() + "(" + player.getSkills().getLevelForXp(Skills.SLAYER) + ") is now your Slayer Partner.");
									player.getSlayer().setPartner(requestingPlayer);
									requestingPlayer.getSlayer().setPartner(player);
									player.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, 68);
									player.getSlayer().refreshPartnerInterface();
									requestingPlayer.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, 68);
									requestingPlayer.getSlayer().refreshPartnerInterface();
								}), new DialogueOption("Decline.", () -> {
									player.sendMessage("Declining offer.");
									requestingPlayer.getDialogueManager().start(new PlainChat(requestingPlayer, player.getName() + " declined the offer."));
								}) });
					}
				});
			});
		} else {
			player.getSlayer().setPartner(null);
			partner.getSlayer().setPartner(null);
			player.getSlayer().refreshPartnerInterface();
			if (partner.getInterfaceHandler().isVisible(68)) {
				partner.getSlayer().refreshPartnerInterface();
			}
			partner.sendMessage(player.getName() + " is no longer your Slayer Partner.");
		}
	}

	@Override
	public int[] getInterfaceIds() {
		return new int[] { 68 };
	}

}
