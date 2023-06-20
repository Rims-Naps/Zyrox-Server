package com.zenyte.game.content.event;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Player;
import mgi.types.config.items.ItemDefinitions;

import java.util.HashMap;
import java.util.Map;

public class DoubleDropsManager {

    public static String DROP_MESSAGE = "Lucky you! Your drop has been doubled!";
    public static Map<Integer, Boolean> DOUBLE_DROPS = new HashMap<>();
    private int[] defaultDrops = {ItemId.MAGIC_FANG, };

    public static void setDoubled(int id, boolean isDoubled) {
        DOUBLE_DROPS.put(id, isDoubled);
    }

    public static boolean isDoubled(int id) {
        return DOUBLE_DROPS.getOrDefault(id, false);
    }

    public static void removeAll() {
        DOUBLE_DROPS.clear();
    };

    public static void handleCommand(Player player, String[] args) {
        if(args.length != 2) {
            player.sendMessage("Proper syntax: ::dd (item id) (is doubled), example ::dd 4151 true is double whips.");
            return;
        }
        int itemId = Integer.valueOf(args[0]);
        if(itemId == -1) {
            return;
        }
        boolean doubled = Boolean.valueOf(args[1]);
        setDoubled(itemId, doubled);
        ItemDefinitions itemdef = ItemDefinitions.get(itemId);
        player.sendMessage("Item doubling for " + itemdef.getName() + "(id:" + itemId + ") is " + (doubled ? Colour.RS_GREEN.wrap("now doubled.") : Colour.RED.wrap("no longer doubled.")));
    }
}