package com.zenyte.game.packet.in.event;

import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.ui.ButtonAction;
import com.zenyte.game.ui.NewInterfaceHandler;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 21:29
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
@Slf4j
public class OpModelEvent implements ClientProtEvent {

    private final int interfaceId, componentId;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId);
    }

    @Override
    public void handle(Player player) {
        if (!player.getInterfaceHandler().getVisible().containsValue(interfaceId)) {
            return;
        }
        val plugin = NewInterfaceHandler.INTERFACES.get(interfaceId);
        if (plugin != null) {
            var opt = plugin.getComponentName(componentId, -1);
            log.info("[" + plugin.getClass().getSimpleName() + "] IF1: " + opt.orElse("Absent") + "(" + interfaceId + "::" + componentId + ")");
            plugin.click(player, componentId, -1, -1, -1);
            return;
        }
        val inter = ButtonAction.INTERFACES.get(interfaceId);
        if (inter != null) {
            inter.handleComponentClick(player, interfaceId, componentId, -1, -1, -1, "");
        }
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
