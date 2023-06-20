package com.zenyte.game.content.partyroom;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

import java.util.Optional;

/**
 * @author Kris | 25/12/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PartyDropChestInterface extends Interface {

    @Override
    protected void attach() {
        put(89, "Accept");
        put(115, "Chest public deposit container");
        put(116, "Chest private deposit container");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
        GameInterface.PARTY_DROP_CHEST_INVENTORY.open(player);
        val partyroom = FaladorPartyRoom.getPartyRoom();
        partyroom.startViewing(player);
        val container = partyroom.getContainer();
        container.setFullUpdate(true);
        container.refresh(player);
        val privateContainer = partyroom.getPrivateContainer(player);
        privateContainer.setFullUpdate(true);
        privateContainer.refresh(player);
    }

    @Override
    public void close(final Player player, final Optional<GameInterface> replacement) {
        FaladorPartyRoom.getPartyRoom().stopViewing(player);
        player.getInterfaceHandler().closeInterface(GameInterface.PARTY_DROP_CHEST_INVENTORY);
        val privateContainer = FaladorPartyRoom.getPartyRoom().getPrivateContainer(player);
        val inventory = player.getInventory();
        privateContainer.getItems().values().forEach(inventory::addOrDrop);
        privateContainer.clear();
    }

    @Override
    protected void build() {
        bind("Accept", player -> {
            val partyroom = FaladorPartyRoom.getPartyRoom();
            val container = partyroom.getPrivateContainer(player);
            if (container.isEmpty()) {
                return;
            }
            if (partyroom.getVariables().isDisabled()) {
                player.sendMessage("The party room has been disabled.");
                return;
            }
            if (!player.getPrivilege().eligibleTo(partyroom.getVariables().getMinimumPrivilegeToDepositItems())) {
                player.sendMessage("You are not eligible to deposit items in the party chest right now.");
                return;
            }
            val length = container.getContainerSize();
            val publicContainer = partyroom.getContainer();
            val minimumStackValue = partyroom.getVariables().getMinimumItemStackValueThreshold();
            boolean unableToDeposit = false;
            for (int i = 0; i < length; i++) {
                val item = container.get(i);
                if (item == null) {
                    continue;
                }
                if (minimumStackValue > 0) {
                    val valueOfStack = (long) item.getSellPrice() * item.getAmount();
                    if (valueOfStack < minimumStackValue) {
                        unableToDeposit = true;
                        player.sendMessage(Colour.RED.wrap(item.getAmount() + " x " + item.getName() + " could not be deposited: Value of stack under the threshold of " + minimumStackValue + "."));
                        continue;
                    }
                }
                publicContainer.deposit(null, container, i, item.getAmount());
            }
            container.setFullUpdate(true);
            container.refresh(player);
            publicContainer.setFullUpdate(true);
            partyroom.forEachViewing(publicContainer::refresh);
            if (!unableToDeposit && !container.isEmpty()) {
                player.sendMessage("Not enough space in the drop chest.");
            }
        });
        bind("Chest private deposit container", (player, slotId, itemId, option) -> {
            val container = FaladorPartyRoom.getPartyRoom().getPrivateContainer(player);
            val item = container.get(slotId);
            if (item == null) {
                return;
            }
            val inventory = player.getInventory();
            val inventoryContainer = inventory.getContainer();
            if (option == 5) {
                player.sendInputInt("How many would you like to withdraw?", value -> {
                    if (container.get(slotId) != item) {
                        return;
                    }
                    inventoryContainer.deposit(player, container, slotId, value);
                    container.refresh(player);
                    inventoryContainer.refresh(player);
                    PartyDropInventoryInterface.refreshContainer(player);
                });
                return;
            }
            val amount = option == 1 ? 1 : option == 2 ? 5 : option == 3 ? 10 : item.getAmount();
            inventoryContainer.deposit(player, container, slotId, amount);
            container.refresh(player);
            inventoryContainer.refresh(player);
            PartyDropInventoryInterface.refreshContainer(player);
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.PARTY_DROP_CHEST;
    }
}
