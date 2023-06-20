package com.zenyte.game.content.theatreofblood.plugin.object;

import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.interfaces.PartyOverlayInterface;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Tommeh | 5/26/2020 | 10:33 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class TheatreVyreOrator implements ObjectAction {

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        val party = VerSinhazaArea.getParty(player);
        if (party == null) {
            return;
        }
        if (option.equals("Talk-to")) {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    npc(NpcId.VYRE_ORATOR, "Lady Verzik Vitur lets people in here to perform, not to<br>chat.", 1);
                    options(TITLE, "What am I supposed to do in here?", "I want to resign from the party.", "Sorry, I'll get on with it.")
                            .onOptionOne(key(5))
                            .onOptionTwo(key(10))
                            .onOptionThree(key(15));

                    player(5, "What am I supposed to do in here?");
                    npc(NpcId.VYRE_ORATOR, "Pass through the barrier and face your challenge. If<br>you survive, and your struggle entertains Verzik, she<br>will grant you freedom from the blood tithes.", 6);
                    player("Okay.");

                    player(10, "I want to resign from the party.").executeAction(() -> resign(player, party));

                    player(15, "Sorry, I'll get on with it.");
                }
            });
        } else if (option.equals("Resign")) {
            resign(player, party);
        }
    }

    private static void resign(final Player player, final RaidingParty party) {
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                options("There is no penalty for resigning now.", "Resign and leave the Theatre.", "Do not resign.")
                        .onOptionOne(() -> {
                            PartyOverlayInterface.fadeRed(player, "Nice try.");
                            WorldTasksManager.schedule(() -> {
                                party.removeMember(player);
                                player.setLocation(TheatreOfBloodRaid.outsideLocation);
                                PartyOverlayInterface.fade(player, 200, 0, "Nice try.");
                            });

                        })
                        .onOptionTwo(key(5));

                player(5, "Actually, I'll stay in for now.");
                npc(NpcId.VYRE_ORATOR, "As you wish.", 6);
            }
        });
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{ObjectId.VYRE_ORATOR, ObjectId.VYRE_ORATOR_32757};
    }
}
