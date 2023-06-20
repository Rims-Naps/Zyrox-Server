package com.zenyte.game.content.theatreofblood.plugin.object;

import com.zenyte.game.content.follower.impl.BossPet;
import com.zenyte.game.content.theatreofblood.reward.RewardRoom;
import com.zenyte.game.content.theatreofblood.reward.TobNormalReward;
import com.zenyte.game.content.theatreofblood.reward.TobRareReward;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Position;
import com.zenyte.game.world.World;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;
import lombok.var;

/**
 * @author Cresinkel
 */

public class MonumentalChest implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        val playerChest = player.getAttributes().get("tobChestLoc");
        if (!(object.getPosition().matches((Position) playerChest))) {
            player.sendMessage("This is not your Monumental chest.");
            return;
        }/*
        if (TheatreEntranceObject.isBetaTester(player)) {
            player.sendMessage("Show this message to Cresinkel and he might give you something for beta testing...");
            return;
        }*/
        if (option.equals("Open")) {
            World.removeObject(object);
            WorldObject newChest = new WorldObject(ObjectId.MONUMENTAL_CHEST_32994, 10, object.getRotation(), object.getPosition());
            World.spawnObject(newChest);
            player.setAnimation(new Animation(536));
            player.lock(2);
            WorldTasksManager.schedule(new TickTask() {
                @Override
                public void run() {
                    if (ticks == 1) {
                        if (player.getBooleanAttribute("rareTobReward")) {
                            val item = getRareReward();
                            player.getInventory().addOrDrop(item);
                            player.getCollectionLog().add(item);
                            WorldBroadcasts.broadcast(player, BroadcastType.RARE_DROP, item, "Theatre of Blood");
                        } else if (player.getNumericAttribute("tobpoints").intValue() == 0) {
                            player.getInventory().addOrDrop(new Item(ItemId.CABBAGE));
                        } else {
                            for (int i = 0; i < 3; i++) {
                                val item = getNormalReward();
                                player.getInventory().addOrDrop(item);
                                player.getCollectionLog().add(item);
                            }
                        }
                        if (Utils.random(19) == 0) {
                            player.getInventory().addOrDrop(new Item(ItemId.SCROLL_BOX_ELITE, 1));
                        }

                        val pet = BossPet.LIL_ZIK;
                        int petRate = RewardRoom.getPetRate(player);
                        pet.roll(player,petRate);

                        player.getAttributes().remove("rareTobReward");
                        stop();
                    }
                    ticks++;
                }
            }, 0, 0);
        }
        if (option.equals("Search")) {
            player.sendMessage("This Monumental chest was already opened");
            return;
        }
    }

    public static Item getRareReward() {
        val random = Utils.secureRandom(TobRareReward.TOTAL_WEIGHT);
        var roll = 0;
        for (val reward : TobRareReward.values) {
            if ((roll += reward.getWeight()) < random) {
                continue;
            }
            val item = reward.getItem();
            return new Item(item.getId(), item.getAmount());
        }
        throw new IllegalStateException();
    }

    public static Item getNormalReward() {
        val random = TobNormalReward.random();
        return new Item(random.getId(),Utils.random(random.getMinimumAmount(),random.getMaximumAmount())).toNote();
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{ObjectId.MONUMENTAL_CHEST, ObjectId.MONUMENTAL_CHEST_32991, ObjectId.MONUMENTAL_CHEST_32992, ObjectId.MONUMENTAL_CHEST_32993, ObjectId.MONUMENTAL_CHEST_32994, 33086, 33087, 33088, 33089, 33090};
    }
}
