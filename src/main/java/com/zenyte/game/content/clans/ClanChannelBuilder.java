package com.zenyte.game.content.clans;

import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.network.io.RSBuffer;
import lombok.Getter;

/**
 * @author Kris | 28/01/2019 15:56
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public abstract class ClanChannelBuilder {

    ClanChannelBuilder(final ServerProt prot, final ClanChannel channel, final Player clanOwner) {
        buffer = new RSBuffer(prot);
        this.channel = channel;
        this.clanOwner = clanOwner;
    }

    @Getter RSBuffer buffer;
    @Getter ClanChannel channel;
    Player clanOwner;

    int getRank(final ClanRank rank, final Player member, final Player owner) {
        if (owner.getUsername().equals(member.getUsername())) {
            return 7;
        } else if (member.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
            return 127;
        } else if (rank != null) {
            if (rank == ClanRank.FRIENDS) {
                return owner.getSocialManager().containsFriend(member.getUsername()) ? 0 : -1;
            }
            return rank.getId();
        }
        return owner.getSocialManager().containsFriend(member.getUsername()) ? 0 : -1;
    }

    abstract ClanChannelBuilder build();

}
