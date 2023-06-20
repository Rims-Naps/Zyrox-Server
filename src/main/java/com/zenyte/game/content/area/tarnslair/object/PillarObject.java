package com.zenyte.game.content.area.tarnslair.object;

import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public class PillarObject implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if(withinReach(player, object)) {
            player.lock(6);
            player.setForceMovement(new ForceMovement(player.getLocation(), 60, object.getPosition(), 85, getForceMoveDirection(player, object)));
            player.setLocation(object.getPosition());
            player.setAnimation(new Animation(4721, 30));
            player.sendSound(new SoundEffect(2461, 10, 64));
        } else {
            player.sendMessage("You can't jump from where you are; you need to get closer to jump here.");
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 20540, 20541, 20542, 20543, 20544, 20545, 20546, 20547, 20548, 20549, 20550, 20551, 20552, 20553, 20554, 20555, 20556,
                              20558, 20559, 20560, 20561, 20562, 20563, 20564, 20565, 20566, 20567 };
    }

    private boolean withinReach(Player player, WorldObject object) {
        if(object.getY() == player.getY()) {
            int diff = object.getX() - player.getX();
            if(Math.abs(diff) == 2) {
                return true;
            }
        } else if(object.getX() == player.getX()) {
            int diff = object.getY() - player.getY();
            if(Math.abs(diff) == 2) {
                return true;
            }
        }
        return false;
    }

    private int getForceMoveDirection(Player player, WorldObject object) {
        if(object.getY() == player.getY()) {
            int diff = object.getX() - player.getX();
            if(diff == 2) {
                return ForceMovement.EAST;
            }
            if(diff == -2) {
                return ForceMovement.WEST;
            }
        } else if(object.getX() == player.getX()) {
            int diff = object.getY() - player.getY();
            if(diff == 2) {
                return ForceMovement.NORTH;
            }
            if(diff == -2) {
                return ForceMovement.SOUTH;
            }
        }
        return ForceMovement.EAST;
    }

    @Override
    public void handle(final Player player, final WorldObject object, final String name, final int optionId,
                       final String option) {
        if(player.getLocation().equals(object.getPosition())) {
            return;
        }
        if(!withinReach(player, object)) {
            player.sendMessage("You can't jump from where you are; you need to get closer to jump here.");
            return;
        }

        player.setRouteEvent(new ObjectEvent(player, new ObjectStrategy(object,
                1), getRunnable(player, object, name, optionId,
                option), getDelay()));
    }
}
