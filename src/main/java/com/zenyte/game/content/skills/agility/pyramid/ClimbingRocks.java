package com.zenyte.game.content.skills.agility.pyramid;

import com.zenyte.game.content.achievementdiary.diaries.DesertDiary;
import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.ItemChat;
import lombok.val;

public class ClimbingRocks implements Obstacle {
    private static final Animation climbingAnim = new Animation(3063);

    @Override
    public void startSuccess(Player player, WorldObject object) {
        player.setAnimation(climbingAnim);
        player.sendSound(new SoundEffect(2454));
        WorldTasksManager.schedule(() -> {
            if (player.getVarManager().getBitValue(AgilityPyramid.HIDE_PYRAMID_VARBIT) == 0) {
                val inventory = player.getInventory();
                if (inventory.hasFreeSlots()) {
                    WorldTasksManager.schedule(() -> {
                        val reward = new Item(ItemId.PYRAMID_TOP);
                        player.getDialogueManager().start(new ItemChat(player, reward, "You find a golden pyramid!"));
                        player.getVarManager().sendBit(AgilityPyramid.HIDE_PYRAMID_VARBIT, true);
                        inventory.addOrDrop(reward);
                        player.getAchievementDiaries().update(DesertDiary.CLIMB_AGILITY_PYRAMID);
                    });
                } else {
                    player.sendMessage("You don't have enough inventory space to pick up this item.");
                }
            } else {
                player.sendMessage("You find nothing on top of the pyramid.");
            }
        });
    }

    @Override
    public int getDuration(boolean success, WorldObject object) {
        return 3;
    }

    @Override
    public double getSuccessXp(WorldObject object) {
        return 0;
    }

    @Override
    public int getLevel(WorldObject object) {
        return 30;
    }

    @Override
    public int[] getObjectIds() {
        return new int[]{10851};
    }

}
