package com.zenyte.plugins.renewednpc;

import com.zenyte.game.content.follower.Follower;
import com.zenyte.game.content.follower.impl.BossPet;
import com.zenyte.game.content.follower.impl.MiscPet;
import com.zenyte.game.util.IntArray;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.val;

import java.util.Objects;

/**
 * @author Kris | 24/04/2019 15:01
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BossPetNPC extends NPCPlugin {

    @Override
    public void handle() {
        bind("Metamorphosis", (player, npc) -> {
            if (!(npc instanceof Follower) || !Objects.equals(((Follower) npc).getOwner(), player)) {
                player.sendMessage("This is not your pet.");
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
        val set = new IntOpenHashSet(BossPet.BOSS_PETS_BY_PET_NPC_ID.keySet());
        set.removeAll(new IntArrayList(IntArray.of(7520, 8201, 8202, 8203, 8204, 8205, 14036, 14038)));
        for (val misc : MiscPet.values()) {
            if (misc.toString().startsWith("CHAMELEON")) {
                set.add(misc.petId());
            }
        }
        return set.toIntArray();
    }
}
