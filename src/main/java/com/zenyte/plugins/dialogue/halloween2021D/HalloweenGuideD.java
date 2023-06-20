package com.zenyte.plugins.dialogue.halloween2021D;

import com.zenyte.game.content.follower.impl.MiscPet;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.Expression;

/**
 * @author Kris | 2. nov 2017 : 23:24.58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class HalloweenGuideD extends Dialogue {

	public HalloweenGuideD(final Player player, final NPC npc) {
		super(player, npc.getId(), npc);
	}
	public static final String HALLOW_GUIDE_STARTED_DIALOGUE_ATTRIBUTE = "hallow_guide_started";
	public static final String HALLOW_GUIDE_COMPLETE_DIALOGUE_ATTRIBUTE = "hallow_guide_complete";

	@Override
	public void buildDialogue() {
		{
			if(!player.getBooleanAttribute(HALLOW_GUIDE_STARTED_DIALOGUE_ATTRIBUTE)) {
				options("What do you want to ask?", new DialogueOption("What are you doing here?", key(100)), new DialogueOption("Do you like my outfit?", key(200)));
			} else {
				if(player.getBooleanAttribute(HALLOW_GUIDE_COMPLETE_DIALOGUE_ATTRIBUTE)) {
					if(player.containsItem(24560) || (player.getFollower() != null  && player.getFollower().getPet().petId() == MiscPet.DEATH_JR.petId())) {
						npc("What can I do for you?");
						player("Will I ever see you again?");
						npc("...perhaps.");
						player("...", Expression.SAD);
					} else {
						player("So I kinda lost Death Jr...");
						npc("How did you lose him???");
						player("Err, I'm not really sure... It all happened so fast.");
						npc("Try not to lose him this time...").executeAction(() -> {
							if (player.getInventory().addItem(new Item(24560, 1)).isFailure()) {
								npc("Perhaps you should make some room first.");
							}
						});
					}

				} else {
					if(completedAllOtherEvents()) {
						if(wearingGravediggerOutfit()) {
							player("Do you like my outfit?");
							npc("Yes! You look very disgusting... in a good way!");
							npc("I noticed how well you've done on my tasks...");
							npc("But I must get going soon, and I want to give you something as a parting gift.");
							npc("I hope you'll take good care of him.").executeAction(() -> {
								if(player.getInventory().addItem(new Item(24560, 1)).isFailure()) {
									npc("Perhaps you should make some room first...");
								} else {
									npc("He looks just like me, doesn't he?");
									player.putBooleanAttribute(HALLOW_GUIDE_COMPLETE_DIALOGUE_ATTRIBUTE, true);
								}
							});
						} else {
							player("I helped the little boy get some candy, and he gave me part of an outfit.");
							player("I also helped the gravedigger find his skulls, and found the rest of the outfit along the way!");
							npc("It sounds like you've had quite the adventure...");
							player("Yeah, I just hope nothing bad happens to that boy..");
							npc("I'm sure he'll be alright, thanks to you!");
							npc("Maybe you should put on that outfit and talk to me again...");
							player("Alright, I will do that.");
						}
					} else {
						options("What do you want to ask?", new DialogueOption("What am I supposed to do again?", key(500)),  new DialogueOption("Do you like my outfit?", key(200)));
					}
				}
			}
		}

		{
			player(100, "What're you doing here?");
			npc("Enjoying the Halloween season!");
			player("What do you like about Halloween?");
			npc("Everything!");
			npc("Would you like to make this special for everyone?");
			options("Start quest?", new DialogueOption("Yes!", key(300)), new DialogueOption("No thanks..", key(400)));
		}

		{
			player(200, "Do you like my outfit?");
			if(completedAllOtherEvents()) {
				npc("You seemed to have gotten everything together before you even started my quest... sigh...");
				player("I guess I should have paid better attention! Oh well, can I have a reward or something?");
				npc("You must do this the right way. If you aren't paying attention, how can I ever entrust you with such a gift?");
				player("... <br> Sorry, what was that again..?");
				npc("Let's start from the beginning...");
			} else {
				npc("I don't think you have all of the right steps completed yet...");
				npc("Are you sure you talked to everyone that I mentioned?");
				player("On second thought, I guess not! I'll go talk to them now");
			}
		}

		{
			player(300,"Yes!");
			npc("First you will want to talk to the sad boy in Canifis...");
			npc("Then you will want to talk to the Gravedigger in Lumbridge.");
			player("Sounds easy, what next?");
			npc("After that, return back to me.");
			player("Okay I will go ahead and find that boy in Canifis.");
			npc("Good luck!").executeAction(() -> {
				player.putBooleanAttribute(HALLOW_GUIDE_STARTED_DIALOGUE_ATTRIBUTE, true);
			});
		}

		{
			player(400, "No thanks...");
			npc("Well you're missing out, but alright I guess...");
		}

		{
			player(500,"What am I supposed to do again?");
			npc("First you will want to talk to the sad boy in Canifis...");
			npc("Then you will want to talk to the Gravedigger in Lumbridge.");
			player("Uh huh....");
			npc("After that, return back to me.");
			player("Okay, I think I got it this time...");
			npc("Are you sure?");
			player("Yep, quite.");
			npc("Okay, be off then, young adventurer!");
		}
	}

	private boolean completedAllOtherEvents() {
		return player.getBooleanAttribute(GraveDiggerD.GRAVEDIG_COMPLETE_DIALOGUE_ATTRIBUTE) && player.getBooleanAttribute(SadboyD.SAD_BOY_COMPLETE_DIALOGUE_ATTRIBUTE);
	}

	private boolean wearingGravediggerOutfit() {
		return player.getEquipment().isWearing(new Item(13283, 1)) && player.getEquipment().isWearing(new Item(13284, 1)) && player.getEquipment().isWearing(new Item(13285, 1))
				&& player.getEquipment().isWearing(new Item(13286, 1)) && player.getEquipment().isWearing(new Item(13287, 1));
	}

}
