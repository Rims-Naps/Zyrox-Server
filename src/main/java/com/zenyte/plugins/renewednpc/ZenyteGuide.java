package com.zenyte.plugins.renewednpc;

import com.zenyte.Constants;
import com.zenyte.api.client.query.adventurerslog.AdventurersLogIcon;
import com.zenyte.api.client.query.hiscores.UpdateHiscoreExpMode;
import com.zenyte.api.model.ExpMode;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.GameMode;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.cutscene.impl.EdgevilleCutscene;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 25/11/2018 16:13
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ZenyteGuide extends NPCPlugin {

    public static final Item[][] STARTER_ITEMS = {
            { // normal
                    new Item(995, 50000), new Item(841), new Item(882, 250),
                    new Item(558, 250), new Item(556, 250), new Item(557, 250),
                    new Item(555, 250), new Item(554, 250), new Item(1379), new Item(1323),
                    new Item(1171), new Item(303), new Item(362, 50), new Item(1351), new Item(1265),
                    new Item(22331), new Item(22333), new Item(22335), new Item(22711), new Item(4548) },
            { // ironman
                    new Item(12810), new Item(12811), new Item(12812)
            }, { //ult ironman
            new Item(12813), new Item(12814), new Item(12815)
    }, { //hc ironman
            new Item(20792), new Item(20794), new Item(20796)
    }

    };
    private static final Item EXTRA_TUTORIAL_GOLD = new Item(995, 25000);
    public static final Location FINAL_LOCATION = new Location(3088, 3489, 0);
    private static final Location FINAL_FACE_LOCATION = new Location(3088, 3480, 0);
    private static final Location HOME_ZENYTE_GUIDE = new Location(3098, 3505, 0);

    public static void finishAppearance(final Player player) {
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                npc(3308, "Good choice! Now would you like to view the tutorial so I can show you around our home area or do you want to skip it?", 1);
                options(2, "Would you like to view the tutorial?", "Yes. <col=00080>(Receive extra " + Utils.format(EXTRA_TUTORIAL_GOLD.getAmount()) + " gold)</col>", "No, I want to skip it.").onOptionOne(() -> {
                    player.putBooleanTemporaryAttribute("viewed_tutorial", true);
                    player.getPacketDispatcher().sendClientScript(10700, 1);
                    player.getPacketDispatcher().sendClientScript(42, 0, 200); //setting zoom depth
                    player.setLocation(EdgevilleCutscene.START_LOCATION);
                    player.getCutsceneManager().play(new EdgevilleCutscene());
                }).onOptionTwo(() -> {
                    player.putBooleanTemporaryAttribute("viewed_tutorial", false);
                    finishTutorial(player);
                });
            }
        });
    }

    private static final Location ZENYTE_GUIDE_LOCATION = new Location(3098, 3506, 0);

    public static void finishTutorial(final Player player) {
        player.getAppearance().setInvisible(false);

        player.unlock();
        if (player.getBooleanTemporaryAttribute("tutorial_rewatch")) {
            player.setLocation(ZENYTE_GUIDE_LOCATION);
            player.getInterfaceHandler().closeInterface(InterfacePosition.DIALOGUE);
            player.addTemporaryAttribute("tutorial_rewatch", 0);
            return;
        }
        player.setLocation(FINAL_LOCATION);
        player.setFaceLocation(FINAL_FACE_LOCATION);
        player.getDialogueManager().start(new Dialogue(player, 3308) {
            @Override
            public void buildDialogue() {
                npc("That should be everything regarding our home area.");
                npc("If I went a bit too fast for you, you can always come and visit me in the achievement hall and I'll show you around whenever you want.");
                npc("Good luck on your adventure.");
            }
        });
        //player.getDialogueManager().start(new NPCChat(player, 3308, "That should be everything regarding our home area.  Good luck on your adventure!"));

        val gameModeObj = player.getTemporaryAttributes().get("selected_game_mode");
        if (!(gameModeObj instanceof GameMode)) {
            return;
        }
        val gameMode = (GameMode) gameModeObj;

        val experienceModeObj = player.getTemporaryAttributes().get("selected_xp_mode");
        if (!(experienceModeObj instanceof String)) {
            return;
        }
        val rates = ((String) experienceModeObj).split("-");
        player.setGameMode(gameMode);
        player.setExperienceMultiplier(Integer.parseInt(rates[0]), Integer.parseInt(rates[1]));

        val ip = player.getPlayerInformation().getIpFromChannel();
        val count = Constants.starterIPMap.getOrDefault(ip, 0);
        if (count < 2) {
            Constants.starterIPMap.put(ip, count + 1);
            for (val item : STARTER_ITEMS[0]) {
                val it = new Item(item.getId(), item.getAmount(), DegradableItem.getDefaultCharges(item.getId(), 0));
                player.getInventory().addItem(it);
            }
        }
        if (player.isIronman()) {
            val items = STARTER_ITEMS[player.getGameMode().ordinal()];
            for (val item : items) {
                val it = new Item(item.getId(), item.getAmount(), DegradableItem.getDefaultCharges(item.getId(), 0));
                player.getInventory().addItem(it);
            }
        }
        if (player.getBooleanTemporaryAttribute("viewed_tutorial")) {
            player.getInventory().addItem(EXTRA_TUTORIAL_GOLD);
        }
        player.getTemporaryAttributes().remove("registration");
        player.getTemporaryAttributes().remove("viewed_tutorial");
        player.putBooleanAttribute("registered", true);
        val challenge = player.getDailyChallengeManager().getRandomChallenge();
        if (challenge == null) {
            return;
        }
        player.getDailyChallengeManager().assignChallenge(challenge);

        val builder = new StringBuilder();
        builder.append("<img=").append(BroadcastType.HELPFUL_TIP.getIcon()).append(">");
        builder.append(player.getGameMode().getCrown()).append(player.getName());
        builder.append(" has just joined Zenyte on a ").append(player.getCombatXPRate());
        if (player.getCombatXPRate() != player.getSkillingXPRate()) {
            builder.append('/').append(player.getSkillingXPRate());
        }
        builder.append("x account!");
        World.sendMessage(MessageType.UNFILTERABLE, builder.toString());
    }

    @Override
    public void handle() {
        bind("Talk-to", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                player.stopAll();
                player.faceEntity(npc);
                if (!player.getBooleanAttribute("registered")) {
                    player.getDialogueManager().start(new Dialogue(player, npc) {
                        @Override
                        public void buildDialogue() {
                            npc("Greetings! I see you are a new arrival to this world.");
                            npc("Before you continue your adventure, could you please select a game mode you see fit?").executeAction(() -> {
                                player.getTemporaryAttributes().put("ironman_setup", "register");
                                player.getVarManager().sendVar(281, 0);
                                player.getVarManager().sendBit(1777, 0);
                                GameInterface.GAME_MODE_SETUP.open(player);
                            });
                        }
                    });
                } else {
                    player.getDialogueManager().start(new Dialogue(player, npc) {

                        @Override
                        public void buildDialogue() {
                            npc("Hey! It's good to see you again, " + player.getName() + ". What can I do for you?");
                            options(TITLE, "Talk about my experience mode.", "Rewatch the tutorial.")
                                    .onOptionOne(() -> {
                                        if (player.getSkillingXPRate() <= 10) {
                                            setKey(5);
                                        } else {
                                            setKey(20);
                                        }
                                    })
                                    .onOptionTwo(() -> {
                                        player.getPacketDispatcher().sendClientScript(10700, 1);
                                        player.getPacketDispatcher().sendClientScript(42, 0, 200); //setting zoom depth
                                        player.setLocation(EdgevilleCutscene.START_LOCATION);
                                        player.addTemporaryAttribute("tutorial_rewatch", 1);
                                        player.getCutsceneManager().play(new EdgevilleCutscene());
                                    });
                            npc(5, "I see that you're on the <col=00080>" + player.getSkillingXPRate() + "x Combat & Skilling mode</col>. Would you be interested in changing that into an easier experience mode?");
                            if (player.getSkillingXPRate() == 10) {
                                options("Select the experience mode", "50x Combat & 25x Skilling", "Nevermind")
                                        .onOptionOne(() -> {
                                            setKey(10);
                                            if (Constants.WORLD_PROFILE.getApi().isEnabled()
                                                        && !Constants.WORLD_PROFILE.isPrivate()
                                                        && !Constants.WORLD_PROFILE.isBeta()) {
                                                new UpdateHiscoreExpMode(player, player.getApiExperienceMode(), ExpMode.FIFTY).execute();
                                            }
                                            player.setExperienceMultiplier(50, 25);
                                            player.sendAdventurersEntry(AdventurersLogIcon.OVERALL_SKILLING, player.getName() + " has just changed exp mode - they are now playing under the x50 mode!");
                                        })
                                        .onOptionTwo(() -> setKey(15));
                            } else {
                                options("Select the experience mode", "50x Combat & 25x Skilling", "10x Combat & 10x Skilling", "Nevermind")
                                        .onOptionOne(() -> {
                                            setKey(10);
                                            if (Constants.WORLD_PROFILE.getApi().isEnabled()
                                                        && !Constants.WORLD_PROFILE.isPrivate()
                                                        && !Constants.WORLD_PROFILE.isBeta()) {
                                                new UpdateHiscoreExpMode(player, player.getApiExperienceMode(), ExpMode.FIFTY).execute();
                                            }
                                            player.setExperienceMultiplier(50, 25);
                                            player.sendAdventurersEntry(AdventurersLogIcon.OVERALL_SKILLING, player.getName() + " has just changed exp mode - they are now playing under the x50 mode!");
                                        })
                                        .onOptionTwo(() -> {
                                            setKey(12);
                                            if (Constants.WORLD_PROFILE.getApi().isEnabled()
                                                        && !Constants.WORLD_PROFILE.isPrivate()
                                                        && !Constants.WORLD_PROFILE.isBeta()) {
                                                new UpdateHiscoreExpMode(player, player.getApiExperienceMode(), ExpMode.TEN).execute();
                                            }
                                            player.setExperienceMultiplier(10, 10);
                                            player.sendAdventurersEntry(AdventurersLogIcon.OVERALL_SKILLING, player.getName() + " has just changed exp mode - they are now playing under the x10 mode!");
                                        })
                                        .onOptionThree(() -> setKey(15));
                            }
                            npc(10, "There you go! You are now on the <col=00080>50x Combat & 10x Skilling experience mode</col>. Good luck!");
                            npc(12, "There you go! You are now on the <col=00080>10x Combat & 10x Skilling experience mode</col>. Good luck!");
                            npc(15, "That's fine, come back if you change your mind!");
                            npc(20, "I see that you're progressing very well, keep it up!");
                        }
                    });
                }
            }

            @Override
            public void execute(final Player player, final NPC npc) {
                player.stopAll();
                player.setFaceEntity(npc);
                handle(player, npc);
                if (npc.getLocation().getPositionHash() != HOME_ZENYTE_GUIDE.getPositionHash()) {
                    npc.setInteractingWith(player);
                }
            }
        });
        bind("Trade", (player, npc) -> player.getDialogueManager().start(new Dialogue(player, npc) {
            @Override
            public void buildDialogue() {
                npc("Would you like to purchase any of the three starter weapons? I can sell them to you for 200,000 each!");
                options("Purchase a starter weapon?",
                        new DialogueOption("Starter sword.", key(5)),
                        new DialogueOption("Starter bow.", key(10)),
                        new DialogueOption("Starter staff.", key(15)),
                        new DialogueOption("Cancel."));

                player(5, "I'll take a starter sword, please.").executeAction(() -> purchase(player, npc, 22331));
                player(10, "I'll take a starter bow, please.").executeAction(() -> purchase(player, npc, 22333));
                player(15, "I'll take a starter staff, please.").executeAction(() -> purchase(player, npc, 22335));
            }
        }));
    }

    private static final void purchase(@NotNull final Player player, @NotNull final NPC npc, final int weaponId) {
        player.getDialogueManager().finish();
        player.getDialogueManager().start(new Dialogue(player, npc) {
            @Override
            public void buildDialogue() {
                if (!player.getInventory().containsItem(995, 200_000)) {
                    npc("You need at least 200,000 coins to purchase it!");
                    return;
                }
                if (!player.getInventory().hasFreeSlots()) {
                    npc("Perhaps you should make some free space in your inventory first.");
                    return;
                }
                player.getInventory().deleteItem(new Item(995, 200_000));
                val weapon = new Item(weaponId, 1, DegradableItem.getFullCharges(weaponId));
                player.getInventory().addOrDrop(weapon);
                item(weapon, "The guide hands you a " + weapon.getName().toLowerCase() + ".");
                npc("Pleasure doing business with you.");
            }
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] { 3308 };
    }
}
