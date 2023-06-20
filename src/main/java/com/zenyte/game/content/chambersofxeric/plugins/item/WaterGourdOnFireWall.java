package com.zenyte.game.content.chambersofxeric.plugins.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnNPCAction;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.player.Player;

public class WaterGourdOnFireWall implements ItemOnNPCAction {
    @Override
    public void handleItemOnNPCAction(Player player, Item item, int slot, NPC npc) {
        player.getInventory().set(slot, new Item(ItemId.EMPTY_GOURD_VIAL));
        npc.unclip();
        npc.remove();
    }

    @Override
    public Object[] getItems() {
        return new Object[] {ItemId.WATERFILLED_GOURD_VIAL};
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {NpcId.FIRE};
    }
}
