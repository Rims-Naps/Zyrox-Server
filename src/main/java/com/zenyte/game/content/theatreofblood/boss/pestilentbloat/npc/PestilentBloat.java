package com.zenyte.game.content.theatreofblood.boss.pestilentbloat.npc;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.zenyte.game.CameraShakeType;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.theatreofblood.boss.pestilentbloat.PestilentBloatRoom;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Position;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.utils.ProjectileUtils;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Tommeh | 5/28/2020 | 7:02 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class PestilentBloat extends TheatreNPC<PestilentBloatRoom> implements CombatScript {

    private static final Projectile fliesProjectile = new Projectile(1568, 60, 10, 0, 0, 30, 0, 6);
    private static final Graphics fliesImpactGfx = new Graphics(1569);
    private static final Animation stompAnimation = new Animation(8082);
    private static final Graphics stunGfx = new Graphics(254, 0, 92);
    private static final SoundEffect fallingMeatSound = new SoundEffect(3308, 15);
    private static final SoundEffect[] fliesSounds = {
            new SoundEffect(3945), new SoundEffect(3954), new SoundEffect(4016)
    };

    private final Location northWest, southWest, northEast, southEast;

    private BiMap<Location, Location> pathStrategy;
    private Location previousCorner;
    private boolean performingStompAttack;
    private List<Location> previousMeatLocations;
    private int stompCount;

    public PestilentBloat(final PestilentBloatRoom room) {
        super(room.getRaid(), room, NpcId.PESTILENT_BLOAT, room.getLocation(3299, 4445, 0), Direction.SOUTH);
        northWest = room.getLocation(3288, 4451, 0);
        southWest = room.getLocation(3288, 4440, 0);
        northEast = room.getLocation(3299, 4451, 0);
        southEast = room.getLocation(3299, 4440, 0);

        pathStrategy = HashBiMap.create(new HashMap<Location, Location>() {{
            put(northWest, southWest);
            put(southWest, southEast);
            put(southEast, northEast);
            put(northEast, northWest);
        }});

        previousMeatLocations = new ArrayList<>(16);
    }

    @Override
    public NPC spawn() {
        val npc = super.spawn();
        addWalkSteps(northEast.getX(), northEast.getY(), -1, false);
        return npc;
    }

    @Override
    public void performDefenceAnimation(final Entity attacker) {
        val blockDefinitions = combatDefinitions.getBlockDefinitions();
        val sound = blockDefinitions.getSound();
        if (sound != null) {
            if (attacker instanceof Player) {
                WorldTasksManager.schedule(() -> ((Player) attacker).sendSound(sound));
            }
        }
    }

    @Override
    public void autoRetaliate(final Entity source) {
    }

    @Override
    public boolean checkAggressivity() {
        return false;
    }

    @Override
    public boolean isPathfindingEventAffected() {
        return false;
    }

    @Override
    protected void removeHitpoints(final Hit hit) {
        super.removeHitpoints(hit);
        room.refreshHealthBar(raid);
    }

    @Override
    public void heal(final int amount) {
        super.heal(amount);
        room.refreshHealthBar(raid);
    }

    @Override
    public boolean isFreezeable() {
        return performingStompAttack;
    }

    @Override
    public double getRangedPrayerMultiplier() {
        return 0.75;
    }

    @Override
    public boolean isProjectileClipped(final Position target, final boolean closeProximity) {
        //TODO fix LoS
        return ProjectileUtils.isProjectileClipped(null, null, new Location(getX(), getY(), getPlane()), target, true) &&
                ProjectileUtils.isProjectileClipped(null, null, new Location(getX() + getSize(), getY() + getSize(), getPlane()), target, true) &&
                ProjectileUtils.isProjectileClipped(null, null, new Location(getX() + getSize(), getY(), getPlane()), target, true) &&
                ProjectileUtils.isProjectileClipped(null, null, new Location(getX(), getY() + getSize(), getPlane()), target, true);
    }

    @Override
    public boolean addWalkStep(int nextX, int nextY, int lastX, int lastY, boolean check) {
        if (isFrozen()) {
            return false;
        }
        return super.addWalkStep(nextX, nextY, lastX, lastY, check);
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        super.handleIngoingHit(hit);
        if (hasWalkSteps()) {
            hit.setDamage(hit.getDamage() / 2);
        }
    }

    @Override
    public void processNPC() {
        super.processNPC();

        val destination = pathStrategy.get(location);
        if (destination != null) {
            addWalkSteps(destination.getX(), destination.getY(), -1, false);
            previousCorner = destination;
        }

        if(getRaid().getParty().getTargetableMembers().size() < 1) {
            return;
        }

        if (room.isStarted()) {
            if (!performingStompAttack) {
                for (val m : raid.getParty().getTargetableMembers()) {
                    val member = RaidingParty.getPlayer(m);
                    if (member == null) {
                        continue;
                    }
                    if (ifPillarInPath(member)) {
                        continue;
                    }
                    World.sendProjectile(this, member, fliesProjectile);
                    WorldTasksManager.schedule(() -> fliesAttack(member), fliesProjectile.getTime(this, member));
                }
            }
        }
    }

    private void fliesAttack(final Player player) {
        delayHit(0, player, new Hit(this, Utils.random(1, 20), HitType.RANGED));
        player.setGraphics(fliesImpactGfx);
        player.sendSound(fliesSounds[Utils.random(fliesSounds.length - 1)]);
        for (val m : raid.getParty().getMembers()) {
            val member = RaidingParty.getPlayer(m);
            if (member == null || member == player || !member.getPosition().withinDistance(player.getPosition(), 1)) {
                continue;
            }
            delayHit(World.sendProjectile(this, player, fliesProjectile), player, new Hit(this, Utils.random(1, 20), HitType.RANGED));
            member.setGraphics(fliesImpactGfx);
            member.sendSound(fliesSounds[Utils.random(fliesSounds.length - 1)]);
            for (val p : raid.getParty().getPlayers()) {
                p.putBooleanAttribute("PerfectBloat", false);
            }
        }
    }

    public void inversePath(final int delay) {
        if (performingStompAttack) {
            return;
        }
        WorldTasksManager.schedule(() -> {
            pathStrategy = pathStrategy.inverse();

            if (previousCorner != null) {
                val next = pathStrategy.get(previousCorner);
                resetWalkSteps();
                addWalkSteps(next.getX(), next.getY(), -1, false);
                previousCorner = next;
            }
        }, delay);
    }

    public void stompAttack(final int delay) {
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                if (room.getPlayers().isEmpty() || isDead() || isFinished()) {
                    stop();
                    return;
                }
                if (ticks == 0) {
                    performingStompAttack = true;
                    resetWalkSteps();
                    freeze(32);
                    setAnimation(stompAnimation);
                    for (val p : raid.getParty().getPlayers()) {
                        p.getAttributes().put("bloatShutDowns", p.getNumericAttribute("bloatShutDowns").intValue() + 1);
                    }
                } else if (ticks == 30) {
                    for (val m : raid.getParty().getTargetableMembers()) {
                        val member = RaidingParty.getPlayer(m);
                        if (member == null) {
                            continue;
                        }
                        member.getPacketDispatcher().sendCameraShake(CameraShakeType.UP_AND_DOWN, 20, 5, 0);
                        if (ifPillarInPath(member)) {
                            continue;
                        }
                        delayHit(-1, member, new Hit(PestilentBloat.this, Math.min(Utils.random(40, 80), member.getHitpoints()), HitType.REGULAR));
                        for (val p : raid.getParty().getPlayers()) {
                            p.putBooleanAttribute("PerfectBloat", false);
                        }
                    }
                    combatDefinitions.resetStats();
                } else if (ticks == 33) {
                    for (val m : raid.getParty().getTargetableMembers()) {
                        val member = RaidingParty.getPlayer(m);
                        if (member == null) {
                            continue;
                        }
                        member.getPacketDispatcher().resetCamera();
                    }
                    if (previousCorner != null) {
                        addWalkSteps(previousCorner.getX(), previousCorner.getY(), -1, false);
                    }
                    performingStompAttack = false;
                    stompCount++;
                    if (stompCount == 2) {
                        setRun(true);
                    }
                    fallingMeatAttack();
                    inversePath((int) TimeUnit.SECONDS.toTicks(15));
                    stop();
                    return;
                }
                ticks++;
            }
        }, delay, 0);
    }

    public void fallingMeatAttack() {
        val cycles = (int) TimeUnit.SECONDS.toTicks(25); //TODO find out exact amount, depends on current hp or something

        WorldTasksManager.schedule(new TickTask() {
            List<Location> meatLocations;

            @Override
            public void run() {
                if (room.getPlayers().isEmpty() || isDead() || isFinished()) {
                    stop();
                    return;
                }
                if (ticks == cycles) {
                    stompAttack(0);
                    stop();
                    return;
                }

                if (ticks % 5 == 0) {
                    meatLocations = new ArrayList<>(16);

                    while (meatLocations.size() < 16) {
                        val point = room.getRandomMeatPoint();
                        if (!previousMeatLocations.contains(point) && !meatLocations.contains(point)) {
                            meatLocations.add(point);
                        }
                    }

                    for (val point : meatLocations) {
                        World.sendGraphics(new Graphics(1570 + Utils.random(3)), point);
                    }
                    previousMeatLocations = meatLocations;
                } else if (ticks % 5 == 3) {
                    for (val m : raid.getParty().getTargetableMembers()) {
                        val member = RaidingParty.getPlayer(m);
                        if (member == null) {
                            continue;
                        }
                        member.sendSound(fallingMeatSound);
                        for (val point : meatLocations) {
                            if (member.getLocation().matches(point)) {
                                delayHit(0, member, new Hit(PestilentBloat.this, Utils.random(30, 50), HitType.REGULAR));
                                for (val p : raid.getParty().getPlayers()) {
                                    p.putBooleanAttribute("PerfectBloat", false);
                                }
                                member.stun(3);
                                member.setGraphics(stunGfx);
                            }
                        }
                    }
                }
                ticks++;
            }
        }, 0, 0);
    }

    private boolean ifPillarInPath(Player player) {
        int lineOfSightPaths = 0;
        Location bloatLoc = getMiddleLocation();
        for (int diffx = -2; diffx < 3; diffx++) {
            for (int diffy = -2; diffy < 3; diffy++) {
                Location bloatTile = bloatLoc.transform(diffx,diffy,0);
                val tiles = Utils.calculateLine(player.getX(),player.getY(),bloatTile.getX(),bloatTile.getY(),0);
                boolean isValidPath = true;
                for (Location lineTile : tiles) {
                    if (lineTile.getX() >= room.getX(3293) && lineTile.getX() <= room.getX(3298)) {
                        if (lineTile.getY() >= room.getY(4445) && lineTile.getY() <= room.getY(4450)) {
                            isValidPath = false;
                            break;
                        }
                    }
                }
                if (isValidPath) {
                    lineOfSightPaths++;
                }
            }
        }
        return !(lineOfSightPaths > 6);
    }

    @Override
    public void finish() {
        for (val p : room.getPlayers()) {
            if (!raid.getSpectators().contains(p.getUsername())) {
                if (p.getNumericAttribute("bloatShutDowns").intValue() < 3
                        && !p.getBooleanAttribute("master-combat-achievement57")) {
                    p.putBooleanAttribute("master-combat-achievement57", true);
                    MasterTasks.sendMasterCompletion(p, 57);
                }
                if (p.getBooleanAttribute("PerfectBloat")
                        && !p.getBooleanAttribute("master-combat-achievement59")) {
                    p.putBooleanAttribute("master-combat-achievement59", true);
                    MasterTasks.sendMasterCompletion(p, 59);
                }
            }
            p.getPacketDispatcher().resetCamera();
        }
        super.finish();
        if(getRaid().getParty().getTargetablePlayers().size() > 0) {
            room.onCompletion();
        }
    }

    @Override
    public int attack(final Entity target) {
        return 0;
    }
}
