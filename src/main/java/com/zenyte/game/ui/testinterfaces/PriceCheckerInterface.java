package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Examine;
import com.zenyte.game.util.TextUtils;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Optional;

/**
 * @author Kris | 11/05/2019 20:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PriceCheckerInterface extends Interface {

    @Override
    protected void attach() {
        put(10, "Add all");
        put(5, "Search for item");
        put(2, "Interact with item");
        put(8, "Item sprite in search");
        put(12, "Item name in search");
    }

    @Override
    public void close(final Player player, final Optional<GameInterface> replacement) {
        player.getPriceChecker().returnItems();
        player.getInterfaceHandler().closeInterface(GameInterface.PRICE_CHECKER_INVENTORY);
    }

    @Override
    public void open(final Player player) {
        if (player.getAttributes().get("was inside tournament lobby") != null || player.inArea("Tournament Zone")) {
            player.sendMessage("Cannot open price checker from inside of the tournament restricted areas.");
            return;
        }
        val packetDispatcher = player.getPacketDispatcher();
        player.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, id);
        packetDispatcher.sendClientScript(600, 1, 1, 15, (id << 16) | (getComponent("Item name in search")));
        packetDispatcher.sendComponentSettings(id, getComponent("Interact with item"), 0, 27, AccessMask.CLICK_OP1, AccessMask.CLICK_OP2,
                AccessMask.CLICK_OP3, AccessMask.CLICK_OP4, AccessMask.CLICK_OP5, AccessMask.CLICK_OP10);
        packetDispatcher.sendComponentText(id, getComponent("Item name in search"), "Total guide price:<br><col=ffffff>0</col>");
        val container = player.getPriceChecker().getContainer();
        container.setFullUpdate(true);
        container.refresh(player);
        GameInterface.PRICE_CHECKER_INVENTORY.open(player);
    }

    @Override
    protected void build() {
        bind("Interact with item", (player, slotId, itemId, option) -> {
            final Item item = player.getPriceChecker().get(slotId);
            if (item == null) {
                return;
            }
            if (item.getId() != itemId) {
                return;
            }
            if (option == 10) {
                Examine.sendItemExamine(player, item);
                return;
            }
            if (option == 5) {
                player.sendInputInt("Enter amount:", amount -> player.getPriceChecker().withdraw(player, player.getInventory().getContainer(), slotId, amount));
                return;
            }
            val amount = option == 1 ? 1 : option == 2 ? 5 : option == 3 ? 10 : Integer.MAX_VALUE;
            player.getPriceChecker().withdraw(player, player.getInventory().getContainer(), slotId, amount);
        });
        bind("Search for item", (player, slotId, itemId, option) -> player.sendInputItem("Select an item to ask about its price:", item -> {
            player.getPacketDispatcher().sendComponentItem(id, getComponent("Item sprite in search"), item.getId(), 1);
            player.getPacketDispatcher().sendClientScript(600, 0, 1, 15, (id << 16) | (getComponent("Item name in search")));
            player.getPacketDispatcher().sendComponentText(id, getComponent("Item name in search"), item.getName() + ":<br><col=ffffff>" + TextUtils.formatCurrency(item.getSellPrice()) + " coins</col>");
        }));
        bind("Add all", (player, slotId, itemId, option) -> {
            val container = player.getInventory().getContainer();
            if (container.getSize() == 0) {
                player.sendMessage("Your inventory is already empty.");
                return;
            }
            final MutableInt untradablesCount = new MutableInt();
            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                val item = container.get(slot);
                if (item == null) {
                    continue;
                }
                if (!item.isTradable()) {
                    untradablesCount.increment();
                    continue;
                }
                player.getPriceChecker().deposit(player, container, slot, item.getAmount());
            }
            if (untradablesCount.intValue() > 0) {
                player.sendMessage("You have items that cannot be traded.");
            }
            if (container.getSize() > untradablesCount.intValue()) {
                player.sendMessage("Not enough space in your price checker.");
            }
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.PRICE_CHECKER;
    }
}
