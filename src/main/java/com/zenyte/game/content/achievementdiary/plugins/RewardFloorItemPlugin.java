package com.zenyte.game.content.achievementdiary.plugins;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.flooritem.FloorItem;
import com.zenyte.plugins.flooritem.FloorItemPlugin;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;

/**
 * @author Tommeh | 23-4-2019 | 20:34
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class RewardFloorItemPlugin implements FloorItemPlugin {

    @Override
    public void handle(Player player, FloorItem item, int optionId, String option) {
        if (option.equals("Take")) {
            val reward = DiaryReward.get(item.getId());
            if (reward == null) {
                return;
            }
            val bestReward = DiaryReward.getBestEligibleReward(player, reward.getArea());
            if (bestReward != null) {
                if (reward.ordinal() < bestReward.ordinal()) {
                    player.sendMessage("You can't pickup this reward since you're able to get a better version from the taskmaster.");
                    return;
                }
            }
            World.takeFloorItem(player, item);
        }
    }

    @Override
    public boolean overrideTake() {
        return true;
    }

    @Override
    public int[] getItems() {
        val list = new IntArrayList();
        for (val diary : DiaryReward.VALUES) {
            list.add(diary.getItem().getId());
        }
        return list.toIntArray();
    }
}
