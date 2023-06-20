package com.zenyte.plugins.renewednpc;

import com.zenyte.game.content.AccomplishmentCape;
import com.zenyte.game.content.MaxCape;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.shop.Shop;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Kris | 25/11/2018 20:09
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Mac extends NPCPlugin {

    private static final Item MAX_CAPE_COST = new Item(995, 2277000);
    private static final Item UNTRIMMED_CAPE_COST = new Item(995, 99000);

    private static final Item MAX_CAPE = new Item(13342);
    private static final Item MAX_HOOD = new Item(13281);

    @Override
    public void handle() {
        bind("Talk-to", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                player.stopAll();
                player.faceEntity(npc);
                val untrimmed = player.getNumericAttribute("first_99_skill").intValue();
                val untrimmedCape = AccomplishmentCape.getBySkill(untrimmed);
                player.getDialogueManager().start(new Dialogue(player, npc) {
                    @Override
                    public void buildDialogue() {
                        player("Hello.");
                        plain("The man glances at you and grunts something unintelligble.");
                        options(TITLE, "Who are you?", "What do you have in your sack?", "Why are you so dirty?", "Talk about the untrimmed skillcape.", "Can I sell my max cape back to you?")
                                .onOptionOne(() -> setKey(5))
                                .onOptionTwo(() -> setKey(10))
                                .onOptionThree(() -> setKey(15))
                                .onOptionFour(() -> setKey(20))
                                .onOptionFive(() -> setKey(130));
                        player(5, "Who are you?");
                        npc("Mac. What's it to you?");
                        player("Only trying to be friendly.").executeAction(() -> setKey(3));
                        player(10, "What do you have in your sack?");
                        npc("S'me cape.");
                        player("Your cape?");
                        options(TITLE, "Can I have it?", "Why do you keep it in a sack?").onOptionOne(() -> setKey(25)).onOptionTwo(() -> setKey(35));
                        player(15, "Why are you so dirty?");
                        npc("Bath XP waste.").executeAction(() -> setKey(3));
                        player(20,"Can I claim my untrimmed skillcape?");
                        npc("Mebe, let me see first.").executeAction(() -> {
                            if (untrimmed == -1) {
                                setKey(120);
                            } else {
                                setKey(22);
                            }
                        });
                        npc(22, "So <col=00080>" + Skills.getSkillName(untrimmedCape.getSkill()) + " skillcape</col>, that will be " + Utils.format(UNTRIMMED_CAPE_COST.getAmount()) + " coins.");
                        options("Buy the <col=00080>" + Skills.getSkillName(untrimmedCape.getSkill()) + " skillcape</col> for " + Utils.format(UNTRIMMED_CAPE_COST.getAmount()) + " coins?", "Yes.", "Nevermind")
                                .onOptionOne(() -> {
                                    if (!player.getInventory().checkSpace(2)) {
                                        setKey(70);
                                    } else if (!player.getInventory().containsItem(UNTRIMMED_CAPE_COST)) {
                                        setKey(90);
                                    } else {
                                        player.getInventory().deleteItem(UNTRIMMED_CAPE_COST);
                                        player.getInventory().addItem(untrimmedCape.getUntrimmed(), 1);
                                        player.getInventory().addItem(untrimmedCape.getHood(), 1);
                                        setKey(100);
                                    }
                                });
                        player(25, "Can I have it?");
                        npc("Mebe.").executeAction(() -> {
                            if (!player.getSkills().isMaxed()) {
                                setKey(110);
                            } else {
                                setKey(27);
                            }
                        });
                        player(27, "I'm sure I could make it worth your while.");
                        npc("How much?").executeAction(() -> {
                            if (player.getInventory().containsItem(MAX_CAPE_COST)) {
                                setKey(30);
                            } else {
                                setKey(50);
                            }
                        });
                        player(30, "How about " + Utils.format(MAX_CAPE_COST.getAmount()) + " gold?");
                        options("Buy Mac's Cape for " + Utils.format(MAX_CAPE_COST.getAmount()) + " gold?", "Yes, pay the man.", "No.").onOptionOne(() -> setKey(55));
                        player(35, "Why do you keep it in a sack?");
                        npc("Get it dirty.").executeAction(() -> setKey(3));
                        npc("Well you can come back when you think you do.");
                        player(50, "Actually now that I think about I probably don't have enough.");
                        npc(55, "Here you go lad.").executeAction(() -> {
                            if (player.getInventory().getFreeSlots() >= 2) {
                                setKey(60);
                                player.getInventory().deleteItem(MAX_CAPE_COST);
                                player.getInventory().addItem(MAX_CAPE);
                                player.getInventory().addItem(MAX_HOOD);
                            } else {
                                setKey(70);
                            }
                        });
                        doubleItem(60, MAX_HOOD, MAX_CAPE, "Mac grunts and hands over his cape, pocketing your money swiftly.");
                        npc(70, "It seems that you don't have enough space at the moment, come back when you do.");
                        player(80, "Bye.");
                        npc("Later.");
                        npc(90, "Not enough money on you. Come back when you do.");
                        npc(100, "Enjoy the cape matey.");
                        npc(110, "You don't seem to be worthy of this cape yet. Come back and mebe I will sell you one.");
                        npc(120, "Get 99 first then mebe I give you.");
                        npc(130,"Yip. Show me the goods. I pay 80%.").executeAction(() -> {
                            if (player.getInventory().getFreeSlots() >= 1 && player.getInventory().containsAnyOf(ItemId.MAX_CAPE_13342,ItemId.ARDOUGNE_MAX_CAPE,ItemId.ASSEMBLER_MAX_CAPE,ItemId.ACCUMULATOR_MAX_CAPE,ItemId.INFERNAL_MAX_CAPE_21285,ItemId.FIRE_MAX_CAPE,ItemId.SARADOMIN_MAX_CAPE,ItemId.ZAMORAK_MAX_CAPE,ItemId.GUTHIX_MAX_CAPE,ItemId.IMBUED_SARADOMIN_MAX_CAPE,ItemId.IMBUED_ZAMORAK_MAX_CAPE,ItemId.IMBUED_GUTHIX_MAX_CAPE, ItemId.MYTHICAL_MAX_CAPE)){
                                setKey(140);
                            } else if (player.getInventory().getFreeSlots() < 1){
                                setKey(150);
                            } else {
                                setKey(160);
                            }
                        });
                        player(140,"Mebe, let me see for how much first.");
                        options("Sell all max variants in your inventory for 1821600 gold per cape?", "Yes.", "No, I've changed my mind.").onOptionOne(() -> {
                            player.getInventory().addItem(995, (int) (1821600 * player.getInventory().getAmountOf(ItemId.MAX_CAPE_13342,ItemId.ARDOUGNE_MAX_CAPE,ItemId.ASSEMBLER_MAX_CAPE,ItemId.ACCUMULATOR_MAX_CAPE,ItemId.INFERNAL_MAX_CAPE_21285,ItemId.FIRE_MAX_CAPE,ItemId.SARADOMIN_MAX_CAPE,ItemId.ZAMORAK_MAX_CAPE,ItemId.GUTHIX_MAX_CAPE,ItemId.IMBUED_SARADOMIN_MAX_CAPE,ItemId.IMBUED_ZAMORAK_MAX_CAPE,ItemId.IMBUED_GUTHIX_MAX_CAPE,ItemId.MYTHICAL_MAX_CAPE)));
                            player.getInventory().deleteItem(ItemId.MAX_CAPE_13342, player.getInventory().getAmountOf(ItemId.MAX_CAPE_13342));
                            player.getInventory().deleteItem(ItemId.ARDOUGNE_MAX_CAPE, player.getInventory().getAmountOf(ItemId.ARDOUGNE_MAX_CAPE));
                            player.getInventory().deleteItem(ItemId.ASSEMBLER_MAX_CAPE, player.getInventory().getAmountOf(ItemId.ASSEMBLER_MAX_CAPE));
                            player.getInventory().deleteItem(ItemId.ACCUMULATOR_MAX_CAPE, player.getInventory().getAmountOf(ItemId.ACCUMULATOR_MAX_CAPE));
                            player.getInventory().deleteItem(ItemId.INFERNAL_MAX_CAPE_21285, player.getInventory().getAmountOf(ItemId.INFERNAL_MAX_CAPE_21285));
                            player.getInventory().deleteItem(ItemId.FIRE_MAX_CAPE, player.getInventory().getAmountOf(ItemId.FIRE_MAX_CAPE));
                            player.getInventory().deleteItem(ItemId.SARADOMIN_MAX_CAPE, player.getInventory().getAmountOf(ItemId.SARADOMIN_MAX_CAPE));
                            player.getInventory().deleteItem(ItemId.ZAMORAK_MAX_CAPE, player.getInventory().getAmountOf(ItemId.ZAMORAK_MAX_CAPE));
                            player.getInventory().deleteItem(ItemId.GUTHIX_MAX_CAPE, player.getInventory().getAmountOf(ItemId.GUTHIX_MAX_CAPE));
                            player.getInventory().deleteItem(ItemId.IMBUED_SARADOMIN_MAX_CAPE, player.getInventory().getAmountOf(ItemId.IMBUED_SARADOMIN_MAX_CAPE));
                            player.getInventory().deleteItem(ItemId.IMBUED_ZAMORAK_MAX_CAPE, player.getInventory().getAmountOf(ItemId.IMBUED_ZAMORAK_MAX_CAPE));
                            player.getInventory().deleteItem(ItemId.IMBUED_GUTHIX_MAX_CAPE, player.getInventory().getAmountOf(ItemId.IMBUED_GUTHIX_MAX_CAPE));
                            player.getInventory().deleteItem(ItemId.MYTHICAL_MAX_CAPE, player.getInventory().getAmountOf(ItemId.MYTHICAL_MAX_CAPE));
                        });
                        npc(150,"It seems that you don't have enough space at the moment, come back when you do.");
                        npc(160,"It seems that you don't have any max cape variants on you.");
                    }
                });
            }

            @Override
            public void execute(final Player player, final NPC npc) {
                player.stopAll();
                player.setFaceEntity(npc);
                handle(player, npc);
               // npc.setInteractingWith(player);
            }
        });
        bind("Trade", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                player.stopAll();
                player.faceEntity(npc);
                Shop.get("Accomplishment Cape Shop", false, player).open(player);
            }

            @Override
            public void execute(final Player player, final NPC npc) {
                player.stopAll();
                player.setFaceEntity(npc);
                handle(player, npc);
                // npc.setInteractingWith(player);
            }
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                10010
        };
    }
}
