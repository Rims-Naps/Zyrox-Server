package com.zenyte.game.content.skills.hunter.node;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.utils.Ordinal;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Kris | 25/03/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@AllArgsConstructor
@Ordinal
public enum BirdHouseType {
    REGULAR(5, 5, 15, 28, 0.1, ItemId.BIRD_HOUSE, ItemId.LOGS),
    OAK(15, 14, 20, 42, 0.125, ItemId.OAK_BIRD_HOUSE, ItemId.OAK_LOGS),
    WILLOW(25, 24, 25, 56, 0.15, ItemId.WILLOW_BIRD_HOUSE, ItemId.WILLOW_LOGS),
    TEAK(35, 34, 30, 70, 0.175, ItemId.TEAK_BIRD_HOUSE, ItemId.TEAK_LOGS),
    MAPLE(45, 44, 35, 82, 0.2, ItemId.MAPLE_BIRD_HOUSE, ItemId.MAPLE_LOGS),
    MAHOGANY(50, 49, 40, 96, 0.225, ItemId.MAHOGANY_BIRD_HOUSE, ItemId.MAHOGANY_LOGS),
    YEW(60, 59, 45, 102, 0.25, ItemId.YEW_BIRD_HOUSE, ItemId.YEW_LOGS),
    MAGIC(75, 74, 50, 114, 0.275, ItemId.MAGIC_BIRD_HOUSE, ItemId.MAGIC_LOGS),
    REDWOOD(90, 89, 55, 120, 0.3, ItemId.REDWOOD_BIRD_HOUSE, ItemId.REDWOOD_LOGS);

    private final int craftingRequirement, hunterRequirement;
    private final double craftingExperience, hunterExperience;
    private final double chanceOfNest;
    private final int birdhouseId;
    private final int logsId;

    @Getter private static final List<BirdHouseType> values = Collections.unmodifiableList(Arrays.asList(values()));
    @Getter private static final List<Item> ambiguousBirdhouseMenu;

    static {
        val list = new ObjectArrayList<Item>();
        for (val value : values) {
            list.add(new Item(value.birdhouseId));
        }
        ambiguousBirdhouseMenu = Collections.unmodifiableList(list);
    }
    
    public static final Optional<BirdHouseType> findThroughBirdhouse(final int id) {
        return Optional.ofNullable(Utils.findMatching(values, value -> value.birdhouseId == id));
    }
    
    public static final Optional<BirdHouseType> findThroughLogs(final int id) {
        return Optional.ofNullable(Utils.findMatching(values, value -> value.logsId == id));
    }

    public final int getCreateableAmountThroughMaterials(@NotNull final Player player) {
        val inventory = player.getInventory();
        return Math.min(inventory.getAmountOf(ItemId.CLOCKWORK), inventory.getAmountOf(logsId));
    }
}