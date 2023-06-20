package com.zenyte.plugins.object;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.var.VarCollection;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;
import lombok.var;

/**
 * @author Tommeh | 25-4-2019 | 22:34
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ZaffBarrelObject implements ObjectAction {

    private static final DiaryReward[] DIARY_REWARDS = {
            DiaryReward.VARROCK_ARMOUR4, DiaryReward.VARROCK_ARMOUR3,
            DiaryReward.VARROCK_ARMOUR2, DiaryReward.VARROCK_ARMOUR1
    };

    private static final int PRICE = 7000;

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (!canPurchase(player)) {
            player.sendMessage("You need to complete at least the easy Varrock diaries to purchase these battlestaves.");
            return;
        }
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                var maxAmount = 0;
                for (int index = 0; index < DIARY_REWARDS.length; index++) {
                    val reward = DIARY_REWARDS[index];
                    val amount = index == 0 ? 120 : index == 1 ? 60 : index == 2 ? 30 : 15;
                    if (reward.eligibleFor(player)) {
                        maxAmount = amount;
                        break;
                    }
                }
                val amt = maxAmount;
                val price = new Item(995, amt * PRICE);
                plain("Buy " + amt + " discounted battlestaves for " + Utils.format(PRICE) + " coins each?<br><br>Total cost: " + Utils.format(price.getAmount()) + " coins");
                options(TITLE, "Yes", "No.")
                        .onOptionOne(() -> {
                           if (!player.getInventory().containsItem(price)) {
                               setKey(10);
                           } else if (!player.getInventory().checkSpace()) {
                               setKey(15);
                           } else {
                               player.getInventory().deleteItem(price);
                               player.getInventory().addItem(new Item(1392, amt));
                               player.getVariables().setClaimedBattlestaves(true);
                               VarCollection.DAILY_BATTLESTAVES_COLLECTED.update(player);
                           }
                        });
                plain(10, "You're going to need " + Utils.format(price.getAmount()) + " coins to pay for these, even at their<br><br>discounted price.");
                plain(15, "Please make sure that you have enough space in your inventory.");
            }
        });
    }

    private boolean canPurchase(final Player player) {
        for (val reward : DIARY_REWARDS) {
            if (reward.eligibleFor(player)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 30357 };
    }
}
