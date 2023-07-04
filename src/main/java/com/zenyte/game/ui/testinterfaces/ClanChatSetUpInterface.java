package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.clans.ClanChannel;
import com.zenyte.game.content.clans.ClanManager;
import com.zenyte.game.content.clans.ClanRank;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.TextUtils;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;


/**
 * @author Tommeh | 27-10-2018 | 19:18
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ClanChatSetUpInterface extends Interface {

    @Override
    protected void attach() {
        put(10, "Disable/set prefix");
        put(13, "Set enter rank");
        put(16, "Set talk rank");
        put(19, "Set kick rank");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(getInterface());
        val username = player.getUsername();
        var channel = ClanManager.CLAN_CHANNELS.get(username);
        if (channel == null) {
            ClanManager.CLAN_CHANNELS.put(username, channel = new ClanChannel(username));
        }
        player.getPacketDispatcher().initFriendsList();
        val dispatcher = player.getPacketDispatcher();
        dispatcher.sendComponentText(getInterface(), getComponent("Disable/set prefix"), channel.isDisabled() ? "Chat disabled" :
                TextUtils.capitalize(channel.getPrefix()));
        dispatcher.sendComponentText(getInterface(), getComponent("Set enter rank"), channel.getEnterRank().getLabel());
        dispatcher.sendComponentText(getInterface(), getComponent("Set talk rank"), channel.getTalkRank().getLabel());
        dispatcher.sendComponentText(getInterface(), getComponent("Set kick rank"), channel.getKickRank().getLabel());
    }

    @Override
    protected void build() {
        bind("Disable/set prefix", (player, slotId, itemId, option) -> {
            val channel = ClanManager.CLAN_CHANNELS.get(player.getPlayerInformation().getUsername());
            if (option == 1) {
                ClanManager.setPrefix(player, true);
                return;
            }
            if (channel.isDisabled()) {
                player.sendMessage("You've already disabled your clan channel.");
                return;
            }
            ClanManager.setPrefix(player, false);
            player.sendMessage("Your clan channel has now been disabled!");
        });
        bind("Set enter rank", (player, slotId, itemId, option) -> {
            val channel = ClanManager.CLAN_CHANNELS.get(player.getPlayerInformation().getUsername());
            val rank = ClanRank.VALUES[option - 1];
            if (rank == null) {
                return;
            }
            channel.setEnterRank(rank);
            player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Set enter rank"), rank.getLabel());
        });
        bind("Set talk rank", (player, slotId, itemId, option) -> {
            val channel = ClanManager.CLAN_CHANNELS.get(player.getPlayerInformation().getUsername());
            val rank = ClanRank.VALUES[option - 1];
            if (rank == null) {
                return;
            }
            channel.setTalkRank(rank);
            player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Set talk rank"), rank.getLabel());
        });
        bind("Set kick rank", (player, slotId, itemId, option) -> {
            val channel = ClanManager.CLAN_CHANNELS.get(player.getPlayerInformation().getUsername());
            val rank = ClanRank.VALUES[option - 1];
            if (rank == null) {
                return;
            }
            channel.setKickRank(rank);
            player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Set kick rank"), rank.getLabel());
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.CLAN_CHAT_SETUP;
    }
}
