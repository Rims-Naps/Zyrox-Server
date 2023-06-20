package com.zenyte.game.content.follower;

import com.zenyte.game.content.follower.impl.MiscPet;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Position;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.pathfinding.events.npc.NPCEntityEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.Player;
import lombok.Getter;
import lombok.val;

/**
 * @author Kris | 2. nov 2017 : 21:19.28
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public class Follower extends NPC {

	public Follower(final int petId, final Player owner) {
		super(petId, new Location(owner.getLocation()), false);
		this.owner = owner;
		checkNearDirs = Utils.getCoordOffsetsNear(getSize());
		if (petId == MiscPet.AREA_LOCKED_SNOW_IMP.petId()) {
			setRun(true);
		}
	}
	
	public Follower(final int petId, final Player owner, final Location tile) {
		super(petId,  tile, false);
		this.owner = owner;
		checkNearDirs = Utils.getCoordOffsetsNear(getSize());
		if (petId == MiscPet.AREA_LOCKED_SNOW_IMP.petId()) {
			setRun(true);
		}
	}

	@Getter private transient final Player owner;

	private final transient int[][] checkNearDirs;

	@Override
	public boolean isEntityClipped() {
		return false;
	}

	@Override
	public void processNPC() {
		if (isLocked()) {
			return;
		}
		if (!getLocation().withinDistance(owner.getLocation(), 12) && !owner.isTeleported()) {
			call();
			return;
		}
		if (getFaceEntity() != owner.getClientIndex()) {
			setFaceEntity(owner);
		}
        if (colliding()) {
            //TODO: Change into a more efficent pathfinding formula or write a non-pf structure.
            setRouteEvent(new NPCEntityEvent(this, new EntityStrategy(owner)));
            return;
        }

        appendMovement();
	}

    private boolean colliding() {
        return !owner.hasWalkSteps() && Utils.collides(getX(), getY(), getSize(), owner.getX(),
                owner.getY(), owner.getSize());
    }

    private boolean appendMovement() {
        val melee = getCombatDefinitions().isMelee();
        val maxDistance = isForceFollowClose() || melee ? 0 : getAttackDistance();

        if (isProjectileClipped(owner, true)
                || outOfRange(owner, maxDistance, owner.getSize(), melee)) {
            resetWalkSteps();
            calcFollow(owner, isRun() ? 2 : 1, true, isIntelligent(), isEntityClipped());
        }
        return true;
    }

    @Override
	public boolean isIntelligent() {
		return getId() == MiscPet.AREA_LOCKED_SNOW_IMP.petId();
	}

    boolean outOfRange(final Position targetPosition, final int maximumDistance, final int targetSize,
                       final boolean checkDiagonal) {
        val target = targetPosition.getPosition();
        val distanceX = getX() - target.getX();
        val distanceY = getY() - target.getY();
        val npcSize = getSize();

        if (checkDiagonal) {
            if (distanceX == -npcSize && distanceY == -npcSize
                    || distanceX == targetSize && distanceY == targetSize
                    || distanceX == -npcSize && distanceY == targetSize
                    || distanceX == targetSize && distanceY == -npcSize) {
                return true;
            }
        }

        return distanceX > targetSize + maximumDistance || distanceY > targetSize + maximumDistance || distanceX < -npcSize - maximumDistance || distanceY < -npcSize - maximumDistance;
    }
	
	public void call() {
		final int size = getSize();
		Location teleTile = null;
		for (int dir = 0; dir < checkNearDirs[0].length; dir++) {
			final Location tile = new Location(new Location(owner.getX() + checkNearDirs[0][dir],
					owner.getY() + checkNearDirs[1][dir], owner.getPlane()));
			if (World.isTileFree(tile, size)) {
				teleTile = tile;
				break;
			}
		}
		if (teleTile == null) {
			return;
		}
		setLocation(teleTile);
	}

	public Pet getPet() {
		return PetWrapper.getByPet(id);
	}
	
}
