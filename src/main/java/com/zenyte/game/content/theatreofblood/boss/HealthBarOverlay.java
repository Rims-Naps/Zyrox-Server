package com.zenyte.game.content.theatreofblood.boss;

import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.content.theatreofblood.shared.HealthBarType;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Tommeh | 5/31/2020 | 1:20 AM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public interface HealthBarOverlay {

    HealthBarType getHealthBarType();

    int getCurrentHitpoints();

    int getMaximumHitpoints();

    default void refreshHealthBar(final TheatreOfBloodRaid raid) {
        val party = raid.getParty();
        for (val m : party.getMembers()) {
            val member = RaidingParty.getPlayer(m);

            refreshHealthBar(member, raid);
        }
    }

    default void refreshHealthBar(final Player player, final TheatreOfBloodRaid raid) {
        val party = raid.getParty();
        if (player.getArea() != raid.getActiveRoom()) {
            return;
        }
        if (getCurrentHitpoints() == 0) {
            player.getVarManager().sendBit(6447, 0);
            return;
        }
        player.getVarManager().sendBit(6447, getHealthBarType().getId());
        player.getVarManager().sendBit(6448, getCurrentHitpoints() / 10);
        player.getVarManager().sendBit(6449, getMaximumHitpoints() / 10);
    }

    default void removeHealthBar(final Player player) {
        player.getVarManager().sendBit(6447, HealthBarType.REMOVED.getId());
    }
}
