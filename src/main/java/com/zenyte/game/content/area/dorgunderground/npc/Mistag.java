package com.zenyte.game.content.area.dorgunderground.npc;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.cutscene.FadeScreen;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

public class Mistag extends NPCPlugin {
    private final Location TO_LOCATION = new Location(3236, 9610, 0);

    @Override
    public void handle() {
        bind("Follow", (player, npc) -> {
            follow(player);
        });
        bind("Talk-to", (player, npc) -> {
            player.getDialogueManager().start(new Dialogue(player, npc) {
                @Override
                public void buildDialogue() {
                    {
                        npc("Hellow surface-dweller.");
                        options("Select an Option", new DialogueOption("Can you show me the way out of the mines?", key(100)),
                                new DialogueOption("Good bye, cave-dweller.", key(200)));
                    }
                    {
                        player(100, "Can you show me the way out of the mines?");
                        npc("Certainly. Come back soon!").executeAction(() -> {
                            follow(player);
                        });
                    }
                    {
                        player(200, "Good bye, cave-dweller.");
                    }
                }
            });
        });
        bind("Cellar", (player, npc) -> {

        });
        bind("Watermill", (player, npc) -> {

        });
    }

    private void follow(Player player) {
        new FadeScreen(player, () -> {
            player.sendMessage("Mistag shows you the way through the tunnels.");
            player.setLocation(TO_LOCATION);
            WorldTasksManager.schedule(() -> {
                player.addWalkSteps(TO_LOCATION.getX() - 3, TO_LOCATION.getY());
            });

        }).fade(3, true);
    }

    @Override
    public int[] getNPCs() {
        return new int[] { 7298, 7299 };
    }
}
