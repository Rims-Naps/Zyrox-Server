package com.zenyte.game.content.skills.agility.brimhavenminigame;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.object.WorldObject;
import lombok.Getter;

/**
 * @author Cresinkel
 */
public enum Obstacles {

    PRESSURE_PAD_1(new WorldObject(3585, 10, 0, 2799, 9579, 3), new Location(2801, 9579, 3), new Location(2799, 9579, 3), new Location(2798, 9579, 3), ForceMovement.EAST,1),
    PRESSURE_PAD_2(new WorldObject(3585, 10, 0, 2800, 9579, 3), new Location(2798, 9579, 3), new Location(2800, 9579, 3), new Location(2801, 9579, 3), ForceMovement.WEST,1),
    PRESSURE_PAD_3(new WorldObject(3585, 10, 0, 2799, 9557, 3), new Location(2801, 9557, 3), new Location(2799, 9557, 3), new Location(2798, 9557, 3), ForceMovement.EAST,1),
    PRESSURE_PAD_4(new WorldObject(3585, 10, 0, 2800, 9557, 3), new Location(2798, 9557, 3), new Location(2800, 9557, 3), new Location(2801, 9557, 3), ForceMovement.WEST,1),
    PRESSURE_PAD_5(new WorldObject(3585, 10, 0, 2772, 9584, 3), new Location(2772, 9586, 3), new Location(2772, 9584, 3), new Location(2772, 9583, 3), ForceMovement.NORTH,1),
    PRESSURE_PAD_6(new WorldObject(3585, 10, 0, 2772, 9585, 3), new Location(2772, 9583, 3), new Location(2772, 9585, 3), new Location(2772, 9586, 3), ForceMovement.SOUTH,1),

    SPINNING_BLADE_1(new WorldObject(3580, 10, 3, 2777, 9580, 3), new Location(2776, 9579, 3), new Location(2778, 9579, 3), new Location(2780, 9579, 3), ForceMovement.WEST,2),
    SPINNING_BLADE_2(new WorldObject(3580, 10, 3, 2777, 9580, 3), new Location(2779, 9579, 3), new Location(2777, 9579, 3), new Location(2776, 9579, 3), ForceMovement.EAST,2),
    SPINNING_BLADE_3(new WorldObject(3580, 10, 2, 2782, 9573, 3), new Location(2783, 9572, 3), new Location(2783, 9574, 3), new Location(2783, 9575, 3), ForceMovement.SOUTH,2),
    SPINNING_BLADE_4(new WorldObject(3580, 10, 2, 2782, 9573, 3), new Location(2783, 9575, 3), new Location(2783, 9573, 3), new Location(2783, 9572, 3), ForceMovement.NORTH,2),
    SPINNING_BLADE_5(new WorldObject(3580, 10, 1, 2777, 9556, 3), new Location(2779, 9557, 3), new Location(2777, 9557, 3), new Location(2776, 9557, 3), ForceMovement.EAST,2),
    SPINNING_BLADE_6(new WorldObject(3580, 10, 1, 2777, 9556, 3), new Location(2776, 9557, 3), new Location(2778, 9557, 3), new Location(2780, 9557, 3), ForceMovement.WEST,2),

    DARTS_1(new WorldObject(3581, 10, 0, 2772, 9568, 3), new Location(2776, 9568, 3), new Location(2778, 9568, 3), new Location(2779, 9568, 3), ForceMovement.WEST,3),
    DARTS_2(new WorldObject(3581, 10, 0, 2783, 9568, 3), new Location(2779, 9568, 3), new Location(2777, 9568, 3), new Location(2776, 9568, 3), ForceMovement.EAST,3),
    DARTS_3(new WorldObject(3581, 10, 0, 2783, 9557, 3), new Location(2787, 9557, 3), new Location(2789, 9557, 3), new Location(2790, 9557, 3), ForceMovement.WEST,3),
    DARTS_4(new WorldObject(3581, 10, 0, 2794, 9557, 3), new Location(2790, 9557, 3), new Location(2788, 9557, 3), new Location(2787, 9557, 3), ForceMovement.EAST,3),
    DARTS_5(new WorldObject(3581, 10, 0, 2794, 9579, 3), new Location(2794, 9575, 3), new Location(2794, 9573, 3), new Location(2794, 9572, 3), ForceMovement.NORTH,3),
    DARTS_6(new WorldObject(3581, 10, 0, 2794, 9568, 3), new Location(2794, 9572, 3), new Location(2794, 9574, 3), new Location(2794, 9575, 3), ForceMovement.SOUTH,3),

    FLOOR_SPIKES_1(new WorldObject(3582, 10, 0, 2761, 9574, 3), new Location(2761, 9572, 3), new Location(2761, 9574, 3), new Location(2761, 9575, 3), ForceMovement.SOUTH,4),
    FLOOR_SPIKES_2(new WorldObject(3582, 10, 0, 2761, 9573, 3), new Location(2761, 9575, 3), new Location(2761, 9573, 3), new Location(2761, 9572, 3), ForceMovement.NORTH,4),
    FLOOR_SPIKES_3(new WorldObject(3582, 10, 0, 2772, 9552, 3), new Location(2772, 9550, 3), new Location(2772, 9552, 3), new Location(2772, 9553, 3), ForceMovement.SOUTH,4),
    FLOOR_SPIKES_4(new WorldObject(3582, 10, 0, 2772, 9551, 3), new Location(2772, 9553, 3), new Location(2772, 9551, 3), new Location(2772, 9550, 3), ForceMovement.NORTH,4),
    FLOOR_SPIKES_5(new WorldObject(3582, 10, 0, 2799, 9568, 3), new Location(2801, 9568, 3), new Location(2799, 9568, 3), new Location(2798, 9568, 3), ForceMovement.EAST,4),
    FLOOR_SPIKES_6(new WorldObject(3582, 10, 0, 2800, 9568, 3), new Location(2798, 9568, 3), new Location(2800, 9568, 3), new Location(2801, 9568, 3), ForceMovement.WEST,4),

    BLADE_1(new WorldObject(3568, 22, 3, 2788, 9579, 3), new Location(2798, 9568, 3), new Location(2788, 9579, 3), new Location(2787, 9579, 3), ForceMovement.WEST,5),
    BLADE_2(new WorldObject(3569, 22, 3, 2789, 9579, 3), new Location(2798, 9568, 3), new Location(2789, 9579, 3), new Location(2790, 9579, 3), ForceMovement.EAST,5),
    BLADE_3(new WorldObject(3568, 22, 2, 2761, 9584, 3), new Location(2798, 9568, 3), new Location(2761, 9584, 3), new Location(2761, 9583, 3), ForceMovement.SOUTH,5),
    BLADE_4(new WorldObject(3569, 22, 2, 2761, 9585, 3), new Location(2798, 9568, 3), new Location(2761, 9585, 3), new Location(2761, 9586, 3), ForceMovement.NORTH,5),
    BLADE_5(new WorldObject(3568, 22, 2, 2783, 9551, 3), new Location(2798, 9568, 3), new Location(2783, 9551, 3), new Location(2783, 9550, 3), ForceMovement.SOUTH,5),
    BLADE_6(new WorldObject(3569, 22, 2, 2783, 9552, 3), new Location(2798, 9568, 3), new Location(2783, 9552, 3), new Location(2783, 9553, 3), ForceMovement.NORTH,5),

    ;

    @Getter WorldObject trapObject;
    @Getter Location location;
    @Getter Location forceTo;
    @Getter Location pushTo;
    @Getter int direction;
    @Getter int type;
    Obstacles(WorldObject trapObject, Location forceTo, Location location , Location pushTo, int direction, int type) {
        this.trapObject = trapObject;
        this.location = location;
        this.forceTo = forceTo;
        this.pushTo = pushTo;
        this.direction = direction;
        this.type = type;
    }

    public static Obstacles getFromCoords(int x, int y) {
        for(Obstacles trap : Obstacles.values()) {
            if(trap.getTrapObject().getX() == x && trap.getTrapObject().getY() == y) {
                return trap;
            }
        }
        return null;
    }
}
