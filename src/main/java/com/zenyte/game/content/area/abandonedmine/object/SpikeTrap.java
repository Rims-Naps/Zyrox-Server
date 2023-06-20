package com.zenyte.game.content.area.abandonedmine.object;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.object.WorldObject;
import lombok.Getter;

public enum SpikeTrap {

    FLOOR1_TRAP_1(new WorldObject(20558, 10, 2, 3195, 4557, 0), new Location(3196, 4559), new Location(3196, 4557), new Location(3196, 4556), ForceMovement.NORTH),
    FLOOR1_TRAP_2(new WorldObject(20558, 10, 2, 3195, 4558, 0), new Location(3196, 4556), new Location(3196, 4558), new Location(3196, 4559), ForceMovement.SOUTH),
    FLOOR1_TRAP_3(new WorldObject(20558, 10, 0, 3196, 4562, 0), new Location(3196, 4564), new Location(3196, 4562), new Location(3196, 4561), ForceMovement.NORTH),
    FLOOR1_TRAP_4(new WorldObject(20558, 10, 0, 3196, 4563, 0), new Location(3196, 4561), new Location(3196, 4563), new Location(3196, 4564), ForceMovement.SOUTH),

    FLOOR2_TRAP_1(new WorldObject(20596, 10, 1, 3146, 4588, 1), new Location(3148, 4589, 1), new Location(3146, 4589, 1), new Location(3145, 4589,1), ForceMovement.EAST),
    FLOOR2_TRAP_2(new WorldObject(20596, 10, 1, 3147, 4588, 1), new Location(3145, 4589, 1), new Location(3147, 4589, 1), new Location(3148, 4589,1), ForceMovement.WEST),
    FLOOR2_TRAP_3(new WorldObject(20596, 10, 3, 3151, 4589, 1), new Location(3153, 4589, 1), new Location(3151, 4589, 1), new Location(3150, 4589,1), ForceMovement.EAST),
    FLOOR2_TRAP_4(new WorldObject(20596, 10, 3, 3152, 4589, 1), new Location(3150, 4589, 1), new Location(3152, 4589, 1), new Location(3153, 4589,1), ForceMovement.WEST),
    ;
    @Getter WorldObject trapObject;
    @Getter Location location;
    @Getter Location forceTo;
    @Getter Location pushTo;
    @Getter int direction;
    SpikeTrap(WorldObject trapObject, Location forceTo, Location location , Location pushTo, int direction) {
        this.trapObject = trapObject;
        this.location = location;
        this.forceTo = forceTo;
        this.pushTo = pushTo;
        this.direction = direction;
    }

    public static SpikeTrap getFromCoords(int x, int y) {
        for(SpikeTrap trap : SpikeTrap.values()) {
            if(trap.getTrapObject().getX() == x && trap.getTrapObject().getY() == y) {
                return trap;
            }
        }
        return null;
    }

}
