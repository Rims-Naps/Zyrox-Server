package com.zenyte.game.world.entity.npc.impl.slayer.dragons;

import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kris | 31/10/2018 13:50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@RequiredArgsConstructor
public enum DragonfireProtection {

    PROTECT_FROM_MAGIC("Protect from Magic prayer", 0.75F),
    ANTI_DRAGON_SHIELD("shield", 0.75F),
    ELEMENTAL_SHIELD("shield", 0.75F),
    DRAGONFIRE_SHIELD("shield", 0.75F),
    ANTIFIRE_POTION("antifire potion", 0.5F),
    SUPER_ANTIFIRE_POTION("super antifire potion", 1F);

    @Getter
    final String protectionName;
    final float protectionTier;

    static final DragonfireProtection[] wyvernProtection = new DragonfireProtection[] {
            ELEMENTAL_SHIELD, DRAGONFIRE_SHIELD
    };

    static final DragonfireProtection[] defaultProtection = new DragonfireProtection[] {
            PROTECT_FROM_MAGIC, ANTI_DRAGON_SHIELD, DRAGONFIRE_SHIELD, ANTIFIRE_POTION, SUPER_ANTIFIRE_POTION
    };


    public static List<DragonfireProtection> getProtection(final Player player) {
        return getProtection(player, false);
    }

    /**
     * Gets all of the active protection types for the given player. Only appends the strongest of collisions.
     * @param player the player whose active protections to obtain.
     * @return a list of active dragonfire protections.
     */
    public static List<DragonfireProtection> getProtection(final Player player, final boolean leather) {
        val list = new ArrayList<DragonfireProtection>(3);

        if (leather) {
            if (player.getPrayerManager().isActive(Prayer.PROTECT_FROM_MAGIC)) {
                list.add(PROTECT_FROM_MAGIC);
            }
        }

        val variables = player.getVariables();
        if (variables.getTime(TickVariable.SUPER_ANTIFIRE) > 0) {
            list.add(SUPER_ANTIFIRE_POTION);
        } else if (variables.getTime(TickVariable.ANTIFIRE) > 0) {
            list.add(ANTIFIRE_POTION);
        }

        val shieldId = player.getEquipment().getId(EquipmentSlot.SHIELD);
        if (shieldId == 2890 || shieldId == 9731) {
            list.add(ELEMENTAL_SHIELD);
        } else if (shieldId == 1540 || shieldId == 8282 || shieldId == 11710) {
            list.add(ANTI_DRAGON_SHIELD);
        } else if (shieldId == 11283 || shieldId == 11284 || shieldId == 22002 || shieldId == 22003 || shieldId == 21633 || shieldId == 21634) {
            list.add(DRAGONFIRE_SHIELD);
        }
        return list;
    }

}
