package com.zenyte.game.content.theatreofblood.plugin.object;

import com.zenyte.game.content.follower.impl.BossPet;
import com.zenyte.game.content.theatreofblood.reward.RewardRoom;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Cresinkel
 */

public class RewardsChest implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Claim")) {
            /*
            if (TheatreEntranceObject.isBetaTester(player)) {
                player.sendMessage("Show this message to Cresinkel and he might give you something for beta testing...");
                return;
            }
            */
            if (player.getAttributes().containsKey("rareTobReward")) {
                player.setAnimation(new Animation(536));
                player.lock(2);
                WorldTasksManager.schedule(new TickTask() {
                    @Override
                    public void run() {
                        if (ticks == 1) {
                            if (player.getBooleanAttribute("rareTobReward")) {
                                val item = MonumentalChest.getRareReward();
                                player.getInventory().addOrDrop(item);
                                player.getCollectionLog().add(item);
                                WorldBroadcasts.broadcast(player, BroadcastType.RARE_DROP, item, "Theatre of Blood");
                            } else {
                                for (int i = 0; i < 3; i++) {
                                    val item = MonumentalChest.getNormalReward();
                                    player.getInventory().addOrDrop(item);
                                    player.getCollectionLog().add(item);
                                }
                            }
                            if (Utils.random(19) == 0) {
                                player.getInventory().addOrDrop(new Item(ItemId.SCROLL_BOX_ELITE, 1));
                            }

                            val pet = BossPet.LIL_ZIK;
                            int petRate = RewardRoom.getPetRate(player);
                            System.out.println("Rolled with rate: " + petRate);
                            pet.roll(player,petRate);

                            player.getAttributes().remove("rareTobReward");
                            stop();
                        }
                        ticks++;
                    }
                }, 0, 0);
            } else {
                player.sendMessage("You do not have any pending rewards.");
            }
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{40053};
    }
}
