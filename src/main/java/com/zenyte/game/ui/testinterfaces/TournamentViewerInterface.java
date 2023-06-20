package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.tournament.plugins.TournamentLobby;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tommeh | 02/06/2019 | 21:35
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class TournamentViewerInterface extends Interface {

    @Override
    protected void attach() {
        put(4, "View Fight");
        put(8, "Players");
        put(9, "Round");
        put(10, "Refresh");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Refresh"), -1, 0, AccessMask.CLICK_OP1);
        refresh(player);
        player.getTemporaryAttributes().put("tournament spectating tile", player.getLocation());
    }

    @Override
    protected void build() {
        bind("Refresh", this::refresh);
        bind("View Fight", (player, slotId, itemId, option) -> {
            val obj = player.getArea();
            if (!(obj instanceof TournamentLobby)) {
                return;
            }
            val area = (TournamentLobby) obj;
            val tourneyPairs = new ArrayList<>(area.getTournament().getPairs());
            val pairObj = player.getTemporaryAttributes().get("tournament pairs for spectator viewer");
            if (!(pairObj instanceof List)) {
                return;
            }
            val pairs = (List<Pair<Player, Player>>) pairObj;
            val pair = pairs.get(slotId / 2);
            if (!tourneyPairs.contains(pair)) {
                player.sendMessage("That fight has already ended!");
                return;
            }
            player.addTemporaryAttribute("tournament_spectating", pair);
            GameInterface.TOURNAMENT_SPECTATING.open(player);
        });
    }

    public static final void refreshSpectator(@NotNull final Player spectator) {
        val pairObject = spectator.getTemporaryAttributes().get("tournament_spectating");
        if (!(pairObject instanceof Pair)) {
            //Remove this so it does not occur again.
            spectator.getTemporaryAttributes().remove("tournament_spectating");
            return;
        }
        val pair = (Pair) pairObject;
        assert pair.getLeft() instanceof Player;
        assert pair.getRight() instanceof Player;
        val left = (Player) pair.getLeft();
        val right = (Player) pair.getRight();
        val dispatcher = spectator.getPacketDispatcher();
        dispatcher.sendUpdateItemContainer(93, -70001, 0, left.getInventory().getContainer());
        dispatcher.sendUpdateItemContainer(611, -1, 0, right.getInventory().getContainer());
        dispatcher.sendClientScript(2180,
                left.getHitpoints(), left.getMaxHitpoints(),
                left.getPrayerManager().getPrayerPoints(), left.getSkills().getLevelForXp(Skills.PRAYER),
                left.getCombatDefinitions().getSpecialEnergy() * 10, 0, left.getName(), Integer.toString(left.getSkills().getCombatLevel()));
        dispatcher.sendClientScript(2181, right.getHitpoints(), right.getMaxHitpoints(),
                right.getPrayerManager().getPrayerPoints(), right.getSkills().getLevelForXp(Skills.PRAYER),
                right.getCombatDefinitions().getSpecialEnergy() * 10, 1, right.getName(), Integer.toString(right.getSkills().getCombatLevel()));
    }

    private void refresh(final Player player) {
        val obj = player.getArea();
        if (!(obj instanceof TournamentLobby)) {
            return;
        }
        val area = (TournamentLobby) obj;
        val builder = new StringBuilder();
        val pairs = new ArrayList<>(area.getTournament().getPairs());
        player.getTemporaryAttributes().put("tournament pairs for spectator viewer", pairs);
        for (val pair : pairs) {
            val left = pair.getLeft();
            val right = pair.getRight();
            builder.append(left.getName());
            builder.append(" vs ");
            builder.append(right.getName()).append("|");
        }
        player.getPacketDispatcher().sendClientScript(10600, builder.toString());
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Round"), area.getTournament().getRound());
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Players"), area.getTournament().getParticipants().size());
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("View Fight"), -1, pairs.size() * 2, AccessMask.CLICK_OP1);
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.TOURNAMENT_VIEWER;
    }
}
