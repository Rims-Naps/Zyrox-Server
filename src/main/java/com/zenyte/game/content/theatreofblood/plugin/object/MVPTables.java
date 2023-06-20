package com.zenyte.game.content.theatreofblood.plugin.object;

import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Cresinkel
 */

public class MVPTables implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        val party = VerSinhazaArea.getParty(player);
        if (option.equals("Read")) {
            val mvp = getMVP(party);
            player.sendMessage("This raids MVP: " + mvp.getName() + "!");
            player.sendMessage("You died " + player.getNumericAttribute("tobdeaths") + " times.");
            for (val member : party.getPlayers()) {
                if (!member.getUsername().equals(player.getUsername())) {
                    player.sendMessage(member.getUsername() + " died " + member.getNumericAttribute("tobdeaths") + " times.");
                }
            }
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{ObjectId.STRATEGY_TABLE, ObjectId.WAR_TABLE};
    }

    private Player getMVP(RaidingParty party) {
        Player mvp = party.getLeader();
        for (Player p : party.getPlayers()) {
            if (p.getNumericAttribute("tobpoints").intValue() > mvp.getNumericAttribute("tobpoints").intValue()) {
                mvp = p;
            }
        }
        return mvp;
    }
}
