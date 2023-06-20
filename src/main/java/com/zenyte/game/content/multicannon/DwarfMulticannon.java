package com.zenyte.game.content.multicannon;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Position;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.pathfinding.events.player.TileEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.TileStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.area.plugins.CannonRestrictionPlugin;
import com.zenyte.plugins.PluginManager;
import com.zenyte.plugins.events.CannonRemoveEvent;
import com.zenyte.plugins.events.InitializationEvent;
import com.zenyte.plugins.events.ServerShutdownEvent;
import com.zenyte.processor.Listener;
import com.zenyte.processor.Listener.ListenerType;
import com.zenyte.utils.ProjectileUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Handles the Dwarf multicannon settings for the player. Contains all the world's cannons. Sound effect: 2877
 * 
 * @author Kris | 13. okt 2017 : 13:03.11
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
@Slf4j
public final class DwarfMulticannon {

	private static final Animation ANIM = new Animation(827);
	private static final Item BASE = new Item(6), STAND = new Item(8), BARRELS = new Item(10), FURNACE = new Item(12);
	private static final Item[] CANNON_PARTS = new Item[] { BASE, STAND, BARRELS, FURNACE };

    private static final SoundEffect CANNON_SETUP = new SoundEffect(2876, 10);
    private static final SoundEffect CANNON_PICKUP = new SoundEffect(2581);

	public DwarfMulticannon(final Player player) {
		this.player = player;
	}

	private transient Player player;
	@Getter
	private transient Multicannon cannon;
	@Getter
	@Setter
	private byte cannonballs;
	@Getter
	@Setter
	private byte graniteballs;
	@Setter
	private long setupTime;
	@Getter
	private byte setupStage;
	public static final Object2ObjectMap<String, Multicannon> placedCannons = new Object2ObjectOpenHashMap<>();

	@Listener(type = ListenerType.LOGIN)
	private static void onLogin(final Player player) {
		val multicannon = player.getDwarfMulticannon();
		if (multicannon.setupTime == 0) {
			return;
		}
		val existingCannon = placedCannons.get(player.getUsername());
		if (existingCannon != null) {
			multicannon.cannon = existingCannon;
			multicannon.cannon.setPlayer(new WeakReference<>(player));
			multicannon.cannon.setCannon(multicannon);
		}
		if (multicannon.setupTime < (Utils.currentTimeMillis() - TimeUnit.TICKS.toMillis(3000))) {
			player.sendMessage("<col=ff0000>Your dwarf multicannon has decayed! Speak with Nulodion to retrieve it.");
		}
	}

	public boolean isDecayed() {
		return setupTime != 0 && setupTime < (Utils.currentTimeMillis() - TimeUnit.TICKS.toMillis(3000));
	}

	@Subscribe
    public static final void onInit(final InitializationEvent event) {
	    val player = event.getPlayer();
	    val savedPlayer = event.getSavedPlayer();
        val cannon = player.getDwarfMulticannon();
        cannon.player = player;
        if (savedPlayer == null) {
            return;
        }
        val savedCannon = savedPlayer.getDwarfMulticannon();
        cannon.cannonballs = savedCannon.cannonballs;
        cannon.graniteballs = savedCannon.graniteballs;
        cannon.setupTime = savedCannon.setupTime;
        cannon.setupStage = savedCannon.setupStage;
    }

	@Subscribe
    public static final void onServerShutdown(final ServerShutdownEvent event) {
        for (val cannon : placedCannons.values()) {
        	val player = cannon.getPlayer().get();
        	if (player == null) {
        		continue;
			}
			player.getDwarfMulticannon().setupTime = 1;// Force the cannon to be picked up at Nulodion.
        }
    }

	/**
	 * Goes through numerous checks, if all come in true, plants the cannon under the player's location.
	 */
	public void setupCannon() {
		if (isDecayed()) {
			player.sendMessage("A cannon of yours has decayed. Speak to Nulodion to retrieve it.");
			return;
		}
		if (cannon != null) {
			player.sendMessage("You already have a dwarf multicannon setup.");
			return;
		}
		if (!player.getInventory().containsItems(CANNON_PARTS)) {
			player.sendMessage("You need a cannon base, stand, barrels and a furnace to setup a dwarf multicannon.");
			return;
		}

        if (!canSetupCannon(player)) {
            return;
        }

		val cannonTile = new Location(player.getX() - 1, player.getY() - 1, player.getPlane());
		val center = new Location(player.getLocation());

		block : if (World.getObjectWithType(cannonTile, 10) != null || !World.isFloorFree(cannonTile, 2)
				|| containsCannon(center.getX(), center.getY()) || isProjectileClipped(cannonTile)) {
		    val cannonX = cannonTile.getX();
		    val cannonY = cannonTile.getY();
		    val z = cannonTile.getPlane();
		    val centerX = center.getX();
		    val centerY = center.getY();
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    cannonTile.setLocation(cannonX + x, cannonY + y, z);
                    center.setLocation(centerX + x, centerY + y, z);
                    if (World.getObjectWithType(cannonTile, 10) == null && World.isFloorFree(cannonTile, 2)
                            && !containsCannon(center.getX(), center.getY()) && !isProjectileClipped(cannonTile)) {

                        break block;
                    }
                }
            }
			player.sendMessage("You can't place a cannon here.");
			return;
		}

        player.lock(10);
		val event = new TileEvent(player, new TileStrategy(center, 2), () -> {
            player.unlock();
            WorldTasksManager.schedule(new TickTask() {
                private int stage;
                private long lastSetup;

                @Override
                public void run() {
                    if (player.isFinished()) {
                        stop();
                        return;
                    }
                    ticks++;
                    val distance = player.getLocation().getDistance(center);
                    if (distance > 3) {
                        if (distance > 6 || player.isProjectileClipped(center, true)) {
                            stop();
                        } else {
                            player.resetWalkSteps();
                            player.setRouteEvent(new TileEvent(player, new TileStrategy(center, 2), null));
                        }
                        return;
                    }
                    if (Utils.currentTimeMillis() - TimeUnit.TICKS.toMillis(2) < lastSetup) {
                        return;
                    }
                    lastSetup = Utils.currentTimeMillis();
                    if (!player.getInventory().containsItem(CANNON_PARTS[stage])) {
                        stop();
                        return;
                    }
                    if (stage > 0 && cannon == null) {
                        stop();
                        return;
                    }
                    Multicannon cannon = new Multicannon(7, 10, 0, cannonTile, player);
                    if (stage == 0) {
                        if (World.getObjectWithType(cannonTile, 10) != null || !World.isFloorFree(cannonTile, 2)
                                || containsCannon(center.getX(), center.getY())) {
                            stop();
                            return;
                        }
                    }
                    player.faceObject(cannon);
                    player.setAnimation(ANIM);
                    World.sendSoundEffect(center, CANNON_SETUP);
                    player.getInventory().deleteItem(CANNON_PARTS[stage]);
                    switch (setupStage = (byte) stage++) {
						case 0:
							DwarfMulticannon.this.cannon = cannon;
							setSetupTime(Utils.currentTimeMillis());
							World.spawnObject(cannon);
							placedCannons.put(player.getUsername(), cannon);
							player.sendFilteredMessage("You place the cannon base on the ground.");
							return;
						case 1:
							World.spawnObject(cannon = new Multicannon(8, 10, 0, cannonTile, player));
							placedCannons.put(player.getUsername(), DwarfMulticannon.this.cannon = cannon);
							player.sendFilteredMessage("You add the stand.");
							return;
						case 2:
							World.spawnObject(cannon = new Multicannon(9, 10, 0, cannonTile, player));
							placedCannons.put(player.getUsername(), DwarfMulticannon.this.cannon = cannon);
							player.sendFilteredMessage("You add the barrels.");
							return;
						case 3:
							World.spawnObject(cannon = new Multicannon(6, 10, 0, cannonTile, player));
							placedCannons.put(player.getUsername(), DwarfMulticannon.this.cannon = cannon);
							player.sendFilteredMessage("You add the furnace.");
							loadCannon();
							stop();
					}
                }
            }, 0, 1);
        });
		event.setOnFailure(() -> player.unlock());
		player.setRouteEvent(event);
	}

	private boolean isProjectileClipped(final Location cannonTile) {
        return ProjectileUtils.isProjectileClipped(null, null, cannonTile, new Location(cannonTile.getX() + 2, cannonTile.getY(), cannonTile.getPlane()), true)
                || ProjectileUtils.isProjectileClipped(null, null, cannonTile, new Location(cannonTile.getX(), cannonTile.getY() + 2, cannonTile.getPlane()), true)
                || ProjectileUtils.isProjectileClipped(null, null, cannonTile, new Location(cannonTile.getX() + 2, cannonTile.getY() + 2, cannonTile.getPlane()), true)
                || ProjectileUtils.isProjectileClipped(null, null, cannonTile, new Location(cannonTile.getX() + 1, cannonTile.getY() + 2, cannonTile.getPlane()), true)
                || ProjectileUtils.isProjectileClipped(null, null, cannonTile, new Location(cannonTile.getX() + 2, cannonTile.getY() + 1, cannonTile.getPlane()), true);
    }

	/**
	 * Checks whether there's a cannon at the specified location or not.
	 */
	private boolean containsCannon(final int x, final int y) {
		for (final Multicannon cannon : placedCannons.values()) {
			if (Utils.collides(x, y, 2, cannon.getX() + 1, cannon.getY() + 1, 2)) {
				return true;
			}
		}
		return false;
	}

    /**
     * @param tile the tile to which the cannon is being placed.
     * @return whether or not the cannon can be placed there.
     */
	public static final boolean canSetupCannon(final Position tile) {
	    if (tile instanceof Player) {
	        val area = ((Player) tile).getArea();
	        if (area instanceof CannonRestrictionPlugin) {
	            if (!((CannonRestrictionPlugin) area).canPlaceCannon(tile)) {
                    ((Player) tile).sendMessage(((CannonRestrictionPlugin) area).restrictionMessage());
                    return false;
                }
            }
        } else {
	        val area = GlobalAreaManager.getArea(tile);
            if (area instanceof CannonRestrictionPlugin) {
                return ((CannonRestrictionPlugin) area).canPlaceCannon(tile);
            }
        }
		return true;
	}

	/**
	 * Handles the object options for the multicannon.
	 * 
	 * @param object
	 *            to handle
	 * @param option
	 *            which option was clicked.
	 * @return whether the operation was with a cannon.
	 */
	public boolean handleCannon(final WorldObject object, final int option) {
		if (!object.equals(placedCannons.get(player.getUsername()))) {
			return false;
		}
		final String opt = object.getDefinitions().getOption(option);
        switch (opt) {
            case "Fire":
                if (cannon.isFiring() || cannonballs == 0 && graniteballs == 0) {
                    if (!player.getInventory().containsItem(2, 1) && !player.getInventory().containsItem(21728, 1)) {
                        player.sendMessage("You need some cannonballs to load the cannon.");
                        return true;
                    }
                    loadCannon();
                    return true;
                } else {
                    player.sendMessage("You fire the dwarf multicannon.");
                    cannon.setFiring(true);
                }
                break;
            case "Pick-up":
                take(object.getId(), true);
                break;
            case "Repair":
				cannon.setDecayTimer(3000);
				cannon.setId(6);
                World.spawnObject(cannon);
                player.sendMessage("You repair the cannon.");
                setSetupTime(Utils.currentTimeMillis());
                break;
            case "Empty":
                int spaceRequired = 0;
                if (cannonballs > 0) {
                    if (!player.getInventory().containsItem(2, 1)) {
                        spaceRequired++;
                    }
                }
                if (graniteballs > 0) {
                    if (!player.getInventory().containsItem(21728, 1)) {
                        spaceRequired++;
                    }
                }
                if (player.getInventory().getFreeSlots() < spaceRequired) {
                    player.sendMessage("You need at least " + spaceRequired + " free inventory slots to pick the cannon up.");
                    return true;
                }
                val granite = graniteballs > 0;
                if (cannonballs > 0) {
                    player.getInventory().addItem(2, cannonballs);
                    cannonballs = 0;
                }
                if (graniteballs > 0) {
                    player.getInventory().addItem(21728, graniteballs);
                    graniteballs = 0;
                }
                cannon.setFiring(false);
                player.sendMessage("You unload your cannon and receive " + (granite ? "Granite cannonball" :
                        "Cannonball") + ".");
                break;
        }
		return true;
	}
	
	public void take(final int id, final boolean message) {
		int spaceRequired = 4;
		if (cannonballs > 0) {
			if (!player.getInventory().containsItem(2, 1)) {
				spaceRequired++;
			}
		}
		if (graniteballs > 0) {
			if (!player.getInventory().containsItem(21728, 1)) {
				spaceRequired++;
			}
		}
		if (player.getInventory().getFreeSlots() < spaceRequired) {
			player.sendMessage("You need at least " + spaceRequired + " free inventory slots to pick the cannon up.");
			return;
		}
		if (message) {
			player.sendMessage("You pick up the cannon. It's really heavy.");
		}
		placedCannons.remove(player.getUsername());
		setSetupTime(0);
		if (cannonballs > 0) {
			player.getInventory().addItem(2, cannonballs);
			cannonballs = 0;
		}
		if (graniteballs > 0) {
			player.getInventory().addItem(21728, graniteballs);
			graniteballs = 0;
		}
		player.getPacketDispatcher().sendSoundEffect(CANNON_PICKUP);
		if (cannon != null) {
			PluginManager.post(new CannonRemoveEvent(cannon));
			World.removeObject(cannon);
			cannon = null;
		}
		if (id == 7 || id == 8 || id == 9 || id == 6 || id == 14916) {
			player.getInventory().addItem(BASE);
		}
		if (id == 8 || id == 9 || id == 6 || id == 14916) {
			player.getInventory().addItem(STAND);
		}
		if (id == 9 || id == 6 || id == 14916) {
			player.getInventory().addItem(BARRELS);
		}
		if (id == 6 || id == 14916) {
			player.getInventory().addItem(FURNACE);
		}
	}

	/**
	 * Attempts to load the cannon with cannonballs.
	 */
	public void loadCannon() {
		if ((cannonballs + graniteballs) >= getMaxCannonballsForDonatorLevel(player)) {
			return;
		}
		val freeSpace = getMaxCannonballsForDonatorLevel(player) - (cannonballs + graniteballs);
		val balls = player.getInventory().getAmountOf(2);
		val amount = Math.min(freeSpace, balls);
		if (amount > 0) {
			player.getInventory().deleteItem(2, amount);
			cannonballs += amount;
			player.sendMessage("You load the cannon with " + amount + " cannonball" + (amount == 1 ? "." : "s."));
		}
		val granite = player.getInventory().getAmountOf(21728);
		val remainingSpace = getMaxCannonballsForDonatorLevel(player) - (cannonballs + graniteballs);
		val toAdd = Math.min(remainingSpace, granite);
		if (toAdd > 0) {
			player.getInventory().deleteItem(21728, toAdd);
			graniteballs += toAdd;
			player.sendMessage("You load the cannon with " + toAdd + " granite cannonball" + (toAdd == 1 ? "." : "s."));
		}
	}

	private int getMaxCannonballsForDonatorLevel(Player p) {
		//30,
		switch(p.getMemberRank()) {
			case SAPPHIRE_MEMBER:
				return 40;
			case EMERALD_MEMBER:
				return 50;
			case RUBY_MEMBER:
				return 60;
			case DIAMOND_MEMBER:
				return 70;
			case DRAGONSTONE_MEMBER:
				return 80;
			case ONYX_MEMBER:
				return 90;
			case ZENYTE_MEMBER:
				return 100;
			default:
			case NONE:
				return 30;
		}
	}

	/**
	 * Initiates a never-ending process of processing all the cannons.
	 */
	public static final void init() {
	    val removedCannons = new ArrayList<Multicannon>();
		WorldTasksManager.schedule(() -> {
			placedCannons.values().forEach(cannon -> {
				try {
					val exists = cannon.process();
					if (!exists) {
						removedCannons.add(cannon);
						//Lovely code btw.
						val player = cannon.getPlayer().get();
						if (player != null) {
							player.getDwarfMulticannon().cannon = null;
						}
					}
				} catch (Exception e) {
					log.error(Strings.EMPTY, e);
				}
			});
			if (!removedCannons.isEmpty()) {
                placedCannons.values().removeAll(removedCannons);
                removedCannons.forEach(cannon -> PluginManager.post(new CannonRemoveEvent(cannon)));
                removedCannons.clear();
            }
		}, 0, 0);
	}

}
