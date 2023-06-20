package com.zenyte.game.content.area.dorgunderground.npc;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.cutscene.FadeScreen;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.events.LoginEvent;

public class Kazgar extends NPCPlugin {

    private final Location TO_LOCATION = new Location(3310, 9613, 0);

    @Subscribe
    public static final void onLogin(final LoginEvent event) {
        event.getPlayer().getVarManager().sendBit(532, 11);
    }

    @Override
    public void handle() {
        bind("Follow", (player, npc) -> {
            follow(player);
        });
        bind("Mines", (player, npc) -> {
            follow(player);
        });
        bind("Talk-to", (player, npc) -> {
            player.getDialogueManager().start(new Dialogue(player, npc) {
                @Override
                public void buildDialogue() {
                    {
                        npc("Hellow surface-dweller.");
                        options("Select an Option", new DialogueOption("Who are you?", key(100)), new DialogueOption("Can you show me the way to the mines?", key(200)),
                                new DialogueOption("Good bye, cave-dweller.", key(300)));
                    }
                    {
                        player(100, "Who are you?");
                        npc("Mistag posted me here when surface-dwellers started<br> visiting the mine. If you have business in the<br> Dorgeshuun mines I will guide you through the<br> tunnels.");
                        options("Select an Option", new DialogueOption("Can you show me the way to the mines?", key(200)),
                                new DialogueOption("Maybe some other time.", key(400)));
                    }
                    {
                        player(200, "Can you show me the way to the mines?");
                        npc("All right. But don't make any trouble!").executeAction(() -> {
                            follow(player);
                        });
                    }
                    {
                        player(300, "Good bye, cave-dweller.");
                    }
                    {
                        player(400, "Maybe some other time.");
                    }
                }
            });
        });
        bind("Watermill", (player, npc) -> {

        });
    }

    private void follow(Player player) {
        new FadeScreen(player, () -> {
            player.sendMessage("Kazgar shows you the way through the tunnels.");
            player.setLocation(TO_LOCATION);
            WorldTasksManager.schedule(() -> {
                player.addWalkSteps(TO_LOCATION.getX() + 3, TO_LOCATION.getY());
            });

        }).fade(3, true);
    }

    @Override
    public int[] getNPCs() {
        return new int[] { 7300, 7301 };
    }
}
