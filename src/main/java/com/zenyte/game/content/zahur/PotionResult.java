package com.zenyte.game.content.zahur;

import com.zenyte.game.content.consumables.drinks.Potion;
import com.zenyte.game.item.Item;
import lombok.Getter;
import lombok.val;
import lombok.var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tommeh | 16-3-2019 | 00:35
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
public class PotionResult {

    private final List<Item> items;
    private final Potion potion;

    public PotionResult(final Potion potion, final Item... items) {
        this.items = new ArrayList<>();
        this.items.addAll(Arrays.asList(items));
        this.potion = potion;
    }

    public void add(final Item item) {
        items.add(item);
    }

    public int getTotalDose() {
        var dose = 0;
        for (val item : items) {
            val def = item.getDefinitions();
            val id = def.getUnnotedOrDefault();
            dose += potion.getDoses(id) * item.getAmount();
        }
        return dose;
    }

}
