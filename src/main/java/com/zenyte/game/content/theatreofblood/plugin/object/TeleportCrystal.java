package com.zenyte.game.content.theatreofblood.plugin.object;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.skills.magic.spells.teleports.TeleportCollection;
import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.interfaces.PartyOverlayInterface;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Cresinkel
 */

public class TeleportCrystal implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        val party = VerSinhazaArea.getParty(player);
        if (option.equals("Use")) {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    options("Make sure you've collected all your stuff!","Leave the Theatre.","Stay in the Theatre.").onOptionOne(() -> {
                        WorldTasksManager.schedule(new TickTask() {
                            @Override
                            public void run() {
                                switch (ticks++) {
                                    case 1:
                                        PartyOverlayInterface.fadeRed(player, "You fought well.");
                                        break;
                                    case 2:
                                        if(party.getMembers().size() > 1) {
                                            party.removeMember(player);
                                        }
                                        player.setLocation(TheatreOfBloodRaid.outsideLocation);
                                        PartyOverlayInterface.fade(player, 200, 0, "You fought well.");
                                        PartyOverlayInterface.refresh(player, party);
                                        stop();
                                        break;
                                }
                            }
                        }, 0, 0);
                    });
                }
            });
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{ObjectId.TELEPORT_CRYSTAL};
    }
}
