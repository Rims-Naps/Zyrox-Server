package com.zenyte.game.world.entity.player.container.impl.bank;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.skills.thieving.CoinPouch;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.player.Lamp;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.*;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import mgi.types.config.items.ItemDefinitions;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Kris | 31. dets 2017 : 22:58.51
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class Bank {

    private static final int VARBIT_FIRST_OP_AMOUNT = 6590;
    public static final int BANK_INTERFACE = 12;
    public static final int MAIN_TAB = 9;

    public static final Int2ObjectMap<Predicate<Player>> predicateMap = new Int2ObjectOpenHashMap<>();
    public static final Set<Integer> unbankableItems = new HashSet<>();

    static {
        for (val lamp : Lamp.all) {
            unbankableItems.add(lamp.getItem().getId());
        }
        unbankableItems.add(12011);
        unbankableItems.addAll(CoinPouch.ITEMS.keySet());
        unbankableItems.add(ItemId.MIMIC);
        unbankableItems.add(ItemId.GOLOVANOVA_SEED);
        unbankableItems.add(ItemId.GOLOVANOVA_FRUIT);
        unbankableItems.add(ItemId.BOLOGANO_SEED);
        unbankableItems.add(ItemId.BOLOGANO_FRUIT);
        unbankableItems.add(ItemId.LOGAVANO_SEED);
        unbankableItems.add(ItemId.LOGAVANO_FRUIT);
        predicateMap.put(7938, player -> {
            if (player == null) {
                return false;
            }
           player.sendMessage("You can't bank that.");
           return false;
        });
    }

    @Getter
    private transient final Player player;
    int[] tabSizes = new int[10];
    private int settings = 32;
    @Getter @Setter
    private int displayType, currentTab = MAIN_TAB;
    @Getter
    @Setter
    private int lastDepositAmount;
    @Getter
    private BankContainer container;
    @Getter private QuantitySelector quantity;

    public Bank(final Player player) {
        this.player = player;
        container = new BankContainer(this, ContainerPolicy.ALWAYS_STACK, ContainerType.BANK);
        quantity = QuantitySelector.ONE;
    }

    public Bank(final Player player, final Bank bank) {
        this.player = player;
        if (bank == null) {
            return;
        }
        currentTab = bank.currentTab;
        lastDepositAmount = bank.lastDepositAmount;
        settings = bank.settings;
        displayType = bank.displayType;
        container = new BankContainer(this, ContainerPolicy.ALWAYS_STACK, ContainerType.BANK);
        container.setContainer(bank.container);
        container.bank = this;
        tabSizes = bank.tabSizes;
        quantity = bank.quantity == null ? QuantitySelector.ONE : bank.quantity;
    }

    public void setQuantity(final QuantitySelector selector) {
        this.quantity = selector;
        refreshQuantity();
    }

    public void refreshQuantity() {
        player.getVarManager().sendBit(VARBIT_FIRST_OP_AMOUNT, quantity.value);
    }

    public int getCurrentQuantity() {
        switch(quantity) {
            case ONE:
                return 1;
            case FIVE:
                return 5;
            case TEN:
                return 10;
            case ALL:
                return Integer.MAX_VALUE;
            case X:
                return this.getLastDepositAmount();
            default:
                throw new RuntimeException("No available quantity unit for type " + quantity + ".");
        }
    }

    public ContainerResult add(final Item requestedItem) {
        return add(requestedItem, currentTab);
    }

    public ContainerResult add(final Item requestedItem, int tab) {
        tab = validateTab(tab);
        val item = new Item(requestedItem);
        val defs = item.getDefinitions();

        if (defs == null) {
            val result = new ContainerResult(item, ContainerState.ADD);
            result.setResult(RequestResult.FAILURE);
            return result;
        }
        if (defs.isNoted()) {
            item.setId(defs.getNotedId());
        }
        val predicate = predicateMap.get(item.getId());
        if (predicate != null) {
            if (!predicate.test(player)) {
                val result = new ContainerResult(item, ContainerState.ADD);
                result.setResult(RequestResult.FAILURE);
                return result;
            }
        }
        if (unbankableItems.contains(requestedItem.getId())) {
            player.sendMessage("A magical force prevents you from banking this item!");
            val result = new ContainerResult(item, ContainerState.ADD);
            result.setResult(RequestResult.FAILURE);
            return result;
        }
        val defId = defs.getUnnotedOrDefault();
        val placeholderId = ItemDefinitions.getOrThrow(PlaceholderRedirections.builder.getOrDefault(defId, defId)).getPlaceholderId();
        val placeholderSlot = container.getSlotOf(placeholderId);
        if (item.hasAttributes() || container.getSlotOf(item.getId()) == -1 && (placeholderId == -1 || placeholderSlot == -1)) {
            val result = new ContainerResult(item, ContainerState.ADD);
            if (container.getFreeSlotsSize() == 0) {
                if (placeholderSlot != -1 && item.hasAttributes()) {
                    container.set(placeholderSlot, new Item(requestedItem.getId(), 1, requestedItem.getAttributes()));
                    result.setSucceededAmount(1);
                    result.setResult(RequestResult.SUCCESS);
                    return result;
                }
                result.setResult(RequestResult.NOT_ENOUGH_SPACE);
                return result;
            }
            if (placeholderSlot != -1) {
                container.set(placeholderSlot, item);
                result.setSucceededAmount(item.getAmount());
                result.setResult(RequestResult.SUCCESS);
                return result;
            }
            val index = getIndexOfLastSlotInTab(tab);
            shiftForwardPastIndex(index);
            if (container.get(index) != null) {
                result.setResult(RequestResult.FAILURE);
                return result;
            }
            container.set(index, item);
            tabSizes[tab]++;
            result.setSucceededAmount(item.getAmount());
            result.setResult(RequestResult.SUCCESS);
            return result;
        }
        return container.add(item);
    }

    public ContainerResult remove(final Item item) {
        return remove(container.getSlotOf(item.getId()), item, false);
    }

    public ContainerResult remove(final Item item, final boolean placeholder) {
        return remove(container.getSlotOf(item.getId()), item, placeholder);
    }

    public ContainerResult remove(final int slot, final int amount, final boolean placeholder) {
        val item = container.get(slot);
        return remove(slot, new Item(item == null ? -1 : item.getId(), amount), placeholder);
    }

    public ContainerResult remove(final int slot, final Item item, final boolean placeholder) {
        val result = container.removeSpecific(slot, item, placeholder || item.getId() != 20594 && getSetting(BankSetting.ALWAYS_PLACEHOLDER) == 1, player);

        if (slot == -1) {
            return result;
        }
        if (container.get(slot) != null) {
            return result;
        }
        shiftBackwardsPastIndex(slot);
        val tabId = getTab(slot);
        if (--tabSizes[tabId] == 0) {
            if (tabId == MAIN_TAB) {
                collapseTab(MAIN_TAB);
            } else {
                resizeTabs();
            }
        }

        return result;
    }

    public Item get(final int slot) {
        return container.get(slot);
    }

    public void incinerate(final int slotId, final int itemId) {
        if (itemId == -1)
            return;
        set(slotId - 1, null);
    }

    public void set(final int slot, final Item item) {
        if (slot < 0 || slot > container.getContainerSize()) {
            return;
        }

        val tabId = getTab(slot);

        if (tabId >= 0 && tabId < 10) {
            if (item == null && container.get(slot) != null) {
                tabSizes[tabId]--;
                if (tabSizes[tabId] == 0) {
                    collapseTab(tabId);
                }
            } else if (item != null && container.get(slot) == null) {
                tabSizes[tabId]++;
            }
        }

        container.set(slot, item);
        container.setFullUpdate(true);
        container.shift();
        container.refresh(player);
    }

    public boolean containsItem(final Item item) {
        return container.contains(item);
    }

    public int getAmountOf(final int id) {
        return container.getAmountOf(id);
    }

    public void deposit(final Player player, final Container container, final int slotId, final int amount) {
        this.container.deposit(player, container, slotId, amount);
        this.container.refresh(this.player);
        container.refresh(this.player);
    }

    public void withdraw(final Player player, final Container container, final int slot, final int amount, final boolean placeholder) {
        this.container.withdraw(player, container, slot, amount, placeholder);
        this.container.refresh(this.player);
        container.refresh(this.player);
    }

    public void switchItem(final BankSwitchType type, final int fromSlot, int toSlot) {
        if (type.equals(BankSwitchType.ITEM_TO_ITEM)) {
            if (toSlot >= this.container.getSize()) {
                toSlot = this.container.getSize() - 1;
            }
            val fromItem = container.get(fromSlot);
            val toItem = container.get(toSlot);
            if (getSetting(BankSetting.REARRANGE_MODE) == 0) {
                container.set(fromSlot, toItem);
                container.set(toSlot, fromItem);
            } else {
                val fromTab = getTab(fromSlot);
                val toTab = getTab(toSlot);
                if (fromTab == toTab) {
                    if (fromSlot < toSlot) {
                        for (int i = fromSlot + 1; i <= toSlot; i++) {
                            container.set(i - 1, container.get(i));
                        }
                        container.set(toSlot, fromItem);
                    } else {
                        for (int i = fromSlot - 1; i >= toSlot; i--) {
                            container.set(i + 1, container.get(i));
                        }
                        container.set(toSlot, fromItem);
                    }
                } else {
                    if (fromSlot > toSlot) {
                        for (int i = fromSlot - 1; i >= toSlot; i--) {
                            container.set(i + 1, container.get(i));
                        }
                        container.set(toSlot, fromItem);
                    } else {
                        for (int i = fromSlot + 1; i < toSlot; i++) {
                            container.set(i - 1, container.get(i));
                        }
                        container.set(toSlot - 1, fromItem);
                    }
                    tabSizes[toTab]++;
                    if (--tabSizes[fromTab] == 0) {
                        resizeTabs();
                    }
                }
            }
            container.refresh(player);
        } else if (type.equals(BankSwitchType.ITEM_TO_TAB) || type.equals(BankSwitchType.ITEM_TO_END_OF_TAB)) {
            val fromTab = getTab(fromSlot);
            int toTab = type.equals(BankSwitchType.ITEM_TO_END_OF_TAB) ? toSlot : getTabFromSlot(toSlot);

            if (fromTab == toTab && tabSizes[fromTab] == 1) {
                return;
            }

            val fromItem = container.get(fromSlot);
            if (fromItem == null) {
                return;
            }
            container.set(fromSlot, null);
            container.shift();
            if (--tabSizes[fromTab] == 0) {
                collapseTab(fromTab);
                if (fromTab < toTab && toTab != MAIN_TAB || fromTab == MAIN_TAB) {
                    if (--toTab == -1) {
                        toTab = 9;
                    }
                }

            }
            this.add(fromItem, toTab);
            container.setFullUpdate(true);
            container.refresh(player);
        } else if (type.equals(BankSwitchType.TAB_TO_TAB)) {
            final int fromTab = getTabFromSlot(fromSlot);
            int toTab = getTabFromSlot(toSlot);
            if (tabSizes[toTab] == 0) {
                for (int i = 0; i < 10; i++) {
                    if (tabSizes[i] == 0) {
                        toTab = i - 1;
                        if (toTab < 0) {
                            toTab = 0;
                        }
                        break;
                    }
                }
            }
            if (toTab == fromTab) {
                return;
            }

            val list = new ArrayList<Item>(container.getSize());
            int i, fromStartIndex, toStartIndex;
            val tabs = new int[10];
            if (fromTab < toTab) {
                if (toTab == MAIN_TAB) {
                    fromStartIndex = getIndexOfFirstSlotInTab(fromTab);
                    toStartIndex = getIndexOfFirstSlotInTab(toTab);
                    for (i = toStartIndex; i < toStartIndex + tabSizes[toTab]; i++) {
                        list.add(container.get(i));
                    }
                    for (i = 0; i < fromStartIndex; i++) {
                        list.add(container.get(i));
                    }
                    for (i = fromStartIndex + tabSizes[fromTab]; i < toStartIndex; i++) {
                        list.add(container.get(i));
                    }
                    for (i = fromStartIndex; i < fromStartIndex + tabSizes[fromTab]; i++) {
                        list.add(container.get(i));
                    }
                    int tabIndex = 0;
                    tabs[tabIndex++] = tabSizes[MAIN_TAB];
                    for (i = 0; i < fromTab; i++) {
                        tabs[tabIndex++] = tabSizes[i];
                    }
                    for (i = fromTab + 1; i < 9; i++) {
                        tabs[tabIndex++] = tabSizes[i];
                    }
                    tabs[tabIndex++] = tabSizes[fromTab];
                } else {
                    fromStartIndex = getIndexOfFirstSlotInTab(fromTab);
                    toStartIndex = getIndexOfFirstSlotInTab(toTab + 1);
                    for (i = 0; i < fromStartIndex; i++) {
                        list.add(container.get(i));
                    }
                    for (i = (i + tabSizes[fromTab]); i < toStartIndex; i++) {
                        list.add(container.get(i));
                    }
                    for (i = fromStartIndex; i < fromStartIndex + tabSizes[fromTab]; i++) {
                        list.add(container.get(i));
                    }
                    for (i = toStartIndex; i < container.getContainerSize(); i++) {
                        val item = container.get(i);
                        if (item == null) {
                            continue;
                        }
                        list.add(item);
                    }
                    int tabIndex = 0;
                    for (i = 0; i < fromTab; i++) {
                        tabs[tabIndex++] = tabSizes[i];
                    }
                    for (i = fromTab + 1; i < (toTab + 1); i++) {
                        tabs[tabIndex++] = tabSizes[i];
                    }
                    tabs[tabIndex++] = tabSizes[fromTab];
                    for (i = toTab + 1; i < 10; i++) {
                        tabs[tabIndex++] = tabSizes[i];
                    }
                }
            } else {
                fromStartIndex = getIndexOfFirstSlotInTab(toTab);
                toStartIndex = getIndexOfFirstSlotInTab(fromTab);
                for (i = 0; i < fromStartIndex; i++) {
                    list.add(container.get(i));
                }
                for (i = toStartIndex; i < toStartIndex + tabSizes[fromTab]; i++) {
                    list.add(container.get(i));
                }

                for (i = fromStartIndex; i < toStartIndex; i++) {
                    list.add(container.get(i));
                }
                for (i = getIndexOfFirstSlotInTab(fromTab + 1); i < container.getContainerSize(); i++) {
                    list.add(container.get(i));
                }
                int tabIndex = 0;
                for (i = 0; i < toTab; i++) {
                    tabs[tabIndex++] = tabSizes[i];
                }
                tabs[tabIndex++] = tabSizes[fromTab];
                for (i = toTab; i < fromTab; i++) {
                    tabs[tabIndex++] = tabSizes[i];
                }
                for (i = fromTab + 1; i < 10; i++) {
                    tabs[tabIndex++] = tabSizes[i];
                }
            }
            container.clear();
            tabSizes = tabs;
            val length = list.size();
            for (i = 0; i < length; i++) {
                container.set(i, list.get(i));
            }
            container.refresh(player);
        }
    }

        public enum BankSwitchType {
            ITEM_TO_ITEM,
            ITEM_TO_TAB,
            TAB_TO_TAB,
            ITEM_TO_END_OF_TAB
        }

    public void collapseTab(final int tab) {
        if (tab < 0 || tab > MAIN_TAB) {
            return;
        }
        if (tab == MAIN_TAB) {
            val mainTab = new ArrayList<Item>(tabSizes[0]);

            for (int i = 0; i < tabSizes[0]; i++) {
                mainTab.add(container.get(i));
                container.set(i, null);
            }

            container.shift();

            int total = 0;
            for (int i = 0; i < 9; i++) {
                tabSizes[i] = tabSizes[i + 1];
                total += tabSizes[i];

            }
            tabSizes[MAIN_TAB] = mainTab.size();
            for (int i = total; i < (total + tabSizes[MAIN_TAB]); i++) {
                container.set(i, mainTab.get(i - total));
            }
            return;
        }

        val list = new ArrayList<Item>(tabSizes[tab]);

        val index = getIndexOfFirstSlotInTab(tab);

        val length = (index + tabSizes[tab]);

        for (int i = index; i < length; i++) {
            list.add(container.get(i));
            container.set(i, null);
        }

        tabSizes[tab] = 0;
        resizeTabs();
        tabSizes[MAIN_TAB] += list.size();

        container.shift();

        val startIndex = container.getSize();
        val size = list.size();

        for (int i = startIndex; i < (startIndex + size); i++) {
            container.set(i, list.remove(0));
        }
        container.refresh(player);
    }

    public int getTabSize(final int tabId) {
        if (tabId < 0 || tabId >= tabSizes.length) {
            return 0;
        }
        return tabSizes[tabId];
    }

    public void refreshContainer() {
        container.refresh(player);
    }

    public void releasePlaceholders(final int tab) {
        val index = tab == -1 ? 0 : getIndexOfFirstSlotInTab(tab);
        val length = tab == -1 ? container.getContainerSize() : (tabSizes[tab] + index);
        for (int i = index; i < length; i++) {
            val item = container.get(i);
            if (item == null) {
                continue;
            }
            val defs = item.getDefinitions();
            if (defs == null) {
                continue;
            }
            if (defs.isPlaceholder()) {
                this.remove(i--, 1, false);
            }
        }
        refreshBankSizeInConfiguration();
        container.refresh(player);
    }

    private void refreshBankSizeInConfiguration() {
        GameInterface.BANK.getPlugin().ifPresent(bankInterface ->
                player.getPacketDispatcher().sendComponentText(bankInterface.getInterface(),
                        bankInterface.getComponent("Bank size in configuration menu"), container.getSize()));
    }

    public void revalidate() {
        val slots = container.getAvailableSlots();
        if (slots.isEmpty()) {
            return;
        }
        int tabsSize = 0;
        for (val tab : tabSizes) {
            tabsSize += tab;
        }
        val length = container.getSize();
        if (tabsSize > length) {
            loop : for (int i = 9; i >= 0; i--) {
                while(tabSizes[i] > 0) {
                    --tabSizes[i];
                    if (--tabsSize <= length) {
                        break loop;
                    }
                }
            }
        }
        if (slots.firstInt() != length) {
            val map = new Int2ObjectAVLTreeMap<Item>(container.getItems());
            container.getItems().clear();
            container.setWeight(0);
            val availableSlots = container.getAvailableSlots();
            val modifiedSlots = container.getModifiedSlots();
            for (int i = 0; i < container.getContainerSize(); i++) {
                availableSlots.add(i);
                modifiedSlots.add(i);
            }
            int lastSlot = 0;
            for (val entry : map.int2ObjectEntrySet()) {
                container.set(lastSlot++, entry.getValue());
            }
        }
    }

    /**
     * Toggles the bank setting to specified value.
     *
     * @param setting
     * @param on
     */
    public void toggleSetting(final BankSetting setting, final boolean on) {
        final int val = getSetting(setting);
        if (val == 1 && !on) {
            settings &= (~(1 << (setting.ordinal() + 1)));
        } else if (val == 0 && on) {
            settings |= 1 << (setting.ordinal() + 1);
        }
        setting.updateVar(player);
    }

    /**
     * Gets the value of the setting, either 1 (true) or 0 (false)
     *
     * @param setting
     * @return
     */
    public int getSetting(final BankSetting setting) {
        return (settings >> (setting.ordinal() + 1)) & 1;
    }

    public void resetBank() {
        container.clear();
        container.refresh(player);
        this.currentTab = MAIN_TAB;
    }

    public void addFillers(int maxAmount) {
        maxAmount = Math.min(816, maxAmount);
        val filler = new Item(20594, 1, new HashMap<>());
        int count = 0;
        while (maxAmount-- > 0 && container.getFreeSlotsSize() != 0) {
            if (++count > 816) {
                break;
            }
            this.add(filler, currentTab);
        }
        container.refresh(player);
        refreshBankSizeInConfiguration();
    }

    public void removeFillers(final int slot, int amount) {
        if (slot == -1) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                val item = container.get(i);
                if (item == null || item.getId() != 20594) {
                    continue;
                }
                this.remove(i--, 1, false);
                if (--amount <= 0) {
                    break;
                }
            }
        } else {
            if (container.get(slot).getId() != 20594) {
                return;
            }
            container.remove(slot, 1);
            shiftBackwardsPastIndex(slot);
            val tabId = getTab(slot);
            if (--tabSizes[tabId] == 0) {
                if (tabId == MAIN_TAB) {
                    collapseTab(MAIN_TAB);
                } else {
                    resizeTabs();
                }
            }
        }
        container.refresh(player);
    }

    public int getTabFromSlot(final int slotId) {
        int tab = slotId - 11;
        if (tab == -1) {
            tab = 9;
        }
        return tab;
    }

    private int validateTab(final int tab) {
        if (tab < 0 || tab > 9) {
            return MAIN_TAB;
        }
        if (tab == 9 || tabSizes[9] == 0) {
            return MAIN_TAB;
        }
        for (int i = 0; i < tab; i++) {
            if (tabSizes[i] == 0) {
                return i;
            }
        }
        return tab;
    }

    private void resizeTabs() {
        val sizes = new int[10];
        int index = 0;
        for (int i = 0; i < 9; i++) {
            val length = tabSizes[i];
            if (length == 0) {
                continue;
            }
            sizes[index++] = length;
        }
        sizes[MAIN_TAB] = tabSizes[MAIN_TAB];
        tabSizes = sizes;
        container.setFullUpdate(true);
    }

    private int getIndexOfFirstSlotInTab(final int tabId) {
        int index = 0;
        for (int i = 0; i < tabId; i++) {
            index += tabSizes[i];
        }
        return index;
    }

    private int getIndexOfLastSlotInTab(final int tab) {
        int index = 0;
        for (int i = 0; i <= tab; i++) {
            index += tabSizes[i];
        }
        return index;
    }

    private void shiftForwardPastIndex(final int index) {
        for (int slot = container.getContainerSize() - 1; slot >= 0; slot--) {
            if (slot > index) {
                final Item existingItem = slot == 0 ? null : container.get(slot - 1);
                container.set(slot, existingItem);
                if (slot > 0) {
                    container.set(slot - 1, null);
                }
            }
        }
    }

    private void shiftBackwardsPastIndex(final int index) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (slot > index) {
                val existingItem = container.get(slot);
                if (slot > 0) {
                    container.set(slot - 1, existingItem);
                }
                container.set(slot, null);
            }
        }
    }

    private int getTab(final int slot) {
        if (slot == -1) {
            return currentTab;
        }
        int size = 0;
        for (int i = 0; i < tabSizes.length; i++) {
            val tab = tabSizes[i];
            size += tab;
            if (size > slot) {
                return i;
            }
        }
        return currentTab;
    }

    public void refreshBankSizes(@NotNull final Player player) {
        player.getVarManager().sendBit(4170, displayType);
        for (int i = 0; i < 9; i++) {
            player.getVarManager().sendBit(4171 + i, tabSizes[i]);
        }
    }

    void refreshTabSizes() {
        refreshBankSizes(player);
    }

    public boolean hasFreeSlots() {
        return container.getFreeSlotsSize() > 0;
    }

    public boolean checkSpace() {
        if (!hasFreeSlots()) {
            player.sendMessage("Not enough space in your bank.");
            return false;
        }
        return true;
    }

    @AllArgsConstructor
    public enum QuantitySelector {
        ONE(0),
        FIVE(1),
        TEN(2),
        X(3),
        ALL(4);

        private final int value;
    }

}
