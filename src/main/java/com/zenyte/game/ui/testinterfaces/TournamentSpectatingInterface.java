package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.tournament.Tournament;
import com.zenyte.game.content.tournament.TournamentInstance;
import com.zenyte.game.content.tournament.plugins.TournamentLobby;
import com.zenyte.game.packet.out.FreeCam;
import com.zenyte.game.packet.out.IfOpenTop;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.ui.PaneType;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.processor.Listener;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Kris | 04/06/2019 23:06
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class TournamentSpectatingInterface extends Interface {

    @Listener(type = Listener.ListenerType.LOGIN)
    static void login(final Player player) {
        player.setHidden(false);
        player.send(new FreeCam(false));
        player.getPacketDispatcher().sendClientScript(2070, 0);
        player.getPacketDispatcher().sendClientScript(2221, 1);
    }

    @Override
    protected void attach() {

    }

    @Override
    public void close(final Player player, final Optional<GameInterface> replacement) {
        val pair = player.getTemporaryAttributes().remove("tournament_spectating");
        if (pair == null) {
            throw new IllegalStateException();
        }
        val obj = player.getArea();
        Tournament tournament = null;
        if (obj instanceof TournamentInstance) {
            tournament = ((TournamentInstance) obj).getTournament();
        } else if (obj instanceof TournamentLobby) {
            tournament = ((TournamentLobby) obj).getTournament();
        }
        if (tournament == null) {
            throw new IllegalStateException();
        }
        tournament.getLobby().getFight().getSpectatorMap().computeIfPresent((Pair<Player, Player>) pair, (p, list) -> {
            list.remove(player);
            return list.isEmpty() ? null : list;
        });
        val interfaceHandler = player.getInterfaceHandler();
        player.getPacketDispatcher().sendPane(interfaceHandler.getPane());
        for (val position : InterfacePosition.VALUES) {
            if (position.getGameframeInterfaceId() == -1 || position.equals(InterfacePosition.FRIENDS_TAB) || position.equals(InterfacePosition.JOURNAL_TAB_HEADER) || position.equals(InterfacePosition.ACCOUNT_MANAGEMENT)) {
                continue;
            }
            val gameInter = GameInterface.get(position.getGameframeInterfaceId());
            if (gameInter.isPresent()) {
                gameInter.get().open(player);
            } else {
                interfaceHandler.sendInterface(position, position.getGameframeInterfaceId());
            }
        }
        interfaceHandler.openJournal();
        GameInterface.GAME_NOTICEBOARD.open(player);
        player.setHidden(false);
        player.send(new FreeCam(false));
        player.resetFreeze();
        player.unlock();
        val spectatingTile = player.getTemporaryAttributes().remove("tournament spectating tile");
        player.setLocation(spectatingTile instanceof Location ? (Location) spectatingTile : tournament.getLobby().getLocation(TournamentLobby.SPECTATING_LOCATION));
        player.getPacketDispatcher().sendClientScript(2070, 0);
        player.getPacketDispatcher().sendClientScript(2221, 1);
    }

    @Override
    public void open(final Player player) {
        val obj = player.getArea();
        if (!(obj instanceof TournamentLobby)) {
            throw new IllegalStateException();
        }
        val pairObj = player.getTemporaryAttributes().get("tournament_spectating");
        if (!(pairObj instanceof Pair)) {
            throw new IllegalStateException();
        }
        val pair = (Pair<Player, Player>) pairObj;
        val area = (TournamentLobby) obj;
        player.getVarManager().sendBit(1463, 1);
        val location = area.getFight().getPairCastleMap().get(pair);
        area.getFight().getSpectatorMap().computeIfAbsent(pair, x -> new ArrayList<>()).add(player);
        player.setLocation(location);
        val dispatcher = player.getPacketDispatcher();
        dispatcher.sendClientScript(2070, 2);
        dispatcher.sendComponentSettings(154, 56, -1, 28, AccessMask.CLICK_OP10);
        dispatcher.sendComponentSettings(154, 57, -1, 28, AccessMask.CLICK_OP10);
        player.send(new IfOpenTop(PaneType.GAME_SCREEN));
        WorldTasksManager.schedule(() -> {
            if (!player.getInterfaceHandler().isPresent(GameInterface.TOURNAMENT_SPECTATING)) {
                return;
            }
            player.send(new FreeCam(true));
        });
        player.setHidden(true);
        player.stop(Player.StopType.INTERFACES, Player.StopType.ROUTE_EVENT, Player.StopType.WALK, Player.StopType.ACTIONS, Player.StopType.ANIMATIONS,
                Player.StopType.WORLD_MAP);
        player.lock(Short.MAX_VALUE);
        player.freeze(Integer.MAX_VALUE);
        player.getInterfaceHandler().sendInterface(154, 3, PaneType.GAME_SCREEN, false);
        player.getInterfaceHandler().getVisible().forcePut(player.getInterfaceHandler().getPane().getId() << 16 | InterfacePosition.CENTRAL.getComponent(player.getInterfaceHandler().getPane()), 154);
    }

    @Override
    protected void build() {

    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.TOURNAMENT_SPECTATING;
    }
}
