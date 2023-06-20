package com.zenyte.game.packet.in.event;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.ui.ButtonAction;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 20:19
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Slf4j
@AllArgsConstructor
public class If3ButtonEvent implements ClientProtEvent {

    private int interfaceId, componentId, slotId, itemId, option;

    @Override
    public void handle(Player player) {
        if (itemId == 0xFFFF) {
            itemId = -1;
        }
        if (slotId == 0xFFFF) {
            slotId = -1;
        }
        ButtonAction.handleComponentAction(player, interfaceId, componentId, slotId, itemId, option, 3);
    }

    @Override
    public void log(@NotNull final Player player) {
        val interfaceName = GameInterface.get(interfaceId);
        val name = interfaceName.isPresent() ? interfaceName.get().toString() : Strings.EMPTY;
        log(player, "Interface: " + name + ", id: " + interfaceId + ", component: " + componentId + ", slot: " + slotId + ", item: " + itemId + ", option: " + option);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
