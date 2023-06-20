package com.zenyte.game.content.achievementdiary.plugins.drop;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Tommeh | 18-11-2018 | 22:13
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class DagannothBonesProcessor extends DropProcessor {

    private static final int DAGANNOTH_BONES = 6729;
    private static final int NOTED_DAGANNOTH_BONES = 6730;

    @Override
    public void attach() {
        put(DAGANNOTH_BONES, new PredicatedDrop("This drop will be noted if you have the elite Fremennik diary completed."));
    }

    @Override
    public Item drop(NPC npc, Player killer, Drop drop, Item item) {
        if (item.getId() == DAGANNOTH_BONES && DiaryReward.FREMENNIK_SEA_BOOTS4.eligibleFor(killer)) {
            item.setId(NOTED_DAGANNOTH_BONES);
        }
        return item;
    }

    @Override
    public int[] ids() {
        return new int[] { 2265, 2266, 2267 };
    }
}
