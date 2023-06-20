package com.zenyte.plugins.object;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.events.LoginEvent;

public class HayBale implements ObjectAction {

    @Subscribe
    public static final void onLogin(final LoginEvent event) {
        event.getPlayer().getVarManager().sendBit(2258, 12);
    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if(option.equals("Search")) {
            player.lock();
            WorldTasksManager.schedule(new TickTask() {
                int ticks = 0;
                @Override
                public void run() {
                    if(ticks == 0) {
                        player.setAnimation(new Animation(827));
                        player.sendMessage(String.format("You search the %s...", object.getName().toLowerCase()));
                    }
                    if(ticks == 3) {
                        if(Utils.random(0, 9) == 0) {
                            player.getInventory().addOrDrop(new Item(ItemId.NEEDLE));
                            player.getDialogueManager().start(new Dialogue(player) {
                                @Override
                                public void buildDialogue() {
                                    player("Wow! A needle!<br> Now what are the chances of finding that?");
                                }
                            });
                        } else if(Utils.random(0, 49) == 0) {
                            player.applyHit(new Hit(1, HitType.DEFAULT));
                        } else {
                            player.sendMessage("You find nothing of interest.");
                        }
                    }
                    if(ticks == 4) {
                        player.unlock();
                        stop();
                    }
                    ticks++;
                }
            }, 0, 0);
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 298, 299, 304 };
    }
}
