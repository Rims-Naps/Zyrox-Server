package com.zenyte.game.content.skills.hunter;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Kris | 14/02/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class CustomFurClothingInterface extends Interface {

    @Getter
    @AllArgsConstructor
    private enum FurStock {

        POLAR_CAMO_TOP(ItemId.POLAR_CAMO_TOP, 20, new Item(ItemId.POLAR_KEBBIT_FUR, 2)),
        POLAR_CAMO_LEGS(ItemId.POLAR_CAMO_LEGS, 20, new Item(ItemId.POLAR_KEBBIT_FUR, 2)),

        WOOD_CAMO_TOP(ItemId.WOOD_CAMO_TOP, 20, new Item(ItemId.COMMON_KEBBIT_FUR, 2)),
        WOOD_CAMO_LEGS(ItemId.WOOD_CAMO_LEGS, 20, new Item(ItemId.COMMON_KEBBIT_FUR, 2)),

        JUNGLE_CAMO_TOP(ItemId.JUNGLE_CAMO_TOP, 20, new Item(ItemId.FELDIP_WEASEL_FUR, 2)),
        JUNGLE_CAMO_LEGS(ItemId.JUNGLE_CAMO_LEGS, 20, new Item(ItemId.FELDIP_WEASEL_FUR, 2)),

        DESERT_CAMO_TOP(ItemId.DESERT_CAMO_TOP, 20, new Item(ItemId.DESERT_DEVIL_FUR, 2)),
        DESERT_CAMO_LEGS(ItemId.DESERT_CAMO_LEGS, 20, new Item(ItemId.DESERT_DEVIL_FUR, 2)),

        GLOVES_OF_SILENCE(ItemId.GLOVES_OF_SILENCE, 600, new Item(ItemId.DARK_KEBBIT_FUR, 2)),

        SPOTTED_CAPE(ItemId.SPOTTED_CAPE, 400, new Item(ItemId.SPOTTED_KEBBIT_FUR, 2)),

        LARUPIA_HAT(ItemId.LARUPIA_HAT, 500, new Item(ItemId.LARUPIA_FUR)),
        LARUPIA_TOP(ItemId.LARUPIA_TOP, 100, new Item(ItemId.LARUPIA_FUR), new Item(ItemId.TATTY_LARUPIA_FUR)),
        LARUPIA_LEGS(ItemId.LARUPIA_LEGS, 100, new Item(ItemId.LARUPIA_FUR), new Item(ItemId.TATTY_LARUPIA_FUR)),

        GRAAHK_HEADDRESS(ItemId.GRAAHK_HEADDRESS, 750, new Item(ItemId.GRAAHK_FUR)),
        GRAAHK_TOP(ItemId.GRAAHK_TOP, 150, new Item(ItemId.GRAAHK_FUR), new Item(ItemId.TATTY_GRAAHK_FUR)),
        GRAAHK_LEGS(ItemId.GRAAHK_LEGS, 150, new Item(ItemId.GRAAHK_FUR), new Item(ItemId.TATTY_GRAAHK_FUR)),

        KYATT_HAT(ItemId.KYATT_HAT, 1000, new Item(ItemId.KYATT_FUR)),
        KYATT_TOP(ItemId.KYATT_TOP, 200, new Item(ItemId.KYATT_FUR), new Item(ItemId.TATTY_KYATT_FUR)),
        KYATT_LEGS(ItemId.KYATT_LEGS, 200, new Item(ItemId.KYATT_FUR), new Item(ItemId.TATTY_KYATT_FUR)),

        SPOTTIER_CAPE(ItemId.SPOTTIER_CAPE, 800, new Item(ItemId.DASHING_KEBBIT_FUR, 2));

        FurStock(final int item, final int fee, @NotNull final Item fur) {
            this(item, fee, fur, null);
        }
        private final int item, fee;
        private final Item fur, alternativeFur;

        private static final FurStock[] values = values();
    }

    public CustomFurClothingInterface() {
        container = new Container(ContainerPolicy.ALWAYS_STACK, ContainerType.CUSTOM_FUR_CLOTHING, Optional.empty());
        for (val furItem : FurStock.values) {
            container.add(new Item(furItem.item));
        }
    }

    private final Container container;

    @Override
    protected void attach() {
        put(75, "Interact with item");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
        container.setFullUpdate(true);
        player.getPacketDispatcher().sendUpdateItemContainer(container);
    }

    @Override
    protected void build() {
        bind("Interact with item", (player, slotId, itemId, option) -> {
            val furItem = Objects.requireNonNull(FurStock.values[slotId]);
            val fee = furItem.fee;
            val requirement = furItem.fur;
            val alternativeRequirement = furItem.alternativeFur;
            val name = ItemDefinitions.getOrThrow(furItem.item).getName();
            if (option == 1) {
                player.sendMessage(name + " costs: " + furItem.fee + " coins and " +
                        (alternativeRequirement == null ? "" : "either ") + requirement.getAmount() + " " + requirement.getName().toLowerCase()
                        + (alternativeRequirement == null ? "" : (" or 1 " + alternativeRequirement.getName().toLowerCase())) + ".");
                return;
            }
            val inventory = player.getInventory();
            val amount = option == 2 ? 1 : option == 3 ? 5 : 10;
            for (int i = 0; i < amount; i++) {
                if (!inventory.containsItem(ItemId.COINS_995, fee) || (!inventory.containsItem(requirement) && (alternativeRequirement == null || !inventory.containsItem(alternativeRequirement)))) {
                    val lowercaseName = name.toLowerCase();
                    val prefix = furItem == FurStock.GLOVES_OF_SILENCE || name.endsWith("legs") ? ("a pair of") : Utils.getAOrAn(name);
                    player.sendMessage("You need at least " + furItem.fee + " coins and " +
                            (alternativeRequirement == null ? "" : "either ") + requirement.getAmount() + " " + requirement.getName().toLowerCase()
                            + (alternativeRequirement == null ? "" : (" or 1 " + alternativeRequirement.getName().toLowerCase())) + " to purchase " + prefix + " " + lowercaseName + ".");
                    return;
                }
                inventory.deleteItem(requirement).onFailure(it -> inventory.deleteItem(alternativeRequirement));
                inventory.deleteItem(ItemId.COINS_995, fee);
                inventory.addOrDrop(new Item(furItem.item));
            }
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.CUSTOM_FUR_CLOTHING;
    }
}
