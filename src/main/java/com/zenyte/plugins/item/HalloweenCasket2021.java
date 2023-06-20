package com.zenyte.plugins.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.Optional;

/**
 * @author Matt
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class HalloweenCasket2021 extends ItemPlugin {

    @AllArgsConstructor
    private enum CasketReward {
        COINS_500k(new Item(995, 500000), 10),
        COINS_1m(new Item(995, 1000000), 10),
        Hunting_Knife(new Item(20779, 1), 8),
        JONAS_MASK(new Item(21720, 1), 8),
        CORRUPT_SET(new Item(2738, 1), 8),
        RING_OF_BONE(new Item(24575, 1), 6),
        PET_MYSTERY_BOX(new Item(30031, 1), 6),
      ICON_1(new Item(10559, 1), 6),
        ICON_2(new Item(10556, 1), 6),
        ICON_3(new Item(10557, 1), 6),
        ICON_4(new Item(10558, 1), 6),
        EVIL_CREATURE(new Item(30002, 1), 6),
        CUTE_CREATURE(new Item(30000, 1), 6),
        BABY_DRAGON(new Item(30156, 1), 4),
        BABY_DRAGON2(new Item(30157, 1), 4),
        BABY_DRAGON3(new Item(30158, 1), 4),
        BABY_DRAGON5(new Item(30159, 1), 4),
        PET_PENACE_QUEEN(new Item(12703, 1), 4),
        SLAYER_SKIP_SCROLLS(new Item(30568, 6), 4),
        XERIC_WISDOM(new Item(19782, 2), 4),
        GREEN_MASK(new Item(1053, 1), 1),
        BLUE_MASK(new Item(1055, 1), 1),
        RED_MASK(new Item(1057, 1), 1),
        BLACK_MASK(new Item(11847, 1), 1);

        private final Item item;
        private final int weight;

        private static final CasketReward[] values = values();

        private static final Optional<CasketReward> getReward() {
            val roll = Utils.random(128);
            int current = 0;
            for (val reward : values) {
                if ((current += reward.weight) >= roll) {
                    return Optional.of(reward);
                }
            } //
            return Optional.empty();
        }
    }

    @Override
    public void handle() {
        bind("Open", (player, item, container, slotId) -> {
            Optional<CasketReward> reward = CasketReward.getReward();
            if(reward.isPresent()) {
                player.getInventory().deleteItem(item);
                player.sendMessage("You open the halloween casket and find " + reward.get().item.getAmount() + " x " + reward.get().item.getName() + ".");
                player.getInventory().addOrDrop(new Item(reward.get().item));
            } else {
                player.getInventory().deleteItem(item);
                player.sendMessage("You open the Halloween casket and find 2m!");
                player.getInventory().addOrDrop(new Item(ItemId.COINS_995, 2_000_000));
            }
        });
    }

    @Override
    public int[] getItems() {
        return new int[] {
                30083
        };
    }
}
