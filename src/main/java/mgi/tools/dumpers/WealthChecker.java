package mgi.tools.dumpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zenyte.game.content.skills.farming.Farming;
import com.zenyte.game.content.skills.farming.FarmingSpot;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.VarManager;
import com.zenyte.game.world.entity.player.container.impl.bank.Bank;
import com.zenyte.game.world.entity.player.dailychallenge.ChallengeAdapter;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.DailyChallenge;
import com.zenyte.game.world.entity.player.perk.Perk;
import com.zenyte.game.world.entity.player.perk.PerkAdapter;
import com.zenyte.game.world.entity.player.punishments.Punishment;
import com.zenyte.game.world.entity.player.punishments.PunishmentManager;
import com.zenyte.game.world.entity.player.punishments.PunishmentType;
import mgi.types.config.items.ItemDefinitions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class WealthChecker {

    static final String PLAYER_SAVE_DIRECTORY = "./data/characters/";
    private static final Gson gson =
            new GsonBuilder()
                    .registerTypeAdapter(Perk.class, new PerkAdapter())
                    .registerTypeAdapter(FarmingSpot.class, Farming.deserializer())
                    .registerTypeAdapter(VarManager.class, VarManager.deserializer()).disableHtmlEscaping()
                    .registerTypeAdapter(DailyChallenge.class, new ChallengeAdapter())
                    .create();
    private static int accountsProcessed;
    private static int accountsTotal;
    private static List<String> failedFiles = new ArrayList<String>();
    private static Map<String, Long> coinsPeople = new HashMap<String, Long>();
    private static Map<String, Long> tokensPeople = new HashMap<String, Long>();
    private static Map<String, Long> tbowPeople = new HashMap<String, Long>();
    private static Map<String, Long> shardPeople = new HashMap<String, Long>();
    private static Map<String, Long> bond10 = new HashMap<String, Long>();
    private static Map<String, Long> bond50 = new HashMap<String, Long>();
    private static Map<String, Long> bond100 = new HashMap<String, Long>();
    private static Map<String, Integer> voteTotal = new HashMap<String, Integer>();

    private static Map<String, Long> totals = new HashMap<String, Long>();
    private static final int[] idsToCheck = { ItemId.COINS_995, ItemId.PLATINUM_TOKEN, ItemId.TWISTED_BOW, ItemId.CRYSTAL_SHARD, ItemId.CRYSTAL_KEY,
            ItemId.ROCKY, ItemId.HERON, ItemId.ROCK_GOLEM, ItemId.GIANT_SQUIRREL,
            30051, 13190, 30017, 30018, ItemId.DEXTEROUS_PRAYER_SCROLL, ItemId.ARCANE_PRAYER_SCROLL, ItemId.TWISTED_BUCKLER, ItemId.DRAGON_HUNTER_CROSSBOW,
    ItemId.DINHS_BULWARK, ItemId.ANCESTRAL_HAT, ItemId.ANCESTRAL_ROBE_TOP, ItemId.ANCESTRAL_ROBE_BOTTOM, ItemId.DRAGON_CLAWS, ItemId.ELDER_MAUL, ItemId.KODAI_WAND,
    ItemId.KODAI_INSIGNIA};
    private static final String[] blacklist = {"Mew2", "Corey", "Kris", "Gepan", "Grant", "Noele"};

    public static void main(String[] args) {
        File saveDir = new File(PLAYER_SAVE_DIRECTORY);
        accountsTotal = saveDir.listFiles().length;

        for(File file : saveDir.listFiles()) {
            try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
                if(reader == null) {
                    failedFiles.add(file.getName());
                    continue;
                }
                Player playerObj = gson.fromJson(reader, Player.class);
                if(playerObj == null) {
                    failedFiles.add(file.getName());
                    continue;
                }
                Player player = new Player(null, playerObj == null ?
                        null :
                        playerObj.getAuthenticator());
                String username = file.getName().substring(0, file.getName().length() - 5);
                boolean blackListed = false;
                for(String s : blacklist) {
                    if(username.equalsIgnoreCase(s)) {
                       blackListed = true;
                    }
                }
                if(blackListed) {
                    continue;
                }
                player.setBank(new Bank(player, playerObj.getBank()));
                player.getInventory().setInventory(playerObj.getInventory());
                Optional<Punishment> punishment = PunishmentManager.isPunishmentActive(username,
                        playerObj.getLastIP(), playerObj.getLastMAC(), PunishmentType.BAN);
                if (punishment.isPresent() && punishment.get().getDurationInHours() == 0) {
                    continue;
                }
                for(int i = 0; i < idsToCheck.length; i++) {
                    int amount = player.getBank().getAmountOf(idsToCheck[i]);
                    int amount2 = player.getInventory().getAmountOf(idsToCheck[i]);
                    int amount3 = playerObj.getEquipment().getAmountOf(idsToCheck[i]);
                    int followerId = playerObj.getFollower() != null ? playerObj.getFollower().getPet().itemId() : -1;
                    if(followerId != -1) {
                        for(int j = 0; j < idsToCheck.length; j++) {
                            if(idsToCheck[j] == followerId) {
                                totals.put(ItemDefinitions.get(idsToCheck[i]).getName(), 1 + totals.getOrDefault(ItemDefinitions.get(idsToCheck[i]).getName(), 0L));
                            }
                        }
                    }
                    long total = amount + amount2 + amount3;
                    if(total > 0) {
                        totals.put(ItemDefinitions.get(idsToCheck[i]).getName(), total + totals.getOrDefault(ItemDefinitions.get(idsToCheck[i]).getName(), 0L));
                        switch(idsToCheck[i]) {
                            case ItemId.TWISTED_BOW:
                                tbowPeople.put(username, total);
                                break;
                            case ItemId.PLATINUM_TOKEN:
                                if(total > 500_000) {
                                    tokensPeople.put(username, total);
                                }
                                break;
                            case ItemId.CRYSTAL_SHARD:
                                shardPeople.put(username, total);
                                break;
                            case 13190:
                                bond10.put(username, total);
                                break;
                            case 30017:
                                bond50.put(username, total);
                                break;
                            case 30018:
                                bond100.put(username, total);
                                break;
                            case 995:
                                if(total >= 500_000_000) {
                                    coinsPeople.put(username, total);
                                }
                                break;
                        }
                    }
                    int votePoints = playerObj.getNumericAttribute("vote_points").intValue();
                    if(votePoints > 0) {
                        totals.put("Vote points", votePoints + totals.getOrDefault("Vote points", 0L));
                        if(votePoints > 100) {
                            voteTotal.put(username, votePoints);
                        }
                    }
                }
                accountsProcessed++;
                System.out.println(accountsProcessed + "/" + accountsTotal + " accounts processed.");
            } catch (final Exception e) {
                e.printStackTrace();
                failedFiles.add(file.getName());
                continue;
            }
        }

        System.out.println(String.format("There were %d files that failed", failedFiles.size()));
        /*if(failedFiles.size() > 0) {
            for(String s : failedFiles) {
                System.out.println(s + " failed.");
            }
        }*/

        totals.forEach((name, amount) -> {
            System.out.println(String.format("There are %,d %s currently in game", amount, getPlural(amount, name)));
        });

        tokensPeople.forEach((name, amount) -> {
            System.out.println(name + " has " + String.format("%,d", amount) + " " + getPlural(amount, "Platinum token"));
        });

        System.out.println();
        /*
        tbowPeople.forEach((name, amount) -> {
            System.out.println(name + " has " + String.format("%,d", amount) + " " + getPlural(amount, "Twisted bow"));
        });

        System.out.println();

        shardPeople.forEach((name, amount) -> {
            if(amount > 100) {
                System.out.println(name + " has " + String.format("%,d", amount) + " " + getPlural(amount, "Crystal shards"));
            }
        });*/

        System.out.println();

        coinsPeople.forEach((name, amount) -> {
            System.out.println(name + " has " + String.format("%,d", amount) + " " + getPlural(amount, "Coins"));
        });

  /*      bond10.forEach((name, amount) -> {
            System.out.println(name + " has " + String.format("%,d", amount) + " " + getPlural(amount, "$10 Bond"));
        });

        System.out.println();

        bond50.forEach((name, amount) -> {
            System.out.println(name + " has " + String.format("%,d", amount) + " " + getPlural(amount, "$50 Bond"));
        });

        System.out.println();

        bond100.forEach((name, amount) -> {
            System.out.println(name + " has " + String.format("%,d", amount) + " " + getPlural(amount, "$100 Bond"));
        });

        System.out.println();

        voteTotal.forEach((name, amount) -> {
            System.out.println(name + " has " + String.format("%,d", amount) + " " + getPlural(amount, "Vote points"));
        });*/

    }

    private void printResults() {

    }

    private static String getPlural(int amount, String name) {
        if(name.endsWith("s")) {
            return name;
        }
        return name + (amount > 1 ? "s" : "");
    }

    private static String getPlural(long amount, String name) {
        if(name.endsWith("s")) {
            return name;
        }
        return name + (amount > 1 ? "s" : "");
    }
}
