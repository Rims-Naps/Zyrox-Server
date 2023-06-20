package com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc;

import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.model.NylocasTypeV;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;
import java.util.ArrayList;
import java.util.List;


import static com.zenyte.game.content.theatreofblood.boss.verzikvitur.model.NylocasTypeV.*;

public class VerzikPhase2 extends VerzikPhase{

    private boolean transitionComplete = false;
    private int attackCount = 0;
    private int ticksUntilAttack = 0;
    private final static int ATTACK_DELAY = 4;
    private static int RED_CRAB_TICK;
    private boolean bloodPhase = false;
    private int siphonTick;
    private int firstRandomSpawnTick;
    private Player[] playersWithinMeleeDistance = new Player[5];
    private List<Player> lightningBounceTargets = new ArrayList<>();
    private List<Player> bloodTargets = new ArrayList<>();
    private boolean slam;
    private List<NylocasMatomenosV> aliveRedCrabs = new ArrayList<>(2);
    private Location throneLocation;

    public VerzikPhase2(VerzikVitur verzik) {
        super(verzik, 3);
        firstRandomSpawnTick = Utils.random(2, 5);
    }

    @Override
    public void onPhaseStart() {
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                if(ticks == 0) {
                    removeDawnbringer();
                    verzik.setAttackAble(false);
                    verzik.blockIncomingHits();
                    verzik.setForceTalk("You think you can defeat me?");
                } //transformations* taking hella long
                if(ticks == 1) {
                    verzik.resetFreeze();
                    verzik.reset();
                    verzik.setAnimation(new Animation(8111));
                }
                if(ticks == 2) {
                    World.spawnObject(new WorldObject(32686, 10, 0, new Location(verzik.getPosition().getX(), verzik.getPosition().getY() + 1, 0)));
                    throneLocation = verzik.getPosition().transform(1,1,0);
                }
                if(ticks == 3) {
                    verzik.setTransformation(8371);
                    verzik.setAnimation(Animation.STOP);
                    verzik.faceDirection(Direction.SOUTH);
                }
                if(ticks == 4) {
                    verzik.resetWalkSteps();
                    verzik.addWalkSteps(verzik.getX(), verzik.getY() - 11, - 1, false);
                }
                if(ticks == 10) {
                    transitionComplete = true;
                    bloodPhase = false;
                    verzik.getTemporaryAttributes().put("siphon", false);
                    resetTicks();
                    ticksUntilAttack = 3;
                    verzik.setAggressionDistance(25);
                    verzik.setTransformation(8372); //where all da constants at tho
                    verzik.setAnimation(Animation.STOP);
                    verzik.setLocation(new Location(verzik.getLocation().getX() + 1, verzik.getLocation().getY() + 1));
                    Player target = getRandomTarget();
                    if(target != null) {
                        verzik.setFaceEntity(target);
                        verzik.setTarget(target);
                    }
                    verzik.addWalkSteps(verzik.getX(), verzik.getY() - 11, - 1, false);
                    verzik.faceDirection(Direction.SOUTH);
                    verzik.setAttackAble(true);
                    verzik.setWalkAble(false);
                    stop();
                }
                ticks++;
            }
        }, 1, 1);
    }

    @Override
    public void onTick() {
        if (ticks.intValue() - 10 >= siphonTick || verzik.getHitpoints() > verzik.getMaxHitpoints()*0.35) {
            verzik.getTemporaryAttributes().put("siphon", false);
        }
        if(verzik.getTemporaryAttributes().getOrDefault("siphon",false).equals(true)) {
            return;
        }
        if(!transitionComplete) {
            return;
        } else {
            slam = false;
            if(ticksUntilAttack == 1) {
                List<Player> targets = getPossibleTargets();
                for(int i = 0; i < targets.size(); i++) {
                    if(targets.get(i).getLocation().withinDistance(verzik.getMiddleLocation(), 2)) {
                        //targets.get(i).sendMessage("About to pounce on yo bitch ass, if you don't move in the next tick");
                        playersWithinMeleeDistance[i] = targets.get(i);
                    }
                }
            }
            if(ticksUntilAttack == 0) {
                Player playerToFace = null;
                for(int i = 0; i < playersWithinMeleeDistance.length; i++) {
                    if(playersWithinMeleeDistance[i] == null) {
                        continue;
                    }
                    if(playersWithinMeleeDistance[i].getLocation().withinDistance(verzik.getMiddleLocation(), 2)) {
                        slam = true;
                        playerToFace = playersWithinMeleeDistance[i];
                        slamPlayer(playersWithinMeleeDistance[i]);
                        playersWithinMeleeDistance[i] = null;
                    } else {
                        playersWithinMeleeDistance[i] = null;
                    }
                }
                if(slam) {
                    verzik.faceEntity(playerToFace);
                    verzik.setAnimation(new Animation(8116));
                }

                if(attackCount % 5 == 0 && attackCount != 0) {
                    //lightning
                    if(!slam) {
                        if (verzik.getHitpoints() > verzik.getMaxHitpoints()*0.35) {
                            verzik.getTemporaryAttributes().put("siphon", false);
                        }
                        lightningAttack();
                    }
                } else {
                    //cabbage
                    if(!slam) {
                        if (verzik.getHitpoints() > verzik.getMaxHitpoints()*0.35) {
                            verzik.getTemporaryAttributes().put("siphon", false);
                            cabbageAttack();
                        } else {
                            if (Utils.random(2) == 0) {
                                cabbageAttack();
                            } else {
                                bloodAttack();
                            }
                        }
                    }
                }
            }
            if (verzik.getHitpoints() > verzik.getMaxHitpoints()*0.35) {
                if (ticks.intValue() == ATTACK_DELAY * firstRandomSpawnTick) {
                    spawnCrabs();
                } else {
                    if ((ticks.intValue() - (ATTACK_DELAY * firstRandomSpawnTick)) % 75 == 0) {
                        spawnCrabs();
                    }
                }
            } else {
                if (!bloodPhase) {
                    bloodPhase = true;
                    RED_CRAB_TICK = ticks.intValue();
                }

                if (ticks.intValue() == RED_CRAB_TICK) {
                    spawnRedCrabs();
                    verzik.setAnimation(new Animation(8117));
                    verzik.getTemporaryAttributes().put("siphon", true);
                    siphonTick = ticks.intValue();
                } else {
                    if ((ticks.intValue() - RED_CRAB_TICK) % 44 == 0) {
                        spawnRedCrabs();
                        verzik.setAnimation(new Animation(8117));
                        verzik.getTemporaryAttributes().put("siphon", true);
                        siphonTick = ticks.intValue();
                    }
                }
            }
            if(ticksUntilAttack == 0) {
                return;
            }
            ticksUntilAttack--;
        }
    }

    private boolean isPlayerInMeleeDistance() {
        for(Player p : playersWithinMeleeDistance) {
            if(p != null) {
                return true;
            }
        }
        return false;
    }

    private void slamPlayer(Player p) {
        Location l = p.getFaceLocation(verzik);
        int pushDirection; //the direction we are pushing the player, same direction
        Location pushLocation;
        if(l.getX() < verzik.getX()) {
            //player is west of verzik
            pushDirection = ForceMovement.WEST;
            pushLocation = new Location(p.getX() - 3, p.getY());
        } else if(l.getX() >= verzik.getX() + verzik.getSize()) {
            //player is east of verzik
            pushDirection = ForceMovement.EAST;
            pushLocation = new Location(p.getX() + 3, p.getY());
        } else if(l.getY() < verzik.getY()) {
            //player is south of verzik
            pushDirection = ForceMovement.SOUTH;
            pushLocation = new Location(p.getX(), p.getY()  - 3);
        } else {
            pushDirection = ForceMovement.NORTH;
            pushLocation = new Location(p.getX(), p.getY()  + 3);
            //just push them north and see what happens
        } //oh noooooo lol!
        p.lock(7); //the force move lasts 2 ticks, plus 2 tick stun after (?)
        p.setForceMovement(new ForceMovement(pushLocation, 30, pushDirection));
        p.setAnimation(new Animation(1157));
        p.setGraphics(new Graphics(80, 5, 60));
        p.setLocation(pushLocation);
        ticksUntilAttack = ATTACK_DELAY;
    }

    private void spawnCrabs() {
        List<Player> targets = getPossibleTargets();
        if(targets.size() < 1) {
            return;
        }
        if(!slam) {
            verzik.setAnimation(new Animation(8114));
        }
        World.sendSoundEffect(verzik.getMiddleLocation(), new SoundEffect(1514, 10));
        Player target = getRandomTarget();
        final Location landingLocation = new Location(target.getLocation());
        World.sendGraphics(new Graphics(1589), landingLocation);
        verzik.faceEntity(target);
        WorldTasksManager.schedule(() -> {
            World.sendGraphics(new Graphics(1590,1,30), landingLocation);
            if (target.getPosition().equals(landingLocation)) {
                target.applyHit(new Hit(verzik,Utils.random(20,78),HitType.REGULAR));
            }
            NylocasAthanatosV purplyDude = new NylocasAthanatosV(verzik.getRoom(), new Location(landingLocation.getX() - 1, landingLocation.getY() -1), Direction.SOUTH);
            purplyDude.spawn();
            purplyDude.setAnimation(new Animation(8079));
            purplyDude.faceEntity(verzik);
            purplyDude.lock(2);
        }, World.sendProjectile(verzik.getMiddleLocation(), landingLocation, new Projectile(1586, 70, 1, 21, 0, (30 * 3), 64, 1)));

        //id = 1586, startHeight = 70, endHeight = 1, delay = 21, angle = 0, distOffset = 64) - Distance: 3, flight duration: 159
        WorldTasksManager.schedule(() -> {
            boolean[] usedIndexes = {false, false, false, false, false, false, false};
            for(val tar : targets) {
                int index = Utils.random(verzik.getRoom().getNyloSpawns().length - 1);
                for (int i = 0; i < 100; i++) {
                    if (!usedIndexes[index]) {
                        break;
                    }
                    index = Utils.random(verzik.getRoom().getNyloSpawns().length - 1);
                }
                NylocasV randomDude = spawnRandomNylo(verzik.getRoom().getNyloSpawns()[index]);
                usedIndexes[index] = true;
                randomDude.spawn();
                randomDude.lock(2);
                randomDude.setTarget(tar);
            }
        });
        ticksUntilAttack = ATTACK_DELAY;
        attackCount++;
    }

    public NylocasV spawnRandomNylo(Location location) {
        switch(getRandomType()) {
            case MAGIC:
                return new NylocasHagiosV(verzik.getRoom(), location, Direction.SOUTH);
            case RANGED:
                return new NylocasToxobolosV(verzik.getRoom(), location, Direction.SOUTH);
            case MELEE:
                return new NylocasIschyrosV(verzik.getRoom(), location, Direction.SOUTH);
        }
        return null;
    }

    private NylocasTypeV getRandomType() {
        switch(Utils.random(0, 2)) {
            case 0:
                return MAGIC;
            case 1:
                return MELEE;
            case 2:
                return RANGED;
        }
        return MAGIC;
    }

    private List<Player> getPossibleTargets() {
        return verzik.getRaid().getParty().getTargetablePlayers();
    }

    private Player getRandomTarget() {
        List<Player> targets =  getPossibleTargets();
        if(targets == null || targets.size() < 1) {
            return null;
        }
        return targets.get(Utils.random(0, targets.size() - 1));
    }

    private void cabbageAttack() {
        List<Player> targets = getPossibleTargets();
        if(targets.size() < 1) {
            return;
        }
        verzik.faceEntity(targets.get(Utils.random(0, targets.size() - 1)));
        verzik.setAnimation(new Animation(8114));

        for(Player p : targets) {
            Projectile cabbage = new Projectile(1583, 70, 0, 21,  12, 30,  128, 1);
            final Location landingLocation = new Location(p.getLocation());
            WorldTasksManager.schedule(new TickTask() {
                @Override
                public void run() {
                   World.sendGraphics(new Graphics(1584), landingLocation);
                   for(Player p2 : verzik.getRaid().getParty().getTargetablePlayers()) {
                       if(p2.getLocation().equals(landingLocation.getX(), landingLocation.getY(), 0)) {
                           if (p2.getPrayerManager().isActive(Prayer.PROTECT_FROM_MISSILES)) {
                               p2.applyHit(new Hit(verzik, Utils.random(15,25), HitType.REGULAR));
                           } else {
                               p2.applyHit(new Hit(verzik, Utils.random(31, 50), HitType.REGULAR));
                           }
                           for (val p3 : VerSinhazaArea.getParty(p2).getPlayers()) {
                               p3.putBooleanAttribute("PerfectVerzik", false);
                           }
                       }
                   }

                }
            }, World.sendProjectile(verzik.getMiddleLocation(), landingLocation, cabbage));
        }
        ticksUntilAttack = ATTACK_DELAY;
        attackCount++;
    }

    private void lightningAttack()
    {
        lightningBounceTargets = getPossibleTargets();
        if(lightningBounceTargets.size() < 1) {
            return;
        }
        verzik.faceEntity(lightningBounceTargets.get(Utils.random(0, lightningBounceTargets.size() - 1)));
        verzik.setAnimation(new Animation(8114));

        Player p = getRandomTarget();
        Projectile lightning = new Projectile(1585, 24, 24, 0,  32, 90,  128, 1);
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run()
            {
                if (p != null) {
                    if (lightningBounceTargets.size() == 1) {
                        //apply hit, not able to be bounced
                        if (!verzik.isFinished()) {
                            p.applyHit(new Hit(verzik, Math.min(p.getHitpoints(), Utils.random(45, 50)), HitType.REGULAR));
                            for (val p3 : VerSinhazaArea.getParty(p).getPlayers()) {
                                p3.putBooleanAttribute("PerfectVerzik", false);
                            }
                        }
                    } else {
                        bounceLightning(p);
                    }
                }
            }
        }, World.sendProjectile(verzik.getMiddleLocation(), p, lightning));
        ticksUntilAttack = ATTACK_DELAY;
        attackCount++;
    }

    private void removeDawnbringer() {
        for(Player player : verzik.getRaid().getParty().getPlayers()) {
            if(player.getInventory().containsItem(ItemId.DAWNBRINGER)) {
                WorldTasksManager.schedule(() -> {
                    player.getInventory().deleteItem(ItemId.DAWNBRINGER, 1);
                    player.getInventory().refresh();
                    player.sendMessage("The weapon is vaporized in your inventory as Verzik's shield is destroyed.", MessageType.FILTERABLE);
                });
            }
            if(player.getEquipment().getId(EquipmentSlot.WEAPON) == ItemId.DAWNBRINGER) {
                WorldTasksManager.schedule(() -> {
                    player.getEquipment().set(EquipmentSlot.WEAPON, null);
                    player.getEquipment().refresh();
                    player.sendMessage("The weapon falls apart in your hand as Verzik's shield is destroyed.", MessageType.FILTERABLE);
                });
            }
        }
    }

    private void bounceLightning(Player p) {
        lightningBounceTargets.remove(p);
        Player finalTarget = null;

        for(Player target : lightningBounceTargets) {
            if(target.getUsername() != p.getUsername())
            {
                finalTarget = target;
            }
        }

        if(finalTarget == null) {
            //eat damage, nobody else to bounce it to
            if (!verzik.isFinished()) {
                p.applyHit(new Hit(verzik, 50, HitType.REGULAR));
                for (val p3 : VerSinhazaArea.getParty(p).getPlayers()) {
                    p3.putBooleanAttribute("PerfectVerzik", false);
                }
            }
            lightningBounceTargets.clear();
            return;
        }

        int tileDistance = finalTarget.getLocation().getTileDistance(p.getLocation());
        if(tileDistance > 8) {
            //eat the damage cuz they suck
            WorldTasksManager.schedule(() -> p.applyHit(new Hit(verzik, 50, HitType.REGULAR)));
            for (val p3 : VerSinhazaArea.getParty(p).getPlayers()) {
                p3.putBooleanAttribute("PerfectVerzik", false);
            }
            lightningBounceTargets.clear();
            return;
        } else {
            Projectile lightning = new Projectile(1585, 24, 24, 0,  32, 90,  128, 1);
            final Player target = finalTarget;
            if (ifVerzikInPath(p, finalTarget)) {
                World.sendProjectile(p, verzik, lightning);
                verzik.applyHit(new Hit(Utils.random(15,25), HitType.REGULAR));
            } else {
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        bounceLightning(target);
                    }
                }, World.sendProjectile(p, finalTarget, lightning));
            }
        }
    }

    private boolean ifVerzikInPath(Player p, Player finaltarget) {
        Location verzikLoc = verzik.getMiddleLocation();
        if (finaltarget.getY() == p.getY()) {
            if (p.getY() == verzikLoc.getY() || p.getY() == verzikLoc.getY() + 1 || p.getY() == verzikLoc.getY() - 1) {
                return true;
            } else {
                return false;
            }
        }
        if (finaltarget.getX() == p.getX()) {
            if (p.getX() == verzikLoc.getX() || p.getX() == verzikLoc.getX() + 1 || p.getX() == verzikLoc.getX() - 1) {
                return true;
            } else {
                return false;
            }
        }

        val a = (float) (finaltarget.getY() - p.getY()) / (float) (finaltarget.getX() - p.getX());
        val b = p.getY() - (a * p.getX());

        double midX = (p.getX() + finaltarget.getX())/2.0;
        double midY = (p.getY() + finaltarget.getY())/2.0;

        for (int i = 9; i > 0; i--) {
            int diffX = 0;
            int diffY = 0;
            if (i == 9) { diffX = -1; diffY = -1; }
            else if (i == 8) { diffX = 0; diffY = -1; }
            else if (i == 7) { diffX = +1; diffY = -1; }
            else if (i == 6) { diffX = -1; diffY = 0; }
            else if (i == 5) { diffX = 0; diffY = 0; }
            else if (i == 4) { diffX = +1; diffY = 0; }
            else if (i == 3) { diffX = -1; diffY = +1; }
            else if (i == 2) { diffX = 0; diffY = +1; }
            else if (i == 1) { diffX = +1; diffY = +1; }

            Location verzikTile = verzikLoc.transform(diffX,diffY,0);
            if (verzikTile.getY() == a*verzikTile.getX() + b) {
                return true;
            } else if (midX < verzikLoc.getX() + 1.5
                    && midX > verzikLoc.getX() - 1.5
                    && midY < verzikLoc.getY() + 1.5
                    && midY > verzikLoc.getY() - 1.5) {
                return true;
            }
        }
        return false;
    }

    private void spawnRedCrabs() {
        List<Player> targets = getPossibleTargets();
        if(targets.size() < 1) {
            return;
        }
        if(!slam) {
            verzik.setAnimation(new Animation(8114));
        }
        aliveRedCrabs.clear();
        World.sendSoundEffect(verzik.getMiddleLocation(), new SoundEffect(1514, 10));
        final Location landingLocation1 = new Location(verzik.getMiddleLocation().transform(3,0,0));
        final Location landingLocation2 = new Location(verzik.getMiddleLocation().transform(-5,0,0));
        WorldTasksManager.schedule(() -> {
            NylocasMatomenosV redDude = new NylocasMatomenosV(verzik.getRoom(), new Location(landingLocation1.getX(), landingLocation1.getY()), Direction.SOUTH);
            redDude.spawn();
            redDude.setAnimation(new Animation(8098));
            redDude.faceDirection(Direction.SOUTH);
            redDude.lock(2);
            aliveRedCrabs.add(redDude);
        });
        WorldTasksManager.schedule(() -> {
            NylocasMatomenosV redDude = new NylocasMatomenosV(verzik.getRoom(), new Location(landingLocation2.getX(), landingLocation2.getY()), Direction.SOUTH);
            redDude.spawn();
            redDude.setAnimation(new Animation(8098));
            redDude.faceDirection(Direction.SOUTH);
            redDude.lock(2);
            aliveRedCrabs.add(redDude);
        });
        ticksUntilAttack = ATTACK_DELAY;
        attackCount++;
    }

    private void bloodAttack()
    {
        bloodTargets = getPossibleTargets();
        if(bloodTargets.size() < 1) {
            return;
        }
        verzik.faceEntity(bloodTargets.get(Utils.random(0, bloodTargets.size() - 1)));
        verzik.setAnimation(new Animation(8114));

        Player p = getRandomTarget();
        Projectile blood = new Projectile(1591, 135, 20, 20, 11, -15, 0, 15);
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                if (p != null) {
                    if (p.getPrayerManager().isActive(Prayer.PROTECT_FROM_MAGIC)) {
                        p.applyHit(new Hit(verzik,0, HitType.MISSED));
                    } else {
                        p.applyHit(new Hit(verzik,Utils.random(1,45), HitType.MAGIC));
                        for (val p3 : VerSinhazaArea.getParty(p).getPlayers()) {
                            p3.putBooleanAttribute("PerfectVerzik", false);
                        }
                    }
                    p.getPrayerManager().drainPrayerPoints(Utils.random(1,5));
                    verzik.applyHit(new Hit(Utils.random(5, 11), HitType.HEALED));
                }
            }
        }, World.sendProjectile(verzik.getMiddleLocation(), p, blood));
        ticksUntilAttack = ATTACK_DELAY;
        attackCount++;
    }

    @Override
    public boolean isPhaseComplete() {
        return verzik.getHitpoints() == 0;
    }

    @Override
    public VerzikPhase advance() {
        verzik.setAttackAble(false);
        verzik.blockIncomingHits();
        verzik.setHitpoints(verzik.getMaxHitpoints());
        return new VerzikEndPhase(verzik, throneLocation);
        //return new VerzikPhase3(verzik, throneLocation);
    }
}
