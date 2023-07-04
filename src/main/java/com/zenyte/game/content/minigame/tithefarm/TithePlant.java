package com.zenyte.game.content.minigame.tithefarm;

import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class TithePlant {

    @Getter private transient final Player owner;
    @Getter @Setter private WorldObject plant;
    @Getter private final TithePlantType type;
    @Getter @Setter private TitheStatus status = TitheStatus.SEEDLING_UNWATERED;
    @Getter @Setter private boolean fertilised;

    private int ticks;

    public static final Map<Integer, TithePlant> GLOBAL_PLANTS = new HashMap<>();

    public TithePlant(final Player owner, final WorldObject plant, final TithePlantType type) {
        this.owner = owner;
        this.plant = plant;
        this.type = type;
    }

    public boolean isUnwatered() {
        return status.toString().toLowerCase().contains("unwatered");
    }

    public boolean isWatered() {
        return status.toString().toLowerCase().contains("watered") && !isUnwatered();
    }

    public boolean isBlighted() {
        return status.toString().toLowerCase().contains("blighted");
    }

    public boolean isSeedling() {
        return status.toString().toLowerCase().contains("seedling");
    }

    public boolean isGrowing() {
        return status.toString().toLowerCase().contains("growing");
    }

    public boolean isMature() {
        return status.toString().toLowerCase().contains("mature");
    }

    public boolean isReady() {
        return status.toString().toLowerCase().contains("ready");
    }

    public void grow() {
        if(isMature()) {
            status = TitheStatus.READY;
        }

        if(isGrowing()) {
            status = TitheStatus.MATURE_UNWATERED;
        }

        if(isSeedling()) {
            status = TitheStatus.GROWING_UNWATERED;
        }

        final WorldObject newPlant = new WorldObject(status.getObjectId(type), plant.getType(), plant.getRotation(), plant);
        World.spawnObject(newPlant);
        plant = newPlant;
    }

    public void water() {
        if(isSeedling()) {
            status = TitheStatus.SEEDLING_WATERED;
        }

        if(isGrowing()) {
            status = TitheStatus.GROWING_WATERED;
        }

        if(isMature()) {
            status = TitheStatus.MATURE_WATERED;
        }

        final WorldObject newPlant = new WorldObject(status.getObjectId(type), plant.getType(), plant.getRotation(), plant);
        World.spawnObject(newPlant);
        plant = newPlant;
    }

    public void blight() {
        if(isSeedling()) {
            status = TitheStatus.SEEDLING_BLIGHTED;
        }

        if(isGrowing()) {
            status = TitheStatus.GROWING_BLIGHTED;
        }

        if(isMature()) {
            status = TitheStatus.MATURE_BLIGHTED;
        }

        if(isReady()) {
            status = TitheStatus.READY_BLIGHTED;
        }

        // spawn the new object, assign it to a new variable so we can reassign our local variable after spawning
        final WorldObject newPlant = new WorldObject(status.getObjectId(type), plant.getType(), plant.getRotation(), plant);
        World.spawnObject(newPlant);
        plant = newPlant;
    }

    public void die() {
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, plant.getType(), plant.getRotation(), plant));
        GLOBAL_PLANTS.remove(plant.getPositionHash());
    }

    public void process() {
        // skip the first tick.
        if(ticks == 0) {
            ticks++;
            return;
        }

        if(ticks % (fertilised ? 50 : 100) == 0) {
            if(isBlighted()) {
                this.die();
            }

            if(isUnwatered() || isReady() && !isBlighted()) {
                this.blight();
            }

            if(isWatered()) {
                this.grow();
            }
        }

        ticks++;
    }



}
