package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.SwitchPlugin;
import com.zenyte.game.util.Examine;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import lombok.val;

/**
 * @author Tommeh | 25-4-2019 | 18:37
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class EquipmentInventoryInterface extends Interface implements SwitchPlugin {

    @Override
    protected void attach() {
        put(0, "Interact with Item");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
    }

    @Override
    protected void build() {
        bind("Interact with Item", (player, slotId, itemId, option) -> {
            val item = player.getInventory().getItem(slotId);
            if (item == null) {
                return;
            }
            if (option == 1) {
                player.getEquipment().wear(slotId);
            } else if (option == 10) {
                Examine.sendItemExamine(player, item.getId());
            }
        });
        bind("Interact with Item", "Interact with Item", ((player, fromSlot, toSlot) -> {
            if (fromSlot < 0 || toSlot < 0 || fromSlot >= ContainerType.INVENTORY.getSize() || toSlot >= ContainerType.INVENTORY.getSize()) return;
            player.getInventory().switchItem(fromSlot, toSlot);
        }));
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.EQUIPMENT_INVENTORY;
    }
}
