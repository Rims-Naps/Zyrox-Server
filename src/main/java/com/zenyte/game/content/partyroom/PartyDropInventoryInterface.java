package com.zenyte.game.content.partyroom;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.SwitchPlugin;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 25/12/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PartyDropInventoryInterface extends Interface implements SwitchPlugin {
    @Override
    protected void attach() {
        put(0, "Interact with item");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
        val packetDispatcher = player.getPacketDispatcher();
        packetDispatcher.sendComponentSettings(id, getComponent("Interact with item"), 0, 27, AccessMask.CLICK_OP1, AccessMask.CLICK_OP2,
                AccessMask.CLICK_OP3, AccessMask.CLICK_OP4, AccessMask.CLICK_OP5, AccessMask.CLICK_OP10);
        refreshContainer(player);
    }

    @Override
    protected void build() {
        bind("Interact with item", (player, slotId, itemId, option) -> {
            val inventory = player.getInventory();
            val item = inventory.getItem(slotId);
            if (item == null || item.getId() != itemId) {
                return;
            }
            val inventoryContainer = inventory.getContainer();
            val container = FaladorPartyRoom.getPartyRoom().getPrivateContainer(player);
            if (option == 5) {
                player.sendInputInt("How many would you like to deposit?", value -> {
                    if (inventory.getItem(slotId) != item) {
                        return;
                    }
                    if (!item.isTradable()) {
                        player.sendMessage("You can't trade that.");
                        return;
                    }
                    container.deposit(player, inventoryContainer, slotId, value);
                    container.refresh(player);
                    refreshContainer(player);
                });
                return;
            }
            if (!item.isTradable()) {
                player.sendMessage("You can't trade that.");
                return;
            }
            val amount = option == 1 ? 1 : option == 2 ? 5 : option == 3 ? 10 : (!item.isStackable() ? inventory.getAmountOf(item.getId()) : item.getAmount());
            container.deposit(player, inventoryContainer, slotId, amount);
            container.refresh(player);
            refreshContainer(player);
        });
        bind("Interact with item", "Interact with item", (player, fromSlot, toSlot) -> {
            player.getInventory().switchItem(fromSlot, toSlot);
            refreshContainer(player);
        });
    }

    static final void refreshContainer(@NotNull final Player player) {
        player.getPacketDispatcher().sendUpdateItemContainer(player.getInventory().getContainer(), ContainerType.FALADOR_PARTY_CHEST_INVENTORY_DEPOSIT);
        player.getInventory().refreshAll();
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.PARTY_DROP_CHEST_INVENTORY;
    }
}
