package com.zenyte.game.content.theatreofblood.interfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Tommeh | 5/21/2020 | 5:18 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class PartyOverlayInterface extends Interface {

    @Override
    protected void attach() {
        put(9, "Party members");
        put(13, "Exit Spectating");
    }

    @Override
    protected void build() {
        bind("Exit Spectating", player -> {
            val party = VerSinhazaArea.getParty(player, false, false, true);
            if (party != null && party.getRaid() != null) {
                party.getRaid().getSpectators().remove(player.getUsername());
            }

            PartyOverlayInterface.fadeRed(player, "");
            WorldTasksManager.schedule(() -> {
                PartyOverlayInterface.fade(player, 200, 0, "");
                player.setLocation(TheatreOfBloodRaid.outsideLocation);
            });
        });
    }

    @Override
    public void open(final Player player) {
        player.getInterfaceHandler().sendInterface(this);
    }

    public static void refresh(final Player player, final RaidingParty party) {
        val optionalPlugin = GameInterface.TOB_PARTY.getPlugin();
        if (!optionalPlugin.isPresent()) {
            return;
        }
        val plugin = optionalPlugin.get();

        plugin.open(player);

        val builder = new StringBuilder();
        for (int index = 0; index < 5; index++) {
            if (party == null || index >= party.getMembers().size()) {
                builder.append("-<br>");
                continue;
            }
            val member = party.getMember(index);
            builder.append(member.getName() + "<br>");
        }
        player.getVarManager().sendBit(6440, party != null ? 1 : 0);
        player.getPacketDispatcher().sendComponentText(plugin.getInterface(), plugin.getComponent("Party members"), builder.toString());
    }
    
    public static void fade(final Player player, final int arg1, final int arg2, final String text) {
        player.getPacketDispatcher().sendClientScript(2306, arg1, arg2, text);
    }
    
    public static void fadeRed(final Player player, final String text) {
        player.getPacketDispatcher().sendClientScript(2307, text);
    }
    
    public static void fadeWhite(final Player player, final String text) {
        player.getPacketDispatcher().sendClientScript(2308, text);
    }
    
    @Override
    public GameInterface getInterface() {
        return GameInterface.TOB_PARTY;
    }
}
