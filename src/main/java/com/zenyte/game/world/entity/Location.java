package com.zenyte.game.world.entity;

import com.google.common.base.Preconditions;
import com.google.gson.annotations.Expose;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Position;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 17. march 2018 : 18:48.17
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class Location implements Position {

    //@Expose protected int x, y, z;
    @Expose protected int hash;

    public Location(final int x, final int y, final int z) {
        hash = y | x << 14 | z << 28;
        //this.x = x;
        //this.y = y;
        //this.z = z;
    }

    public Location(final int x, final int y) {
        this(x, y, 0);
    }

    public Location(final Location tile) {
        this(tile.getX(), tile.getY(), tile.getPlane());
    }

    public Location(final int hash) {
        this(hash >> 14 & 0x3FFF, hash & 0x3FFF, hash >> 28 & 0x3);
    }

    public Location(final Location loc, final int randomize) {
        this(loc.getX() + (Utils.random(1) == 0 ? Utils.random(0, -randomize) : Utils.random(randomize)), loc.getY() + (Utils.random(1) == 0 ? Utils.random(0, -randomize) : Utils.random(randomize)), loc.getPlane());
    }

    public Location transform(final Direction direction) {
        return transform(direction.getOffsetX(), direction.getOffsetY(), 0);
    }

    public Location transform(final int diffX, final int diffY, final int diffZ) {
        return new Location(getX() + diffX, getY() + diffY, getPlane() + diffZ);
    }

    public Location transform(final Direction direction, final int distance) {
        return new Location(getX() + (direction.getOffsetX() * distance),
                getY() + (direction.getOffsetY() * distance), getPlane());
    }

    public static final int hash(final int x, final int y, final int z) {
        return y | x << 14 | z << 28;
    }

    public final int get18BitHash() {
        return getY() >> 13 | (getX() >> 13) << 8 | getPlane() << 16;
    }

    public Location moveLocation(final int xOffset, final int yOffset, final int planeOffset) {
        int x = getX(), y = getY(), z = getPlane();
        x += xOffset;
        y += yOffset;
        z += planeOffset;
        hash = y | x << 14 | z << 28;
        return this;
    }

    public void setLocation(final int x, final int y, final int plane) {
        hash = y | x << 14 | plane << 28;
    }

    public void setLocation(final int hash) {
        this.hash = hash;
    }

    public void setLocation(final Location tile) {
        hash = tile.getPositionHash();
    }

    public int getRegionHash() {
        return getRegionY() + (getRegionX() << 8) + (getPlane() << 16);
    }

    public int getChunkHash() {
        return getChunkX() | getChunkY() << 11 | (getPlane() & 0x3) << 22;
    }

    public boolean withinDistance(final int x, final int y, final int distance) {
        final int deltaX = x - getX(), deltaY = y - getY();
        return deltaX <= distance && deltaX >= -distance && deltaY <= distance && deltaY >= -distance;
    }

    public boolean withinDistance(@NotNull final Position position, final int distance) {
        val tile = position.getPosition();
        if (tile.getPlane() != getPlane()) {
            return false;
        }
        final int deltaX = tile.getX() - getX(), deltaY = tile.getY() - getY();
        return deltaX <= distance && deltaX >= -distance && deltaY <= distance && deltaY >= -distance;
    }

    public int getX() {//y | x << 14 | z << 28
        return (hash >> 14) & 0x3FFF;
    }

    public int getY() {
        return hash & 0x3FFF;
    }

    public int getPlane() {
        return (hash >> 28) & 0x3;
    }

    public int getLocalX(final Location l) {
        return getX() - 8 * (l.getChunkX() - 6);
    }

    public int getLocalY(final Location l) {
        return getY() - 8 * (l.getChunkY() - 6);
    }

    public int getLocalHash(final Location tile) {
        return ((getLocalX(tile) & 0x7) << 4) | (getLocalY(tile) & 0x7);
    }

    public int getChunkX() {
        return (getX() >> 3);
    }

    public int getChunkY() {
        return (getY() >> 3);
    }

    public int getRegionX() {
        return (getX() >> 6);
    }

    public int getRegionY() {
        return (getY() >> 6);
    }

    public int getXInRegion() {
        return getX() & 0x3F;
    }

    public int getYInRegion() {
        return getY() & 0x3F;
    }

    public int getXInChunk() {
        return getX() & 0x7;
    }

    public int getYInChunk() {
        return getY() & 0x7;
    }

    public int getRegionId() {
        return ((getRegionX() << 8) + getRegionY());
    }

    public Location random(final int radius) {
        val xOff = Utils.random(-radius, radius);
        val yOff = Utils.random(-radius, radius);
        return new Location(getX() + xOff, getY() + yOff, getPlane());
    }

    public Location random(final int radius, final int excludedRadius) {
        Preconditions.checkArgument(radius > excludedRadius, "Can not get random tile with excluded radius larger than included.");
        return random(radius, radius, radius, radius, excludedRadius, excludedRadius, excludedRadius, excludedRadius);
    }

    public Location random(final int minX, final int maxX, final int minY, final int maxY,
                           final int excludedMinX, final int excludedMaxX, final int excludedMinY, final int excludedMaxY) {
        var xOff = 0;
        var yOff = 0;
        if (Utils.random(1) == 0) {
            xOff = Utils.random(1) == 0 ? -Utils.random(excludedMinX + 1, minX) : Utils.random(excludedMaxX + 1, maxX);
            yOff = Utils.random(1) == 0 ? -Utils.random(Math.min(0, excludedMinY - xOff + 1), minY) : Utils.random(Math.min(0, excludedMaxY - xOff + 1), maxY);
        } else {
            yOff = Utils.random(1) == 0 ? -Utils.random(excludedMinY + 1, minX) : Utils.random(excludedMaxY + 1, maxX);
            xOff = Utils.random(1) == 0 ? -Utils.random(Math.min(0, excludedMinX - yOff + 1) , minY) : Utils.random(Math.min(0, excludedMaxX - yOff + 1), maxY);
        }
        return new Location(getX() + xOff, getY() + yOff, getPlane());
    }

    public static final int getRegionId(final int x, final int y) {
        return (((x >> 6) << 8) + (y >> 6));
    }

    public int getChunkXInScene(final Entity entity) {
        ////(x) | (y << 11) | (plane << 22)
        return getChunkX() - (entity.getSceneBaseChunkId() & 2047);
        //MapUtils.decode(Structure.CHUNK, entity.getSceneBaseChunkId())[0];
    }

    public int getChunkYInScene(final Entity entity) {
        return getChunkY() - (entity.getSceneBaseChunkId() >> 11 & 2047);
        //MapUtils.decode(Structure.CHUNK, entity.getSceneBaseChunkId())[1];
    }

    public int getXInScene(final Entity entity) {
        return getX() - ((entity.getSceneBaseChunkId() & 2047) << 3);
        //MapUtils.decode(Structure.CHUNK, entity.getSceneBaseChunkId())[0] * 8;
    }

    public int getYInScene(final Entity entity) {
        return getY() - ((entity.getSceneBaseChunkId() >> 11 & 2047) << 3);
        //MapUtils.decode(Structure.CHUNK, entity.getSceneBaseChunkId())[1] * 8;
    }

    @Deprecated
    public double getDistance(final Location other) {
        final int xdiff = getX() - other.getX();
        final int ydiff = getY() - other.getY();
        return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
    }

    @Deprecated
    public double getDistance(final int x, final int y) {
        final int xdiff = getX() - x;
        final int ydiff = getY() - y;
        return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
    }

    public int getTileDistance(final Location other) {
        final int deltaX = other.getX() - getX(), deltaY = other.getY() - getY();
        return Math.max(Math.abs(deltaX), Math.abs(deltaY));
    }

    public int getPositionHash() {
        return hash;
    }

    public static final int getHash(final int x, final int y, final int plane) {
        return y | x << 14 | plane << 28;
    }


    public static final int getX(final int hash) {
        return (hash >> 14) & 0x3FFF;
    }

    public static final int getY(final int hash) {
        return hash & 0x3FFF;
    }

    public static final int getPlane(final int hash) {
        return (hash >> 28) & 0x3;
    }

    public boolean equals(final int x, final int y, final int plane) {
        return getX() == x && getY() == y && getPlane() == plane;
    }

    @Override
    public int hashCode() {
        return getPositionHash();
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Location)) {
            return false;
        }
        return ((Location) other).getPositionHash() == getPositionHash();
    }

    @Override
    public String toString() {
        return "Tile: " + getX() + ", " + getY() + ", " + getPlane() + ", "
                + "region[" + getRegionId() + ", " + getRegionX() + ", " + getRegionY() + "], "
                + "chunk[" + getChunkX() + ", " + getChunkY() + "], hash [" + getPositionHash() + "]";
    }

    public int hashInRegion() {
        return ((getX() & 0x3F) << 6) | (getY() & 0x3F) | ((getPlane() & 0x3) << 12);
    }

    public int getCoordFaceX(final int sizeX) {
        return getCoordFaceX(sizeX, -1, -1);
    }

    public int getCoordFaceX(final int sizeX, final int sizeY, final int rotation) {
        return getX() + ((rotation == 1 || rotation == 3 ? sizeY : sizeX) - 1) / 2;
    }

    public int getCoordFaceY(final int sizeY) {
        return getCoordFaceY(-1, sizeY, -1);
    }

    public int getCoordFaceY(final int sizeX, final int sizeY, final int rotation) {
        return getY() + ((rotation == 1 || rotation == 3 ? sizeX : sizeY) - 1) / 2;
    }

    public float getPreciseCoordFaceX(final int sizeX, final int sizeY, final int rotation) {
        final int sizeCoord = rotation == 1 || rotation == 3 ? sizeY : sizeX;
        if (sizeCoord % 2 == 0) {
            return getX() + (sizeCoord / 4f);
        }
        return getX() + sizeCoord / 2;
    }

    public float getPreciseCoordFaceY(final int sizeX, final int sizeY, final int rotation) {
        final int sizeCoord = rotation == 1 || rotation == 3 ? sizeX : sizeY;
        if (sizeCoord % 2 == 0) {
            return getY() + (sizeCoord / 4f);
        }
        return getY() + (int) (sizeCoord / 2f);
    }

    @Override
    public Location getPosition() {
        return this;
    }

}