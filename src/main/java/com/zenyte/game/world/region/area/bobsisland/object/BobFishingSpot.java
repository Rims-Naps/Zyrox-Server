package com.zenyte.game.world.region.area.bobsisland.object;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.area.bobsisland.EvilBobIsland;
import com.zenyte.plugins.dialogue.ItemChat;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.val;

public class BobFishingSpot implements ObjectAction
{
    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (player.getAttributes().get("evil bob complete") != null) {
            player.getDialogueManager().start(new PlainChat(player, "You have already fed the cat - RUN!"));
            return;
        }
        player.getActionManager().setAction(new Action() {
            private final Animation animation = new Animation(621);
            private int ticks;
            @Override
            public boolean start() {
                if (!player.getInventory().containsItem(303, 1)) {
                    player.getDialogueManager().start(new ItemChat(player, new Item(303, 1), "You need a small fishing net to catch the fish. Perhaps you can find them on the " +
                            "island."));
                    return false;
                }
                if (!player.getInventory().hasFreeSlots()) {
                    player.sendMessage("You need some free inventory space to catch fish.");
                    return false;
                }
                player.sendMessage("You cast out your net...");
                delay(8);
                return true;
            }

            @Override
            public boolean process() {
                if (ticks++ % 4 == 0) {
                    player.setAnimation(animation);
                }
                if (!player.getInventory().hasFreeSlots()) {
                    player.sendMessage("You need some free inventory space to catch fish.");
                    return false;
                }
                return true;
            }

            @Override
            public int processWithDelay() {
                val direction = EvilBobIsland.getDirection(player);
                val correctSpot = direction.getPredicate().test(player);
                val fish = correctSpot ? new Item(6202) : new Item(6206);
                player.getInventory().addOrDrop(fish);
                player.getDialogueManager().start(new ItemChat(player, fish,
                        "You catch a... what is this?? Is this a fish?? And... it's cooked already?"));
                return -1;
            }

            @Override
            public void stop() {
                delay(2);
            }
        });
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                23114
        };
    }
}