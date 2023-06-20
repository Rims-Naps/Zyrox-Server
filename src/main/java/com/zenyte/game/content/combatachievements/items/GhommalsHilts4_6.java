package com.zenyte.game.content.combatachievements.items;

import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.GrandmasterTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
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

public class GhommalsHilts4_6 extends ItemPlugin {

    public static final IntList hilts = new IntArrayList(Arrays.asList(ItemId.GHOMMALS_HILT_4, ItemId.GHOMMALS_HILT_5, ItemId.GHOMMALS_HILT_6));

    @Override
    public void handle() {
        bind("Godwars Dungeon", (player, item, slotId) -> {
            if (item.getId() == ItemId.GHOMMALS_HILT_4 && !EliteTasks.allEliteCombatAchievementsDone(player)) {
                player.sendMessage(Colour.RED.wrap("You do not have all elite tasks completed anymore."));
                return;
            }
            if (item.getId() == ItemId.GHOMMALS_HILT_5 && !MasterTasks.allMasterCombatAchievementsDone(player)) {
                player.sendMessage(Colour.RED.wrap("You do not have all master tasks completed anymore."));
                return;
            }
            if (item.getId() == ItemId.GHOMMALS_HILT_6 && !GrandmasterTasks.allGrandmasterCombatAchievementsDone(player)) {
                player.sendMessage(Colour.RED.wrap("You do not have all grandmaster tasks completed anymore."));
                return;
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
        bind("Mor Ul Rek", (player, item, slotId) -> {
            if (item.getId() == ItemId.GHOMMALS_HILT_4 && !EliteTasks.allEliteCombatAchievementsDone(player)) {
                player.sendMessage(Colour.RED.wrap("You do not have all elite tasks completed anymore."));
                return;
            }
            if (item.getId() == ItemId.GHOMMALS_HILT_5 && !MasterTasks.allMasterCombatAchievementsDone(player)) {
                player.sendMessage(Colour.RED.wrap("You do not have all master tasks completed anymore."));
                return;
            }
            if (item.getId() == ItemId.GHOMMALS_HILT_6 && !GrandmasterTasks.allGrandmasterCombatAchievementsDone(player)) {
                player.sendMessage(Colour.RED.wrap("You do not have all grandmaster tasks completed anymore."));
                return;
            }
            val usedTeleports = player.getVariables().getMorulrekGhommalHiltTeleports();
            if (item.getId() == ItemId.GHOMMALS_HILT_4 && usedTeleports >= 3) {
                player.sendMessage(Colour.RED.wrap("You have used up your daily Mor Ul Rek teleports for today."));
                return;
            }
            if (item.getId() == ItemId.GHOMMALS_HILT_5 && usedTeleports >= 5) {
                player.sendMessage(Colour.RED.wrap("You have used up your daily Mor Ul Rek teleports for today."));
                return;
            }
            if (item.getId() == ItemId.GHOMMALS_HILT_4 || item.getId() == ItemId.GHOMMALS_HILT_5) {
                player.getVariables().setMorulrekGhommalHiltTeleports(usedTeleports + 1);
                player.getTemporaryAttributes().put("mor ul rek restricted teleport", true);
            }
            TeleportCollection.GHOMALT_HILT_MOR_UL_REK.teleport(player);
        });
    }

    @Override
    public int[] getItems() {
        return hilts.toIntArray();
    }
}
