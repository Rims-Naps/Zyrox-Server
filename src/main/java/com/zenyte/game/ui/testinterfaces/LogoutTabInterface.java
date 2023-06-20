package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.area.plugins.LogoutPlugin;
import lombok.val;

/**
 * @author Tommeh | 1-2-2019 | 20:17
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class LogoutTabInterface extends Interface {

    @Override
    protected void attach() {
        put(8, "Logout");
        put(3, "World switcher");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(getInterface());
    }

    @Override
    protected void build() {
        bind("Logout", player -> {
            if (player.isLocked() || player.isLoggedOut()) {
                return;
            }
            val area = player.getArea();
            if (area instanceof LogoutPlugin) {
                if (!((LogoutPlugin) area).manualLogout(player))
                    return;
            }
            player.logout(false);
        });

        bind("World switcher", player -> player.sendMessage("There are currently no other worlds available."));
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.LOGOUT;
    }
}
