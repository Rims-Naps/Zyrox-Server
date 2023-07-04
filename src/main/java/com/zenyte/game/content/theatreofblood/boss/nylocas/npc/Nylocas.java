package com.zenyte.game.content.theatreofblood.boss.nylocas.npc;

import com.zenyte.game.content.theatreofblood.boss.nylocas.NylocasRoom;
import com.zenyte.game.content.theatreofblood.boss.nylocas.model.*;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.npc.CombatScriptsHandler;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NPCCombat;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.pathfinding.events.npc.NPCCollidingEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.Player;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Tommeh | 6/7/2020 | 1:46 AM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public abstract class Nylocas extends TheatreNPC<NylocasRoom> implements CombatScript {

    public static final Class[] params = {
            NylocasRoom.class, Location.class, Direction.class, Spawn.class
    };

    private static final Animation magicAnimation = new Animation(7990);
    private static final Animation meleeAnimation = new Animation(8004);
    private static final Animation rangedAnimation = new Animation(8001);

    private Spawn spawn;
    private NylocasType type;
    @Getter
    private boolean large;
    @Getter
    private List<String> immunity;
    @Setter
    private SegmentType segment;
    private boolean exploding, startTargetSequence;

    public Nylocas(final NylocasRoom room, final int id, final Location location, final Direction direction, final Spawn spawn) {
        super(room.getRaid(), room, id, location, direction);
        this.large = spawn.isLarge();
        this.spawn = spawn;
        type = spawn.getType();
        immunity = new ArrayList<>();
        setTargetType(EntityType.BOTH);
        combat = new NPCCombat(this) {
            @Override
            public int combatAttack() {
                if (target == null || target.isDead() || target.getLocation().getDistance(getLocation()) >= 64
                        || (target.getNextLocation() != null && target.getNextLocation().getDistance(getLocation()) >= 64)) {
                    return 0;
                }
                val melee = npc.getCombatDefinitions().isMelee();
                var distance = melee || npc.isForceFollowClose() ? 0 : npc.getAttackDistance();
                if (target.hasWalkSteps()) {
                    distance++;
                }
                if (Utils.collides(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize())) {
                    return 0;
                }
                if (outOfRange(target, distance, target.getSize(), melee)) {
                    return 0;
                }
                addAttackedByDelay(target);
                return CombatScriptsHandler.specialAttack(npc, target);
            }

            @Override
            protected boolean checkAll() {
                if (target.isFinished() || npc.isDead() || npc.isFinished()) {
                    return false;
                }
                if (target.isDead() || npc.isMovementRestricted()) {
                    return true;
                }
                if (colliding()) {
                    //TODO: Change into a more efficent pathfinding formula or write a non-pf structure.
                    npc.setRouteEvent(new NPCCollidingEvent(npc, new EntityStrategy(target)));
                    return true;
                }
                return appendMovement();
            }
        };
    }

    @Override
    public NPC spawn() {
        val npc = (Nylocas) super.spawn();
        WorldTasksManager.schedule(() -> {
            if (isDead() || isFinished()) {
                return;
            }
            explode();
        }, 50);
        return npc;
    }

    @Override
    public float getXpModifier(final Hit hit) {
        val source = hit.getSource();
        if (!(source instanceof Player)) {
            return 1;
        }

        val player = (Player) source;
        return isImmune(player, hit) ? 0 : 1;
    }

    @Override
    public void setId(final int id) {
        super.setId(id);
        type = NylocasType.get(id);
    }

    @Override
    public void setTransformation(final int id) {
        nextTransformation = id;
        setId(id);
        size = definitions.getSize();
        updateFlags.flag(UpdateFlag.TRANSFORMATION);
    }

    @Override
    public int attack(final Entity target) {
        var damage = 0;
        if (target instanceof PillarSupport) {
            damage = large ? 4 : 2;
        } else {
            damage = getRandomMaxHit(this, combatDefinitions.getMaxHit(), combatDefinitions.getAttackType(), target);
        }
        if (target instanceof Player) {
            if (getRaid().getParty().getLifeStates().get(((Player) target).getUsername()).equals("dead") ||
                    getRaid().getParty().getLifeStates().get(((Player) target).getUsername()).equals("wiped") ||
                    getRoom().getPillars().isEmpty()) {
                return combatDefinitions.getAttackSpeed();
            }
        }
        if (type == NylocasType.MELEE) {
            setAnimation(meleeAnimation);
            delayHit(0, target, new Hit(this, damage, HitType.MELEE));
        } else if (type == NylocasType.MAGIC) {
            val projectile = new Projectile(large ? 1610 : 1609, 20, 20, 30, 10, 30, 0, 1);
            setAnimation(magicAnimation);
            delayHit(World.sendProjectile(this, target, projectile), target, new Hit(this, damage, HitType.MAGIC));
        } else if (type == NylocasType.RANGED) {
            val projectile = new Projectile(large ? 1560 : 1559, 10, 10, 30, 10, 30, 0, 1);
            setAnimation(rangedAnimation);
            delayHit(World.sendProjectile(this, target, projectile), target, new Hit(this, damage, HitType.RANGED));
        }
        return combatDefinitions.getAttackSpeed();
    }

    @Override
    protected void setStats() {
        val partySize = getRaid().getParty().getSize();

        if (partySize <= 3) {
            combatDefinitions.setHitpoints(8);
        } else if (partySize == 4) {
            combatDefinitions.setHitpoints(9);
        } else if (partySize == 5)
            combatDefinitions.setHitpoints(11);

        setHitpoints(combatDefinitions.getHitpoints());
        setAttackDistance(0);
    }

    @Override
    public boolean freeze(final int freezeTicks, final int immunityTicks, @Nullable final Consumer<Entity> onFreezeConsumer) {
        val freeze = super.freeze(freezeTicks, immunityTicks, onFreezeConsumer);
        combat.setCombatDelay((int) TimeUnit.SECONDS.toTicks(5));
        return freeze;
    }

    @Override
    public void setTarget(final Entity target) {
        if (target == null) {
            return;
        }
        combat.setTarget(target);
    }

    public void explode() {
        exploding = true;
        lock();
        setCantInteract(true);
        setAnimation(type.getExplosionAnimation());
        WorldTasksManager.schedule(() -> {
            for (val m : raid.getParty().getMembers()) {
                val member = RaidingParty.getPlayer(m);
                if (member == null || !getLocation().withinDistance(member, 1)) {
                    continue;
                }
                delayHit(0, member, new Hit(this, Utils.random(12, 18), HitType.REGULAR));
            }
            onFinish(null);
        }, 2);
    }


    public boolean isImmune(final Player player, final Hit hit) {
        return immunity.contains(player.getUsername()) || (type != null && !hit.getHitType().equals(type.getAcceptableHitType()));
    }

    @Override
    public void processNPC() {
        super.processNPC();

        if(getRaid().getParty().getTargetableMembers().size() < 1) {
            return;
        }

        if (!room.getPlatform().contains(location) && !isFrozen()) {
            pathStraightLine(segment);
            return;
        }

        if (isFrozen() || startTargetSequence) {
            return;
        }
        if (!spawn.isAggressive()) {
            if (!room.getPillars().containsKey(spawn.getTarget())) {
                freeze(1);
                setTarget(raid.getParty().getRandomPlayer());
                startTargetSequence = true;
                return;
            }
            if (hasWalkSteps()) {
                var corner = room.getClosestCorner(this, spawn.getTarget());
                if (corner != null) {
                    val tiles = new Location[4];
                    val corners = spawn.getTarget().getCorners();
                    //var corner = room.getClosestCorner(this, spawn.getTarget());
                    tiles[0] = room.getLocation(corner.getPrimary());
                    tiles[1] = room.getLocation(corner.getSecondary());


                    corner = corners[0] == corner ? corners[1] : corners[0];
                    tiles[2] = room.getLocation(corner.getPrimary());
                    tiles[3] = room.getLocation(corner.getSecondary());


                    resetWalkSteps();
                    for (val tile : tiles) {
                    if (room.isOccupied(tile)) {
                        continue;
                    }
                        if (isLarge()) {
                            if (room.isLargeSpotBlocked(tile)) {
                                continue;
                            }
                        }
                        addWalkSteps(tile.getX(), tile.getY(), -1, false);
                        return;
                    }
                }
            }
        } else {
            freeze(1);
            setTarget(raid.getParty().getRandomPlayer());
        }
        startTargetSequence = true;
    }

    @Override
    public void sendDeath() {
        if (exploding) {
            return;
        }
        super.sendDeath();
    }

    @Override
    protected void onFinish(final Entity source) {
        super.onFinish(source);

        if (large) {
            val middle = getLastLocation();
            val middle2 = middle.transform(1,-1,0);
            if (room.getPlatform().contains(middle)) {
                spawnRandomNylo(middle);
            } else {
                spawnRandomNylo(room.getLocation(3294, 4247, 0));
            }
            if (room.getPlatform().contains(middle2)) {
                spawnRandomNylo(middle2);
            }
            else {
                spawnRandomNylo(room.getLocation(3295, 4246, 0));
            }
        }

        room.getNylos().remove(spawn);
        if (room.getNylos().isEmpty() && room.getWave() == WaveDefinition.WAVE_31) {
            WorldTasksManager.schedule(() -> room.setPhase(NylocasPhase.BOSS), (int) TimeUnit.SECONDS.toTicks(3));
        }
    }

    private void spawnRandomNylo(final Location location) {
        val randomNylo = NylocasType.getRandom();
        Entity target = null;

        val pillars = new ArrayList<>(room.getPillars().values());
        Collections.shuffle(pillars);

        for (val pillar : pillars) {
            if (!pillar.isFinished()) {
                target = pillar;
                break;
            }
        }

        if (target == null) {
            target = room.getRaid().getParty().getRandomPlayer();
        }

        if (target == null) {
            return;
        }

        try {
            val spawn = Spawn.of(randomNylo);
            if (target instanceof Player) {
                spawn.aggressive();
            } else {
                spawn.target(((PillarSupport) target).getType());
            }
            val nylocas = randomNylo.getClazz().getDeclaredConstructor(params).newInstance(room, location, Direction.getNPCDirection(Utils.getFaceDirection(target.getLocation(), location)), spawn);

            nylocas.spawn();
            room.getNylos().put(spawn, nylocas);

            nylocas.pathStraightLine(segment);

            val t = target;
            WorldTasksManager.schedule(new TickTask() {
                PillarLocation type;

                @Override
                public void run() {
                    if (type != null && !nylocas.hasWalkSteps()) {
                        val pillar = room.getPillars().get(type);
                        if (pillar != null) {
                            WorldTasksManager.schedule(() -> nylocas.setTarget(pillar), 2);
                        }
                        stop();
                        return;
                    }
                    if (World.isFloorFree(nylocas.getLocation(), 1)) {
                        if (t instanceof PillarSupport) {
                            type = ((PillarSupport) t).getType();
                            val corner = room.getClosestCorner(nylocas, type);
                            if (corner != null) {
                                val primary = room.getLocation(corner.getPrimary());
                                nylocas.resetWalkSteps();
                                nylocas.addWalkSteps(primary.getX(), primary.getY(), -1, false);
                            }
                        } else {
                            nylocas.setTarget(t);
                            stop();
                        }
                    }
                }
            }, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pathStraightLine(final SegmentType segment) {
        resetWalkSteps();
        if (segment == SegmentType.EAST) {
            addWalkSteps(getX() - 15, getY(), -1, false);
        } else if (segment == SegmentType.SOUTH) {
            addWalkSteps(getX(), getY() + 15, -1, false);
        } else if (segment == SegmentType.WEST) {
            addWalkSteps(getX() + 15, getY(), -1, false);
        }
    }

    @Override
    public boolean isEntityClipped() {
        return true;
    }
}
