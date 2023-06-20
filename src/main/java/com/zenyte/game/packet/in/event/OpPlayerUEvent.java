package com.zenyte.game.packet.in.event;

import com.zenyte.game.item.ItemOnPlayerHandler;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 21:12
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class OpPlayerUEvent implements ClientProtEvent {

    private final int targetIndex;
    private final int slotId;
    private final int itemId;
    private final int interfaceId;
    private final int run;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", slot: " + slotId + ", itemId: " + itemId + ", run: " + run + ", target: " + targetIndex + " -> " + World.getPlayers().get(targetIndex));
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }

    @Override
    public void handle(Player player) {
        if (player.isLocked()) {
            return;
        }
        val target = World.getPlayers().get(targetIndex);
        if (target == null) {
            return;
        }
        val item = player.getInventory().getItem(slotId);
        if (item == null) {
            return;
        }
        if (run == 1) {
            if (player.eligibleForShiftTeleportation()) {
                player.setLocation(new Location(target.getLocation()));
                return;
            }
            player.setRun(true);
        }
        ItemOnPlayerHandler.handleItemOnPlayer(player, item, slotId, target);
    }
}
