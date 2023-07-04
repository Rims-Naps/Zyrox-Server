package com.zenyte.game.ui.testinterfaces;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.Subscribe;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.preset.Preset;
import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.SwitchPlugin;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Examine;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.game.world.entity.player.container.impl.Inventory;
import com.zenyte.game.world.entity.player.container.impl.bank.BankSetting;
import com.zenyte.game.world.entity.player.container.impl.equipment.Equipment;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.events.InventoryItemSwitchEvent;
import lombok.val;

import mgi.types.config.items.ItemDefinitions;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * @author Tommeh | 27/05/2019 | 14:38
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@SuppressWarnings("DuplicatedCode")
public class PresetManagerInterface extends Interface implements SwitchPlugin {

    private static final int HIGHLIGHT_SPELLBOOK_CLIENTSCRIPT = 10904;
    private static final int BUILD_EQUIPMENT_AND_INVENTORY_CONTAINER_CLIENTSCRIPT = 10901;
    private static final int SELECT_PRESET_CLIENTSCRIPT = 10909;
    private static final int HIDE_SPELLBOOK_BUTTONS_CLIENTSCRIPT = 10902;
    private static final int BUILD_PRESET_INTERFACE_BASE_CLIENTSCRIPT = 10900;
    private static final int REFRESH_PRESET_LIST_CLIENTSCRIPT = 10906;
    private static final int ACTIVE_SELECTED_PRESET_VARP = 262;
    private static final int SELECT_SPELLBOOK_SOUND_EFFECT = 2266;
    private static final int MAX_WORD_LENGTH = 14;
    private static final int CAPACITY_TOOLTIP_SCRIPT = 1495;
    private static final String NAME_FILTER_REGEX = "[^a-zA-Z0-9$#€£()+\\-/*!,.'_ \\[\\]@{}]";
    private static final String[] equipmentSlotLabels = {
            "Helm", "Cape", "Amulet", "Body", "Shield",
            "Legs", "Hands", "Feet", "Ring", "Ammunition"
    };
    private static final Map<Integer, Integer> equipmentSlotComponentMap = ImmutableMap.<Integer, Integer>builder()
            .put(17, EquipmentSlot.HELMET.getSlot()).put(18, EquipmentSlot.CAPE.getSlot()).put(19, EquipmentSlot.AMULET.getSlot())
            .put(20, EquipmentSlot.WEAPON.getSlot()).put(21, EquipmentSlot.PLATE.getSlot()).put(22, EquipmentSlot.SHIELD.getSlot())
            .put(23, EquipmentSlot.LEGS.getSlot()).put(24, EquipmentSlot.HANDS.getSlot()).put(25, EquipmentSlot.BOOTS.getSlot())
            .put(26, EquipmentSlot.RING.getSlot()).put(27, EquipmentSlot.AMMUNITION.getSlot()).build();

    private static void forceSetSpellbook(@NotNull final Player player, @Nullable final Spellbook book) {
        val inter = GameInterface.PRESET_MANAGER;
        val plugin = inter.getPlugin().orElseThrow(RuntimeException::new);
        val interfaceId = inter.getId();
        val dispatcher = player.getPacketDispatcher();
        for (val spellbook : Spellbook.VALUES) {
            val componentHash = interfaceId << 16 | plugin.getComponent(spellbook + " Spellbook");
            dispatcher.sendClientScript(HIGHLIGHT_SPELLBOOK_CLIENTSCRIPT, spellbook == book ? 1 : 0, componentHash);
        }
    }

    @Subscribe
    public static final void onInventoryItemSwitch(final InventoryItemSwitchEvent event) {
        val player = event.getPlayer();
        val containsPresetManagerInterface = player.getInterfaceHandler().isPresent(GameInterface.PRESET_MANAGER);
        if (!containsPresetManagerInterface) {
            return;
        }
        val presetManager = player.getPresetManager();
        val availablePresets = presetManager.getPresets();
        if (availablePresets.isEmpty()) {
            refresh(player, OptionalInt.empty(), false);
        }
    }

    private static void refresh(final Player player, @NotNull final OptionalInt optionalSlot, final boolean reselectPreset) {
        val presetManager = player.getPresetManager();
        val isViewingPreset = optionalSlot.isPresent();
        if (isViewingPreset) {
            val slot = optionalSlot.getAsInt();
            if (slot < 0 || slot >= presetManager.getTotalPresets()) {
                throw new IllegalStateException();
            }
        }
        val preset = isViewingPreset ? presetManager.getPreset(optionalSlot.getAsInt()) : null;
        val spellbook = isViewingPreset ? Objects.requireNonNull(preset).getSpellbook() : player.getCombatDefinitions().getSpellbook();
        val container = new Container(ContainerPolicy.NORMAL, ContainerType.TOURNAMENT, Optional.empty());
        final Map<Integer, ? extends Item> inventoryMap = isViewingPreset ? preset.getInventory() : player.getInventory().getContainer().getItems();
        final Map<Integer, ? extends Item> equipmentMap = isViewingPreset ? preset.getEquipment() : player.getEquipment().getContainer().getItems();
        for (int i = Equipment.SIZE + Inventory.SIZE - 1; i >= 0; i--) {
            container.set(i, i < Equipment.SIZE ? equipmentMap.get(i) : inventoryMap.get(i - Equipment.SIZE));
        }
        val index = optionalSlot.orElse(-1);
        player.getPacketDispatcher().sendUpdateItemContainer(container);
        player.getPacketDispatcher().sendClientScript(BUILD_EQUIPMENT_AND_INVENTORY_CONTAINER_CLIENTSCRIPT);
        if (reselectPreset) {
            player.getPacketDispatcher().sendClientScript(SELECT_PRESET_CLIENTSCRIPT, index, presetManager.getTotalPresets(), presetManager.getMaximumPresets());
        }
        forceSetSpellbook(player, !player.getMemberRank().eligibleTo(Preset.SPELLBOOK_MINIMUM_MEMBER_RANK) ? null : spellbook);
        player.getVarManager().sendVar(ACTIVE_SELECTED_PRESET_VARP, index);
        if (!player.getMemberRank().eligibleTo(Preset.SPELLBOOK_MINIMUM_MEMBER_RANK)) {
            player.getPacketDispatcher().sendClientScript(HIDE_SPELLBOOK_BUTTONS_CLIENTSCRIPT);
        }
    }

    @Override
    protected void attach() {
        put(8, "Preset");
        for (int index = 0; index < equipmentSlotLabels.length; index++) {
            put(17 + index, equipmentSlotLabels[index]);
        }
        put(28, "Inventory");
        put(33, "Normal Spellbook");
        put(34, "Ancient Spellbook");
        put(35, "Lunar Spellbook");
        put(36, "Arceuus Spellbook");
        put(39, "Create Preset");
        put(41, "Apply");
        put(43, "Rename");
        put(45, "Delete");
        put(47, "Always set placeholders");
        put(49, "Bank");
        put(50, "Move preset up");
        put(51, "Move preset down");
        put(52, "Preset count");
        put(55, "Preset cap");
        put(56, "Capacity tooltip");
    }

    @Override
    public void open(final Player player) {
        val presetManager = player.getPresetManager();
        presetManager.revalidatePresets();
        player.getInterfaceHandler().sendInterface(this);
        player.getPacketDispatcher().sendClientScript(BUILD_PRESET_INTERFACE_BASE_CLIENTSCRIPT);
        refresh(player, presetManager.getTotalPresets() == 0 ? OptionalInt.empty() : OptionalInt.of(presetManager.getDefaultPreset()), true);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Inventory"), 0, 28, AccessMask.CLICK_OP10, AccessMask.DRAG_DEPTH1, AccessMask.DRAG_TARGETABLE);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Preset"), 0, 20, AccessMask.CLICK_OP1, AccessMask.CLICK_OP2, AccessMask.CLICK_OP3,
                AccessMask.CLICK_OP4, AccessMask.CLICK_OP5);
        refreshPresetsList(player);
        refreshSize(player);
        val interfaceId = getInterface().getId();
        player.getPacketDispatcher().sendClientScript(CAPACITY_TOOLTIP_SCRIPT,
                "Default slots: 2<br><br>" +
                        "Purchasable slots:<br>" +
                        "Non-member slots: 3<br>" +
                        "Member slots: 8<br>" +
                        "Additional member slots: 1 per rank",
                getComponent("Preset cap") | (interfaceId << 16), getComponent("Capacity tooltip") | (interfaceId << 16));
    }

    private void refreshSize(@NotNull final Player player) {
        val presetManager = player.getPresetManager();
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Preset count"), presetManager.getTotalPresets());
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Preset cap"), Math.min(presetManager.getMaximumAllowedPresets(), presetManager.getMaximumPresets()));
    }

    @Override
    protected void build() {
        bind("Create Preset", player -> {
            val presetManager = player.getPresetManager();
            if (presetManager.getTotalPresets() >= Math.min(presetManager.getMaximumAllowedPresets(), presetManager.getMaximumPresets())) {
                if (presetManager.canPurchaseFreeSlot()) {
                    player.getDialogueManager().start(new Dialogue(player) {
                        @Override
                        public void buildDialogue() {
                            val unlockableSlotsCount = presetManager.getUnlockableSlotsCount();
                            val availableVotePoints = player.getNumericAttribute("vote_points").intValue();
                            plain("You do not have any available preset slots remaining. You can unlock up to " + unlockableSlotsCount + " more preset" + (unlockableSlotsCount == 1 ? Strings.EMPTY :
                                    "s") + ". " +
                                    "Each unlock costs " + Colour.RED.wrap("five") + " vote points. You currently have " + availableVotePoints + " vote point" +
                                    (availableVotePoints == 1 ? Strings.EMPTY : "s") + ".");
                            options("Unlock another preset slot?", new DialogueOption("Yes.", () -> {
                                val votePoints = player.getNumericAttribute("vote_points").intValue();
                                val unlockableSlots = presetManager.getUnlockableSlotsCount();
                                if (votePoints >= 5 && unlockableSlots > 0) {
                                    player.addAttribute("vote_points", votePoints - 5);
                                    GameInterface.GAME_NOTICEBOARD.getPlugin().ifPresent(plugin -> player.getPacketDispatcher().sendComponentText(GameInterface.GAME_NOTICEBOARD,
                                            plugin.getComponent("Vote credits"), "Vote credits: <col=ffffff>" + player.getNumericAttribute("vote_points").intValue() + "</col>"));
                                    presetManager.addUnlockedSlot();
                                    player.sendMessage("You have unlocked another preset slot!");
                                    GameInterface.PRESET_MANAGER.open(player);
                                } else if (votePoints < 5) {
                                    player.sendMessage("You do not have enough vote points to buy another slot.");
                                    GameInterface.PRESET_MANAGER.open(player);
                                }
                            }), new DialogueOption("No."));
                        }
                    });
                } else {
                    player.sendMessage("You have reached your maximum quota of presets.");
                }
                return;
            }
            player.sendInputString("What should this preset be called?", name -> {
                if (presetManager.getTotalPresets() >= Math.min(presetManager.getMaximumAllowedPresets(), presetManager.getMaximumPresets())) {
                    throw new IllegalStateException();
                }
                val filteredName = provideValidName(name);
                if (!filteredName.isPresent()) {
                    player.sendMessage("You may not set that as a preset name.");
                    return;
                }
                val presetName = filteredName.get();
                if (isTooLong(presetName)) {
                    player.sendMessage("The provided name exceeds maximum character length.");
                    return;
                }
                var indexOfActivePreset = indexOfActivePreset(player);
                if (indexOfActivePreset < 0 || indexOfActivePreset > presetManager.getTotalPresets()) {
                    indexOfActivePreset = 0;
                }
                val index = presetManager.getTotalPresets() == 0 ? 0 : (indexOfActivePreset + 1);
                presetManager.addPreset(index, presetName);
                refreshPresetsList(player);
                refresh(player, OptionalInt.of(index), true);
                refreshSize(player);
            });
        });
        bind("Preset", (player, slotId, itemId, option) -> {
            if (option == 1) {
                val activeIndex = indexOfActivePreset(player);
                if (activeIndex == slotId) {
                    return;
                }
                refresh(player, OptionalInt.of(slotId), true);
            } else if (option == 2) {
                load(player, slotId);
            } else if (option == 3) {
                rename(player, slotId);
            } else if (option == 4) {
                val preset = player.getPresetManager().getPreset(slotId);
                if (preset == null) {
                    return;
                }
                if (!preset.isAvailable()) {
                    player.sendMessage("You may not set locked presets as default preset.");
                    return;
                }
                player.getPresetManager().setDefaultPreset(slotId);
                refreshPresetsList(player);
            } else if (option == 5) {
                delete(player, slotId);
            }
        });
        bind("Normal Spellbook", player -> selectSpellbook(player, Spellbook.NORMAL));
        bind("Ancient Spellbook", player -> selectSpellbook(player, Spellbook.ANCIENT));
        bind("Lunar Spellbook", player -> selectSpellbook(player, Spellbook.LUNAR));
        bind("Arceuus Spellbook", player -> selectSpellbook(player, Spellbook.ARCEUUS));
        bind("Apply", player -> load(player, indexOfActivePreset(player)));
        bind("Rename", player -> rename(player, indexOfActivePreset(player)));
        bind("Delete", player -> delete(player, indexOfActivePreset(player)));
        bind("Inventory", (player, slotId, itemId, option) -> examine(player, false, itemId));
        for (val slot : equipmentSlotLabels) {
            bind(slot, (player, slotId, itemId, option) -> examine(player, true, getComponent(slot)));
        }
        bind("Always set placeholders", player -> player.getBank().toggleSetting(BankSetting.ALWAYS_PLACEHOLDER, player.getBank().getSetting(BankSetting.ALWAYS_PLACEHOLDER) == 0));
        bind("Bank", GameInterface.BANK::open);
        bind("Inventory", "Inventory", (player, fromSlot, toSlot) -> {
            val presetManager = player.getPresetManager();
            val availablePresets = presetManager.getPresets();
            if (availablePresets.isEmpty()) {
                player.getInventory().switchItem(fromSlot, toSlot);
                return;
            }
            val activeIndex = indexOfActivePreset(player);
            val preset = presetManager.getPreset(activeIndex);
            if (preset == null) {
                return;
            }
            preset.switchInventoryItem(fromSlot, toSlot);
            refresh(player, OptionalInt.of(activeIndex), false);
        });
        bind("Move preset up", (player, slotId, itemId, option) -> {
           val presetManager = player.getPresetManager();
           if (presetManager.getTotalPresets() == 0) {
               player.sendMessage("You do not have any presets yet.");
               return;
           }
            val activeIndex = indexOfActivePreset(player);
            val preset = presetManager.getPreset(activeIndex);
            if (preset == null) {
                return;
            }
            if (activeIndex <= 0) {
                return;
            }
            val abovePreset = presetManager.getPreset(activeIndex - 1);
            if (abovePreset == null) {
                return;
            }
            if (!preset.isAvailable() || !abovePreset.isAvailable()) {
                player.sendMessage("You may not move locked presets.");
                return;
            }
            presetManager.setPreset(activeIndex, abovePreset);
            presetManager.setPreset(activeIndex - 1, preset);
            refresh(player, OptionalInt.of(activeIndex - 1), true);
            if (presetManager.getDefaultPreset() == activeIndex) {
                presetManager.setDefaultPreset(activeIndex - 1);
            } else if (presetManager.getDefaultPreset() == activeIndex - 1) {
                presetManager.setDefaultPreset(activeIndex);
            }
            refreshPresetsList(player);
        });
        bind("Move preset down", (player, slotId, itemId, option) -> {
            val presetManager = player.getPresetManager();
            if (presetManager.getTotalPresets() == 0) {
                player.sendMessage("You do not have any presets yet.");
                return;
            }
            val activeIndex = indexOfActivePreset(player);
            val preset = presetManager.getPreset(activeIndex);
            if (preset == null) {
                return;
            }
            if (activeIndex >= presetManager.getTotalPresets() - 1) {
                return;
            }
            val belowPreset = presetManager.getPreset(activeIndex + 1);
            if (belowPreset == null) {
                return;
            }
            if (!preset.isAvailable() || !belowPreset.isAvailable()) {
                player.sendMessage("You may not move locked presets.");
                return;
            }
            presetManager.setPreset(activeIndex, belowPreset);
            presetManager.setPreset(activeIndex + 1, preset);
            refresh(player, OptionalInt.of(activeIndex + 1), true);
            if (presetManager.getDefaultPreset() == activeIndex) {
                presetManager.setDefaultPreset(activeIndex + 1);
            } else if (presetManager.getDefaultPreset() == activeIndex + 1) {
                presetManager.setDefaultPreset(activeIndex);
            }
            refreshPresetsList(player);
        });
    }

    private Optional<String> provideValidName(@NotNull final String string) {
        val filteredName = string.replaceAll(NAME_FILTER_REGEX, "").trim();
        if (filteredName.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(filteredName);
    }

    private boolean isTooLong(@NotNull final String filteredString) {
        if (filteredString.length() > MAX_WORD_LENGTH * 3) {
            return true;
        }
        val splitName = filteredString.split(" ");
        for (val namePart : splitName) {
            //If any of the "words"(word = phrase split by space in client terms) has a length greater than 15, do not allow the name
            //as the client would render the <br> upon rightclicking the preset.
            if (namePart.length() > MAX_WORD_LENGTH) {
                return true;
            }
        }
        return false;
    }

    private int indexOfActivePreset(@NotNull final Player player) {
        if (player.getPresetManager().getTotalPresets() == 0) {
            return -1;
        }
        return player.getVarManager().getValue(ACTIVE_SELECTED_PRESET_VARP);
    }

    private void selectSpellbook(@NotNull final Player player, @NotNull final Spellbook clickedSpellbook) {
        if (!player.getMemberRank().eligibleTo(Preset.SPELLBOOK_MINIMUM_MEMBER_RANK)) {
            player.sendMessage("You need to be a Ruby member or above to use this feature.");
            return;
        }
        val presetManager = player.getPresetManager();
        if (presetManager.getTotalPresets() == 0) {
            player.sendFilteredMessage("You must select a preset from the menu on the left first.");
            return;
        }
        val activeIndex = indexOfActivePreset(player);
        val preset = presetManager.getPreset(activeIndex);
        if (preset == null) {
            return;
        }
        val currentSpellbook = preset.getSpellbook();
        val highlightedSpellbook = clickedSpellbook == currentSpellbook ? null : clickedSpellbook;
        preset.setSpellbook(highlightedSpellbook);
        val dispatcher = player.getPacketDispatcher();
        val interfaceId = getInterface().getId();
        for (val spellbook : Spellbook.VALUES) {
            val componentHash = interfaceId << 16 | getComponent(spellbook + " Spellbook");
            dispatcher.sendClientScript(HIGHLIGHT_SPELLBOOK_CLIENTSCRIPT, spellbook == highlightedSpellbook ? 1 : 0, componentHash);
        }
        player.sendSound(SELECT_SPELLBOOK_SOUND_EFFECT);
    }

    private void refreshPresetsList(final Player player) {
        val presetManager = player.getPresetManager();
        val builder = new StringBuilder();
        val size = presetManager.getTotalPresets();
        val presets = presetManager.getPresets();
        for (int i = 0; i < size; i++) {
            val preset = presets.get(i);
            if (i == presetManager.getDefaultPreset()) {
                builder.append("<img=13>");
            }
            builder.append(preset.isAvailable() ? Strings.EMPTY : "<col=000000>").append(preset.getName()).append(preset.isAvailable() ? Strings.EMPTY : "</col>").append("|");
        }
        player.getPacketDispatcher().sendClientScript(REFRESH_PRESET_LIST_CLIENTSCRIPT, presetManager.getTotalPresets(), presetManager.getMaximumPresets(), builder.toString());
    }

    public static void load(final Player player, final int index) {
        val presetManager = player.getPresetManager();
        if (presetManager.getTotalPresets() == 0) {
            player.sendMessage("You haven't set any presets yet.");
            return;
        }
        val preset = presetManager.getPreset(index);
        if (preset == null) {
            return;
        }
        if (!preset.isAvailable()) {
            player.sendMessage("You may not load locked presets.");
            return;
        }
        val scheduledPresetLoad = player.getTemporaryAttributes().put("queued preset for load", preset) != null;
        if (!scheduledPresetLoad) {
            player.addPostProcessRunnable(() -> {
                val finalPreset = (Preset) player.getTemporaryAttributes().remove("queued preset for load");
                finalPreset.load(player);
            });
        }
    }

    private void rename(final Player player, final int index) {
        val presetManager = player.getPresetManager();
        if (presetManager.getTotalPresets() == 0) {
            player.sendMessage("You haven't set any presets yet.");
            return;
        }
        val preset = presetManager.getPreset(index);
        if (preset == null) {
            return;
        }
        if (!preset.isAvailable()) {
            player.sendMessage("You may not rename locked presets.");
            return;
        }
        val oldName = preset.getName();
        player.sendInputString("Rename this preset:", name -> {
            val filteredName = provideValidName(name);
            if (!filteredName.isPresent()) {
                player.sendMessage("You may not set that as a preset name.");
                return;
            }
            val presetName = filteredName.get();
            if (isTooLong(presetName)) {
                player.sendMessage("The provided name exceeds maximum character length.");
                return;
            }
            preset.rename(presetName);
            refreshPresetsList(player);
            player.sendMessage("Preset " + Colour.RS_PURPLE.wrap(oldName) + " has been renamed to " + Colour.RS_PURPLE.wrap(preset.getName()) + ".");
        });
    }

    private void delete(final Player player, final int index) {
        val presetManager = player.getPresetManager();
        if (presetManager.getTotalPresets() == 0) {
            player.sendMessage("You haven't set any presets yet.");
            return;
        }
        if (index < 0 || index >= presetManager.getTotalPresets()) {
            return;
        }
        val preset = presetManager.getPreset(index);
        if (preset == null) {
            throw new IllegalStateException();
        }

        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                options("Remove the preset " + preset.getName() + "?", new DialogueOption("Yes.", () -> {
                    if (presetManager.getPreset(index) != preset) {
                        return;
                    }
                    presetManager.getPresets().remove(index);
                    refreshPresetsList(player);
                    refresh(player, presetManager.getTotalPresets() == 0 ? OptionalInt.empty() : OptionalInt.of(0), true);
                    player.sendMessage("Preset " + Colour.RS_PURPLE.wrap(preset.getName()) + " has been deleted.");
                    if (index <= presetManager.getDefaultPreset()) {
                        presetManager.setDefaultPreset(presetManager.getDefaultPreset() == index ? 0 : (presetManager.getDefaultPreset() - 1));
                    }
                    presetManager.revalidatePresets();
                    GameInterface.PRESET_MANAGER.open(player);
                }), new DialogueOption("No."));
            }
        });
    }

    private void examine(final Player player, final boolean equipment, final int id) {
        if (id == ItemId.RUNE_POUCH) {
            Examine.sendItemExamine(player, id);
            val presetManager = player.getPresetManager();
            val activeIndex = presetManager.getTotalPresets() == 0 ? -1 : indexOfActivePreset(player);
            if (activeIndex > -1) {
                val preset = presetManager.getPreset(activeIndex);
                if (preset != null) {
                    val pouch = preset.getRunePouch();
                    if (pouch != null) {
                        if (pouch.isEmpty()) {
                            player.sendMessage("Rune pouch: " + Colour.RED.wrap("Empty"));
                        } else {
                            val builder = new StringBuilder();
                            int i = 0;
                            for (val rune : pouch.values()) {
                                builder.append(rune.getAmount()).append(" x ").append(ItemDefinitions.nameOf(rune.getId()));
                                if (i == pouch.size() - 2) {
                                    builder.append(" and ");
                                } else if (i < pouch.size() - 2) {
                                    builder.append(", ");
                                }
                                i++;
                            }
                            player.sendMessage("Rune pouch: " + Colour.RS_GREEN.wrap(builder.toString()));
                        }
                    }
                }
            }
            return;
        }
        if (equipment) {
            val presetManager = player.getPresetManager();
            val activeIndex = presetManager.getTotalPresets() == 0 ? -1 : indexOfActivePreset(player);
            val slot = equipmentSlotComponentMap.get(id);
            if (activeIndex > -1) {
                val preset = presetManager.getPreset(activeIndex);
                if (preset != null && slot != null) {
                    val item = preset.getEquipment().get(slot);
                    Examine.sendItemExamine(player, item);
                }
            } else {
                Examine.sendItemExamine(player, player.getEquipment().getItem(slot));
            }
        } else {
            Examine.sendItemExamine(player, id);
        }

    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.PRESET_MANAGER;
    }
}
