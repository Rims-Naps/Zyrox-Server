package com.zenyte.game.content.chambersofxeric.rewards;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.GrandmasterTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.event.DoubleDropsManager;
import com.zenyte.game.content.follower.impl.BossPet;
import com.zenyte.game.content.treasuretrails.ClueItem;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.RequestResult;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.plugins.dialogue.PlainChat;
import com.zenyte.plugins.object.WellOfGoodwill;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

/**
 * @author Kris | 12. mai 2018 : 22:56:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public final class RaidRewards {

    /**
     * The max number of points that can be rolled against per roll(80% chance).
     */
    private static final float MAXIMUM_POINT_ROLL = 570_000F; //Old Value: 570000F

    /**
     * The max number of points to achieve 100% chance of getting reward based on {@link RaidRewards#MAXIMUM_POINT_ROLL}
     */
    private static final float MAXIMUM_POINT_ROLL_TOTAL = WellOfGoodwill.BONUSPURPLES ? 600_000F : 750_000F; //Old Value: 867500F
    /**
     * The raid for which we generate the rewards.
     */
    private final Raid raid;
    /**
     * A map of all the containers for each player.
     */
    @Getter
    private final Map<Player, Container> rewardMap;
    @Getter
    private final Map<String, List<Item>> originalRewards;
    /**
     * A duplicate list of raid players that's modified if a rare drop is given.
     */
    @Getter
    private final Set<Player> players;

    public RaidRewards(final Raid raid) {
        this.raid = raid;
        val size = raid.getPlayers().size();
        rewardMap = new Object2ObjectOpenHashMap<>(size);
        players = new ObjectOpenHashSet<>(size);
        originalRewards = new Object2ObjectOpenHashMap<>();
    }

    /**
     * Generates the rewards for all players, based on their and the raid's total points.
     */
    public void generate() {
        players.clear();
        players.addAll(raid.getPlayers());
        rewardMap.clear();
        raid.complete();
        val ticks = raid.getDuration();
        val seconds = TimeUnit.TICKS.toSeconds(ticks) % 60;
        val minutes = TimeUnit.TICKS.toMinutes(ticks);
        raid.getLevelCompletionMessages().put(0, Pair.of("Olm level: ", TimeUnit.TICKS.toMillis(ticks)));
        recordBottom();
        val inCMTime = raid.isChallengeMode() && raid.isMetamorphicDustEligible();
        boolean perfectOlm = true;
        for (val p : players) {
            if (!p.getBooleanAttribute("PerfectOlm")) {
                perfectOlm = false;
            }
        }
        for (val p : players) {
            var message = Colour.RS_PINK + "Congratulations - your raid is complete! Duration: " + Colour.RED + Utils.formatTime(minutes, seconds) + Colour.END;
            if (inCMTime) {
                p.sendMessage("Your team beat the challenge target time and you earned an extra 10,000 points.");
                raid.addPoints(p, 10000, true);
                if (!p.getBooleanAttribute("elite-combat-achievement65")) {
                    p.putBooleanAttribute("elite-combat-achievement65", true);
                    EliteTasks.sendEliteCompletion(p, 65);
                }
            }

            val pbKey = "coxpb" + getOriginalRewards().size() + raid.isChallengeMode();

            if (!p.getAttributes().containsKey(pbKey)) {
                p.getAttributes().put(pbKey, ticks);
                message += " (Personal Best!)";
            } else {
                int pbTicks = p.getNumericAttribute(pbKey).intValue();
                if (pbTicks > ticks) {
                    p.getAttributes().put(pbKey, ticks);
                    message += " (Personal Best!)";
                } else {
                    val pbSeconds = TimeUnit.TICKS.toSeconds(pbTicks) % 60;
                    val pbMinutes = TimeUnit.TICKS.toMinutes(pbTicks);
                    message += ". Personal Best: " + Colour.RED + Utils.formatTime(pbMinutes, pbSeconds) + Colour.END;
                }
            }
            p.sendMessage(message);
            if (!raid.isChallengeMode()) {
                p.addAttribute("chambersofxeric", p.getNumericAttribute("chambersofxeric").intValue() + 1);
                p.sendMessage("Your completed Chambers of Xeric count is: " + Colour.RED + p.getNumericAttribute("chambersofxeric").intValue() + Colour.END + ".");
            } else {
                p.addAttribute("challengechambersofxeric", p.getNumericAttribute("challengechambersofxeric").intValue() + 1);
                p.sendMessage("Your completed challenge mode Chambers of Xeric count is: " + Colour.RED + p.getNumericAttribute("challengechambersofxeric").intValue() + Colour.END + ".");
            }
            rewardMap.put(p, new Container(ContainerPolicy.ALWAYS_STACK, ContainerType.RAID_REWARDS, Optional.of(p)));
            if (raid.getDeaths().size() == 0
                    && !p.getBooleanAttribute("elite-combat-achievement45")) {
                p.putBooleanAttribute("elite-combat-achievement45", true);
                EliteTasks.sendEliteCompletion(p, 45);
            }
            if (raid.getOriginalPlayers().size() == 1
                    && Integer.parseInt(raid.getOlm().getTemporaryAttributes().get("special attacks during second to last phase").toString()) < 1
                    && !p.getBooleanAttribute("master-combat-achievement17")) {
                p.putBooleanAttribute("master-combat-achievement17", true);
                MasterTasks.sendMasterCompletion(p, 17);
            } else if (raid.getOriginalPlayers().size() == 1 && !p.getBooleanAttribute("master-combat-achievement17")) {
                p.sendMessage("Olm did " + raid.getOlm().getTemporaryAttributes().get("special attacks during second to last phase").toString() + " special attack(s) on you during the penultimate phase, therefore failing the A Not So Special Lizard Task.");
            }
            if (raid.getOriginalPlayers().size() == 1
                    && raid.getPoints(p) > 50000
                    && !p.getBooleanAttribute("master-combat-achievement18")) {
                p.putBooleanAttribute("master-combat-achievement18", true);
                MasterTasks.sendMasterCompletion(p, 18);
            }
            if (raid.getOriginalPlayers().size() == 1
                    && perfectOlm
                    && !p.getBooleanAttribute("master-combat-achievement26")) {
                p.putBooleanAttribute("master-combat-achievement26", true);
                MasterTasks.sendMasterCompletion(p, 26);
            }
            if (raid.getOriginalPlayers().size() != 1
                    && perfectOlm
                    && !p.getBooleanAttribute("master-combat-achievement27")) {
                p.putBooleanAttribute("master-combat-achievement27", true);
                MasterTasks.sendMasterCompletion(p, 27);
            }
            if (raid.getDeaths().size() == 0
                    && raid.getOriginalPlayers().size() == 1
                    && !p.getBooleanAttribute("master-combat-achievement29")) {
                p.putBooleanAttribute("master-combat-achievement29", true);
                MasterTasks.sendMasterCompletion(p, 29);
            }
            if (raid.getOriginalPlayers().size() == 1
                    && raid.getDeaths().size() == 0
                    && raid.isChallengeMode()
                    && !p.getBooleanAttribute("master-combat-achievement30")) {
                p.putBooleanAttribute("master-combat-achievement30", true);
                MasterTasks.sendMasterCompletion(p, 30);
            }
            if (raid.getDeaths().size() == 0
                    && raid.isChallengeMode()
                    && !p.getBooleanAttribute("master-combat-achievement31")) {
                p.putBooleanAttribute("master-combat-achievement31", true);
                MasterTasks.sendMasterCompletion(p, 31);
            }
            if (raid.getOriginalPlayers().size() == 3
                    && raid.getDuration() < 1800
                    && !p.getBooleanAttribute("master-combat-achievement40")) {
                p.putBooleanAttribute("master-combat-achievement40", true);
                MasterTasks.sendMasterCompletion(p, 40);
            }
            if (raid.getOriginalPlayers().size() == 1
                    && raid.getDuration() < 2100
                    && !p.getBooleanAttribute("master-combat-achievement41")) {
                p.putBooleanAttribute("master-combat-achievement41", true);
                MasterTasks.sendMasterCompletion(p, 41);
            }
            if (raid.getOriginalPlayers().size() == 3
                    && raid.isChallengeMode()
                    && raid.getDuration() < 4000
                    && !p.getBooleanAttribute("master-combat-achievement42")) {
                p.putBooleanAttribute("master-combat-achievement42", true);
                MasterTasks.sendMasterCompletion(p, 42);
            }
            if (raid.getOriginalPlayers().size() == 1
                    && raid.isChallengeMode()
                    && raid.getDuration() < 5000
                    && !p.getBooleanAttribute("master-combat-achievement43")) {
                p.putBooleanAttribute("master-combat-achievement43", true);
                MasterTasks.sendMasterCompletion(p, 43);
            }
            if (raid.getOriginalPlayers().size() == 3
                    && raid.getDuration() < 1650
                    && !p.getBooleanAttribute("grandmaster-combat-achievement23")) {
                p.putBooleanAttribute("grandmaster-combat-achievement23", true);
                GrandmasterTasks.sendGrandmasterCompletion(p, 23);
            }
            if (raid.getOriginalPlayers().size() == 1
                    && raid.getDuration() < 1950
                    && !p.getBooleanAttribute("grandmaster-combat-achievement24")) {
                p.putBooleanAttribute("grandmaster-combat-achievement24", true);
                GrandmasterTasks.sendGrandmasterCompletion(p, 24);
            }
            if (raid.getOriginalPlayers().size() == 3
                    && raid.isChallengeMode()
                    && raid.getDuration() < 3400
                    && !p.getBooleanAttribute("grandmaster-combat-achievement25")) {
                p.putBooleanAttribute("grandmaster-combat-achievement25", true);
                GrandmasterTasks.sendGrandmasterCompletion(p, 25);
            }
            if (raid.getOriginalPlayers().size() == 1
                    && raid.isChallengeMode()
                    && raid.getDuration() < 4500
                    && !p.getBooleanAttribute("grandmaster-combat-achievement26")) {
                p.putBooleanAttribute("grandmaster-combat-achievement26", true);
                GrandmasterTasks.sendGrandmasterCompletion(p, 26);
            }
        }
        if (raid.isChallengeMode()) {
            if (raid.isMetamorphicDustEligible()) {
                for (val player : players) {
                    if (Utils.secureRandom(199) == 0) {
                        val item = new Item(ItemId.METAMORPHIC_DUST);
                        player.getAttributes().put("coxrarereward", item);
                        //WorldBroadcasts.broadcast(player, BroadcastType.RARE_DROP, item, "Challenge Mode Chambers of Xeric");
                        addReward(player, item);
                    }
                    if (Utils.secureRandom(9) == 0) {
                        addReward(player, new Item(ItemId.CRYSTAL_OF_CALEN));
                    }
                }
            }
        }
        rollRareRewards();
        List<String> kitRecipeints = new ArrayList<>();
        for (val player : players) {
            if(!rewardMap.get(player).contains(new Item(ItemId.METAMORPHIC_DUST))) {
                if(Utils.secureRandom(125) == 0) {
                    val item = new Item(30526);
                    //WorldBroadcasts.broadcast(player, BroadcastType.RARE_DROP, item, (raid.isChallengeMode() ? "Challenge Mode " : "") + "Chambers of Xeric");
                    player.getAttributes().put("coxrarereward", item);
                    addReward(player, item);
                    kitRecipeints.add(player.getName());
                }
                if(Utils.secureRandom(115) == 0) {
                    val item = new Item(32106);
                    //WorldBroadcasts.broadcast(player, BroadcastType.RARE_DROP, item, (raid.isChallengeMode() ? "Challenge Mode " : "") + "Chambers of Xeric");
                    player.getAttributes().put("coxrarereward", item);
                    addReward(player, item);
                    kitRecipeints.add(player.getName());
                }
            }
            addRandomRewards(player);
            val rewards = rewardMap.get(player);
            if (rewards != null && !rewards.isEmpty()) {
                player.getVarManager().sendBit(5456, 1);
            }
        }
        players.addAll(raid.getPlayers());
        for (val reward : rewardMap.entrySet()) {
            val list = originalRewards.computeIfAbsent(reward.getKey().getUsername(), __ -> new ObjectArrayList<>());
            for (val itemEntry : reward.getValue().getItems().int2ObjectEntrySet()) {
                Item itemReward = itemEntry.getValue();
                list.add(new Item(itemReward));
            }
        }
    }

    /**
     * Records the bottom floor completion time into the logger.
     */
    private void recordBottom() {
        val millis = Utils.currentTimeMillis() - raid.getStartTime();
        val prefix = "Bottom";
        raid.getLevelCompletionMessages().put(0, Pair.of(prefix + " level: ", millis));
    }

    /**
     * Adds random non-rare rewards for the player requested.
     *
     * @param player the player whose container to fill with random rewards.
     */
    private void addRandomRewards(final Player player) {
        val tablet = !player.containsItem(21046)
                && !player.getAttributes().containsKey("xeric's honour")
                && Utils.secureRandom(10) == 0;
        if (tablet) {
            addReward(player, new Item(21046));
        }

        val points = raid.getPoints(player);
        val size = 2 - (tablet ? 1 : 0);
        for (int i = 0; i < size; i++) {
            final RaidReward reward = raid.isChallengeMode() ? ChallengeRaidNormalReward.random() : RaidNormalReward.random();
            val modifier = 131070F / reward.getMaximumAmount();
            val amount = Math.max(1, (int) (points / modifier));
            assert amount <= 65535;
            addReward(player, new Item(ItemDefinitions.getOrThrow(reward.getId()).getNotedOrDefault(), amount));
            val unnoted = new Item(ItemDefinitions.getOrThrow(reward.getId()).getUnnotedOrDefault(), amount);
            player.getCollectionLog().add(unnoted);
        }
        if (Utils.secureRandom(player.getBooleanAttribute("Obtained elite Ca Rewards") ? 10 : 11) == 0) {
			addReward(player, new Item(ClueItem.ELITE.getScrollBox()));
		}
        if (Utils.secureRandom(9) == 0) {
            addReward(player, new Item(ItemId.CRYSTAL_OF_CADARN));
        }
    }

    /**
     * Opens the rewards interface that's filled with their personal rewards.
     *
     * @param player the player who's opening the interface.
     */
    public void open(final Player player) {
        val container = rewardMap.get(player);
        if (container.isEmpty()) {
            player.getDialogueManager().start(new PlainChat(player, "The chest is empty."));
            return;
        }
        player.getVarManager().sendBit(5457, !player.containsItem(20899) && players.contains(player) ? 1 : 0);
        container.setFullUpdate(true);
        container.refresh(player);
        GameInterface.RAID_REWARDS.open(player);
    }

    /**
     * Attempts to roll for rare rewards using OSRS formulas.
     */
    private void rollRareRewards() {
        var points = (int) Math.min(raid.getTotalPoints(), MAXIMUM_POINT_ROLL * 3);
        val length = (int) Math.ceil(points / MAXIMUM_POINT_ROLL);
        var specialLoot = false;
        for (var i = 0; i < length; i++) {
            if (players.isEmpty()) {
                break;
            }
            val totalPoints = getTotalPoints();
            val currentPoints = Math.min(MAXIMUM_POINT_ROLL, points);
            val percentage = currentPoints / MAXIMUM_POINT_ROLL_TOTAL;
            points -= currentPoints;
            if (Utils.SECURE_RANDOM.nextDouble() > percentage) {
                continue;
            }

            val roll = Utils.secureRandom(totalPoints);
            try {
                val player = getPlayerAtPoints(roll);
                if (!specialLoot) {
                    specialLoot = true;
                    for (val p : raid.getPlayers()) {
                        p.sendMessage(Colour.RS_PINK.wrap("Special loot:"));
                    }
                }
                player.getVarManager().sendBit(5456, 2);
                val rareLoot = getRareReward();
                if(DoubleDropsManager.isDoubled(rareLoot.getId())) {
                    rareLoot.setAmount(rareLoot.getAmount() * 2);
                    player.sendMessage(Colour.RS_GREEN.wrap("You received a double drop!"));
                }
                addReward(player, rareLoot);
                /*
                for (val p : raid.getPlayers()) {
                    p.sendMessage(player.getName() + " - " + Colour.RED.wrap(rareLoot.getName()));
                }
                */
                val unnotedItem = new Item(rareLoot.getDefinitions().getUnnotedOrDefault(), rareLoot.getAmount());
                //WorldBroadcasts.broadcast(player, BroadcastType.RARE_DROP, unnotedItem, raid.isChallengeMode() ? "Challenge Mode Chambers of Xeric" : "Chambers of Xeric");
                player.getAttributes().put("coxrarereward", unnotedItem);
                val pet = BossPet.OLMLET.roll(player, 53);
                if (pet) {
                    originalRewards.computeIfAbsent(player.getUsername(), __ -> new ObjectArrayList<>()).add(new Item(ItemId.OLMLET));
                }
                players.remove(player);
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
        }
    }

    /**
     * Adds a reward to the requested player's container.
     *
     * @param player the player whose container to enqueue the reward to.
     * @param reward the item to enqueue to the container.
     */
    private void addReward(final Player player, final Item reward) {
        val container = rewardMap.get(player);
        val result = container.add(reward);
        if (result.getResult() != RequestResult.SUCCESS) {
            System.err.println("Failure to successfully add reward: " + reward + "\n" + result);
        }
    }

    /**
     * Gets the player at the randomly rolled points index. The roll value will be between 0 and total points of the raid. It will loop over
     * the players until the current roll stack exceeds or equals the roll.
     *
     * @param roll the number rolled, from 0 to total points in raid.
     * @return the lucky player.
     */
    private Player getPlayerAtPoints(final int roll) {
        var currentRoll = 0;
        for (val player : players) {
            if ((currentRoll += raid.getPoints(player)) < roll) {
                continue;
            }
            return player;
        }
        throw new IllegalStateException();
    }

    /**
     * Gets the total points of all the players remaining in the set combined.
     *
     * @return total points combined.
     */
    private int getTotalPoints() {
        var amount = 0;
        for (val player : players) {
            amount += raid.getPoints(player);
        }
        return amount;
    }

    /**
     * Gets a random rare reward based on their own individual weights defined in RaidReward enum.
     *
     * @return a random rare reward.
     */
    private Item getRareReward() {
        val random = Utils.secureRandom(RaidRareReward.TOTAL_WEIGHT);
        var roll = 0;
        for (val reward : RaidRareReward.values) {
            if ((roll += reward.getWeight()) < random) {
                continue;
            }
            val item = reward.getItem();
            return new Item(item.getDefinitions().getNotedOrDefault(), item.getAmount());
        }
        throw new IllegalStateException();
    }

}
