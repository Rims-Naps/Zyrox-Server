package com.zenyte.game.content.skills.agility.shortcut;

import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.AttachedObject;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

public class AbandonedMineMineCart implements Shortcut {

    //840 anim
    private final Animation HOP_OVER = new Animation(840, 30);
    private static final Location WEST = new Location(3444, 3236, 0);
    private static final Location EAST = new Location(3446, 3236, 0);
    private static final AttachedObject MINE_CART = new AttachedObject(new WorldObject(4918, 10, 2, 3445, 3236, 0), 30, 100, -1, 1, 0, 0);
    @Override
    public int getLevel(WorldObject object) {
        return 0;
    }

    @Override
    public int[] getObjectIds() {
        return new int[] { 4918 }; //4918
    }

    @Override
    public int getDuration(boolean success, WorldObject object) {
        return 0;
    }

    @Override
    public void startSuccess(Player player, WorldObject object) {
        player.lock(2);
        val west = player.getPosition().getPositionHash() == WEST.getPositionHash();
        player.blockIncomingHits();
        player.setForceMovement(new ForceMovement(player.getLocation(), 30, west ? EAST : WEST, 100, west ? ForceMovement.EAST : ForceMovement.WEST));
        player.setAnimation(HOP_OVER);
        World.sendAttachedObject(player, MINE_CART);
        player.sendSound(new SoundEffect(2453, 10, 40));
        player.setLocation(west ? EAST : WEST);
        player.blockIncomingHits();
    }

    @Override
    public double getSuccessXp(WorldObject object) {
        return 0.1D;
    }
}
