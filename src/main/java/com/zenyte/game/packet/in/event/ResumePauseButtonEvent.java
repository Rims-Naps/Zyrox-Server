package com.zenyte.game.packet.in.event;

import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.ui.ButtonAction;
import com.zenyte.game.ui.NewInterfaceHandler;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 19:24
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Slf4j
@RequiredArgsConstructor
public class ResumePauseButtonEvent implements ClientProtEvent {

    private final int interfaceId, componentId, slotId;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId + ", slot: " + slotId);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }

    @Override
    public void handle(Player player) {
        if (!player.getInterfaceHandler().isVisible(interfaceId)) {
            return;
        }
        val plugin = NewInterfaceHandler.INTERFACES.get(interfaceId);

        if (plugin != null) {
            var opt = plugin.getComponentName(componentId, slotId);
            if (!opt.isPresent()) {
                opt = plugin.getComponentName(componentId, -1);
            }
            log.info("[" + plugin.getClass().getSimpleName() + "] Dialogue: " + opt.orElse("Absent") + "(" + interfaceId + "::" + componentId + ") | Slot: " + slotId);
            plugin.click(player, componentId, slotId, -1, -1);
            return;
        }
        val script = ButtonAction.INTERFACES.get(interfaceId);
        if (script == null) {
            log.info("Unhandled Dialogue Interface: " + "interfaceId=" + interfaceId + ", component=" + componentId + ", slot=" + slotId);
            return;
        }
        log.info("Dialogue Interface: " + script.getClass().getSimpleName() + ", interfaceId=" + interfaceId + ", component=" + componentId + ", slot=" + slotId);
        script.handleComponentClick(player, interfaceId, componentId, slotId, -1, -1, "");
    }
}
