package com.zenyte.game.world.entity.player.punishments;

import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import java.time.Instant;
import java.util.Date;

/**
 * @author Kris | 09/03/2019 19:45
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
@Getter
public class Punishment {
    
    private final PunishmentType type;
    private final String reporter;
    private final String offender;
    private final String ip;
    private final String macAddress;
    private final Date timeOfPunishment;
    private final int durationInHours;
    private final Date expirationDate;
    private final String reason;
    
    boolean isExpired() {
        return expirationDate != null && expirationDate.before(Date.from(Instant.now()));
    }
    
    @Override
    public String toString() {
        return Colour.RS_GREEN.wrap(type.getFormattedString()) +
                " by " + Colour.RS_GREEN.wrap(Utils.formatString(reporter)) + " expires "
                + Colour.RS_GREEN.wrap((expirationDate == null ? "Never" : expirationDate.toString()));
    }

    public String toLoginString() {
        val formattedString = type.getFormattedString();
        return Colour.RS_GREEN.wrap(formattedString + (type.getCategory() == PunishmentCategory.MUTE ? "d" : "ned")) +
                " by " + Colour.RS_GREEN.wrap(Utils.formatString(reporter)) + " - expires "
                + Colour.RS_GREEN.wrap((expirationDate == null ? "Never" : expirationDate.toString()));
    }

}
