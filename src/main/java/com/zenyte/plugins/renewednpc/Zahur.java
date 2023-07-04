package com.zenyte.plugins.renewednpc;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.consumables.drinks.Potion;
import com.zenyte.game.content.zahur.Herb;
import com.zenyte.game.content.zahur.PotionResult;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.impl.NPCChat;
import mgi.types.config.items.ItemDefinitions;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import lombok.val;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * @author Tommeh | 15-3-2019 | 23:42
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class Zahur extends NPCPlugin {

    private static final Item NOTED_VIAL_OF_WATER = new Item(228);
    private static final Item NOTED_VIAL_OF_BLOOD = new Item(22447);

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> {
            player.getDialogueManager().start(new Dialogue(player, npc) {

                @Override
                public void buildDialogue() {
                    npc("Hello and welcome! I'm Zahur, Zenyte's master herbalist.");
                    options(TITLE, "Clean my noted grimy herbs.", "Decant my potions.", "Make unfinished potions.", "Nevermind.").onOptionOne(() -> setKey(5)).onOptionTwo(() -> setKey(10)).onOptionThree(() -> setKey(15));
                    npc(5, "Very, well. Just keep in mind I can only clean noted grimy herbs so unnoted grimy herbs will not work.").executeAction(() -> cleanHerbs(player, npc));
                    decant(10).onDose(1, () -> {
                        if (Zahur.this.decant(player, npc, 1)) {
                            setKey(20);
                        }
                    }).onDose(2, () -> {
                        if (Zahur.this.decant(player, npc, 2)) {
                            setKey(20);
                        }
                    }).onDose(3, () -> {
                        if (Zahur.this.decant(player, npc, 3)) {
                            setKey(20);
                        }
                    }).onDose(4, () -> {
                        if (Zahur.this.decant(player, npc, 4)) {
                            setKey(20);
                        }
                    });
                    npc(15, "Very, well. Just keep in mind, I can only make unfinished potions out of noted herbs. They can either be grimy or cleaned but you'll need the Herblore skillcape perk for me to clean your grimy herbs.").executeAction(() -> makeUnfinishedPotions(player, npc));
                    npc(20, "There, all done.");
                }
            });
        });
        bind("Clean", (player, npc) -> {
            player.getDialogueManager().start(new NPCChat(player, npc.getId(), "Keep in mind, I can only clean noted grimy herbs right now.", () -> cleanHerbs(player, npc)));
        });
        bind("Make unfinished potion(s)", (player, npc) -> {
            player.getDialogueManager().start(new NPCChat(player, npc.getId(), "Keep in mind, I can only make unfinished potions out of noted herbs. They can either be grimy or cleaned but you'll need the Herblore skillcape perk for me to clean your grimy herbs.", () -> makeUnfinishedPotions(player, npc)));
        });
        bind("Decant", (player, npc) -> {
            player.getDialogueManager().start(new Dialogue(player, npc) {

                @Override
                public void buildDialogue() {
                    decant().onDose(1, () -> {
                        if (Zahur.this.decant(player, npc, 1)) {
                            setKey(5);
                        }
                    }).onDose(2, () -> {
                        if (Zahur.this.decant(player, npc, 2)) {
                            setKey(5);
                        }
                    }).onDose(3, () -> {
                        if (Zahur.this.decant(player, npc, 3)) {
                            setKey(5);
                        }
                    }).onDose(4, () -> {
                        if (Zahur.this.decant(player, npc, 4)) {
                            setKey(5);
                        }
                    });
                    npc(5, "There, all done.");
                }
            });
        });
        bind("Crush secondaries", Zahur::crushSecondaries);
    }

    public static final void crushSecondaries(@NotNull final Player player, @NotNull final NPC npc) {
        player.getDialogueManager().finish();
        val inventory = player.getInventory();
        val cost = calculateSecondariesCost(player, npc);
        val free = player.getMemberRank().eligibleTo(MemberRank.ONYX_MEMBER);
        if (cost == -1) {
            return;
        }
        player.getDialogueManager().start(new Dialogue(player, npc) {
            @Override
            public void buildDialogue() {
                if (inventory.getAmountOf(995) < cost && !free) {
                    npc("You need " + Utils.format(cost) + " coins if you'd like me to crush all those secondaries.");
                    return;
                }
                if (cost == 0) {
                    npc("You have no secondaries with you that I'm able to crush.");
                    return;
                }
                if (!free) {
                    npc("Crushing the secondaries in your inventory will cost you " + Utils.format(cost) + " coins.");
                }
                options("Crush the secondaries for " + (free ? "free" : Utils.format(cost)) + "?",
                        new DialogueOption("Yes, crush them.", () -> {
                            player.getDialogueManager().finish();
                            val cost = calculateSecondariesCost(player, npc);
                            if (cost == -1) {
                                return;
                            }
                            if (cost == 0) {
                                player.getDialogueManager().start(new Dialogue(player, npc) {
                                    @Override
                                    public void buildDialogue() {
                                        npc("You have no secondaries with you that I'm able to crush.");
                                    }
                                });
                                return;
                            }
                            if (inventory.getAmountOf(995) < cost && !free) {
                                player.getDialogueManager().start(new Dialogue(player, npc) {
                                    @Override
                                    public void buildDialogue() {
                                        npc("You need " + Utils.format(cost) + " coins if you'd like me to crush all those secondaries.");
                                    }
                                });
                                return;
                            }
                            if (!free) {
                                inventory.deleteItem(new Item(995, (int) cost));
                            }
                            for (int i = 0; i < 28; i++) {
                                val item = inventory.getItem(i);
                                if (item == null) {
                                    continue;
                                }
                                val id = item.getId();
                                val secondary = Utils.findMatching(HerbloreSecondary.values, sec -> (sec.rawItem == id && sec.processedItem != -1) || (sec.rawNotedItem == id && (sec == HerbloreSecondary.LAVA_SCALE || sec.processedNotedItem != -1)));
                                if (secondary != null) {
                                    val crushedId = id == secondary.rawItem ? secondary.processedItem : secondary.processedNotedItem;
                                    if (secondary == HerbloreSecondary.LAVA_SCALE) {
                                        inventory.deleteItem(i, item);
                                        inventory.addOrDrop(new Item(11994, Utils.random(3, 6) * item.getAmount()));
                                    } else {
                                        inventory.deleteItem(i, item);
                                        inventory.addOrDrop(new Item(crushedId, item.getAmount()));
                                    }
                                }
                            }
                            inventory.refreshAll();
                        }), new DialogueOption("No, don't crush them."));
            }
        });
    }

    private static final long calculateSecondariesCost(@NotNull final Player player, @NotNull final NPC npc) {
        val inventory = player.getInventory();
        int amount = 0;
        for (int i = 0; i < 28; i++) {
            val item = inventory.getItem(i);
            if (item == null) {
                continue;
            }
            val id = item.getId();
            val secondary = Utils.findMatching(HerbloreSecondary.values, sec -> (sec.rawItem == id && sec.processedItem != -1) || (sec.rawNotedItem == id && (sec == HerbloreSecondary.LAVA_SCALE || sec.processedNotedItem != -1)));
            if (secondary != null) {
                amount += item.getAmount();
                if (amount < 0) {
                    player.getDialogueManager().start(new Dialogue(player, npc) {
                        @Override
                        public void buildDialogue() {
                            npc("You have too many items there with you, I cannot crush them all.");
                        }
                    });
                    return -1;
                }
            }
        }
        return amount * 50L;
    }

    private enum HerbloreSecondary {
        UNICORN_HORN(237, 235),
        CHOCOLATE_BAR(1973, 1975),
        KEBBIT_TEETH(10109, 10111),
        GORAK_CLAWS(9016, 9018),
        BIRD_NEST(5075, 6693),
        DESERT_GOAT_HORN(9735, 9736),
        SPRING_SQIRK(10844, 10848),
        SUMMER_SQIRK(10845, 10849),
        AUTUMN_SQIRK(10846, 10850),
        WINTER_SQIRK(10847, 10851),
        CHARCOAL(973, 704),
        RUNE_SHARDS(6466, 6467),
        ASHES(592, 8865),
        RAW_KARAMBWAN(3142, 3152),
        POISON_KARAMBWAN(3146, 3153),
        COOKED_KARAMBWAN(3147, 3154),
        LAVA_SCALE(11992, 11994),
        BLUE_DRAGON_SCALE(ItemId.BLUE_DRAGON_SCALE, ItemId.DRAGON_SCALE_DUST),
        SUPERIOR_DRAGON_BONES(22124, 21975);

        private final int rawItem;
        private final int processedItem;
        private final int rawNotedItem;
        private final int processedNotedItem;

        private static final HerbloreSecondary[] values = values();

        HerbloreSecondary(final int rawItem, final int processedItem) {
            this.rawItem = rawItem;
            this.processedItem = processedItem;
            val notedRaw = ItemDefinitions.getOrThrow(rawItem).getNotedOrDefault();
            val notedProcessed = ItemDefinitions.getOrThrow(processedItem).getNotedOrDefault();
            this.rawNotedItem = notedRaw == rawItem ? -1 : notedRaw;
            this.processedNotedItem = notedProcessed == processedItem ? -1 : notedProcessed;
        }
    }

    private void makeUnfinishedPotions(final Player player, final NPC npc) {
        val vialsOfWater = player.getInventory().getAmountOf(NOTED_VIAL_OF_WATER.getId());
        val vialsOfBlood = player.getInventory().getAmountOf(NOTED_VIAL_OF_BLOOD.getId());
        val coins = player.getInventory().getAmountOf(995);
        val herbs = new HashMap<Herb, Item>();
        for (val entry : player.getInventory().getContainer().getItems().int2ObjectEntrySet()) {
            if (entry == null) {
                continue;
            }
            val item = entry.getValue();
            val herb = Herb.get(item.getId(), DiaryReward.DESERT_AMULET3.eligibleFor(player) || SkillcapePerk.HERBLORE.isEffective(player));
            if (herb == null || herb.getUnfinishedPotion().getId() == -1) {
                continue;
            }
            herbs.put(herb, item);
        }
        val price = getPrice(player);
        var amountForWater = player.getMemberRank().eligibleTo(MemberRank.ZENYTE_MEMBER) ? Integer.MAX_VALUE : price == 0 ? vialsOfWater : Math.min(coins / price, vialsOfWater);
        var amountForBlood = player.getMemberRank().eligibleTo(MemberRank.ZENYTE_MEMBER) ? Integer.MAX_VALUE : price == 0 ? vialsOfBlood : Math.min(coins / price, vialsOfBlood);

        for (val entry : herbs.entrySet()) {
            if (entry == null) {
                continue;
            }
            val herb = entry.getKey();
            val item = entry.getValue();
            var unfinishedPotion = new Item(-1);
            if (vialsOfBlood != 0) {
                unfinishedPotion = new Item(ItemId.CADANTINE_BLOOD_POTION_UNF);
            } else {
                unfinishedPotion = herb.getUnfinishedPotion();
            }
            var requestedAmount = item.getAmount();
            if (vialsOfBlood != 0) {
                if (amountForBlood < requestedAmount) {
                    requestedAmount = amountForBlood;
                }
            } else {
                if (amountForWater < requestedAmount) {
                    requestedAmount = amountForWater;
                }
            }
            if (requestedAmount == 0) {
                player.getDialogueManager().start(new NPCChat(player, npc.getId(), "I can't make any unfinished potions with those items. Make sure you have enough noted vials of water or blood, noted herbs and coins on you."));
                return;
            }
            if (vialsOfBlood != 0) {
                player.getInventory().deleteItem(new Item(NOTED_VIAL_OF_BLOOD.getId(), requestedAmount));
            } else {
                player.getInventory().deleteItem(new Item(NOTED_VIAL_OF_WATER.getId(), requestedAmount));
            }
            player.getInventory().deleteItem(new Item(995, requestedAmount * price));
            player.getInventory().deleteItem(new Item(item.getId(), requestedAmount));
            player.getInventory().addItem(unfinishedPotion.getDefinitions().getNotedId(), requestedAmount);
            if (vialsOfBlood != 0) {
                amountForBlood -= requestedAmount;
            } else {
                amountForWater -= requestedAmount;
            }
        }
        if (!herbs.isEmpty()) {
            player.getDialogueManager().start(new NPCChat(player, npc.getId(), "There, all done."));
        } else {
            player.getDialogueManager().start(new NPCChat(player, npc.getId(), "You don't seem to have the right supplies for me to make unfinished potions. Make sure that the and vials of water herbs are noted."));
        }
    }

    private void cleanHerbs(final Player player, final NPC npc) {
        val herbs = new HashMap<Herb, Item>();
        val coins = player.getInventory().getAmountOf(995);
        for (val entry : player.getInventory().getContainer().getItems().int2ObjectEntrySet()) {
            if (entry == null) {
                continue;
            }
            val item = entry.getValue();
            if (!item.getName().contains("Grimy")) {
                continue;
            }
            val herb = Herb.get(item.getId(), true);
            if (herb == null) {
                continue;
            }
            herbs.put(herb, item);
        }
        val price = getPrice(player);
        var amount = price == 0 ? Integer.MAX_VALUE : coins / price;

        for (val entry : herbs.entrySet()) {
            if (entry == null) {
                continue;
            }
            val herb = entry.getKey();
            val item = entry.getValue();
            val notedCleanedHerb = herb.getClean().getDefinitions().getNotedId();
            var requestedAmount = item.getAmount();
            if (amount < requestedAmount) {
                requestedAmount = amount;
            }
            if (requestedAmount == 0) {
                player.getDialogueManager().start(new NPCChat(player, npc.getId(), "You don't have enough coins for me to clean your herbs."));
                return;
            }
            if (player.getInventory().getAmountOf(notedCleanedHerb) + item.getAmount() < 0) {
                player.sendMessage("You currently have too many cleaned " + herb + "s, get rid of some and come back.");
                return;
            }
            player.getInventory().deleteItem(new Item(995, requestedAmount * price));
            player.getInventory().deleteItem(new Item(item.getId(), requestedAmount));
            if(player.getInventory().getFreeSlots() > 0) {
                player.getInventory().addItem(herb.getClean().getDefinitions().getNotedId(), requestedAmount);
            } else {
                World.spawnFloorItem(new Item(herb.getGrimy().getDefinitions().getNotedId(), (requestedAmount)), player,0, 200);
            }

            amount -= requestedAmount;
        }
        if (!herbs.isEmpty()) {
            player.getDialogueManager().start(new NPCChat(player, npc.getId(), "There, all done."));
        } else {
            player.getDialogueManager().start(new NPCChat(player, npc.getId(), "You don't seem to have any grimy herbs for me to clean right now, come back when you do."));
        }
    }

    private boolean decant(final Player player, final NPC npc, final int selectedDose) {
        val potions = new HashMap<Potion, PotionResult>();
        for (val entry : player.getInventory().getContainer().getItems().int2ObjectEntrySet()) {
            if (entry == null) {
                continue;
            }
            val item = entry.getValue();
            val potion = Potion.get(item.getId());
            if (potion == null) {
                continue;
            }
            if (potions.containsKey(potion)) {
                potions.get(potion).add(item);
            } else {
                val result = new PotionResult(potion, item);
                potions.put(potion, result);
            }
        }
        if (potions.isEmpty()) {
            player.getDialogueManager().start(new NPCChat(player, npc.getId(), "You don't seem to have any potions for me to decant."));
            return false;
        }
        for (val entry : potions.entrySet()) {
            if (entry == null) {
                continue;
            }
            val potion = entry.getKey();
            val result = entry.getValue();
            val totalDose = result.getTotalDose();
            val amount = totalDose / selectedDose;
            val remainder = totalDose % selectedDose;
            Int2IntFunction doseFunction = (dose) -> {
                val id = potion.getIds()[dose];
                return ItemDefinitions.get(id).getNotedId();
            };
            val decanted = new Item(doseFunction.apply(selectedDose - 1), amount);
            if (amount >= 500_000_000) {
                player.getDialogueManager().start(new NPCChat(player, npc.getId(), "I'm sorry but I can't decant that many at once."));
                return false;
            }
            if (player.getInventory().getAmountOf(decanted.getId()) + amount < 0) {
                player.getDialogueManager().start(new NPCChat(player, npc.getId(), "You have too many " +decanted.getName().toLowerCase() + "s right now, decanting would result in an overflow. Get rid of some and come back."));
                return false;
            }
            result.getItems().forEach(item -> player.getInventory().deleteItem(item));
            player.getInventory().addItem(doseFunction.apply(selectedDose - 1), amount);
            if (remainder != 0) {
                player.getInventory().addItem(doseFunction.apply(remainder - 1), 1).onFailure(item -> {
                    player.sendMessage("<col=ff0000><shad=000000>Some of the potion(s) were sent to your bank.");
                    player.getBank().add(item).onFailure(i -> {
                        player.sendMessage("<col=ff0000><shad=000000>Some of the potion(s) were dropped on the ground.");
                        World.spawnFloorItem(i, player);
                    });
                });
            }
        }
        return true;
    }

    private int getPrice(final Player player) {
        if (player.getMemberRank().eligibleTo(MemberRank.ONYX_MEMBER)) {
            return 0;
        } else if (player.getMemberRank().eligibleTo(MemberRank.DRAGONSTONE_MEMBER)) {
            return 50;
        } else if (player.getMemberRank().eligibleTo(MemberRank.DIAMOND_MEMBER)) {
            return 75;
        } else if (player.getMemberRank().eligibleTo(MemberRank.RUBY_MEMBER)) {
            return 100;
        } else if (player.getMemberRank().eligibleTo(MemberRank.EMERALD_MEMBER)) {
            return 125;
        } else if (player.getMemberRank().eligibleTo(MemberRank.SAPPHIRE_MEMBER)) {
            return 150;
        }
        return 200;
    }

    @Override
    public int[] getNPCs() {
        return new int[] { 4753 };
    }
}
