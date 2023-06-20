package com.zenyte.game.content.area.taverley;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Tommeh | 3 mei 2018 | 17:52:06
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public final class KeyMasterD extends Dialogue {

	public KeyMasterD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		player("Hello.");
		npc("Who goes there?");
		npc("This is no place for a human. You need to leave.");
		player("Why?");
		npc("The voices! The voices in my head!");
		player("You're starting to scare me man...");
		npc("I am no man! They changed me and cursed me to<br>remain here...");
		options(TITLE, "What do you mean they changed you?", "What was the curse?", "Goodbye")
			.onOptionOne(() -> setKey(10))
			.onOptionTwo(() -> setKey(25))
			.onOptionThree(() -> setKey(36));
		npc(10, "I was once a free man, powerful and wealthy. I owned<br>several apothecaries across Zeah and sold the tastiest<br>potions in the land.");
		player("What happened?");
		npc("One of my greatest inventions, it was going so well. I<br>spent days finding the right herbs, I travelled across all<br>of Zeah to find" + " the most exotic weeds. Once I had<br>gathered them all, I put them in a potion and mixed in");
		npc("the final ingredient.");
		player("Zeah? Interesting...");
		npc("Yes, yes... The potion tasted delicious but it was missing<br>a tiny something so I added Magic roots to the potion.<br>It started to pulsate and glow!" + " I took a sip and I felt<br>like a million gold! A few seconds later my eye sight");
		npc("began to blur, my brain was throbbing. I fell and hit<br>my head on my worktop, then it all went black.");
		player("Ouch! But, you still haven't said who changed you?");
		npc("Will you let me finish??");
		npc("I woke up screaming in pain. Blue foam streaming out<br>of my mouth, my eye sight worse than before, the only<br>things I could make out were 3 tall figures with" + " green<br>banners - they were of the Arceuus Elders. They");
		npc("muttered to each other in Archaic Language after<br>trying to get up several times. I lost hope and stared at<br>the sky. I had given up when the tallest of the figures<br>" + "bent over and brought his face right to mine and spoke");
		npc("very softly 'We can save you, but it will come at a<br>cost'.");
		player("So they saved you?");
		npc("Yes of course they saved me but look at the cost! I<br>have horns coming out of my head and I'm still blind<br>and stuck here... forever alone.");
		npc(25, "I have been charged to stay here to prevent the<br>Monstrosity from escaping. Those gates and the<br>winches that operate them are the only thing that stops<br>it breaking free.");
		player("What monstrosity? What's behind those gates?");
		npc("You really do not want to know. Leave this place<br>human.");
		player("Hey now, I'm no wimp. What's to stop me from just<br>turning the winch to open the gate and going in?");
		npc("Me. And of course your quick demise at the mercy of<br>Cerberus, guardian of the river of souls.");
		player("But I can do it!");
		npc("You are obviously passionate at trying. But only those<br>with great skill at slaying these types of beast may<br>enter.");
		player("I have been charged by the slayer masters to eliminate<br>this type of threat.");
		npc("Then you may pass... may your soul not end up<br>consumed and forever condemned to the-");
		npc("THE VOICES! AAAHHHHH! Leave this place human!");
		player(36, "Goodbye.");
	}

}
