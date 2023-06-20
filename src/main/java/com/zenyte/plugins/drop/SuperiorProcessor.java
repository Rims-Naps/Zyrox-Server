package com.zenyte.plugins.drop;

import com.zenyte.game.world.entity.npc.combatdefs.NPCCDLoader;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.npc.impl.slayer.superior.SuperiorMonster;
import lombok.val;

/**
 * @author Kris | 12/10/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SuperiorProcessor extends DropProcessor {
    @Override
    public void attach() {
        //Actual drops are being dropped through the NPC class itself.
        for (int id : allIds) {
            val req = NPCCDLoader.get(id).getSlayerLevel();
            val probability = 0.5F / (1F / (200F - (Math.pow(req + 55F, 2) / 125F)));
            appendDrop(new DisplayedDrop(20736, 1, 1,  probability / (3 / 8F), (player, npcId) -> npcId == id));
            appendDrop(new DisplayedDrop(20730, 1, 1, probability / (3 / 8F), (player, npcId) -> npcId == id));
            appendDrop(new DisplayedDrop(20724, 1, 1, probability / (1 / 8F), (player, npcId) -> npcId == id));
            appendDrop(new DisplayedDrop(21270, 1, 1, probability / (1 / 8F), (player, npcId) -> npcId == id));
            put(id, 20736, new PredicatedDrop("Superior monsters will always roll three times on the parent NPC's drop table in addition to rolling once on the drops shown here."));
        }
    }

    @Override
    public int[] ids() {
        return SuperiorMonster.superiorMonsters.toIntArray();
    }
}
