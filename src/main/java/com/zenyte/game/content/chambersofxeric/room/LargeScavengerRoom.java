package com.zenyte.game.content.chambersofxeric.room;

import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.chambersofxeric.map.RaidRoom;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.WorldObject;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.Set;

/**
 * @author Kris | 16. nov 2017 : 2:37.54
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class LargeScavengerRoom extends ScavengerRoom {

    /**
     * The blocking object ids as well as their transformations, if applicable.
     */
    public static final int ROCK = 29738, TREE = 29736, BOULDER = 29740, RUBBLE = 29739, STUB = 29737;
    /**
     * The tiles where the blocking tree, boulder or rocks will spawn at.
     */
    private static final Location[] blockingObjectTiles = new Location[]{
            new Location(3272, 5222, 1),
            new Location(3314, 5238, 1),
            new Location(3353, 5224, 1)
    };
    /**
     * The blocking object ids that they spawn as.
     */
    private static final int[] blockingObjects = new int[]{
            ROCK, TREE, BOULDER
    };

    public LargeScavengerRoom(final RaidRoom type, final Raid raid, final int rotation, final int size, final int regionX, final int regionY, final int chunkX, final int chunkY, final int fromPlane, final int toPlane) {
        super(type, raid, rotation, size, regionX, regionY, chunkX, chunkY, fromPlane, toPlane);
    }

    @Override
    public void loadRoom() {
        super.loadRoom();
        val id = blockingObjects[Utils.random(blockingObjects.length - 1)];
        val skill = id == ROCK ? Skills.MINING : id == TREE ? Skills.WOODCUTTING : Skills.STRENGTH;
        val randomLevel = Math.min(99, Utils.random(getLowestLevel(skill), getHighestLevel(skill) + 2));
        World.spawnObject(new BlockingObject(id, 10, getRotation(), getLocation(blockingObjectTiles[index]), randomLevel));
    }

    @Override
    public String name() {
        return "Chambers of Xeric: Large Scavenger room";
    }

    /**
     * The blocking object class used to provide common variables between all the blocking objects.
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class BlockingObject extends WorldObject {

        /**
         * A set of players who are interacting with this object at the time, needed to track due to it giving points to everyone who is interacting with it at the time, not just
         * the person who manages to deplete the object.
         */
        private final Set<Player> interactingPlayers = new ObjectOpenHashSet<>();

        /**
         * The level in a specific determined skill to interact with this object.
         */
        @Setter
        private int levelRequired;

        private BlockingObject(final int id, final int type, final int rotation, final Location tile, final int levelRequired) {
            super(id, type, rotation, tile);
            this.levelRequired = levelRequired;
        }
    }

}
