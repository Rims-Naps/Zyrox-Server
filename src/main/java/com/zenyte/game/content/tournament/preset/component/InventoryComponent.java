package com.zenyte.game.content.tournament.preset.component;

import com.zenyte.game.content.tournament.preset.BooleanEntry;
import com.zenyte.game.item.Item;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tommeh | 25/05/2019 | 16:11
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
public class InventoryComponent {

    private List<BooleanEntry<Item>> items;

    public InventoryComponent(final List<BooleanEntry<Item>> items) {
        this.items = items;
    }

    public static class InventoryComponentBuilder {

        private List<BooleanEntry<Item>> items;

        public InventoryComponentBuilder() {
            items = new ArrayList<>();
        }

        public InventoryComponentBuilder add(final int id, final boolean droppable) {
            return add(id, 1, droppable);
        }

        public InventoryComponentBuilder add(final int id, final int amount, final boolean droppable) {
            items.add(new BooleanEntry<>(new Item(id, amount), droppable));
            return this;
        }

        public InventoryComponent build() {
            return new InventoryComponent(items);
        }

    }
}
