package com.zenyte.game.packet.in.event;

import com.zenyte.Constants;
import com.zenyte.database.structs.ClanChatMessage;
import com.zenyte.database.structs.GenericChatLog;
import com.zenyte.game.content.clans.ClanManager;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.player.GameCommands;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.game.world.entity.player.punishments.PunishmentManager;
import com.zenyte.game.world.entity.player.punishments.PunishmentType;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 21:23
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class MessagePublicEvent implements ClientProtEvent {

    private final int type, colour, effect;
    private final String message;

    @Override
    public void handle(Player player) {
        val effects = ((colour & 0xFF) << 8) | (effect & 0xFF);
        if (message.startsWith(";;") && player.getPrivilege().eligibleTo(Privilege.JUNIOR_MODERATOR)) {
            GameCommands.process(player, message.substring(2));
            return;
        }
        val punishment = PunishmentManager.isPunishmentActive(player.getUsername(), player.getIP(), player.getMACAddress(),
                PunishmentType.MUTE);
        if (punishment.isPresent()) {
            player.sendMessage("You cannot talk while the punishment is active: " + punishment.get().toString() + ".");
            return;
        }
        if (type == 2) {
            if (player.getSettings().getChannel() != null) {
                val clanMessage = player.getClanMessage();
                clanMessage.set(message.replaceFirst("/", ""), effects, false);
                ClanManager.message(player, clanMessage);
                if (Constants.SQL_ENABLED) {
                    ClanChatMessage.list.add(new ClanChatMessage(player, clanMessage.getChatText(),
                            player.getSettings().getChannelOwner()));
                }
                return;
            }
        }
        if (player.getUpdateFlags().get(UpdateFlag.CHAT)) {
            return;
        }
        player.getUpdateFlags().flag(UpdateFlag.CHAT);
        player.getChatMessage().set(message, effects, type == 1);
        if (Constants.SQL_ENABLED) {
            GenericChatLog.list.add(new GenericChatLog(player, message));
        }
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Type: " + type + ", colour: " + colour + ", effect: " + effect + ", message: " + message);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
