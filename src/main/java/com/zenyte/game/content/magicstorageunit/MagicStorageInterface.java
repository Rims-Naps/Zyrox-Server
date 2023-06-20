package com.zenyte.game.content.magicstorageunit;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Examine;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import lombok.val;
import mgi.types.config.enums.Enums;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 15/09/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class MagicStorageInterface extends Interface {

    private static final int SCRIPT_ID = 417;
    private static final int RESET_SCROLL_POS = 1;
    private static final int DO_NOT_RESET_SCROLL_POS = 0;

    @Override
    protected void attach() {
        put(2, "Interact with element");
    }

    @Override
    public void open(Player player) {
        openPage(player, player.getNumericTemporaryAttribute(pageKey).intValue(), true);
    }

    @Override
    protected void build() {
        bind("Interact with element", (player, slotId, itemId, option) -> {
            val page = player.getNumericTemporaryAttribute(pageKey).intValue();
            val typeObject = player.getTemporaryAttributes().get(typeKey);
            if (!(typeObject instanceof StorageUnitType)) {
                throw new IllegalStateException();
            }
            val type = (StorageUnitType) typeObject;
            val container = StorageUnitCollection.getSingleton().getContainer(type, page);
            val itemSlot = slotId >> 2;
            val item = container.get(itemSlot);
            if (option == 10) {
                Examine.sendItemExamine(player, item.getId());
            } else if (option == 1) {
                if (item.getId() == ItemId.MORE) {
                    openPage(player, page + 1, true);
                } else if (item.getId() == ItemId.BACK) {
                    openPage(player, page - 1, true);
                } else {
                    val storage = player.getMagicStorageUnit();
                    val set = storage.getStoredItemIds(item.getId());
                    if (set == null) {
                        throw new IllegalStateException();
                    }
                    val inventory = player.getInventory();
                    val storageEnum = Enums.COSTUME_STORAGE_UNIT_ENUM;
                    val name = storageEnum.getValue(item.getId()).orElseThrow(RuntimeException::new);
                    if (inventory.getFreeSlots() < set.size()) {
                        player.sendMessage("You do not have enough free inventory space to take the " + name + ".");
                        return;
                    }
                    for (val id : set) {
                        inventory.addItem(new Item(id));
                    }
                    storage.remove(item.getId());
                    openPage(player, page, false);
                    player.sendMessage("You take the " + name + " from the magic storage unit.");
                }
            }
        });
    }

    public void openPage(@NotNull final Player player, final int page, final boolean reset) {
        val typeObject = player.getTemporaryAttributes().get(typeKey);
        if (!(typeObject instanceof StorageUnitType)) {
            throw new IllegalStateException();
        }
        player.getInterfaceHandler().sendInterface(this);
        val type = (StorageUnitType) typeObject;
        player.getTemporaryAttributes().put(pageKey, page);
        val container = StorageUnitCollection.getSingleton().getContainer(type, page);
        player.getPacketDispatcher().sendUpdateItemContainer(container);
        val value = heldItemsClientscriptValue(player, container);
        player.getPacketDispatcher().sendClientScript(SCRIPT_ID, ContainerType.MAGIC_STORAGE.getId(), (int) (value & 0x7fffffff), (int) ((value >> 31) & 0x7fffffff),
                 reset ? RESET_SCROLL_POS : DO_NOT_RESET_SCROLL_POS, Utils.formatString(type.toString().replace("_", " ")));
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Interact with element"), 0,
                StorageUnitCollection.MAXIMUM_ELEMENTS_PER_PAGE << 2, AccessMask.CLICK_OP1, AccessMask.CLICK_OP10);
    }

    private long heldItemsClientscriptValue(@NotNull final Player player, @NotNull final Container container) {
        val storage = player.getMagicStorageUnit();
        long value = 0;
        for (val entry : container.getItems().int2ObjectEntrySet()) {
            val slot = entry.getIntKey();
            val item = entry.getValue();
            val id = item.getId();
            if (id == ItemId.MORE || id == ItemId.BACK || storage.getStoredItemIds(id) != null) {
                value |= 1L << slot;
            }
        }
        return value;
    }

    private static final String typeKey = "magic storage unit type";
    private static final String pageKey = "magic storage unit interface index";

    public static final void view(@NotNull final Player player, @NotNull final StorageUnitType type) {
        player.getTemporaryAttributes().put(typeKey, type);
        player.getTemporaryAttributes().remove(pageKey);
        GameInterface.MAGIC_STORAGE.open(player);
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.MAGIC_STORAGE;
    }
}
