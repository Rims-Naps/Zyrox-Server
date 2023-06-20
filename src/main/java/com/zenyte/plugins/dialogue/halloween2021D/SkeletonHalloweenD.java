package com.zenyte.plugins.dialogue.halloween2021D;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Kris | 2. nov 2017 : 23:24.58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class SkeletonHalloweenD extends Dialogue {

    public SkeletonHalloweenD(final Player player, final NPC npc) {
        super(player, npc);
    }

    @Override
    public void buildDialogue() {
        player("Boo?");
        npc("Boo?");
        player("You speak English?");
        npc("Why wouldn't I?");
        player("Well.. You know because..");
        player("Your.. Uh...");
        npc("I'm what?");
        player("Your dead.");
        npc("No I'm not!?");
        npc("I feel fine!");
        player("But your all bone?");
        npc("It's a flesh wound.");
    }

}
