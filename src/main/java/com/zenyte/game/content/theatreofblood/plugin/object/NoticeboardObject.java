package com.zenyte.game.content.theatreofblood.plugin.object;

import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.interfaces.PartiesOverviewInterface;
import com.zenyte.game.content.theatreofblood.plugin.npc.MysteriousStranger;
import com.zenyte.game.item.enums.RareDrop;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.impl.NPCChat;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import mgi.types.config.items.ItemDefinitions;

/**
 * @author Tommeh | 5/21/2020 | 5:53 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class NoticeboardObject implements ObjectAction {
    
    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (option.equals("Read")) {
            if (!MysteriousStranger.completedInitialDialogue(player)) {
                player.getDialogueManager().start(new NPCChat(player, NpcId.MYSTERIOUS_STRANGER_8325, "Hey. Over here."));
                return;
            }
            if(TheatreOfBloodRaid.TOB_ENABLED) {
                PartiesOverviewInterface.refresh(player);
            } else {
                player.getDialogueManager().start(new Dialogue(player) {
                    @Override
                    public void buildDialogue() {
                        plain("The Theatre of Blood is currently disabled.");
                    }
                });
            }

        }
    }
    
    @Override
    public Object[] getObjects() {
        return new Object[]{32655};
    }
}
