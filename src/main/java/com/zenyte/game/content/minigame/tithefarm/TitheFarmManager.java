package com.zenyte.game.content.minigame.tithefarm;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.plugins.events.ServerLaunchEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Optional;

/**
 * @author Kris | 21/05/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class TitheFarmManager {

    private static final int INSTANCES_COUNT = 1;
    @Getter private static final TitheFarmManager singleton = new TitheFarmManager();

    @Subscribe
    public static final void onServerLaunch(final ServerLaunchEvent event) {
        for (int i = 0; i < INSTANCES_COUNT; i++) {
            try {
                val allocatedArea = MapBuilder.findEmptyChunk(12, 13);
                val area = new TitheFarmArea(i, allocatedArea, 214, 443);
                singleton.titheFarmAreas.add(area);
                area.constructRegion();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
        }
    }

    private final List<TitheFarmArea> titheFarmAreas = new ObjectArrayList<>();

    public final TitheFarmArea selectRandomInstance() {
        return titheFarmAreas.get(Utils.random(titheFarmAreas.size() - 1));
    }

    public final Optional<TitheFarmArea> selectArea(final int index) {
        if (index >= titheFarmAreas.size()) {
            return Optional.empty();
        }
        return Optional.of(titheFarmAreas.get(index));
    }

}
