package com.zenyte.game.packet.in.event;

import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.game.world.object.ObjectExamineLoader;
import mgi.types.config.ObjectDefinitions;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 21:45
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class OpLocExamineEvent implements ClientProtEvent {

    private final int id;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Id: " + id);
    }

    @Override
    public void handle(Player player) {
        val examine = ObjectExamineLoader.DEFINITIONS.get(id);
        if (examine == null) {
            return;
        }
        val def = ObjectDefinitions.get(id);
        if (player.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
            if (def != null) {
                player.sendMessage("Object: <col=C22731>" + def.getName() + "</col> - " + id + "</col>");
            }
        }
        player.sendMessage(examine.getExamine(), MessageType.EXAMINE_OBJECT);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
