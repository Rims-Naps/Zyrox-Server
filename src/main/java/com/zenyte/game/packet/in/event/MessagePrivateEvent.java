package com.zenyte.game.packet.in.event;

import com.zenyte.Constants;
import com.zenyte.database.structs.PrivateMessage;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.masks.ChatMessage;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.punishments.PunishmentManager;
import com.zenyte.game.world.entity.player.punishments.PunishmentType;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 21:20
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class MessagePrivateEvent implements ClientProtEvent {

    private String recipient;
    private ChatMessage message;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Recipient: " + recipient + ", message: " + message.getChatText());
    }

    @Override
    public void handle(Player player) {
        val punishment = PunishmentManager.isPunishmentActive(player.getUsername(), player.getIP(), player.getMACAddress(),
                PunishmentType.MUTE);
        if (punishment.isPresent()) {
            player.sendMessage("You cannot talk while the punishment is active: " + punishment.get().toString() + "."
            );
            return;
        }
        if (recipient.length() > 12) {
            recipient = recipient.substring(0, 12);
        }
        player.getSocialManager().sendMessage(recipient, message);
        if(Constants.SQL_ENABLED) {
            PrivateMessage.list.add(new PrivateMessage(player, recipient, message.getChatText()));
        }
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
