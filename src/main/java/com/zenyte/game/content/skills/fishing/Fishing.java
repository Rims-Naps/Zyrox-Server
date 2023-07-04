package com.zenyte.game.content.skills.fishing;

import com.google.common.collect.ImmutableMap;
import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.achievementdiary.diaries.*;
import com.zenyte.game.content.skills.cooking.CookingDefinitions;
import com.zenyte.game.content.treasuretrails.ClueItem;
import com.zenyte.game.content.treasuretrails.clues.CharlieTask;
import com.zenyte.game.content.treasuretrails.clues.SherlockTask;
import com.zenyte.game.content.vote.BoosterPerks;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.impl.FishingSpot;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.plugins.dialogue.PlainChat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zenyte.game.content.skills.woodcutting.actions.Woodcutting.BURN_GFX;

/**
 * @author Noele | Nov 9, 2017 : 12:22:34 AM
 */

public class Fishing extends Action {

    public static final Map<DiaryReward, Integer> diaryRewardMap = ImmutableMap.<DiaryReward, Integer>builder()
            .put(DiaryReward.RADAS_BLESSING4, 8)
            .put(DiaryReward.RADAS_BLESSING3, 6)
            .put(DiaryReward.RADAS_BLESSING2, 4)
            .put(DiaryReward.RADAS_BLESSING1, 2)
            .build();

    private final FishingSpot spot;
    private final SpotDefinitions defs;
    private FishingTool.Tool tool;

    private final Item BIG_SWORDFISH = new Item(7991);
    private final Item BIG_BASS = new Item(7989);
    private final Item BIG_SHARK = new Item(7993);

    private final List<FishDefinitions> fish = new ObjectArrayList<>();

    private final int posHash;
    private final boolean barbarian;

    public Fishing(final FishingSpot spot, final SpotDefinitions spotDefinitions) {
        this.spot = spot;
        defs = spotDefinitions;
        posHash = spot.getPosition().getPositionHash();
        barbarian = defs == SpotDefinitions.BARBARIAN_FISH;
    }

    public static void init(final Player player, final NPC npc, final String option) {
        if (!(npc instanceof FishingSpot)) {
            throw new RuntimeException("Spot is not original/implemented: " + npc.getId() + ", " + option);
        }
        val spot = SpotDefinitions.get(player.getTransmogrifiedId(npc.getDefinitions(), npc.getId()) + "|" + option);
        if (spot == null) {
            throw new RuntimeException("Spot is null: " + npc.getId() + ", " + option);
        }
        player.getActionManager().setAction(new Fishing((FishingSpot) npc, spot));
    }

    @Override
    public boolean start() {
        val tool = defs.getTool().getTool(player);
        if (!tool.isPresent() && !isBareHanded()) {
            val name = ItemDefinitions.getOrThrow(defs.getTool().tools[0].id).getName().toLowerCase();
            player.getDialogueManager().start(new PlainChat(player, "You need a " + name + " to " + (barbarian ?
                    "catch" : defs.getActions()[0].toLowerCase()) + " these fish."));
            return false;
        }
        if (!isBareHanded()) {
            this.tool = tool.get();
        }
        if (!check()) {
            return false;
        }
        this.fish.addAll(Arrays.stream(defs.getFish()).filter(fish -> player.getSkills().getLevel(Skills.FISHING) >= fish.getLevel()).collect(Collectors.toList()));
        if (fish.contains(FishDefinitions.INFERNAL_EEL)) {
            val gloves = player.getEquipment().getId(EquipmentSlot.HANDS);
            if (gloves != 1580 && gloves != 30030) {
                player.sendMessage("You need a pair of ice gloves to fish in this hot lava.");
                return false;
            }
        }
        val name = (fish.size() == 1) ? Utils.checkPlural(fish.get(0).getName()) : "a fish";
        if (defs == SpotDefinitions.MINNOW) {
            player.sendFilteredMessage("You attempt to catch some minnows.");
        } else {
            player.sendFilteredMessage("You attempt to catch " + name + ".");
        }
        player.setFaceEntity(spot);
        delay(getDelay());
        return true;
    }

    private int getDelay() {
        if (isBareHanded()){
            return 4;
        }
        return defs == SpotDefinitions.MINNOW ? 1 : tool.increasedSpeed ? 3 : 4;
    }

    @Override
    public boolean process() {
        val fish = defs.getLowestTierFish();
        if (!isBareHanded()) {
            player.setAnimation(tool.animation);
        }else if (fish.getId() == 359){
            player.setAnimation(new Animation(6710));
        }else if (fish.getId() == 371){
            player.setAnimation(new Animation(6707));
        }else if (fish.getId() == 383){
            player.setAnimation(new Animation(6706));
        }
        return checkSpot();
    }

    @Override
    public int processWithDelay() {
        if (!success()) {
            return getDelay();
        }
        if (!check()) {
            player.setAnimation(Animation.STOP);
            return -1;
        }
        addFish();
        if (!player.getInventory().hasFreeSlots()) {
            player.sendMessage("You do not have enough space in your inventory.");
            return -1;
        }
        return getDelay();
    }

    @Override
    public void stop() {
        player.setFaceEntity(null);
    }

    public boolean success() {
        val fishLevel = defs == SpotDefinitions.MINNOW ? 20 : defs == SpotDefinitions.BARBARIAN_FISH ? 1 : defs.getLowestTierFish().getLevel();
        val level = player.getSkills().getLevel(Skills.FISHING) + (player.inArea("Fishing Guild") ? 7 : 0);
        val advancedLevels =
                level - fishLevel;
        return Math.min(Math.round(advancedLevels * 0.6F) + 30, 70) > Utils.random(100);
    }

    private void addFish() {
        //TODO : add proper catch rate algorithm to 'fish' value
        val baits = defs.getBait();
        if (baits != null) {
            for (val bait : baits) {
                if (player.getInventory().containsItem(bait.getId(), 1)) {
                    player.getInventory().deleteItem(bait.getId(), 1);
                    break;
                }
            }
        }

        val fish = this.fish.get(Utils.random(this.fish.size() - 1));
        if (fish.equals(FishDefinitions.TROUT)) {
            player.getAchievementDiaries().update(VarrockDiary.CATCH_TROUT);
            player.getDailyChallengeManager().update(SkillingChallenge.CATCH_TROUT);
            player.getAchievementDiaries().update(KourendDiary.FISH_A_TROUT);
        } else if (fish.equals(FishDefinitions.SALMON)) {
            player.getAchievementDiaries().update(LumbridgeDiary.CATCH_SALMON);
        } else if (fish.equals(FishDefinitions.ANCHOVIES)) {
            player.getAchievementDiaries().update(LumbridgeDiary.CATCH_ANCHOVIES);
        } else if (fish.equals(FishDefinitions.KARAMBWAN)) {
            player.getAchievementDiaries().update(KaramjaDiary.CATCH_A_KARAMBWAN);
        } else if (fish.equals(FishDefinitions.DARK_CRAB)) {
            player.getAchievementDiaries().update(WildernessDiary.FISH_AND_COOK_DARK_CRAB, 0x1);
        } else if (fish.equals(FishDefinitions.MACKERAL)) {
            player.getAchievementDiaries().update(KandarinDiary.CATCH_A_MACKEREL);
        } else if (fish.equals(FishDefinitions.BASS)) {
            player.getAchievementDiaries().update(KandarinDiary.CATCH_AND_COOK_BASS, 0x1);
            player.getAchievementDiaries().update(WesternProvincesDiary.FISH_BASS);
        } else if (fish.equals(FishDefinitions.LEAPING_STURGEON)) {
            player.getAchievementDiaries().update(KandarinDiary.CATCH_LEAPING_STURGEON);
        } else if (fish.equals(FishDefinitions.SHARK)) {
            SherlockTask.CATCH_RAW_SHARK.progress(player);
            player.getDailyChallengeManager().update(SkillingChallenge.CATCH_SHARKS);
        } else if (fish.equals(FishDefinitions.ANGLERFISH)) {
            player.getAchievementDiaries().update(KourendDiary.CATCH_ANGLERFISH, 0x1);
            player.getDailyChallengeManager().update(SkillingChallenge.CATCH_ANGLERFISH);
        } else if (fish.equals(FishDefinitions.KARAMBWANJI)) {
            player.getDailyChallengeManager().update(SkillingChallenge.CATCH_KARAMBWANJI);
        } else if (fish.equals(FishDefinitions.MONKFISH)) {
            player.getDailyChallengeManager().update(SkillingChallenge.CATCH_MONKFISH);
        } else if (fish.equals(FishDefinitions.SWORDFISH)) {
            player.getDailyChallengeManager().update(SkillingChallenge.CATCH_SWORDFISH);
        } else if (fish.equals(FishDefinitions.LAVA_EEL)) {
            player.getAchievementDiaries().update(WildernessDiary.FISH_RAW_LAVA_EEL);
        }

        if (fish.equals(FishDefinitions.HERRING)) {
            CharlieTask.FISH_A_HERRING.progress(player);
        } else if (fish.equals(FishDefinitions.TROUT)) {
            CharlieTask.FISH_A_TROUT.progress(player);
        }

        //TODO
        //player.getAchievementDiaries().update(ArdougneDiary.CATCH_FISH_ON_FISHING_PLATFORM);
        player.getAchievementDiaries().update(KaramjaDiary.USE_FISHING_SPOTS);
        if (defs == SpotDefinitions.MINNOW) {
            val amount = (10 + Math.min(4, ((player.getSkills().getLevel(Skills.FISHING) - 82) / 3))) * 2;
            player.getInventory().addOrDrop(new Item(fish.getId(), amount));
            player.sendFilteredMessage("You catch some minnows in your net.");
        } else if (fish.equals(FishDefinitions.KARAMBWANJI)) {
            val amount = (int) (1 + Math.floor(player.getSkills().getLevel(Skills.FISHING) / 5F));
            val incinerated = incinerate(fish, amount);
            if (!incinerated) {
                player.getInventory().addItem(fish.getId(), amount).onFailure(remainder -> World.spawnFloorItem(remainder, player));
                player.sendFilteredMessage("You catch " + ((amount > 1) ? "some" : "a") + " karambwanji in your net.");
            } else {
                player.sendFilteredMessage("Your infernal harpoon instantly incinerates the karambwanji.");
            }
        } else {
            var diaryChance = 0 ;
            for (val entry : diaryRewardMap.entrySet()) {
                if (entry.getKey().eligibleFor(player) && player.getEquipment().containsItem(entry.getKey().getItem())) {
                    diaryChance = entry.getValue();
                }
            }
            val amount = BoosterPerks.isActive(player, BoosterPerks.FISHING) && Utils.random(100) <= 5 ? 2 : (diaryChance > 0  && Utils.random(100) <= diaryChance) ? 2 : 1;
            val incinerated = incinerate(fish, amount);
            if (!incinerated) {
                player.getInventory().addItem(fish.getId(), amount).onFailure(remainder -> World.spawnFloorItem(remainder, player));
                player.sendFilteredMessage("You catch " + Utils.checkPlural(fish.getName()) + ".");
            } else {
                player.sendFilteredMessage("Your infernal harpoon instantly incinerates the " + fish.getName() + ".");
            }
            if (fish.getId() == 371) {
                if(Utils.random(999) == 0) {
                    player.sendMessage("You catch an enormous swordfish!");
                    player.getInventory().addOrDrop(BIG_SWORDFISH.getId());
                    player.getCollectionLog().add(BIG_SWORDFISH);
                }
            }
            if (fish.getId() == 363) {
                if(Utils.random(999) == 0) {
                    player.sendMessage("You catch an enormous bass!");
                    player.getInventory().addOrDrop(BIG_BASS.getId());
                    player.getCollectionLog().add(BIG_BASS);
                }
            }
            if (fish.getId() == 383) {
                if(Utils.random(999) == 0) {
                    player.sendMessage("You catch an enormous shark!");
                    player.getInventory().addOrDrop(BIG_SHARK.getId());
                    player.getCollectionLog().add(BIG_SHARK);
                }
            }
        }

        player.getSkills().addXp(Skills.FISHING, fish.getXp());
        if (isBareHanded()) {
            if (fish.getId() == 359){
                player.getSkills().addXp(Skills.STRENGTH, 8);
            }
            if (fish.getId() == 371){
                player.getSkills().addXp(Skills.STRENGTH, 10);
            }
            if (fish.getId() == 383){
                player.getSkills().addXp(Skills.STRENGTH, 11);
            }
        }
        if (barbarian) {
            player.getSkills().addXp(Skills.AGILITY, fish.getBarbarianXp());
            player.getSkills().addXp(Skills.STRENGTH, fish.getBarbarianXp());
        }
        if (Utils.random(999) == 0 && player.getSkills().getLevelForXp(Skills.FISHING) >= 34) {
            Item item = null;
            if (!player.containsItem(ItemId.ANGLER_BOOTS)) {
                player.getInventory().addOrDrop(item = new Item(ItemId.ANGLER_BOOTS));
                player.sendMessage("A pair of angler boots washes up ashore; you catch them before they drift away.");
            } else if (!player.containsItem(ItemId.ANGLER_HAT)) {
                player.getInventory().addOrDrop(item = new Item(ItemId.ANGLER_HAT));
                player.sendMessage("An angler's hat washes up ashore; you catch it before it drifts away.");
            } else if (!player.containsItem(ItemId.ANGLER_WADERS)) {
                player.getInventory().addOrDrop(item = new Item(ItemId.ANGLER_WADERS));
                player.sendMessage("A pair of angler's waders washes up ashore; you catch it before it drifts away.");
            } else if (!player.containsItem(ItemId.ANGLER_TOP)) {
                player.getInventory().addOrDrop(item = new Item(ItemId.ANGLER_TOP));
                player.sendMessage("An angler top washes up ashore; you catch it before it drifts away.");
            }
            if (item != null) {
                player.setForceTalk(new ForceTalk("Ooh! I found something."));
                player.getCollectionLog().add(item);
            }
        }
        ClueItem.roll(player, defs.getBaseClueBottleChance(), player.getSkills().getLevel(Skills.FISHING), ClueItem::getClueBottle);
    }

    private boolean incinerate(final FishDefinitions fish, final int amount) {
        if (isBareHanded()){
            return false;
        }
        val isUsingInfernal = tool.id == 21031;
        if (isUsingInfernal) {
            if (Utils.random(2) == 0) {
                val weapon = player.getWeapon();
                if (weapon != null && weapon.getId() == 21031 && weapon.getCharges() > 0) {
                    incinerate(fish, amount, weapon, EquipmentSlot.WEAPON.getSlot(),
                            player.getEquipment().getContainer());
                    return true;
                }
                for (val entryset : player.getInventory().getContainer().getItems().int2ObjectEntrySet()) {
                    val value = entryset.getValue();
                    if (value == null) {
                        continue;
                    }
                    if (value.getId() == 21031 && value.getCharges() > 0) {
                        incinerate(fish, amount, value, entryset.getIntKey(), player.getInventory().getContainer());
                        break;
                    }
                }
                return true;

            }
        }
        return false;
    }

    private void incinerate(final FishDefinitions fish, final int amount, final Item harpoon, final int slot,
                            final Container container) {
        player.getChargesManager().removeCharges(harpoon, amount,
                container, slot);
        player.sendSound(2577);
        player.setGraphics(BURN_GFX);
        for (int i = 0; i < amount; i++) {
            val cookable = CookingDefinitions.CookingData.getData(fish.getId());
            if (cookable != null) {
                player.getSkills().addXp(Skills.COOKING, cookable.getXp() / 2F);
            }
        }
    }

    private boolean check() {
        return (checkBait() && checkLevel() && player.getInventory().checkSpace());
    }

    private boolean checkLevel() {
        val fish = defs.getLowestTierFish();
        if (fish.getLevel() > player.getSkills().getLevel(Skills.FISHING)) {
            player.getDialogueManager().start(new PlainChat(player,
                    "You need to be at least level " + fish.getLevel() + " Fishing to " + defs.getActions()[0].toLowerCase() + " these fish."));
            return false;
        }
        if (fish.getBarbarianLevel() > player.getSkills().getLevel(Skills.AGILITY) || fish.getBarbarianLevel() > player.getSkills().getLevel(Skills.STRENGTH)) {
            player.getDialogueManager().start(new PlainChat(player,
                    "You need to be at least level " + fish.getBarbarianLevel()
                            + " in Agility and Strength to catch these fish."));
            return false;
        }
        return true;
    }

    private boolean isBareHanded() {
        val fish = defs.getLowestTierFish();
        val tool = defs.getTool().getTool(player);
        if ((fish.getId() == 359 || fish.getId() == 371 || fish.getId() == 383) && !tool.isPresent()) {
            if (fish.getId() == 359 && player.getSkills().getLevel(Skills.FISHING) >= 55 && player.getSkills().getLevel(Skills.STRENGTH) >= 35) {
                return true;
            }
            if (fish.getId() == 371 && player.getSkills().getLevel(Skills.FISHING) >= 70 && player.getSkills().getLevel(Skills.STRENGTH) >= 50) {
                return true;
            }
            if (fish.getId() == 383 && player.getSkills().getLevel(Skills.FISHING) >= 96 && player.getSkills().getLevel(Skills.STRENGTH) >= 76) {
                return true;
            }
        }
        return false;
    }

    private boolean checkBait() {
        val baits = defs.getBait();
        if (baits == null) {
            return true;
        }
        for (val bait : baits) {
            if (player.getInventory().containsItem(bait.getId(), 1)) {
                return true;
            }
        }
        player.getDialogueManager().start(new PlainChat(player, "You don't have any bait for this fishing spot!"));
        return false;
    }

    private boolean checkSpot() {
        if (spot.getTicks() <= 1 || spot.isFinished() || spot.getPosition().getPositionHash() != posHash) {
            player.setAnimation(Animation.STOP);
            player.setFaceEntity(null);
            return false;
        }
        return true;
    }

}
