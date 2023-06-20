package com.zenyte.plugins.flooritem;

import com.zenyte.game.content.skills.farming.Seedling;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import com.zenyte.game.world.flooritem.FloorItem;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 19/04/2019 20:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * Plugin dedicated to invoking the watered seedling sprout cycle when a player picks up a watered seedling.
 */
public class FarmingSeedlingPlugin implements FloorItemPlugin {

    @Override
    public boolean overrideTake() {
        return true;
    }

    @Override
    public void telegrab(@NotNull final Player player, @NotNull final FloorItem item) {
        if (!canTelegrab(player, item)) {
            return;
        }
        val space = player.getInventory().getFreeSlots();
        World.destroyFloorItem(item);
        player.getInventory().addItem(item).onFailure(it -> World.spawnFloorItem(it, player, 100, 200));
        val remainingSpace = player.getInventory().getFreeSlots();
        if (space != remainingSpace) {
            player.getVariables().schedule(15, TickVariable.SEEDLING_SPROUT);
        }
    }

    @Override
    public void handle(final Player player, final FloorItem item, final int optionId, final String option) {
        if (option.equalsIgnoreCase("Take")) {
            val space = player.getInventory().getFreeSlots();
            World.takeFloorItem(player, item);
            val remainingSpace = player.getInventory().getFreeSlots();
            if (space != remainingSpace) {
                player.getVariables().schedule(15, TickVariable.SEEDLING_SPROUT);
            }
        }
    }

    @Override
    public int[] getItems() {
        val set = new IntOpenHashSet();
        for (val value : Seedling.values) {
            set.add(value.getWateredSeedling());
        }
        return set.toIntArray();
    }
}
