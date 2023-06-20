package com.zenyte.plugins.renewednpc;

import com.zenyte.game.content.achievementdiary.diaries.KourendDiary;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import lombok.val;

/**
 * @author Kris | 25/11/2018 13:23
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ShopNPCPlugin extends NPCPlugin {

    @Override
    public void handle() {
        bind("Trade", (player, npc) -> {
            val handler = ShopNPCHandler.map.get(player.getTransmogrifiedId(npc.getDefinitions(), npc.getId()));
            if (handler == null) {
                throw new RuntimeException("Shop handler is null!");
            }
            if (handler == ShopNPCHandler.WARRENS_GENERAL_STORE) {
                player.getAchievementDiaries().update(KourendDiary.BROWSE_WARRENWS_GENERAL_STORE);
            }
            player.openShop(handler.shop);
        });
    }

    @Override
    public int[] getNPCs() {
        return ShopNPCHandler.map.keySet().toIntArray();
    }
}
