package com.zenyte.game.packet.in.event;

import com.zenyte.game.item.ItemOnObjectHandler;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 21:08
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class OpLocUEvent implements ClientProtEvent {

    private final int interfaceId, componentId, slotId, itemId, objectId, x, y;
    private final boolean run;

    @Override
    public void log(@NotNull final Player player) {
        log(player,
                "Interface: " + interfaceId + ", component: " + componentId + ", slot: " + slotId + ", item: " + itemId + ", object id: " + objectId + ", x: " + x + ", y: " + y + ", " +
                        "z: " + player.getPlane() + ", run: " + run);
    }

    @Override
    public void handle(Player player) {
        if (player.isLocked()) {
            return;
        }
        val location = new Location(x, y, player.getPlane());
        val object = World.getObjectWithId(location, objectId);
        if (object == null) {
            return;
        }
        val item = player.getInventory().getItem(slotId);
        if (item == null) {
            return;
        }
        player.stopAll();
        //player.setRun(run);
        ItemOnObjectHandler.handleItemOnObject(player, item, slotId, object);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
