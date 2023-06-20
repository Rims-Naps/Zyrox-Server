package com.zenyte.game.world.region.area.bobsisland.npc;

import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.cutscene.Cutscene;
import com.zenyte.game.world.entity.player.cutscene.actions.CameraLookAction;
import com.zenyte.game.world.entity.player.cutscene.actions.CameraPositionAction;
import com.zenyte.game.world.entity.player.cutscene.actions.CameraResetAction;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.region.area.bobsisland.EvilBobIsland;
import lombok.val;

public class BobsServant extends NPCPlugin
{
    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new Dialogue(player, npc) {
            @Override
            public void buildDialogue() {
                if (player.getAttributes().get("evil bob complete") != null) {
                    npc("Use the portal while you can!");
                    return;
                }
                player("I need help, I've been kidnapped by an evil cat!");
                npc("Meow! Errr... I c-c-c-an't help you... He'll kill us all!");
                player("Now you listen to me! He's just a little cat! There must be something I can do!");
                npc("F-f-f-fish... give him the f-f-f-fish he likes and he might f-f-f-fall asleep.");
                npc("Look... over t-t-there! That fishing spot c-c-contains the f-f-f-fish he likes.").executeAction(() -> {
                    val direction = EvilBobIsland.getDirection(player);
                    player.getCutsceneManager().play(new Cutscene() {
                        @Override
                        public void build() {
                            addActions(0, () -> player.lock(), new CameraPositionAction(player, direction.getCameraPosition(), 800, 10, 20), new CameraLookAction(player,
                                    direction.getCameraLook(), 0, 10, 20));
                            addActions(5, () -> player.unlock(), new CameraResetAction(player));
                        }
                    });
                });
            }
        }));
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                392
        };
    }
}