package com.zenyte.game.content.skills.agility.brimhavenminigame;

import com.zenyte.game.content.combatachievements.combattasktiers.*;
import com.zenyte.game.content.treasuretrails.TreasureTrail;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Kris | 26/11/2018 18:28
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class CapnIzzyNobeard extends NPCPlugin {


    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> {
            if (TreasureTrail.talk(player, npc)) {
                return;
            }
            player.getDialogueManager().start(new Dialogue(player, npc) {
                @Override
                public void buildDialogue() {
                    player("Ahoi Cap'n!");
                    npc("Ahoy there!");
                    options(TITLE, "Can I view your shop please?", "Cancel.")
                            .onOptionOne(() -> setKey(5));
                    player(5, "Can I view your shop please?");
                    npc("Sure.").executeAction(() -> {
                        player.openShop("Cap'n Izzy No-beard's Shop");
                    });
                }
            });
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {5789};
    }
}
