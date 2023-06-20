package com.zenyte.plugins.dialogue.halloween2021D;

import com.zenyte.game.content.achievementdiary.Diary;
import com.zenyte.game.content.event.DoubleDropsManager;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.plugins.renewednpc.halloween2021npc.GraveDiggerNPC2021;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GhostlyNote extends ItemPlugin {
    @Override
    public void handle() {
        bind("Read", new OptionHandler() {
            @Override
            public void handle(Player player, Item item, Container container, int slotId) {
                ArrayList<String> statuses = new ArrayList<String>();
                Arrays.stream(GraveDiggerNPC2021.DIG_SPOTS).forEach((spot) ->
                        {
                            statuses.add("X: " + spot.getX() + " Y: " + spot.getY());
                        });
                Diary.sendJournal(player, "Digging coordinates...", statuses);
            }
        });
    }

    @Override
    public int[] getItems() {
        return new int[] {24566};
    }
}
