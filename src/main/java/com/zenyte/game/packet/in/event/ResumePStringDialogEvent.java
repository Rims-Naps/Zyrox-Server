package com.zenyte.game.packet.in.event;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.ui.testinterfaces.DropViewerInterface;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.StringDialogue;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 20:31
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class ResumePStringDialogEvent implements ClientProtEvent {

    private final String string;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Value: " + string);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }

    @Override
    public void handle(Player player) {
        if (player.getInterfaceHandler().isPresent(GameInterface.DROP_VIEWER)) {
            DropViewerInterface.search(player, string);
            return;
        }
        val input = player.getTemporaryAttributes().get("interfaceInput");
        if (input instanceof StringDialogue) {
            val dialogue = (StringDialogue) input;
            dialogue.execute(player, string);
        }
    }
}
