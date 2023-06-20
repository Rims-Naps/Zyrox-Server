package com.zenyte.game.content.theatreofblood.plugin.object;

import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.interfaces.PartyOverlayInterface;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.content.theatreofblood.plugin.item.VerzikCrystalShard;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectHandler;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.val;

/**
 * @author Tommeh | 5/22/2020 | 5:31 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class TheatreEntranceObject implements ObjectAction {

    private static final WorldObject noticeBoardObject = new WorldObject(32655, 10, 3, new Location(3662, 3218, 0));

    /*
    public static final List<String> beta_testers = new ArrayList<String>();

    public static final boolean isBetaTester(final Player player) {
        beta_testers.add("tob_beta_1");
        beta_testers.add("tob_beta_2");
        beta_testers.add("tob_beta_3");
        beta_testers.add("tob_beta_4");
        beta_testers.add("tob_beta_5");
        return beta_testers.contains(player.getUsername());
    }*/

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if(!TheatreOfBloodRaid.TOB_ENABLED) {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    plain("The Theatre of Blood is currently disabled.");
                }
            });
            return;
        }
        /*
        if (isBetaTester(player)) {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    plain("The Theatre of Blood is only open to beta testers at this stage.");
                }
            });
            return;
        }
        */
        val party = VerSinhazaArea.getParty(player);
        if (party == null) {
            val hasLuggage = !player.getInventory().getContainer().isEmpty() || !player.getEquipment().getContainer().isEmpty();
            val isIll = player.getToxins().isIll();
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    options("You are not in a party...", "Form or join a party", (hasLuggage || isIll ? "<str>" : "") + "Observe a specific party", (hasLuggage || isIll ? "<str>" : "") + "Observe a recent party", "Cancel")
                            .onOptionOne(() -> {
                                val plugin = ObjectHandler.getPlugin(Integer.toString(ObjectId.NOTICE_BOARD_32655));
                                if (plugin == null) {
                                    return;
                                }
                                plugin.handle(player, noticeBoardObject, noticeBoardObject.getName(), 1, "Read");
                            })
                            .onOptionTwo(() -> {
                                if (!player.getInventory().getContainer().isEmpty() || !player.getEquipment().getContainer().isEmpty()) {
                                    setKey(5);
                                    return;
                                }

                                if (player.getToxins().isIll()) {
                                    setKey(10);
                                    return;
                                }

                                finish();
                                player.sendInputName("Who would you like to observe?", name -> {
                                    val p = World.getPlayerByUsername(name.toLowerCase());
                                    spectate(player, p);
                                });
                            })
                            .onOptionThree(() -> {
                                if (!player.getInventory().getContainer().isEmpty() || !player.getEquipment().getContainer().isEmpty()) {
                                    setKey(5);
                                    return;
                                }

                                if (player.getToxins().isIll()) {
                                    setKey(10);
                                    return;
                                }

                                finish();
                                val parties = VerSinhazaArea.getParties();
                                if (parties.isEmpty()) {
                                    player.sendMessage("There is no party currently running TOB.");
                                    return;
                                }
                                spectate(player, parties.get(parties.keySet().toArray()[parties.size() - 1]).getLeader());
                            });

                    plain(5, "Spectators cannot bring luggage into the Theatre<br><br>Please visit the bank to unburden yourself.");
                    plain(10, "You appear to be ill; the vampyres would not appreciate your<br><br>company in their Theatre's viewing galleries while you're in that state.<br><br>Come back when you're feeling better.");
                }
            });
            return;
        }

        if (!party.isLeader(player) && party.getRaid() == null) {
            player.getDialogueManager().start(new PlainChat(player, "Your leader, " + party.getLeader().getName() + ", must go first."));
            return;
        }
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                if (!player.getBooleanAttribute("tob_death_warning")) {
                    plain(Colour.RED.wrap("Warning: ") + "The Theatre of blood is " + Colour.RED.wrap("dangerous.") + " Once you enter, you are at " + Colour.RED.wrap("risk of death") + ". The only method of escape is to resign or<br>defect the Theatre. " + Colour.RED.wrap("Teleporting is restricted") + ", and " + Colour.RED.wrap("logging out is<br>considered a death") + ". Your " + Colour.RED.wrap("items will be lost") + " if the whole party dies.");
                    plain("You will not see the warning again should you accept.");
                    options("Accept the warning and proceed?", "Yes - proceed.", "No - stay out.")
                            .onOptionOne(() -> {
                                player.addAttribute("tob_death_warning", 1);
                                if (player.getInventory().containsItem(VerzikCrystalShard.verzikCrystalShard) || player.getBooleanAttribute("verziks_crystals_warning")) {
                                    enter(player, party);
                                } else {
                                    setKey(10);
                                }
                            });
                } else {
                    if (player.getInventory().containsItem(VerzikCrystalShard.verzikCrystalShard) || player.getBooleanAttribute("verziks_crystals_warning")) {
                        enter(player, party);
                    } else {
                        options(Colour.RED.wrap("Only Verzik's crystals can teleport out of the Theatre."), "Go and buy teleport crystals.", "Enter the Theatre without any teleport crystals.", "Enter the Theatre, and don't ask this again.")
                                .onOptionTwo(() -> enter(player, party))
                                .onOptionThree(() -> {
                                    player.addAttribute("verziks_crystals_warning", 1);
                                    enter(player, party);
                                });
                    }
                }

                options(10, Colour.RED.wrap("Only Verzik's crystals can teleport out of the Theatre."), "Go and buy teleport crystals.", "Enter the Theatre without any teleport crystals.", "Enter the Theatre, and don't ask this again.")
                        .onOptionTwo(() -> enter(player, party))
                        .onOptionThree(() -> {
                            player.addAttribute("verziks_crystals_warning", 1);
                            enter(player, party);
                        });
            }
        });
    }

    private static void enter(final Player player, final RaidingParty party) {
        if(party.getRaid() != null && (party.getRaid().getActiveRoom().isStarted() && party.getMembers().size() > 1)) {
            player.sendMessage("The raid has already started. You must wait until the wave is complete to re-enter.");
            return;
        }
        if (!party.isLeader(player) && party.getRaid() != null) {
            party.getRaid().enter(player);
            return;
        }
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                options("Is your party ready? (Members: " + party.getMembers().size() + ")", "Yes, let's go!", "Cancel.")
                        .onOptionOne(() -> {
                            val raid = new TheatreOfBloodRaid(party);
                            party.setRaid(raid);
                            party.getPlayers().forEach((p) -> {
                                party.getLifeStates().put(p.getUsername(), "alive");
                            });
                            raid.enter(player);
                        });
            }
        });
    }

    private static void spectate(final Player spectator, final Player player) {
        if (player == null || player.getSocialManager().isOffline()) {
            spectator.sendMessage("That player is offline, or has privacy mode enabled.");
            return;
        }
        val party = VerSinhazaArea.getParty(player, true, false, true);
        if (party == null || party.getRaid() == null) {
            spectator.getDialogueManager().start(new PlainChat(spectator, player.getName() + " does not appear to be in the Theatre."));
            return;
        }
        PartyOverlayInterface.fade(spectator, 0, 0, "Seeking " + player.getName() + "...");
        WorldTasksManager.schedule(() -> {
            val raid = party.getRaid();
            val activeRoom = raid.getActiveRoom();
            PartyOverlayInterface.fade(spectator, 255, 0, activeRoom.getRoom().getName());
            raid.getSpectators().add(spectator.getUsername());
            party.initializeStatusHUD(spectator);
            spectator.setLocation(activeRoom.getSpectatingLocation());
        }, 2);
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{ObjectId.THEATRE_OF_BLOOD_32653};
    }
}
