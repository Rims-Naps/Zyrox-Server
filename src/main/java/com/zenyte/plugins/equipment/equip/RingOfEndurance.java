package com.zenyte.plugins.equipment.equip;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.variables.PlayerVariables;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Cresinkel
 */
public class RingOfEndurance implements EquipPlugin {
    @Override
    public boolean handle(Player player, Item item, int slotId, int equipmentSlot) {
        return true;
    }

    @Override
    public void onUnequip(final Player player, final Container container, final Item unequippedItem) {
        int time = player.getVariables().getTime(TickVariable.STAMINA_ENHANCEMENT);
        if (time > 200) {
            player.getVariables().cancel(TickVariable.STAMINA_ENHANCEMENT);
            player.getVariables().schedule(time - 200, TickVariable.STAMINA_ENHANCEMENT);
        }
    }

    @Override
    public int[] getItems() {
        return new int[]{
                32236
        };
    }
}
