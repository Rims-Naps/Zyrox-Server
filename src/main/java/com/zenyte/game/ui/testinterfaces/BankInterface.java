package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.ui.SwitchPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.GameMode;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.game.world.entity.player.container.impl.bank.Bank;
import com.zenyte.game.world.entity.player.container.impl.bank.BankSetting;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.val;
import lombok.var;
import mgi.types.config.InventoryDefinitions;

import java.util.Optional;

import static com.zenyte.game.constants.GameInterface.BANK;
import static com.zenyte.game.util.AccessMask.*;


/**
 * @author Kris | 19/10/2018 22:58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BankInterface extends Interface implements SwitchPlugin {

    static final int VAR_LAST_DEPOSIT_AMOUNT = 3960;
    public static final int VAR_CURRENT_TAB = 4150;
    private static final int VAR_DISPLAY_TYPE = 4170;
    private static final int CAPACITY_TOOLTIP_SCRIPT = 1495, INPUT_SCREEN_CLOSE_SCRIPT = 101, BANK_SEARCH_TYPE = 11;

    @Override
    protected void attach() {
        put(67, "Add bank fillers");//Component updated.
        put(56, "Set bank filler amount to 1");//Component updated.
        put(59, "Set bank filler amount to 10");//Component updated.
        put(61, "Set bank filler amount to 50");//Component updated.
        put(63, "Set bank filler amount to X");//Component updated.
        put(65, "Set bank filler amount to All");//Component updated.
        put(17, "Swap mode");
        put(19, "Insert mode");
        put(22, "Withdraw as Item");
        put(24, "Withdraw as Note");
        put(50, "Tab display");
        put(54, "Release all placeholders");//Component updated.
        put(47, "Incinerate item");
        put(42, "Deposit inventory");
        put(44, "Deposit equipment");
        put(11, "Interact with tab");
        put(38, "Always set placeholders");
        put(51, "Toggle incinerator");//Component updated.
        put(52, "Deposit worn items button");
        put(13, "Interact with item");
        put(8, "Bank size");
        put(57, "Capacity tooltip");//Component updated.
        put(28, "First option amount 1");
        put(30, "First option amount 5");
        put(32, "First option amount 10");
        put(34, "First option amount X");
        put(36, "First option amount All");
        put(5, "Bank size in configuration menu");
        put(40, "Search");
        put(68, "Preset Manager");
    }

    @Override
    public void open(Player player) {
        if (player.getGameMode().equals(GameMode.ULTIMATE_IRON_MAN)) {
            player.getDialogueManager().start(new PlainChat(player, "As an Ultimate Iron Man, you cannot use the bank."));
            return;
        }
        if (player.inArea("Tournament Zone")) {
            player.sendMessage("Cannot open bank from inside of the tournament restricted areas.");
            return;
        }
        val bank = player.getBank();
        val dispatcher = player.getPacketDispatcher();
        val interfaceId = getInterface().getId();
        val varManager = player.getVarManager();
        val interfaceHandler = player.getInterfaceHandler();
        val bankInventory = InventoryDefinitions.get(ContainerType.BANK.getId());
        assert bankInventory != null;
        val bankSize = bankInventory.getSize();
        interfaceHandler.closeInterface(InterfacePosition.SINGLE_TAB);
        interfaceHandler.closeInterface(InterfacePosition.CENTRAL);
        varManager.sendBit(VAR_CURRENT_TAB, (bank.getCurrentTab() + 1) % 10);
        varManager.sendBit(VAR_DISPLAY_TYPE, bank.getDisplayType());
        varManager.sendBit(VAR_LAST_DEPOSIT_AMOUNT, bank.getLastDepositAmount());
        dispatcher.sendClientScript(CAPACITY_TOOLTIP_SCRIPT, "Members' capacity: " + (bank.getContainer().getContainerSize()) + "<br>+8 for your PIN<br>+8 for your Authenticator", getComponent("Bank size") | (interfaceId << 16), getComponent("Capacity" + " tooltip") | (interfaceId << 16));
        dispatcher.sendComponentSettings(BANK, getComponent("Interact with item"), 0, bankSize, CLICK_OP1, CLICK_OP2, CLICK_OP3, CLICK_OP4, CLICK_OP5, CLICK_OP6, CLICK_OP7, CLICK_OP8, CLICK_OP9, CLICK_OP10, DRAG_DEPTH2, DRAG_TARGETABLE);
        dispatcher.sendComponentSettings(BANK, getComponent("Interact with item"), 834, 843, DRAG_TARGETABLE);
        dispatcher.sendComponentSettings(BANK, getComponent("Tab display"), 0, 4, CLICK_OP1);
        dispatcher.sendComponentSettings(BANK, getComponent("Interact with tab"), 10, 10, CLICK_OP1, CLICK_OP7, DRAG_TARGETABLE);
        dispatcher.sendComponentSettings(BANK, getComponent("Interact with tab"), 11, 19, CLICK_OP1, CLICK_OP6, CLICK_OP7, DRAG_DEPTH1, DRAG_TARGETABLE);
        dispatcher.sendComponentSettings(BANK, getComponent("Incinerate item"), 1, bankSize + 1, CLICK_OP1);
        dispatcher.sendComponentSettings(BANK, getComponent("Always set placeholders"), 0, 3, CLICK_OP1);
        dispatcher.sendComponentText(BANK, getComponent("Bank size"), bank.getContainer().getContainerSize());
        interfaceHandler.sendInterface(BANK);
        bank.revalidate();
        bank.toggleSetting(BankSetting.WITHDRAW_MODE, false);
        BankSetting.update(player);
        bank.getContainer().setFullUpdate(true);
        bank.getContainer().refresh(player);
        player.getLootingBag().refresh();
        GameInterface.BANK_INVENTORY.open(player);
    }

    @Override
    public void close(final Player player, final Optional<GameInterface> replacement) {
        player.getPacketDispatcher().sendClientScript(INPUT_SCREEN_CLOSE_SCRIPT, BANK_SEARCH_TYPE);
    }

    @Override
    protected void build() {
        bind("Deposit worn items button", player -> player.getBank().toggleSetting(BankSetting.DEPOSIT_ITEMS, player.getBank().getSetting(BankSetting.DEPOSIT_ITEMS) == 0));
        bind("Toggle incinerator", player -> player.getBank().toggleSetting(BankSetting.INCINERATOR, player.getBank().getSetting(BankSetting.INCINERATOR) == 0));
        bind("Always set placeholders", player -> player.getBank().toggleSetting(BankSetting.ALWAYS_PLACEHOLDER, player.getBank().getSetting(BankSetting.ALWAYS_PLACEHOLDER) == 0));
        bind("Incinerate item", (player, slotId, itemId, option) -> player.getBank().incinerate(slotId, itemId));
        bind("Release all placeholders", player -> player.getBank().releasePlaceholders(-1));
        bind("Tab display", (player, slotId, itemId, option) -> player.getBank().setDisplayType(slotId));
        bind("Withdraw as Item", player -> player.getBank().toggleSetting(BankSetting.WITHDRAW_MODE, false));
        bind("Withdraw as Note", player -> player.getBank().toggleSetting(BankSetting.WITHDRAW_MODE, true));
        bind("Swap mode", player -> player.getBank().toggleSetting(BankSetting.REARRANGE_MODE, false));
        bind("Insert mode", player -> player.getBank().toggleSetting(BankSetting.REARRANGE_MODE, true));
        bind("Search", player -> player.getBank().setCurrentTab(Bank.MAIN_TAB));
        bind("Set bank filler amount to 1", player -> player.getTemporaryAttributes().put("bankFillerAmount", 1));
        bind("Set bank filler amount to 10", player -> player.getTemporaryAttributes().put("bankFillerAmount", 10));
        bind("Set bank filler amount to 50", player -> player.getTemporaryAttributes().put("bankFillerAmount", 50));
        bind("Set bank filler amount to All", player -> player.getTemporaryAttributes().put("bankFillerAmount", Integer.MAX_VALUE));
        bind("Set bank filler amount to X", player -> player.sendInputInt("Set bank fillers amount:", a -> player.getTemporaryAttributes().put("bankFillerAmount", a)));
        bind("Add bank fillers", player -> player.getBank().addFillers(player.getNumericTemporaryAttributeOrDefault("bankFillerAmount", Integer.MAX_VALUE).intValue()));
        bind("First option amount 1", player -> player.getBank().setQuantity(Bank.QuantitySelector.ONE));
        bind("First option amount 5", player -> player.getBank().setQuantity(Bank.QuantitySelector.FIVE));
        bind("First option amount 10", player -> player.getBank().setQuantity(Bank.QuantitySelector.TEN));
        bind("First option amount X", (player, slotId, itemId, option) -> {
            if (option == 2) {
                player.sendInputInt("Set quantity selector amount:", a -> {
                    val value = Math.max(a, 1);
                    player.getBank().setLastDepositAmount(value);
                    player.getVarManager().sendBit(VAR_LAST_DEPOSIT_AMOUNT, player.getBank().getLastDepositAmount());
                });
                if (player.getBank().getLastDepositAmount() == 0) {
                    player.getVarManager().sendBit(VAR_LAST_DEPOSIT_AMOUNT, 1);
                }
            }
            player.getBank().setQuantity(Bank.QuantitySelector.X);
        });
        bind("First option amount All", player -> player.getBank().setQuantity(Bank.QuantitySelector.ALL));

        bind("Deposit equipment", (player, slotId, itemId, option) -> {
            if (player.getTemporaryAttributes().containsKey("viewing another bank")) {
                return;
            }
            val container = player.getEquipment().getContainer();
            if (container.getSize() == 0) {
                player.sendMessage("You're not wearing anything.");
                return;
            }
            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                val item = container.get(slot);
                if (item == null) {
                    continue;
                }
                player.getBank().deposit(null, container, slot, item.getAmount());
            }
            player.getEquipment().refreshAll();
            player.getCombatDefinitions().setAutocastSpell(null);
            player.getCombatDefinitions().refresh();
            if (container.getSize() != 0) {
                player.sendMessage("Not enough space in your bank.");
            }
        });

        bind("Deposit inventory", (player, slotId, itemId, option) -> {
            if (player.getTemporaryAttributes().containsKey("viewing another bank")) {
                return;
            }
            val container = player.getInventory().getContainer();
            if (container.getSize() == 0) {
                player.sendMessage("Your inventory is already empty.");
                return;
            }
            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                val item = container.get(slot);
                if (item == null) {
                    continue;
                }
                player.getBank().deposit(null, container, slot, item.getAmount());
            }
            player.getEquipment().refreshAll();
            player.getCombatDefinitions().refresh();

            if (container.getSize() != 0) {
                player.sendMessage("Not enough space in your bank.");
            }
        });

        bind("Interact with tab", (player, slotId, itemId, option) -> {
            if (option == 1) {
                val size = player.getBank().getTabSize(slotId == 10 ? 9 : slotId - 11);
                if (size == 0) {
                    player.sendMessage("Drag an item here to create a new tab.");
                }
                player.getBank().setCurrentTab(slotId == 10 ? 9 : slotId - 11);
            } else if (option == 6) {
                player.getBank().collapseTab(player.getBank().getTabFromSlot(slotId));
            } else if (option == 7) {
                val tab = player.getBank().getTabFromSlot(slotId);
                if (tab < 0 || tab > Bank.MAIN_TAB) {
                    return;
                }
                player.getBank().releasePlaceholders(tab);
            }
        });

        bind("Interact with item", (player, slotId, itemId, option) -> {
            val item = player.getBank().get(slotId);
            if (item == null) {
                return;
            }
            val id = item.getId();
            if (id != itemId) {
                return;
            }

            var op = ItemOption.of(option);

            if (op.is(ItemOption.EXAMINE)) {
                player.sendMessage(item.getDefinitions().getExamine());
                return;
            }

            if (id == 20594) {
                if (op.is(ItemOption.CLEAR_ALL)) {
                    player.getBank().removeFillers(-1, Integer.MAX_VALUE);
                } else if (op.is(ItemOption.CLEAR)) {
                    player.getBank().removeFillers(slotId, 1);
                } else {
                    throw new RuntimeException("No other options available on bank fillers, player: " + player.getName());
                }
                return;
            }

            if (op.is(ItemOption.WITHDRAW_X)) {
                player.sendInputInt("How many would you like to withdraw?", value -> {
                    val itemAmount = player.getBank().getAmountOf(id);
                    player.getBank().setLastDepositAmount(value);
                    player.getVarManager().sendBit(3960, value);
                    player.getBank().withdraw(player, player.getInventory().getContainer(), slotId, Math.min(itemAmount, value), false);
                });
                return;
            }

            val itemAmount = player.getBank().getAmountOf(id);
            if (itemAmount == 0 && op.is(ItemOption.CLEAR_ALL)) {
                op = ItemOption.PLACEHOLDER;
            }
            val amount = op.getAmount(player, itemAmount);
            player.getBank().withdraw(player, player.getInventory().getContainer(), slotId, Math.min(itemAmount, amount), op.is(ItemOption.PLACEHOLDER));
        });
    
        bind("Interact with item", "Interact with item", ((player, fromSlot, toSlot) -> {
            if (toSlot >= 834) {
                val tabId = toSlot == 834 ? 9 : (toSlot - 835);
                player.getBank().switchItem(Bank.BankSwitchType.ITEM_TO_END_OF_TAB, fromSlot, tabId);
            } else {
                player.getBank().switchItem(Bank.BankSwitchType.ITEM_TO_ITEM, fromSlot, toSlot);
            }
        }));
        bind("Interact with item", "Interact with tab", ((player, fromSlot, toSlot) -> player.getBank().switchItem(Bank.BankSwitchType.ITEM_TO_TAB, fromSlot, toSlot)));
        bind("Interact with tab", "Interact with tab", ((player, fromSlot, toSlot) -> player.getBank().switchItem(Bank.BankSwitchType.TAB_TO_TAB, fromSlot, toSlot)));
        bind("Preset Manager", player -> {
            if (player.getTemporaryAttributes().containsKey("viewing another bank")) {
                return;
            }
            player.getInterfaceHandler().closeInterfaces();
            GameInterface.PRESET_MANAGER.open(player);
        });
    }

    @Override
    public GameInterface getInterface() {
        return BANK;
    }

    private enum ItemOption {

        WITHDRAW_1_OR_SELECTED(1, (player, itemAmount) -> player.getBank().getCurrentQuantity()),
        WITHDRAW_1_ALT(2, (player, itemAmount) -> 1),
        WITHDRAW_5(3, (player, itemAmount) -> 5),
        WITHDRAW_10(4, (player, itemAmount) -> 10),
        WITHDRAW_LAST_AMOUNT(5, (player, itemAmount) -> player.getBank().getLastDepositAmount()),
        WITHDRAW_X(6), WITHDRAW_ALL(7, (player, itemAmount) -> itemAmount),
        WITHDRAW_ALL_BUT_1(8, (player, itemAmount) -> itemAmount - 1),
        CLEAR(7),
        CLEAR_ALL(8),
        PLACEHOLDER(9, (player, itemAmount) -> itemAmount),
        EXAMINE(10);

        private static final ItemOption[] values = values();
        private final int optionId;
        private final ParametrizedIntFunction<Player> function;

        ItemOption(final int optionId) {
            this(optionId, (player, itemAmount) -> {
                throw new IllegalArgumentException("Invalid constant.");
            });
        }

        ItemOption(final int optionId, final ParametrizedIntFunction<Player> function) {
            this.optionId = optionId;
            this.function = function;
        }

        /**
         * Gets the ItemOption constant for the input integer option id.
         *
         * @param option the option id.
         * @return the ItemOption constant.
         */
        private static final ItemOption of(final int option) {
            val constant = Utils.findMatching(values, value -> value.optionId == option);
            if (constant == null) {
                throw new IllegalArgumentException("Option cannot be " + option + ".");
            }
            return constant;
        }

        /**
         * Whether the input enum constant is identical to this option-wise, necessary because
         * {@code SkeletonEnum#equals(final Object other)} is final, thus preventing us from overriding it,
         * and because the options {@code CLEAR_ALL} and {@code WITHDRAW_ALL_BUT_1} are identical
         * option id wise.
         *
         * @param other the other constant to compare against.
         * @return whether the constants are identical option id wise.
         */
        public boolean is(final ItemOption other) {
            return other.optionId == optionId;
        }

        /**
         * Gets the amount of the constant variable.
         *
         * @param player the player argument, necessary to get a dynamic amount.
         * @return the respective amount of the constant.
         */
        public int getAmount(final Player player, final int itemAmount) {
            return function.getInt(player, itemAmount);
        }

        /**
         * A primitive-returning generic int function, used to prevent overhead created by autoboxing and unboxing.
         *
         * @param <K> generic parameter.
         */
        @FunctionalInterface
        private interface ParametrizedIntFunction<K> {
            int getInt(final K k, final int itemAmount);
        }
    }
}
