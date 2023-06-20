package com.zenyte.game.content.skills.farming.actions;

import com.zenyte.game.content.achievementdiary.diaries.*;
import com.zenyte.game.content.skills.farming.*;
import com.zenyte.game.content.skills.farming.hespori.HesporiInstance;
import com.zenyte.game.content.skills.woodcutting.actions.Woodcutting;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Kris | 04/02/2019 18:13
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Harvesting extends Action {

    private static final SoundEffect DAMAGE_SOUND = new SoundEffect(1138, 0, 0);

    public Harvesting(final FarmingSpot spot) {
        this.spot = spot;
    }

    private final FarmingSpot spot;

    @Override
    public boolean start() {
        if (spot.getPatch().getType() == PatchType.HESPORI_PATCH) {
            val dismiss = player.getAttributes().containsKey("start_hespori_fight");
            if (dismiss) {
                HesporiInstance.start(player);
                return false;
            }
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    options("Do you want to fight the Hespori?",
                            new DialogueOption("Yes.", () -> HesporiInstance.start(player)),
                            new DialogueOption("Yes (Don't show this again).", () -> {
                                HesporiInstance.start(player);
                                player.getAttributes().put("start_hespori_fight", 1);
                            }), new DialogueOption("No."));
                }
            });
            return false;
        }
        if (!player.getInventory().hasFreeSlots()) {
            player.sendMessage("You need some free inventory space to harvest the produce.");
            return false;
        }
        if ((spot.getPatch().getType().equals(PatchType.ALLOTMENT) || spot.getPatch().getType().equals(PatchType.HERB_PATCH)) && !player.getInventory().containsItem(FarmingConstants.SPADE)) {
            player.sendMessage("You need a spade in order to harvest this.");
            return false;
        }
        //Apparently secateurs are no longer needed for herb-harvesting.
        /*if (spot.getPatch().getType().equals(PatchType.HERB_PATCH) && !FarmingConstants.hasSecateurs(player)) {
            player.sendMessage("You need secateurs in order to harvest this.");
            return false;
        }*/
        if (spot.getPatch().getType().equals(PatchType.CELASTRUS_PATCH)) {
            val axe = Woodcutting.getAxe(player);
            if (!axe.isPresent()) {
                player.sendMessage("You need an axe to harvest the Celastrus tree.");
                return false;
            }
        }
        if (!spot.isTreePatch()) {
            player.sendFilteredMessage("You begin to harvest the " + spot.getPatch().getType().getSanitizedName() + ".");
        }
        harvest();
        delay(3);
        return true;
    }

    @Override
    public boolean process() {
        if (!player.getInventory().hasFreeSlots()) {
            player.sendMessage("You need some free inventory space to harvest the produce.");
            player.setAnimation(Animation.STOP);
            return false;
        }
        return true;
    }

    @Override
    public int processWithDelay() {
        val type = spot.getPatch().getType();
        val chanceOfLosingLife = spot.successProbability();
        val rate = Math.max(50, 1000 * chanceOfLosingLife);
        if (Utils.random((int) rate) == 0) {
            player.getInventory().addOrDrop(new Item(22875, 1));
            player.sendMessage("You find a Hespori seed.");
        }
        if (type == PatchType.BELLADONNA_PATCH) {
            harvestBelladonna();
            return -1;
        } else if (spot.getProduct() == FarmingProduct.WHITE_LILY) {
            harvestWhitelily();
            return -1;
        }
        val sound = type.getPickSoundEffect();
        if (sound != null) {
            player.sendSound(sound);
        }
        if (spot.getProduct().equals(FarmingProduct.CELASTRUS)) {
            player.getAchievementDiaries().update(KourendDiary.CREATE_BATTLESTAFF, 0x1);
        }

        if (player.getMemberRank().eligibleTo(MemberRank.RUBY_MEMBER))
        {
            if(Utils.percentage(5))
            {
                spot.getProduct().getProduct().setAmount(2);
            }
        }

        if (player.getMemberRank().eligibleTo(MemberRank.DIAMOND_MEMBER))
        {
            if(Utils.percentage(5))
            {
                spot.getProduct().getProduct().setAmount(3);
            }
        }
        // custom exception for harvesting grapes with bologas blessing
        if(spot.getProduct() == FarmingProduct.GRAPE && player.getInventory().containsItem(ItemId.BOLOGAS_BLESSING)) {
            player.sendMessage("You use Bologa's blessing to create grapes of Zamorak from the vine.", MessageType.FILTERABLE);
            player.getInventory().deleteItem(ItemId.BOLOGAS_BLESSING, 1);
            player.getInventory().addItem(ItemId.ZAMORAKS_GRAPES, 2);
        } else {
            player.getInventory().addItem(spot.getProduct().getProduct());
        }

        player.getSkills().addXp(Skills.FARMING, spot.getProduct().getHarvestExperience());
        updateDiaries();
        updateChallenges();
        sendHarvestMessage();

/*
        if (player.getPerkManager().isValid(PerkWrapper.LEPRECHAUNS_FRIEND) && Utils.random(100) <= 15) {
            product.setAmount(2);
            player.getPerkManager().consume(PerkWrapper.LEPRECHAUNS_FRIEND);
        }
 */


        val product = spot.getProduct();
        spot.removeFruit();
        if (spot.getValue() <= 3) {
            player.sendFilteredMessage("The " + spot.getPatch().getType().getSanitizedName() + " is now empty.");
            player.getFarming().handleContractCompletion(player, product);
            spot.clear();
            return -1;
        }
        if (spot.isClear() || spot.bearsFruit() && spot.isFruitless()) {
            return -1;
        }
        harvest();
        return 2;
    }

    private void updateDiaries() {
        val product = spot.getProduct();
        if (product.equals(FarmingProduct.MARRENTILL)) {
            player.getDailyChallengeManager().update(SkillingChallenge.HARVEST_MARRENTILLS);
        } else if (product.equals(FarmingProduct.RANARR)) {
            player.getDailyChallengeManager().update(SkillingChallenge.HARVEST_RANARRS);
        } else if (product.equals(FarmingProduct.SNAPDRAGON)) {
            player.getDailyChallengeManager().update(SkillingChallenge.HARVEST_SNAPDRAGONS);
        } else if (product.equals(FarmingProduct.SNAPE_GRASS)) {
            player.getDailyChallengeManager().update(SkillingChallenge.HARVEST_SNAPE_GRASS);
        }
    }

    private void updateChallenges() {
        val id = spot.getProduct().getProduct().getId();
        val patch = spot.getPatch();
        val product = spot.getProduct();
        if (patch.equals(FarmingPatch.CATHERBY_FLOWER) && id == 225) {
            player.getAchievementDiaries().update(KandarinDiary.PICK_LIMPWURT_ROOT);
        } else if (patch.equals(FarmingPatch.CATHERBY_HERB) && id == 217) {
            player.getAchievementDiaries().update(KandarinDiary.PICK_DWARF_WEED);
        } else if (patch.equals(FarmingPatch.ARDOUGNE_HERB) && id == 219) {
            player.getAchievementDiaries().update(ArdougneDiary.PICK_TORSTOL);
        } else if (patch.equals(FarmingPatch.ARDOUGNE_BUSH) && product.equals(FarmingProduct.POISON_IVY)) {
            player.getAchievementDiaries().update(ArdougneDiary.PICK_POISON_IVY_BERRIES);
        } else if (patch.equals(FarmingPatch.CANIFIS_MUSHROOM) && product.equals(FarmingProduct.MUSHROOM)) {
            player.getAchievementDiaries().update(MorytaniaDiary.HARVEST_BITTERCAP_MUSHROOMS);
        }
    }

    private void harvestWhitelily() {
        assert spot.getLives() > 0;
        updateDiaries();
        player.getSkills().addXp(Skills.FARMING, spot.getProduct().getHarvestExperience());
        player.getInventory().addOrDrop(spot.getProduct().getProduct());
        player.getFarming().handleContractCompletion(player, spot.getProduct());
        player.sendFilteredMessage("The " + spot.getPatch().getType().getSanitizedName() + " is now empty.");
        spot.clear();
        spot.refresh();
    }

    /**
     * Belladonna patch functions in mysterious ways - You harvest the patch in a single take.
     */
    private void harvestBelladonna() {
        assert spot.getLives() > 0;
        if (player.getEquipment().getId(EquipmentSlot.HANDS) == -1) {
            player.getPacketDispatcher().sendSoundEffect(DAMAGE_SOUND);
            player.applyHit(new Hit(2, HitType.POISON));
            player.sendMessage("You have been poisoned by the belladonna!");
            return;
        }
        player.getAchievementDiaries().update(LumbridgeDiary.PICK_BELLADONNA);
        player.getSkills().addXp(Skills.FARMING, spot.getProduct().getHarvestExperience());
        player.getFarming().handleContractCompletion(player, spot.getProduct());
        val container = player.getInventory().getContainer();
        int count = 100;
        val product = spot.getProduct();
        while (--count > 0 && spot.checkHarvest() > 0 && product == FarmingProduct.BELLADONNA) {
            //You only receive belladonna produce if you have inventory space; excess belladonna is not dropped.
            container.add(product.getProduct());
        }
        container.refresh(player);
        sendHarvestMessage();
    }

    private void sendHarvestMessage() {
        val product = spot.getProduct();
        if (product == FarmingProduct.CACTUS) {
            player.sendFilteredMessage("You carefully pick a spine from the cactus.");
        } else if (product == FarmingProduct.BELLADONNA) {
            player.sendFilteredMessage("You pick some Deadly Nightshade.");
        } else if (product == FarmingProduct.GIANT_SEAWEED) {
            player.sendFilteredMessage("You pick some giant seaweed.");
        }
    }

    private void harvest() {
        val type = spot.getPatch().getType();
        if (type == PatchType.CELASTRUS_PATCH) {
            Woodcutting.getAxe(player).ifPresent(axe -> player.setAnimation(axe.getDefinitions().getEmote()));
            return;
        }
        val product = spot.getProduct();
        if (product == FarmingProduct.STRAWBERRY) {
            player.getAchievementDiaries().update(ArdougneDiary.HARVEST_STRAWBERRIES);
        } else if (product == FarmingProduct.WATERMELON) {
            player.getAchievementDiaries().update(MorytaniaDiary.HARVEST_WATERMELON);
        }
        val animation = type.getHarvestAnimation();
        val sound = type.getHarvestSoundEffect();
        if (animation != null) {
            player.setAnimation(animation);
        }
        if (sound != null) {
            player.getPacketDispatcher().sendSoundEffect(sound);
        }
    }

}