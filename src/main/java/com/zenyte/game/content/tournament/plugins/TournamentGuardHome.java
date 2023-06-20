package com.zenyte.game.content.tournament.plugins;

import com.zenyte.game.content.tournament.Tournament;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.OptionsMenuD;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Tommeh | 07/06/2019 | 00:01
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class TournamentGuardHome extends NPCPlugin {

    @Override
    public void handle() {
        bind("Talk-to", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                player.getDialogueManager().start(new Dialogue(player) {

                    @Override
                    public void buildDialogue() {
                        npc(10012, "Greetings warrior, I'm the Tournament Guard. What can I do for you?", 1);
                        options(TITLE, "Can you tell me more about the tournament system?", "Can I view the current tournaments?", "Nevermind.")
                                .onOptionOne(() -> setKey(5))
                                .onOptionTwo(() -> setKey(15))
                                .onOptionThree(() -> setKey(25));
                        player(5, "Can you tell me more about this tournament?");
                        npc(10012, "Yes of course! Every once in awhile a tournament will be started. Everyone is able to join and you will be granted a reward if you want to win all rounds!", 6);
                        npc(10012, "Every tournament comes with a selected preset load-out. All the participants will be using this preset during their fights.", 7);
                        npc(10012, "While you're in the lobby, you will however be able to take supplies like runes, food, potions and special-attack weapons from the Tournament Merchant.", 8);
                        npc(10012, "In order to participate, you must bank your inventory and equipment. You cannot enter the tournament if you're still holding any items with you!", 9).executeAction(() -> setKey(1));

                        player(15, "Can I view the current tournaments?");
                        npc(10012, "Yes of course! Here's an overview.", 16).executeAction(() -> viewTournaments(player));
                        player(25, "Nevermind.");
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
        bind("View Tournaments", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                viewTournaments(player);
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

    private void viewTournaments(final Player player) {
        val tournaments = new ArrayList<>(Tournament.tournaments);
        tournaments.removeIf(Tournament::isFinished);
        val tournamentsNameList = new ArrayList<String>(tournaments.size());
        for (val tournament : tournaments) {
            tournamentsNameList.add(tournament.toString());
        }
        player.getDialogueManager().start(new OptionsMenuD(player, "Select a Tournament", tournamentsNameList.toArray(new String[0])) {
            @Override
            public void handleClick(final int slotId) {
                val tournament = tournaments.get(slotId);
                tournament.getLobby().teleportPlayer(player);
            }

            @Override
            public boolean cancelOption() {
                return true;
            }
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] { 10012 };
    }
}
