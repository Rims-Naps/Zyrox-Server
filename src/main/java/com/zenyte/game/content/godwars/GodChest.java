package com.zenyte.game.content.godwars;

import com.zenyte.game.item.ImmutableItem;
import com.zenyte.game.item.Item;
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
public class GodChest implements ObjectAction {

    private static final Animation unlockAnim = new Animation(832);

    @RequiredArgsConstructor
    private enum ChestReward {



        NOTED_SUPER_COMBAT_3(20, new ImmutableItem(12698, 25, 50)),
        NOTED_BASTION_POTION_3(20, new ImmutableItem(22465, 15, 25)),
        NOTED_GRIMY_TOADFLAX(20, new ImmutableItem(2999, 50, 100)),
        NOTED_SNAPDRAGON(20, new ImmutableItem(3001, 50, 115)),
        BONES_TO_PEACHES(20, new ImmutableItem(8015, 25, 50)),
        NOTED_DRAGON_BONES(20, new ImmutableItem(537, 60, 90)),
        BLACKCHINS(20, new ImmutableItem(11959, 75, 150)),
        ANGLERFISH(20, new ImmutableItem(13442, 50, 100)),
        DRAGON_BOOTS(30, new ImmutableItem(11840, 1, 1)),
        DRAGONSTONE(20, new ImmutableItem(1616, 5, 5)),
        RUNE_SWORD(20, new ImmutableItem(1290, 5, 10)),
        LIMP(30, new ImmutableItem(226, 100, 150)),
        RED_SPIDER_EGGS(30, new ImmutableItem(224, 100, 150)),
        WHITEBERRIES(30, new ImmutableItem(240, 100, 150)),
        BLUE_DRAGON_SCALES(30, new ImmutableItem(242, 100, 150)),
        WINES_OF_ZAMORAK(30, new ImmutableItem(246, 100, 150)),
        CRYSTAL_SHARDS(40, new ImmutableItem(30560, 400, 500)),
        ENCHANCED_CRYSTAL_KEY(60, new ImmutableItem(30588, 1, 2)),
        SARADOMIN_SWORD(200, new ImmutableItem(11838, 1, 1)),
        SARADOMIN_LIGHT(200, new ImmutableItem(13256, 1, 1)),
        ARMADYL_CROSSBOW(225, new ImmutableItem(11785, 1, 1)),
        SARADOMIN_HILT(225, new ImmutableItem(11814, 1, 1)),
        ARMADYL_HELMET(225, new ImmutableItem(11826, 1, 1)),
        ARMADYL_CHESTPLATE(225, new ImmutableItem(11828, 1, 1)),
        ARMADYL_CHAINSKIRT(225, new ImmutableItem(11830, 1, 1)),
        ARMADYL_HILT(225, new ImmutableItem(11810, 1, 1)),
        BANDOS_CHESTPLATE(225, new ImmutableItem(11832, 1, 1)),
        BANDOS_TASSETS(225, new ImmutableItem(11834, 1, 1)),
        BANDOS_BOOTS(200, new ImmutableItem(11836, 1, 1)),
        BANDOS_HILT(225, new ImmutableItem(11812, 1, 1)),
        STEAM_BATTLESTAFF(200, new ImmutableItem(11787, 1, 1)),
        ZAMORAKIAN_SPEAR(225, new ImmutableItem(11824, 1, 1)),
        STAFF_OF_THE_DEAD(225, new ImmutableItem(11791, 1, 1)),
        ZAMORAK_HILT(225, new ImmutableItem(11816, 1, 1)),
        GODSWORD_SHARD_1(150, new ImmutableItem(11818, 1, 1)),
        GODSWORD_SHARD_2(150, new ImmutableItem(11820, 1, 1)),
        GODSWORD_SHARD_3(150, new ImmutableItem(11822, 1, 1)),
        SNAPE_GRASS(20, new ImmutableItem(232, 60, 120));


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
                    if (item.getId() == 11816 || item.getId() == 11791 || item.getId() == 11824 || item.getId() == 11812 || item.getId() == 11836 || item.getId() == 11834 || item.getId() == 11832 ||
                            item.getId() == 11810 || item.getId() == 11830 || item.getId() == 11828 || item.getId() == 11826 || item.getId() == 11814 || item.getId() == 11785) {
                        WorldBroadcasts.broadcast(player, BroadcastType.CHEST, item.getId(), "God's Chest");
                    }
                    player.getCollectionLog().add(new Item(item.getId()));
                    return Optional.of(new Item(item.getId(), Utils.random(item.getMinAmount(), item.getMaxAmount())));
                }
            }
            return Optional.empty();
        }

    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Open")) {
            if (!player.getInventory().containsItem(30915, 1)) {
                player.sendMessage("You will need a key dropped from one of the bosses from the Godwars Dungeon.");
                return;
            }
            player.setAnimation(unlockAnim);
            player.getVarManager().sendBit(6583, 1);
            player.getInventory().deleteItem(30915, 1);
            WorldTasksManager.schedule(() -> {
                ChestReward.randomReward(player).ifPresent(reward -> {
                    val price = reward.getSellPrice() * reward.getAmount();
                    player.getInventory().addOrDrop(reward);
                    player.getVarManager().sendBit(object.getDefinitions().getVarbitId(), 0);
                    player.sendMessage("You find some treasure in the chest!");
                    player.sendMessage(Colour.RED.wrap("Valuable drop: " + reward.getAmount() + " x " + reward.getName() + " (" + Utils.format(price) + " coins)"));
                    LootBroadcastPlugin.fireEvent(player.getName(), reward, player.getLocation(), false, false);
                    player.addAttribute("godwars_resource_chest_open_count", player.getNumericAttribute("godwars_resource_chest_open_count").intValue() + 1);
                    sendOpenedCount(player);
                });
            });
        } else if (option.equals("Check")) {
            sendOpenedCount(player);
        }
    }

    private static void sendOpenedCount(final Player player) {
        val opened = player.getNumericAttribute("godwars_resource_chest_open_count").intValue();
        player.sendMessage("You have opened the Chest of the God's " + (opened == 1 ? "once." : opened + " times."));
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{38500};
    }
}
