package com.zenyte.game.world.entity.player.punishments;

import com.zenyte.utils.Ordinal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kris | 09/03/2019 19:44
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
@Getter
@Ordinal
public enum PunishmentType {
    YELL_MUTE(PunishmentCategory.YELL_MUTE, "Yell Mute"),
    IP_YELL_MUTE(PunishmentCategory.YELL_MUTE, "IP Yell Mute"),
    MAC_YELL_MUTE(PunishmentCategory.YELL_MUTE, "MAC Yell Mute"),
    MUTE(PunishmentCategory.MUTE, "Mute"),
    IP_MUTE(PunishmentCategory.MUTE, "IP Mute"),
    MAC_MUTE(PunishmentCategory.MUTE, "MAC Mute"),
    BAN(PunishmentCategory.BAN, "Ban"),
    IP_BAN(PunishmentCategory.BAN, "IP Ban"),
    MAC_BAN(PunishmentCategory.BAN, "MAC Ban"),
    BANHAMMER(PunishmentCategory.BAN, "Ban");

    private final PunishmentCategory category;
    private final String formattedString;

}
