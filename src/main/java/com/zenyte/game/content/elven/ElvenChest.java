package com.zenyte.game.content.elven;

import com.zenyte.game.item.ImmutableItem;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.enums.RareDrop;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.area.plugins.LootBroadcastPlugin;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Tommeh | 26/10/2019 | 16:48
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class ElvenChest implements ObjectAction {

    private static final Animation unlockAnim = new Animation(832);

    @RequiredArgsConstructor
    private enum ChestReward {

        UNCUT_DIAMOND(12, new ImmutableItem(1618, 25, 50)),
        UNCUT_RUBY(12, new ImmutableItem(1620, 25, 50)),
        UNCUT_EMERALD(12, new ImmutableItem(1622, 25, 50)),
        COAL(12, new ImmutableItem(454, 230, 500)),
        GOLD_ORE(15, new ImmutableItem(445, 230, 200)),
        ADAMANITE_ORE(15, new ImmutableItem(450, 75, 150)),
        RUNEITE_ORE(15, new ImmutableItem(452, 40, 60)),
        DRAGON_ARROWTIPS(15, new ImmutableItem(11237, 75, 200)),
        DRAGON_BOLTS(15, new ImmutableItem(21905, 75, 150)),
        DRAGON_DART_TIP(15, new ImmutableItem(11232, 80, 175)),
        COINS(20, new ImmutableItem(995, 50_000, 100_000)),
        STEEL_BAR(20, new ImmutableItem(2354, 125, 225)),
        MAGIC_LOGS(30, new ImmutableItem(1514, 80, 110)),
        NOTED_RANARR(40, new ImmutableItem(258, 20, 50)),
        NOTED_SNAPDRAGONS(30, new ImmutableItem(3001, 15, 25)),
        NOTED_TORSTOLS(30, new ImmutableItem(270, 15, 25)),
        PALM_TREE_SEED(60, new ImmutableItem(5289, 3, 5)),
        MAGIC_SEED(60, new ImmutableItem(5316, 2, 6)),
        CELASTRUS_SEED(60, new ImmutableItem(22869, 2, 6)),
        DRAGONFRUIT_TREE_SEED(60, new ImmutableItem(22877, 2, 6)),
        REDWOOD_TREE_SEED(60, new ImmutableItem(22871, 1, 2)),
        TORSTOL_SEED(60, new ImmutableItem(5304, 3, 7)),
        SNAPDRAGON_SEED(60, new ImmutableItem(5300, 6, 12)),
        RANARR_SEED(60, new ImmutableItem(5295, 6, 15)),

        DRAGONSTONE_HELM(200, new ImmutableItem(30748, 1, 1)),
        DRAGONSTONE_BODY(200, new ImmutableItem(30751, 1, 1)),
        DRAGONSTONE_PLATELEGS(200, new ImmutableItem(30754, 1, 1)),
        DRAGONSTONE_BOOTS(200, new ImmutableItem(30757, 1, 1)),
        DRAGONSTONE_GAUNTLETS(200, new ImmutableItem(30760, 1, 1)),

        INFINITY_BOOTS(200, new ImmutableItem(6920, 1, 1)),

        CRYSTAL_ARMOUR(400, new ImmutableItem(30804, 1, 1)),
        CRYSTAL_WEAPON(600, new ImmutableItem(30787, 1, 1)),
        CRYSTAL_TOOL(600, new ImmutableItem(30806, 1, 1));



        private final double rate;
        private final ImmutableItem reward;

        private static final int TOTAL_WEIGHT;
        private static final Map<ChestReward, ImmutableItem> rewards = new EnumMap<>(ChestReward.class);

        static {
            int total = 0;
            for (val entry : values()) {
                val probability = 1D / entry.rate;
                val weight = (int) (1_000_000 * probability);
                total += weight;
                val reward = entry.reward == null ? new ImmutableItem(-1, 0, 0, weight) : entry.reward;
                rewards.put(entry, new ImmutableItem(reward.getId(), reward.getMinAmount(), reward.getMaxAmount(), weight));
            }
            TOTAL_WEIGHT = total;
        }

        /**
         * Rolls a random reward out of all the brimstone chest rewards.
         *
         * @param player the player who is rolling the chest, used to calculate the fish reward should it land on that.
         * @return an optional item reward.
         */
        private static final Optional<Item> randomReward(@NotNull final Player player) {
            val random = Utils.random(TOTAL_WEIGHT);
            int current = 0;

            for (val reward : rewards.entrySet()) {
                val item = reward.getValue();
                if ((current += (int) item.getRate()) >= random) {
                    if (item.getId() == 30804 || item.getId() == 30806 || item.getId() == 30787) {
                        WorldBroadcasts.broadcast(player, BroadcastType.CHEST, item.getId(), "Enhanced Crystal Chest");
                    }
                    return Optional.of(new Item(item.getId(), Utils.random(item.getMinAmount(), item.getMaxAmount())));
                }
            }
            return Optional.empty();
        }

        /**
         * Rolls a random fish out of every fish the player can obtain.
         *
         * @param player the player whose fishing level is used to check what fish can be obtained.
         * @return an optional fish reward out of the possible options.
         */
        private static final Optional<FishReward> rollFish(@NotNull final Player player) {
            val level = player.getSkills().getLevelForXp(Skills.FISHING);
            val fishSet = new ObjectOpenHashSet<FishReward>();
            for (val reward : FishReward.values) {
                if (reward.levelThreshold <= level) {
                    fishSet.add(reward);
                }
            }
            return fishSet.isEmpty() ? Optional.empty() : Optional.ofNullable(Utils.getRandomCollectionElement(fishSet));
        }

        /**
         * An enum containing all the possible fish rewards the player can get, if they have the necessary level for it.
         */
        @AllArgsConstructor
        private enum FishReward {
            RAW_TUNA(0, new ImmutableItem(360, 200, 1200)),
            RAW_LOBSTER(35, new ImmutableItem(378, 200, 700)),
            RAW_SWORDFISH(50, new ImmutableItem(372, 200, 500)),
            RAW_MONKFISH(62, new ImmutableItem(7945, 200, 500)),
            RAW_SHARK(80, new ImmutableItem(384, 200, 500)),
            RAW_SEA_TURTLE(79, new ImmutableItem(396, 180, 200)),
            RAW_MANTA_RAY(81, new ImmutableItem(390, 150, 200));
            private final int levelThreshold;
            private final ImmutableItem reward;

            private static final FishReward[] values = values();
        }
    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Open")) {
            if (!player.getInventory().containsItem(30588, 1)) {
                player.sendMessage("You will need a Enhanced Crystal Key to unlock this chest.");
                return;
            }
            player.setAnimation(unlockAnim);
            player.getVarManager().sendBit(6583, 1);
            player.getInventory().deleteItem(30588, 1);
            WorldTasksManager.schedule(() -> {
                ChestReward.randomReward(player).ifPresent(reward -> {
                    val price = reward.getSellPrice() * reward.getAmount();
                    player.getInventory().addOrDrop(reward);
                    player.getVarManager().sendBit(object.getDefinitions().getVarbitId(), 0);
                    player.sendMessage("You find some treasure in the chest!");
                    player.sendMessage(Colour.RED.wrap("Valuable drop: " + reward.getAmount() + " x " + reward.getName() + " (" + Utils.format(price) + " coins)"));
                    LootBroadcastPlugin.fireEvent(player.getName(), reward, player.getLocation(), false, false);
                    player.addAttribute("elven_chest_open_count", player.getNumericAttribute("elven_chest_open_count").intValue() + 1);
                    sendOpenedCount(player);
                });
            });
        } else if (option.equals("Check")) {
            sendOpenedCount(player);
        }
    }

    private static void sendOpenedCount(final Player player) {
        val opened = player.getNumericAttribute("elven_chest_open_count").intValue();
        player.sendMessage("You have opened the Elven Crystal Chest " + (opened == 1 ? "once." : opened + " times."));
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{40002};
    }
}
