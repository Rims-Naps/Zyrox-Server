package com.zenyte.game.content.theatreofblood.boss.maidenofsugadinti.npc;

import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.content.theatreofblood.boss.maidenofsugadinti.MaidenOfSugadintiRoom;
import com.zenyte.game.content.theatreofblood.boss.maidenofsugadinti.object.BloodTrail;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.player.Bonuses;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.utils.Ordinal;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import lombok.var;

import java.util.Collections;
import java.util.List;

/**
 * @author Corey
 * @since 26/05/2020
 */
public class MaidenOfSugadinti extends TheatreNPC<MaidenOfSugadintiRoom> implements CombatScript {
    
    /**
     * Spawn point of the Maiden.
     */
    public static final Location spawnLocation = new Location(3162, 4444);
    private static final Animation stormAttackAnimation = new Animation(8092);
    private static final Animation bloodAttackAnimation = new Animation(8091);
    private static final Projectile stormProjectile = new Projectile(1577, 0, 10, 120, 0, 50, 0, 0);
    private static final Projectile bloodProjectile = new Projectile(1578, 135, 20, 20, 11, -15, 0, 15);
    private static final Graphics bloodSplatGraphic = new Graphics(1579);
    private static final Bonuses.Bonus[] bonusesToCheck = new Bonuses.Bonus[]{Bonuses.Bonus.ATT_STAB, Bonuses.Bonus.ATT_SLASH, Bonuses.Bonus.ATT_CRUSH, Bonuses.Bonus.ATT_MAGIC, Bonuses.Bonus.ATT_RANGED};
    private static final SoundEffect bloodSplatSound = new SoundEffect(3547, 2);
    private static final SoundEffect stormAttackHitSound = new SoundEffect(176);
    
    private static final Animation deathAnimationPartOne = new Animation(8093);
    private static final Animation deathAnimationPartTwo = new Animation(8094);
    
    /**
     * Possible spawn locations for Nylocas Matomenos.
     */
    private static final ObjectList<Location> nylocasSpawnLocations = new ObjectArrayList<>(new Location[]{
            new Location(3174, 4457),
            new Location(3178, 4457),
            new Location(3182, 4457),
            new Location(3186, 4457),
            new Location(3186, 4455),
            
            new Location(3186, 4437),
            new Location(3186, 4435),
            new Location(3182, 4435),
            new Location(3178, 4435),
            new Location(3174, 4435),
    });
    
    /**
     * Location of each 'direction' the Maiden checks in order to find the nearest player.
     * <br>
     * Order is important.
     * <br>
     * Based on static map positions, not to be used for calculations.
     */
    private static final ObjectList<Location> stormDirectionPoints = new ObjectArrayList<>(new Location[]{
            new Location(3167, 4446), // East
            new Location(3165, 4449), // North
            new Location(3165, 4444), // South
            new Location(3162, 4447) // West
    });
    
    private static final int MAX_HIT = 35;
    private static final int BLOOD_SPAWN_MAX_COUNT = 8;
    
    private static final int BLOOD_ATTACK_CHANCE = 4;
    private static final int TICKS_PER_ATTACK = 10;
    private static final int BLOOD_SPLAT_MAX_HIT = 10;
    private static final int BLOOD_SPLAT_MIN_HIT = 5;
    
    private final IntSet nylocasPhases = new IntArraySet();
    private final ObjectList<Location> stormDirections = new ObjectArrayList<>(stormDirectionPoints.size());
    private final IntSet bloodSplatTargets = new IntArraySet(); // where projectiles are about to hit
    private final IntSet bloodSplatTiles = new IntArraySet(); // where persistent splats are
    @Getter
    private final List<BloodTrail> bloodTrails = new ObjectArrayList<>();
    private final List<NylocasMatomenos> nylocasSpawns = new ObjectArrayList<>();
    private final List<BloodSpawn> bloodSpawns = new ObjectArrayList<>();
    private int ticks;
    private Phase currentPhase = Phase.FIRST;
    private double maxHit = MAX_HIT;
    private double maxBloodSplatHit = BLOOD_SPLAT_MAX_HIT;
    private boolean hasUsedStormAttack;
    private boolean canRollBloodAttack = true; // can only roll blood attack after 2 storm attacks
    
    public MaidenOfSugadinti(final MaidenOfSugadintiRoom room) {
        super(room.getRaid(), room, Phase.FIRST.getNpcId(), room.getLocation(spawnLocation), Direction.EAST);
        maxDistance = 0;
        radius = 0;
    
        for (val point : stormDirectionPoints) {
            stormDirections.add(getRoom().getLocation(point));
        }
    }
    
    @Override
    protected void setStats() {
        if (currentPhase != Phase.FIRST) {
            return;
        }
        super.setStats();
    }
    
    @Override
    public void setTransformation(final int id) {
        nextTransformation = id;
        setId(id);
        size = definitions.getSize();
        updateFlags.flag(UpdateFlag.TRANSFORMATION);
    }
    
    @Override
    protected void removeHitpoints(Hit hit) {
        super.removeHitpoints(hit);
        room.refreshHealthBar(raid);
    }
    
    @Override
    public void heal(int amount) {
        super.heal(amount);
        room.refreshHealthBar(raid);
    }
    
    private void hitsplatHeal(final int amount) {
        if (getHitpoints() == getMaxHitpoints()) {
            return;
        }
        applyHit(new Hit(amount, HitType.HEALED));
    }
    
    @Override
    public void processNPC() {
        super.processNPC();

        if (!canProcess()) {
            return;
        }

        ticks++;

        handleBloodSplats();
    
        if (ticks % TICKS_PER_ATTACK == 0) {
            if (canRollBloodAttack && Utils.random(Utils.SECURE_RANDOM, 1, BLOOD_ATTACK_CHANCE) == 1) {
                bloodAttack();
                canRollBloodAttack = false;
                hasUsedStormAttack = false;
            } else {
                stormAttack();
            }
        }
    
        val newPhase = Phase.getPhase(this);
    
        if (currentPhase != newPhase) {
            currentPhase = newPhase;
            if (currentPhase == Phase.DYING) {
                sendDeath();
            } else {
                changePhase();
            }
        }
    
        nylocasSpawns.removeIf(nylocas -> nylocas.isDying() || nylocas.isDead());
        bloodSpawns.removeIf(NPC::isDead);
    }
    
    protected void absorbNylocas(final NylocasMatomenos nylocas) {
        if (!canProcess()) {
            return;
        }
        maxHit += 1.8;
        maxBloodSplatHit += 1.8;
        hitsplatHeal(Math.min(nylocas.getHitpoints() * 2, 200));
        for (val p : room.getPlayers()) {
            p.putBooleanAttribute("PerfectMaiden", false);
        }
    }
    
    private void changePhase() {
        setTransformation(currentPhase.getNpcId());
        
        if (nylocasPhases.contains(currentPhase.getPercent()) || !currentPhase.hasNylocasWave) {
            // each Nylocas phase only happens once, even if the Maiden heals
            return;
        }
        
        nylocasPhases.add(currentPhase.getPercent());
        spawnNylocas();
    }
    
    private void spawnNylocas() {
        val amount = getRaid().getParty().getSize() * 2;
        val spawns = new ObjectArrayList<>(nylocasSpawnLocations);
        Collections.shuffle(spawns, Utils.SECURE_RANDOM);
        
        for (int i = 0; i < amount; i++) {
            nylocasSpawns.add((NylocasMatomenos) new NylocasMatomenos(this, spawns.get(i)).spawn());
        }
    }
    
    private void stormAttack() {
        // used to ensure blood attacks can only be rolled after 2 successful storm attacks
        if (hasUsedStormAttack && !canRollBloodAttack) {
            canRollBloodAttack = true;
            hasUsedStormAttack = false;
        } else {
            hasUsedStormAttack = true;
        }

        val toAttack = getNearestPlayer();
        if(toAttack == null) {
            return;
        }
        faceEntity(toAttack);
        setAnimation(stormAttackAnimation);
        val delay = World.sendProjectile(this, toAttack, stormProjectile);
        val hit = new Hit(this, getRandomMaxHit(this, (int) maxHit, AttackType.MAGIC, toAttack), HitType.MAGIC);
        delayHit(delay - 1, toAttack, hit);
        if (!toAttack.getPrayerManager().isActive(Prayer.PROTECT_FROM_MAGIC)) {
            for (val p : room.getPlayers()) {
                p.putBooleanAttribute("PerfectMaiden", false);
            }
        }
        // 1/3 chance to drain highest stat if damage > 0
        // https://twitter.com/JagexAsh/status/1265697130959831042
        if (hit.getDamage() > 0 && Utils.random(Utils.SECURE_RANDOM, 2) == 0) {
            drainStats(toAttack, hit.getDamage());
        }
        toAttack.sendSound(new SoundEffect(stormAttackHitSound.getId(), 0, Math.max(0, (delay - 1) * 30)));
        WorldTasksManager.schedule(() -> faceEntity(toAttack), 3);
    }
    
    private void drainStats(final Player player, final int damage) {
        val drainAmount = (int) Math.min(Math.ceil(damage / 4.0), 5);
        
        Bonuses.Bonus highestBonus = bonusesToCheck[0];
        int highestBonusAmount = 0;
        
        for (val bonus : bonusesToCheck) {
            val bonusAmount = player.getBonuses().getBonus(bonus);
            if (bonusAmount > highestBonusAmount) {
                highestBonus = bonus;
                highestBonusAmount = bonusAmount;
            }
        }
        
        switch (highestBonus) {
            case ATT_SLASH:
            case ATT_CRUSH:
            case ATT_STAB:
                player.drainSkill(Skills.ATTACK, drainAmount);
                player.drainSkill(Skills.STRENGTH, drainAmount);
                break;
            case ATT_RANGED:
                player.drainSkill(Skills.RANGED, drainAmount);
                break;
            case ATT_MAGIC:
                player.drainSkill(Skills.MAGIC, drainAmount);
                break;
        }
        
        player.sendFilteredMessage("Your stats are drained.");
    }
    
    private Player getNearestPlayer() {
        // find closest player and attack them
        // E N S W = priority order (if multiple people are stood within the same distance)
        // priority by party order if multiple people stood in same direction
        
        var shortestDistance = Integer.MAX_VALUE;
        Player nearestPlayer = null;
        if(getRaid().getParty().getTargetableMembers().size() > 0) {
            for (val direction : stormDirections) {
                for (val m : getRaid().getParty().getTargetableMembers()) {
                    val member = RaidingParty.getPlayer(m);
                    if (member == null) {
                        continue;
                    }
                    val distance = direction.getTileDistance(member.getLocation());
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        nearestPlayer = member;
                    }
                }
            }
        }

        return nearestPlayer;
    }
    
    private void bloodAttack() {
        bloodSplatTargets.clear();
        setAnimation(bloodAttackAnimation);
    
        val locations = new ObjectArrayList<Location>(getRaid().getParty().getSize());
        for (val m : getRaid().getParty().getTargetableMembers()) {
            val member = RaidingParty.getPlayer(m);
            if (member == null) {
                continue;
            }
            locations.add(new Location(member.getLocation()));
        }

        if(getRaid().getParty().getTargetablePlayers().size() == 0) {
            return;
        }
        val randomUsername = Utils.getRandomCollectionElement(getRaid().getParty().getTargetableMembers());
        val randomPlayer = RaidingParty.getPlayer(randomUsername);

        if (randomPlayer == null) {
            return;
        }
        faceEntity(randomPlayer);

        for (val loc : locations) {
            fireBloodProjectile(loc);
        }

        WorldTasksManager.schedule(() -> {
            if (!canProcess()) {
                return;
            }
            val amount = Utils.random(1, 2);
            for (int i = 0; i < amount; i++) {
                fireBloodProjectile(randomPlayer.getLocation().random(2));
            }
        });
    
    }
    
    private void fireBloodProjectile(final Location tile) {
        if (!bloodSplatTargets.contains(tile.getPositionHash())
                    && !splatExists(tile)
                    && World.canPlaceObjectWithoutCollisions(tile, 10)
                    && World.isFloorFree(tile, 1)) {
            bloodSplatTargets.add(tile.getPositionHash());
        } else {
            return;
        }
    
        val dir = Utils.getFaceDirection(getMiddleLocation().getX() - tile.getX(), getMiddleLocation().getY() - tile.getY());
        Direction d = Direction.WEST;
    
        for (Direction value : Direction.orderedByDirectionValue) {
            if (dir <= value.getDirection()) {
                d = value;
                break;
            }
        }
    
        val delay = World.sendProjectile(this, tile.transform(d, 1), bloodProjectile);
    
        WorldTasksManager.schedule(() -> {
            if (!canProcess()) {
                return;
            }
        
            addSplat(tile);
            World.sendSoundEffect(tile, bloodSplatSound);
            World.sendGraphics(bloodSplatGraphic, tile);
        
            boolean attackHitTemp = false;
            for (val m : getRaid().getParty().getMembers()) {
                val member = RaidingParty.getPlayer(m);
                if (member == null) {
                    continue;
                }
                if (member.getLocation().getPositionHash() == tile.getPositionHash()) {
                    attackHitTemp = true;
                    break;
                }
            }
        
            val attackHit = attackHitTemp;
            WorldTasksManager.schedule(() -> rollBloodSpawn(tile, attackHit), 9);
            WorldTasksManager.schedule(() -> removeSplat(tile), 10);
        }, Math.max(delay - 1, 0));
    }
    
    private void rollBloodSpawn(final Location tile, final boolean hit) {
        if (bloodSpawns.size() >= BLOOD_SPAWN_MAX_COUNT) {
            return;
        }
    
        if (!canProcess()) {
            return;
        }
    
        val chance = hit ? 5 : 10;
    
        if (Utils.random(Utils.SECURE_RANDOM, 1, chance) == 1) {
            bloodSpawns.add((BloodSpawn) new BloodSpawn(this, tile).spawn());
        }
    }
    
    private void handleBloodSplats() {
        if (bloodSplatTiles.isEmpty()) {
            return;
        }
    
        for (val m : getRaid().getParty().getTargetableMembers()) {
            val member = RaidingParty.getPlayer(m);
            if (member == null) {
                continue;
            }
            if (bloodSplatTiles.contains(member.getLocation().getPositionHash())) {
                val damage = Utils.random(Utils.SECURE_RANDOM, BLOOD_SPLAT_MIN_HIT, (int) maxBloodSplatHit);
    
                member.applyHit(new Hit(damage, HitType.REGULAR));
                member.getPrayerManager().drainPrayerPoints(damage / 2);
                hitsplatHeal(damage);
                for (val p : room.getPlayers()) {
                    p.putBooleanAttribute("CantDrainThis", false);
                    p.putBooleanAttribute("PerfectMaiden", false);
                }
            }
        }
    
        if (!bloodTrails.isEmpty()) {
            bloodTrails.removeIf(trail -> !trail.process());
        }
    }
    
    protected boolean splatExists(final Location tile) {
        return bloodSplatTiles.contains(tile.getPositionHash());
    }
    
    public void addSplat(final Location tile) {
        bloodSplatTiles.add(tile.getPositionHash());
    }
    
    public void removeSplat(final Location tile) {
        bloodSplatTiles.remove(tile.getPositionHash());
    }
    
    private boolean canProcess() {
        return !dead() && getRaid().getParty().getTargetablePlayers().size() != 0;
    }
    
    @Override
    public void sendDeath() {
        setPhase(Phase.DYING);
    
        bloodTrails.forEach(BloodTrail::remove);
        bloodTrails.clear();
        bloodSplatTiles.clear();
        bloodSplatTargets.clear();
    
        bloodSpawns.forEach(b -> b.setHitpoints(0));
        nylocasSpawns.forEach(n -> n.setHitpoints(0));
        bloodSpawns.clear();
        nylocasSpawns.clear();
    
        setAnimation(deathAnimationPartOne);
    
        WorldTasksManager.schedule(() -> {
            setPhase(Phase.DEAD);
            setAnimation(deathAnimationPartTwo);
            WorldTasksManager.schedule(this::finish, 2);
        }, 2);
        
    }
    
    @Override
    public int attack(Entity target) {
        return 0;
    }
    
    @Override
    public double getMagicPrayerMultiplier() {
        return 0.5;
    }
    
    @Override
    public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check) {
        return false;
    }
    
    @Override
    public void finish() {
        for (val p : room.getPlayers()) {
            if (!raid.getSpectators().contains(p.getUsername())) {
                if (p.getBooleanAttribute("CantDrainThis")
                        && !p.getBooleanAttribute("master-combat-achievement64")) {
                    p.putBooleanAttribute("master-combat-achievement64", true);
                    MasterTasks.sendMasterCompletion(p, 64);
                }
                if (p.getBooleanAttribute("PerfectMaiden")
                        && !p.getBooleanAttribute("master-combat-achievement58")) {
                    p.putBooleanAttribute("master-combat-achievement58", true);
                    MasterTasks.sendMasterCompletion(p, 58);
                }
            }
        }
        super.finish();
        if(getRaid().getParty().getTargetablePlayers().size() > 0) {
            room.onCompletion();
        }
    }
    
    @Override
    public boolean checkProjectileClip(final Player player) {
        return false;
    }
    
    private void setPhase(final Phase phase) {
        currentPhase = phase;
        setTransformation(currentPhase.getNpcId());
    }
    
    public boolean dead() {
        return currentPhase == Phase.DYING || currentPhase == Phase.DEAD;
    }
    
    @AllArgsConstructor
    @Getter
    @Ordinal
    public enum Phase {
        FIRST(100, NpcId.THE_MAIDEN_OF_SUGADINTI),
        SECOND(70, NpcId.THE_MAIDEN_OF_SUGADINTI_8361, true),
        THIRD(50, NpcId.THE_MAIDEN_OF_SUGADINTI_8362, true),
        FOURTH(30, NpcId.THE_MAIDEN_OF_SUGADINTI_8363, true),
        DYING(0, NpcId.THE_MAIDEN_OF_SUGADINTI_8364),
        DEAD(0, NpcId.THE_MAIDEN_OF_SUGADINTI_8365);
        
        private static final Phase[] phases = values();
        private final int percent;
        private final int npcId;
        private final boolean hasNylocasWave;
        
        Phase(final int percent, final int npcId) {
            this(percent, npcId, false);
        }
        
        public static Phase getPhase(final MaidenOfSugadinti maiden) {
            val healthPercentage = maiden.getHitpointsAsPercentage();
            
            for (int i = phases.length - 1; i >= 0; i--) {
                val phase = phases[i];
                if (healthPercentage <= phase.getPercent()) {
                    return phase;
                }
            }
    
            return DYING;
        }
        
    }
    
}
