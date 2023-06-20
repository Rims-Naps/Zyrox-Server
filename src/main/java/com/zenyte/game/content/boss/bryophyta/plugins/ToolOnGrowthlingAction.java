package com.zenyte.game.content.boss.bryophyta.plugins;

import com.zenyte.game.RuneDate;
import com.zenyte.game.content.combatachievements.combattasktiers.MediumTasks;
import com.zenyte.game.content.skills.magic.Rune;
import com.zenyte.game.content.skills.woodcutting.AxeDefinitions;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnNPCAction;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;

import java.sql.Time;

/**
 * @author Tommeh | 17/05/2019 | 20:01
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class ToolOnGrowthlingAction implements ItemOnNPCAction {

    @Override
    public void handleItemOnNPCAction(Player player, Item item, int slot, NPC npc) {
        if (!npc.isDead() && npc.getHitpoints() <= 2) {
            npc.sendDeath();
            if (!player.getBooleanAttribute("medium-combat-achievement18")) {
                val oldGrowthlingCount = player.getAttributes().getOrDefault("amount_of_growthlings_killed", "0").toString();
                player.getAttributes().put("amount_of_growthlings_killed", oldGrowthlingCount.contains("0") ? "1" : oldGrowthlingCount.contains("1") ? "2" : "3");
                val newGrowthlingCount = player.getAttributes().get("amount_of_growthlings_killed").toString();
                if (newGrowthlingCount.equals("1")) {
                    player.getAttributes().put("time_when_first_growthling_was_killed", RuneDate.currentTimeMillis());
                } else if (newGrowthlingCount.equals("3")) {
                    long timeOfFirstKill = 0;
                    if (player.getAttributes().get("time_when_first_growthling_was_killed") instanceof Long) {
                        timeOfFirstKill = (long) player.getAttributes().get("time_when_first_growthling_was_killed");
                    }
                    val timeNow = RuneDate.currentTimeMillis();
                    if (timeOfFirstKill + 3000L >= timeNow) {
                        player.putBooleanAttribute("medium-combat-achievement18", true);
                        MediumTasks.sendMediumCompletion(player, 18);
                    }
                    player.sendMessage("You killed the first and last growthling " + Colour.RED.wrap((int) (timeNow - timeOfFirstKill)) + " milliseconds apart");
                    player.getAttributes().put("amount_of_growthlings_killed", "0");
                }
            }
        } else {
            player.sendMessage("The growthling isn't weak enough yet.");
        }
    }

    @Override
    public Object[] getItems() {
        val list = new IntArrayList();
        for (val def : AxeDefinitions.VALUES) {
            list.add(def.getItemId());
        }
        list.add(5329);
        list.add(7409);
        return list.toArray(new Object[list.size()]);
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 8194 };
    }
}
