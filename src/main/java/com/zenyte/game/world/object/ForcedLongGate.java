package com.zenyte.game.world.object;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.pathfinding.events.RouteEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.TileStrategy;
import kotlin.Pair;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Kris | 27/03/2019 19:43
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@SuppressWarnings("all")
public class ForcedLongGate<T extends Entity> {

    @NotNull private final WorldObject clickedObject;
    @NotNull private final Pair<WorldObject, WorldObject> gatePair;
    @Nullable private Pair<WorldObject, WorldObject> graphicalPair;
    private final boolean vertical;
    @NotNull private final T entity;

    public ForcedLongGate(@NotNull final T entity, @NotNull final WorldObject object) {
        this.entity = entity;
        this.clickedObject = object;
        val other = Objects.requireNonNull(Gate.getOtherGate(object));
        gatePair = new Pair<>(object, other);
        val rotation = object.getRotation();
        this.vertical = (rotation & 0x1) == 0;
    }

    public void handle(final Optional<Predicate<T>> predicate) {
        val event = getEvent();
        if (event != null) {
            event.setEvent(() -> handle(predicate));
            entity.setRouteEvent(event);
            return;
        }
        handleSub(predicate);
    }

    private void handleSub(final Optional<Predicate<T>> predicate) {
        if (predicate.isPresent()) {
            if (!predicate.get().test(entity)) {
                return;
            }
        }
        val first = gatePair.getFirst();
        val second = gatePair.getSecond();
        val innerMapGate = Gate.getInnerMapGate(first, second);
        val outerMapGate = first == innerMapGate ? second : first;
        val tiles = Gate.getOpenLocations(innerMapGate);
        graphicalPair = new Pair<>(new WorldObject(Gate.getRespectiveId(innerMapGate.getId()), innerMapGate.getType(), (innerMapGate.getRotation() - 1) & 0x3, tiles[0]),
                new WorldObject(Gate.getRespectiveId(outerMapGate.getId()), outerMapGate.getType(), (outerMapGate.getRotation() - 1) & 0x3, tiles[1]));
        World.removeGraphicalDoor(first);
        World.removeGraphicalDoor(second);
        World.spawnGraphicalDoor(graphicalPair.getFirst());
        World.spawnGraphicalDoor(graphicalPair.getSecond());
        onStart();
        val entityX = entity.getX();
        val entityY = entity.getY();
        val objX = clickedObject.getX();
        val objY = clickedObject.getY();
        val z = entity.getPlane();
        Location destination;
        val rotation = clickedObject.getRotation();
        switch(rotation) {
            case 0:
            case 2:
                destination = new Location(entityX + (entityX > (objX + (rotation == 0 ? -1 : 0)) ? -1 : 1), entityY, z);
                break;
            default:
                destination = new Location(entityX, entityY + (entityY > (objY + (rotation == 1 ? -1 : 0)) ? -1 : 1), z);
                break;
        }
        entity.lock(2);
        entity.addWalkSteps(destination.getX(), destination.getY(), 1, false);
        WorldTasksManager.schedule(() -> {
            World.removeGraphicalDoor(graphicalPair.getFirst());
            World.removeGraphicalDoor(graphicalPair.getSecond());
            World.spawnGraphicalDoor(first);
            World.spawnGraphicalDoor(second);
            onEnd();
        }, 1);
    }

    protected RouteEvent<T, TileStrategy> getEvent() {
        return null;
    }

    protected void onStart() {

    }

    protected void onEnd() {

    }

}
