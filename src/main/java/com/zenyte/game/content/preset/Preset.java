package com.zenyte.game.content.preset;

import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.UnmodifiableItem;
import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.game.parser.impl.ItemRequirements;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.GameMode;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.impl.bank.Bank;
import com.zenyte.game.world.entity.player.container.impl.bank.BankSetting;
import com.zenyte.game.world.region.area.plugins.EquipmentPlugin;
import com.zenyte.plugins.equipment.equip.EquipPluginLoader;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;

import static com.zenyte.game.content.preset.PresetLoadResponse.*;

/**
 * @author Kris | 16/09/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@SuppressWarnings("DuplicatedCode")
@Getter
@Slf4j
public class Preset {

    @NotNull
    public static final MemberRank SPELLBOOK_MINIMUM_MEMBER_RANK = MemberRank.RUBY_MEMBER;

    /**
     * The name of the preset.
     */
    @NotNull private String name;
    @Nullable @Getter @Setter private Spellbook spellbook;
    @NotNull private final Map<Integer, UnmodifiableItem> inventory;
    @NotNull private final Map<Integer, UnmodifiableItem> equipment;
    @Nullable private final Map<Integer, UnmodifiableItem> runePouch;
    @Getter @Setter private transient boolean available;

    public Preset(@NotNull final String name, @NotNull final Player player) {
        this.name = name;
        this.inventory = defineCollection(player.getInventory().getContainer());
        this.equipment = Int2ObjectMaps.unmodifiable(defineCollection(player.getEquipment().getContainer()));
        this.runePouch = !player.getInventory().containsItem(ItemId.RUNE_POUCH) ? null : Int2ObjectMaps.unmodifiable(defineCollection(player.getRunePouch().getContainer()));
        val memberRank = player.getMemberRank();
        spellbook = !memberRank.eligibleTo(SPELLBOOK_MINIMUM_MEMBER_RANK) ? null : player.getCombatDefinitions().getSpellbook();
    }

    public Preset(@NotNull final Preset other) {
        this.name = other.name;
        this.spellbook = other.spellbook;
        this.inventory = defineCollectionCopy(other.inventory);
        this.equipment = Int2ObjectMaps.unmodifiable(defineCollectionCopy(other.equipment));
        this.runePouch = other.runePouch == null ? null : Int2ObjectMaps.unmodifiable(defineCollectionCopy(other.runePouch));
    }

    /**
     * Builds a slot map based on the contents of the container passed to it.
     * @param container the container passed to it.
     * @return a slot map of the container.
     */
    @NotNull
    private Int2ObjectMap<UnmodifiableItem> defineCollection(@NotNull final Container container) {
        val collection = new Int2ObjectOpenHashMap<UnmodifiableItem>();
        for (val entry : container.getItems().int2ObjectEntrySet()) {
            collection.put(entry.getIntKey(), new UnmodifiableItem(entry.getValue()));
        }
        return collection;
    }

    @NotNull
    private Int2ObjectMap<UnmodifiableItem> defineCollectionCopy(@NotNull final Map<Integer, UnmodifiableItem> container) {
        val collection = new Int2ObjectOpenHashMap<UnmodifiableItem>();
        for (val entry : container.entrySet()) {
            collection.put(entry.getKey().intValue(), new UnmodifiableItem(entry.getValue()));
        }
        return collection;
    }

    public void rename(@NotNull final String name) {
        this.name = name;
    }

    public void switchInventoryItem(final int fromSlot, final int toSlot) {
        val fromItem = inventory.get(fromSlot);
        val toItem = inventory.get(toSlot);
        if (fromItem == null) {
            return;
        }
        inventory.put(fromSlot, toItem);
        inventory.put(toSlot, fromItem);
    }

    public void load(@NotNull final Player player) {
        if (player.getGameMode() == GameMode.ULTIMATE_IRON_MAN) {
            throw new IllegalStateException();
        }
        val nano = System.nanoTime();
        val memberRank = player.getMemberRank();
        if (spellbook != null && memberRank.eligibleTo(SPELLBOOK_MINIMUM_MEMBER_RANK)) {
            player.getCombatDefinitions().setSpellbook(spellbook, true);
        }
        val builder = new StringBuilder(8192);
        builder.append("Loading following preset:").append("\n").append(toString());
        builder.append("Containers before banking:\n");
        builder.append("Inventory: ").append(player.getInventory().getContainer().getItems()).append("\n");
        builder.append("Equipment: ").append(player.getEquipment().getContainer().getItems()).append("\n");
        if (runePouch != null) {
            builder.append("Rune pouch: ").append(player.getRunePouch().getContainer().getItems()).append("\n");
        }
        val depositedAllInventory = depositContainer(player, player.getInventory().getContainer());
        val depositedAllEquipment = depositContainer(player, player.getEquipment().getContainer());
        val depositedAllRunes = runePouch == null || depositContainer(player, player.getRunePouch().getContainer());
        player.sendMessage("Preset " + Colour.RS_PURPLE.wrap(name) + " has been loaded.");
        if (!depositedAllInventory || !depositedAllEquipment || !depositedAllRunes) {
            player.sendFilteredMessage(Colour.YELLOW.wrap("Some items could not be banked."));
            builder.append("Containers after incomplete banking:\n");
            builder.append("Inventory: ").append(player.getInventory().getContainer().getItems()).append("\n");
            builder.append("Equipment: ").append(player.getEquipment().getContainer().getItems()).append("\n");
            if (runePouch != null) {
                builder.append("Rune pouch: ").append(player.getRunePouch().getContainer().getItems()).append("\n");
            }
        }
        val inventoryResponse = loadInventory(player);
        val equipmentResponse = loadEquipment(player);
        val runePouchResponse = loadRunePouch(player, this.runePouch != null && player.getInventory().containsItem(ItemId.RUNE_POUCH));
        player.getInventory().refreshAll();
        player.getEquipment().refreshAll();
        player.getAppearance().resetRenderAnimation();
        val runePouchContainer = player.getRunePouch().getContainer();
        runePouchContainer.setFullUpdate(true);
        runePouchContainer.refresh(player);
        val combatDefinitions = player.getCombatDefinitions();
        combatDefinitions.setAutocastSpell(null);
        combatDefinitions.refresh();

        player.sendFilteredMessage("Inventory was loaded " + inventoryResponse.getResponse() + ".");
        player.sendFilteredMessage("Equipment was loaded " + equipmentResponse.getResponse() + ".");
        if (runePouch != null) {
            if (runePouchResponse == null) {
                player.sendFilteredMessage("Rune pouch " + Colour.RS_RED.wrap("could not be located") + ".");
            } else {
                player.sendFilteredMessage("Rune pouch was loaded " + runePouchResponse.getResponse() + ".");
            }
        }
        builder.append("Containers after loading preset:\n");
        builder.append("Inventory: ").append(player.getInventory().getContainer().getItems()).append("\n");
        builder.append("Equipment: ").append(player.getEquipment().getContainer().getItems());
        if (runePouch != null) {
            builder.append("\n").append("Rune pouch: ").append(player.getRunePouch().getContainer().getItems());
        }
        builder.append("Preset load time: ").append(System.nanoTime() - nano).append("ns.");
        player.log(LogLevel.INFO, builder.toString());
    }

    boolean depositContainer(@NotNull final Player player, @NotNull final Container container) {
        if (container.getSize() == 0) {
            return true;
        }
        val isEquipment = player.getEquipment().getContainer() == container;
        val bank = player.getBank();
        val length = container.getContainerSize();
        for (int slot = 0; slot < length; slot++) {
            val item = container.get(slot);
            if (item == null) {
                continue;
            }
            bank.deposit(null, container, slot, item.getAmount());
            if (isEquipment) {
                //If the item has been successfully deposited
                if (container.get(slot) == null) {
                    val plugin = EquipPluginLoader.PLUGINS.get(item.getId());
                    if (plugin != null) {
                        try {
                            plugin.onUnequip(player, container, item);
                        } catch (Exception e) {
                            log.error(Strings.EMPTY, e);
                        }
                    }
                }
            }
        }
        return container.getSize() == 0;
    }

    PresetLoadResponse loadInventory(@NotNull final Player player) {
        val inventory = player.getInventory();
        val inventoryContainer = inventory.getContainer();
        val size = inventoryContainer.getContainerSize();
        val bank = player.getBank();
        val alwaysSetPlaceholders = bank.getSetting(BankSetting.ALWAYS_PLACEHOLDER) == 1;
        var loadResponse = FLAWLESS_LOAD;
        for (int i = 0; i < size; i++) {
            val presetItem = ((Int2ObjectMap<UnmodifiableItem>) this.inventory).get(i);
            //If no preset item is set in that slot, ignore it entirely, even if it remained filled from failing to offload the items into the bank.
            if (presetItem == null) {
                continue;
            }
            val presetItemId = presetItem.getId();
            val existingItem = inventory.getItem(i);
            //If the inventory slot is free, let's try to set the item.
            if (existingItem == null) {
                val slot = findSlotOfBestMatchingItem(bank, presetItem, true);
                if (slot.isPresent()) {
                    val slotId = slot.getAsInt();
                    val itemInBank = bank.get(slotId);
                    val bankItemId = itemInBank.getId();
                    val finalInventoryId = presetItem.getDefinitions().isNoted() ? ItemDefinitions.getOrThrow(bankItemId).getNotedOrDefault() : bankItemId;
                    if (ItemDefinitions.getOrThrow(finalInventoryId).isStackable()) {
                        val existingInvSlot = inventoryContainer.getSlotOf(finalInventoryId);
                        if (existingInvSlot != -1 && existingInvSlot != i) {
                            continue;
                        }
                    }

                    if (loadResponse == FLAWLESS_LOAD && bankItemId != presetItemId && presetItemId != ItemDefinitions.getOrThrow(bankItemId).getNotedOrDefault()) {
                        loadResponse = ALTERNATE_LOAD;
                    }
                    val succeededCount =
                            bank.remove(slotId, new Item(itemInBank.getId(), Math.min(itemInBank.getAmount(), presetItem.getAmount()), itemInBank.getAttributes()),
                                    alwaysSetPlaceholders).getSucceededAmount();
                    if (succeededCount > 0) {
                        inventoryContainer.set(i, new Item(finalInventoryId, succeededCount, itemInBank.getAttributesCopy()));
                    }
                } else {
                    loadResponse = PresetLoadResponse.INCOMPLETE_LOAD;
                }
            } else if (existingItem.isStackable() && presetItemId == existingItem.getId()) {
                val presetItemAmount = presetItem.getAmount();
                //If there's an item already in this slot that's stackable and of the same id, let's try to "top it up" if applicable.
                if (existingItem.getAmount() < presetItemAmount) {
                    val unnotedId = ItemDefinitions.getOrThrow(presetItemId).getUnnotedOrDefault();
                    val amountInBank = bank.getAmountOf(unnotedId);
                    if (amountInBank > 0) {
                        val amountRequired = presetItemAmount - existingItem.getAmount();
                        val amountToAdd = Math.min(amountInBank, amountRequired);
                        val succeededCount = bank.remove(new Item(unnotedId, amountToAdd), alwaysSetPlaceholders).getSucceededAmount();
                        if (amountRequired > succeededCount) {
                            loadResponse = PresetLoadResponse.INCOMPLETE_LOAD;
                        }
                        if (succeededCount > 0) {
                            val finalInventoryId = presetItem.getDefinitions().isNoted() ? ItemDefinitions.getOrThrow(presetItemId).getNotedOrDefault() : presetItemId;
                            inventoryContainer.set(i, new Item(finalInventoryId, succeededCount + existingItem.getAmount()));
                        }
                    } else {
                        loadResponse = PresetLoadResponse.INCOMPLETE_LOAD;
                    }
                }
            } else {
                if (!Objects.equals(existingItem, presetItem)) {
                    loadResponse = INCOMPLETE_LOAD;
                }
            }
        }
        return loadResponse;
    }

    PresetLoadResponse loadEquipment(@NotNull final Player player) {
        val equipment = player.getEquipment();
        val equipmentContainer = equipment.getContainer();
        val size = equipmentContainer.getContainerSize();
        val bank = player.getBank();
        val alwaysSetPlaceholders = bank.getSetting(BankSetting.ALWAYS_PLACEHOLDER) == 1;
        var loadResponse = FLAWLESS_LOAD;
        val area = player.getArea();
        for (int i = 0; i < size; i++) {
            val presetItem = ((Int2ObjectMap<UnmodifiableItem>) this.equipment).get(i);
            //If no preset item is set in that slot, ignore it entirely, even if it remained filled from failing to offload the items into the bank.
            if (presetItem == null) {
                continue;
            }
            val presetItemId = presetItem.getId();
            val existingItem = equipment.getItem(i);
            //If the equipment slot is free, let's try to set the item.
            if (existingItem == null) {
                val slot = findSlotOfBestMatchingItem(bank, presetItem, false);
                if (slot.isPresent()) {
                    val slotId = slot.getAsInt();
                    val itemInBank = bank.get(slotId);
                    val bankItemId = itemInBank.getId();
                    if ((area instanceof EquipmentPlugin && !((EquipmentPlugin) area).equip(player, itemInBank, i)) || !mayEquip(player, itemInBank)) {
                        loadResponse = PresetLoadResponse.INCOMPLETE_LOAD;
                        continue;
                    }
                    if (loadResponse == FLAWLESS_LOAD && bankItemId != presetItemId) {
                        loadResponse = ALTERNATE_LOAD;
                    }
                    val succeededCount =
                            bank.remove(slotId, new Item(itemInBank.getId(), Math.min(itemInBank.getAmount(), presetItem.getAmount()), itemInBank.getAttributes()),
                                    alwaysSetPlaceholders).getSucceededAmount();
                    if (succeededCount > 0) {
                        val it = new Item(bankItemId, succeededCount, itemInBank.getAttributesCopy());
                        equipmentContainer.set(i, it);
                        val plugin = EquipPluginLoader.PLUGINS.get(bankItemId);
                        if (plugin != null) {
                            try {
                                plugin.onEquip(player, equipmentContainer, it);
                            } catch (Exception e) {
                                log.error(Strings.EMPTY, e);
                            }
                        }
                    }
                } else {
                    loadResponse = INCOMPLETE_LOAD;
                }
            } else if (existingItem.isStackable() && presetItemId == existingItem.getId()) {
                val presetItemAmount = presetItem.getAmount();
                //If there's an item already in this slot that's stackable and of the same id, let's try to "top it up" if applicable.
                if (existingItem.getAmount() < presetItemAmount) {
                    val amountInBank = bank.getAmountOf(presetItemId);
                    if (amountInBank > 0) {
                        val amountRequired = presetItemAmount - existingItem.getAmount();
                        val amountToAdd = Math.min(amountInBank, amountRequired);
                        val succeededCount = bank.remove(new Item(presetItemId, amountToAdd), alwaysSetPlaceholders).getSucceededAmount();
                        if (amountRequired > succeededCount) {
                            loadResponse = PresetLoadResponse.INCOMPLETE_LOAD;
                        }
                        if (succeededCount > 0) {
                            equipmentContainer.set(i, new Item(presetItemId, succeededCount + existingItem.getAmount()));
                        }
                    }
                }
            } else {
                if (!Objects.equals(existingItem, presetItem)) {
                    loadResponse = INCOMPLETE_LOAD;
                }
            }
        }
        return loadResponse;
    }

    boolean mayEquip(@NotNull final Player player, @NotNull final Item item) {
        val definitions = item.getDefinitions();
        val requirement = definitions.getRequirements();
        val requirements = requirement == null ? null : requirement.getRequirements();
        if (requirements != null) {
            if (!requirements.isEmpty()) {
                val skills = player.getSkills();
                if (requirements.size() >= 10) {
                    if (!skills.isMaxed()) {
                        return false;
                    }
                }
                val it = requirements.iterator();
                ItemRequirements.ItemRequirement.PrimitiveRequirement entry;
                var canEquip = true;
                int key, value;
                while (it.hasNext()) {
                    entry = it.next();
                    key = entry.getSkill();
                    value = entry.getLevel();
                    if (skills.getLevelForXp(key) < value) {
                        canEquip = false;
                        break;
                    }
                }
                return canEquip;
            }
        }
        return true;
    }

    PresetLoadResponse loadRunePouch(@NotNull final Player player, final boolean containsRunePouch) {
        if (this.runePouch == null || this.runePouch.isEmpty()) {
            return FLAWLESS_LOAD;
        }
        if (!containsRunePouch) {
            return null;
        }
        val container = player.getRunePouch().getContainer();
        val size = container.getContainerSize();
        val bank = player.getBank();
        val alwaysSetPlaceholders = bank.getSetting(BankSetting.ALWAYS_PLACEHOLDER) == 1;
        var loadResponse = FLAWLESS_LOAD;
        for (int i = 0; i < size; i++) {
            val presetItem = ((Int2ObjectMap<UnmodifiableItem>) this.runePouch).get(i);
            if (presetItem == null) {
                continue;
            }
            val presetItemId = presetItem.getId();
            val presetItemAmount = presetItem.getAmount();
            val pouch = player.getRunePouch().getContainer();
            val existingItem = pouch.get(i);
            val existingSlot = pouch.getSlotOf(presetItemId);
            //Don't allow putting the same rune in a different slot.
            if (existingSlot != -1 && existingSlot != i) {
                continue;
            }
            if (existingItem == null || existingItem.getId() == presetItemId && existingItem.getAmount() < presetItemAmount) {
                val amountInBank = bank.getAmountOf(presetItemId);
                if (amountInBank > 0) {
                    val amountToAdd = Math.min(amountInBank, presetItemAmount - (existingItem == null ? 0 : existingItem.getAmount()));
                    val succeededCount = bank.remove(new Item(presetItemId, amountToAdd), alwaysSetPlaceholders).getSucceededAmount();
                    if (succeededCount > 0) {
                        container.set(i, new Item(presetItemId, succeededCount + (existingItem == null ? 0 : existingItem.getAmount())));
                    }
                }
                val finalItem = container.get(i);
                if (finalItem == null || finalItem.getId() != presetItemId) {
                    loadResponse = PresetLoadResponse.INCOMPLETE_LOAD;
                } else if (finalItem.getAmount() != presetItem.getAmount()) {
                    if (loadResponse == FLAWLESS_LOAD) {
                        loadResponse = ALTERNATE_LOAD;
                    }
                }
            }
        }
        return loadResponse;
    }

    @NotNull
    OptionalInt findSlotOfBestMatchingItem(@NotNull final Bank bank, @NotNull final Item item, final boolean checkNotes) {
        int bestSlot = -1;
        int bestItemCharges = 0;
        Item bestItem = null;
        val id = checkNotes ? item.getDefinitions().getUnnotedOrDefault() : item.getId();
        val attributes = item.getAttributes();
        var possibleIds = PresetSubstitute.findSubstitutes(id);
        if (possibleIds == null) {
            possibleIds = DegradableItem.findSubstitutes(id);
        }
        var charges = possibleIds != null && id == possibleIds[0] ? Math.max(item.getCharges(), DegradableItem.getFullCharges(id)) : item.getCharges();
        if (charges == 0) {
            val substituteCharges = PresetSubstitute.getCharges(id);
            if (substituteCharges != 0) {
                charges = substituteCharges;
            }
        }
        val onlyCharges = charges == 0 ? (attributes == null) : (attributes == null || attributes.size() <= 1);
        for (val entry : bank.getContainer().getItems().int2ObjectEntrySet()) {
            val entrySlot = entry.getIntKey();
            val entryItem = entry.getValue();
            if (entryItem == null) {
                continue;
            }
            val entryItemId = entryItem.getId();
            if (entryItemId == ItemId.BANK_FILLER) {
                continue;
            }
            boolean forceSelect = false;
            if (id == ItemId.TOXIC_BLOWPIPE && entryItemId == ItemId.TOXIC_BLOWPIPE) {
                forceSelect = true;
            }
            if (entryItemId == id || (possibleIds != null && ArrayUtils.contains(possibleIds, entryItemId))) {
                //If we have found the perfect match, just drop the rest.
                if ((attributes == null && charges == 0) || attributes != null && attributes.equals(entryItem.getAttributes()) || forceSelect) {
                    return OptionalInt.of(entrySlot);
                }
                //If the item has attributes associated with it that aren't charges, we can't realistically predict what the person would want, so skip.
                if (!onlyCharges) {
                    continue;
                }
                var entryItemCharges = possibleIds != null && entryItemId == possibleIds[0] ?
                        Math.max(entryItem.getCharges(), DegradableItem.getFullCharges(entryItemId)) : entryItem.getCharges();
                if (entryItemCharges == 0) {
                    val substituteCharges = PresetSubstitute.getCharges(entryItemId);
                    if (substituteCharges != 0) {
                        entryItemCharges = substituteCharges;
                    }
                }
                if (bestItem == null || Math.abs(bestItemCharges - charges) > Math.abs(entryItemCharges - charges)) {
                    bestItem = entryItem;
                    bestSlot = entrySlot;
                    bestItemCharges = entryItemCharges;
                }
            }
        }
        return bestSlot == -1 ? OptionalInt.empty() : OptionalInt.of(bestSlot);
    }

    @Override
    public String toString() {
        return "Preset name: " + name + "\n" +
                "Preset spellbook: " + (spellbook == null ? "Not selected" : spellbook.toString()) + "\n" +
                "Preset inventory: " + inventory + "\n" +
                "Preset equipment: " + equipment + "\n" +
                "Preset rune pouch: " + (runePouch == null ? "Not included" : runePouch.toString()) + "\n";
    }
}
