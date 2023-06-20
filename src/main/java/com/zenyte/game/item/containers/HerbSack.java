package com.zenyte.game.item.containers;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import lombok.Getter;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashSet;
import java.util.Optional;

/**
 * @author Tommeh | 25-3-2019 | 20:14
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
public class HerbSack {

    private transient Player player;
    private final Container container;

    public static final int[] HERBS = new int[] { 199, 201, 203, 205, 207, 3049, 209, 211, 213, 3051, 215, 2485, 217, 219 };

    public HerbSack(final Player player) {
        this.player = player;
        this.container = new Container(ContainerPolicy.ALWAYS_STACK, ContainerType.HERB_SACK, Optional.of(player));
    }

    public void initialize(final HerbSack sack) {
        if (sack != null && sack.container != null) {
            container.setContainer(sack.container);
        }
    }

    public void fill() {
        if (getSize() == 420) {
            player.sendMessage("You cannot more than 420 grimy herbs in your herb sack.");
            return;
        }
        var added = 0;
        val checked = new HashSet<Integer>(28);
        for (int slot = 0; slot < 28; slot++) {
            val item = player.getInventory().getItem(slot);
            if (item == null) {
                continue;
            }
            if (ArrayUtils.contains(HERBS, item.getId())) {
                if (getAmountOf(item.getId()) == 30) {
                    if (!checked.contains(item.getId())) {
                        player.sendMessage("You cannot store anymore " + item.getName().toLowerCase() + "s.");
                        checked.add(item.getId());
                    }
                    continue;
                }
                container.add(item);
                player.getInventory().deleteItem(item);
                added++;
            }
        }
        player.sendMessage(added > 0 ? "You search your inventory for herbs appropriate to put in the sack...<br>You enqueue the herb" + (added == 1 ? "" : "s") + " to your sack." : "You have no grimy herbs in your inventory that can be stored in the sack.");
    }

    public void check() {
        player.sendMessage("You look in your herb sack and see:");
        if (getSize() == 0) {
            player.sendMessage("The herb sack is empty.");
            return;
        }
        for (val entry : container.getItems().int2ObjectEntrySet()) {
            val item = entry.getValue();
            val name = item.getName();
            val amount = item.getAmount();
            if (amount > 0) {
                player.sendMessage(amount + " x " + name);
            }
        }
    }

    public void empty(final Container target) {
        if (container.isEmpty()) {
            player.sendMessage("The herb sack is empty.");
            return;
        }
        player.sendMessage("You rummage around to see if you can extract any herbs from your herb sack.");
        for (val entry : container.getItems().int2ObjectEntrySet()) {
            val herb = entry.getValue();
            val slot = entry.getIntKey();
            target.deposit(player, container, slot, herb.getAmount());
        }
        target.refresh(player);
    }

    public int getSize() {
        var size = 0;
        for (val entry : container.getItems().int2ObjectEntrySet()) {
            size += entry.getValue().getAmount();
        }
        return size;
    }

    public Int2ObjectLinkedOpenHashMap<Item> getHerbs() {
        return container.getItems();
    }

    public int getAmountOf(final int id) {
        return container.getAmountOf(id);
    }

    public void clear() {
        container.clear();
    }
}
