package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.Examine;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.Equipment;
import lombok.val;

/**
 * @author Tommeh | 19/05/2019 | 15:56
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class EquipmentStatsInterface extends Interface {

    @Override
    protected void attach() {
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
    }

    @Override
    protected void build() {
    }

    @Override
    protected DefaultClickHandler getDefaultHandler() {
        return (player, componentId, slotId, itemId, optionId) -> {
            if (player.isLocked()) {
                return;
            }
            slotId = Equipment.getIndexByButton(getInterface().getId(), componentId);
            val item = player.getEquipment().getItem(slotId);
            if (item == null) {
                return;
            }

            val opt = EquipmentTabInterface.getOption(item, optionId);
            val plugin = ItemPlugin.getPlugin(item.getId());
            val handler = plugin.getHandler(opt);
            if (handler != null) {
                handler.handle(player, item, player.getEquipment().getContainer(), slotId);
                return;
            }

            if(opt.equals(EquipmentTabInterface.EXAMINE)) {
                Examine.sendItemExamine(player, item);
                return;
            }

            player.sendMessage("Nothing interesting happens.");
        };
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.EQUIPMENT_STATS;
    }
}
