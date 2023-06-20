package com.zenyte.game.content.minigame.pestcontrol;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.PVPEquipment;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.var;
import mgi.types.config.enums.Enums;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.val;

import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Consumer;

/**
 * @author Kris | 24/03/2019 15:32
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class VoidKnightRewardsInterface extends Interface {

    private enum VoidKnightReward {

        ATTACK_XP_1("Attack XP", Skills.ATTACK, player -> addExperience(player, Skills.ATTACK, 1)),
        ATTACK_XP_10("Attack XP (+1%)", Skills.ATTACK, player -> addExperience(player, Skills.ATTACK, 10)),
        ATTACK_XP_100("Attack XP (+10%)", Skills.ATTACK, player -> addExperience(player, Skills.ATTACK, 100)),

        DEFENCE_XP_1("Defence XP", Skills.DEFENCE, player -> addExperience(player, Skills.DEFENCE, 1)),
        DEFENCE_XP_10("Defence XP (+1%)", Skills.DEFENCE, player -> addExperience(player, Skills.DEFENCE, 10)),
        DEFENCE_XP_100("Defence XP (+10%)", Skills.DEFENCE, player -> addExperience(player, Skills.DEFENCE, 100)),

        MAGIC_XP_1("Magic XP", Skills.MAGIC, player -> addExperience(player, Skills.MAGIC, 1)),
        MAGIC_XP_10("Magic XP (+1%)", Skills.MAGIC, player -> addExperience(player, Skills.MAGIC, 10)),
        MAGIC_XP_100("Magic XP (+10%)", Skills.MAGIC, player -> addExperience(player, Skills.MAGIC, 100)),

        PRAYER_XP_1("Prayer XP", Skills.PRAYER, player -> addExperience(player, Skills.PRAYER, 1)),
        PRAYER_XP_10("Prayer XP (+1%)", Skills.PRAYER, player -> addExperience(player, Skills.PRAYER, 10)),
        PRAYER_XP_100("Prayer XP (+10%)", Skills.PRAYER, player -> addExperience(player, Skills.PRAYER, 100)),

        STRENGTH_XP_1("Strength XP", Skills.STRENGTH, player -> addExperience(player, Skills.STRENGTH, 1)),
        STRENGTH_XP_10("Strength XP (+1%)", Skills.STRENGTH, player -> addExperience(player, Skills.STRENGTH, 10)),
        STRENGTH_XP_100("Strength XP (+10%)", Skills.STRENGTH, player -> addExperience(player, Skills.STRENGTH, 100)),

        RANGED_XP_1("Ranged XP", Skills.RANGED, player -> addExperience(player, Skills.RANGED, 1)),
        RANGED_XP_10("Ranged XP (+1%)", Skills.RANGED, player -> addExperience(player, Skills.RANGED, 10)),
        RANGED_XP_100("Ranged XP (+10%)", Skills.RANGED, player -> addExperience(player, Skills.RANGED, 100)),

        HITPOINTS_XP_1("Hitpoints XP", Skills.HITPOINTS, player -> addExperience(player, Skills.HITPOINTS, 1)),
        HITPOINTS_XP_10("Hitpoints XP (+1%)", Skills.HITPOINTS, player -> addExperience(player, Skills.HITPOINTS, 10)),
        HITPOINTS_XP_100("Hitpoints XP (+10%)", Skills.HITPOINTS, player -> addExperience(player, Skills.HITPOINTS, 100)),

        HERB_PACK("Herb Pack", -1, player -> addPack(player, "herb")),
        SEED_PACK("Seed Pack", -1, player -> addPack(player, "seed")),
        MINERAL_PACK("Mineral Pack", -1, player -> addPack(player, "mineral")),

        VOID_MACE("Void Knight Mace", -1, player -> addItem(player, 8841)),
        VOID_TOP("Void Knight Top", -1, player -> addItem(player, 8839)),
        VOID_ROBES("Void Knight Robes", -1, player -> addItem(player, 8840)),
        VOID_GLOVES("Void Knight Gloves", -1, player -> addItem(player, 8842)),
        VOID_MAGE_HELM("Void Mage Helm", -1, player -> addItem(player, 11663)),
        VOID_RANGER_HELM("Void Ranger Helm", -1, player -> addItem(player, 11664)),
        VOID_MELEE_HELM("Void Melee Helm", -1, player -> addItem(player, 11665)),
        VOID_SEAL("Void Knight Seal", -1, player -> addItem(player, 11666));

        private static final int[] COMBAT_SKILLS = new int[]{
                Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.HITPOINTS,
                Skills.RANGED, Skills.MAGIC
        };

        private static boolean isEligibleForVoid(final Player player) {
            val skills = player.getSkills();
            for (val skill : COMBAT_SKILLS) {
                if (skills.getLevelForXp(skill) < 42) {
                    return false;
                }
            }
            return skills.getLevelForXp(Skills.PRAYER) >= 22;
        }

        private static void addExperience(final Player player, final int skill, final int amount) {
            val xp = getExperience(player, skill) * (skill == Skills.PRAYER ? 0.5F : 1);
            val bulkModifier = amount == 1 ? 1F : amount == 10 ? 1.01F : 1.1F;
            player.getSkills().addXp(skill, xp * amount * bulkModifier);
        }

        @AllArgsConstructor
        private enum MineralPack {
            IRON_ORE(3, 200, ItemId.IRON_ORE),
            COAL(2, 150, ItemId.COAL),
            MITHRIL(2, 50, ItemId.MITHRIL_ORE),
            ADAMANTITE(1, 25, ItemId.ADAMANTITE_ORE),
            RUNITE(1,10,ItemId.RUNITE_ORE);

            private static final MineralPack[] values = values();

            @Getter private final int weight;
            @Getter private final int quantity;
            @Getter private final int itemId;

            private static int getTotalWeighting() {
                int sum = 0;
                for(val i : values) {
                    sum += i.weight;
                }
                return sum;
            }

            public static Item roll() {
                val roll = Utils.random(getTotalWeighting() - 1);
                var currWeight = -1;
                for(val i : values) {
                    if(currWeight + i.weight >= roll) {
                        return new Item(i.itemId+1, i.quantity);
                    } else {
                        currWeight += i.weight;
                    }
                }
                return null;
            }
        }

        @AllArgsConstructor
        private enum HerbPack {
            RANARR(1, 3, ItemId.GRIMY_RANARR_WEED),
            TOADFLAX(1,3,ItemId.GRIMY_TOADFLAX),
            IRIT(1,3,ItemId.GRIMY_IRIT_LEAF),
            AVANTOE(1,3,ItemId.GRIMY_AVANTOE),
            KWUARM(1,3,ItemId.GRIMY_KWUARM),
            SNAPDRAGON(1,3,ItemId.GRIMY_SNAPDRAGON),
            CADANTINE(1,3,ItemId.GRIMY_CADANTINE),
            LANTADYME(1,3,ItemId.GRIMY_LANTADYME),
            DWARF_WEED(1,3,ItemId.GRIMY_DWARF_WEED),
            TORSTOL(1,3,ItemId.GRIMY_TORSTOL);

            private static final HerbPack[] values = values();

            @Getter private final int weight;
            @Getter private final int quantity;
            @Getter private final int itemId;

            private static int getTotalWeighting() {
                int sum = 0;
                for(val i : values) {
                    sum += i.weight;
                }
                return sum;
            }

            public static Item roll() {
                val roll = Utils.random(getTotalWeighting() - 1);
                var currWeight = -1;
                for(val i : values) {
                    if(currWeight + i.weight >= roll) {
                        return new Item(i.itemId+1, i.quantity);
                    } else {
                        currWeight += i.weight;
                    }
                }
                return null;
            }
        }

        @AllArgsConstructor
        private enum TopTierSeed {
            CELASTRUS(1, 1, ItemId.CELASTRUS_SEED),
            DRAGONFRUIT(1,1,ItemId.DRAGONFRUIT_TREE_SEED),
            MAGIC(1,1,ItemId.MAGIC_SEED),
            TORSTOL(1,1,ItemId.TORSTOL_SEED);

            private static final TopTierSeed[] values = values();

            @Getter private final int weight;
            @Getter private final int quantity;
            @Getter private final int itemId;

            private static int getTotalWeighting() {
                int sum = 0;
                for(val i : values) {
                    sum += i.weight;
                }
                return sum;
            }

            public static Item roll() {
                val roll = Utils.random(getTotalWeighting() - 1);
                var currWeight = -1;
                for(val i : values) {
                    if(currWeight + i.weight >= roll) {
                        return new Item(i.itemId, i.quantity);
                    } else {
                        currWeight += i.weight;
                    }
                }
                return null;
            }
        }

        @AllArgsConstructor
        private enum MediumTierSeed {
            CALQUAT(1, 2, ItemId.CALQUAT_TREE_SEED),
            MAHOGANY(1,2,ItemId.MAHOGANY_SEED),
            TEAK(1,2,ItemId.TEAK_SEED),
            PALM(1,2,ItemId.PALM_TREE_SEED),
            PAPAYA(1,2,ItemId.PAPAYA_TREE_SEED),
            PINEAPPLE(1,2,ItemId.PINEAPPLE_SEED),
            YEW(1,2,ItemId.YEW_SEED),
            MAPLE(1,2,ItemId.MAPLE_SEED),
            RANARR(1,2,ItemId.RANARR_SEED),
            SNAPDRAGON(1,2,ItemId.SNAPDRAGON_SEED),
            POISON_IVY(1,5,ItemId.POISON_IVY_SEED);

            private static final MediumTierSeed[] values = values();

            @Getter private final int weight;
            @Getter private final int quantity;
            @Getter private final int itemId;

            private static int getTotalWeighting() {
                int sum = 0;
                for(val i : values) {
                    sum += i.weight;
                }
                return sum;
            }

            public static Item roll() {
                val roll = Utils.random(getTotalWeighting() - 1);
                var currWeight = -1;
                for(val i : values) {
                    if(currWeight + i.weight >= roll) {
                        return new Item(i.itemId, i.quantity);
                    } else {
                        currWeight += i.weight;
                    }
                }
                return null;
            }
        }

        @AllArgsConstructor
        private enum LowTierSeed {
            WILLOW(1, 2, ItemId.WILLOW_SEED),
            JANGERBERRY(1,2,ItemId.JANGERBERRY_SEED),
            HARRALANDER(1,2,ItemId.HARRALANDER_SEED),
            TOADFLAX(1,2,ItemId.TOADFLAX_SEED),
            IRIT(1,2,ItemId.IRIT_SEED),
            CADANTINE(1,2,ItemId.CADANTINE_SEED),
            DWARF_WEED(1,2,ItemId.DWARF_WEED_SEED),
            AVANTOE(1,2,ItemId.AVANTOE_SEED),
            WHITEBERRY(1,5,ItemId.WHITEBERRY_SEED),
            POTATO_CACTUS(1,5,ItemId.POTATO_CACTUS_SEED),
            WATERMELON(1,10,ItemId.WATERMELON_SEED),
            SNAPEGRASS(1,10,ItemId.SNAPE_GRASS_SEED);

            private static final LowTierSeed[] values = values();

            @Getter private final int weight;
            @Getter private final int quantity;
            @Getter private final int itemId;

            private static int getTotalWeighting() {
                int sum = 0;
                for(val i : values) {
                    sum += i.weight;
                }
                return sum;
            }

            public static Item roll() {
                val roll = Utils.random(getTotalWeighting() - 1);
                var currWeight = -1;
                for(val i : values) {
                    if(currWeight + i.weight >= roll) {
                        return new Item(i.itemId, i.quantity);
                    } else {
                        currWeight += i.weight;
                    }
                }
                return null;
            }
        }

        private static void addPack(final Player player, final String packType) {
            player.sendMessage("Claiming " + packType + " pack...");
            switch(packType) {
                case "seed":
                    int seedType = Utils.random(2);
                    var seed = new Item(0);
                    if (seedType == 0) {//top tier
                        seed = TopTierSeed.roll();
                        player.sendFilteredMessage("You received " + seed.getAmount() + "x " + seed.getName() + ".");
                        player.getInventory().addOrDrop(seed);
                    } else if (seedType == 1) { //med tier
                        for(int i = 0; i < 2; i++) {
                            seed = MediumTierSeed.roll();
                            player.sendFilteredMessage("You received " + seed.getAmount() + "x " + seed.getName() + ".");
                            player.getInventory().addOrDrop(seed);
                        }
                    } else { //low tier
                        for(int i = 0; i < 3; i++) {
                            seed = LowTierSeed.roll();
                            player.sendFilteredMessage("You received " + seed.getAmount() + "x " + seed.getName() + ".");
                            player.getInventory().addOrDrop(seed);
                        }
                    }
                    break;
                case "mineral":
                    for(int i=0; i < 2; i++) {
                        val mineral = MineralPack.roll();
                        player.sendFilteredMessage("You received " + mineral.getAmount() + "x " + mineral.getName() + ".");
                        player.getInventory().addOrDrop(mineral);
                    }
                    break;
                case "herb":
                    for(int i = 0; i < 10; i++) {
                        val herb = HerbPack.roll();
                        player.sendFilteredMessage("You received " + herb.getAmount() + "x " + herb.getName() + ".");
                        player.getInventory().addOrDrop(herb);
                    }
                    break;
            }
        }

        private static void addItem(final Player player, final int itemId) {
            val it = new Item(itemId);
            player.getCollectionLog().add(it);
            player.getInventory().addItem(it).onFailure(item -> {
                World.spawnFloorItem(item, player);
                player.sendMessage("Your " + item.getName() + " was dropped on the floor due to lack of inventory space.");
            });
        }

        private static final Int2ObjectMap<VoidKnightReward> map = new Int2ObjectOpenHashMap<>();

        static {
            val e = Enums.PEST_CONTROL_REWARDS_ENUM;
            for (val value : values()) {
                map.put(e.getKey(value.enumName).orElseThrow(RuntimeException::new), value);
            }
        }

        private final int getSlot() {
            return Enums.PEST_CONTROL_REWARDS_ENUM.getKey(enumName)
                    .orElseThrow(RuntimeException::new);
        }

        private int getPoints() {
            return Enums.PEST_CONTROL_POINTS_ENUM.getValue(getSlot()).orElseThrow(RuntimeException::new);
        }

        private final String enumName;
        private final int skillId;
        private final Consumer<Player> function;

        private OptionalInt getSkill() {
            return skillId == -1 ? OptionalInt.empty() : OptionalInt.of(skillId);
        }

        VoidKnightReward(String enumName, final int skillId,
                         final Consumer<Player> function) {
            this.enumName = enumName;
            this.skillId = skillId;
            this.function = function;
        }

        private static final int getExperience(final Player player, final int skillId) {
            val level = player.getSkills().getLevelForXp(skillId);
            val modifier = skillId == Skills.PRAYER ? 18 : (skillId == Skills.MAGIC || skillId == Skills.RANGED) ? 32 : 35;
            val ceil = Math.ceil((level + 25F) * (level - 24F) / 606F);
            return (int) (ceil * modifier);
        }
    }

    @Override
    protected void attach() {
        put(6, "Confirm reward");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Confirm reward"), 0,
                Enums.PEST_CONTROL_REWARDS_ENUM.getSize(), AccessMask.CLICK_OP1);
        player.getVarManager().sendVar(261, player.getNumericAttribute("pest_control_points").intValue());
    }

    @Override
    protected void build() {
        bind("Confirm reward", (player, slotId, itemId, option) -> {
            val reward = Objects.requireNonNull(VoidKnightReward.map.get(slotId));
            val hasVoidRequirements = Enums.PEST_CONTROL_REWARDS_VOID_ELEMENTS_ENUM.getValue(slotId).isPresent();
            if (hasVoidRequirements && !VoidKnightReward.isEligibleForVoid(player)) {
                player.sendMessage(
                        "You need level 42 in Attack, Strength, Defence, Ranged, Magic and Hitpoints, and level 22 Prayer, to purchase that item.");
                return;
            }
            val skill = Enums.PEST_CONTROL_REWARDS_PACKS_STATS_ENUM.getValue(slotId);
            if (skill.isPresent()) {
                if (reward.function == null) {
                    player.sendMessage("Item packs are currently unavailable.");
                    return;
                }
                val skillId = skill.getAsInt();
                if (player.getSkills().getLevelForXp(skillId) < 25) {
                    player.sendMessage("You need level " + skillId + " " +
                            Enums.SKILL_NAMES_ENUM.getValue(skillId).orElseThrow(RuntimeException::new) + " to purchase that item.");
                    return;
                }
            }
            val xpSkill = reward.getSkill();
            if (xpSkill.isPresent()) {
                val skillId = xpSkill.getAsInt();
                if (player.getSkills().getLevelForXp(skillId) < 25) {
                    player.sendMessage("The Void Knights will not offer training in skills for which you have a level under 25.");
                    return;
                }
            }
            val points = player.getNumericAttribute("pest_control_points").intValue();
            val requiredPoints = reward.getPoints();
            if (points < requiredPoints) {
                player.sendMessage("You need " + requiredPoints + " " + (requiredPoints == 1 ? "point" : "points") +" to claim that reward.");
                return;
            }
            player.addAttribute("pest_control_points", points - reward.getPoints());
            player.getVarManager().sendVar(261, player.getNumericAttribute("pest_control_points").intValue());
            reward.function.accept(player);
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.VOID_KNIGHT_REWARDS;
    }
}
