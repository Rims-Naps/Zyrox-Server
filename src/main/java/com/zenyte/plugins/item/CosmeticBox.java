package com.zenyte.plugins.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.plugins.dialogue.ItemChat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Kris | 03/09/2019 02:16
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class CosmeticBox extends ItemPlugin {

    @AllArgsConstructor
    public enum CosmeticItem {

        CORRUPTED_HELM(20838, 50, 500),
        CORRUPTED_PLATEBODY(20840, 50, 500),
        CORRUPTED_PLATELEGS(20842, 50, 500),
        CORRUPTED_PLATESKIRT(20844, 50, 500),
        CORRUPTED_KITESHIELD(20846, 50, 500),
        BLACK_HWEEN_MASK(11847, 20, 3000),
        GREEN_HALLOWEEN_MASK(1053, 50, 1666),
        BLUE_HALLOWEEN_MASK(1055, 50, 1666),
        RED_HALLOWEEN_MASK(1057, 50, 1666),
        RED_PARTYHAT(1038, 40, 2000),
        YELLOW_PARTYHAT(1040, 40, 2000),
        BLUE_PARTYHAT(1042, 40, 2000),
        GREEN_PARTYHAT(1044, 40, 2000),
        PURPLE_PARTYHAT(1046, 40, 2000),
        WHITE_PARTYHAT(1048, 40, 2000),
        BLACK_PARTYHAT(11862, 20, 5000),
        RAINBOW_PARTYHAT(11863, 10, 7500),
        SCYTHE(1419, 50, 1000),
        WISE_OLD_MANS_SANTA_HAT(21859, 25, 3000),
        INVERTED_SANTA_HAT(13344, 25, 1000),
        BLACK_SANTA_HAT(13343, 25, 3000),
        SANTA_HAT(1050, 25, 2000),
        BUNNY_EARS(1037, 40, 1000),
        MIME_MASK(3057, 200, 0),
        MIME_TOP(3058, 200, 0),
        MIME_LEGS(3059, 200, 0),
        MIME_GLOVES(3060, 200, 0),
        MIME_BOOTS(3061, 200, 0),
        PRINCE_TUNIC(6184, 200, 0),
        PRINCE_LEGGINGS(6185, 200, 0),
        PRINCESS_BLOUSE(6186, 200, 0),
        PRINCESS_SKIRT(6187, 200, 0),
        ZOMBIE_SHIRT(7592, 200, 0),
        ZOMBIE_TROUSERS(7593, 200, 0),
        ZOMBIE_MASK(7594, 200, 0),
        ZOMBIE_GLOVES(7595, 200, 0),
        ZOMBIE_BOOTS(7596, 200, 0),
        SANTA_MASK(12887, 200, 0),
        SANTA_JACKET(12888, 200, 0),
        SANTA_PANTALOONS(12889, 200, 0),
        SANTA_GLOVES(12890, 200, 0),
        SANTA_BOOTS(12891, 200, 0),
        CHICKEN_HEAD(11021, 200, 0),
        CHICKEN_FEET(11019, 200, 0),
        CHICKEN_WINGS(11020, 200, 0),
        CHICKEN_LEGS(11022, 200, 0),
        CAMO_TOP(6654, 200, 0),
        CAMO_BOTTOMS(6655, 200, 0),
        CAMO_HELM(6656, 200, 0),
        ;

        @Getter private final int id, weight, credits;

        private static final CosmeticItem[] values = values();

        private static int total;

        static {
            for (val reward : values) {
                total += reward.weight;
            }
        }

        private static final CosmeticItem generate() {
            val random = Utils.random(total);
            int current = 0;
            for (val it : values) {
                if ((current += it.weight) >= random) {
                    return it;
                }
            }
            return null;
        }

    }

    @Override
    public void handle() {
        if (ArrayUtils.isEmpty(getItems())) {
            return;
        }
        bind("Open", (player, item, container, slotId) -> {
            val rewardItem = CosmeticItem.generate();
            if (rewardItem == null) {
                return;
            }
            if (rewardItem.weight <= 40) {
                WorldBroadcasts.broadcast(player, BroadcastType.COSMETIC_BOX_RARE_ITEM, rewardItem.id);
            }
            player.getInventory().deleteItem(item);
            val reward = new Item(rewardItem.id, 1);
            player.getInventory().addOrDrop(reward);
            player.getDialogueManager().start(new ItemChat(player, reward, "You open the cosmetic box and find 1 x " + reward.getName() + "!"));
        });
    }

    @Override
    public int[] getItems() {
        return new int[] {

        };
    }
}
