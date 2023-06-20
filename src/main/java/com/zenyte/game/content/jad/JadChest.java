package com.zenyte.game.content.jad;

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
 *  Created by Matt on 3/1/2022
 */
public class JadChest implements ObjectAction {

    private static final Animation unlockAnim = new Animation(832);

    @RequiredArgsConstructor
    private enum ChestReward {

        UNCUT_DIAMOND(12, new ImmutableItem(1618, 50, 100)),
        UNCUT_RUBY(12, new ImmutableItem(1620, 50, 100)),
        UNCUT_EMERALD(12, new ImmutableItem(1622, 50, 100)),
        UNCUT_SAPPHIRE(12, new ImmutableItem(1624, 50, 100)),
        STEEL_BARS(15, new ImmutableItem(2354, 100, 200)),
        ADDY_BARS(15, new ImmutableItem(2362, 100, 200)),
        RUNE_BARS(15, new ImmutableItem(2364, 100, 200)),
        LIMP(30, new ImmutableItem(226, 100, 150)),
        RED_SPIDER_EGGS(30, new ImmutableItem(224, 100, 150)),
        WHITEBERRIES(30, new ImmutableItem(240, 100, 150)),
        BLUE_DRAGON_SCALES(30, new ImmutableItem(242, 100, 150)),
        WINES_OF_ZAMORAK(30, new ImmutableItem(246, 100, 150)),
        DRAGON_ARROWTIPS(30, new ImmutableItem(11237, 100, 250)),
        DRAGON_DART_TIP(30, new ImmutableItem(11232, 100, 250)),
        CRYSTAL_SHARDS(40, new ImmutableItem(30560, 400, 500)),
        TOKTZ_XIL_UL(40, new ImmutableItem(6522, 200, 400)),
        TOKTZ_MEJ_TAL(40, new ImmutableItem(6528, 1, 2)),
        TOKTZ_KET_VIL(40, new ImmutableItem(6526, 1, 2)),
        TZHAAR_KET_EM(40, new ImmutableItem(6527, 1, 2)),
        OBSIDIAN_CAPE(40, new ImmutableItem(6568, 1, 2)),

        IMBUE_SCROLL(108, new ImmutableItem(30910, 1, 1));


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
         * Rolls a random reward out of all the molten chest rewards.
         *
         * @param player the player who is rolling the chest.
         * @return an optional item reward.
         */
        private static final Optional<Item> randomReward(@NotNull final Player player) {
            val random = Utils.random(TOTAL_WEIGHT);
            int current = 0;

            for (val reward : rewards.entrySet()) {
                val item = reward.getValue();
                if ((current += (int) item.getRate()) >= random) {
                    if (item.getId() == 30910) {
                        WorldBroadcasts.broadcast(player, BroadcastType.CHEST, item.getId(), "Molten Chest");
                    }
                    return Optional.of(new Item(item.getId(), Utils.random(item.getMinAmount(), item.getMaxAmount())));
                }
            }
            return Optional.empty();
        }
    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Collect")) {
            if (!player.getInventory().containsItem(30912, 1)) {
                player.sendMessage("You will need a key from the fight caves to unlock this chest.");
                return;
            }
            player.setAnimation(unlockAnim);
            player.getVarManager().sendBit(6583, 1);
            player.getInventory().deleteItem(30912, 1);
            WorldTasksManager.schedule(() -> {
                ChestReward.randomReward(player).ifPresent(reward -> {
                    val price = reward.getSellPrice() * reward.getAmount();
                    player.getInventory().addOrDrop(reward);
                    player.getCollectionLog().add(reward);
                    player.getVarManager().sendBit(object.getDefinitions().getVarbitId(), 0);
                    player.sendMessage("You find some treasure in the chest!");
                    player.sendMessage(Colour.RED.wrap("Valuable drop: " + reward.getAmount() + " x " + reward.getName() + " (" + Utils.format(price) + " coins)"));
                    if (Utils.secureRandom(14) == 0) {
                        player.getInventory().addOrDrop(ItemId.CRYSTAL_OF_HEFIN);
                        player.sendMessage(Colour.RED.wrap("You find a Crystal of Hefin."));
                    }
                    LootBroadcastPlugin.fireEvent(player.getName(), reward, player.getLocation(), false, false);
                    player.addAttribute("jad_resource_chest_open_count", player.getNumericAttribute("jad_resource_chest_open_count").intValue() + 1);
                    sendOpenedCount(player);
                });
            });
        } else if (option.equals("Check")) {
            sendOpenedCount(player);
        }
    }

    private static void sendOpenedCount(final Player player) {
        val opened = player.getNumericAttribute("jad_resource_chest_open_count").intValue();
        player.sendMessage("You have opened the Molten chest " + (opened == 1 ? "once." : opened + " times."));
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{40036};
    }
}
