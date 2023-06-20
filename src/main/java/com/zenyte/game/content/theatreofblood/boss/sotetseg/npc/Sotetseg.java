package com.zenyte.game.content.theatreofblood.boss.sotetseg.npc;

import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.content.theatreofblood.TheatreRoom;
import com.zenyte.game.content.theatreofblood.boss.sotetseg.ShadowRealmArea;
import com.zenyte.game.content.theatreofblood.boss.sotetseg.SotetsegRoom;
import com.zenyte.game.content.theatreofblood.interfaces.PartyOverlayInterface;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArraySet;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Corey
 * @since 10/06/2020
 */
@Slf4j
public class Sotetseg extends TheatreNPC<SotetsegRoom> implements CombatScript {
    
    public static final String SOTETSEG_PRAYER_DISABLE_ATTRIBUTE_KEY = "tob_sotetseg_prayer_disable";
    
    private static final int MELEE_MAX_HIT = 50;
    private static final int ATTACK_SPEED = 5;
    private static final int BOMB_BASE_DAMAGE = 70;
    private static final int BOMB_EXTRA_DAMAGE_PER_PLAYER = 12;
    
    private static final Animation meleeAttackAnimation = new Animation(8138);
    private static final Animation magicAttackAnimation = new Animation(8139);
    
    private static final Graphics bombHitGraphic = new Graphics(1605);
    private static final Projectile bombProjectile = new Projectile(1604, 40, 30, 0, 0, 15 * 30, 0, 0);
    
    private final DoubleSet mazePhases = new DoubleArraySet(ShadowRealmPhase.phases.length);
    private final ArrayList<Location> mazeTiles = new ArrayList<>();
    private final ObjectOpenHashSet<Location> damageTiles = new ObjectOpenHashSet<>(4);
    @Setter
    private boolean started;
    private int magicAttackCount;
    @Getter
    private boolean mazePhase;
    @Getter
    @Nullable
    private ShadowRealmArea shadowRealm;
    private boolean mazeStormActive;
    private RedStorm mazeStorm;
    private Location activeMazeTile;
    private int ticks = 0;
    private boolean startCombat = false;
    
    public Sotetseg(final SotetsegRoom room) {
        super(room.getRaid(), room, NpcId.SOTETSEG_8388, room.getSotetsegSpawnLocation(), Direction.SOUTH);
        setForceMultiArea(true);
        setAttackDistance(10);
    }

    @Override
    public void processNPC() {
        super.processNPC();

        if (!started) {
            return;
        }
        
        if (mazePhase) {
            mazePhaseProcess();
            return;
        }
        
        if (ShadowRealmPhase.shouldInitiate(this)) {
            commenceMazePhase();
            return;
        }


        if(getRaid().getParty().getTargetablePlayers().size() == 0) {
            return;
        }

        Player target = Utils.getRandomCollectionElement(getRaid().getParty().getTargetablePlayers());
        if (target == null) {
            return;
        }

        if(ticks == 0) {
            faceEntity(target);
            setTarget(target);
            ticks = attackTarget(target);
        } else {
            ticks--;
        }
    }

    public boolean mazeComplete() {
        if(getRaid().getParty().getAlivePlayers().size() == 1) {
            if(shadowRealm != null && shadowRealm.getPlayer() != null) {
                return false;
            }
        }
        for(Player p : getRaid().getParty().getAlivePlayers()) {
            if(p.getY() <= getRoom().getMazeTopLeft().getY()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int attack(final Entity target) {
        return 0;
    }

    public int attackTarget(final Entity target) {
        if (!(target instanceof Player) || target == null) {
            return 0;
        }
        val meleeAttack = Utils.random(1, isWithinMeleeDistance(this, target) ? 2 : 1) == 1;

        if (meleeAttack) {
            if(!isWithinMeleeDistance(this, target)) {
                return performMagicAttack((Player) target);
            } else {
                return performMeleeAttack((Player) target);
            }
        } else {
            return performMagicAttack((Player) target);
        }
    }
    
    private int performMeleeAttack(final Player target) {
        setAnimation(meleeAttackAnimation);
        delayHit(1, target, new Hit(this, getRandomMaxHit(this, MELEE_MAX_HIT, CRUSH, target), HitType.MELEE));
        if (!target.getPrayerManager().isActive(Prayer.PROTECT_FROM_MELEE)) {
            for (val p : room.getPlayers()) {
                p.putBooleanAttribute("PerfectSotetseg", false);
            }
        }
        
        return ATTACK_SPEED;
    }
    
    private int performMagicAttack(final Player target) {
        setAnimation(magicAttackAnimation);
        
        if (magicAttackCount == 10) {
            performBombAttack(target);
            magicAttackCount = 0;
            return 9;
        } else {
            magicAttackCount++;
            
            /*
                Solo = only mage, no ricochet
                Duos = 50/50 ricochet into range or mage
                Trio+ = 1 magic + 1 range
             */
    
            // initial magic ball projectile
            val delay = World.sendProjectile(this, target, BallAttack.MAGIC.createProjectile(false));
    
            // delay hit for initial magic ball
            WorldTasksManager.schedule(() -> {
                if (!shouldAttack()) {
                    return;
                }
    
                BallAttack.MAGIC.applyHit(target, this);
    
                if (getRaid().getParty().getTargetableMembers().size() <= 1) {
                    // no ricochet occurs in solos
                    return;
                }
    
                WorldTasksManager.schedule(() -> {
                    if (!shouldAttack()) {
                        return;
                    }
        
                    val members = new ObjectArrayList<>(getRaid().getParty().getTargetableMembers());
                    Collections.shuffle(members); // randomise players
        
                    BallAttack firstRicochet = null;
        
                    for (val m : members) {
                        val member = RaidingParty.getPlayer(m);
                        if (member == null || member.equals(target)) {
                            continue;
                        }
                        if (firstRicochet == null) {
                            firstRicochet = BallAttack.randomBall();
                            performBallAttack(target, member, firstRicochet);
                        } else {
                            if (firstRicochet == BallAttack.MAGIC) {
                                performBallAttack(target, member, BallAttack.RANGE);
                            } else {
                                performBallAttack(target, member, BallAttack.MAGIC);
                            }
                            break;
                        }
                    }
                }, 0);
    
            }, delay);
            
        }
        
        return ATTACK_SPEED;
    }
    
    private void performBallAttack(final Player initialPlayer, final Player target, final BallAttack ball) {
        val delay = World.sendProjectile(initialPlayer, target, ball.createProjectile(true));
        WorldTasksManager.schedule(() -> {
            if (!shouldAttack() || target.isDead()) {
                return;
            }
            ball.applyHit(target, this);
        }, delay);
    }
    
    private void performBombAttack(final Player target) {
        World.sendSoundEffect(getMiddleLocation(), new SoundEffect(3994, 10, 0));
        target.sendMessage(Colour.RED.wrap("A large ball of energy is shot your way..."));
        
        val delay = World.sendProjectile(this, target, bombProjectile);
        WorldTasksManager.schedule(() -> {
            if (!shouldAttack() || target.isDead()) {
                return;
            }
            
            val membersInRange = membersInRange(target);
            val numberOfMembersOutOfRange = getRaid().getParty().getSize() - membersInRange.size();
    
            val totalDamage = BOMB_BASE_DAMAGE + (numberOfMembersOutOfRange * BOMB_EXTRA_DAMAGE_PER_PLAYER);
            val damagePerPlayer = totalDamage / (membersInRange.size() > 0 ? membersInRange.size() + 1 : 1);
            
            for (val member : membersInRange) {
                if (!room.isCompleted()) {
                    member.applyHit(new Hit(damagePerPlayer, HitType.REGULAR));
                }
                member.setGraphics(bombHitGraphic);
                if (membersInRange.size() == 1) {
                    member.getAttributes().put("soloBombTanks", member.getNumericAttribute("soloBombTanks").intValue() + 1);
                }
                // TODO sound
            }
            
        }, delay);
        
    }
    
    private void disablePrayers(final Player target) {
        val prayers = target.getPrayerManager();
        if (prayers.isActive(Prayer.PROTECT_FROM_MAGIC)) {
            prayers.deactivatePrayer(Prayer.PROTECT_FROM_MAGIC);
        }
        if (prayers.isActive(Prayer.PROTECT_FROM_MISSILES)) {
            prayers.deactivatePrayer(Prayer.PROTECT_FROM_MISSILES);
        }
        if (prayers.isActive(Prayer.PROTECT_FROM_MELEE)) {
            prayers.deactivatePrayer(Prayer.PROTECT_FROM_MELEE);
        }
        target.sendMessage("You've been injured and can't use protection prayers!");
        target.getTemporaryAttributes().put(SOTETSEG_PRAYER_DISABLE_ATTRIBUTE_KEY, Utils.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2));
    }
    
    private List<Player> membersInRange(final Player target) {
        return CharacterLoop.find(target.getLocation(), 1, Player.class, p -> getRaid().getParty().getMembers().contains(p.getUsername()));
    }

    public void startMazeForSolo() {
        mazePhase = true;
    }
    
    private void commenceMazePhase() {
        mazePhase = true;
        setAttackedBy(null);
        cancelCombat();
        faceDirection(Direction.SOUTH);
        getRoom().setMazeTileIds(SotetsegRoom.Tile.DARK_GREY);
        combatDefinitions.resetStats(); // reset defence
        getRoom().refreshHealthBar(getRaid());

        if(getRaid().getParty().getAlivePlayers().size() > 1) {
            Player target = Utils.getRandomCollectionElement(getRaid().getParty().getAlivePlayers());
            if (target == null) {
                return;
            }

            PartyOverlayInterface.fadeWhite(target, Colour.GREY.wrap("Sotetseg chooses you..."));
            WorldTasksManager.schedule(() -> {
                shadowRealm = constructShadowRealm(target);
                translateMaze(shadowRealm);
                PartyOverlayInterface.fade(target, 200, 0, Colour.GREY.wrap("Sotetseg chooses you..."));
            }, 2);

            for (val m : getRaid().getParty().getMembers()) {
                val member = RaidingParty.getPlayer(m);
                if (member == null || member.equals(target) || !getRaid().getParty().getAlivePlayers().contains(member)) {
                    continue;
                }
                PartyOverlayInterface.fadeWhite(member, "");
                WorldTasksManager.schedule(() -> {
                    PartyOverlayInterface.fade(member, 200, 0, "");
                    member.setLocation(getRoom().getLocation(3274, 4307, 0));
                    member.cancelCombat();
                }, 2);
            }
        } else {
            if(getRaid().getParty().getAlivePlayers().size() == 1) {
                Player target = RaidingParty.getPlayer(getRaid().getParty().getAliveMembers().get(0));
                if(target == null) {
                    return;
                }
                PartyOverlayInterface.fadeWhite(target, Colour.GREY.wrap("Sotetseg chooses you..."));
                WorldTasksManager.schedule(() -> {
                    shadowRealm = constructShadowRealm(target);
                    translateMaze(shadowRealm);
                    PartyOverlayInterface.fade(target, 200, 0, Colour.GREY.wrap("Sotetseg chooses you..."));
                }, 2);
            }
        }
    }
    
    private void translateMaze(final ShadowRealmArea shadowRealm) {
        mazeTiles.clear();
        
        val path = shadowRealm.getMazePath();
        
        for (val tile : path) {
            System.out.println("Path: (" + tile.getX() + ", " + tile.getY() + ")");
            mazeTiles.add(translateShadowRealmTile(shadowRealm, tile));
        }
    }
    
    private Location translateShadowRealmTile(final ShadowRealmArea shadowRealm, final Location input) {
        val shadowRealmTopLeft = shadowRealm.getMazeTopLeft();
        val sotetsegTopLeft = getRoom().getMazeTopLeft();
        
        val xOffset = sotetsegTopLeft.getX() - shadowRealmTopLeft.getX();
        val yOffset = sotetsegTopLeft.getY() - shadowRealmTopLeft.getY();
        val planeOffset = sotetsegTopLeft.getPlane() - shadowRealmTopLeft.getPlane();
        val copyTile = input.transform(xOffset, yOffset, planeOffset);
        System.out.println("Translated: (" + copyTile.getX() + ", " + copyTile.getY() + ")");
        return input.transform(xOffset, yOffset, planeOffset);
    }
    
    private void mazePhaseProcess() {
        System.out.println("Processing maze phase");
        if (shadowRealm == null) {
            return;
        }

        if(raid.getParty().getAlivePlayers().size() == 1) {
            if(!mazeStormActive && !shadowRealm.isCompleted()) {
                //raid.getParty().getAlivePlayers().get(0).sendMessage("Don't start yet");
                return;
            }
            if(mazeComplete() && shadowRealm != null && shadowRealm.isCompleted()) {
                completeMaze(getRaid().getParty().getAlivePlayers().get(0));
                //raid.getParty().getAlivePlayers().get(0).sendMessage("JFC!");
                return;
            }
        }

        if(raid.getParty().getAlivePlayers().size() == 1) {
            if (shouldStartMazeSolo(raid.getParty().getAlivePlayers().get(0)) && !mazeStormActive) {
                System.out.println("Time for the storm!!!!!!!!!!!!");
                mazeStormActive = true;
                mazeStorm = new RedStorm(this, mazeTiles);
                mazeStorm.spawn();
            }
        } else {
            if (shadowRealm.shouldStartMazeStorm() && !mazeStormActive) {
                System.out.println("Time for the storm!!!!!!!!!!!!");
                mazeStormActive = true;
                mazeStorm = new RedStorm(this, mazeTiles);
                mazeStorm.spawn();
            }
        }


    
        removeDamageTiles();
    
        for (val member : getAllActivePlayers()) {
            if (member == null) {
                continue;
            }
            if (member.equals(shadowRealm.getPlayer())) {
            
                val translatedTile = translateShadowRealmTile(shadowRealm, member.getLocation());
            
                if (activeMazeTile != null && activeMazeTile.equals(translatedTile)) {
                    continue;
                }
            
                if (activeMazeTile != null) {
                    // remove previous red tile
                    World.spawnObject(new WorldObject(SotetsegRoom.Tile.DARK_GREY.getId(), 22, 0, activeMazeTile));
                    activeMazeTile = null;
                }
            
                if (getRoom().isInMaze(translatedTile)) {
                    activeMazeTile = translatedTile;
                    // add red tile to where shadow realm player is standing
                    World.spawnObject(new WorldObject(SotetsegRoom.Tile.RED.getId(), 22, 0, activeMazeTile));
                }
            
                continue;
            }
            if (getRoom().isInMaze(member) && !mazeTiles.contains(member.getLocation())) {
                val surroundingPlayers = CharacterLoop.find(member.getLocation(), 1, Player.class, p -> getRaid().getParty().getMembers().contains(p) && !p.equals(member));
                addDamageTile(member.getLocation());
                for (val player : surroundingPlayers) {
                    player.applyHit(new Hit(Utils.random(10, 25), HitType.REGULAR));
                }
                WorldTasksManager.schedule(() -> {
                    member.applyHit(new Hit(Utils.random(10, 25), HitType.REGULAR));
                });
                for (val p : room.getPlayers()) {
                    p.putBooleanAttribute("PerfectSotetseg", false);
                }
            }
            if (mazeStorm != null) {
                if (mazeStorm.getLocation().getY() >= member.getY() && mazeStormActive && room.inCombatZone(member.getX(), member.getY())) {
                    member.applyHit(new Hit(Utils.random(10, 25), HitType.REGULAR));
                    for (val p : room.getPlayers()) {
                        p.putBooleanAttribute("PerfectSotetseg", false);
                    }
                }
            }
        }
    }

    private List<Player> getAllActivePlayers() {
        List <Player> list = getRaid().getParty().getTargetablePlayers();
        list.add(shadowRealm.getPlayer());
        return list;
    }

    private boolean shouldStartMazeSolo(Player player) {
        if(shadowRealm.getPlayer() != null) {
            return false;
        }
        return player.getY() > getRoom().getMazeBottomRight().getY() + 2;
    }
    
    private void removeDamageTiles() {
        if (shadowRealm == null || !mazePhase) {
            // delete all tiles
            for (val tile : damageTiles) {
                if(getRoom().isInMaze(tile)) {
                    World.spawnObject(new WorldObject(SotetsegRoom.Tile.DARK_GREY.getId(), 22, 0, tile));
                }
            }
            damageTiles.clear();
            return;
        }

        val playerLocations = new ArrayList<Location>();
        for (val m : getRaid().getParty().getMembers()) {
            val member = RaidingParty.getPlayer(m);
            if (member == null || member.equals(shadowRealm.getPlayer())) {
                continue;
            }
            playerLocations.add(member.getLocation());
        }
        val toRemove = new ArrayList<Location>();
        
        for (val tile : damageTiles) {
            if (!playerLocations.contains(tile)) {
                toRemove.add(tile);
                if(getRoom().isInMaze(tile)) {
                    World.spawnObject(new WorldObject(SotetsegRoom.Tile.DARK_GREY.getId(), 22, 0, tile));
                }
            }
        }
        
        for (val tile : toRemove) {
            damageTiles.remove(tile);
        }
    }
    
    private void addDamageTile(final Location location) {
        if (damageTiles.add(location)) {
            World.spawnObject(new WorldObject(SotetsegRoom.Tile.RED_DAMAGE.getId(), 22, 0, location));
        }
    }
    
    public void completeMaze(final Player player) {
        if(getRaid().getParty().getAlivePlayers().size() > 1) {
            PartyOverlayInterface.fadeWhite(player, "");
            WorldTasksManager.schedule(() -> {
                PartyOverlayInterface.fade(player, 200, 0, "");
                player.cancelCombat();
                player.setLocation(getRoom().getLocation(3275, 4327, 0));
                getRoom().refreshHealthBar(getRaid());
            }, 2);
        }
        
        getRoom().setMazeTileIds(SotetsegRoom.Tile.LIGHT_GREY);
        
        mazePhase = false;
        shadowRealm = null;
        mazeStormActive = false;
        if(mazeStorm != null) {
            mazeStorm.finish();
            mazeStorm = null;
        }
        activeMazeTile = null;
        removeDamageTiles();
    }
    
    private ShadowRealmArea constructShadowRealm(final Player player) {
        val room = TheatreRoom.SHADOW_REALM;
        
        ShadowRealmArea realm = null;
        
        try {
            val allocatedArea = MapBuilder.findEmptyChunk(room.getSizeX(), room.getSizeY());
            
            realm = new ShadowRealmArea(getRaid(), allocatedArea, room, player, this);
            realm.constructRegion();
            
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
        
        return realm;
    }

    
    @Override
    public double getMagicPrayerMultiplier() {
        return 0.5;
    }
    
    @Override
    public double getMeleePrayerMultiplier() {
        return 0.5;
    }
    
    @Override
    public double getRangedPrayerMultiplier() {
        return 0.5;
    }
    
    @Override
    public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check) {
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
    public void finish() {
        super.finish();
        for (val p : room.getPlayers()) {
            if (!raid.getSpectators().contains(p.getUsername())) {
                if (p.getBooleanAttribute("ATimelySnack")
                        && p.getNumericAttribute("soloBombTanks").intValue() >= 3
                        && !p.getBooleanAttribute("master-combat-achievement56")) {
                    p.putBooleanAttribute("master-combat-achievement56", true);
                    MasterTasks.sendMasterCompletion(p, 56);
                }
                if (p.getBooleanAttribute("PerfectSotetseg")
                        && !p.getBooleanAttribute("master-combat-achievement61")) {
                    p.putBooleanAttribute("master-combat-achievement61", true);
                    MasterTasks.sendMasterCompletion(p, 61);
                }
            }
        }
        if(getRaid().getParty().getTargetablePlayers().size() > 0) {
            room.onCompletion();
        }
    }
    
    private boolean shouldAttack() {
        if (isDead()) {
            return false;
        }
        return !mazePhase;
    }
    
    @Getter
    @AllArgsConstructor
    private enum BallAttack {
        MAGIC(new Projectile(1606, 40, 30, 1 * 30, 0, 3 * 30, -32, 0), Prayer.PROTECT_FROM_MAGIC),
        RANGE(new Projectile(1607, 40, 30, 1 * 30, 0, 3 * 30, 0, 0), Prayer.PROTECT_FROM_MISSILES);
        
        public static final BallAttack[] values = values();
        private final Projectile projectile;
        private final Prayer requiredPrayer;
        
        public static BallAttack randomBall() {
            return values[Utils.random(values.length - 1)];
        }
        
        public Projectile createProjectile(final boolean ricochet) {
            val proj = new Projectile(projectile);
            if (ricochet) {
                proj.setDuration(7 * 30);
                proj.setDelay(0);
            }
            return proj;
        }
        
        public boolean hasCorrectPrayer(final Player player) {
            return player.getPrayerManager().isActive(requiredPrayer);
        }
        
        public void applyHit(final Player target, final Sotetseg sotetseg) {
            if (!sotetseg.shouldAttack()) {
                return;
            }
            if (this.equals(BallAttack.MAGIC)) {
                if (!hasCorrectPrayer(target)) {
                    sotetseg.delayHit(0, target, sotetseg.magic(target, 50));
                } else {
                    sotetseg.delayHit(0, target, new Hit(sotetseg, 0, HitType.MISSED));
                }
                target.setGraphics(new Graphics(131, 0, 124));
                target.sendSound(new SoundEffect(156, 6, 0));
            } else {
                if (!hasCorrectPrayer(target)) {
                    sotetseg.delayHit(0, target, sotetseg.ranged(target, 50));
                } else {
                    sotetseg.delayHit(0, target, new Hit(sotetseg, 0, HitType.MISSED));
                }
                target.sendSound(new SoundEffect(4015, 6, 0));
            }
            WorldTasksManager.schedule(() -> {
                if (!hasCorrectPrayer(target)) {
                    for (val p : sotetseg.getRaid().getParty().getPlayers()) {
                        p.putBooleanAttribute("PerfectSotetseg",false);
                    }
                    sotetseg.disablePrayers(target);
                }
            },0);
        }
    }
    
    @Getter
    @AllArgsConstructor
    private enum ShadowRealmPhase {
        TWO_THIRDS(66.6),
        ONE_THIRD(33.3);
        
        private static final ShadowRealmPhase[] phases = values();
        private final double healthPercent;
        
        private static boolean shouldInitiate(final Sotetseg sotetseg) {
            val healthPercentage = sotetseg.getHitpointsAsPercentage();
            
            for (val phase : phases) {
                if (healthPercentage <= phase.getHealthPercent() && !sotetseg.mazePhases.contains(phase.getHealthPercent())) {
                    sotetseg.mazePhases.add(phase.getHealthPercent());
                    return true;
                }
            }
            
            return false;
        }
    }
    
}
