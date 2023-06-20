package com.zenyte.game.world.flooritem;

import com.google.gson.GsonBuilder;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.item.Item;
import com.zenyte.game.packet.out.ObjAdd;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.region.Chunk;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Kris | 30. mai 2018 : 01:44:39
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public final class GlobalItem extends FloorItem {
	
	private static final Int2ObjectOpenHashMap<Set<GlobalItem>> GLOBAL_ITEMS = new Int2ObjectOpenHashMap<Set<GlobalItem>>();
	private static final Queue<GlobalItem> PENDING_GLOBAL_ITEMS = new ConcurrentLinkedQueue<>();
	
	public GlobalItem(final Item item, final Location location, final int respawnTime) {
		super(item, location, null, null, -1, -1);
		this.respawnTime = respawnTime;
		originalAmount = item.getAmount();
	}
	
	public static void createPersistentGlobalItemSpawn(final GlobalItem item) {
		val regionId = item.getLocation().getRegionId();
		if (!GLOBAL_ITEMS.containsKey(regionId)) {
			GLOBAL_ITEMS.put(regionId, new LinkedHashSet<GlobalItem>());
		}
		val set = GLOBAL_ITEMS.get(regionId);
		set.add(item);
	}
	
	@Getter
	@Setter
	private int respawnTime, originalAmount;
	private transient int ticks;
	
	public void schedule() {
		ticks = respawnTime;
		setAmount(originalAmount);
		PENDING_GLOBAL_ITEMS.add(this);
	}

	public void spawn() {
		val regionId = location.getRegionId();
		val region = World.getRegion(regionId);
		if (region == null) {
			return;
		}
        val chunkId = Chunk.getChunkHash(location.getX() >> 3, location.getY() >> 3, location.getPlane());
        val chunk = World.getChunk(chunkId);
        chunk.addFloorItem(this);
		for (val player : World.getPlayers()) {
			if (player == null || !player.getMapRegionsIds().contains(regionId)) {
				continue;
			}
			player.sendZoneUpdate(location.getX(), location.getY(), new ObjAdd(this));
		}
	}
	
	public static final Set<GlobalItem> getGlobalItems(final int regionId) {
		return GLOBAL_ITEMS.get(regionId);
	}
	
	public static final void load() {
		WorldTasksManager.schedule(() -> {
			try {
				if (PENDING_GLOBAL_ITEMS.isEmpty()) {
					return;
				}
				val iterator = PENDING_GLOBAL_ITEMS.iterator();
				while (iterator.hasNext()) {
					try {
						val item = iterator.next();
						if (--item.ticks == 0) {
							item.spawn();
							iterator.remove();
						}
					} catch (Exception e) {
						log.error(Strings.EMPTY, e);
					}
				}
			} catch (Exception e) {
				log.error(Strings.EMPTY, e);
			}
		}, 0, 0);

		CoresManager.getServiceProvider().executeNow(() -> {
			try {
				val br = new BufferedReader(new FileReader("data/items/globalItems.json"));
				val definitions = World.getGson().fromJson(br, SkeletonGlobalItem[].class);
				for (val def : definitions) {
					val tile = new Location(def.x, def.y, def.z);
					val item = new GlobalItem(new Item(def.id, def.amount), tile, def.respawnTime);
					createPersistentGlobalItemSpawn(item);
				}
			} catch (final Exception e) {
                log.error(Strings.EMPTY, e);
			}
		});
	}
	
	public static final void parse() {
		String line = "";
		int i = 0;

		val globalItems = new ArrayList<SkeletonGlobalItem>();
		val gson = new GsonBuilder().setPrettyPrinting().create();

		try (val reader = new BufferedReader(new FileReader(new File("Ground item drops.txt")))) {
			while ((line = reader.readLine()) != null) {
				i++;
				if (!line.startsWith("item name:") && !line.startsWith("Item name:")) {
					continue;
				}

				final int index = line.indexOf("item id:");
				line = line.substring(index + 8);
				line = line.trim();

				val split = line.split(" ");
				val id = Integer.valueOf(split[0].substring(split[0].indexOf("(") + 1, split[0].length() - 1));

				val x = Integer.valueOf(split[3].substring(0, split[3].length() - 1));
				val y = Integer.valueOf(split[4].substring(0, split[4].length() - 1));
				val z = Integer.valueOf(split[5]);
				int amount = 1;
				if (line.contains("amount: ")) {
					val startIndex = line.indexOf("amount: ");
					val restOfTheLine = line.substring(startIndex);
					val endIndex = restOfTheLine.indexOf(")") + startIndex;
					amount = Integer.valueOf(line.substring(startIndex + 8, endIndex));
				}

				int respawnTime = 30;
				if (line.contains("respawn time: ")) {
					val startIndex = line.indexOf("respawn time: ");
					val restOfTheLine = line.substring(startIndex);
					val endIndex = restOfTheLine.indexOf(" second") + startIndex;
					respawnTime = Integer.valueOf(line.substring(startIndex + 14, endIndex));
				}
				globalItems.add(new SkeletonGlobalItem(id, amount, x, y, z, respawnTime));

			}
		} catch (final Exception e) {
			e.printStackTrace();
			System.out.println(line + " --------- " + i);
		}
		val toJson = gson.toJson(globalItems);
		try {
			final PrintWriter pw = new PrintWriter("globalItems.json", "UTF-8");
			pw.println(toJson);
			pw.close();
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}

	@AllArgsConstructor
	@NoArgsConstructor
	public static class SkeletonGlobalItem {

		@Getter private int id, amount, x, y, z, respawnTime;

	}

}
