package com.zenyte.plugins.itemonobject;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.object.SpiderWebObjectAction;
import lombok.val;

/**
 * @author Kris | 06/05/2019 21:39
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ItemOnSpiderWebAction implements ItemOnObjectAction {
    @Override
    public void handleItemOnObjectAction(final Player player, final Item item, final int slot, final WorldObject object) {
        val name = item.getName().toLowerCase();
        if (item.getDefinitions().getSlot() != -1 && SpiderWebObjectAction.sharpBladePredicate.test(name)) {
            SpiderWebObjectAction.slash(player, object, item);
            return;
        }
        player.sendMessage("Only a sharp blade can cut through this sticky web.");
    }

    @Override
    public Object[] getItems() {
        return null;
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                733
        };
    }
}
