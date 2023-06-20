package com.zenyte.game.packet.in.event;

import com.zenyte.game.item.ItemOnFloorItemHandler;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 20:54
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class OpObjUEvent implements ClientProtEvent {

    private final int interfaceId, componentId, slotId, itemId, floorItemId, x, y;
    private final boolean run;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId + ", slot: " + slotId + ", item: " + itemId + ", floor item: " + floorItemId + ", x: " + x + ", y:" +
                " " + y + ", z: " + player.getPlane() + ", run: " + run);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }

    @Override
    public void handle(Player player) {
        if (player.isNulled() || player.isFinished() || player.isDead() || player.isLocked()) {
            return;
        }
        val location = new Location(x, y, player.getPlane());
        val floorItem = World.getRegion(location.getRegionId()).getFloorItem(floorItemId, location, player);
        if (floorItem == null) {
            return;
        }
        val item = player.getInventory().getItem(slotId);
        if (item == null) {
            return;
        }
        player.stopAll();
//        player.setRun(run);
        ItemOnFloorItemHandler.handleItemOnFloorItem(player, item, floorItem);
    }
}
