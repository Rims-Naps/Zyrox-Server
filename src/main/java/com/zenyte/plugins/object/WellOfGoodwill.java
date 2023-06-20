package com.zenyte.plugins.object;

import com.zenyte.Constants;
import com.zenyte.game.BonusCoxManager;
import com.zenyte.game.BonusTobManager;
import com.zenyte.game.BonusXpManager;
import com.zenyte.game.RuneDate;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;
import lombok.var;

import java.util.Date;

/**
 * @author Cresinkel
 */
public class WellOfGoodwill implements ObjectAction {

    public static boolean DEPOBOX = false;
    public static boolean BONUSSLAYER = false;
    public static boolean BONUSPURPLES = false;
    public static long expirationDateDepo;
    public static long expirationDateBonusSlayer;
    public static long expirationDateBonusPurples;
    public int donationTotal;
    private static final String[] COMMUNITY_EVENTS = {
            "BXP", "COXBOOST", "DEPOBOX", "BONUSSLAYER", "BONUSPURPLES", "TOBBOOST"
    };
    private int amountOfCox;
    private int amountOfBxp;
    private int amountOfDepo;
    private int amountOfSlayer;
    private int amountOfPurple;
    private int amountOfTob;

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (option.equals("Donate")) {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    doubleItem(ItemId.COINS_995,ItemId.PLATINUM_TOKEN, "Via here you can trade in your hard earned gp for gambling tokens.");
                    item(ItemId.SURVIVAL_TOKEN, "These gambling tokens can be used to spin a wheel and get either a rare, super rare or extremely rare pet/cosmetic.");
                    item(ItemId.QUEST_LIST, "Trade-ins of 100m and more are broadcasted!");
                    options("1 gambling token costs 10m in gp or platinum tokens.", "I understand that I won't get my gp back after buying a token.", "Cancel.")
                            .onOptionOne(() -> buyWithGpOrPlats(player));
                }
            });
        }
        if (option.equals("Donate-items")) {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    doubleItem(ItemId.BANDOS_CHESTPLATE,ItemId.TOXIC_BLOWPIPE_EMPTY, "Via here you can trade in your hard earned items for gambling tokens.");
                    item(ItemId.SURVIVAL_TOKEN, "These gambling tokens can be used to spin a wheel and get either a rare, super rare or extremely rare pet/cosmetic.");
                    item(ItemId.QUEST_LIST, "Trade-ins of 100m and more are broadcasted!");
                    options("1 gambling token costs 20m worth of items according to G.E. value.", "I understand that I won't get my items back after buying a token.", "Cancel.")
                            .onOptionOne(() -> buyWithItems(player));

                }
            });
        }
        if (option.equals("Info")) {
            player.getPacketDispatcher().sendURL("https://forums.zenyte.com/topic/11678-october%C2%A02022-well-of-goodwill-and-qol-bug-fixes/");
        }
        if (option.equals("Event")) {
            player.sendMessage("The amount needed to get a community boost is " + (200_000_000 - donationTotal) + ".");
        }
    }

    private void buyWithGpOrPlats(Player player) {
        player.getDialogueManager().finish();
        player.sendInputInt("How much gambling tokens do you want to buy at 10m each?", amount -> {

            var playersGp = player.getInventory().getAmountOf(ItemId.COINS_995);
            var playersPlats = player.getInventory().getAmountOf(ItemId.PLATINUM_TOKEN);
            var buyAbleViaPlats = (playersPlats/10000);
            var buyAbleViaGp = (playersGp/10000000);
            var buyAbleTokens = buyAbleViaGp + buyAbleViaPlats;

            if (amount > buyAbleTokens) {
                player.sendMessage("You do not have the money to buy " + amount + " gambling tokens.");
                return;
            }
            if (amount > 200) {
                player.sendMessage("You can not buy more than 200 gambling tokens at once.");
                return;
            }
            if (amount < buyAbleTokens) {
                buyAbleTokens = amount;
            }
            if (buyAbleTokens >= 10) {
                broadcastTradeIn(player, buyAbleTokens);
            }
            addDonation(buyAbleTokens * 10000000, player);
            if (buyAbleViaPlats > 0) {
                for (int i = buyAbleViaPlats; buyAbleTokens>0 && i>0; i--) {
                    player.getInventory().deleteItem(ItemId.PLATINUM_TOKEN, 10000);
                    player.getInventory().addOrDrop(ItemId.SURVIVAL_TOKEN, 1);
                    buyAbleTokens -= 1;
                }
            }
            if (buyAbleViaGp > 0) {
                for (int i = buyAbleViaGp; buyAbleTokens>0 && i>0; i--) {
                    player.getInventory().deleteItem(ItemId.COINS_995, 10000000);
                    player.getInventory().addOrDrop(ItemId.SURVIVAL_TOKEN, 1);
                    buyAbleTokens -= 1;
                }
            }
        });
    }

    private void buyWithItems(Player player) {
        player.getDialogueManager().finish();
        player.sendInputItem("What item do you wish to turn in for tokens?", item -> {
            var itemValue = item.getGEDefinitions().getPrice();
            if (!player.getInventory().containsItem(item.getId(), 1)) {
                player.sendMessage("You do not have this item in your inventory.");
                return;
            }
            if (itemValue < 20000000) {
                player.sendMessage("This item is worth less than 20m.");
                return;
            }
            finishItemSell(player,item);
        });
    }

    private void finishItemSell(Player player, Item item) {
        player.getDialogueManager().finish();
        var itemValue = item.getGEDefinitions().getPrice();
        var buyAbleTokens = (itemValue/20000000);
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                options("Your " + item.getName() + " is worth " + itemValue + " or " + buyAbleTokens + " gambling tokens.", "Yes trade my " + item.getName() + " for " + buyAbleTokens + " gambling tokens.", "Cancel.")
                        .onOptionOne(() -> {
                            if (buyAbleTokens > 0) {
                                player.getInventory().deleteItem(item.getId(), 1);
                                for (int i = buyAbleTokens; i>0; i--) {
                                    player.getInventory().addOrDrop(ItemId.SURVIVAL_TOKEN, 1);
                                }
                                addDonation(itemValue, player);
                                if (itemValue >= 100000000) {
                                    broadcastTradeIn(player, item);
                                }
                            }
                        });

            }
        });
    }

    private void broadcastTradeIn(Player player, int tokens) {
        WorldBroadcasts.broadcast(player, BroadcastType.TRADE_IN, tokens*10000000);
    }

    private void broadcastTradeIn(Player player, Item item) {
        WorldBroadcasts.broadcast(player, BroadcastType.TRADE_IN, item.getGEDefinitions().getPrice());
    }

    private void addDonation(int value, Player player) {
        donationTotal+=value;
        amountOfBxp = 0;
        amountOfCox = 0;
        amountOfDepo = 0;
        amountOfSlayer = 0;
        amountOfPurple = 0;
        amountOfTob = 0;
        while (donationTotal >= 200_000_000) {
            String event = Utils.random(COMMUNITY_EVENTS);
            switch (event) {
                case "BXP":
                    amountOfBxp += 1;
                    break;
                case "COXBOOST":
                    amountOfCox += 1;
                    break;
                case "DEPOBOX":
                    amountOfDepo += 1;
                    break;
                case "BONUSSLAYER":
                    amountOfSlayer += 1;
                    break;
                case "BONUSPURPLES":
                    amountOfPurple += 1;
                    break;
                case "TOBBOOST":
                    amountOfTob += 1;
                    break;
            }
            donationTotal -= 200_000_000;
        }
        if (amountOfBxp > 0) {
            WorldBroadcasts.broadcast(player,BroadcastType.WELL_EVENT,"BXP", 24*amountOfBxp);
            BonusXpManager.set((long) amountOfBxp*60*60*1000*24 + (Constants.BOOSTED_XP ? BonusXpManager.expirationDate : RuneDate.currentTimeMillis()));
        }
        if (amountOfCox > 0) {
            WorldBroadcasts.broadcast(player,BroadcastType.WELL_EVENT,"COX", 24*amountOfCox);
            BonusCoxManager.set((long) amountOfCox*24*60*60*1000 + (Constants.BOOSTED_COX ? BonusCoxManager.expirationDateCox : RuneDate.currentTimeMillis()));
        }
        if (amountOfDepo > 0) {
            WorldBroadcasts.broadcast(player,BroadcastType.WELL_EVENT,"DEPO", 24*amountOfDepo);
            expirationDateDepo = (DEPOBOX ? expirationDateDepo : RuneDate.currentTimeMillis()) + (24*60*60*1000) * amountOfDepo;
        }
        if (amountOfSlayer > 0) {
            WorldBroadcasts.broadcast(player,BroadcastType.WELL_EVENT,"SLAYER", 24*amountOfSlayer);
            expirationDateBonusSlayer = (BONUSSLAYER ? expirationDateBonusSlayer : RuneDate.currentTimeMillis()) + (24*60*60*1000) * amountOfSlayer;
        }
        if (amountOfPurple > 0) {
            WorldBroadcasts.broadcast(player,BroadcastType.WELL_EVENT,"PURPLES", 24*amountOfPurple);
            expirationDateBonusPurples = (BONUSPURPLES ? expirationDateBonusPurples : RuneDate.currentTimeMillis()) + (24*60*60*1000) * amountOfPurple;
        }
        if (amountOfTob > 0) {
            WorldBroadcasts.broadcast(player,BroadcastType.WELL_EVENT,"TOB", 24*amountOfTob);
            BonusTobManager.set((long) amountOfTob*24*60*60*1000 + (Constants.BOOSTED_TOB ? BonusTobManager.expirationDateTob : RuneDate.currentTimeMillis()));
        }
    }

    public static final void checkIfFlip() {
        if (DEPOBOX) {
            if (expirationDateDepo < System.currentTimeMillis()) {
                DEPOBOX = false;
                expirationDateDepo = 0;
                for (val player : World.getPlayers()) {
                    player.sendMessage("<col=FF0000><shad=000000>Special deposit boxes around Zenyte are no longer open!</col></shad>");
                }
            }
        } else {
            if (expirationDateDepo > 0) {
                DEPOBOX = true;
                for (val player : World.getPlayers()) {
                    player.sendMessage("<col=00FF00><shad=000000>Special deposit boxes around Zenyte are open until " + new Date(WellOfGoodwill.expirationDateDepo).toString() + "!</col></shad>");
                }
            }
        }
        if (BONUSSLAYER) {
            if (expirationDateBonusSlayer < System.currentTimeMillis()) {
                BONUSSLAYER = false;
                expirationDateBonusSlayer = 0;
                for (val player : World.getPlayers()) {
                    player.sendMessage("<col=FF0000><shad=000000>Slayer Points are not longer boosted by 50%!</col></shad>");
                }
            }
        } else {
            if (expirationDateBonusSlayer > 0) {
                BONUSSLAYER = true;
                for (val player : World.getPlayers()) {
                    player.sendMessage("<col=00FF00><shad=000000>Slayer Points are boosted by 50% until " + new Date(WellOfGoodwill.expirationDateBonusSlayer).toString() + "!</col></shad>");
                }
            }
        }
        if (BONUSPURPLES) {
            if (expirationDateBonusPurples < System.currentTimeMillis()) {
                BONUSPURPLES = false;
                expirationDateBonusPurples = 0;
                for (val player : World.getPlayers()) {
                    player.sendMessage("<col=FF0000><shad=000000>COX Purples are no longer dropped at a higher rate!</col></shad>");
                }
            }
        } else {
            if (expirationDateBonusPurples > 0) {
                BONUSPURPLES = true;
                for (val player : World.getPlayers()) {
                    player.sendMessage("<col=00FF00><shad=000000>COX Purples are dropped at a higher rate until " + new Date(WellOfGoodwill.expirationDateBonusPurples).toString() + "!</col></shad>");
                }
            }
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                40047
        };
    }
}
