package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.GameSetting;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

import java.util.Optional;

/**
 * @author Tommeh | 27-4-2019 | 11:24
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class GameSettingsInterface extends Interface {

    @Override
    protected void attach() {
        put(6, "Close");
        put(7, "Handle Setting");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
        val size = GameSetting.ALL.length;
        for (int index = 0; index < size; index++) {
            val setting = GameSetting.ALL[index];
            if (setting == null) {
                continue;
            }
            player.getPacketDispatcher().sendClientScript(10200, index, setting.getName(), setting.getDescription(), setting.getType().ordinal(), player.getNumericAttribute(setting.toString()).intValue());
        }
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Handle Setting"), 0, size * 6, AccessMask.CLICK_OP1);
        player.getPacketDispatcher().sendClientScript(10201, size);
    }

    @Override
    public void close(final Player player, final Optional<GameInterface> replacement) {
        GameInterface.GAME_NOTICEBOARD.open(player);
    }

    @Override
    protected void build() {
        bind("Handle Setting", (player, slotId, itemId, option) -> {
            val setting = GameSetting.get(slotId);
            if (setting == null) {
                return;
            }
            setting.handleSetting(player);
        });
        bind("Close", player -> player.getInterfaceHandler().closeInterface(getInterface()));
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.GAME_SETTINGS;
    }
}
