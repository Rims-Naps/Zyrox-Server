package com.zenyte.game.content.chambersofxeric.storageunit;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.SwitchPlugin;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Examine;
import com.zenyte.game.world.entity.player.GameMode;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.bank.Bank;
import mgi.types.config.enums.Enums;
import lombok.val;

/**
 * @author Kris | 21/07/2019 02:12
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class StorageInventoryInterface extends Interface implements SwitchPlugin {

    /**
     * The access masks for the private storage unit to allow clicking and dragging elements on it.
     */
    private static final AccessMask[] privateMasks = new AccessMask[]{
            AccessMask.CLICK_OP1, AccessMask.CLICK_OP2, AccessMask.CLICK_OP3, AccessMask.CLICK_OP4, AccessMask.CLICK_OP5, AccessMask.CLICK_OP10, AccessMask.DRAG_DEPTH1,
            AccessMask.DRAG_TARGETABLE
    };

    /**
     * The access masks for the shared storage unit to allow clicking elements on it.
     */
    private static final AccessMask[] publicMasks = new AccessMask[]{
            AccessMask.CLICK_OP1, AccessMask.CLICK_OP2, AccessMask.CLICK_OP3, AccessMask.CLICK_OP4, AccessMask.CLICK_OP5, AccessMask.CLICK_OP10
    };

    @Override
    protected void attach() {
        put(1, "Interact with item");
        put(8, "Dismiss storage warning");
    }

    @Override
    public void open(final Player player) {
        val interfaceHandler = player.getInterfaceHandler();
        if (!interfaceHandler.isPresent(GameInterface.RAIDS_SHARED_STORAGE) && !interfaceHandler.isPresent(GameInterface.RAIDS_PRIVATE_STORAGE)) {
            throw new IllegalStateException("Cannot open storage inventory interface directly.");
        }
        interfaceHandler.sendInterface(this);
        player.getVarManager().sendBit(3459, !interfaceHandler.isPresent(GameInterface.RAIDS_PRIVATE_STORAGE) ? 0 : 1);
        player.getVarManager().sendBit(5509, player.getNumericAttribute("dismissed raids storage warning").intValue());
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Interact with item"), 0, 27,
                interfaceHandler.isPresent(GameInterface.RAIDS_PRIVATE_STORAGE) ? privateMasks : publicMasks);
    }

    @Override
    protected void build() {
        bind("Interact with item", (player, slotId, itemId, option) -> {
            val inventory = player.getInventory();
            val item = inventory.getItem(slotId);
            if (item == null || item.getId() != itemId) {
                return;
            }
            if (option == 10) {
                if (player.getInterfaceHandler().isPresent(GameInterface.RAIDS_SHARED_STORAGE)) {
                    if (!Enums.RAIDS_ONLY_ITEMS.getKey(item.getId()).isPresent()) {
                        player.sendMessage("You cannot donate that item in the shared storage.");
                        return;
                    }
                }
                Examine.sendItemExamine(player, item);
            }
            val optionalRaid = player.getRaid();
            if (!optionalRaid.isPresent()) {
                player.sendMessage("You can't store items now.");
                return;
            }
            val raid = optionalRaid.get();
            val privateStorage = player.getInterfaceHandler().isPresent(GameInterface.RAIDS_PRIVATE_STORAGE);
            final Storage storage = (privateStorage ? player.getPrivateStorage() : raid.constructOrGetSharedStorage());
            if (privateStorage && player.getGameMode().equals(GameMode.ULTIMATE_IRON_MAN)) {
                player.sendMessage("You cannot use the storage units as an ultimate ironman.");
                return;
            }
            if (Bank.unbankableItems.contains(item.getId())) {
                player.sendMessage("This item cannot be stored in here.");
                return;
            }
            val predicate = Bank.predicateMap.get(item.getId());
            if (predicate != null && !predicate.test(player)) {
                return;
            }
            switch (option) {
                case 1:
                    storage.deposit(player, slotId, 1);
                    break;
                case 2:
                    storage.deposit(player, slotId, 5);
                    break;
                case 3:
                    storage.deposit(player, slotId, 10);
                    break;
                case 4:
                    storage.deposit(player, slotId, inventory.getAmountOf(item.getId()));
                    break;
                case 5:
                    player.sendInputInt("Enter amount:", amount -> storage.deposit(player, slotId, amount));
                    break;
            }
        });
        bind("Dismiss storage warning", (player, slotId, itemId, option) -> {
            player.addAttribute("dismissed raids storage warning", 1);
            player.getVarManager().sendBit(3459, 1);
        });
        bind("Interact with item", "Interact with item", (player, fromSlot, toSlot) -> player.getInventory().switchItem(fromSlot, toSlot));
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.RAIDS_STORAGE_INVENTORY_INTERFACE;
    }
}
