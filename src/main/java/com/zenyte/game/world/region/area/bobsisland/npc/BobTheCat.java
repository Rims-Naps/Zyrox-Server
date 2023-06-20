package com.zenyte.game.world.region.area.bobsisland.npc;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

public class BobTheCat extends NPCPlugin
{
    public static final Dialogue getDialogue(final Player player, final NPC bob) {
        return new Dialogue(player, bob) {
            @Override
            public void buildDialogue() {
                player("Huh?");
                player("Where am I?");
                npc("On my island.");
                player("Who brought me here?");
                npc("That would be telling.");
                player("Take me to your leader!");
                npc("I am your leader, you are but a slave.");
                player("I am not a slave, I am a free man!");
                npc("Ah-ha-ha-ha-ha-ha!");
            }
        };
    }

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(getDialogue(player, npc)));
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                390
        };
    }
}
