package com.zenyte.game.content.larran;

import com.zenyte.game.item.ImmutableItem;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
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
public class LarranChest implements ObjectAction {

    private static final Animation unlockAnim = new Animation(832);

    @RequiredArgsConstructor
    private enum ChestReward {

        UNCUT_DIAMOND(12, new ImmutableItem(1618, 35, 45)),
        UNCUT_RUBY(12, new ImmutableItem(1620, 35, 45)),
        //COAL(12, new ImmutableItem(454, 450, 650)),
        COINS(12, new ImmutableItem(995, 400000, 800000)),
        GOLD_ORE(15, new ImmutableItem(445, 150, 250)),
        DRAGON_ARROWTIPS(15, new ImmutableItem(11237, 100, 250)),
        PURE_ESSENSE(20, new ImmutableItem(7937, 4000, 7000)),
        RUNE_FULL_HELM(20, new ImmutableItem(1164, 3, 5)),
        RUNE_PLATEBODY(20, new ImmutableItem(1128, 2, 3)),
        RUNE_PLATELEGS(20, new ImmutableItem(1080, 2, 3)),

        RUNITE_ORE(30, new ImmutableItem(452, 15, 25)),
        STEEL_BAR(30, new ImmutableItem(2354, 350, 550)),
        MAGIC_LOGS(30, new ImmutableItem(1514, 180, 220)),
        DRAGON_DART_TIP(30, new ImmutableItem(11232, 80, 250)),
        PALM_TREE_SEED(60, new ImmutableItem(5289, 3, 5)),
        MAGIC_SEED(60, new ImmutableItem(5316, 3, 4)),
        CELASTRUS_SEED(60, new ImmutableItem(22869, 3, 5)),
        DRAGONFRUIT_TREE_SEED(60, new ImmutableItem(22877, 3, 5)),
        REDWOOD_TREE_SEED(60, new ImmutableItem(22871, 1, 1)),
        TORSTOL_SEED(60, new ImmutableItem(5304, 4, 6)),
        SNAPDRAGON_SEED(60, new ImmutableItem(5300, 4, 6)),
        RANARR_SEED(60, new ImmutableItem(5295, 4, 6)),

        DRAGON_HARPOON(200, new ImmutableItem(21028, 1, 1)),
        MAGE_BOOK(200, new ImmutableItem(6889, 1, 1)),
        MASTER_WAND(200, new ImmutableItem(6914, 1, 1)),
        SLAYER_SKIP_SCROLLS(60, new ImmutableItem(30568, 5, 10)),

        DAGONHAI_HAT(800, new ImmutableItem(30528, 1, 1)),
        DAGONHAI_ROBE_TOP(800, new ImmutableItem(30530, 1, 1)),
        DAGONHAI_ROBE_BOTTOm(800, new ImmutableItem(30532, 1, 1));

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
                    if (item.getId() == 30528 || item.getId() == 30530 || item.getId() == 30532) {
                        WorldBroadcasts.broadcast(player, BroadcastType.CHEST, item.getId(), "Larran's Chest");
                    }
                    return Optional.of(new Item(item.getId(), Utils.random(item.getMinAmount(), item.getMaxAmount())));
                }
            }
            return Optional.empty();
        }

    }



    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Open")) {
            if (!player.getInventory().containsItem(30540, 1)) {
                player.sendMessage("You will need the key of Larran to unlock this chest.");
                return;
            }
            player.setAnimation(unlockAnim);
            player.getVarManager().sendBit(6583, 1);
            player.getInventory().deleteItem(30540, 1);
            WorldTasksManager.schedule(() -> {
                ChestReward.randomReward(player).ifPresent(reward -> {
                    val price = reward.getSellPrice() * reward.getAmount();
                    player.getInventory().addOrDrop(reward);
                    player.getVarManager().sendBit(object.getDefinitions().getVarbitId(), 0);
                    player.sendMessage("You find some treasure in the chest!");
                    player.sendMessage(Colour.RED.wrap("Valuable drop: " + reward.getAmount() + " x " + reward.getName() + " (" + Utils.format(price) + " coins)"));
                    LootBroadcastPlugin.fireEvent(player.getName(), reward, player.getLocation(), false, false);
                    player.addAttribute("larran_chest_open_count", player.getNumericAttribute("larran_chest_open_count").intValue() + 1);
                    sendOpenedCount(player);
                });
            });
        } else if (option.equals("Check")) {
            sendOpenedCount(player);
        }
    }

    private static void sendOpenedCount(final Player player) {
        val opened = player.getNumericAttribute("larran_chest_open_count").intValue();
        player.sendMessage("You have opened Larran's chest " + (opened == 1 ? "once." : opened + " times."));
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{40000};
    }
}
