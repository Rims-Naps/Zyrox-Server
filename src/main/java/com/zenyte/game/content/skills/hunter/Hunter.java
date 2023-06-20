package com.zenyte.game.content.skills.hunter;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.content.skills.hunter.node.TrapType;
import com.zenyte.game.content.skills.hunter.object.Birdhouse;
import com.zenyte.game.content.skills.hunter.object.HunterTrap;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.events.InitializationEvent;
import com.zenyte.plugins.events.LoginEvent;
import com.zenyte.plugins.events.LogoutEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.val;
import mgi.types.config.ObjectDefinitions;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author Kris | 25/03/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Hunter {

    private transient WeakReference<Player> player;
    @Getter
    private transient List<HunterTrap> traps;
    private transient List<HunterTrap> removedTraps;
    private List<Birdhouse> builtBirdhouses;

    public Hunter(@NotNull final Player player) {
        this.player = new WeakReference<>(player);
        this.traps = new LinkedList<>();
        this.removedTraps = new LinkedList<>();
        this.builtBirdhouses = new ObjectArrayList<>(BirdHousePosition.getValues().size());
    }

    @Subscribe
    public static final void onInitialization(@NotNull final InitializationEvent event) {
        val player = event.getPlayer();
        val savedPlayer = event.getSavedPlayer();
        val otherHunter = savedPlayer.getHunter();
        if (otherHunter == null) {
            return;
        }
        val birdHouses = otherHunter.builtBirdhouses;
        if (birdHouses == null) {
            return;
        }
        otherHunter.builtBirdhouses.forEach(birdhouse -> birdhouse.setPlayerReference(player));
        player.getHunter().builtBirdhouses = otherHunter.builtBirdhouses;
    }

    @Subscribe
    public static final void onLogout(@NotNull final LogoutEvent event) {
        val player = event.getPlayer();
        val hunter = player.getHunter();
        val traps = hunter.traps;
        for (HunterTrap trap : traps) {
            val removalRunnable = trap.getRemovalRunnable();
            if (removalRunnable != null) {
                removalRunnable.run();
            } else {
                trap.remove();
            }
        }
        traps.clear();
    }

    @Subscribe
    public static final void onLogin(@NotNull final LoginEvent event) {
        event.getPlayer().getHunter().builtBirdhouses.forEach(Birdhouse::refreshVarbits);
    }

    public Optional<Birdhouse> findBirdhouse(final int objectId) {
        if (builtBirdhouses.isEmpty()) {
            return Optional.empty();
        }
        for (val birdhouse : builtBirdhouses) {
            if (birdhouse.getPosition().getObjectId() == objectId) {
                return Optional.of(birdhouse);
            }
        }
        return Optional.empty();
    }

    public void addBirdhouse(@NotNull final Birdhouse birdhouse) {
        if (findBirdhouse(birdhouse.getPosition().getObjectId()).isPresent()) {
            throw new IllegalStateException();
        }
        builtBirdhouses.add(birdhouse);
        birdhouse.refreshVarbits();
    }

    public void removeBirdhouse(@NotNull final Birdhouse birdhouse) {
        val position = birdhouse.getPosition();
        builtBirdhouses.removeIf(bh -> bh.getPosition() == position);
        val p = player.get();
        if (p != null && !p.isNulled()) {
            p.getVarManager().sendVar(ObjectDefinitions.getOrThrow(position.getObjectId()).getVarp(), 0);
        }
    }

    public void process() {
        for (val trap : traps) {
            //If another piece of code removed this trap, let's skip it. It will be removed from memory below.
            if (trap.getState() == TrapState.REMOVED) {
                continue;
            }
            trap.process();
        }
        if (!removedTraps.isEmpty()) {
            traps.removeAll(removedTraps);
            removedTraps.clear();
        }
        for (val birdhouse : builtBirdhouses) {
            birdhouse.checkCompletion();
        }
    }

    public boolean hasDeadfallLaid() {
        for (val trap : traps) {
            val state = trap.getState();
            if (state == TrapState.COLLAPSED || state == TrapState.DISMANTLING || state == TrapState.REMOVED || trap.getType() != TrapType.DEADFALL) {
                continue;
            }
            return true;
        }
        return false;
    }

    public int getLaidTrapsSize() {
        int count = 0;
        for (val trap : traps) {
            val state = trap.getState();
            if (state == TrapState.DISMANTLING || state == TrapState.REMOVED) {
                continue;
            }
            count++;
        }
        return count;
    }

    public void appendTrap(@NotNull final HunterTrap trap) {
        traps.add(trap);
    }

    public void removeTrap(@NotNull final HunterTrap trap) {
        removedTraps.add(trap);
    }

}
