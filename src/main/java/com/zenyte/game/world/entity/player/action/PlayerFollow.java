package com.zenyte.game.world.entity.player.action;

import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import lombok.val;

/**
 * @author Kris | 07/10/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PlayerFollow extends Action {

    private final Player target;

    @Override
    public boolean interruptedByCombat() {
        return false;
    }

    public PlayerFollow(final Player target) {
        this.target = target;
    }

    private int targetPos;

    @Override
    public boolean start() {
        player.setFaceEntity(target);
        if (checkAll(player)) {
            return true;
        }
        player.setFaceEntity(null);
        return false;
    }

    private boolean checkAll(Player player) {
        if (player.isDead() || player.isFinished() || target.isDead() || target.isFinished()) return false;
        if (player.getPlane() != target.getPlane()) return false;
        if (player.isFrozen() || player.isStunned()) return true;
        int distanceX = player.getX() - target.getX();
        int distanceY = player.getY() - target.getY();
        int size = player.getSize();
        int maxDistance = 16;
        if (player.getPlane() != target.getPlane() || distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance || distanceY < -1 - maxDistance)
            return false;
        if (targetPos == 0) {
            val tile = target.getLastLocation();
            player.calcFollow(tile, -1, true, true, false);
            targetPos = target.getLocation().getPositionHash();
            return true;
        }
        int lastFaceEntity = target.getFaceEntity();
        if (targetPos != target.getLocation().getPositionHash()) {
            targetPos = target.getLocation().getPositionHash();
            player.resetWalkSteps();
        }
        if (lastFaceEntity == player.getClientIndex() && (target.getDelayedActionManager().getAction() instanceof PlayerFollow || target.getActionManager().getAction() instanceof PlayerCombat) && player.getLocation().getDistance(target.getLocation()) < 3) {
            val tile = target.getLocation().transform(target.getLastWalkX(), target.getLastWalkY(), 0);
            if (!tile.matches(player)) {
                player.resetWalkSteps();
                player.addWalkSteps(tile.getX(), tile.getY(), 2, true);
            }
            return true;
        }
        val tile = getFaceLocation(target, target.getLocation());
        player.calcFollow(tile, -1, true, false, false);
        return true;
    }

    private Location getFaceLocation(final Player player, final Location tile) {
        val direction = player.getRoundedDirection(1024);
        val dir = Direction.npcMap.get(direction);
        return tile.transform(dir, 1);
    }

    @Override
    public boolean process() {
        return checkAll(player);
    }

    @Override
    public int processWithDelay() {
        return 0;
    }

    @Override
    public void stop() {
        player.setFaceEntity(null);
    }
}