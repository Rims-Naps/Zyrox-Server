package com.zenyte.plugins.dialogue.halloween2021D;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.Expression;

/**
 * @author Kris | 2. nov 2017 : 23:24.58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class SadboyD extends Dialogue {

	public static final String SAD_BOY_STARTED_DIALOGUE_ATTRIBUTE = "sad_boy_started";
	public static final String SAD_BOY_COMPLETE_DIALOGUE_ATTRIBUTE = "sad_boy_complete";

	private boolean completedInitialDialogue(Player player) {
		return player.getBooleanAttribute(SAD_BOY_STARTED_DIALOGUE_ATTRIBUTE);
	}

	private boolean completedCandyRequest(Player player) {
		return player.getBooleanAttribute(SAD_BOY_COMPLETE_DIALOGUE_ATTRIBUTE);
	}

	public SadboyD(final Player player, final NPC npc) {
		super(player, npc.getId(), npc);
	}

	@Override
	public void buildDialogue() {
		if(completedCandyRequest(player)) {
			if(player.containsAny(13284, 13285)) {
				npc("Thank you for getting the candy for me, it was delicious!");
			} else {
				if(player.getInventory().getFreeSlots() >= 2) {
					npc("I see you were able to make space for those items, here they are!").executeAction(() -> {
						player.getInventory().addItem(new Item(13284, 1));
						player.getInventory().addItem(new Item(13285, 1));
					});
				} else {
					npc("You still don't have enough space for the outfit, come back with at least 2 free spaces!");
				}
			}
		} else {
			if(completedInitialDialogue(player)) {
				npc("Have you found my 6 candies yet?");
				if(player.getInventory().getAmountOf(24565) >= 6) {
					player("Sure! I have them right here!");
					plain("You give all of your candies to the boy").executeAction(() -> {
						player.getInventory().deleteItem(new Item(24565, player.getInventory().getAmountOf(24565)));
						player.putBooleanAttribute(SAD_BOY_COMPLETE_DIALOGUE_ATTRIBUTE, true);
					});
					npc("Thank you so much! Take some of this spooky outfit I found...").executeAction(() -> {
						if(player.getInventory().getFreeSlots() >= 2) {
							player.getInventory().addItem(new Item(13284, 1));
							player.getInventory().addItem(new Item(13285, 1));
						} else {
							npc("Let me know when you have enough space for these 2 items.");
						}
					});
				} else {
					player("Not yet.. I'm still working on it.");
					npc("...please hurry! You never know.. about these creatures..", Expression.ANXIOUS);
					player("I'll get right on that.");
				}
			} else {
				player("Are you ok?");
				npc("No.. Halloween is almost over..and I have no candy..");
				player("Why are you not trick or treating?");
				npc("Have you not heard the stories?!");
				player("What stories?");
				player("Remember now son.. stories are stories..does not mean they are real..");
				npc("You don't understand....");
				player("What do I not understand?");
				npc("I had a friend...Carl..");
				player("Carl.. and where is Carl?");
				npc("Carl went trick or treating last year...");
				npc("*gulps* Carl went by himself..");
				npc("Then....");
				npc("We never heard from Carl again..");
				player("What happened?");
				npc("No one knows.. So that is the reason.. I will not trick or treat.."
						+ "Will you do me a favor..");
				player("What favor?");
				npc("Can you trick or treat for me?");
				player("What do I get out of it?");
				npc("I will give you this old top that someone left near our house.. *points at a gravedigger top*");
				player("How much candy do you want?");
				npc("how about... SIX PIECES!... Yes my mother allows me to have one a day.. but what she doesn't know doesn't hurt her..");
				player("I will see what I can do..");
				npc("By the way.. I only want candy from.. people I know so.. stick around Canfis.").executeAction(() -> player.putBooleanAttribute(SAD_BOY_STARTED_DIALOGUE_ATTRIBUTE, true));
			}
		}
	}

}
