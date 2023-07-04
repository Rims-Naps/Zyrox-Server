package com.zenyte.game.world.region;

import com.zenyte.utils.MultiwayArea;
import com.zenyte.utils.efficientarea.Area;
import com.zenyte.utils.efficientarea.EfficientArea;
import com.zenyte.utils.efficientarea.Polygon;

import com.zenyte.Game;
import com.zenyte.game.music.Music;
import com.zenyte.game.packet.out.LocAdd;
import com.zenyte.game.packet.out.LocDel;
import com.zenyte.game.util.IntArray;
import com.zenyte.game.world.SceneSynchronization;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.pathfinding.Flags;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.flooritem.FloorItem;
import com.zenyte.game.world.flooritem.GlobalItem;
import com.zenyte.game.world.object.WorldObject;
import mgi.types.config.ObjectDefinitions;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

@Slf4j
public class Region {

	/**
	 * A list of object slots, index is equivalent to type.
	 */
	public static final int[] OBJECT_SLOTS = new int[] { 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3 };

	/**
	 * A volatile integer defining the map load stage. Volatile as it's accessed by multiple threads.
	 */
	private volatile int loadStage;

	/**
	 * Region's Id.
	 */
	protected final int regionId;

	/**
	 * A hashmap of the hash of localX, localY, slot & plane, and object.
	 */
	@Getter
	protected Short2ObjectMap<WorldObject> objects;
	
    @Getter private final Set<Music> musicTracks = new HashSet<>();


	/**
	 * Region's map.
	 */
	protected RegionMap map;
	
	public Region(final int regionId) {
		this.regionId = regionId;
	}

	public final int getId() {
		return regionId;
	}

    /**
     *
     * @param chunkXInRegion
     * @param chunkYInRegion
     * @param chunkZ
     * @param copiedChunkHash
     */
	public void addMultiZone(final int chunkXInRegion, final int chunkYInRegion, final int chunkZ, final int copiedChunkHash) {
	    val copiedChunkX = copiedChunkHash & 0x7FF;
		val copiedChunkY = copiedChunkHash >> 11 & 0x7FF;
		val copiedChunkZ = copiedChunkHash >> 22 & 0x3;
        val x = copiedChunkX << 3;
        val y = copiedChunkY << 3;

        val chunkPolygon = new RSPolygon(new int[][] {
                new int[] { x, y },
                new int[] { x + 8, y },
                new int[] { x + 8, y + 8},
                new int[] { x, y + 8 }
        }, copiedChunkZ).getPolygon();

        val chunkArea = new Area(chunkPolygon);

        val regionChunkX = (regionId >> 8) << 3;
        val regionChunkY = (regionId & 0xFF) << 3;

        val allPolygons = MultiwayArea.getPolygons();

        val polygons = allPolygons.get(copiedChunkZ);

        for (int i = polygons.size() - 1; i >= 0; i--) {
            val polygon = polygons.get(i);

            if (!polygon.getBounds2D().intersects(chunkArea.getBounds2D())) {
                continue;
            }

            val area = new Area(polygon);

            area.intersect(chunkArea);

            if (!area.isEmpty()) {
                val xOffset = ((regionChunkX + chunkXInRegion) << 3) - x;
                val yOffset = ((regionChunkY + chunkYInRegion) << 3) - y;

                val chunk = World.getChunk(Chunk.getChunkHash(regionChunkX + chunkXInRegion, regionChunkY + chunkYInRegion, chunkZ));
                val repositionedArea = getRepositionedArea(area, xOffset, yOffset);
                val efficientAreas = MultiwayArea.addDynamicMultiArea(repositionedArea, chunkZ);
                for (val efficientArea : efficientAreas) {
                    chunk.addMultiPolygon(new Chunk.RSArea(chunkZ, efficientArea));
                }
            }
        }
	}

	private Area getRepositionedArea(final Area area, final int xOffset, final int yOffset) {
        val list = new ArrayList<int[]>();
        val it = area.getPathIterator(null);
        while (!it.isDone()) {
            val coords = new float[6];
            it.currentSegment(coords);
            it.next();
            if (coords[0] == 0 || coords[1] == 0) {
                continue;
            }
            list.add(IntArray.of((int) coords[0] + xOffset, (int) coords[1] + yOffset));
        }

        val size = list.size();
        val array = new int[size][2];

        for (int i = 0; i < size; i++) {
            array[i] = list.get(i);
        }

        val rspolygon = new RSPolygon(array);
        val polygon = rspolygon.getPolygon();
        return new Area(polygon);
    }

	
	void removeMultiZone(final int chunkX, final int chunkY, final int z) {
	    val chunk = World.getChunk(Chunk.getChunkHash(chunkX, chunkY, z));
	    val multiAreas = chunk.getMultiPolygons();
	    if (multiAreas != null) {
	        for (int i = multiAreas.size() - 1; i >= 0; i--) {
	            val area = multiAreas.get(i);
	            MultiwayArea.removeDynamicMultiArea(area);
            }
        }
	}

	public final RegionMap getRegionMap(final boolean load) {
		if (load && map == null) {
			map = new RegionMap(regionId);
		}
		return map;
	}

	public final int getMask(final int plane, final int localX, final int localY) {
		if (map == null || loadStage != 2) {
			return -1;
		}
		return map.getLocalMask(plane, localX & 0x3F, localY & 0x3F);
	}

	public final int getLoadStage() {
		return loadStage;
	}

	public final void setLoadStage(final int loadMapStage) {
		loadStage = loadMapStage;
	}

	/**
	 * Get's ground item with specific id on the specific location in this region.
	 */
	public final FloorItem getFloorItem(final int id, final Location tile, final Player player) {
        val chunk = this.getChunk(tile.getX() & 0x3F, tile.getY() & 0x3F, tile.getPlane());
        val floorItems = chunk.getFloorItems();
		if (floorItems == null) {
			return null;
		}

        FloorItem unpickableItem = null;

		for (final FloorItem item : floorItems) {
			if (!item.isVisibleTo(player)) {
				continue;
			}
			if (item.getId() == id && tile.getPositionHash() == item.getLocation().getPositionHash()) {
			    if (player.isIronman() && !player.getUsername().equals(item.getOwnerName())) {
			        if (unpickableItem == null) {
			            unpickableItem = item;
			            continue;
                    }
                }
				return item;
			}
		}
		return unpickableItem;
	}

    /**
     * Get's ground item with specific id on the specific location in this region.
     */
    public final FloorItem getFloorItem(final int id, final Location tile) {
        val chunk = this.getChunk(tile.getX() & 0x3F, tile.getY() & 0x3F, tile.getPlane());
        val floorItems = chunk.getFloorItems();
        if (floorItems == null) {
            return null;
        }

        for (final FloorItem item : floorItems) {
            if (item.getId() == id && tile.getPositionHash() == item.getLocation().getPositionHash()) {
                return item;
            }
        }
        return null;
    }

	public final void setMask(final int plane, final int localX, final int localY, final int mask) {
		if (map == null || loadStage != 2) {
			return;
		}
		if (localX >= 64 || localY >= 64 || localX < 0 || localY < 0) {
			final Location tile = new Location(map.getRegionX() + localX, map.getRegionY() + localY, plane);
			final int regionId = tile.getRegionId();
			final int newRegionX = (regionId >> 8) * 64;
			final int newRegionY = (regionId & 0xff) * 64;
			World.getRegion(tile.getRegionId(), false).setMask(plane, tile.getX() - newRegionX, tile.getY() - newRegionY, mask);
			return;
		}
		map.setFlag(plane, localX, localY, mask);
	}
	
	public final void addFlag(final int plane, final int localX, final int localY, final int mask) {
		if (map == null || loadStage != 2) {
			return;
		}
		map.setFlag(plane, localX, localY, mask, true);
	}
	
	
	public final void removeFlag(final int plane, final int localX, final int localY, final int mask) {
		if (map == null || loadStage != 2) {
			return;
		}
		map.setFlag(plane, localX, localY, mask, false);
	}
	
	public final void unclip(final int plane, final int x, final int y) {
		if (map == null) {
			map = new RegionMap(regionId);
		}
		map.setFlag(plane, x, y, 0);
	}

	public void destroy() {
    	loadStage = 0;
    	if (objects != null) {
    		objects.clear();
    		objects = null;
		}

    	musicTracks.clear();
    	map = null;
	}

	public void load() {
		if (loadStage != 0) {
			return;
		}
		loadStage = 1;
		try {
			val set = GlobalItem.getGlobalItems(regionId);
			if (set != null) {
				for (val item : set) {
					item.spawn();
				}
			}
			loadRegionMap();
			loadStage = 2;
		} catch (final Throwable e) {
            log.error(Strings.EMPTY, e);
		}
	}

	public final void clip(final int plane, final int x, final int y) {
		if (map == null) {
			map = new RegionMap(regionId);
		}
		map.setFlag(plane, x, y, Flags.FLOOR_DECORATION);
	}

	private final void clip(final WorldObject object, final int x, final int y) {
		if (map == null) {
			map = new RegionMap(regionId);
		}
		final int plane = object.getPlane();
		final int type = object.getType();
		final int orientation = object.getRotation();
		if (x < 0 || y < 0 || x >= 64 || y >= 64) {
			return;
		}
		final ObjectDefinitions objectDefinition = ObjectDefinitions.get(object.getId());
		if (objectDefinition == null) {
			return;
		}
		if (type == 22 ? objectDefinition.getClipType() != 1 : objectDefinition.getClipType() == 0) {
			return;
		}
		if (type >= 0 && type <= 3) {
		    map.setWall(plane, x, y, type, orientation, objectDefinition.isProjectileClip(), true);
		} else if (type >= 9 && type <= 21) {
		    val reverseSizes = (orientation & 0x1) == 0x1;
		    val width = reverseSizes ? objectDefinition.getSizeY() : objectDefinition.getSizeX();
            val height = reverseSizes ? objectDefinition.getSizeX() : objectDefinition.getSizeY();
			map.setObject(plane, x, y, width, height, objectDefinition.isProjectileClip(), true);
		} else if (type == 22) {
			map.setFloor(plane, x, y, true);
		}
	}

	private final void unclip(final WorldObject object, final int x, final int y) {
		if (map == null) {
			map = new RegionMap(regionId);
		}
		final int plane = object.getPlane();
		final int type = object.getType();
		final int rotation = object.getRotation();
		if (x < 0 || y < 0 || x >= 64 || y >= 64) {
			return;
		}
		final ObjectDefinitions objectDefinition = ObjectDefinitions.get(object.getId());
		if (objectDefinition == null) {
			return;
		}
		if (type == 22 ? objectDefinition.getClipType() != 1 : objectDefinition.getClipType() == 0) {
			return;
		}
		if (type >= 0 && type <= 3) {
			map.setWall(plane, x, y, type, rotation, objectDefinition.isProjectileClip(), false);
		} else if (type >= 9 && type <= 21) {
            val reverseSizes = (rotation & 0x1) == 0x1;
            val width = reverseSizes ? objectDefinition.getSizeY() : objectDefinition.getSizeX();
            val height = reverseSizes ? objectDefinition.getSizeX() : objectDefinition.getSizeY();
			map.setObject(plane, x, y, width, height, objectDefinition.isProjectileClip(), false);
		} else if (type == 22) {
			map.setFloor(plane, x, y, false);
		}
	}

	private Chunk getChunk(final int localX, final int localY, final int plane) {
	    val x = ((regionId >> 8 & 0xFF) << 6) + localX;
	    val y = ((regionId & 0xFF) << 6) + localY;
	    val chunkId = Chunk.getChunkHash(x >> 3, y >> 3, plane);
        return World.getChunk(chunkId);
    }

	public void spawnObject(final WorldObject object, final int plane, final int localX, final int localY, final boolean mapObject, final boolean alterClipping) {
		if (objects == null) {
			objects = new Short2ObjectOpenHashMap<>();
        }

        val slot = OBJECT_SLOTS[object.getType()];
        val hash = (short) (localX | localY << 6 | slot << 12 | plane << 14);
        if (mapObject) {
            objects.put(hash, object);
            if (alterClipping) {
				clip(object, localX, localY);
			}
            return;
        }
        val chunk = this.getChunk(localX, localY, plane);
        val originalObjects = chunk.getOriginalObjects();
        val spawnedObjects = chunk.getSpawnedObjects();

        originalObjects.remove(hash);
        val original = objects.get(hash);
        val spawned = spawnedObjects.remove(hash);

        if (spawned != null && alterClipping) {
            unclip(spawned, localX, localY);
        }

        if (!Objects.equals(original, object)) {
            if (original != null && alterClipping) {
                unclip(original, localX, localY);
            }
            spawnedObjects.put(hash, object);
            originalObjects.put(hash, object);
        }
        if (alterClipping) {
			clip(object, localX, localY);
		}
		SceneSynchronization.forEach(object, player -> new LocAdd(object));
    }

	public void removeObject(final WorldObject object, final int plane, final int localX, final int localY) {
		if (objects == null) {
			objects = new Short2ObjectOpenHashMap<>();
		}
		final int slot = OBJECT_SLOTS[object.getType()];
		final short hash = (short) (localX | localY << 6 | slot << 12 | plane << 14);
        val chunk = this.getChunk(localX, localY, plane);
        val originalObjects = chunk.getOriginalObjects();
        val spawnedObjects = chunk.getSpawnedObjects();
		val spawned = spawnedObjects.remove(hash);
		val current = objects.get(hash);
		if (spawned != null) {
			unclip(spawned, localX, localY);
		}
		if (current != null) {
			unclip(current, localX, localY);
			originalObjects.put(hash, current);
		}
		SceneSynchronization.forEach(object, player -> new LocDel(object));
	}

	private final WorldObject getSpawnedObjectWithSlot(final int plane, final int x, final int y, final int slot) {
        val chunk = this.getChunk(x, y, plane);
        val spawnedObjects = chunk.getSpawnedObjects();
		if (spawnedObjects == null) {
			return null;
		}
		final short hash = (short) (x | y << 6 | slot << 12 | plane << 14);
		return spawnedObjects.get(hash);
	}

	public void loadRegionMap() {
		try {
			val baseX = (regionId >> 8) * 64;
			val baseY = (regionId & 0xff) * 64;
			val xteas = XTEALoader.getXTEAs(regionId);

			val cache = Game.getCacheMgi();
			val archive = cache.getArchive(ArchiveType.MAPS);

			val mapGroup = archive.findGroupByName("m" + (regionId >> 8) + "_" + (regionId & 0xFF));
			val landGroup = archive.findGroupByName("l" + (regionId >> 8) + "_" + (regionId & 0xFF), xteas);

			val mapBuffer = mapGroup == null ? null : mapGroup.findFileByID(0).getData();
			val landBuffer = landGroup == null ? null : landGroup.findFileByID(0).getData();

			val mapSettings = mapBuffer == null ? null : new byte[4][64][64];

			if (mapBuffer != null) {
				mapBuffer.setPosition(0);
				for (int plane = 0; plane < 4; plane++) {
					for (int x = 0; x < 64; x++) {
						for (int y = 0; y < 64; y++) {
							while (true) {
								val value = mapBuffer.readUnsignedByte();
								if (value == 0) {
									break;
								} else if (value == 1) {
									mapBuffer.readByte();
									break;
								} else if (value <= 49) {
									mapBuffer.readByte();
								} else if (value <= 81) {
									mapSettings[plane][x][y] = (byte) (value - 49);
								}
							}
						}
					}
				}

				for (int plane = 0; plane < 4; plane++) {
					for (int x = 0; x < 64; x++) {
						for (int y = 0; y < 64; y++) {
							if ((mapSettings[plane][x][y] & 1) == 1) {
								int realPlane = plane;
								if ((mapSettings[1][x][y] & 2) == 2) {
									realPlane--;
								}
								if (realPlane >= 0) {
									getRegionMap(true).setFloor(realPlane, x, y, true);
								}
							}
						}
					}
				}
			} else {
                for (int plane = 0; plane < 4; plane++) {
					for (int x = 0; x < 64; x++) {
						for (int y = 0; y < 64; y++) {
							getRegionMap(true).setFloor(plane, x, y, true);
						}
					}
				}
			}
			if (landBuffer != null) {
				landBuffer.setPosition(0);
				int objectId = -1;
				int incr;
				while ((incr = landBuffer.readHugeSmart()) != 0) {
					objectId += incr;
					int location = 0;
					int incr2;
					while ((incr2 = landBuffer.readUnsignedSmart()) != 0) {
						location += incr2 - 1;
						val localX = (location >> 6 & 0x3f);
						val localY = (location & 0x3f);
						val plane = location >> 12;
						val objectData = landBuffer.readUnsignedByte();
						val type = objectData >> 2;
						val rotation = objectData & 0x3;
                        int objectPlane = plane;
						if (mapSettings != null && (mapSettings[1][localX][localY] & 2) == 2) {
							objectPlane--;
						}
						if (objectPlane < 0 || objectPlane >= 4 || plane < 0 || plane >= 4) {
							continue;
						}
                        spawnObject(new WorldObject(objectId, type, rotation, localX + baseX, localY + baseY,
                                        objectPlane), objectPlane,
                                localX, localY, true, true);
                    }
				}
			}
		} catch (final Exception ignored) {
			System.err.println(regionId);
		    ignored.printStackTrace();
		}
	}

	public boolean containsSpawnedObject(final WorldObject object) {

	    val chunk = this.getChunk(object.getX() & 0x3F, object.getY() & 0x3F, object.getPlane());
	    val spawnedObjects = chunk.getSpawnedObjects();

		if (spawnedObjects == null) {
			return false;
		}
		final short hash = (short) (object.getXInRegion() | object.getYInRegion() << 6 | OBJECT_SLOTS[object.getType()] << 12
				| object.getPlane() << 14);
		final WorldObject obj = spawnedObjects.get(hash);
		return obj == object;
	}

	public boolean containsObjectWithId(final int plane, final int x, final int y, final int id) {
		final WorldObject object = getObjectWithId(plane, x, y, id);
		return object != null && object.getId() == id;
	}

    public WorldObject getObjectWithId(final int plane, final int x, final int y, final int id) {
        if (objects == null) {
            return null;
        }

        for (int i = 0; i < 4; i++) {
            final short hash = (short) (x | y << 6 | i << 12 | plane << 14);
            val chunk = this.getChunk(x, y, plane);
            val spawnedObjects = chunk.getSpawnedObjects();
            final WorldObject object = spawnedObjects.get(hash);
            if (object != null) {
                if (object.getId() == id) {
                    return object;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            final short hash = (short) (x | y << 6 | i << 12 | plane << 14);
            final WorldObject object = objects.get(hash);
            if (object == null)
                continue;
            if (object.getId() == id) {
                val chunk = this.getChunk(x, y, plane);
                val removedObjects = chunk.getOriginalObjects();
                if (removedObjects.get(hash) != null)
                    continue;
                return object;
            }
        }

        return null;
    }

	public boolean containsObjectWithEqualSlot(final int plane, final int x, final int y, final int type) {
		return getObjectOfSlot(plane, x, y, type) != null;
	}

    public WorldObject getObjectWithType(final Location tile,
                                         final int type) {
        return getObjectWithType(tile.getPlane(), tile.getX(), tile.getY(), type);
    }

    public WorldObject getObjectOfSlot(final int locationZ, final int locationX, final int locationY,
                                         final int type) {
        if (objects == null) {
            return null;
        }
        val x = locationX & 0x3F;
        val y = locationY & 0x3F;
        val plane = locationZ & 0x3;
        final int slot = OBJECT_SLOTS[type];
        final short hash = (short) (x | y << 6 | slot << 12 | plane << 14);
        val chunk = this.getChunk(x, y, plane);
        val spawnedObjects = chunk.getSpawnedObjects();
        final WorldObject spawnedObj = spawnedObjects.get(hash);
        if (spawnedObj != null) {
            return spawnedObj;
        }
        final WorldObject object = objects.get(hash);
        if (object != null) {
            final WorldObject spawned = getSpawnedObjectWithSlot(plane, x, y, OBJECT_SLOTS[object.getType()]);
            if (spawned == null) {
                val originalObjects = chunk.getOriginalObjects();
                if (originalObjects.containsKey(hash)) {
                    return null;
                }
            }
            return spawned == null ? object : null;
        }
        return null;
    }

	public WorldObject getObjectWithType(final int locationZ, final int locationX, final int locationY,
                                         final int type) {
		if (objects == null) {
			return null;
		}
		val x = locationX & 0x3F;
		val y = locationY & 0x3F;
		val plane = locationZ & 0x3;
		final int slot = OBJECT_SLOTS[type];
		final short hash = (short) (x | y << 6 | slot << 12 | plane << 14);
        val chunk = this.getChunk(x, y, plane);
        val spawnedObjects = chunk.getSpawnedObjects();
		final WorldObject spawnedObj = spawnedObjects.get(hash);
		if (spawnedObj != null) {
			return spawnedObj;
		}
		final WorldObject object = objects.get(hash);
		if (object != null && object.getType() == type) {
			final WorldObject spawned = getSpawnedObjectWithSlot(plane, x, y, OBJECT_SLOTS[object.getType()]);
			if (spawned == null) {
                val originalObjects = chunk.getOriginalObjects();
				if (originalObjects.containsKey(hash)) {
					return null;
				}
			}
			return spawned == null ? object : null;
		}
		return null;
	}

	public boolean containsObject(final int id, final int type, final Location tile) {
		final int absX = (regionId >> 8) * 64;
		final int absY = (regionId & 0xff) * 64;
		final int localX = tile.getX() - absX;
		final int localY = tile.getY() - absY;
		if (localX < 0 || localY < 0 || localX >= 64 || localY >= 64) {
			return false;
		}
		final WorldObject spawnedObject = getSpawnedObject(tile, object -> Region.OBJECT_SLOTS[object.getType()] == Region.OBJECT_SLOTS[type]);
		if (spawnedObject != null) {
			return spawnedObject.getId() == id;
		}
		final WorldObject[] mapObjects = getObjects(tile.getPlane(), localX, localY);
		if (mapObjects == null || getRemovedObject(tile, object -> Region.OBJECT_SLOTS[object.getType()] == Region.OBJECT_SLOTS[type]) != null) {
			return false;
		}
		for (final WorldObject object : mapObjects) {
			if (object == null) {
				continue;
			}
			if (object.getId() == id && object.getType() == type) {
				return true;
			}
		}
		return false;
	}

	public WorldObject[] getObjects(final int plane, final int x, final int y) {
		if (objects == null) {
			return null;
		}
		final WorldObject[] objs = new WorldObject[4];
		for (int i = 0; i < 4; i++) {
			final short hash = (short) (x | y << 6 | i << 12 | plane << 14);
			objs[i] = objects.get(hash);
		}
		return objs;
	}

    public WorldObject getSpawnedObject(final Location tile, final Predicate<WorldObject> predicate) {
        val chunk = this.getChunk(tile.getX() & 0x3F, tile.getY() & 0x3F, tile.getPlane());
        val spawnedObjects = chunk.getSpawnedObjects();
        if (spawnedObjects == null) {
            return null;
        }
        for (int i = 0; i < 4; i++) {
            final short hash = (short) (tile.getXInRegion() | tile.getYInRegion() << 6 | i << 12 | tile.getPlane() << 14);
            final WorldObject obj = spawnedObjects.get(hash);
            if (obj != null && predicate.test(obj)) {
                return obj;
            }
        }
        return null;
    }

    public WorldObject getRemovedObject(final Location tile, final Predicate<WorldObject> predicate) {
        val chunk = this.getChunk(tile.getX() & 0x3F, tile.getY() & 0x3F, tile.getPlane());
        val removedObjects = chunk.getOriginalObjects();
        if (removedObjects == null) {
            return null;
        }
        for (int i = 0; i < 4; i++) {
            final short hash = (short) (tile.getXInRegion() | tile.getYInRegion() << 6 | i << 12 | tile.getPlane() << 14);
            final WorldObject obj = removedObjects.get(hash);
            if (obj != null && predicate.test(obj)) {
                return obj;
            }
        }
        return null;
    }

	public WorldObject getSpawnedObject(final Location tile) {
        val chunk = this.getChunk(tile.getX() & 0x3F, tile.getY() & 0x3F, tile.getPlane());
        val spawnedObjects = chunk.getSpawnedObjects();
		if (spawnedObjects == null) {
			return null;
		}
		for (int i = 0; i < 4; i++) {
			final short hash = (short) (tile.getXInRegion() | tile.getYInRegion() << 6 | i << 12 | tile.getPlane() << 14);
			final WorldObject obj = spawnedObjects.get(hash);
			if (obj != null) {
				return obj;
			}
		}
		return null;
	}

}