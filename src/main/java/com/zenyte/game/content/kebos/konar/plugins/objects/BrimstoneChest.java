package com.zenyte.game.content.kebos.konar.plugins.objects;

import com.zenyte.game.item.ImmutableItem;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
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
public class BrimstoneChest implements ObjectAction {

    private static final Animation unlockAnim = new Animation(832);

    @RequiredArgsConstructor
    private enum ChestReward {

        UNCUT_DIAMOND(12, new ImmutableItem(1618, 25, 35)),
        UNCUT_RUBY(12, new ImmutableItem(1620, 25, 35)),
        COAL(12, new ImmutableItem(454, 300, 500)),
        COINS(12, new ImmutableItem(995, 50000, 150000)),
        GOLD_ORE(15, new ImmutableItem(445, 100, 200)),
        DRAGON_ARROWTIPS(15, new ImmutableItem(11237, 50, 200)),
        IRON_ORE(20, new ImmutableItem(441, 350, 500)),
        RUNE_FULL_HELM(20, new ImmutableItem(1164, 2, 4)),
        RUNE_PLATEBODY(20, new ImmutableItem(1128, 1, 2)),
        RUNE_PLATELEGS(20, new ImmutableItem(1080, 1, 2)),
        RAW_FISH(20, null),

        RUNITE_ORE(30, new ImmutableItem(452, 10, 15)),
        STEEL_BAR(30, new ImmutableItem(2354, 300, 500)),
        MAGIC_LOGS(30, new ImmutableItem(1514, 120, 160)),
        DRAGON_DART_TIP(30, new ImmutableItem(11232, 40, 160)),
        PALM_TREE_SEED(60, new ImmutableItem(5289, 2, 4)),
        MAGIC_SEED(60, new ImmutableItem(5316, 2, 3)),
        CELASTRUS_SEED(60, new ImmutableItem(22869, 2, 4)),
        DRAGONFRUIT_TREE_SEED(60, new ImmutableItem(22877, 2, 4)),
        REDWOOD_TREE_SEED(60, new ImmutableItem(22871, 1, 1)),
        TORSTOL_SEED(60, new ImmutableItem(5304, 3, 5)),
        SNAPDRAGON_SEED(60, new ImmutableItem(5300, 3, 5)),
        RANARR_SEED(60, new ImmutableItem(5295, 3, 5)),
        PURE_ESSENCE(60, new ImmutableItem(7937, 3000, 6000)),

        BROKEN_DRAGON_HASTA(200, new ImmutableItem(22963, 1, 1)),

        MYSTIC_HAT_DUSK(1000, new ImmutableItem(23047, 1, 1)),
        MYSTIC_ROBE_TOP_DUSK(1000, new ImmutableItem(23050, 1, 1)),
        MYSTIC_ROBE_BOTTOM_DUSK(1000, new ImmutableItem(23053, 1, 1)),
        MYSTIC_GLOVES_DUSK(1000, new ImmutableItem(23056, 1, 1)),
        MYSTIC_BOOTS_DUSK(1000, new ImmutableItem(23059, 1, 1));

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
                    if (item.getId() == -1) {
                        val fishReward = rollFish(player);
                        if (!fishReward.isPresent()) {
                            return Optional.empty();
                        }
                        val fish = fishReward.get();
                        return Optional.of(new Item(fish.reward.getId(), Utils.random(fish.reward.getMinAmount(), fish.reward.getMaxAmount())));
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
            RAW_TUNA(0, new ImmutableItem(360, 100, 350)),
            RAW_LOBSTER(35, new ImmutableItem(378, 100, 350)),
            RAW_SWORDFISH(50, new ImmutableItem(372, 100, 300)),
            RAW_MONKFISH(62, new ImmutableItem(7945, 100, 300)),
            RAW_SHARK(80, new ImmutableItem(384, 100, 250)),
            RAW_SEA_TURTLE(79, new ImmutableItem(396, 80, 200)),
            RAW_MANTA_RAY(81, new ImmutableItem(390, 80, 160));
            private final int levelThreshold;
            private final ImmutableItem reward;

            private static final FishReward[] values = values();
        }
    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Unlock")) {
            if (!player.getInventory().containsItem(ItemId.BRIMSTONE_KEY, 1)) {
                player.sendMessage("You need a special key that'll fit that keyhole to unlock the chest.");
                return;
            }
            player.setAnimation(unlockAnim);
            player.getVarManager().sendBit(6583, 1);
            player.getInventory().deleteItem(ItemId.BRIMSTONE_KEY, 1);
            WorldTasksManager.schedule(() -> {
                ChestReward.randomReward(player).ifPresent(reward -> {
                    val price = reward.getSellPrice() * reward.getAmount();
                    player.getInventory().addOrDrop(reward);
                    player.getCollectionLog().add(reward);
                    player.getVarManager().sendBit(object.getDefinitions().getVarbitId(), 0);
                    player.sendMessage("You find some treasure in the chest!");
                    player.sendMessage(Colour.RED.wrap("Valuable drop: " + reward.getAmount() + " x " + reward.getName() + " (" + Utils.format(price) + " coins)"));
                    LootBroadcastPlugin.fireEvent(player.getName(), reward, player.getLocation(), false, false);
                    player.addAttribute("brimstone_chest_open_count", player.getNumericAttribute("brimstone_chest_open_count").intValue() + 1);
                    sendOpenedCount(player);
                });
            });
        } else if (option.equals("Check")) {
            sendOpenedCount(player);
        }
    }

    private static void sendOpenedCount(final Player player) {
        val opened = player.getNumericAttribute("brimstone_chest_open_count").intValue();
        player.sendMessage("You have opened the Brimstone chest " + (opened == 1 ? "once." : opened + " times."));
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{34662};
    }
}