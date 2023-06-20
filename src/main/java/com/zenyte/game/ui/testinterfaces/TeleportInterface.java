package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.teleportsystem.PortalTeleport;
import com.zenyte.game.world.entity.player.teleportsystem.TeleportCategory;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Tommeh | 13-11-2018 | 17:54
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class TeleportInterface extends Interface {

    @Override
    protected void attach() {
        put(4, "View Category");
        put(11, "Teleport to/Toggle favorite");
        put(17, "Favorite teleport to");
    }

    @Override
    public void open(Player player) {
        val manager = player.getTeleportManager();
        player.getInterfaceHandler().sendInterface(getInterface());
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("View Category"), 0, (TeleportCategory.VALUES.length * 2) + 2, AccessMask.CLICK_OP1);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Teleport to/Toggle favorite"), 0, 4 * 100, AccessMask.CLICK_OP1);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Favorite teleport to"), 0, 4 * 100, AccessMask.CLICK_OP1);
        manager.populateCategories(manager.getLastCategory(), true);
        manager.populateTeleports(manager.getLastCategory(), true);
        manager.populateFavorites();
    }

    @Override
    protected void build() {
        bind("View Category", (player, slotId, itemId, option) -> {
            val manager = player.getTeleportManager();
            val selectedCategory = TeleportCategory.get(slotId / 2);
            if (selectedCategory == null) {
                return;
            }
            /*if (selectedCategory == TeleportCategory.SKILLING) {
                manager.populateSkillingCategories();
                return;
            }*/
            player.getPacketDispatcher().sendClientScript(10005);
            manager.populateCategories(selectedCategory, true);
            manager.populateTeleports(selectedCategory, true);
            manager.setLastCategory(selectedCategory);
        });
        bind("Teleport to/Toggle favorite", (player, slotId, itemId, option) -> {
            val manager = player.getTeleportManager();
            val category = manager.getLastCategory();
            if (category == null) {
                return;
            }
            val teleports = PortalTeleport.get(category);
            if (teleports.isEmpty()) {
                return;
            }
            val sorted = new ArrayList<PortalTeleport>(teleports);
            sorted.sort((o1, o2) -> {
                val comp = Boolean.compare(manager.isUnlocked(o2), manager.isUnlocked(o1));
                if (comp != 0) {
                    return comp;
                }

                String x1 = o1.toString();
                String x2 = o2.toString();
                return x1.compareTo(x2);
            });
            if (slotId % 4 == 0) {
                val slot = slotId / 4;
                if (slot >= sorted.size()) {
                    return;
                }
                val teleport = sorted.get(slot);
                if (teleport == null) {
                    return;
                }

                if (!manager.isUnlocked(teleport)) {
                    player.sendMessage("You haven't unlocked this teleport yet.");
                    return;
                }

                manager.teleportTo(teleport);
            } else {
                val slot = (slotId + 1) / 4 - 1;
                if (slot >= sorted.size()) {
                    return;
                }
                val teleport = sorted.get(slot);
                if (teleport == null) {
                    return;
                }
                manager.toggleFavorite(teleport);
            }
        });
        bind("Favorite teleport to", (player, slotId, itemId, option) -> {
            val manager = player.getTeleportManager();
            if ((slotId / 2) >= manager.getFavoriteTeleports().size()) {
                return;
            }
            val teleport = manager.getFavoriteTeleports().get(slotId / 2);
            if (teleport == null) {
                return;
            }
            manager.teleportTo(teleport);
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.TELEPORT_MENU;
    }
}
