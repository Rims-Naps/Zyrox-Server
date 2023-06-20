package com.zenyte.game.content.chambersofxeric.storageunit;

import com.zenyte.Constants;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.object.WorldObject;
import mgi.types.config.items.ItemDefinitions;
import com.zenyte.plugins.dialogue.ItemChat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author Kris | 23/09/2019 17:43
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@AllArgsConstructor
public enum StorageUnit {

    SMALL_STORAGE_UNIT(30, 2, 30, 250, 29770, 21037),
    MEDIUM_STORAGE_UNIT(60, 4, 60, 500, 29779, 21038),
    MEDIUM_STORAGE_UNIT_UPGRADE(60, 2, 60, 500, 29779, 21040),
    LARGE_STORAGE_UNIT(90, 6, 90, 1000, 29780, 21039),
    LARGE_STORAGE_UNIT_SMALL_UPGRADE(90, 4, 90, 1000, 29780, 21041),
    LARGE_STORAGE_UNIT_MEDIUM_UPGRADE(90, 2, 90, 1000, 29780, 21039);

    /**
     * An array containing the storage unit objects that are visible when building the storage unit from scratch.
     */
    private static final StorageUnit[] values = new StorageUnit[]{
            SMALL_STORAGE_UNIT,
            MEDIUM_STORAGE_UNIT,
            LARGE_STORAGE_UNIT
    };

    /**
     * An array containing the storage unit objects that are visible when upgrading a small storage unit.
     */
    private static final StorageUnit[] smallStorageUnitUpgrades = new StorageUnit[]{
            MEDIUM_STORAGE_UNIT_UPGRADE,
            LARGE_STORAGE_UNIT_SMALL_UPGRADE
    };

    /**
     * An array containing the storage unit objects that are visible when upgrading a medium storage unit.
     */
    private static final StorageUnit[] mediumStorageUnitUpgrades = new StorageUnit[]{
            LARGE_STORAGE_UNIT_MEDIUM_UPGRADE
    };
    /**
     * The hammer item id.
     */
    private static final int HAMMER = 2347;

    /**
     * The hotspot object id for storage units, transparent white object which can be built into an actual unit.
     */
    public static final int UNIT_HOTSPOT_OBJECT = 29769;
    /**
     * The mallignum plank item id.
     */
    private static final int MALLIGNUM_PLANK = 21036;

    /**
     * The CS2 id which appends a furniture piece into the interface.
     */
    private static final int ADD_FURNITURE_CS2 = 1404;

    /**
     * The CS2 id which builds the furniture interface based on the values sent previously through the {@link StorageUnit#ADD_FURNITURE_CS2}.
     */
    private static final int BUILD_FURNITURE_INTERFACE_CS2 = 1406;

    /**
     * The parameter value for the furniture interface which implies the chat input is disabled, allowing for hotkeys usage in building the furniture.
     */
    private static final int STOP_CHAT_INPUT = 0;
    /**
     * The sound effect played when hammering the storage unit.
     */
    private static final SoundEffect hammerSound = new SoundEffect(938);
    private final int level, planks, personalAmount, publicAmount, objectId, itemId;

    /**
     * Gets the best hammer the player has with them, checking the weapon as well as inventory.
     *
     * @param player the player whom to check.
     * @return an optional result of the hammer type, or absent if the player has none.
     */
    @NotNull
    private static final Optional<HammerType> getHammer(@NotNull final Player player) {
        return Optional.ofNullable(Utils.findMatching(HammerType.values,
                hammer -> ArrayUtils.contains(hammer.ids, player.getEquipment().getId(EquipmentSlot.WEAPON))
                        || player.getInventory().containsAnyOf(hammer.ids)));
    }

    /**
     * Opens the furniture creation menu for the player w/ what they can build based on the object they were interacting with.
     *
     * @param player the player opening the creation menu.
     * @param object the hotspot object which is being constructed.
     */
    public static final void openCreationMenu(@NotNull final Player player, @NotNull final WorldObject object) {
        val hammer = getHammer(player);
        if (!hammer.isPresent() && player.getEquipment().getItem(EquipmentSlot.WEAPON).getId() != 30907) {
            player.getDialogueManager().start(new ItemChat(player, new Item(2347), "You need a hammer to build anything here."));
            return;
        }

        player.getTemporaryAttributes().put("raidsStorageUnit", object);
        GameInterface.FURNITURE_CREATION.open(player);
        val objectId = object.getId();
        val storageUnits = objectId == UNIT_HOTSPOT_OBJECT ? values : objectId == SMALL_STORAGE_UNIT.objectId ? smallStorageUnitUpgrades : mediumStorageUnitUpgrades;

        val materialBuilder = new StringBuilder();
        val elementsList = new ObjectArrayList<String>(3);
        for (val unit : storageUnits) {
            materialBuilder.append(ItemDefinitions.nameOf(unit.itemId));
            materialBuilder.append('|');
            materialBuilder.append(ItemDefinitions.nameOf(MALLIGNUM_PLANK));
            materialBuilder.append(": ");
            materialBuilder.append(unit.planks);
            materialBuilder.append("<br>");
            elementsList.add(materialBuilder.toString());
            materialBuilder.setLength(0);
        }
        val dispatcher = player.getPacketDispatcher();
        val inventory = player.getInventory();
        val construction = player.getSkills().getLevelForXp(Skills.CONSTRUCTION);
        for (int i = 0, length = storageUnits.length; i < length; i++) {
            val unit = storageUnits[i];
            val canBuild = inventory.containsItem(MALLIGNUM_PLANK, unit.planks) && construction >= unit.level;
            dispatcher.sendClientScript(ADD_FURNITURE_CS2, 1 + i, unit.itemId, unit.level, elementsList.get(i), canBuild ? 1 : 0);
        }
        player.getPacketDispatcher().sendClientScript(BUILD_FURNITURE_INTERFACE_CS2, storageUnits.length, STOP_CHAT_INPUT);
    }

    /**
     * Builds the furniture seen on the interface at the selected slot.
     *
     * @param player the player building the furniture.
     * @param slot   the slot clicked on the interface.
     */
    public static final void build(final Player player, final int slot) {
        val storageUnitObjectAttribute = player.getTemporaryAttributes().get("raidsStorageUnit");
        player.getInterfaceHandler().closeInterface(GameInterface.FURNITURE_CREATION);
        if (!(storageUnitObjectAttribute instanceof WorldObject)) {
            return;
        }
        val object = (WorldObject) storageUnitObjectAttribute;
        if (!object.exists()) {
            return;
        }
        player.getRaid().ifPresent(raid -> {
            if (raid.isConstructingStorage()) {
                player.sendMessage("Someone else is already building a storage unit.");
                return;
            }
            val hammer = getHammer(player);
            if (!hammer.isPresent() && player.getEquipment().getItem(EquipmentSlot.WEAPON).getId() != 30907) {
                player.getDialogueManager().start(new ItemChat(player, new Item(HAMMER), "You need a hammer to build anything here."));
                return;
            }
            val objectId = object.getId();
            val storageUnits = objectId == UNIT_HOTSPOT_OBJECT ? values : objectId == SMALL_STORAGE_UNIT.objectId ? smallStorageUnitUpgrades : mediumStorageUnitUpgrades;
            if (slot < 0 || slot >= storageUnits.length) {
                return;
            }
            val unit = storageUnits[slot];
            if (Constants.CONSTRUCTION) {
                if (player.getSkills().getLevel(Skills.CONSTRUCTION) < unit.level) {
                    player.sendMessage("You need a Construction level of at least " + unit.level + " to build this.");
                    return;
                }
            }
            val inventory = player.getInventory();
            if (!inventory.containsItem(MALLIGNUM_PLANK, unit.planks)) {
                player.sendMessage("You need at least " + unit.planks + " mallignum root planks to build this.");
                return;
            }
            if (Constants.CONSTRUCTION) {
                player.getSkills().addXp(Skills.CONSTRUCTION, unit.planks * 75);
            }
            inventory.deleteItem(MALLIGNUM_PLANK, unit.planks);
            player.setAnimation(hammer.get().animation);
            player.sendSound(hammerSound);
            raid.addPoints(player, unit.planks * 100);
            raid.setConstructingStorage(true);
            WorldTasksManager.schedule(() -> {
                raid.setConstructingStorage(false);
                raid.getPlayers().stream().filter(p -> p.getInterfaceHandler().isVisible(GameInterface.FURNITURE_CREATION.getId())).forEach(p ->
                        p.getInterfaceHandler().closeInterface(GameInterface.FURNITURE_CREATION));
                raid.constructOrGetSharedStorage().getContainer().setContainerSize(unit.publicAmount);
                val map = raid.getMap();
                if (map != null) {
                    for (val chunk : map.getRaidChunks()) {
                        chunk.refreshStorageUnits(unit.objectId);
                    }
                }
            }, 2);
        });
    }

    /**
     * An enum defining the types of the hammers that may be used for building storage units.
     */
    private enum HammerType {
        DRAGON_WARHAMMER(new Animation(7049), 13576, 20785),
        HAMMER(new Animation(3676), StorageUnit.HAMMER),
        STATIUSS_WARHAMMER(new Animation(3676), 22622);

        private static final HammerType[] values = values();
        private final Animation animation;
        private final int[] ids;

        HammerType(final Animation animation, final int... ids) {
            this.animation = animation;
            this.ids = ids;
        }
    }

}
