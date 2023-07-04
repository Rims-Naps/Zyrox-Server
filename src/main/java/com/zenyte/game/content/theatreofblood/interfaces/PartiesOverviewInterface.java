package com.zenyte.game.content.theatreofblood.interfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;


/**
 * @author Tommeh | 5/21/2020 | 5:55 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class PartiesOverviewInterface extends Interface {

    @Override
    protected void attach() {
        put(3, 0, "Refresh");
        put(3, 1, "My/Make Party");
        put(16, "View Party");
    }
    
    @Override
    public void open(final Player player) {
        player.getInterfaceHandler().sendInterface(this);
    }

    @Override
    protected void build() {
        bind("Refresh", PartiesOverviewInterface::refresh);
        bind("My/Make Party", player -> {
            var party = VerSinhazaArea.getParty(player);
            if (party == null) {
                party = VerSinhazaArea.createParty(player);
            }
            party.updateInformation(player);
            PartyOverlayInterface.refresh(player, party);
        });
        bind("View Party", (player, slotId, itemId, option) -> {
            val party = VerSinhazaArea.getPartyByIndex(slotId);
            if (party == null) {
                refresh(player);
                return;
            }
            party.updateInformation(player);
        });
    }

    public static void refresh(final Player player) {
        val dispatcher = player.getPacketDispatcher();
        
        player.getVarManager().sendVar(1740, VerSinhazaArea.getParty(player) != null ? 0 : -1);
        GameInterface.TOB_PARTIES_OVERVIEW.open(player);
        player.getPacketDispatcher().sendClientScript(2524, -1, -1);

        val currentParty = VerSinhazaArea.getParty(player);
        for (int index = 0; index < 46; index++) {
            val party = VerSinhazaArea.getPartyByIndex(index);
            if (party == null || party.getRaid() != null) {
                dispatcher.sendClientScript(2340, index, "");
                continue;
            }
            if (party.getLeader() == null) {
                continue;
            }
            val builder = new StringBuilder();
            builder.append((currentParty == party ? Colour.WHITE.wrap(party.getLeader().getName()) : party.getLeader().getName()) + "|");
            builder.append(party.getMembers().size() + "|");
            builder.append(party.getPreferredSize() + "|");
            builder.append(party.getPreferredCombatLevel() + "|");
            builder.append(party.getAge() + "|");
            player.getPacketDispatcher().sendClientScript(2340, index, builder.toString());
        }
    }

    
    @Override
    public GameInterface getInterface() {
        return GameInterface.TOB_PARTIES_OVERVIEW;
    }
}
