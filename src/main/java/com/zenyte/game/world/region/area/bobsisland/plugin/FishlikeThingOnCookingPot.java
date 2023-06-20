package com.zenyte.game.world.region.area.bobsisland.plugin;

import com.zenyte.game.content.skills.cooking.CookingDefinitions;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.ItemChat;

public class FishlikeThingOnCookingPot implements ItemOnObjectAction
{
    @Override
    public void handleItemOnObjectAction(final Player player, final Item item, final int slot, final WorldObject object) {
        if (item.getId() == 6200 || item.getId() == 6204) {
            player.getDialogueManager().start(new ItemChat(player, item, "The fishlike thing is already uncooked!"));
            return;
        }
        player.getActionManager().setAction(new Action() {
            @Override
            public boolean start() {
                player.setAnimation(CookingDefinitions.STOVE);
                delay(2);
                player.lock(3);
                return true;
            }

            @Override
            public boolean process() {
                return true;
            }

            @Override
            public int processWithDelay() {
                player.getInventory().deleteItem(item);
                player.getInventory().addOrDrop(new Item(item.getId() - 2, 1));
                return -1;
            }
        });
    }

    @Override
    public Object[] getItems() {
        return new Object[] {
                6200, 6202, 6204, 6206
        };
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                23113
        };
    }
}