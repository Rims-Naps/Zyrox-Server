package com.zenyte.game.world.entity.player.collectionlog;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Notification;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import mgi.types.config.enums.EnumDefinitions;
import mgi.types.config.StructDefinitions;
import com.zenyte.plugins.events.InitializationEvent;
import com.zenyte.plugins.events.PostInitializationEvent;
import com.zenyte.utils.StaticInitializer;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

import static com.zenyte.game.world.entity.player.collectionlog.CollectionLogInterface.STRUCT_POINTER_ENUM_CAT;
import static com.zenyte.game.world.entity.player.collectionlog.CollectionLogInterface.STRUCT_POINTER_SUB_ENUM_CAT;

/**
 * @author Kris | 13/03/2019 21:10
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@StaticInitializer
public class CollectionLog {

    private static final IntOpenHashSet COLLECTION_LOG_ITEMS = new IntOpenHashSet(1500);
    private static final IntOpenHashSet UNTRADABLE_COLLECTION_LOG_ITEMS = new IntOpenHashSet(1500);
    
    static {
        for (val type : CLCategoryType.values()) {
            val struct = Objects.requireNonNull(StructDefinitions.get(type.struct()));
            val optional = struct.getValue(STRUCT_POINTER_ENUM_CAT);
            val enumId = optional.orElseThrow(RuntimeException::new);
            assert enumId instanceof Integer;
            val categoryEnum = EnumDefinitions.getIntEnum((Integer) enumId);
            val entrySet = categoryEnum.getValues().int2IntEntrySet();
            for (val entry : entrySet) {
                val subCategoryStructId = entry.getIntValue();
                val subCategoryStruct = Objects.requireNonNull(StructDefinitions.get(subCategoryStructId));
                val subEnumId = Integer.parseInt(subCategoryStruct.getValue(STRUCT_POINTER_SUB_ENUM_CAT).orElseThrow(RuntimeException::new).toString());
                val subEnum = EnumDefinitions.getIntEnum(subEnumId);
                val subEnumEntrySet = subEnum.getValues().int2IntEntrySet();
                for (val e : subEnumEntrySet) {
                    COLLECTION_LOG_ITEMS.add(e.getIntValue());
                    if (!new Item(e.getIntValue()).isTradable()) {
                        UNTRADABLE_COLLECTION_LOG_ITEMS.add(e.getIntValue());
                    }
                }
            }
        }
    }

    @Getter private final Container container;
    @Getter private transient Player player;

    public CollectionLog(Player player) {
        this.container = new Container(ContainerPolicy.ALWAYS_STACK, ContainerType.COLLECTION_LOG, Optional.empty());
        this.player = player;
    }

    @Subscribe
    public static final void onInitialization(final InitializationEvent event) {
        val player = event.getPlayer();
        val savedPlayer = event.getSavedPlayer();
        val otherCollectionLog = savedPlayer.getCollectionLog();
        if (otherCollectionLog == null || otherCollectionLog.container == null || otherCollectionLog.container.isEmpty()) {
            return;
        }
        player.getCollectionLog().container.setContainer(otherCollectionLog.container);
    }
    
    @Subscribe
    public static final void postInit(final PostInitializationEvent event) {
        val player = event.getPlayer();
        val allItems = new Int2IntOpenHashMap(1500);
        
        for (val itemCollection : Utils.concatenate(player.getInventory().getContainer().getItems().values(),
                player.getBank().getContainer().getItems().values(),
                player.getEquipment().getContainer().getItems().values(),
                player.getRetrievalService().getContainer().getItems().values())) {
            for (val item : itemCollection) {
                if (!item.isTradable()) {
                    allItems.addTo(item.getId(), item.getAmount());
                }
            }
        }
        
        for (int itemId : UNTRADABLE_COLLECTION_LOG_ITEMS) {
            if (player.getCollectionLog().getContainer().contains(itemId, 1)) {
                continue;
            }
            if (allItems.containsKey(itemId)) {
                player.getCollectionLog().add(new Item(itemId, allItems.get(itemId)));
            }
        }
    }

    public void add(@NotNull final Item item) {
        val unnotedItem = item.getDefinitions().isNoted() ? new Item(item.getDefinitions().getUnnotedOrDefault(), item.getAmount()) : item;
        if (!COLLECTION_LOG_ITEMS.contains(unnotedItem.getId())) {
            return;
        }
        if(!player.getCollectionLog().getContainer().contains(item) && !player.getCollectionLog().getContainer().contains(new Item(item.getDefinitions().getNotedId()))) {
            val itemName = item.getName();
            player.getNotifications().addLast(new Notification("Collection Log", "New Item:" + "<br>" + "<br>" + "<col=ffffff>" + itemName + "</col>", -1));
            player.sendMessage("New item added to your collection log: " + Colour.RED.wrap(itemName));
        }
        container.add(unnotedItem);
    }

}
