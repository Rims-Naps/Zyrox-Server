package com.zenyte.game.content.skills.agility.pyramid;

import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

public class LowWall implements Obstacle {
    private static final Animation climbAnim = new Animation(840, 15);

    @Override
    public String getFilterableStartMessage(boolean success) {
        return "You climb the low wall...";
    }

    @Override
    public void startSuccess(Player player, WorldObject object) {
        player.sendSound(new SoundEffect(2453, 1, 30));
        player.faceObject(object);
        player.setAnimation(climbAnim);

        val forward = Math.abs(object.getFaceDirection().getDirection() -
                Direction.getNPCDirection(player.getRoundedDirection()).getDirection()) <= 257;
        val direction = forward ? object.getFaceDirection() : object.getFaceDirection().getCounterClockwiseDirection(4);
        val destination = player.getLocation().transform(direction, 2);
        val currentTile = new Location(player.getLocation());
        player.setLocation(destination);
        player.setForceMovement(new ForceMovement(currentTile, 1, destination, 80,
                direction.getDirection()));
    }

    @Override
    public String getFilterableEndMessage(boolean success) {
        return "... and make it over.";
    }

    @Override
    public int getLevel(WorldObject object) {
        return 30;
    }

    @Override
    public int getDuration(boolean success, WorldObject object) {
        return 2;
    }

    @Override
    public double getSuccessXp(WorldObject object) {
        return 8;
    }

    @Override
    public int[] getObjectIds() {
        return new int[]{10865};
    }

}
