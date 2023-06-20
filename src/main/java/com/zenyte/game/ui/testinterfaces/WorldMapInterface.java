package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.PaneType;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Device;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Tommeh | 28-10-2018 | 16:14
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class WorldMapInterface extends Interface {

    @Override
    protected void attach() {
        put(37, "Close");
        put(3, "Esc Close");
    }

    @Override
    public void open(Player player) {
        if (player.getWorldMap().isFullScreen() || player.getPlayerInformation().getDevice().equals(Device.MOBILE)) {
            player.getWorldMap().setVisible(true);
            player.getWorldMap().setPreviousPane(player.getInterfaceHandler().getPane());
            player.getWorldMap().updateLocation();
            player.getInterfaceHandler().sendPane(player.getWorldMap().getPreviousPane(), PaneType.FULL_SCREEN);
            if (player.getPlayerInformation().getDevice().equals(Device.MOBILE)) {
                player.getInterfaceHandler().sendInterface(595, 28, PaneType.FULL_SCREEN, true);
            } else {
                player.getInterfaceHandler().sendInterface(getInterface());
            }
            player.getInterfaceHandler().sendInterface(594, 27, PaneType.FULL_SCREEN, false);
            player.getPacketDispatcher().sendComponentSettings(getInterface(), 17, 0, 4, AccessMask.CLICK_OP1);
        } else {
            player.getWorldMap().setVisible(true);
            player.getWorldMap().updateLocation();
            player.getInterfaceHandler().sendInterface(getInterface());
            player.getPacketDispatcher().sendComponentSettings(getInterface(), 17, 0, 4, AccessMask.CLICK_OP1);
        }
    }

    @Override
    protected void build() {
        bind("Close", player -> {
            if (player.isLocked()) {
                return;
            }
            player.getWorldMap().close();
        });
        bind("Esc Close", player -> {
            if (player.isLocked()) {
                return;
            }
            player.getWorldMap().close();
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.WORLD_MAP;
    }
}
