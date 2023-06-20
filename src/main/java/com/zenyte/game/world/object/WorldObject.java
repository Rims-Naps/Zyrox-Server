package com.zenyte.game.world.object;

import com.zenyte.game.util.Direction;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import mgi.types.config.ObjectDefinitions;

import lombok.val;

public class WorldObject extends Location {

	private int objectHash;

	public WorldObject(final int id, final int type, final int rotation, final int x, final int y, final int plane) {
		super(x, y, plane);
		objectHash = (id & 0xFFFF) | ((type & 0x1F) << 16) | ((rotation & 0x3) << 21);
	}
	public WorldObject(final int id, final int type, final int rotation, final Location tile) {
		super(tile.getX(), tile.getY(), tile.getPlane());
		objectHash = (id & 0xFFFF) | ((type & 0x1F) << 16) | ((rotation & 0x3) << 21);
	}

	public WorldObject(final WorldObject object) {
		super(object.getX(), object.getY(), object.getPlane());
		objectHash = (object.getId() & 0xFFFF) | ((object.getType() & 0x1F) << 16) | ((object.getRotation() & 0x3) << 21) | (object.isLocked() ? (1 << 24) : 0);
	}
	
	public int getId() {
		return objectHash & 0xFFFF;
	}
	
	public final int getType() {
		return (objectHash >> 16) & 0x1F;
	}

	public void setType(final int type) {
		objectHash = (getId() & 0xFFFF) | ((type & 0x1F) << 16) | ((getRotation() & 0x3) << 21);
	}
	
	public final int getRotation() {
		return (objectHash >> 21) & 0x3;
	}
	
	public void setId(final int id) {
		objectHash = (id & 0xFFFF) | ((getType() & 0x1F) << 16) | ((getRotation() & 0x3) << 21);
	}
	
	public void setRotation(final int rotation) {
		objectHash = (getId() & 0xFFFF) | ((getType() & 0x1F) << 16) | ((rotation & 0x3) << 21);
	}
	
	public ObjectDefinitions getDefinitions() {
		return ObjectDefinitions.get(getId());
	}
	
	public String getName() {
		return getDefinitions().getName();
	}
	
	public int getId(final Player player) {
		int transformedId = getId();
		final ObjectDefinitions defs = getDefinitions();
        if (defs.getVarp() != -1 || defs.getVarbit() != -1) {
        	final int[] transmogrificationIds = defs.getTransformedIds();
            final int varValue = defs.getVarp() != -1 ? player.getVarManager().getValue(defs.getVarp()) : player.getVarManager().getBitValue(defs.getVarbit());
            transformedId = transmogrificationIds[varValue];
        }
        return transformedId;
	}

	public Direction getFaceDirection() {
		return getRotation() == 1 ? Direction.NORTH : getRotation() == 0 ?
				Direction.WEST : getRotation() == 2 ?
				Direction.EAST :
				Direction.SOUTH;
	}

	public String getName(final Player player) {
        return ObjectDefinitions.get(getId(player)).getName();
	}
	
	public boolean isLocked() {
		return ((objectHash >> 24) & 0x1) == 1;
	}
	
	public void setLocked(final boolean value) {
		objectHash = objectHash & 0xFFFFFF;
		if (value) {
			objectHash |= 1 << 24;
		}
	}

	public boolean exists() {
	    val region = World.getRegion(this.getRegionId());
	    return region.containsObject(getId(), getType(), this);
    }

    public boolean isMapObject() {
	    return !World.getRegion(getRegionId()).containsSpawnedObject(this);
    }
	
	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof WorldObject)) {
			return false;
		}
		val obj = (WorldObject) object;
		return obj.objectHash == objectHash && obj.getPositionHash() == getPositionHash();
	}

	@Override
	public String toString() {
		return getName() + ": " + getId() + ", " + getType() + ", " + getRotation() + ", " + isLocked() + "\n"
				+ "Tile: " + getX() + ", " + getY() + ", " + getPlane() + ", "
				+ "region[" + getRegionId() + ", " + getRegionX() + ", " + getRegionY() + "], "
						+ "chunk[" + getChunkX() + ", " + getChunkY() + "], hash [" + getPositionHash() + "]";
	}
	
}