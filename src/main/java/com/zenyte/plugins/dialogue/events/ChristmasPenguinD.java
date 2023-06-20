package com.zenyte.plugins.dialogue.events;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Kris | 2. nov 2017 : 23:24.58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class ChristmasPenguinD extends Dialogue {

    public ChristmasPenguinD(final Player player, final NPC npc) {
        super(player, npc);
    }

    @Override
    public void buildDialogue() {
        player("What is a penguin doing so far away from home?");
        npc("The fat man in the red suit.. he's lost the plot!");
        player("Do you mean Santa?");
        npc("YES! That's him! He has taken over our home.");
        player("Let me guess, you need my help getting your home back?");
        npc("That would be great, we just need you to convince him that he has been reading too many conspiracy theories");
        player("Um... What?");
        npc("He thinks we are government drones! Sent to spy on everyone!");
        player("But... You are...");
        npc("We aren't drones, we are living creatures! Are you going to help us or not?");
        player("I guess so, I'll go find Santa and have a chat with him.");
    }

}
