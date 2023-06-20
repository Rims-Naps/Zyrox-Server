package com.zenyte.game.content.area.abandonedmine;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.DropPlugin;

public class AbandonedMineArea extends Area implements DropPlugin {


    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[]{ new RSPolygon(new int[][]{
                {2816, 4416},
                {2687, 4416},
                {2687, 4544},
                {2751, 4608},
                {2816, 4608},
                {2816, 4544},
        }), new RSPolygon(new int[][]{
                {3448, 9664},
                {3395, 9608},
                {3395, 9664},
                {3448, 9664},
        })};
    }

    @Override
    public void enter(Player player) {
    }

    @Override
    public void leave(Player player, boolean logout) {
        if(player.inArea(AbandonedMineArea.class)) {
            return;
        }
        if(player.getInventory().containsItem(ItemId.GLOWING_FUNGUS)) {
            for(Item i : player.getInventory().getContainer().getItems().values()) {
                if(i.getId() == ItemId.GLOWING_FUNGUS) {
                    i.setId(ItemId.ASHES);
                }
            }
            player.getInventory().refreshAll();
            player.sendMessage("The fungus you are carrying crumbles to dust.");
        }
    }

    @Override
    public String name() {
        return "Abandoned Mine";
    }

    @Override
    public boolean drop(Player player, Item item) {
        if(item.getId() == ItemId.GLOWING_FUNGUS) {
            item.setId(ItemId.ASHES);
            player.sendMessage("When you drop the fungus it crumbles mysteriously into dust.");
        }
        return DropPlugin.super.drop(player, item);
    }
}
