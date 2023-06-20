package com.zenyte.game.content.minigame.pestcontrol.area;

import com.zenyte.game.content.minigame.pestcontrol.PestControlGameType;
import com.zenyte.game.content.minigame.pestcontrol.PestControlInstance;
import com.zenyte.game.content.minigame.pestcontrol.PestControlStatistic;
import com.zenyte.game.content.minigame.pestcontrol.PestControlUtilities;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.teleportsystem.PortalTeleport;
import com.zenyte.game.world.region.area.VoidKnightsOutpost;
import com.zenyte.game.world.region.area.plugins.CycleProcessPlugin;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.game.world.region.dynamicregion.OutOfSpaceException;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.zenyte.game.content.minigame.pestcontrol.PestControlUtilities.TIME_UNTIL_GAME_START;

/**
 * @author Kris | 26. juuni 2018 : 20:25:24
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public abstract class AbstractLanderArea extends VoidKnightsOutpost implements CycleProcessPlugin {

	private final Object2IntOpenHashMap<Player> prioritizedPlayers = new Object2IntOpenHashMap<>();

	@Getter
	@Setter
	protected int ticks = TIME_UNTIL_GAME_START;

	@Override
	public void enter(final Player player) {
	    player.getTeleportManager().unlock(PortalTeleport.PEST_CONTROL);
		prioritizedPlayers.put(player, 1);
		PestControlUtilities.sendLanderInterface(player);
		for (val p : players) {
			PestControlUtilities.updateLanderInformation(p, this);
		}
	}

	@Override
	public void leave(final Player player, boolean logout) {
		prioritizedPlayers.removeInt(player);
		player.getInterfaceHandler().closeInterface(InterfacePosition.MINIGAME_OVERLAY);
		for (val p : players) {
			PestControlUtilities.updateLanderInformation(p, this);
		}
	}

	@Override
	public void process() {
		ticks--;
		if (ticks == 0) {
			ticks = TIME_UNTIL_GAME_START;
			if (players.isEmpty()) {
				return;
			}

			if (players.size() < PestControlUtilities.MINIMUM_PLAYERS_LIMIT) {
				return;
			}

			val list = new ArrayList<it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Player>>(prioritizedPlayers.object2IntEntrySet());
			list.sort(Entry.comparingByValue());
			val result = new HashMap<Player, PestControlStatistic>(Math.min(list.size(), PestControlUtilities.MAXIMUM_PLAYERS_LIMIT));

			val removed = new ArrayList<it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Player>>();
			for (val entry : list) {
				val player = entry.getKey();
				removed.add(entry);
				result.put(player, new PestControlStatistic(PestControlUtilities.HALF_FULL_ACTIVITY_PERCENTAGE_VALUE));
				if (result.size() >= PestControlUtilities.MAXIMUM_PLAYERS_LIMIT) {
					break;
				}
			}

			for (val p : removed) {
			    prioritizedPlayers.removeInt(p);
            }

			dispatch(result);

			for (val entry : prioritizedPlayers.object2IntEntrySet()) {
				val value = entry.getIntValue();
				entry.getKey().sendMessage(
						String.format("You have been given priority level %d over other players in joining the next game.", value));
				entry.setValue(value + 1);
			}

		}
		if (players.isEmpty()) {
			return;
		}
		if (ticks % 50 == 0) {
			for (val player : players) {
				PestControlUtilities.updateLanderInformation(player, this);
			}
		}
	}

	private void dispatch(final Map<Player, PestControlStatistic> players) {
	    try {
            val area = MapBuilder.findEmptyChunk(PestControlUtilities.INSTANCE_WIDTH, PestControlUtilities.INSTANCE_HEIGHT);
            for (val entry : players.entrySet()) {
                val player = entry.getKey();
                if (player.isNulled()) {
                    continue;
                }
                player.lock();
            }
            new PestControlInstance(getType(), players, area).constructRegion();
        } catch (OutOfSpaceException e) {
            log.error(Strings.EMPTY, e);
        }
	}

	public abstract PestControlGameType getType();
	
}
