package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.containers.GemBag;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.SwitchPlugin;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.game.world.entity.player.container.impl.LootingBag;
import com.zenyte.plugins.item.CoalBag;
import com.zenyte.plugins.item.RunecraftingPouch;
import lombok.AllArgsConstructor;
import lombok.val;

import static com.zenyte.game.constants.GameInterface.BANK_INVENTORY;
import static com.zenyte.game.util.AccessMask.CLICK_OP1;
import static com.zenyte.game.util.AccessMask.CLICK_OP10;
import static com.zenyte.game.util.AccessMask.CLICK_OP2;
import static com.zenyte.game.util.AccessMask.CLICK_OP3;
import static com.zenyte.game.util.AccessMask.CLICK_OP4;
import static com.zenyte.game.util.AccessMask.CLICK_OP5;
import static com.zenyte.game.util.AccessMask.CLICK_OP6;
import static com.zenyte.game.util.AccessMask.CLICK_OP7;
import static com.zenyte.game.util.AccessMask.CLICK_OP8;
import static com.zenyte.game.util.AccessMask.CLICK_OP9;
import static com.zenyte.game.util.AccessMask.DRAG_DEPTH1;
import static com.zenyte.game.util.AccessMask.DRAG_TARGETABLE;

/**
 * @author Kris | 21/10/2018 09:43
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BankInventoryInterface extends Interface implements SwitchPlugin {

    @Override
    protected void attach() {
        put(3, "Interact with Item");
        put(5, "Empty Looting Bag");
        put(10, "Deposit Looting Bag Item");
    }

    @Override
    public void open(Player player) {
        if (!player.getInterfaceHandler().isPresent(GameInterface.BANK)) throw new RuntimeException("Bank inventory overlay cannot be opened without the presence of bank itself.");
        val dispatcher = player.getPacketDispatcher();
        val inventorySize = ContainerType.INVENTORY.getSize();
        dispatcher.sendComponentSettings(BANK_INVENTORY, getComponent("Interact with Item"), 0, inventorySize, CLICK_OP1, CLICK_OP2, CLICK_OP3, CLICK_OP4, CLICK_OP5, CLICK_OP6, CLICK_OP7, CLICK_OP8, CLICK_OP9, CLICK_OP10, DRAG_DEPTH1, DRAG_TARGETABLE);
        player.getInterfaceHandler().sendInterface(getInterface());
    }

    @Override
    protected void build() {
        bind("Deposit Looting Bag Item", (player, slotId, itemId, option) -> {
            val lootingBag = player.getLootingBag();
            val item = lootingBag.getItem(slotId);
            if (item == null) {
                return;
            }
            if (option == 9) {
                player.sendMessage(item.getDefinitions().getExamine());
            } else if (option == 4) {
                player.sendInputInt("Amount to deposit:", amount -> player.getBank().deposit(player, lootingBag.getContainer(), slotId, amount));
            } else {
                val amount = option == 1 ? 1 : option == 2 ? 5 : lootingBag.getContainer().getAmountOf(item.getId());
                player.getBank().deposit(player, lootingBag.getContainer(), slotId, amount);
            }
        });
        bind("Empty Looting Bag", player -> {
            val lootingBag = player.getLootingBag();
            for (int slotId = 0; slotId < 28; slotId++) {
                val item = lootingBag.getItem(slotId);
                if (item == null) {
                    continue;
                }
                player.getBank().deposit(player, lootingBag.getContainer(), slotId, item.getAmount());
            }
        });
        bind("Interact with Item", "Interact with Item", ((player, fromSlot, toSlot) -> {
            if (fromSlot < 0 || toSlot < 0 || fromSlot >= ContainerType.INVENTORY.getSize() || toSlot >= ContainerType.INVENTORY.getSize()) return;
            player.getInventory().switchItem(fromSlot, toSlot);
        }));

        bind("Interact with Item", ((player, slotId, itemId, option) -> {
            val item = player.getInventory().getItem(slotId);
            if (item == null || item.getId() != itemId) {
                return;
            }

            if (option == 1 && !player.getLootingBag().isEmpty() && LootingBag.isBag(item.getId())) {
                player.getLootingBag().refresh();
                player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Deposit Looting Bag Item"), 0, ContainerType.INVENTORY.getSize(), AccessMask.CLICK_OP1, AccessMask.CLICK_OP2, AccessMask.CLICK_OP3, AccessMask.CLICK_OP4, AccessMask.CLICK_OP10);
                return;
            }
            if (option == 9) {
                if (item.getId() == 13226) {
                    player.getHerbSack().empty(player.getBank().getContainer());
                } else if (item.getId() == 13639) {
                    player.getSeedBox().emptyToBank();
                } else if (item.getId() == CoalBag.ITEM.getId()) {
                    CoalBag.emptyBagToBank(item, player);
                } else if (item.getId() == GemBag.GEM_BAG.getId()) {
                    player.getGemBag().empty(player.getBank().getContainer());
                } else if (item.getId() >= 12792 && item.getId() <= 12794) {
                    val charges = item.getCharges();
                    val id = item.getId();
                    val nestId = id == 12792 ? 5075 : id == 12793 ? 5073 : 5074;
                    val amount = player.getBank().add(new Item(nestId, charges)).getSucceededAmount();
                    player.getBank().refreshContainer();
                    if (amount >= charges) {
                        player.getInventory().deleteItem(slotId, item);
                    } else {
                        item.setCharges(charges - amount);
                        player.sendMessage("Not enough space in your bank.");
                    }
                } else if (item.getId() == 12791) {
                    val container = player.getRunePouch().getContainer();
                    if (container.isEmpty()) {
                        player.sendMessage("Your rune pouch is already empty.");
                        return;
                    }
                    player.getRunePouch().emptyRunePouch();
                } else if (RunecraftingPouch.pouches.contains(item.getId())) {
                    RunecraftingPouch.fill(player, item, player.getBank().getContainer(), slotId);
                }
                return;
            }
            val op = ItemOption.of(option);
            if (op.equals(ItemOption.EXAMINE)) {
                player.sendMessage(item.getDefinitions().getExamine());
                return;
            }

            if (op.equals(ItemOption.WITHDRAW_X)) {
                player.sendInputInt("How many would you like to deposit?", amount -> {
                    player.getBank().setLastDepositAmount(amount);
                    player.getVarManager().sendBit(BankInterface.VAR_LAST_DEPOSIT_AMOUNT, player.getBank().getLastDepositAmount());
                    player.getBank().deposit(player, player.getInventory().getContainer(), slotId, amount);
                });
                return;
            }

            val amount = op.equals(ItemOption.WITHDRAW_1_OR_SELECTED) ? player.getBank().getCurrentQuantity() : op.equals(ItemOption.WITHDRAW_LAST_AMOUNT) ? player.getBank().getLastDepositAmount() : op.amount;
            player.getBank().deposit(player, player.getInventory().getContainer(), slotId, amount);
        }));
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.BANK_INVENTORY;
    }

    @AllArgsConstructor
    private enum ItemOption {
        WITHDRAW_1_OR_SELECTED(2, 1),
        WITHDRAW_1(3, 1),
        WITHDRAW_5(4, 5),
        WITHDRAW_10(5, 10),
        WITHDRAW_LAST_AMOUNT(6, -1),
        WITHDRAW_X(7, -1),
        WITHDRAW_ALL(8, Integer.MAX_VALUE),
        EXAMINE(10, -1);

        private static final ItemOption[] values = values();

        private final int optionId;
        private final int amount;


        /**
         * Gets the ItemOption constant for the input integer option id.
         *
         * @param option the option id.
         * @return the ItemOption constant.
         */
        private static final ItemOption of(final int option) {
            val opt = Utils.findMatching(values, v -> v.optionId == option);
            if (opt == null) {
                throw new IllegalArgumentException("Option cannot be " + option + ".");
            }
            return opt;
        }
    }
}
