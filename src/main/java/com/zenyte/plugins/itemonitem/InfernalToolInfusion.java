package com.zenyte.plugins.itemonitem;

import com.google.common.base.Preconditions;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.DoubleItemChat;
import com.zenyte.plugins.dialogue.ItemChat;
import lombok.AllArgsConstructor;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.zenyte.game.item.ItemId.SMOULDERING_STONE;

/**
 * @author Kris | 30/08/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class InfernalToolInfusion implements PairedItemOnItemPlugin {

    @AllArgsConstructor
    private enum InfernalTool {
        AXE(Skills.WOODCUTTING, Skills.FIREMAKING, 61, 85, new int[] {ItemId.DRAGON_AXE, ItemId.INFERNAL_AXE_UNCHARGED}, ItemId.INFERNAL_AXE),
        PICKAXE(Skills.MINING, Skills.SMITHING, 61, 85, new int[] {ItemId.DRAGON_PICKAXE, ItemId.INFERNAL_PICKAXE_UNCHARGED}, ItemId.INFERNAL_PICKAXE),
        HARPOON(Skills.FISHING, Skills.COOKING, 75, 85, new int[] {ItemId.DRAGON_HARPOON, ItemId.INFERNAL_HARPOON_UNCHARGED}, ItemId.INFERNAL_HARPOON);

        private final int primarySkill, secondarySkill;
        private final int primaryRequirement, secondaryRequirement;
        private final int[] usableWeaponry;
        private final int product;
        private static final List<InfernalTool> values = Collections.unmodifiableList(Arrays.asList(values()));
    }

    private static final ItemPair cosmeticPickaxePrimaryPair = new ItemPair(SMOULDERING_STONE, ItemId.DRAGON_PICKAXE_12797);
    private static final ItemPair cosmeticPickaxeSecondaryPair = new ItemPair(ItemId.DRAGON_PICKAXE_12797, ItemId.INFERNAL_PICKAXE_UNCHARGED);
    private static final ItemPair cosmeticPickaxeTertiaryPair = new ItemPair(ItemId.DRAGON_PICKAXE_12797, ItemId.INFERNAL_PICKAXE);
    private static final Animation animation = new Animation(4511);
    private static final Graphics graphics = new Graphics(1240);
    private static final int SOUND = 2725;
    public static final int CHARGES = 5000;
    private static final int PRIMARY_EXPERIENCE = 200;
    private static final int SECONDARY_EXPERIENCE = 350;

    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        val consumedItem = (from.getId() == SMOULDERING_STONE ||
                (Utils.findMatching(InfernalTool.values, value -> (value.usableWeaponry[1] == to.getId() || value.product == to.getId())) != null)) ? from : to;
        val tool = from == consumedItem ? to : from;
        if (matches(cosmeticPickaxePrimaryPair, consumedItem, tool) || matches(cosmeticPickaxeSecondaryPair, consumedItem, tool) || matches(cosmeticPickaxeTertiaryPair, consumedItem, tool)) {
            player.getDialogueManager().start(new ItemChat(player, tool, "The cosmetically upgraded pickaxe is too beautiful to convert into an infernal pickaxe."));
            return;
        }
        val constant = Objects.requireNonNull(Utils.findMatching(InfernalTool.values, value -> value.product == tool.getId() || ArrayUtils.contains(value.usableWeaponry, tool.getId())));
        //Lets allow recharging the tool if it's still in the charged form but has no charges remaining just in case.
        if (tool.getId() == constant.product && tool.getCharges() > 0) {
            player.getDialogueManager().start(new ItemChat(player, tool, "The tool has not yet run out of charges."));
            return;
        }
        val skills = player.getSkills();
        if (skills.getLevelForXp(constant.primarySkill) < constant.primaryRequirement || skills.getLevelForXp(constant.secondarySkill) < constant.secondaryRequirement) {
            player.getDialogueManager().start(new DoubleItemChat(player, consumedItem, tool,
                    "You need a " + Skills.SKILLS[constant.primarySkill] + " level of at least " + constant.primaryRequirement + " and a " +
                            "" + Skills.SKILLS[constant.secondarySkill] + " level of at least " + constant.secondaryRequirement + " to infuse the two together."));
            return;
        }
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                val isDragon = tool.getName().contains("Dragon");
                val productName = ItemDefinitions.getOrThrow(constant.product).getName();
                if (isDragon) {
                    doubleItem(consumedItem, tool,
                            "Are you sure you wish to convert your " + tool.getName().toLowerCase() + " into an "
                                    + productName.toLowerCase() + "? This cannot be reversed. " + productName + "s are untradeable.");
                } else {
                    doubleItem(consumedItem, tool, "Are you sure you wish to recharge your " + productName.toLowerCase() + "? This cannot be reversed.");
                }
                options(new DialogueOption("Proceed with the infusion.", () -> {
                    val inventory = player.getInventory();
                    Preconditions.checkArgument(inventory.getItem(fromSlot) == from);
                    Preconditions.checkArgument(inventory.getItem(toSlot) == to);
                    Preconditions.checkArgument(inventory.containsItems(consumedItem, tool));
                    player.setAnimation(animation);
                    player.setGraphics(graphics);
                    player.sendSound(SOUND);
                    inventory.deleteItem(fromSlot, from);
                    inventory.deleteItem(toSlot, to);
                    inventory.addItem(new Item(constant.product, 1, CHARGES));
                    skills.addXp(constant.primarySkill, PRIMARY_EXPERIENCE);
                    skills.addXp(constant.secondarySkill, SECONDARY_EXPERIENCE);
                    setKey(10);
                }), new DialogueOption("Cancel."));
                item(10, new Item(constant.product), "You infuse the smouldering stone into the " + productName.replace("Infernal ", Strings.EMPTY) + " to make an " + productName.toLowerCase() + ".");
            }
        });
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[]{
                new ItemPair(SMOULDERING_STONE, ItemId.DRAGON_AXE),
                new ItemPair(SMOULDERING_STONE, ItemId.INFERNAL_AXE),
                new ItemPair(SMOULDERING_STONE, ItemId.INFERNAL_AXE_UNCHARGED),
                new ItemPair(ItemId.DRAGON_AXE, ItemId.INFERNAL_AXE),
                new ItemPair(ItemId.DRAGON_AXE, ItemId.INFERNAL_AXE_UNCHARGED),
                new ItemPair(SMOULDERING_STONE, ItemId.DRAGON_PICKAXE),
                new ItemPair(SMOULDERING_STONE, ItemId.INFERNAL_PICKAXE),
                new ItemPair(SMOULDERING_STONE, ItemId.INFERNAL_PICKAXE_UNCHARGED),
                new ItemPair(ItemId.DRAGON_PICKAXE, ItemId.INFERNAL_PICKAXE),
                new ItemPair(ItemId.DRAGON_PICKAXE, ItemId.INFERNAL_PICKAXE_UNCHARGED),
                new ItemPair(SMOULDERING_STONE, ItemId.DRAGON_HARPOON),
                new ItemPair(SMOULDERING_STONE, ItemId.INFERNAL_HARPOON),
                new ItemPair(SMOULDERING_STONE, ItemId.INFERNAL_HARPOON_UNCHARGED),
                new ItemPair(ItemId.DRAGON_HARPOON, ItemId.INFERNAL_HARPOON),
                new ItemPair(ItemId.DRAGON_HARPOON, ItemId.INFERNAL_HARPOON_UNCHARGED),
                cosmeticPickaxePrimaryPair,
                cosmeticPickaxeSecondaryPair,
                cosmeticPickaxeTertiaryPair
        };
    }
}
