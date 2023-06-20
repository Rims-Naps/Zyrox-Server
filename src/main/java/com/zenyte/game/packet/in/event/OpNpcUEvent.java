package com.zenyte.game.packet.in.event;

import com.zenyte.game.item.ItemOnNPCHandler;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 21:06
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class OpNpcUEvent implements ClientProtEvent {

    private final int interfaceId;
    private final int componentId;
    private final int slotId;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId + ", slot: " + slotId);
    }

    private final int itemId;
    private final int index;
    private final boolean run;

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }

    @Override
    public void handle(Player player) {
        if (player.isLocked()) {
            return;
        }
        val npc = World.getNPCs().get(index);
        if (npc == null) {
            return;
        }
        val item = player.getInventory().getItem(slotId);
        if (item == null) {
            return;
        }
        if (run) {
            if (player.eligibleForShiftTeleportation()) {
                player.setLocation(new Location(npc.getLocation()));
                return;
            }
            player.setRun(true);
        }
        ItemOnNPCHandler.handleItemOnNPC(player, item, slotId, npc);
    }
}
