package com.zenyte.game.content.chambersofxeric;

import com.zenyte.game.content.chambersofxeric.party.RaidParty;
import com.zenyte.game.content.clans.ClanManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.kourend.GreatKourend;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * @author Kris | 06/09/2019 19:24
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class MountQuidamortemArea extends GreatKourend {
    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[] {
                new RSPolygon(new int[][]{
                        { 1228, 3581 },
                        { 1223, 3579 },
                        { 1219, 3572 },
                        { 1220, 3563 },
                        { 1218, 3557 },
                        { 1230, 3543 },
                        { 1262, 3553 },
                        { 1266, 3557 },
                        { 1266, 3561 },
                        { 1272, 3567 },
                        { 1265, 3572 },
                        { 1265, 3578 },
                        { 1258, 3578 },
                        { 1254, 3580 },
                        { 1251, 3578 },
                        { 1243, 3581 },
                        { 1232, 3583 }
                })
        };
    }

    @Override
    public void enter(final Player player) {

    }

    @Override
    public void leave(final Player player, final boolean logout) {
        onLeave(player);
    }

    static final void onLeave(@NotNull final Player player) {
        val channel = player.getSettings().getChannel();
        if (channel == null) {
            return;
        }
        val party = channel.getRaidParty();
        if (party == null) {
            return;
        }
        val raid = party.getRaid();
        if (raid != null && raid.getPlayers().contains(player)) {
            return;
        }
        val owner = party.getPlayer().equalsIgnoreCase(player.getUsername());
        player.sendMessage(Colour.RS_PINK.wrap(owner ? "You are no longer eligible to lead the party." : "You have been removed from the party."));
        if (owner) {
            if (appiontAnotherPartyLeader(player)) {
                return;
            }
            RaidParty.advertisedParties.remove(party);
            channel.setRaidParty(null);
            if (raid != null) {
                val players = new ObjectOpenHashSet<>(party.getRaid().getPlayers());
                for (val target : channel.getMembers()) {
                    if (target == null || !target.inArea(MountQuidamortemArea.class)) {
                        continue;
                    }
                    players.add(target);
                }
                players.remove(player);
                players.forEach(p -> p.sendMessage(Colour.RS_PINK.wrap(player.getName() + " has debanded the party.")));
                raid.destroy(false, true);
            }
        }
    }

    public static final boolean appiontAnotherPartyLeader(@NotNull final Player player) {
        val channel = player.getSettings().getChannel();
        if (channel == null) {
            return false;
        }
        val party = channel.getRaidParty();
        if (party == null) {
            return false;
        }
        val raid = party.getRaid();
        if (raid != null && raid.getPlayers().contains(player)) {
            return false;
        }
        if (raid != null) {
            val eligibleLeaders = new ObjectArrayList<Player>();
            for (val p : raid.getPlayers()) {
                if (p == null || p.isNulled() || p.isFinished() || p.isLoggedOut() || !channel.getMembers().contains(p) || p == player) {
                    continue;
                }
                if (ClanManager.canKick(p, channel)) {
                    eligibleLeaders.add(p);
                }
            }
            if (!eligibleLeaders.isEmpty()) {
                eligibleLeaders.sort(Comparator.comparingInt(c -> ClanManager.getRank(c, channel).getId()));
                val bestChoice = eligibleLeaders.get(eligibleLeaders.size() - 1);
                party.setPlayer(bestChoice.getUsername());
                raid.refreshTab();
                raid.getPlayers().forEach(p -> {
                    if (p == bestChoice) {
                        p.sendMessage(Colour.RS_PINK.wrap("You are the new party leader."));
                    } else {
                        p.sendMessage(Colour.RS_PINK.wrap(bestChoice.getName() + " is your new party leader."));
                    }
                });
                return true;
            }
        }
        return false;
    }

    @Override
    public String name() {
        return "Mount Quidamortem";
    }
}
