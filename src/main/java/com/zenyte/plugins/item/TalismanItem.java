package com.zenyte.plugins.item;

import com.zenyte.game.content.skills.runecrafting.Runecrafting;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.val;

import java.util.Objects;

/**
 * @author Kris | 03/07/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class TalismanItem extends ItemPlugin {
    @Override
    public void handle() {
        bind("Locate", (player, item, container, slotId) -> {
            val rc = Objects.requireNonNull(Utils.findMatching(Runecrafting.VALUES, value -> value.getTalismanId() == item.getId()));
            val location = rc.getRuinsCenter();
            if (location.getY() >= 4160 && player.getY() < 4160) {
                player.sendMessage("The talisman doesn't seem to work. Maybe the ruins are deep underground.");
                return;
            } else if (player.getY() >= 4160 && location.getY() < 4160) {
                player.sendMessage("The talisman doesn't seem to work. Maybe the ruins are on the surface.");
                return;
            }
            val direction = Entity.getRoundedDirection(Utils.getFaceDirection(location, player.getLocation()), 0);
            val constant = Direction.getNPCDirection(direction);
            player.sendMessage("The talisman pulls you towards the " + constant.toString().toLowerCase().replace("_", "-") + ".");
        });
    }

    @Override
    public int[] getItems() {
        val talismans = new IntOpenHashSet();
        for (val rc : Runecrafting.VALUES) {
            talismans.add(rc.getTalismanId());
        }
        talismans.remove(-1);
        return talismans.toIntArray();
    }
}
