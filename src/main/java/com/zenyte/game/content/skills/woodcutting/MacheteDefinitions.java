package com.zenyte.game.content.skills.woodcutting;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Optional;

/**
 * @author Kris | 27/06/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum MacheteDefinitions {

    MACHETE(ItemId.MACHETE, 1, 6, 910),
    OPAL_MACHETE(ItemId.OPAL_MACHETE, 1, 5, 910),
    JADE_MACHETE(ItemId.JADE_MACHETE, 1, 4, 910),
    RED_TOPAZ_MACHETE(ItemId.RED_TOPAZ_MACHETE, 1, 2, 910);

    @Getter
    private final int itemId, levelRequired, cutTime;
    @Getter private final Animation emote;

    public static final MacheteDefinitions[] values = values();

    static {
        ArrayUtils.reverse(values);
    }

    MacheteDefinitions(final int itemId, final int levelRequried, final int axeTime, final int emoteId) {
        this.itemId = itemId;
        levelRequired = levelRequried;
        cutTime = axeTime;
        emote = new Animation(emoteId);
    }

    public static final Optional<MacheteDefinitions> getMachete(final Player player) {
        val inventory = player.getInventory().getContainer();
        val weapon = player.getEquipment().getId(EquipmentSlot.WEAPON);
        for (val def : values) {
            if (weapon == def.getItemId()) {
                return Optional.of(def);
            }
            for (int slot = 0; slot < 28; slot++) {
                val item = inventory.get(slot);
                if (item == null || item.getId() != def.getItemId()) {
                    continue;
                }
                return Optional.of(def);
            }
        }
        return Optional.empty();
    }

}
