package com.zenyte.plugins.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.ItemChat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Kris | 10/06/2019 06:31
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class MysteryBox extends ItemPlugin {

    @Getter
    @AllArgsConstructor
    public enum MysteryItem {
        //RESOURCES + CONSUMABLES
        DRAGON_DART_TIP(11232, 100, 150, 1000, 0),
        DRAGON_KNIFE(22804, 100, 150, 1000, 0),
        DRAGON_THROWNAXE(20849, 100, 150, 1000, 0),
        RAW_SHARK(384, 100, 225, 1000, 0),
        RAW_ANGLERFISH(13440, 100, 150, 1000, 0),
        RAW_MANTA_RAY(390, 100, 150, 1000, 0),
        UNCUT_DRAGONSTONE(1632, 25, 35, 1000, 0),
        UNCUT_DIAMOND(1618, 40, 60, 1000, 0),
        UNCUT_RUBY(1620, 50, 70, 1000, 0),
        MITHRIL_BAR(2360, 120, 200, 1000, 0),
        ADAMANTITE_BAR(2362, 100, 150, 1000, 0),
        RUNITE_BAR(2364, 50, 75, 1000, 0),
        MAGIC_LOGS(1514, 100, 150, 1000, 0),
        DRAGON_BONES(537, 75, 100, 1000, 0),
        CANNONBALL(2, 400, 500, 1000, 0),
        CHINCHOMPA(10033, 150, 300, 1000, 0),
        RED_CHINCHOMPA(10034, 150, 300, 1000, 0),
        BLACK_CHINCHOMPA(11959, 120, 200, 1000, 0),
        DIAMOND_BOLTS_E(9243, 300, 500, 1000, 0),
        DRAGONSTONE_BOLTS_E(9244, 250, 350, 1000, 0),
        YEW_SEED(5315, 5, 10, 1000, 0),
        MAGIC_SEED(5316, 3, 5, 1000, 0),
        PALM_TREE_SEED(5289, 3, 5, 1000, 0),
        PAPAYA_TREE_SEED(5288, 5, 10, 1000, 0),
        STAMINA_POTION(12626, 15, 25, 1000, 0),
        SUPER_COMBAT(12696, 15, 20, 1000, 0),
        SANFEW_SERUM(10926, 15, 25, 1000 ,0),
        ZULRAH_SCALES(ItemId.ZULRAHS_SCALES, 500, 2500, 500, 0),
        BURNT_PAGE(20718, 30, 50, 1000, 0),

        //All items above this line are rolled within the two supply rolls. An additional roll will be done for the entire table afterwards.

        //Crystal Chest
        INFINITY_ROBE_TOP(6916, 1, 1, 500, 50),
        INFINITY_ROBE_BOTTOM(6924, 1, 1, 500, 50),
        INFINITY_GLOVES(6922, 1, 1, 500, 50),
        INFINITY_BOOTS(6920, 1, 1, 500, 50),
        INFINITY_HAT(6918, 1, 1, 500, 50),

        //CLUE ITEMS
        /*RING_OF_COINS(20017, 1, 1, 100, 200),
        RING_OF_NATURE(20005, 1, 1, 100, 200),
        WIZARD_BOOTS(2579, 1, 1, 100, 100),
        RED_DRAGON_MASK(12522, 1, 1, 100, 0),
        URIS_HAT(23255, 1, 1, 100, 0),
        BLACK_CAVALIER(2643, 1, 1, 100, 0),
        WHITE_CAVALIER(12321, 1, 1, 100, 0),
        DARK_CAVALIER(2641, 1, 1, 100, 0),
        BLUE_CAVALIER(12325, 1, 1, 100, 0),
        PURPLE_SWEETS(10476, 25, 150, 100, 0),*/

        //OTHER
        AMULET_OF_ETERNAL_GLORY(19707, 1, 1, 100, 0),
        //ANTI_PANTIES(13288, 1, 1, 100, 0),
        //EASTER_EGG(1961, 1, 1, 100, 50),
        //DISK_OF_RETURNING(981, 1, 1, 100, 50),
        //HALF_FULL_WINE_JUG(1989, 1, 1, 100, 50),
        //PUMPKIN(1959, 1, 1, 100, 0),
        //EASTER_RING(7927, 1, 1, 100, 50),
        GNOME_CHILD_HAT(13655, 1, 1, 100, 0),
        DRAGON_AXE(6739, 1, 1, 1000, 0),
        DRAGON_BOOTS(11840, 1, 1, 1000, 50),
        //FLIPPERS(6666, 1, 1, 1000, 0),
        //VOLCANIC_WHIP_MIX(12771, 1, 1, 1000, 0),
        //FROZEN_WHIP_MIX(12769, 1, 1, 1000, 0),
        ONYX(6573, 1, 1, 750, 150),

        //BARROWS
        GUTHANS_SET(ItemId.GUTHANS_ARMOUR_SET, 1, 1, 50, 0),

        VERACS_SET(ItemId.VERACS_ARMOUR_SET, 1, 1, 50, 0),

        DHAROKS_SET(ItemId.DHAROKS_ARMOUR_SET, 1, 1, 50, 0),

        TORAGS_SET(ItemId.TORAGS_ARMOUR_SET, 1, 1, 50, 0),

        AHRIMS_SET(ItemId.AHRIMS_ARMOUR_SET, 1, 1, 50, 0),

        KARILS_SET(ItemId.KARILS_ARMOUR_SET, 1, 1, 200, 0),

        AMULET_OF_THE_DAMNED(12851, 1, 1, 500, 100),

        //Donation Store Items
        BLACK_PARTYHAT(11862, 1, 1, 20, 3000),
        CHRISTMAS_CRACKER(962, 1, 1, 20, 2000),
        BUNNY_EARS(1037, 1, 1, 50, 1000),
        SCYTHE(1419, 1, 1, 50, 1000),
        ABYSSAL_DAGGER(13265, 1, 1, 50, 200),
        ABYSSAL_BLUDGEON(ItemId.ABYSSAL_BLUDGEON, 1, 1, 35, 500),
        ABYSSAL_WHIP(4151, 1, 1, 100, 75),
        SARADOMIN_SWORD(11838, 1, 1, 100, 100),
        ZAMORAKIAN_SPEAR(11824, 1, 1, 100, 300),
        DRAGON_DEFENDER(12954, 1, 1, 1000, 75),
        FIGHTER_TORSO(10551, 1, 1, 1000, 75),
        FIRE_CAPE(6570, 1, 1, 500, 75),
        AMULET_OF_FURY(6585, 1, 1, 1000, 75),
        MAGES_BOOK(6889, 1, 1, 500, 75),
        CRYSTAL_KEY(990, 1, 30, 1000, 10),
        CRYSTAL_SHARDS(ItemId.CRYSTAL_SHARD, 50, 350, 1000, 0),
        //RANGERS_BOOTS(2577, 1, 1, 100, 500),
        BARROWS_TELEPORT_SCROLL(12100, 1, 1, 750, 150),
        GODWARS_TELEPORT_SCROLL(12101, 1, 1, 750, 150),
        ZULRAH_TELEPORT_SCROLL(12102, 1, 1, 750, 150),
        KRAKEN_TELEPORT_SCROLL(12103, 1, 1, 750, 150),
        CERBERUS_TELEPORT_SCROLL(12104, 1, 1, 750, 150),
        DAGANNOTH_KINGS_TELEPORT_SCROLL(12105, 1, 1, 750, 150),

        //Extra
        DRAGONFIRE_SHIELD(11284, 1, 1, 100, 350),
        IMBUED_HEART(20724, 1, 1, 100, 350),
        //BLACK_TOURMALINE_CORE(ItemId.BLACK_TOURMALINE_CORE, 1, 1, 50, 500),
        ANCIENT_SHARD(ItemId.ANCIENT_SHARD, 1, 7, 200, 0),
        SPIRIT_SHIELD(ItemId.SPIRIT_SHIELD, 1, 1, 100, 0),
        BLESSED_SPIRIT_SHIELD(ItemId.BLESSED_SPIRIT_SHIELD, 1, 1, 40, 0),
        ANCIENT_WYVERN_SHIELD(ItemId.ANCIENT_WYVERN_SHIELD_21634, 1, 1, 42, 250),
        ENHANCED_ICE_GLOVES(30030, 1, 1, 60, 0),
        DRAGON_PICKAXE(ItemId.DRAGON_PICKAXE, 1, 1, 75, 0),
        DRAGON_CROSSBOW(ItemId.DRAGON_CROSSBOW, 1, 1, 75, 250),
        PET_MYSTERY_BOX(30031, 1, 1, 80, 250),
        DRAGON_KITESHIELD(ItemId.DRAGON_KITESHIELD, 1, 1, 35, 250),
        DRAGON_PLATEBODY(ItemId.DRAGON_PLATEBODY, 1, 1, 35, 250);
        //DRAGON_FULL_HELM(ItemId.DRAGON_FULL_HELM, 1, 1, 35, 250);

        private final int id, minAmount, maxAmount, weight, credits;
        public static final MysteryItem[] values = values();

        private static int total;

        static {
            for (val reward : values) {
                total += reward.weight;
            }
        }

        private static final MysteryItem generate() {

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
        bind("Open", (player, item, container, slotId) -> {
            player.getInventory().deleteItem(item);
            val builder = new StringBuilder();
            val rewards = getRewards(player);
            for (val rewardItem : rewards) {
                val reward = new Item(rewardItem.id, Utils.random(rewardItem.minAmount, rewardItem.maxAmount));
                builder.append(reward.getAmount()).append(" x ").append(reward.getName()).append(", ");
                player.getInventory().addOrDrop(reward);
                if (rewardItem.credits >= 200) {
                    WorldBroadcasts.broadcast(player, BroadcastType.MYSTERY_BOX_RARE_ITEM, rewardItem.id);
                }
            }
            builder.delete(builder.length() - 2, builder.length());
            player.getDialogueManager().start(new ItemChat(player, item, "You open the mystery box and find " + builder.toString() + "!"));
        });
    }

    private final List<MysteryItem> getRewards(@NotNull final Player player) {
        val list = new ArrayList<MysteryItem>(3);
        for (int i = 0; i < 3; i++) {
            list.add(generateItem(player));
        }
        return list;
    }

    @NotNull
    public static final MysteryItem generateItem(@NotNull final Player player) {
        MysteryItem rewardItem;
        while(true) {
            rewardItem = MysteryItem.generate();
            if (rewardItem == MysteryItem.FIGHTER_TORSO || rewardItem == MysteryItem.FIRE_CAPE || rewardItem == MysteryItem.DRAGON_DEFENDER) {
                if (player.containsItem(rewardItem.id)) {
                    continue;
                }
            }
            break;
        }
        return Objects.requireNonNull(rewardItem);
    }

    @Override
    public int[] getItems() {
        return new int[] {
                6199
        };
    }
}
