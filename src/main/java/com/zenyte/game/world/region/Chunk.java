package com.zenyte.game.world.region;

import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.flooritem.FloorItem;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.utils.efficientarea.EfficientArea;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.val;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Kris | 28. march 2018 : 17:28.55
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@EqualsAndHashCode
public final class Chunk implements MapSquare {

	/**
	 * The bitpacked if of this chunk.
	 */
	@Getter
	private final int chunkId;

	@Getter
	private long referenceTime;

	public Chunk resetReferenceTime() {
		referenceTime = System.currentTimeMillis();
		return this;
	}

	/**
	 * List of players in the chunk.
	 */
	@Getter
	private final List<Player> players;

	/**
	 * List of NPCs in the chunk.
	 */
	private final List<NPC> npcs;

    /**
     * List of objects that have been spawned.
     */
    @Getter
    private Short2ObjectMap<WorldObject> spawnedObjects;

    /**
     * List of original map-bound objects that have been removed or replaced.
     */
    @Getter
    private Short2ObjectMap<WorldObject> originalObjects;

    @Getter
    private Set<FloorItem> floorItems;

	@Getter @Nullable private List<RSArea> multiPolygons;

	public boolean isFree() {
		return players.isEmpty() && npcs.isEmpty()
				&& (spawnedObjects == null || spawnedObjects.isEmpty())
				&& (originalObjects == null || originalObjects.isEmpty())
				&& (floorItems == null || floorItems.isEmpty())
				&& (multiPolygons == null || multiPolygons.isEmpty());
	}

	public Chunk(final int hash) {
		chunkId = hash;
		players = new ArrayList<>();
		npcs = new ArrayList<>();
		spawnedObjects = new Short2ObjectOpenHashMap<>();
		originalObjects = new Short2ObjectOpenHashMap<>();
		floorItems = new HashSet<>();
	}

	public void addFloorItem(final FloorItem item) {
	    floorItems.add(item);
	    World.getAllFloorItems().add(item);
	    if (item.getInvisibleTicks() <= 0) {
	        World.destroyFloorItem(getRemovedItemIfCapReached());
        }
    }

    public void removeFloorItem(final FloorItem item, final boolean removeFromGlobal) {
	    floorItems.remove(item);
	    if (removeFromGlobal)
            World.getAllFloorItems().remove(item);
    }

    public FloorItem getRemovedItemIfCapReached() {
	    if (floorItems.size() <= 128) {
	        return null;
        }
	    int count = 0;
	    for (val item : floorItems) {
	        if (item == null || item.getInvisibleTicks() > 0 || !item.isTradable()) {
	            continue;
            }
	        count++;
        }
	    if (count > 129) {
	        FloorItem cheapest = null;
            for (val item : floorItems) {
                if (item == null || item.getInvisibleTicks() > 0 || !item.isTradable()) {
                    continue;
                }
                if (cheapest == null || ((long) item.getSellPrice() * item.getAmount()) < ((long) cheapest.getSellPrice() * cheapest.getAmount())) {
                    cheapest = item;
                }
            }
            return cheapest;
        }
	    return null;
    }

    public void clearFloorItems() {
	    if (floorItems.isEmpty())
	        return;
	    for (val floorItem : floorItems) {
            World.getAllFloorItems().remove(floorItem);
        }
        floorItems.clear();
    }

    public void wipe() {
	    clearFloorItems();
        spawnedObjects.clear();
        originalObjects.clear();
    }

	public void addMultiPolygon(final RSArea area) {
	    if (multiPolygons == null)
	        multiPolygons = new ArrayList<>();
	    multiPolygons.add(area);
    }

    @AllArgsConstructor
    @Getter
    public static final class RSArea {
	    private final int height;
	    private final EfficientArea area;
    }

	public static final int getChunkHash(final int chunkX, final int chunkY, final int height) {
        return chunkX | chunkY << 11 | height << 22;
    }

    public final int getChunkX() {
	    return chunkId & 0x7FF;
    }

    public final int getChunkY() {
	    return chunkId >> 11 & 0x7FF;
    }

    public final int getChunkZ() {
	    return chunkId >> 22;
    }

	@Override
	public List<NPC> getNPCs() {
		return npcs;
	}
}
