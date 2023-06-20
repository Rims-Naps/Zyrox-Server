package com.zenyte.game.content.tournament.preset.component;

import com.zenyte.game.content.tournament.preset.BooleanEntry;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.game.world.entity.player.container.impl.equipment.Equipment;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tommeh | 25/05/2019 | 16:00
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
public class EquipmentComponent {

    private Map<Integer, BooleanEntry<Item>> items;

    public EquipmentComponent(final Map<Integer, BooleanEntry<Item>> items) {
        this.items = items;
    }

    public static class EquipmentComponentBuilder {

        private Map<Integer, BooleanEntry<Item>> items;

        public EquipmentComponentBuilder() {
            items = new HashMap<>();
            for (int slot = 0; slot < Equipment.SIZE; slot++) {
                items.put(slot, null);
            }
        }

        public EquipmentComponentBuilder put(final EquipmentSlot slot, final int id, final boolean droppable) {
            return put(slot, id, 1, droppable);
        }

        public EquipmentComponentBuilder put(final EquipmentSlot slot, final int id, final int amount, final boolean droppable) {
            items.put(slot.getSlot(), new BooleanEntry<>(new Item(id, amount, DegradableItem.getFullCharges(id)), droppable));
            return this;
        }

        public EquipmentComponent build() {
            return new EquipmentComponent(items);
        }

    }
}
