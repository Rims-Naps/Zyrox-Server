package com.zenyte.game.world.entity.npc;

import com.zenyte.Constants;
import com.zenyte.cores.WorldThread;
import com.zenyte.game.content.event.DoubleDropsManager;
import com.zenyte.game.content.multicannon.Multicannon;
import com.zenyte.game.content.rottenpotato.PotatoToggles;
import com.zenyte.game.content.skills.prayer.actions.Ashes;
import com.zenyte.game.content.skills.prayer.actions.Bones;
import com.zenyte.game.content.skills.prayer.ectofuntus.AshSanctifier;
import com.zenyte.game.content.skills.prayer.ectofuntus.Bonecrusher;
import com.zenyte.game.content.skills.slayer.SlayerMaster;
import com.zenyte.game.content.supplycaches.SupplyCache;
import com.zenyte.game.content.vote.BoosterPerks;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Position;
import com.zenyte.game.world.World;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.*;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.npc.combatdefs.AggressionType;
import com.zenyte.game.world.entity.npc.combatdefs.NPCCDLoader;
import com.zenyte.game.world.entity.npc.combatdefs.NPCCombatDefinitions;
import com.zenyte.game.world.entity.npc.combatdefs.StatType;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessorLoader;
import com.zenyte.game.world.entity.npc.drop.matrix.NPCDrops;
import com.zenyte.game.world.entity.npc.impl.slayer.superior.SuperiorMonster;
import com.zenyte.game.world.entity.npc.impl.slayer.superior.SuperiorNPC;
import com.zenyte.game.world.entity.npc.spawns.NPCSpawn;
import com.zenyte.game.world.entity.pathfinding.Flags;
import com.zenyte.game.world.entity.player.*;
import com.zenyte.game.world.entity.player.action.combat.CombatType;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.game.world.region.Chunk;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.Region;
import com.zenyte.game.world.region.area.plugins.LootBroadcastPlugin;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import com.zenyte.plugins.item.RingOfWealthItem;
import com.zenyte.utils.ProjectileUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import mgi.types.config.AnimationDefinitions;
import mgi.types.config.items.ItemDefinitions;
import mgi.types.config.npcs.NPCDefinitions;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
public class NPC extends Entity {

    public static final IntOpenHashSet pendingAggressionCheckNPCs = new IntOpenHashSet();
    @Getter
    protected final Object2LongMap<Entity> interactingEntities = new Object2LongOpenHashMap<>();
    protected transient final Int2ObjectOpenHashMap<NPCCombatDefinitions> combatDefinitionsMap = new Int2ObjectOpenHashMap<>(1);
    @Getter
    protected int id;

    /**
     * Whether the NPC is manually spawned or not. Used to define whether to assign a respawning task to the NPC upon death or not. Spawned
     * NPCs by default will not respawn unless modified.
     */
    @Getter
    @Setter
    protected boolean spawned;
    @Getter
    protected NPCCombatDefinitions combatDefinitions;
    /**
     * The location at which the NPC was initially spawned, used to determine where the NPC should respawn.
     */
    @Setter
    protected ImmutableLocation respawnTile;
    /**
     * The actual combat of the NPC.
     */
    @Getter
    protected NPCCombat combat;
    protected int size;
    @Getter
    @Setter
    protected int interactionDistance = 3;
    /**
     * The next transformation id of the NPC. Used to transmogrify NPCs.
     */
    @Getter
    protected int nextTransformation = -1;
    /**
     * The radius of the walk distance of the NPC. It will only random walk within the boundaries here.
     */
    @Getter
    @Setter
    protected int radius = 5;
    /**
     * Whether the NPC ignores its combat distance checks and always enters melee distance or not.
     */
    @Getter
    @Setter
    protected boolean forceFollowClose;
    /**
     * The distance from which the NPC will be able to see you and become aggressive towards you, granted the rest of the aggression
     * requirements are met.
     */
    @Getter
    @Setter
    protected int aggressionDistance;
    /**
     * The delay in ticks between the death animation start and the call to finish().
     */
    @Getter
    @Setter
    protected int deathDelay = 2;
    /**
     * The damage cap of the NPC. By default, there is no cap AKA -1. If you wish to restrict the maximum damage that can be dealt to the
     * NPC in one blow, modify this value.
     */
    @Getter
    @Setter
    protected int damageCap = -1;
    /**
     * The maximum distance between the NPC and its target, if this value is exceeded, the NPC will end its combat task and return to its
     * normal stand-by state.
     */
    @Getter
    @Setter
    protected int maxDistance = 10;
    /**
     * The attack distance for the NPC with magic and ranged styles.
     */
    @Getter
    @Setter
    protected int attackDistance = 7;
    /**
     * The type of the targets that can be assigned through aggressivity to this NPC. Defaults to just players, so it only checks for nearby
     * players whom to aggressively attack.
     */
    @Getter
    @Setter
    protected EntityType targetType = EntityType.PLAYER;
    /**
     * The forced state of the NPC aggression. This variable is only effective if its value is true. Used to set passive NPCs aggressive for
     * a certain period.
     */
    @Getter
    @Setter
    protected boolean forceAggressive;
    /**
     * The region in which the NPC is originally spawned.
     */
    @Setter
    protected Region region;
    /**
     * The tile to which the NPC will force walk; by default the value is null and the NPC does no forcewalking.
     */
    @Getter
    protected Location forceWalk;
    @Getter
    @Setter
    protected Direction spawnDirection = Direction.SOUTH;
    protected NPCDefinitions definitions;
    protected Entity interactingWith;
    protected int ticksUntilRespawn;
    @Getter
    @Setter
    private transient int swapTicks;
    @Getter
    @Setter
    private transient NPCSpawn npcSpawn;
    protected Predicate<Entity> predicate = this::isPotentialTarget;
    protected transient boolean supplyCache = true;

    @Getter
    private boolean inWilderness;

    @Getter
    @Setter
    protected transient int randomWalkDelay;

    private boolean despawnWhenStuck;
    private int despawnTimer;

    @Getter
    @Setter
    protected transient long flinchTime;

    @Getter
    @Setter
    private boolean intelligent;
    private int statReduceTimer = Utils.random(100);//Randomize the timer so all npcs don't reduce their stats at the same exact time - better balances loadp ressure.

    public void normalizeBoostedStats() {
        if (isDead() || isFinished() || !isCycleHealable() || statReduceTimer++ % 100 != 0) {
            return;
        }
        val hitpoints = getHitpoints();
        val maxHitpoints = getMaxHitpoints();
        if (hitpoints < maxHitpoints) {
            setHitpoints(hitpoints + 1);
        } else if (hitpoints > maxHitpoints) {
            setHitpoints(hitpoints - 1);
        }
        val originalCombatDefinitions = NPCCDLoader.get(getId());
        if (originalCombatDefinitions == null) {
            return;
        }
        val statDefinitions = combatDefinitions.getStatDefinitions();
        val originalStatDefinitions = originalCombatDefinitions.getStatDefinitions();
        for (val statType : StatType.levelTypes) {
            val currentLevel = statDefinitions.get(statType);
            val originalLevel = originalStatDefinitions.get(statType);
            if (currentLevel > originalLevel) {
                statDefinitions.set(statType, currentLevel - 1);
            } else if (currentLevel < originalLevel) {
                statDefinitions.set(statType, currentLevel + 1);
            }
        }
    }

    public void flinch() {
        if (flinchTime > WorldThread.WORLD_CYCLE || !isFlinchable()) {
            return;
        }
        val attackSpeed = combatDefinitions.getAttackSpeed();
        combat.combatDelay += attackSpeed / 2;
        flinchTime = WorldThread.WORLD_CYCLE + (attackSpeed / 2) + 8;
    }

    public boolean isFlinchable() {
        return true;
    }

    public void renewFlinch() {
        flinchTime = WorldThread.WORLD_CYCLE + combatDefinitions.getAttackSpeed() + 8;
    }

    @Override
    public void applyHit(final Hit hit) {
        super.applyHit(hit);
        flinch();
    }

    public NPC(final int id, final Location tile, final Direction facing, final int radius) {
        this(id, tile, false);
        spawnDirection = facing;
        if (spawnDirection != null) {
            direction = spawnDirection.getDirection();
        }
        this.radius = radius;
    }

    public NPC(final int id, final Location tile, final boolean spawned) {
        if (tile == null) {
            return;
        }
        forceLocation(new Location(tile));
        this.id = id;
        resetDefinitions();
        updateCombatDefinitions();
        final Animation death = combatDefinitions.getSpawnDefinitions().getDeathAnimation();
        if (death != null) {
            deathDelay = Math.max(Math.min((int) Math.ceil(death.getDuration() / 1200F), 10), 1);
        }
        despawnWhenStuck = definitions.containsOption("Pickpocket");
        aggressionDistance = combatDefinitions.getAggressionDistance();
        this.inWilderness = WildernessArea.isWithinWilderness(getX(), getY());
        if (inWilderness) {
            aggressionDistance /= 2;
        }
        combat = new NPCCombat(this);
        respawnTile = new ImmutableLocation(tile);
        this.spawned = spawned;
        size = getDefinitions().getSize();
        setFinished(true);
        setLastRegionId(0);
        region = World.getRegion(getLocation().getRegionId());
    }

    public static final void clearPendingAggressions() {
        pendingAggressionCheckNPCs.clear();
    }

    public static final int getTransformedId(final int npcId, final Player player) {
        return player.getTransmogrifiedId(NPCDefinitions.getOrThrow(npcId), npcId);
    }

    public void setId(final int id) {
        this.id = id;
        resetDefinitions();
    }

    private void resetDefinitions() {
        val definitions = NPCDefinitions.get(id);
        if (definitions == null) {
            throw new RuntimeException("Invalid NPC id: " + id);
        }
        this.definitions = definitions;
    }

    public Location getRespawnTile() {
        return respawnTile;
    }

    /**
     * @return Whether the NPC can walk through other entities (both players and NPCs) as well as whether other NPCs can walk through this
     * NPC {value false} or not {value true}.
     */
    public boolean isEntityClipped() {
        return true;
    }

    @Override
    public void setLocation(final Location tile) {
        super.setLocation(tile);
        setTeleported(true);
    }

    @Override
    public void unclip() {
        if (!isEntityClipped()) return;
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
                if (collides(chunk.getPlayers(), x1, y1) || collides(chunk.getNPCs(), x1, y1)) continue;
                World.getRegion(Location.getRegionId(x1, y1), true).removeFlag(z, x1 & 0x3F, y1 & 0x3F, clipFlag());
            }
        }
    }

    @Override
    public void clip() {
        if (!isEntityClipped()) return;
        if (isFinished()) {
            return;
        }
        val size = getSize();
        val x = getX();
        val y = getY();
        val z = getPlane();
        for (int x1 = x; x1 < (x + size); x1++) {
            for (int y1 = y; y1 < (y + size); y1++) {
                World.getRegion(Location.getRegionId(x1, y1), true).addFlag(z, x1 & 0x3F, y1 & 0x3F, clipFlag());
            }
        }
    }

    protected int clipFlag() {
        return Flags.OCCUPIED_BLOCK_NPC;
    }

    public boolean isAttackable(final Entity e) {
        return true;
    }

    @Override
    protected void processHit(final Hit hit) {
        /*if (isDead()) {
            return;
        }*/
        if (isImmune(hit.getHitType())) {
            hit.setDamage(0);
        }
        if (hit.getDamage() > Short.MAX_VALUE) {
            hit.setDamage(Short.MAX_VALUE);
        }
        if (hit.getDamage() > getHitpoints()) {
            hit.setDamage(getHitpoints());
        }
        getUpdateFlags().flag(UpdateFlag.HIT);
        getNextHits().add(hit);
        addHitbar();
        if (hit.getHitType() == HitType.HEALED) {
            heal(hit.getDamage());
        } else {
            removeHitpoints(hit);
        }
        postHitProcess();
    }

    protected void postHitProcess() {
    }

    public boolean isCycleHealable() {
        return true;
    }

    public boolean checkAggressivity() {
        if (!isAttackable()) {
            return false;
        }
        if (!forceAggressive) {
            if (!combatDefinitions.isAggressive()) {
                return false;
            }
        }
        getPossibleTargets(targetType);
        if (!possibleTargets.isEmpty()) {
            this.resetWalkSteps();
            val target = possibleTargets.get(Utils.random(possibleTargets.size() - 1));
            setTarget(target);
        }
        return true;
    }

    public void setTarget(final Entity target) {
        combat.setTarget(target);
    }

    /**
     * Whether the NPC is affected by tolerance(players standing in the area for 20 minutes straight)
     *
     * @return whether the npc is tolerable.
     */
    public boolean isTolerable() {
        return true;
    }

    @Override
    public int getSize() {
        return size;
    }

    public final NPCDefinitions getDefinitions() {
        return definitions;
    }

    public void setTransformation(final int id) {
        nextTransformation = id;
        setId(id);
        size = definitions.getSize();
        updateFlags.flag(UpdateFlag.TRANSFORMATION);
        if (preserveStatsOnTransformation()) {
            updateTransformationalDefinitions();
        } else {
            updateCombatDefinitions();
        }
    }

    protected boolean preserveStatsOnTransformation() {
        return false;
    }

    protected void updateTransformationalDefinitions() {
        val def = NPCCombatDefinitions.clone(getId(), NPCCDLoader.get(getId()));
        val currentHitpoints = getHitpoints();
        val currentMaxHitpoints = getMaxHitpoints();
        val updatedMaxHitpoints = def.getHitpoints();
        if (currentMaxHitpoints != updatedMaxHitpoints) {
            setHitpoints((int) ((double) currentHitpoints / currentMaxHitpoints * updatedMaxHitpoints));
        }
        def.getStatDefinitions().setCombatStats(this.combatDefinitions.getStatDefinitions().getCombatStats());
        setCombatDefinitions(def);
        if (inWilderness) {
            if (this.combatDefinitions.isAggressive()) {
                this.combatDefinitions.setAggressionType(AggressionType.ALWAYS_AGGRESSIVE);
            }
        }
    }

    protected void updateCombatDefinitions() {
        var def = combatDefinitionsMap.get(getId());
        if (def == null) {
            val cachedDefs = NPCCDLoader.get(getId());
            def = NPCCombatDefinitions.clone(getId(), cachedDefs);
        }
        if (combatDefinitionsMap.isEmpty()) {
            setHitpoints(def.getHitpoints());
        }
        setCombatDefinitions(def);
        if (inWilderness) {
            if (this.combatDefinitions.isAggressive()) {
                this.combatDefinitions.setAggressionType(AggressionType.ALWAYS_AGGRESSIVE);
            }
        }
    }

    public NPCCombatDefinitions getBaseCombatDefinitions() {
        return NPCCDLoader.get(getId());
    }

    public void setCombatDefinitions(final NPCCombatDefinitions definitions) {
        this.combatDefinitions = definitions;
        combatDefinitionsMap.put(getId(), definitions);
    }

    public boolean lockUponInteraction() {
        return true;
    }

    @Getter
    protected RetreatMechanics retreatMechanics = new RetreatMechanics(this);

    @Override
    public void processEntity() {
        if (getX() < 6400) {
            if (region == null || region.getLoadStage() != 2) {
                return;
            }
        }
        if (ticksUntilRespawn > 0) {
            if (--ticksUntilRespawn > 0) {
                return;
            }
            spawn();
        }
        if (isFinished()) return;
        if (routeEvent != null) {
            if (routeEvent.process()) {
                routeEvent = null;
            }
        }
        iterateScheduledHits();
        processReceivedHits();
        processNPC();
        processMovement();
        retreatMechanics.process();
        toxins.process();
        try {
            normalizeBoostedStats();
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    @Override
    public boolean isProjectileClipped(final Position target, final boolean closeProximity) {
        return ProjectileUtils.isProjectileClipped(target instanceof Entity ? (Entity) target : null, this, target, getNextPosition(isRun() ? 2 : 1), closeProximity);
    }

    /**
     * Whether or not the monster will be frozen on-spot by entity event(to prevent player walking side by side with the npc for extended duration, we reset their steps and stop them
     * from moving)
     *
     * @return whether or not the npc is affected.
     */
    public boolean isPathfindingEventAffected() {
        return true;
    }

    public void finish() {
        if (isFinished()) {
            return;
        }
        try {
            setFinished(true);
            routeEvent = null;
            unclip();
            World.updateEntityChunk(this, true);
            setLastChunkId(-1);
            interactingWith = null;
            if (!interactingEntities.isEmpty()) {
                interactingEntities.clear();
            }
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
        World.removeNPC(this);
    }

    protected transient boolean forceCheckAggression;

    private boolean checkIfDespawn() {
        if (!addWalkSteps(getX() - 1, getY())) {
            if (!addWalkSteps(getX(), getY() - 1)) {
                if (!addWalkSteps(getX() + 1, getY())) {
                    return !addWalkSteps(getX(), getY() + 1);
                }
            }
        }
        return false;
    }

    public void processNPC() {
        if (despawnWhenStuck) {
            if (!isDead()) {
                if (++despawnTimer % 500 == 0) {
                    if (checkIfDespawn()) {
                        finish();
                        setRespawnTask();
                    }
                }
            }
        }
        val delay = randomWalkDelay;
        if (delay > 0) {
            randomWalkDelay--;
        }
        if (combat.process()) {
            return;
        }

        if (isLocked()) {
            return;
        }
        if (this.targetType == EntityType.PLAYER) {
            if (forceCheckAggression || pendingAggressionCheckNPCs.contains(getIndex())) {
                if (checkAggressivity()) {
                    if (combat.getTarget() != null) {
                        return;
                    }
                }
            }
        } else {
            if (checkAggressivity()) {
                if (combat.getTarget() != null) {
                    return;
                }
            }
        }

        if (!interactingEntities.isEmpty()) {
            val it = interactingEntities.object2LongEntrySet().iterator();
            val ctms = Utils.currentTimeMillis();
            while (it.hasNext()) {
                val entry = it.next();
                val e = entry.getKey();
                if (e == null) {
                    continue;
                }
                val time = entry.getLongValue();
                if (e.getLocation().getDistance(getLocation()) > interactionDistance || e.isFinished() || ctms > time) {
                    it.remove();
                    if (e == interactingWith) {
                        setInteractingWith(null);
                    }
                }
            }
            if (!interactingEntities.isEmpty()) {
                return;
            }
        }

        if (delay > 0 || radius <= 0 || Constants.SPAWN_MODE) {
            return;
        }
        if (routeEvent != null || !getWalkSteps().isEmpty()) {
            return;
        }
        if (Utils.random(5) != 0 || isFrozen() || isStunned()) {
            return;
        }
        val moveX = Utils.random(-radius, radius);
        val moveY = Utils.random(-radius, radius);
        val respawnX = respawnTile.getX();
        val respawnY = respawnTile.getY();
        addWalkStepsInteract(respawnX + moveX, respawnY + moveY, radius, getSize(), true);
    }

    public boolean isUsingMelee() {
        return combatDefinitions.isMelee();
    }

    @Override
    public void processMovement() {
        if (faceEntity >= 0) {
            final Entity target = faceEntity >= 32768 ? World.getPlayers().get(faceEntity - 32768) : World.getNPCs().get(faceEntity);
            if (target != null) {
                direction = Utils.getFaceDirection(target.getLocation().getCoordFaceX(target.getSize()) - getX(), target.getLocation().getCoordFaceY(target.getSize()) - getY());
            }
        }
        walkDirection = runDirection = -1;
        if (nextLocation != null) {
            if (lastLocation == null) {
                lastLocation = new Location(location);
            } else {
                lastLocation.setLocation(location);
            }
            despawnTimer = 0;
            unclip();
            forceLocation(nextLocation);
            onMovement();
            clip();
            nextLocation = null;
            teleported = true;
            World.updateEntityChunk(this, false);
            resetWalkSteps();
            return;
        }
        teleported = false;
        if (walkSteps.isEmpty() || isLocked() && temporaryAttributes.get("ignoreWalkingRestrictions") == null) {
            return;
        }
        if (isDead() || isFinished()) {
            return;
        }
        if (lastLocation == null) {
            lastLocation = new Location(location);
        } else {
            lastLocation.setLocation(location);
        }
        final int steps = silentRun ? 1 : run ? 2 : 1;
        int stepCount;
        for (stepCount = 0; stepCount < steps; stepCount++) {
            final int nextStep = getNextWalkStep();
            if (nextStep == 0) {
                break;
            }
            final int dir = WalkStep.getDirection(nextStep);
            if ((WalkStep.check(nextStep) && !canMove(getX(), getY(), dir))) {
                resetWalkSteps();
                break;
            }
            if (stepCount == 0) {
                walkDirection = dir;
            } else {
                runDirection = dir;
            }
            final int x = Utils.DIRECTION_DELTA_X[dir];
            final int y = Utils.DIRECTION_DELTA_Y[dir];
            unclip();
            location.moveLocation(x, y, 0);
            clip();
        }
        despawnTimer = 0;
        onMovement();
        if (faceEntity < 0) {
            direction = Utils.getFaceDirection(location.getX() - lastLocation.getX(), location.getY() - lastLocation.getY());
        }
        World.updateEntityChunk(this, false);
    }

    protected boolean canMove(final int fromX, final int fromY, final int direction) {
        return World.checkWalkStep(getPlane(), fromX, fromY, direction, getSize(), isEntityClipped(), false);
    }

    public void forceWalkRespawnTile() {
        setForceWalk(respawnTile);
    }

    public boolean isUnderCombat() {
        return combat.underCombat();
    }

    public void setForceWalk(final Location tile) {
        resetWalkSteps();
        forceWalk = tile;
    }

    public void finishInteractingWith(final Entity entity) {
        if (entity == interactingWith) {
            interactingWith = null;
        }
        interactingEntities.removeLong(entity);
        if (!isUnderCombat()) {
            setFaceEntity(null);
        }
    }

    public void setInteractingWith(final Entity entity) {
        if (entity == interactingWith) {
            if (entity != null) {
                interactingEntities.put(entity, Utils.currentTimeMillis() + 60000);
            }
            return;
        }
        interactingWith = entity;
        if (!isUnderCombat()) {
            setFaceEntity(entity);
        }
        if (entity == null) {
            return;
        }
        if (!(entity instanceof Player)) {
            entity.resetWalkSteps();
        }
        if (!interactingEntities.containsKey(entity)) {
            interactingEntities.put(entity, Utils.currentTimeMillis() + 60000);
        }
    }

    @Override
    public int getMaxHitpoints() {
        return combatDefinitions.getHitpoints();
    }

    @Override
    public boolean isDead() {
        return getHitpoints() == 0;
    }

    @Override
    public int getClientIndex() {
        return getIndex();
    }

    @Override
    public Location getMiddleLocation() {
        if (middleTile == null) {
            middleTile = size == 1 ? new Location(getLocation()) : new Location(getLocation().getCoordFaceX(size), getLocation().getCoordFaceY(size), getPlane());
        } else {
            if (size == 1) {
                middleTile.setLocation(getLocation());
            } else {
                middleTile.setLocation(getLocation().getCoordFaceX(size), getLocation().getCoordFaceY(size), getPlane());
            }
        }
        return middleTile;
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        if (damageCap != -1 && hit.getDamage() > damageCap) {
            hit.setDamage(damageCap);
        }
    }

    @Override
    public void postProcessHit(final Hit hit) {

    }

    public boolean isTickEdible() {
        return true;
    }

    @Override
    public void handleOutgoingHit(final Entity target, final Hit hit) {
    }

    @Override
    public double getMagicPrayerMultiplier() {
        return 0;
    }

    @Override
    public double getRangedPrayerMultiplier() {
        return 0;
    }

    @Override
    public double getMeleePrayerMultiplier() {
        return 0;
    }

    protected String notificationName(@NotNull final Player player) {
        return definitions.getName().toLowerCase();
    }

    protected void sendNotifications(final Player player) {
        val name = notificationName(player);
        val isBoss = NotificationSettings.BOSS_NPC_NAMES.contains(name);
        if (this instanceof SuperiorNPC) {
            player.getNotificationSettings().increaseKill("superior creature");
        }
        if (NotificationSettings.isKillcountTracked(name)) {
            player.getNotificationSettings().increaseKill(name);
            if (isBoss) {
                player.getNotificationSettings().sendBossKillCountNotification(name);
            }
        }
    }

    public boolean isAttackableNPC() {
        return getDefinitions().containsOption("Attack");
    }

    protected void onFinish(final Entity source) {
        try {
            spawnSuperior(source, this);
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
        drop(getMiddleLocation());
        reset();
        finish();
        if (!spawned) {
            setRespawnTask();
        }
        if (source != null) {
            if (source instanceof Player) {
                val player = (Player) source;
                sendNotifications(player);
            }
        }
    }

    @Override
    public void setAnimation(final Animation animation) {
        this.animation = animation;
        if (animation == null) {
            updateFlags.set(UpdateFlag.ANIMATION, false);
            lastAnimation = 0;
        } else {
            if (!AnimationMap.isValidAnimation(id, animation.getId())) {
                new Exception("Invalid animation: " + animation.getId() + ", " + getId()).printStackTrace();
                return;
            }
            updateFlags.flag(UpdateFlag.ANIMATION);
            final AnimationDefinitions defs = AnimationDefinitions.get(animation.getId());
            if (defs != null) {
                lastAnimation = Utils.currentTimeMillis() + defs.getDuration();
            } else {
                lastAnimation = Utils.currentTimeMillis();
            }
        }
    }

    @Override
    public void setInvalidAnimation(final Animation animation) {
        this.animation = animation;
        if (animation == null) {
            updateFlags.set(UpdateFlag.ANIMATION, false);
            lastAnimation = 0;
        } else {
            updateFlags.flag(UpdateFlag.ANIMATION);
            final AnimationDefinitions defs = AnimationDefinitions.get(animation.getId());
            if (defs != null) {
                lastAnimation = Utils.currentTimeMillis() + defs.getDuration();
            } else {
                lastAnimation = Utils.currentTimeMillis();
            }
        }
    }

    @Override
    public void setUnprioritizedAnimation(final Animation animation) {
        if (lastAnimation > Utils.currentTimeMillis() || updateFlags.get(UpdateFlag.ANIMATION)) {
            return;
        }
        if (animation != null && !AnimationMap.isValidAnimation(id, animation.getId())) {
            Thread.dumpStack();
            return;
        }
        this.animation = animation;
        updateFlags.set(UpdateFlag.ANIMATION, animation != null);
    }

    @Getter private transient long timeOfDeath;

    protected void onDeath(final Entity source) {
        try {
            timeOfDeath = WorldThread.WORLD_CYCLE;
            resetWalkSteps();
            combat.removeTarget();
            setAnimation(null);
            if (source != null) {
                if (source instanceof Player) {
                    val player = (Player) source;
                    player.getSlayer().checkAssignment(this);
                }
            }
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    @Override
    public void sendDeath() {
        val source = getMostDamagePlayerCheckIronman();
        onDeath(source);
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                if (ticks == 0) {
                    val spawnDefinitions = combatDefinitions.getSpawnDefinitions();
                    setAnimation(spawnDefinitions.getDeathAnimation());
                    val sound = spawnDefinitions.getDeathSound();
                    if (sound != null && source != null) {
                        source.sendSound(sound);
                    }
                } else if (ticks == deathDelay) {
                    onFinish(source);
                    stop();
                    return;
                }
                ticks++;
            }
        }, 0, 1);
    }

    protected Player getDropRecipient() {
        Player killer = getMostDamagePlayer();
        if (killer == null) {
            return null;
        }
        if (killer.isIronman() && !hasDealtEnoughDamage(killer)) {
            killer = PotatoToggles.IRONMAN_DMG_RESTRICTION ? getMostDamageNonIronmanPlayer() : getMostDamagePlayer();
        }
        return killer;
    }

    protected void drop(final Location tile) {
        val killer = getDropRecipient();
        if (killer == null) {
            return;
        }
        onDrop(killer);
        val processors = DropProcessorLoader.get(id);
        if (processors != null) {
            for (val processor : processors) {
                processor.onDeath(this, killer);
            }
        }
        val drops = NPCDrops.getTable(getId());
        if (drops == null) {
            return;
        }
        NPCDrops.forEach(drops, drop -> dropItem(killer, drop, tile));
    }

    private static final Class<?>[] superiorParams = new Class[]{
            Player.class, NPC.class, Location.class
    };

    private void spawnSuperior(@Nullable final Entity killer, @NotNull final NPC inferior) {
        if (!(killer instanceof Player)) {
            return;
        }
        val player = (Player) killer;
        val rate = player.getNumericTemporaryAttributeOrDefault("superior rate", BoosterPerks.isActive(player, BoosterPerks.SLAYER) ? 84 : (player.getMemberRank().eligibleTo(MemberRank.DIAMOND_MEMBER) ? 89 : 99)).intValue();
        val slayer = ((Player) killer).getSlayer();
        if (Utils.random(rate) != 0 || !slayer.isCurrentAssignment(inferior)) {
            return;
        }
        if (slayer.getMaster() == SlayerMaster.KONAR_QUO_MATEN) {
            val assignment = slayer.getAssignment();
            val area = assignment.getArea();
            if (area != null) {
                if (!player.inArea(area) && !assignment.checkExceptions(inferior, area)) {
                    return;
                }
            }
        }
        val superior = SuperiorMonster.getSuperior(inferior.getDefinitions().getName());
        if (!superior.isPresent()) {
            return;
        }

        if (player.getTemporaryAttributes().containsKey("superior monster") || !player.getSlayer().isBiggerAndBadder()) {
            return;
        }
        try {
            val sup = superior.get().getDeclaredConstructor(superiorParams).newInstance((Player) killer, inferior, getLocation());
            val tile = Utils.findEmptySquare(getLocation(), sup.getSize() + 6, sup.getSize(),
                    Optional.of(t -> !Utils.collides(t.getX(), t.getY(), sup.getSize(), killer.getX(), killer.getY(), killer.getSize())));
            if (tile.isPresent()) {
                sup.setLocation(tile.get());
                sup.spawn();
                player.sendMessage(Colour.RED.wrap("A superior foe has appeared..."));
            }
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    public void onDrop(@NonNull final Player killer) {
        val level = getDefinitions().getCombatLevel();

        if (supplyCache && level >= 50 && !killer.isIronman()) {
            val wilderness = WildernessArea.isWithinWilderness(getX(), getY());
            val cappedLevel = Math.min(300, level);
            int chance = 350 - cappedLevel;
            if (wilderness) {
                chance *= 0.75F;
            }
            if (Utils.random(chance - 1) == 0) {
                val loot = SupplyCache.random();
                killer.sendMessage(Colour.RS_GREEN.wrap("The " + getName(killer).toLowerCase() + " drops you some extra supplies."));
                loot.ifPresent(cache -> {
                    dropItem(killer, new Item(ItemDefinitions.getOrThrow(cache.getId()).getNotedOrDefault(), Utils.random(cache.getMin(), cache.getMax())));
                    dropItem(killer, new Item(995, Utils.random(50_000, 150_000)));
                });
            }
        }
    }

    protected void invalidateItemCharges(@NotNull final Item item) {
        item.setCharges(DegradableItem.getFullCharges(item.getId()));
    }

    public void dropItem(final Player killer, final Item item, final Location tile, final boolean guaranteedDrop) {
        if(DoubleDropsManager.isDoubled(item.getId())) {
            System.out.println("Doubled item: " + item.getName() + "x " + item.getAmount() + " for " + killer.getUsername());
            killer.sendMessage(DoubleDropsManager.DROP_MESSAGE, MessageType.FILTERABLE);
            item.setAmount(item.getAmount() * 2);
        }
        invalidateItemCharges(item);
        killer.getCollectionLog().add(item);
        WorldBroadcasts.broadcast(killer, BroadcastType.RARE_DROP, item, getName(killer));
        LootBroadcastPlugin.fireEvent(killer.getName(), item, tile, guaranteedDrop);
        if (item.getId() == 11941 && killer.containsItem(item)) {
            return;
        }
        //Amulet of avarice's effect inside revenant caves. Now all of wilderness
        if (killer.getEquipment().getId(EquipmentSlot.AMULET) == 22557 && GlobalAreaManager.get("Forinthry Dungeon").inside(getLocation())) {
            item.setId(item.getDefinitions().getNotedOrDefault());
        } else if(killer.getEquipment().getId(EquipmentSlot.AMULET) == 22557 && killer.inArea("Wilderness") && (killer.getSlayer().getAssignment() != null && killer.getSlayer().getMaster() == SlayerMaster.KRYSTILIA && killer.getSlayer().isCurrentAssignment(this))) {
            item.setId(item.getDefinitions().getNotedOrDefault());
        }

        val id = item.getId();
        if ((id == 995 || id == 21555 || id == 6529 || id == 2803 || id == 2805 || id == 2807 || id == 2809 || id == 2811 || id == 2813) && RingOfWealthItem.isRingOfWealth(killer.getRing())
                && !killer.getBooleanSetting(Setting.ROW_CURRENCY_COLLECTOR)) {
            killer.getInventory().addOrDrop(item);
            killer.getNotificationSettings().sendDropNotification(item);
            return;
        }

        killer.getNotificationSettings().sendDropNotification(item);

        val bone = Bones.getBone(item.getId());
        val crusherType = Bonecrusher.CrusherType.get(killer);
        if (crusherType != null && bone != null) {
            if (crusherType.getEffect().crush(killer, bone, false)) {
                return;
            }
        }
        val ash = Ashes.getAsh(item.getId());
        val sanctifierType = AshSanctifier.CrusherType.get(killer);
        if (sanctifierType != null && ash != null) {
            if (sanctifierType.getEffect().crush(killer, ash)) {
                return;
            }
        }
        spawnDrop(item, tile, killer);
    }

    protected void spawnDrop(final Item item, final Location tile, final Player killer) {
        World.spawnFloorItem(item, tile, killer, invisibleDropTicks(), visibleDropTicks());
    }

    protected int invisibleDropTicks() {
        return 100;
    }

    protected int visibleDropTicks() {
        return 200;
    }

    public void dropItem(final Player killer, final Item item) {
        this.dropItem(killer, item, getMiddleLocation(), false);
    }

    public final void dropItem(final Player killer, final Drop drop, final Location location) {
        var item = new Item(drop.getItemId(), Utils.random(drop.getMinAmount(), drop.getMaxAmount()));
        val processors = DropProcessorLoader.get(id);
        if (processors != null) {
            val baseItem = item;
            for (val processor : processors) {
                if ((item = processor.drop(this, killer, drop, item)) == null) {
                    return;
                }
                if (item != baseItem) break;
            }
        }
        //do NOT reference 'drop' after this line, rely on 'item' only!
        dropItem(killer, item, location, drop.isAlways());
    }

    public int getRespawnDelay() {
        return 60;
    }

    public void setRespawnTask() {
        if (!isFinished()) {
            reset();
            finish();
        }
        val respawnTile = getRespawnTile();
        val region = World.regions.get(respawnTile.getRegionId());
        WorldTasksManager.schedule(() -> {
            if (respawnTile.getX() >= 6400 && World.regions.get(respawnTile.getRegionId()) != region) {
                return;
            }
            spawn();
        }, getRespawnDelay());
    }

    public NPC spawn() {
        if (!isFinished()) {
            throw new RuntimeException("The NPC has already been spawned: " + getId() + ", " + getDefinitions().getName() + ", " + getNpcSpawn() + ", " + getLocation());
        }
        World.addNPC(this);
        location.setLocation(getRespawnTile());
        setFinished(false);
        updateLocation();
        if (!combatDefinitionsMap.isEmpty()) {
            combatDefinitionsMap.clear();
        }
        updateCombatDefinitions();
        return this;
    }

    public void updateLocation() {
        setLastRegionId(0);
        World.loadRegion(location.getRegionId());
        World.updateEntityChunk(this, false);
        loadMapRegions();
        clip();
    }

    @Override
    public final int getCombatLevel() {
        return getDefinitions().getCombatLevel();
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.NPC;
    }

    @Override
    protected boolean isAcceptableTarget(final Entity entity) {
        return true;
    }

    @Override
    protected boolean isPotentialTarget(final Entity entity) {
        val entityX = entity.getX();
        val entityY = entity.getY();
        val entitySize = entity.getSize();

        val x = getX();
        val y = getY();
        val size = getSize();

        val currentTime = Utils.currentTimeMillis();

        return !entity.isMaximumTolerance() && (entity.isMultiArea() || entity.getAttackedBy() == this || (entity.getAttackedByDelay() <= currentTime && entity.getFindTargetDelay() <= currentTime)) && (!ProjectileUtils.isProjectileClipped(this, entity, getLocation(), entity.getLocation(), combatDefinitions.isMelee())

                || Utils.collides(x, y, size, entityX, entityY, entitySize)) && (forceAggressive || combatDefinitions.isAlwaysAggressive() || combatDefinitions.isAggressive() && entity.getCombatLevel() <= (getCombatLevel() << 1)) && (!(entity instanceof NPC)


                || ((NPC) entity).getDefinitions().containsOption("Attack")) && isAcceptableTarget(entity) && (!(entity instanceof Player) || !isTolerable() || !((Player) entity).isTolerant(getLocation()));
    }

    @Override
    public void unlink() {

    }

    @Override
    public List<Entity> getPossibleTargets(final EntityType type) {
        if (!possibleTargets.isEmpty()) {
            possibleTargets.clear();
        }
        CharacterLoop.populateEntityList(possibleTargets, this.getMiddleLocation(), aggressionDistance + (getSize() / 2), type.getClazz(), predicate);
        return possibleTargets;
    }

    public void remove() {
        finish();
    }

    protected void onMovement() {
    }

    @Override
    public void cancelCombat() {
        combat.setTarget(null);
    }

    @Override
    public void performDefenceAnimation(Entity attacker) {
        val blockDefinitions = getCombatDefinitions().getBlockDefinitions();
        setUnprioritizedAnimation(blockDefinitions.getAnimation());
        val sound = blockDefinitions.getSound();
        if (sound != null) {
            if (sound.getRadius() == 0) {
                if (attacker instanceof Player) {
                    ((Player) attacker).sendSound(sound);
                }
            } else {
                World.sendSoundEffect(this::getMiddleLocation, sound);
            }
        }
    }

    @Override
    public int drainSkill(final int skill, final double percentage) {
        return combatDefinitions.drainSkill(skill, percentage, 0);
    }

    @Override
    public int drainSkill(final int skill, final double percentage, final int minimumDrain) {
        return combatDefinitions.drainSkill(skill, percentage, minimumDrain);
    }

    @Override
    public int drainSkill(final int skill, final int amount) {
        return combatDefinitions.drainSkill(skill, amount);
    }

    @Override
    public boolean canAttack(final Player source) {
        if (!definitions.containsOptionCaseSensitive("Attack")) {
            source.sendMessage("You can't attack this npc.");
            return false;
        }
        return true;
    }

    /**
     * Whether or not this npc can be attacked by a multicannon from the given player.
     * @param player the player the multicannon belongs to
     */
    public boolean canBeMulticannoned(@NotNull final Player player) {
        if(getDefinitions().getName().contains("Revenant") || Arrays.asList(Multicannon.UNCANNONABLE_NPC_NAMES).contains(getDefinitions().getName())) {
            return false;
        }
        return true;
    }

    public boolean isAttackable() {
        return definitions.containsOptionCaseSensitive("Attack");
    }

    @Override
    public boolean startAttacking(final Player source, final CombatType type) {
        return true;
    }

    @Override
    public void autoRetaliate(final Entity source) {
        if (combat.getTarget() == source) return;
        if (!combat.isForceRetaliate()) {
            val target = combat.getTarget();
            if (target != null) {
                if (target instanceof Player) {
                    val player = (Player) target;
                    if (player.getActionManager().getAction() instanceof PlayerCombat) {
                        val combat = (PlayerCombat) player.getActionManager().getAction();
                        if (combat.getTarget() == this) {
                            return;
                        }
                    }
                } else {
                    val npc = (NPC) target;
                    if (npc.getCombat().getTarget() == this) return;
                }
            }
        }
        randomWalkDelay = 1;
        resetWalkSteps();
        val previousTarget = combat.getTarget();
        combat.setTarget(source);
        if (previousTarget == null && combat.getCombatDelay() == 0) {
            combat.setCombatDelay(2);
        }
    }

    public boolean isAbstractNPC() {
        return respawnTile == null;
    }

    public String getName(final Player player) {
        return NPCDefinitions.get(getTransformedId(getId(), player)).getName();
    }

    @Override
    public boolean isRunning() {
        return true;// Always true for npcs.
    }

    @Override
    public boolean isMaximumTolerance() {
        return false;
    }

    @Override
    public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check) {
        final int dir = Utils.getMoveDirection(nextX - lastX, nextY - lastY);
        if (dir == -1 || !isMovableEntity() || Constants.SPAWN_MODE) {
            return false;
        }
        if (check && !canMove(lastX, lastY, dir)) {
            return false;
        }
        walkSteps.enqueue(WalkStep.getHash(dir, nextX, nextY, check));
        return true;
    }

    protected boolean isMovableEntity() {
        return true;
    }

    public boolean applyDamageFromHitsAfterDeath() {
        return false;
    }

}
