package com.zenyte.game.world.entity;

import com.google.gson.annotations.Expose;
import com.zenyte.Game;
import com.zenyte.game.content.rottenpotato.PotatoToggles;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Position;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.pathfinding.events.RouteEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.CombatType;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.game.world.region.DynamicRegion;
import com.zenyte.utils.IntLinkedList;
import com.zenyte.utils.ProjectileUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Slf4j
public abstract class Entity implements Position, CharacterLoop {

	public static final int[] MAP_SIZES = { 104, 120, 136, 168, 72 };

	public abstract int getSize();

	public abstract int getClientIndex();

	public abstract int getMaxHitpoints();

	public abstract boolean isDead();

	public abstract void handleIngoingHit(Hit hit);

    public abstract void postProcessHit(Hit hit);

    public abstract void sendDeath();

	public abstract void autoRetaliate(final Entity source);

	public abstract boolean canAttack(final Player source);

	public abstract boolean startAttacking(final Player source, final CombatType type);

	public abstract int getCombatLevel();

	public abstract EntityType getEntityType();

	public abstract void cancelCombat();

	public abstract void processMovement();

	/**
	 * Gets the current middle position of the NPC. NOTE: It reuses the existing tile object and sets its value to the current middle tile,
	 * therefore modifications to this object aren't suggested, but instead if you wish to obtain a tile you could modify - such as set its
	 * X location to something else, construct a new tile object using this.
	 * 
	 * @return the middle tile of the NPC.
	 */
	public abstract Location getMiddleLocation();

	public abstract void handleOutgoingHit(final Entity target, final Hit hit);

	public abstract void performDefenceAnimation(Entity attacker);

	public abstract List<Entity> getPossibleTargets(final EntityType type);

    public abstract int drainSkill(final int skill, final double percentage, final int minimumDrain);

	public abstract int drainSkill(final int skill, final double percentage);

	public abstract int drainSkill(final int skill, final int amount);

	public abstract boolean isRunning();

	public abstract boolean isMaximumTolerance();

	protected abstract boolean isAcceptableTarget(final Entity entity);

	protected abstract boolean isPotentialTarget(final Entity entity);

	/**
	 * Sets a temporary delay during which the entity will not be added to the list of possible targets.
	 */
	@Getter
	@Setter
	private transient long findTargetDelay;
	@Expose
	@Getter
	protected Toxins toxins = new Toxins(this);
	@Getter
	private transient long freezeDelay, freezeImmunity;
	@Getter
	private transient long lockDelay;
	@Expose
	@Getter
	protected Location location;

	protected transient HitEntryList scheduledHits = new HitEntryList();

	@Getter protected transient RouteEvent<?, ?> routeEvent;

	public void forceLocation(final Location location) {
		this.location = new Location(location);
	}

	public void setRouteEvent(final RouteEvent<?, ?> event) {
	    if (event == null) {
			if (routeEvent != null) {
				val failure = routeEvent.getOnFailure();
				if (failure != null) {
					failure.run();
				}
			}
		}
		routeEvent = event;
	}

	/**
	 * Determines whether this entity will trigger opponent auto retaliate if target is a player.
	 */
	public boolean triggersAutoRetaliate() {
		return true;
	}

	/**
	 * A delay that's referenced in {@link CombatUtilities#processHit} - if this value is above the current time in milliseconds, the hit will
	 * not be processed - it's used to prevent entities from being hit when insta-leaving areas like raids.
	 */
	@Getter
	@Setter
	protected transient long protectionDelay;

	protected transient Location middleTile;

    @Getter @Setter private transient boolean forceAttackable;

	public boolean canAttackInSingleZone(final Entity target) {
	    if (target.isForceAttackable()) {
	        return true;
        }
	    val attacking = getAttackedBy();
        return attacking == null || attacking == target || getAttackedByDelay() <= Utils.currentTimeMillis()
                || attacking.isDead() || attacking.isFinished();
    }

    public boolean isMovementRestricted() {
	    return isFrozen() || isStunned();
    }

	@Expose
	@Getter
	protected transient int hitpoints;

	public boolean setHitpoints(final int amount) {
		val dead = isDead();
		this.hitpoints = amount;
		if (!dead && hitpoints <= 0) {
			sendDeath();
			return true;
		}
		return false;
	}

	public double getHitpointsAsPercentage() {
		val current = (double) getHitpoints();
		val max = (double) getMaxHitpoints();

		try {
			return (current / max) * 100;
		} catch (ArithmeticException e) {
			return 0.0;
		}
	}

	@Getter
	@Setter
	private transient int mapSize;
	@Getter
	@Setter
	private transient int index;
	@Getter
	@Setter
	private transient int lastRegionId, lastChunkId;
	@Getter
	@Setter
	protected transient int direction;
	@Getter
	protected transient int lastMovementType;

	@Getter
	@Setter
	private transient boolean multiArea;
	@Getter
	private transient boolean forceMultiArea;
	@Getter
	@Setter
	protected transient boolean teleported;
	@Getter
	@Setter
	private transient boolean finished;
	@Getter
	@Setter
	private transient boolean isAtDynamicRegion;
	@Getter
	@Setter
	private transient Location lastLoadedMapRegionTile;
	@Getter
	@Setter
	protected transient Location lastLocation;
	@Getter
	protected transient Location nextLocation;

	@Getter
	@Setter
	private transient Entity attacking;
	@Getter
	@Setter
	private transient Entity attackedBy;
	@Getter
	@Setter
	private transient long attackedByDelay;
	@Getter
	@Setter
	private transient long lastReceivedHit;
	@Getter
	@Setter
	protected transient long lastAnimation;
	@Getter
	@Setter
	private transient long attackingDelay;
	@Getter
	private transient Location faceLocation;
	@Getter
	protected transient IntList mapRegionsIds = new IntArrayList();
	@Getter
	private transient IntList lastMapRegionsIds = new IntArrayList();
	@Getter
	protected transient IntLinkedList walkSteps = new IntLinkedList();
	@Getter
	protected transient List<Hit> receivedHits = new ArrayList<Hit>();
	@Getter
	protected transient List<Hit> nextHits = new ArrayList<Hit>();
	@Getter
	protected transient Object2ObjectMap<String, List<IntLongPair>> receivedDamage = new Object2ObjectOpenHashMap<>();
	@Getter
	protected transient Map<Object, Object> temporaryAttributes = new HashMap<Object, Object>();
	@Getter
	protected transient UpdateFlags updateFlags = new UpdateFlags(this);
	@Getter
	protected transient List<HitBar> hitBars = new ArrayList<HitBar>();
	@Getter
	protected transient int faceEntity = -1;
	@Getter
	protected transient Animation animation;
	@Getter
	private transient Graphics graphics;
	@Getter
	private transient ForceMovement forceMovement;
	@Getter
	private transient ForceTalk forceTalk;

	@Getter
	@Setter
	protected transient int walkDirection;
	@Getter
	@Setter
	protected transient int runDirection;
	@Getter
	@Setter
	private transient int sceneBaseChunkId;
	@Expose
	@Getter
	protected boolean run;
	@Expose
	@Getter
	protected transient boolean silentRun;
	protected transient HitBar hitBar = new EntityHitBar(this);
	@Getter
	@Setter
	private transient boolean cantInteract;

	public abstract void unlink();

	public boolean isFacing(final Entity target) {
		return this.faceEntity == target.getClientIndex();
	}

	/**
	 * A list of possible entities. It's better to keep one list and clear it on request, rather than re-create a list every time this is
	 * called.
	 */
	protected transient List<Entity> possibleTargets = new ArrayList<>();

	public abstract boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check);

	private transient int[] lastSteps = new int[2];

	private transient long[] immunities = new long[HitType.values.length];

    /**
     * Adds immunity on the character from all damage inflicted by the type specified in parameters.
     * @param type the hit type.
     * @param milliseconds the duration of the effect in milliseconds.
     */
	public void addImmunity(final HitType type, final long milliseconds) {
	    immunities[type.ordinal()] = System.currentTimeMillis() + milliseconds;
    }

    public boolean isImmune(final HitType type) {
	    return immunities[type.ordinal()] > System.currentTimeMillis();
    }

	public final int[] getLastWalkTile() {
		if (walkSteps.size() == 0) {
			lastSteps[0] = getX();
			lastSteps[1] = getY();
			return lastSteps;
		}
		final int hash = walkSteps.getLast();
		lastSteps[0] = WalkStep.getNextX(hash);
		lastSteps[1] = WalkStep.getNextY(hash);
		return lastSteps;
	}

	/**
	 * Gets the next position of the entity based on its current walksteps list and run mode.
	 * 
	 * @return next walkstep position, or current position if none is present.
	 */
	public Location getNextPosition(final int amount) {
		val size = Math.min(walkSteps.size(), amount);
		if (size == 0) {
			return location;
		}
		val nextTileHash = walkSteps.nthPeek(size - 1);
		val x = WalkStep.getNextX(nextTileHash);
		val y = WalkStep.getNextY(nextTileHash);
		return new Location(x, y, location.getPlane());
	}

	/**
	 * Blocks all incoming hits that were scheduled prior to the method call, as well as one tick afterwards for extra protection.
	 */
	public void blockIncomingHits() {
		protectionDelay = Utils.currentTimeMillis() + 600;
	}

	public abstract void unclip();

	public abstract void clip();

    protected boolean collides(final List<? extends Entity> list, final int x, final int y) {
        if (list.isEmpty())
            return false;
        for (int i = list.size() - 1; i >= 0; i--) {
            val entity = list.get(i);
            if (entity == this || entity.isFinished() || (entity instanceof NPC) && !((NPC) entity).isEntityClipped()) {
                continue;
            }
            if (Utils.collides(entity.getX(), entity.getY(), entity.getSize(), x, y, 1)) {
                return true;
            }
        }
        return false;
    }

	/**
	 * Whether to check if this npc is projectile clipped or not. Used for entities that are ontop of clipped tiles (such as objects),
	 * allows players to actually attack those.
	 */
	public boolean checkProjectileClip(final Player player) {
		return true;
	}

	protected int getNextWalkStep() {
		if (walkSteps.isEmpty()) {
			return 0;
		}
		return walkSteps.remove();
	}

	private transient long stunDelay;

	public void stun(final int ticks) {
		if (isStunned()) {
			return;
		}
		stunDelay = Game.getCurrentCycle() + ticks;
		resetWalkSteps();
		setRouteEvent(null);
	}

	public void removeStun() {
		stunDelay = 0;
	}

	public boolean isStunned() {
		return stunDelay > Game.getCurrentCycle();
	}

	public boolean isFreezeImmune() {
		return freezeImmunity > Game.getCurrentCycle();
	}

	protected boolean isFreezeable() {
	    return true;
    }

	public boolean isFrozen() {
		return freezeDelay > Game.getCurrentCycle();
	}

	public void resetFreeze() {
		freezeDelay = 0;
		freezeImmunity = 0;
	}

	public void forceFreezeDelay(final int ticks) {
		this.freezeDelay = Game.getCurrentCycle() + ticks;
	}

	public boolean freeze(final int freezeTicks) {
		return freeze(freezeTicks, 0);
	}

	public boolean freezeWithNotification(final int freezeTicks) {
		return freeze(freezeTicks, 0, entity -> {
			if (entity instanceof Player) {
				((Player) entity).sendMessage("<col=ef1020>You have been frozen!</col>");
			}
		});
	}

	public boolean freeze(final int freezeTicks, final int immunityTicks) {
		return freeze(freezeTicks, immunityTicks, null);
	}

    public boolean freezeWithNotification(final int freezeTicks, final int immunityTicks) {
		return freeze(freezeTicks, immunityTicks, entity -> {
			if (entity instanceof Player) {
				((Player) entity).sendMessage("<col=ef1020>You have been frozen!</col>");
			}
		});
	}

	public void addFreezeImmunity(final int immunityTicks) {
		freezeImmunity = Game.getCurrentCycle() + immunityTicks;
	}

    public boolean freeze(final int freezeTicks, final int immunityTicks, @Nullable final Consumer<Entity> onFreezeConsumer) {
		if (!isFreezeable() || isFreezeImmune()) {
			return false;
		}
		val currentCycle = Game.getCurrentCycle();
		freezeImmunity = currentCycle + freezeTicks + immunityTicks;
		freezeDelay = currentCycle + freezeTicks;
		resetWalkSteps();
		setRouteEvent(null);
		if (onFreezeConsumer != null) {
			onFreezeConsumer.accept(this);
		}
		return true;
	}

	public boolean isNulled() {
	    return false;
    }

	public boolean addWalkSteps(final int destX, final int destY) {
		return addWalkSteps(destX, destY, -1);
	}

	public boolean addWalkSteps(final int destX, final int destY, final int maxStepsCount) {
		return addWalkSteps(destX, destY, -1, true);
	}

	public abstract double getMagicPrayerMultiplier();

	public abstract double getRangedPrayerMultiplier();

	public abstract double getMeleePrayerMultiplier();

	public void heal(final int amount) {
		setHitpoints(Math.min((hitpoints + amount), (getMaxHitpoints())));
	}

	public void applyHit(final Hit hit) {
		applyHit(hit, false);
	}

	public void applyHit(final Hit hit, boolean vengeance) {
		if (isDead() && hit.getHitType() != HitType.HEALED || isFinished()) {
			return;
		}
		handleIngoingHit(hit);
		receivedHits.add(hit);
		addHitbar();
		val source = hit.getSource();
		if (source != null) {
			source.getTemporaryAttributes().put("vengeance_hit", vengeance);
		}
	}

	protected void addHitbar() {
		if (!getHitBars().contains(hitBar)) {
			getHitBars().add(hitBar);
		}
	}

	private transient BiPredicate<Boolean, Hit> hitPredicate = (locked, hit) -> {
	    if (locked && !hit.executeIfLocked()) {
	        return false;
        }
        val predicate = hit.getPredicate();
        if (predicate != null) {
            if (predicate.test(hit)) {
                return true;
            }
        }
        this.postProcessHit(hit);
        processHit(hit);
	    return true;
    };

	public void processReceivedHits() {
	    if (receivedHits.isEmpty())
	        return;
	    val locked = Boolean.valueOf(isLocked());
	    receivedHits.removeIf(hit -> hitPredicate.test(locked, hit));
	}

	public boolean isDying() {
	    return isDead();
    }

	public void checkMultiArea() {
		multiArea = forceMultiArea || World.isMultiArea(getLocation());
	}

	public void setLocation(final Location tile) {
		if (tile == null) {
			return;
		}
		nextLocation = new Location(tile);
	}

	protected void removeHitpoints(final Hit hit) {
		if (isDead()) {
			return;
		}
		int damage = hit.getDamage();
		if (damage > hitpoints) {
			damage = hitpoints;
		}
		addReceivedDamage(hit.getSource(), damage);
		setHitpoints(hitpoints - damage);
	}

	protected void processHit(final Hit hit) {
		if (hit.getScheduleTime() < protectionDelay) {
			return;
		}
		/*if (isDead()) {
			return;
		}*/
		if (hit.getDamage() > Short.MAX_VALUE) {
			hit.setDamage(Short.MAX_VALUE);
		}
		getUpdateFlags().flag(UpdateFlag.HIT);
		nextHits.add(hit);
		addHitbar();
		lastReceivedHit = Utils.currentTimeMillis();
		if (hit.getHitType() == HitType.HEALED) {
			heal(hit.getDamage());
		} else {
			removeHitpoints(hit);
		}
	}

	public void faceEntity(final Entity target) {
		setFaceLocation(new Location(target.getLocation().getCoordFaceX(target.getSize()),
				target.getLocation().getCoordFaceY(target.getSize()), target.getPlane()));
	}

	private static final long TWENTY_MINUTES_IN_MILLIS = TimeUnit.MINUTES.toMillis(20);

	public void addReceivedDamage(final Entity source, final int amount) {
		if (!(source instanceof Player)) {
			return;
		}
		val username = ((Player) source).getUsername();
		List<IntLongPair> list = receivedDamage.get(username);
		if (list == null) {
			receivedDamage.put(username, list = new ObjectArrayList<>());
		}
		if (amount >= 0) {
			list.add(new IntLongPair(System.currentTimeMillis() + TWENTY_MINUTES_IN_MILLIS, amount));
		}
	}

	public boolean addWalkSteps(final int destX, final int destY, final int maxStepsCount, final boolean check) {
		return WalkStep.addWalkSteps(this, destX, destY, maxStepsCount, check);
	}

    private final transient Predicate<HitEntry> hitEntryPredicate = hit -> {
        if (isDead()) {
            return true;
        }
        appendHitEntry(hit);
        val delay = hit.getAndDecrement();
        //Magic hits do not seem to execute the block animation
        if (delay <= 0 && hit.getHit().getHitType() != HitType.MAGIC) {
            performDefenceAnimation(hit.getHit().getSource());
        }
        if (delay < 0) {
            CombatUtilities.processHit(this, hit.getHit());
            return true;
        }
        return false;
    };

	public void appendHitEntry(final HitEntry hitEntry) {
        if (!hitEntry.isFreshEntry()) {
            return;
        }
        hitEntry.setFreshEntry(true);
    }

	public void scheduleHit(final Entity source, @NotNull final Hit hit, final int delay) {
		scheduledHits.add(new HitEntry(source, delay, hit));
	}

	protected final void iterateScheduledHits() {
		val each = scheduledHits.iterator();
		while (each.hasNext()) {
			if (hitEntryPredicate.test(each.next())) {
				each.remove();
			}
		}
    }

    public boolean ignoreUnderneathProjectileCheck() {
		return false;
	}

	public void processEntity() {
	    iterateScheduledHits();
		processReceivedHits();
		toxins.process();
		processMovement();
	}

	public void resetMasks() {
		if (updateFlags.isUpdateRequired()) {
			updateFlags.reset();
		}
		if (!hitBars.isEmpty()) {
			hitBars.clear();
		}
		if (!nextHits.isEmpty()) {
			nextHits.clear();
		}
		this.walkDirection = this.runDirection = -1;
	}

	public void reset() {
		setHitpoints(getMaxHitpoints());
		receivedHits.clear();
		walkSteps.clear();
		toxins.reset();
		receivedDamage.clear();
		hitBars.clear();
		nextHits.clear();
	}

	public void setForceMultiArea(final boolean forceMultiArea) {
		this.forceMultiArea = forceMultiArea;
		checkMultiArea();
	}

	protected final boolean needMapUpdate() {
		if (lastLoadedMapRegionTile == null) {
			return false;
		}

		return Math.abs(lastLoadedMapRegionTile.getChunkX() - location.getChunkX()) >= 5
                || Math.abs(lastLoadedMapRegionTile.getChunkY() - location.getChunkY()) >= 5;
	}

	public final void faceObject(final WorldObject object) {
		val objectDef = object.getDefinitions();
		val preciseX = object.getPreciseCoordFaceX(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation());
		val preciseY = object.getPreciseCoordFaceY(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation());
		if (preciseX == getX() && preciseY == getY()) {
			return;
		}
		faceLocation = new Location((int) preciseX, (int) preciseY, getPlane());
		direction = Utils.getFaceDirection(preciseX - getX(), preciseY - getY());
		getUpdateFlags().flag(UpdateFlag.FACE_COORDINATE);
	}

	public void faceDirection(final Direction direction) {
		final Location middle = getMiddleLocation();
		final int size = getSize() / 2;
		switch (direction) {
			case SOUTH:
				setFaceLocation(new Location(middle.getX() + size, middle.getY() - 15, middle.getPlane()));
				return;
			case EAST:
				setFaceLocation(new Location(middle.getX() + 15, middle.getY() + size, middle.getPlane()));
				return;
			case SOUTH_WEST:
				setFaceLocation(new Location(middle.getX() - 15, middle.getY() - 15, middle.getPlane()));
				break;
			case WEST:
				setFaceLocation(new Location(middle.getX() - 15, middle.getY() + size, middle.getPlane()));
				return;
			case NORTH:
				setFaceLocation(new Location(middle.getX() + size, middle.getY() + 15, middle.getPlane()));
				return;
			case NORTH_WEST:
				setFaceLocation(new Location(middle.getX() - 15, middle.getY() + 15, middle.getPlane()));
				return;
			case NORTH_EAST:
				setFaceLocation(new Location(middle.getX() + 15, middle.getY() + 15, middle.getPlane()));
				break;
			case SOUTH_EAST:
				setFaceLocation(new Location(middle.getX() + 15, middle.getY() - 15, middle.getPlane()));
				break;
		}
	}
	public void setFaceLocation(final Location tile) {
		faceLocation = tile;
		val middle = getMiddleLocation();
		direction = Utils.getFaceDirection(tile.getX() - middle.getX(), tile.getY() - middle.getY());
		getUpdateFlags().flag(UpdateFlag.FACE_COORDINATE);
	}

	public void loadMapRegions() {
		lastMapRegionsIds.clear();
		lastMapRegionsIds.addAll(mapRegionsIds);
		mapRegionsIds.clear();
		isAtDynamicRegion = false;
		final int sceneChunksRadio = MAP_SIZES[mapSize] / 16;
		final int chunkX = location.getChunkX();
		final int chunkY = location.getChunkY();
		final int mapHash = MAP_SIZES[mapSize] >> 4;
		final int minRegionX = (chunkX - mapHash) / 8;
		final int minRegionY = (chunkY - mapHash) / 8;
		final int sceneBaseChunkX = (chunkX - sceneChunksRadio);
		final int sceneBaseChunkY = (chunkY - sceneChunksRadio);
		for (int xCalc = minRegionX < 0 ? 0 : minRegionX; xCalc <= ((chunkX + mapHash) / 8); xCalc++) {
			for (int yCalc = minRegionY < 0 ? 0 : minRegionY; yCalc <= ((chunkY + mapHash) / 8); yCalc++) {
				final int regionId = yCalc + (xCalc << 8);
				if (World.getRegion(regionId, this instanceof Player) instanceof DynamicRegion) {
					isAtDynamicRegion = true;
				}
				mapRegionsIds.add(regionId);
			}
		}
		lastLoadedMapRegionTile = new Location(getX(), getY(), getPlane());
		sceneBaseChunkId = sceneBaseChunkX | (sceneBaseChunkY << 11);
	}

	public Location getFaceLocation(final Entity target) {
		return getFaceLocation(target, getSize());
	}

	/**
	 * Gets the coordinate of the NPC's head, used for large npcs.
	 * 
	 * @return head's location.
	 */
	public Location getFaceLocation(final Entity target, final int npcSize) {
		if (target == null) {
			return getLocation();
		}
		final Location middle = getMiddleLocation();
		final float size = npcSize >> 1;
		double degrees = Math.toDegrees(Math.atan2(target.getY() - middle.getY(), target.getX() - middle.getX()));
		if (degrees < 0) {
			degrees += 360;
		}
		final double angle = Math.toRadians(degrees);
		final int px = (int) Math.round(middle.getX() + size * Math.cos(angle));
		final int py = (int) Math.round(middle.getY() + size * Math.sin(angle));
		return new Location(px, py, middle.getPlane());
	}

    public int getRoundedDirection() {
	    return getRoundedDirection(0);
    }

    public int getRoundedDirection(final int offset) {
	    return getRoundedDirection(this.direction, offset);
    }

	public static final int getRoundedDirection(final int baseDirection, final int offset) {
		val direction = (baseDirection + offset) & 2047;
		if (direction < 128 || direction >= 1920) {
			return 6;
		} else if (direction < 384) {
			return 5;
		} else if (direction < 640) {
			return 3;
		} else if (direction < 896) {
			return 0;
		} else if (direction < 1152) {
			return 1;
		} else if (direction < 1408) {
			return 2;
		} else if (direction < 1664) {
			return 4;
		} else {
			return 7;
		}
	}

	public Location getFaceLocation(final Entity target, final int npcSize, final int offset) {
		if (target == null) {
			return getLocation();
		}
		val middle = getMiddleLocation();
		val size = (float) (npcSize >> 1);
		val targetMiddle = target.getMiddleLocation();
		var degrees = Math.toDegrees(((int) ((Math.atan2(targetMiddle.getY() - middle.getY(), targetMiddle.getX() - middle.getX())
                * 325.949D) + offset) & 0x7ff) / 325.949D);
		if (degrees < 0) {
			degrees += 360;
		}
		val angle = Math.toRadians(degrees);
		val tileX = (int) Math.round(middle.getX() + size * Math.cos(angle));
		val tileY = (int) Math.round(middle.getY() + size * Math.sin(angle));
		return new Location(tileX, tileY, middle.getPlane());
	}

	public boolean calcFollow(final Position target, final int maxStepsCount, final boolean calculate, final boolean intelligent,
			final boolean checkEntities) {
		return WalkStep.calcFollow(this, target, maxStepsCount, calculate, intelligent, checkEntities);
	}

	public boolean isProjectileClipped(final Position target, final boolean closeProximity) {
		return ProjectileUtils.isProjectileClipped(this, target instanceof Entity ? (Entity) target : null,
                this, target, closeProximity);
	}

	public boolean addWalkStepsInteract(final int destX, final int destY, final int maxStepsCount, final int size,
			final boolean calculate) {
		return WalkStep.addWalkStepsInteract(this, destX, destY, maxStepsCount, size, size, calculate);
	}

	public int getNextWalkStepPeek() {
		if (walkSteps.isEmpty()) {
			return 0;
		}
		return walkSteps.peek();
	}

	public int getX() {
		return location.getX();
	}

	public int getY() {
		return location.getY();
	}

	public int getPlane() {
		return location.getPlane();
	}

    public void resetWalkSteps() {
        if (!walkSteps.isEmpty()) {
            walkSteps.clear();
        }
    }

	public abstract void setAnimation(final Animation animation);

	public abstract void setInvalidAnimation(final Animation animation);

	public abstract void setUnprioritizedAnimation(final Animation animation);

	public void setGraphics(final Graphics graphics) {
		this.graphics = graphics;
		updateFlags.flag(UpdateFlag.GRAPHICS);
	}

	public void setForceTalk(final String string) {
		setForceTalk(ForceTalk.get(string));
	}

	public void setForceTalk(final ForceTalk talk) {
		forceTalk = talk;
		updateFlags.flag(UpdateFlag.FORCED_CHAT);
	}

	public void setForceMovement(final ForceMovement movement) {
		forceMovement = movement;
		updateFlags.flag(UpdateFlag.FORCE_MOVEMENT);
	}

	@Getter @Setter private transient long lastFaceEntityDelay;

	public void setFaceEntity(final Entity entity) {
	    this.lastFaceEntityDelay = System.currentTimeMillis();
	    faceEntity = entity == null ? -1 : entity.getClientIndex();
		updateFlags.flag(UpdateFlag.FACE_ENTITY);
	}

	public void setRun(final boolean run) {
		this.run = run;
		if (this instanceof Player) {
			((Player) this).getVarManager().sendVar(173, isRun() ? 1 : 0);
		}
	}

	public final Player getMostDamagePlayer() {
        val damage = new MutableInt();
        val player = new MutableObject<String>();
        val currentDamage = new MutableInt();
	    try {
            receivedDamage.object2ObjectEntrySet().removeIf(entry -> {
                val source = entry.getKey();
                currentDamage.setValue(0);
                entry.getValue().removeIf(pair -> {
                    if (pair.getLeft() < System.currentTimeMillis()) {
                        return true;
                    }
                    currentDamage.add(pair.getRight());
                    return false;
                });
                if (currentDamage.longValue() > damage.longValue()) {
                    damage.setValue(currentDamage.intValue());
                    player.setValue(source);
                }
                return false;
            });
        } catch (Exception e) {
	        log.error(Strings.EMPTY, e);
        }
	    val value = player.getValue();
	    return value == null ? null : World.getPlayer(value).orElse(null);
	}

	public final Player getMostDamageNonIronmanPlayer() {
		val damage = new MutableInt();
		val player = new MutableObject<String>();
		val currentDamage = new MutableInt();
		try {
			receivedDamage.object2ObjectEntrySet().removeIf(entry -> {
				val source = entry.getKey();
				val optionalPlayer = World.getPlayer(source);
				if (!optionalPlayer.isPresent() || optionalPlayer.get().isIronman()) {
					return false;
				}
				currentDamage.setValue(0);
				entry.getValue().removeIf(pair -> {
					if (pair.getLeft() < System.currentTimeMillis()) {
						return true;
					}
					currentDamage.add(pair.getRight());
					return false;
				});
				if (currentDamage.longValue() > damage.longValue()) {
					damage.setValue(currentDamage.intValue());
					player.setValue(source);
				}
				return false;
			});
		} catch (Exception e) {
			log.error(Strings.EMPTY, e);
		}
		val value = player.getValue();
		return value == null ? null : World.getPlayer(value).orElse(null);
	}

    protected boolean hasDealtEnoughDamage(@NotNull final Player killer) {
        val playerDamage = new MutableInt();
        val totalDamage = new MutableInt();
        try {
            receivedDamage.object2ObjectEntrySet().removeIf(entry -> {
                val source = entry.getKey();
                val isPlayer = source.equals(killer.getUsername());
                entry.getValue().removeIf(pair -> {
                    if (pair.getLeft() < System.currentTimeMillis()) {
                        return true;
                    }
                    val operand = pair.getRight();
                    totalDamage.add(operand);
                    if (isPlayer) {
                        playerDamage.add(operand);
                    }
                    return false;
                });
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerDamage.intValue() > (Math.min(totalDamage.intValue(), getMaxHitpoints()) * 0.7F);
    }

	public final Player getMostDamagePlayerCheckIronman() {
	    val killer = getMostDamagePlayer();
	    if (killer == null) {
	        return null;
        }
        if (killer.isIronman() && !hasDealtEnoughDamage(killer)) {
            return PotatoToggles.IRONMAN_DMG_RESTRICTION ? null : killer;
        }
        return killer;
    }

	public void setRunSilent(final boolean run) {
		silentRun = run;
	}

	public void setRunSilent(final int ticks) {
		setRunSilent(true);
		WorldTasksManager.schedule(() -> setRunSilent(false), ticks);
	}

	public boolean isLocked() {
		return lockDelay > Utils.currentTimeMillis();
	}

	public void lock() {
		lockDelay = Long.MAX_VALUE;
	}

	/**
	 * Locks the entity for the requested amount of ticks.
	 * 
	 * @param time
	 *            in ticks.
	 */
	public void lock(final int time) {
		lockDelay = Utils.currentTimeMillis() + (time * 600);
	}

	public void unlock() {
		lockDelay = 0;
	}

	public boolean hasWalkSteps() {
		return !walkSteps.isEmpty();
	}

	public float getXpModifier(Hit hit) {
	    return 1;
    }

	@AllArgsConstructor
	public enum EntityType {
		PLAYER(Player.class),
		NPC(NPC.class),
		BOTH(Entity.class);

		@Getter private final Class<? extends Entity> clazz;
	}

	@Override
	public Location getPosition() {
		return getLocation();
	}

}
