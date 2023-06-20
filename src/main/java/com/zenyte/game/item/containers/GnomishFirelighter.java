package com.zenyte.game.item.containers;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.zenyte.game.content.skills.farming.FarmingProduct;
import com.zenyte.game.content.skills.farming.Seedling;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.plugins.dialogue.ItemChat;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import lombok.Getter;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

/**
 * @author Cresinkel
 */
@Getter
public class GnomishFirelighter {

    private transient Player player;
    private final Container container;

    private static final Map<Integer, String> LIGHTERS = new HashMap<Integer, String>() {{
        put(7329, "Red firelighter");
        put(7330, "Green firelighter");
        put(7331, "Blue firelighter");
        put(10326, "Purple firelighter");
        put(10327, "White firelighter");
    }};

    private static final Integer[] IDS = LIGHTERS.keySet().toArray(new Integer[LIGHTERS.size()]);

    public static final Item GNOMISH_FIRELIGHTER = new Item(20278);

    public GnomishFirelighter(final Player player) {
        this.player = player;
        this.container = new Container(ContainerPolicy.NORMAL, ContainerType.GNOMISH_FIRELIGHTER, Optional.of(player));
    }

    public void initialize(final GnomishFirelighter bag) {
        if (bag != null && bag.container != null) {
            container.setContainer(bag.container);
        }
    }

    public void fill() {
        val inventory = player.getInventory();
        val inventoryContainer = inventory.getContainer();
        val bool = new MutableBoolean();
        for (int slot = 0, length = inventoryContainer.getContainerSize(); slot < length; slot++) {
            val item = inventoryContainer.get(slot);
            if (item == null || !ArrayUtils.contains(IDS,item.getId())) {
                continue;
            }
            container.deposit(null, inventoryContainer, slot, item.getAmount());
            if (inventoryContainer.get(slot) != null) {
                bool.setTrue();
            }
        }
        if (bool.isTrue()) {
            player.sendMessage("You can't store anymore lighters to the box.");
        }
        inventory.refreshAll();
        refresh();
    }

    public void refresh() {
        container.setFullUpdate(true);
        container.refresh(player);
    }

    public void check() {
        val builder = new StringBuilder();
        LIGHTERS.forEach((id, name) -> {
            val amount = container.getAmountOf(id);
            builder.append(name)
                    .append(": ")
                    .append(amount)
                    .append(id == IDS[2] ? "<br>" : " / ");
        });
        player.getDialogueManager().start(new ItemChat(player, GNOMISH_FIRELIGHTER, builder.toString().substring(0, builder.toString().length() - 2)));
    }

    public void empty(final Container target) {
        if (container.isEmpty()) {
            player.sendMessage("The gnomish firelighter is empty.");
            return;
        }
        for (val entry : container.getItems().int2ObjectEntrySet()) {
            val gem = entry.getValue();
            val slot = entry.getIntKey();
            target.deposit(player, container, slot, gem.getAmount());
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

    public int getAmountOf(final int id) {
        return container.getAmountOf(id);
    }

    public Int2ObjectLinkedOpenHashMap<Item> getLighters() {
        return container.getItems();
    }

    public void clear() {
        container.clear();
    }
}