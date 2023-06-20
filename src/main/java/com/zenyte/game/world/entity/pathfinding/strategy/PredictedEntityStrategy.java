package com.zenyte.game.world.entity.pathfinding.strategy;

import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.pathfinding.RouteStrategy;
import com.zenyte.game.world.entity.pathfinding.events.RouteEvent;
import lombok.val;

/**
 * @author Kris | 05/01/2019 23:36
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PredictedEntityStrategy extends EntityStrategy {

    public PredictedEntityStrategy(final Entity entity) {
        super(entity, 0, entity.getSize(), 0);
    }

    @Override
    public boolean canExit(final int currentX, final int currentY, final int sizeXY, final int[][] clip, final int clipBaseX,
                           final int clipBaseY) {
        val x = getApproxDestinationX();
        val y = getApproxDestinationY();
        if (accessBlockFlag != 0) {
            val xDistance = accessBlockFlag == RouteEvent.SOUTH_EXIT || accessBlockFlag == RouteEvent.NORTH_EXIT ? 0 : distance;
            val yDistance = accessBlockFlag == RouteEvent.EAST_EXIT || accessBlockFlag == RouteEvent.WEST_EXIT ? 0 : distance;
            return RouteStrategy.checkFilledRectangularInteract(clip, currentX - clipBaseX, currentY - clipBaseY, sizeXY + xDistance,
                    sizeXY + yDistance, x - clipBaseX, y - clipBaseY, getApproxDestinationSizeX() + xDistance,
                    getApproxDestinationSizeY() + yDistance, accessBlockFlag);
        }
        return RouteStrategy.checkFilledRectangularInteract(clip, currentX - clipBaseX, currentY - clipBaseY, sizeXY + distance,
                sizeXY + distance, x - clipBaseX, y - clipBaseY, getApproxDestinationSizeX() + distance,
                getApproxDestinationSizeY() + distance, accessBlockFlag);
    }

    @Override
    public int getApproxDestinationX() {
        return entity/*.getNextPosition(entity.isRun() ? 2 : 1)*/.getX();
    }

    @Override
    public int getApproxDestinationY() {
        return entity/*.getNextPosition(entity.isRun() ? 2 : 1)*/.getY();
    }

}
