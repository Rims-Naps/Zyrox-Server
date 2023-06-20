package com.zenyte.game.content.clans;

import com.zenyte.Constants;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.util.TextUtils;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 28/01/2019 15:24
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ClanChannelFullBuilder extends ClanChannelBuilder {

    ClanChannelFullBuilder(final ClanChannel channel, final Player clanOwner) {
        super(ServerProt.CLANCHANNEL_FULL, channel, clanOwner);
    }

    @Override
    public ClanChannelBuilder build() {
        assert !buffer.isReadable() : "Buffer is already built";
        val members = channel.getMembers();
        val rankedMembers = channel.getRankedMembers();
        buffer.writeString(channel.getOwner());
        buffer.writeLong(TextUtils.stringToLong(channel.getPrefix()));
        buffer.writeByte(channel.getKickRank().getId() - 1);
        buffer.writeShort(members.size());
        for (val member : members) {
            buffer.writeString(member.getName());
            buffer.writeShort(Constants.WORLD_PROFILE.getNumber());
            buffer.writeByte(getRank(rankedMembers.get(member.getUsername()), member, clanOwner));
            buffer.writeString(Strings.EMPTY);
        }
        return this;
    }

}
