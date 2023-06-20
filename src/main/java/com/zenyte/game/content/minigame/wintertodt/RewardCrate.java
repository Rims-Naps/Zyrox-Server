package com.zenyte.game.content.minigame.wintertodt;

import com.zenyte.game.content.follower.impl.BossPet;
import com.zenyte.game.content.skills.farming.FarmingProduct;
import com.zenyte.game.content.skills.farming.Seedling;
import com.zenyte.game.content.skills.woodcutting.AxeDefinitions;
import com.zenyte.game.item.ImmutableItem;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.item.LightSourceItem;
import com.zenyte.plugins.item.TomeOfFire;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardCrate extends ItemPlugin {
    
    public static final int PYROMANCER_HOOD = 20708;
    public static final int PYROMANCER_GARB = 20704;
    public static final int PYROMANCER_ROBE = 20706;
    public static final int PYROMANCER_BOOTS = 20710;
    
    public static final int PHOENIX_PET_CHANCE = 3750;
    static final int SUPPLY_CRATE = 20703;
    static final int WARM_GLOVES = 20712;
    private static final int DRAGON_AXE_CHANCE = 7500;
    private static final int TOME_OF_FIRE_CHANCE = 750;
    private static final int WARM_GLOVES_CHANCE = 65;
    private static final int BRUMA_TORCH_CHANCE = 65;
    private static final int PYROMANCER_OUTFIT_PIECE_CHANCE = 75;
    
    private static void openCrate(final Player player, final int rolls, final Item crate, final int slotId) {
        if (player.getInventory().getItem(slotId) != crate) {
            return;
        }
        val rewards = new ObjectArrayList<Item>(rolls);
        val rewardNames = new ObjectArrayList<String>(rolls);
        
        for (int i = 0; i < rolls; i++) {
            val reward = rollReward(player);
            if (reward == null) { // if phoenix pet
                continue;
            }
            
            rewards.add(reward);
            rewardNames.add(reward.getName() + " x " + reward.getAmount());
            player.getCollectionLog().add(reward);
        }
        
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                plain("You have earned:  " + String.join(",  ", rewardNames) + ".");
            }
        });
        
        player.getInventory().deleteItem(slotId, crate);
        player.getInventory().addOrDrop(rewards.toArray(new Item[0]));
    }
    
    /**
     * @see <a href="https://oldschool.runescape.wiki/w/Supply_crate#Drop_mechanic">Supply crate drop mechanic</a>
     */
    @Nullable
    private static Item rollReward(final Player player) {
        if (Utils.random(DRAGON_AXE_CHANCE) == 1) {
            return new Item(AxeDefinitions.DRAGON.getItemId());
        } else if (BossPet.PHOENIX.roll(player, PHOENIX_PET_CHANCE)) {
            return null;
        } else if (Utils.random(TOME_OF_FIRE_CHANCE) == 1) {
            return new Item(TomeOfFire.TOME_OF_FIRE_EMPTY);
        } else if (Utils.random(WARM_GLOVES_CHANCE) == 1) {
            // give the player 2 magic seeds if they already have 3+ warm gloves
            if (player.getAmountOf(WARM_GLOVES) >= 3) {
                return new Item(Seedling.MAGIC.getSeed(), 2);
            } else {
                return new Item(WARM_GLOVES);
            }
        } else if (Utils.random(BRUMA_TORCH_CHANCE) == 1) {
            // give the player 2 torstol seeds if they already have 3+ bruma torches
            if (player.getAmountOf(LightSourceItem.LightSource.BRUMA_TORCH.getLitId()) >= 3) {
                return new Item(FarmingProduct.TORSTOL.getSeed().getId(), 2);
            } else {
                return new Item(LightSourceItem.LightSource.BRUMA_TORCH.getLitId());
            }
        } else if (Utils.random(PYROMANCER_OUTFIT_PIECE_CHANCE) == 1) {
            // The piece given is the piece the player has least of, ties are broken in this order: Garb, hood, robe, and boots.
            val pieces = new Int2IntOpenHashMap(3);
            pieces.addTo(PYROMANCER_ROBE, player.getAmountOf(PYROMANCER_ROBE));
            pieces.addTo(PYROMANCER_HOOD, player.getAmountOf(PYROMANCER_HOOD));
            pieces.addTo(PYROMANCER_GARB, player.getAmountOf(PYROMANCER_GARB));
            
            var smallestAmountItemId = PYROMANCER_BOOTS;
            var smallestAmountItemAmount = player.getAmountOf(PYROMANCER_BOOTS);
            for (val entry : pieces.int2IntEntrySet()) {
                if (entry.getIntValue() <= smallestAmountItemAmount) {
                    smallestAmountItemId = entry.getIntKey();
                    smallestAmountItemAmount = entry.getIntValue();
                }
            }
            
            return new Item(smallestAmountItemId);
        }
        
        return Utils.getRandomCollectionElement(SupplyType.WEIGHTED_TABLE).rollSupplyDrop(player);
    }
    
    @Override
    public void handle() {
        bind("Open", (player, item, slotId) -> {
            var rolls = item.getNumericAttribute("rolls").intValue();
            if (rolls <= 0) {
                val newCrate = Wintertodt.generateSupplyCrate(player, Utils.random(500, 750));
                rolls = newCrate.getNumericAttribute("rolls").intValue();
            }
            int finalRolls = rolls;
    
            if (player.getInventory().getFreeSlots() < rolls) {
                player.getDialogueManager().start(new Dialogue(player) {
                    @Override
                    public void buildDialogue() {
                        plain("You don't have enough free space to open the supply crate. You'll<br><br>need " + finalRolls + " spaces.");
                        options("Items may fall to the ground and be lost.",
                                new DialogueOption("Open the crate anyway", () -> openCrate(player, finalRolls, item, slotId)),
                                new DialogueOption("Don't open the crate"));
                    }
                });
                return;
            }
            openCrate(player, rolls, item, slotId);
        });
    }
    
    @Override
    public int[] getItems() {
        return new int[]{SUPPLY_CRATE};
    }
    
    @Getter
    @RequiredArgsConstructor
    private enum SupplyType {
        LOGS(5),
        GEMS(5),
        ORES(5),
        HERBS(5),
        
        HERBLORE_SECONDARY(3),
        
        TREE_SEEDS(1),
        HERB_SEEDS(3),
        OTHER_SEEDS(2),
        
        FISH(5),
        OTHER(15);
        
        public static final SupplyType[] VALUES = values();
        public static final List<SupplyType> WEIGHTED_TABLE = new ArrayList<>();
        
        static {
            for (val supplyType : VALUES) {
                for (int i = 0; i < supplyType.getWeight(); i++) {
                    WEIGHTED_TABLE.add(supplyType);
                }
            }
        }
        
        private final int weight;
        
        public Item rollSupplyDrop(final Player player) {
            val special = rollSpecial();
            if (special != null) {
                return special;
            }
            
            val supplyTypeRewards = CrateReward.rewardsBySupplyType.get(this);
            val rewardsWithSufficientSkillLevel = new ArrayList<CrateReward>();
            
            for (final CrateReward reward : supplyTypeRewards) { // filter out rewards the player cannot get
                if (reward.getSkillId() == -1 || player.getSkills().getLevelForXp(reward.getSkillId()) >= reward.getMinimumSkillLevelRequired()) {
                    rewardsWithSufficientSkillLevel.add(reward);
                }
            }
            
            val weightedItemPool = new ArrayList<ImmutableItem>(100);
            for (val reward : rewardsWithSufficientSkillLevel) {
                val weight = reward.getMinimumSkillLevelRequired() == -1 ? 1 : Math.pow(reward.getMinimumSkillLevelRequired(), 1.1);
                for (int i = 0; i < weight; i++) {
                    weightedItemPool.add(reward.getItem());
                }
            }
            
            val finalItem = Utils.getRandomCollectionElement(weightedItemPool);
            return new Item(finalItem.getId(), Utils.random(finalItem.getMinAmount(), finalItem.getMaxAmount()));
        }
        
        @Nullable
        private Item rollSpecial() {
            if (this == OTHER_SEEDS) {
                if (Utils.random(20) == 1) { // spirit seed
                    return new Item(5317, Utils.random(1, 2));
                }
            }
            return null;
        }
        
    }
    
    @Getter
    @RequiredArgsConstructor
    private enum CrateReward {
        OAK_LOGS(new ImmutableItem(1522, 15, 25), Skills.WOODCUTTING, 1, SupplyType.LOGS),
        WILLOW_LOGS(new ImmutableItem(1520, 15, 25), Skills.WOODCUTTING, 15, SupplyType.LOGS),
        TEAK_LOGS(new ImmutableItem(6334, 15, 25), Skills.WOODCUTTING, 20, SupplyType.LOGS),
        MAPLE_LOGS(new ImmutableItem(1518, 15, 25), Skills.WOODCUTTING, 30, SupplyType.LOGS),
        MAHOGANY_LOGS(new ImmutableItem(8836, 15, 25), Skills.WOODCUTTING, 35, SupplyType.LOGS),
        YEW_LOGS(new ImmutableItem(1516, 15, 25), Skills.WOODCUTTING, 45, SupplyType.LOGS),
        MAGIC_LOGS(new ImmutableItem(1514, 15, 25), Skills.WOODCUTTING, 60, SupplyType.LOGS),
        
        UNCUT_RED_TOPAZ(new ImmutableItem(1630, 2, 5), Skills.CRAFTING, 1, SupplyType.GEMS),
        UNCUT_SAPPHIRE(new ImmutableItem(1624, 2, 5), Skills.CRAFTING, 10, SupplyType.GEMS),
        UNCUT_EMERALD(new ImmutableItem(1622, 2, 5), Skills.CRAFTING, 17, SupplyType.GEMS),
        UNCUT_RUBY(new ImmutableItem(1620, 3, 6), Skills.CRAFTING, 24, SupplyType.GEMS),
        UNCUT_DIAMOND(new ImmutableItem(1618, 2, 5), Skills.CRAFTING, 33, SupplyType.GEMS),
        UNCUT_DRAGONSTONE(new ImmutableItem(1632, 1, 3), Skills.CRAFTING, 50, SupplyType.GEMS),
        
        LIMESTONE(new ImmutableItem(3212, 6, 9), Skills.MINING, 1, SupplyType.ORES),
        IRON_ORE(new ImmutableItem(441, 8, 22), Skills.MINING, 5, SupplyType.ORES),
        SILVER_ORE(new ImmutableItem(443, 8, 22), Skills.MINING, 10, SupplyType.ORES),
        COAL(new ImmutableItem(454, 15, 20), Skills.MINING, 20, SupplyType.ORES),
        GOLD_ORE(new ImmutableItem(445, 12, 15), Skills.MINING, 30, SupplyType.ORES),
        MITHRIL_ORE(new ImmutableItem(448, 5, 8), Skills.MINING, 45, SupplyType.ORES),
        ADAMANTITE_ORE(new ImmutableItem(450, 3, 4), Skills.MINING, 60, SupplyType.ORES),
        RUNITE_ORE(new ImmutableItem(452, 2, 3), Skills.MINING, 75, SupplyType.ORES),
        
        GRIMY_GUAM(new ImmutableItem(200, 5, 8), Skills.HERBLORE, 1, SupplyType.HERBS),
        GRIMY_MARRENTILL(new ImmutableItem(202, 5, 8), Skills.HERBLORE, 1, SupplyType.HERBS),
        GRIMY_TARROMIN(new ImmutableItem(204, 5, 8), Skills.HERBLORE, 5, SupplyType.HERBS),
        GRIMY_HARRALANDER(new ImmutableItem(206, 5, 8), Skills.HERBLORE, 10, SupplyType.HERBS),
        GRIMY_RANARR_WEED(new ImmutableItem(208, 2, 4), Skills.HERBLORE, 15, SupplyType.HERBS),
        GRIMY_TOADFLAX(new ImmutableItem(3050, 2, 4), Skills.HERBLORE, 20, SupplyType.HERBS),
        GRIMY_IRIT_LEAF(new ImmutableItem(210, 5, 7), Skills.HERBLORE, 30, SupplyType.HERBS),
        GRIMY_AVANTOE(new ImmutableItem(212, 5, 7), Skills.HERBLORE, 38, SupplyType.HERBS),
        GRIMY_KWUARM(new ImmutableItem(214, 3, 5), Skills.HERBLORE, 54, SupplyType.HERBS),
        GRIMY_SNAPDRAGON(new ImmutableItem(3052, 2, 4), Skills.HERBLORE, 49, SupplyType.HERBS),
        GRIMY_CADANTINE(new ImmutableItem(216, 3, 5), Skills.HERBLORE, 55, SupplyType.HERBS),
        GRIMY_LANTADYME(new ImmutableItem(2486, 3, 5), Skills.HERBLORE, 57, SupplyType.HERBS),
        GRIMY_DWARF_WEED(new ImmutableItem(218, 3, 5), Skills.HERBLORE, 60, SupplyType.HERBS),
        GRIMY_TORSTOL(new ImmutableItem(220, 2, 4), Skills.HERBLORE, 65, SupplyType.HERBS),
        
        RAW_ANCHOVIES(new ImmutableItem(322, 9, 15), Skills.FISHING, 1, SupplyType.FISH),
        RAW_TROUT(new ImmutableItem(336, 9, 15), Skills.FISHING, 10, SupplyType.FISH),
        RAW_SALMON(new ImmutableItem(332, 9, 15), Skills.FISHING, 20, SupplyType.FISH),
        RAW_TUNA(new ImmutableItem(360, 9, 15), Skills.FISHING, 25, SupplyType.FISH),
        RAW_LOBSTER(new ImmutableItem(378, 9, 15), Skills.FISHING, 30, SupplyType.FISH),
        RAW_SWORDFISH(new ImmutableItem(372, 9, 15), Skills.FISHING, 40, SupplyType.FISH),
        RAW_KARAMBWAN(new ImmutableItem(3143, 9, 15), Skills.FISHING, 55, SupplyType.FISH),
        RAW_SHARK(new ImmutableItem(384, 9, 15), Skills.FISHING, 66, SupplyType.FISH),
        RAW_MANTA_RAY(new ImmutableItem(390, 7, 13), Skills.FISHING, 71, SupplyType.FISH),
        RAW_ANGLER(new ImmutableItem(13440, 7, 13), Skills.FISHING, 72, SupplyType.FISH),
        
        COINS(new ImmutableItem(995, 5000, 12500), -1, 1, SupplyType.OTHER),
        SALTPETRE(new ImmutableItem(13422, 5, 7), -1, 1, SupplyType.OTHER),
        BURNT_PAGE(new ImmutableItem(20718, 7, 28), -1, 1, SupplyType.OTHER),
        PURE_ESSENCE(new ImmutableItem(7937, 70, 250), -1, 1, SupplyType.OTHER),
        
        CHOCOLATE_DUST(new ImmutableItem(1976, 5, 16), Skills.HERBLORE, 15, SupplyType.HERBLORE_SECONDARY),
        CRUSHED_NEST(new ImmutableItem(6694, 2, 8), Skills.HERBLORE, 71, SupplyType.HERBLORE_SECONDARY),
        CRUSHED_SUPERIOR_DRAGON_BONES(new ImmutableItem(21976, 2, 8), Skills.HERBLORE, 82, SupplyType.HERBLORE_SECONDARY),
        DRAGON_SCALE_DUST(new ImmutableItem(242, 5, 16), Skills.HERBLORE, 50, SupplyType.HERBLORE_SECONDARY),
        GOAT_HORN_DUST(new ImmutableItem(9737, 5, 16), Skills.HERBLORE, 26, SupplyType.HERBLORE_SECONDARY),
        LAVA_SCALE_SHARD(new ImmutableItem(11994, 5, 16), Skills.HERBLORE, 74, SupplyType.HERBLORE_SECONDARY),
        LIMPWURT_ROOT(new ImmutableItem(226, 5, 16), Skills.HERBLORE, 1, SupplyType.HERBLORE_SECONDARY),
        MORT_MYRE_FUNGUS(new ImmutableItem(2971, 3, 11), Skills.HERBLORE, 42, SupplyType.HERBLORE_SECONDARY),
        POTATO_CACTUS(new ImmutableItem(3139, 5, 16), Skills.HERBLORE, 66, SupplyType.HERBLORE_SECONDARY),
        RED_SPIDERS_EGGS(new ImmutableItem(224, 5, 16), Skills.HERBLORE, 12, SupplyType.HERBLORE_SECONDARY),
        SNAPE_GRASS(new ImmutableItem(232, 3, 11), Skills.HERBLORE, 28, SupplyType.HERBLORE_SECONDARY),
        TOADS_LEGS(new ImmutableItem(2153, 5, 16), Skills.HERBLORE, 24, SupplyType.HERBLORE_SECONDARY),
        UNICORN_HORN_DUST(new ImmutableItem(236, 5, 16), Skills.HERBLORE, 48, SupplyType.HERBLORE_SECONDARY),
        WHITE_BERRIES(new ImmutableItem(240, 5, 16), Skills.HERBLORE, 20, SupplyType.HERBLORE_SECONDARY),
        WINE_OF_ZAMORAK(new ImmutableItem(246, 3, 11), Skills.HERBLORE, 62, SupplyType.HERBLORE_SECONDARY),
        ZAMORAKS_GRAPES(new ImmutableItem(20750, 6, 22), Skills.HERBLORE, 62, SupplyType.HERBLORE_SECONDARY),
        
        ACORN(new ImmutableItem(5312, 3, 7), Skills.FARMING, 1, SupplyType.TREE_SEEDS),
        WILLOW_SEED(new ImmutableItem(5313, 2, 6), Skills.FARMING, 20, SupplyType.TREE_SEEDS),
        MAPLE_SEED(new ImmutableItem(5314, 2, 5), Skills.FARMING, 35, SupplyType.TREE_SEEDS),
        YEW_SEED(new ImmutableItem(5315, 2, 3), Skills.FARMING, 50, SupplyType.TREE_SEEDS),
        MAGIC_SEED(new ImmutableItem(5316, 2, 4), Skills.FARMING, 65, SupplyType.TREE_SEEDS),
        
        GUAM_SEED(new ImmutableItem(5291, 2, 4), Skills.FARMING, 1, SupplyType.HERB_SEEDS),
        HARRALANDER_SEED(new ImmutableItem(5294, 2, 4), Skills.FARMING, 15, SupplyType.HERB_SEEDS),
        RANARR_SEED(new ImmutableItem(5295, 2, 4), Skills.FARMING, 22, SupplyType.HERB_SEEDS),
        TOADFLAX_SEED(new ImmutableItem(5296, 2, 4), Skills.FARMING, 28, SupplyType.HERB_SEEDS),
        IRIT_SEED(new ImmutableItem(5297, 2, 4), Skills.FARMING, 34, SupplyType.HERB_SEEDS),
        AVANTOE_SEED(new ImmutableItem(5298, 2, 4), Skills.FARMING, 40, SupplyType.HERB_SEEDS),
        KWUARM_SEED(new ImmutableItem(5299, 2, 4), Skills.FARMING, 46, SupplyType.HERB_SEEDS),
        SNAPDRAGON_SEED(new ImmutableItem(5300, 2, 4), Skills.FARMING, 52, SupplyType.HERB_SEEDS),
        CADANTINE_SEED(new ImmutableItem(5301, 2, 4), Skills.FARMING, 57, SupplyType.HERB_SEEDS),
        LANTADYME_SEED(new ImmutableItem(5302, 2, 4), Skills.FARMING, 63, SupplyType.HERB_SEEDS),
        DWARF_WEED_SEED(new ImmutableItem(5303, 2, 4), Skills.FARMING, 69, SupplyType.HERB_SEEDS),
        TORSTOL_SEED(new ImmutableItem(5304, 2, 4), Skills.FARMING, 75, SupplyType.HERB_SEEDS),
        
        BANANA_TREE_SEED(new ImmutableItem(5284, 2, 4), Skills.FARMING, 1, SupplyType.OTHER_SEEDS),
        TEAK_SEED(new ImmutableItem(21486, 2, 4), Skills.FARMING, 25, SupplyType.OTHER_SEEDS),
        MAHOGANY_SEED(new ImmutableItem(21488, 2, 4), Skills.FARMING, 45, SupplyType.OTHER_SEEDS),
        WATERMELON_SEED(new ImmutableItem(5321, 2, 20), Skills.FARMING, 37, SupplyType.OTHER_SEEDS),
        SNAPE_GRASS_SEED(new ImmutableItem(22879, 2, 4), Skills.FARMING, 51, SupplyType.OTHER_SEEDS);
        
        public static final CrateReward[] values = values();
        public static final Map<SupplyType, List<CrateReward>> rewardsBySupplyType = new HashMap<>();
        
        static {
            for (val reward : values) {
                rewardsBySupplyType.putIfAbsent(reward.getType(), new ArrayList<>());
                rewardsBySupplyType.get(reward.getType()).add(reward);
            }
        }
        
        private final ImmutableItem item;
        private final int skillId;
        private final int minimumSkillLevelRequired;
        private final SupplyType type;
    }
    
}
