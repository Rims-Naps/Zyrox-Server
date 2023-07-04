package com.zenyte.game.content.theatreofblood.boss.nylocas;

import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.TheatreRoom;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.content.theatreofblood.boss.nylocas.model.*;
import com.zenyte.game.content.theatreofblood.boss.nylocas.npc.Nylocas;
import com.zenyte.game.content.theatreofblood.boss.nylocas.npc.NylocasVasilias;
import com.zenyte.game.content.theatreofblood.boss.nylocas.npc.PillarSupport;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.content.theatreofblood.shared.HealthBarType;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.HitProcessPlugin;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import lombok.Getter;
import lombok.Setter;
import lombok.val;


import java.util.*;

/**
 * @author Tommeh | 6/5/2020 | 9:34 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class NylocasRoom extends TheatreArea implements HitProcessPlugin {

    private static final SoundEffect blockedSound = new SoundEffect(2277);

    @Getter
    private final Map<Spawn, Nylocas> nylos;
    @Getter
    private final Map<PillarLocation, PillarSupport> pillars;

    @Setter @Getter
    private NylocasPhase phase;
    @Getter @Setter
    private WaveDefinition wave;
    private NylocasVasilias boss;
    @Getter
    private RSPolygon platform;
    private int ticks;


    public NylocasRoom(final TheatreOfBloodRaid raid, final AllocatedArea area, final TheatreRoom room) {
        super(raid, area, room);
        phase = NylocasPhase.MINIONS;
        wave = WaveDefinition.WAVE_1;
        boss = new NylocasVasilias(this);
        nylos = new HashMap<>();
        pillars = new HashMap<>(4);
        for (val location : PillarLocation.values) {
            pillars.put(location, new PillarSupport(this, location));
        }
        platform = new RSPolygon(new int[][]{
                { getX(3290), getY(4253) },
                { getX(3290), getY(4245) },
                { getX(3292), getY(4245) },
                { getX(3292), getY(4243) },
                { getX(3300), getY(4243) },
                { getX(3300), getY(4245) },
                { getX(3302), getY(4245) },
                { getX(3302), getY(4253) },
                { getX(3300), getY(4253) },
                { getX(3300), getY(4255) },
                { getX(3292), getY(4255) },
                { getX(3292), getY(4253) }
        });
    }

    @Override
    public void onStart(final Player player) {
        pillars.forEach((location, pillar) -> {
            pillar.spawn();
            pillar.setHitpoints(pillar.getMaxHitpoints());
            World.spawnObject(pillar.getObject());
        });

    }

    @Override
    public void process() {
        if (!isStarted()) {
            return;
        }
        super.process();
        if (ticks >= 8) {
            if (ticks % 4 == 0) {
                if (phase == NylocasPhase.MINIONS && wave != WaveDefinition.WAVE_31) {
                    var cap = wave.getNylocasCap();

                    if (raid.getParty().getSize() <= 2) {
                        cap = (cap == 12 ? 7 : 15);
                    }

                    if (nylos.size() < cap) {
                        for (val m : raid.getParty().getMembers()) {
                            val member = RaidingParty.getPlayer(m);
                            if (member == null) {
                                continue;
                            }
                            //member.sendMessage("[TOB] Wave " + wave.getWave());
                        }
                        spawnNylos(wave.getEast(), SegmentType.EAST);
                        spawnNylos(wave.getWest(), SegmentType.WEST);
                        spawnNylos(wave.getSouth(), SegmentType.SOUTH);

                        wave = wave.getNext();
                    }
                } else if (phase == NylocasPhase.BOSS) {
                    boss.spawn();
                    boss.setAnimation(new Animation(8075));
                    boss.lock();
                    refreshHealthBar(raid);

                    WorldTasksManager.schedule(() -> {
                        boss.unlock();
                        boss.setAnimation(Animation.STOP);
                        boss.setTransformation(NylocasType.MELEE.getIds()[2]);
                        boss.setAnimation(Animation.STOP);
                        boss.setTarget(raid.getParty().getRandomPlayer());
                        boss.getCombat().setCombatDelay(4);
                    }, 4);
                    phase = null;
                }
            }

            for (val entry : nylos.entrySet()) {
                val spawn = entry.getKey();
                val nylo = entry.getValue();

                if (!platform.contains(nylo.getLocation())) {
                    continue;
                }

                if (nylo.hasWalkSteps() && !spawn.isAggressive()) {
                    val lastWalkTile = nylo.getLastWalkTile();
                    val destination = new Location(lastWalkTile[0], lastWalkTile[1], nylo.getPlane());
                    if (isOccupied(destination)) {
                        val tiles = new Location[4];
                        val corners = spawn.getTarget().getCorners();
                        var corner = getClosestCorner(nylo, spawn.getTarget());
                        tiles[0] = getLocation(corner.getPrimary());
                        tiles[1] = getLocation(corner.getSecondary());


                        corner = corners[0] == corner ? corners[1] : corners[0];
                        tiles[2] = getLocation(corner.getPrimary());
                        tiles[3] = getLocation(corner.getSecondary());


                        nylo.resetWalkSteps();
                        for (val tile : tiles) {
                            if (isOccupied(tile)) {
                                continue;
                            }
                            if (nylo.isLarge()) {
                                if (isLargeSpotBlocked(tile)) {
                                    continue;
                                }
                            }
                            nylo.addWalkSteps(tile.getX(), tile.getY(), -1, false);
                            return;
                        }
                        /*
                        //roam around TODO
                        val middle = getLocation(3295, 4248, 0);
                        nylo.addWalkSteps(middle.getX(), middle.getY(), -1, false);*/
                    }
                } else {
                    if (!spawn.isAggressive()) {
                        val currentTarget = nylo.getCombat().getTarget();
                        val pillar = pillars.get(spawn.getTarget());
                        if (pillar != null) {
                            nylo.setTarget(pillar);
                        } else if (currentTarget == null) {
                            nylo.setTarget(raid.getParty().getRandomPlayer());
                        }
                    }
                }
            }
        }
        ticks++;
    }

    @Override
    public boolean inCombatZone(int x, int y) {
        return inZone(3290, 4243, 3301, 4254, x, y);
    }

    @Override
    public boolean hit(final Player source, final Entity target, final Hit hit, final float modifier) {
        if (source == null) {
            return true;
        }
        if (target instanceof Nylocas) {
            val nylo = (Nylocas) target;

            if (nylo.isImmune(source, hit)) {
                if (!nylo.getImmunity().contains(source.getUsername())) {
                    nylo.getImmunity().add(source.getUsername());
                }
                hit.setDamage(0);
                source.sendSound(blockedSound);
                for (val p : raid.getParty().getPlayers()) {
                    p.putBooleanAttribute("PerfectNylocas", false);
                }
                return false;
            }
        }

        if (target instanceof NylocasVasilias) {
            val boss = (NylocasVasilias) target;
            val type = boss.getType();

            if (type != null && !hit.getHitType().equals(type.getAcceptableHitType())) {
                boss.heal(hit.getDamage());
                boss.applyHit(new Hit(hit.getDamage(), HitType.HEALED));
                boss.delayHit(0, source, new Hit(boss, hit.getDamage(), HitType.REGULAR));

                hit.setPredicate(h -> true);
                return false;
            } else {
                val player = (Player) hit.getSource();
                if (player.getAttributes().containsKey("nylobossdamage")) {
                    player.getAttributes().put("nylobossdamage", (player.getNumericAttribute("nylobossdamage").intValue() + hit.getDamage()));
                } else {
                    player.getAttributes().put("nylobossdamage", hit.getDamage());
                }
            }
        }
        return true;
    }

    private PillarSupport getNextPillar() {
        val list = new ArrayList<>(pillars.values());
        Collections.shuffle(list);
        for (val pillar : list) {
            if (pillar.isFinished()) {
                continue;
            }
            return pillar;
        }
        return null;
    }

    private void transform(final Spawn spawn, final Nylocas nylocas) {
        val transformations = spawn.getTransformations();
        if (transformations == null || transformations.isEmpty()) {
            return;
        }

        val nextTransformation = transformations.get(0);

        val id = nextTransformation.getIds()[nylocas.isLarge() ? 1 : 0];
        nylocas.setTransformation(id);
        transformations.remove(0);
    }

    private void spawnNylos(final Segment segment, final SegmentType type) {
        try {
            val spawns = segment.getSpawns();
            for (int index = 0; index < spawns.size(); index++) {

                val spawn = new Spawn(spawns.get(index));
                val location = spawns.size() == 1 ? spawn.isLarge() ? type.getBigNylocasSpawn() : type.getSingleNylocasSpawn(wave) : type.getLocations()[index];
                val nylocas = spawn.getType().getClazz().getDeclaredConstructor(Nylocas.params).newInstance(this, getLocation(location), type.getDirection(), spawn);

                nylocas.setSegment(type);
                nylocas.spawn();
                nylos.put(spawn, nylocas);

                nylocas.pathStraightLine(type);

                WorldTasksManager.schedule(new TickTask() {

                    @Override
                    public void run() {
                        switch (ticks++) {
                            case 4:
                            case 6:
                                transform(spawn, nylocas);
                                stop();
                                break;
                        }
                    }
                }, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PillarCorner getClosestCorner(final Nylocas nylocas, final PillarLocation pillar) {
        var distance = Integer.MAX_VALUE;
        PillarCorner corner = null;
        for (val c : pillar.getCorners()) {
            val loc = getLocation(c.getPrimary());
            val d = loc.getTileDistance(nylocas.getLocation());
            if (d < distance) {
                distance = d;
                corner = c;
            }
        }
        return corner;
    }

    public boolean isLargeSpotBlocked(Location loc) {
        return !(getPlatform().contains(loc.transform(1,0,0)) && getPlatform().contains(loc.transform(0,1,0)) && getPlatform().contains(loc.transform(1,1,0)));
    }

    public boolean isOccupied(final Location location) {
        for (val entry : nylos.entrySet()) {
            val nylo = entry.getValue();
            if (nylo.getLocation().matches(location)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getCurrentHitpoints() {
        if (phase == NylocasPhase.BOSS || phase == null) {
            return boss.getHitpoints();
        }
        var total = 0;
        for (val entry : pillars.entrySet()) {
            val pillar = entry.getValue();
            total += pillar.getHitpoints();
        }
        return total;
    }

    @Override
    public int getMaximumHitpoints() {
        if (phase == NylocasPhase.BOSS || phase == null) {
            return boss.getMaxHitpoints();
        }
        var total = 0;
        for (val entry : pillars.entrySet()) {
            val pillar = entry.getValue();
            total += pillar.getMaxHitpoints();
        }
        return total;
    }

    @Override
    public TheatreRoom onAdvancement() {
        var MVP = (Player) raid.getParty().getLeader();
        int MVPDamage;
        for (val p : raid.getParty().getPlayers()) {
            MVPDamage = MVP.getNumericAttribute("nylobossdamage").intValue();
            int PlayerDamage = p.getNumericAttribute("nylobossdamage").intValue();
            if (PlayerDamage > MVPDamage) {
                MVP = p;
            }
        }
        if (MVP != null) {
            if (MVP.getAttributes().containsKey("tobpoints")) {
                MVP.getAttributes().put("tobpoints", MVP.getNumericAttribute("tobpoints").intValue() + 1);
            } else {
                MVP.sendMessage("You did not have the attribute for points.");
            }
        } else {
            raid.getParty().getLeader().sendMessage("MVP was null.");
        }
        val nextRoom = TheatreRoom.SOTETSEG;
        raid.removeRoom(getRoom());
        raid.addRoom(TheatreRoom.SOTETSEG);
        return nextRoom;
    }

    @Override
    public Location getEntranceLocation() {
        return getLocation(3295, 4283, 0);
    }

    @Override
    public WorldObject getVyreOrator() {
        return new WorldObject(ObjectId.VYRE_ORATOR, 11, 0, getLocation(3296, 4262, 0));
    }

    @Override
    public WorldObject getRefillChest() {
        return null;
    }

    @Override
    public Location getSpectatingLocation() {
        return getLocation(3290, 4257, 0);
    }

    @Override
    public Location[] getJailLocations() {
        return new Location[]{ getLocation(3287, 4254, 0), getLocation(3290, 4257, 0),
                               getLocation(3301, 4257, 0), getLocation(3304, 4254, 0)};
    }

    @Override
    public Direction[] getJailFacingDirections() {
        return new Direction[] {Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH, Direction.SOUTH_WEST};
    }

    @Override
    public Optional<TheatreNPC<? extends TheatreArea>> getBoss() {
        return Optional.of(boss);
    }

    @Override
    public boolean isEnteringBossRoom(final WorldObject barrier, final Player player) {
        return player.getY() > barrier.getY();
    }

    @Override
    public HealthBarType getHealthBarType() {
        return HealthBarType.REGULAR;
    }

    @Override
    public String name() {
        return "The Nylocas";
    }

    @Override
    public void enterBossRoom(WorldObject barrier, Player player) {
        player.getAttributes().put("tobpoints", player.getNumericAttribute("tobpoints").intValue() + 3);
        if (!isStarted()) {
            setDuration(0);
            boss.getTemporaryAttributes().put("start", System.currentTimeMillis());
        }
        super.enterBossRoom(barrier, player);
    }

    @Override
    public void onCompletion() {
        setDuration((int) TimeUnit.MILLISECONDS.toTicks(System.currentTimeMillis() - ((long) boss.getTemporaryAttributes().get("start"))));
        super.onCompletion();
    }

    @Override
    public Location getRespawnLocation() {
        return getLocation(3295, 4249, 0);
    }
}
