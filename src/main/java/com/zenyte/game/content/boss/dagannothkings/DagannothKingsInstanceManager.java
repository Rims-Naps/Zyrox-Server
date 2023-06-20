package com.zenyte.game.content.boss.dagannothkings;

import com.google.common.base.Preconditions;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * @author Kris | 18/06/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class DagannothKingsInstanceManager {

    @Getter
    private static final DagannothKingsInstanceManager manager = new DagannothKingsInstanceManager();

    private final Map<String, DagannothKingInstance> clanInstances = new Object2ObjectOpenHashMap<>();

    public void addInstance(@NotNull final String clanOwner, @NotNull final DagannothKingInstance area) {
        val existingInstance = clanInstances.get(clanOwner);
        Preconditions.checkArgument(existingInstance == null || existingInstance.getPlayers().isEmpty());
        clanInstances.put(clanOwner, area);
    }

    public void removeInstance(@NotNull final String clanOwner, @NotNull final DagannothKingInstance area) {
        val existingInstance = clanInstances.get(clanOwner);
        Preconditions.checkArgument(existingInstance != null && existingInstance.getPlayers().isEmpty() && existingInstance.equals(area));
        clanInstances.remove(clanOwner, area);
    }

    public Optional<DagannothKingInstance> findInstance(@NotNull final Player player) {
        val channel = player.getSettings().getChannel();
        Preconditions.checkArgument(channel != null);
        val owner = channel.getOwner();
        Preconditions.checkArgument(owner != null);
        return Optional.ofNullable(clanInstances.get(owner));
    }

}
