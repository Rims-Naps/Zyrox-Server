package com.zenyte.game.content.combatachievements.items;

import com.zenyte.game.content.combatachievements.combattasktiers.*;
import com.zenyte.game.content.skills.magic.spells.teleports.TeleportCollection;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.val;

import java.util.Arrays;

/**
 * @author Cresinkel
 */

public class GhommalsHilts1_3 extends ItemPlugin {

    public static final IntList hilts = new IntArrayList(Arrays.asList(ItemId.GHOMMALS_HILT_1, ItemId.GHOMMALS_HILT_2,
            ItemId.GHOMMALS_HILT_3));

    @Override
    public void handle() {
        bind("Godwars Dungeon", (player, item, slotId) -> {
            if (item.getId() == ItemId.GHOMMALS_HILT_1 && !EasyTasks.allEasyCombatAchievementsDone(player)) {
                player.sendMessage(Colour.RED.wrap("You do not have all easy tasks completed anymore."));
                return;
            }
            if (item.getId() == ItemId.GHOMMALS_HILT_2 && !MediumTasks.allMediumCombatAchievementsDone(player)) {
                player.sendMessage(Colour.RED.wrap("You do not have all medium tasks completed anymore."));
                return;
            }
            if (item.getId() == ItemId.GHOMMALS_HILT_3 && !HardTasks.allHardCombatAchievementsDone(player)) {
                player.sendMessage(Colour.RED.wrap("You do not have all hard tasks completed anymore."));
                return;
            }
            val usedTeleports = player.getVariables().getGodwarsGhommalHiltTeleports();
            if (item.getId() == ItemId.GHOMMALS_HILT_1 && usedTeleports >= 3) {
                player.sendMessage(Colour.RED.wrap("You have used up your daily Godwars Dungeon teleports for today."));
                return;
            }
            if (item.getId() == ItemId.GHOMMALS_HILT_2 && usedTeleports >= 5) {
                player.sendMessage(Colour.RED.wrap("You have used up your daily Godwars Dungeon teleports for today."));
                return;
            }
            if (item.getId() == ItemId.GHOMMALS_HILT_1 || item.getId() == ItemId.GHOMMALS_HILT_2) {
                player.getVariables().setGodwarsGhommalHiltTeleports(usedTeleports + 1);
                player.getTemporaryAttributes().put("godwars dungeon restricted teleport", true);
            }
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    options(TITLE, "Kree'arra", "K'ril Tsutsaroth", "General Graardor", "Commander Zilyana")
                            .onOptionOne(() -> TeleportCollection.GHOMMAL_HILT_GWD_ARMA.teleport(player))
                            .onOptionTwo(() -> TeleportCollection.GHOMMAL_HILT_GWD_ZAMMY.teleport(player))
                            .onOptionThree(() -> TeleportCollection.GHOMMAL_HILT_GWD_BANDOS.teleport(player))
                            .onOptionFour(() -> TeleportCollection.GHOMMAL_HILT_GWD_SARA.teleport(player));
                }
            });
        });
    }

    @Override
    public int[] getItems() {
        return hilts.toIntArray();
    }
}
