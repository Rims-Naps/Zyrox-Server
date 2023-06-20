package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.Examine;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.Equipment;
import lombok.val;

import java.util.Map;

/**
 * @author Kris | 16/04/2019 16:58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class EquipmentTabInterface extends Interface {

    public static final String REMOVE = "Remove";
    public static final String EXAMINE = "Examine";
    public static final String CHECK = "Check";

    @Override
    protected void attach() {
        put(17, "View equipment stats");
        put(19, "Price checker");
        put(21, "View items kept on death");
        put(23, "Call follower");

    }

    @Override
    public void open(final Player player) {
        player.getInterfaceHandler().sendInterface(this);
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

            val opt = getOption(item, optionId);
            val plugin = ItemPlugin.getPlugin(item.getId());
            val handler = plugin.getHandler(opt);
            if (handler != null) {
                handler.handle(player, item, player.getEquipment().getContainer(), slotId);
                return;
            }

            if(opt.equals(EXAMINE)) {
                Examine.sendItemExamine(player, item);
                return;
            }

            player.sendMessage("Nothing interesting happens.");
        };
    }

    public static final String getOption(final Item item, final int optionId) {
        if (optionId == 1) {
            return REMOVE;
        }
        if (optionId == 10) {
            return EXAMINE;
        }
        final Map<Integer, Object> params = item.getDefinitions().getParameters();
        if (params == null) {
            return "null";
        }
        final Object option = params.get(449 + optionId);
        if (!(option instanceof String)) {
            return "null";
        }
        return (String) option;
    }

    @Override
    protected void build() {
        bind("View equipment stats", player -> {
            if (player.isLocked()) {
                return;
            }
            if (player.isUnderCombat()) {
                player.sendMessage("You can't do this while in combat.");
                return;
            }
            player.stopAll();
            player.getEquipment().sendEquipmentStatsInterface();
        });

        bind("Price checker", player -> {
            if (player.isLocked()) {
                return;
            }
            if (player.isUnderCombat()) {
                player.sendMessage("You can't do this while in combat.");
                return;
            }
            player.stopAll();
            player.getPriceChecker().openPriceChecker();
        });

        bind("View items kept on death", player -> {
            if (player.isLocked()) {
                return;
            }
            if (player.isUnderCombat()) {
                player.sendMessage("You can't do this while in combat.");
                return;
            }
            player.stopAll();
            GameInterface.ITEMS_KEPT_ON_DEATH.open(player);
        });

        bind("Call follower", player -> {
            if (player.isLocked()) {
                return;
            }
            if (player.getFollower() != null) {
                if (player.getFollower().getLocation().withinDistance(player, 5)) {
                    player.sendMessage("Your follower is already close enough.");
                    return;
                }
                player.getFollower().call();
            } else {
                player.sendMessage("You do not have a follower.");
            }
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.EQUIPMENT_TAB;
    }
}
