package com.zenyte.plugins.flooritem;

import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.flooritem.FloorItem;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 06/06/2019 19:04
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class RunecraftingPouchFlooritemPlugin implements FloorItemPlugin {
    @Override
    public void handle(final Player player, final FloorItem item, final int optionId, final String option) {
        val heldCount = player.getAmountOf(item.getId());
        if (option.equalsIgnoreCase("take")) {
            if (heldCount >= 1) {
                player.sendMessage("You can only one " + item.getName() + " at a time.");
                return;
            }
            World.takeFloorItem(player, item);
        }
    }

    @Override
    public boolean canTelegrab(@NotNull final Player player, @NotNull final FloorItem item) {
        val heldCount = player.getAmountOf(item.getId());
        if (heldCount >= 1) {
            player.sendMessage("You can only one " + item.getName() + " at a time.");
            return false;
        }
        return true;
    }

    @Override
    public boolean overrideTake() {
        return true;
    }

    @Override
    public int[] getItems() {
        return new int[] { 5509, 5510, 5512, 5514, 32194 };
    }
}
