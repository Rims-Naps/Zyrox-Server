package com.zenyte.game.content.area.abandonedmine.npc;

import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.pathfinding.Flags;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.region.Chunk;
import lombok.val;

public class MovingMinecart extends NPC implements Spawnable {

    public MovingMinecart(int id, Location tile, Direction facing, int radius) {
        super(id, tile, facing, radius);
    }

    @Override
    public void processNPC() {
        Minecart cart = Minecart.forId(getId());
        if(getLocation().equals(cart.getSpawnLocation()) || getLocation().equals(cart.getDestination())) {
            if(cart.getXDir() != 0 || cart.getYDir() != 0) {
                cart.setStopTicks(1);
                cart.setYDir(0);
                cart.setXDir(0);
            } else {
                if(cart.getStopTicks() > 0) {
                    cart.setStopTicks(cart.getStopTicks() - 1);
                } else {
                    if(cart.getXDist() > 0) {
                        if(getLocation().equals(cart.getSpawnLocation())) {
                            cart.setXDir(1);
                        } else {
                            cart.setXDir(-1);
                        }
                    }
                    if(cart.getYDist() > 0) {
                        if(getLocation().equals(cart.getSpawnLocation())) {
                            cart.setYDir(1);
                        } else {
                            cart.setYDir(-1);
                        }
                    }
                }
            }
        }
        if(cart.getXDir() != 0 || cart.getYDir() != 0) {
            addWalkSteps(getX() + cart.getXDir(), getY() + cart.getYDir(), -1, false);
            for(Player p : World.getPlayers()) {
                if(p == null) {
                    return;
                }
                if(p.getHitpoints() == 0 || (p.getAnimation() != null && p.getAnimation().getId() == 836) || p.isLocked()) {
                    return;
                }
                if(p.getLocation().equals(new Location(getX() + cart.getXDir(), getY() + cart.getYDir(), 0))) {
                    Location nextLocation = new Location(p.getLocation().getX() + cart.getXDir(), p.getLocation().getY() + cart.getYDir(), 0);
                    int dir = ForceMovement.EAST;
                    if(getDirection() == Direction.SOUTH.getDirection()) {
                        dir = ForceMovement.NORTH;
                    } else if(getDirection() == Direction.NORTH.getDirection()) {
                        dir = ForceMovement.SOUTH;
                    } else if(getDirection() == Direction.EAST.getDirection()) {
                        dir = ForceMovement.WEST;
                    } else if(getDirection() == Direction.WEST.getDirection()){
                        dir = ForceMovement.EAST;
                    }
                    p.setForceMovement(new ForceMovement(p.getLocation(), 25, nextLocation, 45, dir));
                    p.setLocation(nextLocation);
                    p.setAnimation(new Animation(1441, 25));
                    p.sendSound(new SoundEffect(1603));
                    CombatUtilities.processHit(p, new Hit(this, Utils.random(0, 2), HitType.REGULAR));
                }
            }
        }
    }

    @Override
    public boolean validate(int id, String name) {
        return id >= NpcId.MINE_CART_3621 && id <= NpcId.MINE_CART_3624;
    }

    @Override
    public void unclip() {
        val size = getSize();
        val x = getX();
        val y = getY();
        val z = getPlane();
        int hash, lastHash = -1;
        Chunk chunk = null;
        for (int x1 = x; x1 < (x + size); x1++) {
            for (int y1 = y; y1 < (y + size); y1++) {
                if ((hash = Chunk.getChunkHash(x1 >> 3, y1 >> 3, z)) != lastHash) {
                    chunk = World.getChunk(lastHash = hash);
                }
                assert chunk != null;
                World.getRegion(Location.getRegionId(x1, y1), true).removeFlag(z, x1 & 0x3F, y1 & 0x3F,
                        Flags.OCCUPIED_BLOCK_NPC | Flags.OCCUPIED_BLOCK_PLAYER | Flags.OCCUPIED_PROJECTILE_BLOCK_NPC | Flags.OCCUPIED_PROJECTILE_BLOCK_PLAYER);
            }
        }
    }

    @Override
    public void clip() {
        if (isFinished()) {
            return;
        }
        val size = getSize();
        val x = getX();
        val y = getY();
        val z = getPlane();
        for (int x1 = x; x1 < (x + size); x1++) {
            for (int y1 = y; y1 < (y + size); y1++) {
                World.getRegion(Location.getRegionId(x1, y1), true).addFlag(z, x1 & 0x3F, y1 & 0x3F,
                        Flags.OCCUPIED_BLOCK_NPC | Flags.OCCUPIED_BLOCK_PLAYER | Flags.OCCUPIED_PROJECTILE_BLOCK_NPC | Flags.OCCUPIED_PROJECTILE_BLOCK_PLAYER);
            }
        }
    }
}