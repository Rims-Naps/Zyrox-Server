package com.zenyte.plugins.renewednpc;

import com.zenyte.game.content.follower.Follower;
import com.zenyte.game.content.follower.impl.BossPet;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import lombok.val;

import java.util.Objects;

/**
 * @author Kris | 24/04/2019 15:12
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ChambersOfXericBossPetNPC extends NPCPlugin {
    @Override
    public void handle() {
        bind("Metamorphosis", (player, npc) -> {
            if (!(npc instanceof Follower) || !Objects.equals(((Follower) npc).getOwner(), player)) {
                player.sendMessage("This is not your pet.");
                return;
            }
            if (player.getNumericAttribute("Metamorphic dust sprinkle").intValue() <= 0) {
                player.sendMessage("You haven't unlocked the metamorphosis ability yet.");
                return;
            }
            val target = BossPet.metamorphosisMap.getOrDefault(npc.getId(), -1);
            if (target == -1) {
                player.sendMessage("Nothing interesting happens.");
                return;
            }
            npc.setTransformation(target);
            player.setPetId(target);
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                7520, 8201, 8202, 8203, 8204, 8205, 14036, 14038
        };
    }
}
