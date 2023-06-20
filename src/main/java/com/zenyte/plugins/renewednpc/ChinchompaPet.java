package com.zenyte.plugins.renewednpc;

import com.zenyte.game.content.follower.Follower;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

import java.util.Objects;

/**
 * @author Kris | 28/04/2019 14:50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ChinchompaPet extends NPCPlugin {
    @Override
    public void handle() {
        bind("Metamorphosis", (player, npc) -> {
            if (!(npc instanceof Follower) || !Objects.equals(((Follower) npc).getOwner(), player)) {
                player.sendMessage("This is not your pet.");
                return;
            }
            if (npc.getId() == 6759) {
                player.getDialogueManager().start(new Dialogue(player, npc) {
                    @Override
                    public void buildDialogue() {
                        options("Metamorphise the rare golden chinchompa?", new DialogueOption("Metamorphise it.", () -> {
                            npc.setTransformation(6756);
                            player.setPetId(6756);
                        }), new DialogueOption("No."));
                    }
                });
                return;
            }
            val target = Utils.random(9999) == 0 ? 6759 : ((npc.getId() + 1) == 6759 ? 6756 : (npc.getId() + 1));
            npc.setTransformation(target);
            player.setPetId(target);
            if (target == 6759) {
                player.sendMessage(Colour.RS_GREEN.wrap("You metamorphise your follower and a golden chinchompa appears!"));
            }
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                6756, 6757, 6758, 6759
        };
    }
}
