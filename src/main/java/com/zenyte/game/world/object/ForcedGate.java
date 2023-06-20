package com.zenyte.game.world.object;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.pathfinding.events.RouteEvent;
import com.zenyte.game.world.entity.pathfinding.events.npc.NPCTileEvent;
import com.zenyte.game.world.entity.pathfinding.events.player.TileEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.TileStrategy;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import kotlin.Pair;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Kris | 13/04/2019 13:11
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@SuppressWarnings("all")
public class ForcedGate<T extends Entity> {

    private static final Int2IntMap gateMap = new Int2IntOpenHashMap();

    static {
        gateMap.put(2039, 2040);
        gateMap.put(2041, 2042);
        gateMap.put(2102, 2103);
        gateMap.put(2104, 2105);
        gateMap.put(15604, 15606);
        gateMap.put(15605, 15607);
    }

    @NotNull
    private final WorldObject clickedObject;
    @NotNull private final Pair<WorldObject, WorldObject> gatePair;
    @NotNull private final Pair<WorldObject, WorldObject> graphicalPair;
    @NotNull private final T entity;
    @NotNull private final Location startPosition;

    public ForcedGate(@NotNull final T entity, @NotNull final WorldObject object) {
        this.entity = entity;
        this.clickedObject = object;
        val nearbyDoor = Objects.requireNonNull(entity instanceof Player ? TemporaryDoubleDoor.getNearbyDoor((Player) entity, object) : TemporaryDoubleDoor.getNearbyDoor(object));
        val doorA = new WorldObject(object.getX() == nearbyDoor.getX() ? (object.getY() < nearbyDoor.getY() ? object : nearbyDoor) : (object.getX() < nearbyDoor.getX() ? nearbyDoor : object));
        val doorB = new WorldObject(doorA.getPositionHash() == object.getPositionHash() ? nearbyDoor : object);
        val leftDoor = object.getRotation() == 0 || object.getRotation() == 3 ? doorB : doorA;
        val rightDoor = leftDoor == doorA ? doorB : doorA;
        leftDoor.setRotation((leftDoor.getRotation() + 1) & 0x3);
        rightDoor.setRotation((rightDoor.getRotation() - 1) & 0x3);
        leftDoor.setId(gateMap.getOrDefault(leftDoor.getId(), leftDoor.getId()));
        rightDoor.setId(gateMap.getOrDefault(rightDoor.getId(), rightDoor.getId()));
        gatePair = new Pair<>(object, nearbyDoor);
        graphicalPair = new Pair<>(leftDoor, rightDoor);
        switch(object.getRotation()) {
            case 0:
                startPosition = entity.getX() < object.getX() ? new Location(object.getX() - 1, object.getY(), object.getPlane()) : new Location(object);
                break;
            case 1:
                startPosition = entity.getY() > object.getY() ? new Location(object.getX(), object.getY() + 1, object.getPlane()) : new Location(object);
                break;
            case 2:
                startPosition = entity.getX() > object.getX() ? new Location(object.getX() + 1, object.getY(), object.getPlane()) : new Location(object);
                break;
            default:
                startPosition = entity.getY() < object.getY() ? new Location(object.getX(), object.getY() - 1, object.getPlane()) : new Location(object);
        }
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
        val strategy = new TileStrategy(startPosition);
        val runnable = (Runnable) () -> {
            if (predicate.isPresent()) {
                if (!predicate.get().test(entity)) {
                    return;
                }
            }
            val leftDoor = graphicalPair.getFirst();
            val rightDoor = graphicalPair.getSecond();
            val entityX = entity.getX();
            val entityY = entity.getY();
            val objX = clickedObject.getX();
            val objY = clickedObject.getY();
            val z = entity.getPlane();
            val rotation = clickedObject.getRotation();
            World.spawnGraphicalDoor(leftDoor);
            World.spawnGraphicalDoor(rightDoor);
            onStart();
            Location destination;
            switch(rotation) {
                case 0:
                case 2:
                    destination = new Location(entityX + (entityX > (objX + (rotation == 0 ? -1 : 0)) ? -1 : 1), entityY, z);
                    break;
                default:
                    destination = new Location(entityX, rotation == 3 ? (objY + (entityY < objY ? 0 : -1)) : (objY + (entityY > objY ? 0 : 1)), z);
                    break;
            }
            entity.lock(2);
            entity.addWalkSteps(destination.getX(), destination.getY(), -1, false);

            WorldTasksManager.schedule(() -> {
                World.spawnGraphicalDoor(gatePair.getFirst());
                World.spawnGraphicalDoor(gatePair.getSecond());
                onEnd();
            }, 1);
        };
        entity.setRouteEvent(entity instanceof Player ? new TileEvent((Player) entity, strategy, runnable) : new NPCTileEvent((NPC) entity, strategy, runnable));
    }

    protected RouteEvent<T, TileStrategy> getEvent() {
        return null;
    }

    protected void onStart() {

    }

    protected void onEnd() {

    }

}
