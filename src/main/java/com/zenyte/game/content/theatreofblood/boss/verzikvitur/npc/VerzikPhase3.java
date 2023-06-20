package com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc;

import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.model.NylocasTypeV;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.player.MovementLock;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.CharacterLoop;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

import static com.zenyte.game.content.theatreofblood.boss.verzikvitur.model.NylocasTypeV.*;

/**
 * @author Cresinkel
 */

public class VerzikPhase3 extends VerzikPhase {

    private final Location throneLocation;
    private boolean transitionComplete = false;
    private boolean duringSpecialAttack = false;
    private boolean tornadoPhase = false;
    private int attackCount = 0;
    private int ticksUntilAttack = 0;
    private int attackDelay = 7;
    private boolean slam;
    private Player target = null;
    private boolean switchTarget = false;
    private Player[] playersWithinMeleeDistance = new Player[5];
    private final int MELEE_ATTACK_ANIM = 8123;
    private final int MAGE_ATTACK_ANIM = 8124;
    private final int RANGE_ATTACK_ANIM = 8125;
    private final int YELLOW_POOL_ATTACK_ANIM = 8126;
    private final int WEB_ATTACK_ANIM = 8127;
    private final int DEATH_ANIM = 8128;
    private static final Projectile magicProj = new Projectile(1594, 70, 30, 30, 60, 60, 0, 5);
    private static final Projectile rangeProj = new Projectile(1593, 70, 30, 30, 60, 60, 0, 5);
    private static final Projectile webProj = new Projectile(1601, 70, 0, 0, 60, 60, 0, 5);
    private static final Projectile yellowPoolProj = new Projectile(1596, 70, 0, 0, 70, 90, 0, 5);
    private static final Projectile greenBallProj = new Projectile(1598, 70, 30, 0, 60, 90, 0, 5);
    private static final Graphics yellowPoolGroundGfx = new Graphics(1595);
    private static final Graphics yellowPoolHitGfx = new Graphics(1597);
    private static final Graphics greenBallExplodeGfx = new Graphics(1600,0,60);
    List<Location> webs = new ArrayList<>(100);
    List<Location> websToBe = new ArrayList<>(100);
    List<Location> pools = new ArrayList<>(5);
    List<Player> onSamePool = new ArrayList<>(5);
    List<Player> greenBallBounces = new ArrayList<>(5);
    List<Player> potentialBounces = new ArrayList<>(5);
    private int greenBallBounceNumber = 0;

    public VerzikPhase3(VerzikVitur verzik, Location throneLocation) {
        super(verzik, 4);
        this.throneLocation = throneLocation;
    }

    @Override
    public void onPhaseStart() {
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                if(ticks == 0) {
                    verzik.resetFreeze();
                    verzik.reset();
                    verzik.setAnimation(Animation.STOP);
                    verzik.faceDirection(Direction.SOUTH);
                }
                if(ticks == 1) {
                    verzik.setAnimation(new Animation(8118));
                }
                if(ticks == 2) {
                    verzik.setTransformation(NpcId.VERZIK_VITUR_8373);
                    verzik.setLocation(verzik.getLocation().transform(-2, -2, 0));
                    verzik.setAnimation(new Animation(8119));
                }
                if(ticks == 3) {
                    verzik.setTransformation(NpcId.VERZIK_VITUR_8374);
                    verzik.setAnimation(new Animation(8120));
                }
                if(ticks == 4) {
                    verzik.setForceTalk("Behold my true nature!");
                    verzik.setAnimation(Animation.STOP);
                    ticksUntilAttack = 3;
                    resetTicks();
                    transitionComplete = true;
                    verzik.setAttackAble(true);
                    verzik.setWalkAble(true);
                    verzik.setForceAggressive(true);
                    target = getRandomTarget();
                    verzik.setAttackDistance(0);
                    verzik.setMaxDistance(100);
                    stop();
                }
                ticks++;
            }
        }, 1, 1);
    }

    @Override
    public void onTick() {
        if (verzik.getHitpointsAsPercentage() <= 20 && !tornadoPhase) {
            verzik.setForceTalk("I'm not finished with you just yet!");
            tornadoPhase = true;
            attackDelay = 5;
            for (val player : verzik.getRaid().getParty().getTargetablePlayers()) {
                Tornado tornado = new Tornado(verzik, player, getTornadoSpawnLoc(player));
                tornado.spawn();
            }
        }

        for (val player : verzik.getRaid().getParty().getTargetablePlayers()) {
            if (player.isMovementLocked(true)) {
                continue;
            }
            if (webs.contains(player.getPosition())) {
                player.addMovementLock(new MovementLock(System.currentTimeMillis() + 50000));
                player.resetWalkSteps();
            }
        }

        if(!transitionComplete) {
            return;
        } else if (duringSpecialAttack) {
            return;
        } else {
            if (target == null || switchTarget) { //might need to check if the target is dead and/or in jail
                target = getRandomTarget();
                switchTarget = false;
            }

            if(ticksUntilAttack == 1) {
                if (target != null) {
                    if (target.getLocation().getTileDistance(verzik.getMiddleLocation()) == 4) {
                        List<Player> targets = verzik.getRaid().getParty().getTargetablePlayers();
                        for(int i = 0; i < targets.size(); i++) {
                            if(targets.get(i).getLocation().getTileDistance(verzik.getMiddleLocation()) == 4) {
                                playersWithinMeleeDistance[i] = targets.get(i);
                            }
                        }
                        slam = true;
                    }
                }
            }

            if(ticksUntilAttack == 0) {
                if (attackCount == 5) {
                    nyloAttack();
                } else if (attackCount == 10) {
                    webAttack();
                } else if (attackCount == 15) {
                    yellowPoolAttack();
                } else if (attackCount == 20) {
                    greenBallAttack();
                } else {
                    if (slam) {
                        meleeAttack();
                    } else {
                        autoAttack();
                    }
                }
                return;
            }
            ticksUntilAttack--;
        }
    }

    private void autoAttack() {
        Projectile proj = null;
        Animation anim = null;
        Prayer prayer = null;
        if (Utils.random(1) == 0) {
            proj = magicProj;
            anim = new Animation(MAGE_ATTACK_ANIM);
            prayer = Prayer.PROTECT_FROM_MAGIC;
        } else {
            proj = rangeProj;
            anim = new Animation(RANGE_ATTACK_ANIM);
            prayer = Prayer.PROTECT_FROM_MISSILES;
        }
        for (val p : verzik.getRaid().getParty().getTargetablePlayers()) {
            int delay = World.sendProjectile(verzik.getMiddleLocation(), p, proj);
            Prayer finalPrayer = prayer;
            WorldTasksManager.schedule(() -> {
                HitType type = finalPrayer.equals(Prayer.PROTECT_FROM_MAGIC) ? HitType.MAGIC : HitType.RANGED;
                if (p.getPrayerManager().isActive(finalPrayer)) {
                    p.applyHit(new Hit(verzik, Utils.random(0,16), type));
                } else {
                    p.applyHit(new Hit(verzik, Utils.random(0,33), type));
                }
            }, Math.max(0, delay));
        }
        verzik.setAnimation(anim);
        attackCount++;
        ticksUntilAttack = attackDelay;
        slam = false;
        if (!getPossibleTargets().contains(target) || target.isDying()) {
            switchTarget = true;
        }
    }

    private void meleeAttack() {
        boolean melee = false;
        for(int i = 0; i < playersWithinMeleeDistance.length; i++) {
            if(playersWithinMeleeDistance[i] == null) {
                continue;
            }
            if(playersWithinMeleeDistance[i].getLocation().getTileDistance(verzik.getMiddleLocation()) == 4) {
                playersWithinMeleeDistance[i].applyHit(new Hit(verzik, Utils.random(0,63), HitType.MELEE));
                playersWithinMeleeDistance[i] = null;
                melee = true;
            } else {
                playersWithinMeleeDistance[i] = null;
            }
        }
        if (!melee) {
            autoAttack();
            return;
        }
        verzik.setAnimation(new Animation(MELEE_ATTACK_ANIM));
        attackCount++;
        ticksUntilAttack = attackDelay;
        slam = false;
        if (!getPossibleTargets().contains(target) || target.isDying()) {
            switchTarget = true;
        }
    }

    private void nyloAttack() {
        spawnCrabs();
        switchTarget = true;
    }

    private void spawnCrabs() {
        List<Player> targets = getPossibleTargets();
        if(targets.size() < 1) {
            return;
        }
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
        if (slam) {
            meleeAttack();
        } else {
            autoAttack();
        }
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

    private void webAttack() {
        duringSpecialAttack = true;
        webs.clear();
        websToBe.clear();
        verzik.setAttackAble(false);
        verzik.blockIncomingHits();

        Location middle = new Location(verzik.getRoom().getX(3165), verzik.getRoom().getY(4309));
        verzik.resetWalkSteps();
        verzik.setForceAggressive(false);
        verzik.cancelCombat();
        verzik.addWalkSteps(middle.getX(), middle.getY(),-1);
        WorldTasksManager.schedule(new TickTask() {
            int webStart = 0;
            int webShoots = 0;
            @Override
            public void run() {
                if (verzik.getLocation().equals(middle) && webStart == 0) {
                    verzik.setWalkAble(false);
                    verzik.setAttackAble(true);
                    verzik.setAnimation(new Animation(WEB_ATTACK_ANIM)); //30 ticks long
                    webStart = ticks + 1;
                }
                if (ticks >= webStart && webStart != 0) {
                    Player p = getRandomTarget();
                    if (verzik.getPhase().getOrdinal() != 4) {
                        duringSpecialAttack = false;
                        stop();
                        return;
                    }
                    if (p == null) {
                        duringSpecialAttack = false;
                        verzik.setForceAggressive(true);
                        verzik.setWalkAble(true);
                        stop();
                        return;
                    }
                    verzik.faceEntity(p);
                    int webQuantity = 3;
                    for (int i = 0; i < webQuantity; i++) {
                        Location landing;
                        if (i == 0 && !webs.contains(p.getLocation()) && !websToBe.contains(p.getLocation())) {
                            landing = new Location(p.getLocation());
                        } else {
                            int dx = Utils.random(-2,2);
                            int dy = Utils.random(-2,2);
                            for (int index2 = 0;index2 < 100 && dx == 0 && dy == 0; index2++) {
                                dx = Utils.random(-2,2);
                                dy = Utils.random(-2,2);
                            }
                            landing = new Location(p.getLocation().transform(dx,dy, 0));
                            for (int index = 0; (index < 100 &&
                                    (webs.contains(landing)
                                            || websToBe.contains(landing)
                                            || verzik.getMiddleLocation().getTileDistance(landing) < 4
                                            || !verzik.getRoom().inCombatZone(landing.getX(), landing.getY()))) ; index++) {
                                if (index == 99) {
                                    landing = null;
                                    break;
                                }
                                for (int index2 = 0;index2 < 100 && dx == 0 && dy == 0; index2++) {
                                    dx = Utils.random(-2,2);
                                    dy = Utils.random(-2,2);
                                }
                                landing = new Location(p.getLocation().transform(dx,dy, 0));
                            }
                        }
                        websToBe.add(landing);
                        if (landing == null) {
                            continue;
                        }
                        int delay = World.sendProjectile(verzik.getMiddleLocation(), landing, webProj);
                        Location finalLanding = landing;
                        WorldTasksManager.schedule(() -> {
                            webs.add(finalLanding);
                            Web web = new Web(NpcId.WEB, finalLanding, Direction.NORTH, 0, verzik.getRaid());
                            World.spawnNPC(web);
                        }, Math.max(0, delay));
                    }
                    webShoots++;
                }
                if (webShoots == 28) {
                    duringSpecialAttack = false;
                    verzik.setForceAggressive(true);
                    verzik.setWalkAble(true);
                    stop();
                }
                ticks++;
            }
        },0,0);

        attackCount++;
        ticksUntilAttack = attackDelay;
        switchTarget = true;
        slam = false;
    }

    public void removeWeb(Web web, boolean killed) {
        webs.remove(web.getLocation());
        CharacterLoop.forEach(web.getLocation(),0,Player.class, p -> {
            if (!killed) {
                p.applyHit(new Hit(verzik, Utils.random(35, 50), HitType.REGULAR));
            }
            p.removeAllMovementLocks();
        });

    }

    private void yellowPoolAttack() {
        duringSpecialAttack = true;
        pools.clear();
        onSamePool.clear();
        verzik.setAttackAble(false);
        verzik.blockIncomingHits();
        verzik.setWalkAble(false);
        verzik.resetWalkSteps();
        verzik.setForceAggressive(false);
        verzik.cancelCombat();
        verzik.setAnimation(new Animation(YELLOW_POOL_ATTACK_ANIM));
        int aliveSize = verzik.getRaid().getParty().getTargetablePlayers().size();
        int poolCount = 0;
        for (int i = 0; i < 100 && poolCount < aliveSize; i++) {
            int poolX = Utils.random(verzik.getRoom().getX(3154), verzik.getRoom().getX(3182));
            int poolY = Utils.random(verzik.getRoom().getY(4303), verzik.getRoom().getY(4322));
            Location poolLocation = new Location(poolX, poolY, 0);
            if (!pools.contains(poolLocation)) {
                if (verzik.getMiddleLocation().getTileDistance(poolLocation) > 4) {
                    pools.add(poolLocation);
                    poolCount++;
                    World.sendGraphics(yellowPoolGroundGfx, poolLocation);
                }
            }
        }
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                if (ticks == 4) {
                    for (val poolLocation : pools) {
                        World.sendGraphics(yellowPoolGroundGfx, poolLocation);
                    }
                }
                if (ticks == 8) {
                    for (val poolLocation : pools) {
                        World.sendGraphics(yellowPoolGroundGfx, poolLocation);
                    }
                }
                if (ticks == 8) {
                    for (val player : verzik.getRaid().getParty().getTargetablePlayers()) {
                        int delay = World.sendProjectile(verzik.getMiddleLocation(), player, yellowPoolProj);
                        WorldTasksManager.schedule(() -> {
                            CharacterLoop.forEach(player.getLocation(),0,Player.class,p -> {
                                if (!player.equals(p)) {
                                    onSamePool.add(p);
                                    onSamePool.add(player);
                                }
                            });
                            if (!pools.contains(player.getPosition())) {
                                player.applyHit(new Hit(verzik,Utils.random(50,75), HitType.REGULAR));
                            } else if (onSamePool.contains(player)) {
                                player.applyHit(new Hit(verzik,Utils.random(50,75), HitType.REGULAR));
                            } else {
                                player.setGraphics(yellowPoolHitGfx);
                                player.sendMessage("The power resonating here protects you from the blast.");
                            }
                        }, Math.max(0, delay));
                    }
                }
                if (ticks == 10) {
                    duringSpecialAttack = false;
                    verzik.setForceAggressive(true);
                    verzik.setWalkAble(true);
                    verzik.setAttackAble(true);
                    stop();
                }
                ticks++;
            }
        },0,0);
        attackCount++;
        ticksUntilAttack = attackDelay;
        switchTarget = true;
        slam = false;
    }

    private void greenBallAttack() {
        greenBallBounces.clear();
        greenBallBounceNumber = 0;
        verzik.setAnimation(new Animation(RANGE_ATTACK_ANIM));
        if (target != null) {
            greenBallBounce(target, verzik, 0);
        }
        attackCount = 0;
        ticksUntilAttack = 12;
        switchTarget = true;
        slam = false;
    }

    public void greenBallBounce(Player nextTarget, Entity sender, int extraDelay) {
        int delay;
        if (sender instanceof VerzikVitur) {
            delay = World.sendProjectile(verzik.getMiddleLocation(), nextTarget, greenBallProj);
        } else {
            delay = World.sendProjectile(sender.getLocation(), nextTarget, greenBallProj);
        }
        greenBallBounceNumber++;
        WorldTasksManager.schedule(() -> {
            greenBallBounces.add(nextTarget);
            potentialBounces.clear();
            if (greenBallBounceNumber >= 4) {
                return;
            }
            if (sender instanceof Player) {
                Player senderPlayer = (Player) sender;
                for (val player : verzik.getRaid().getParty().getTargetablePlayers()) {
                    if (!player.equals(senderPlayer) && !player.equals(nextTarget)) {
                        greenBallBounces.remove(player);
                    }
                }
            }
            CharacterLoop.forEach(nextTarget.getPosition(), 2, Player.class, p -> {
                if (!greenBallBounces.contains(p)) {
                    potentialBounces.add(p);
                }
            });
            if (potentialBounces.isEmpty()) {
                nextTarget.applyHit(new Hit(verzik, Utils.random(50,75), HitType.REGULAR));
                nextTarget.setGraphics(greenBallExplodeGfx);
            } else {
                greenBallBounce(potentialBounces.get(Utils.random(potentialBounces.size() - 1)), nextTarget, 1);
            }
        }, delay + extraDelay);
    }

    private Location getTornadoSpawnLoc(Player player) {
        Location spawnLoc = player.getLocation().transform(Utils.random(5,7), Utils.random(5,7), 0);
        for (int i = 0; (i < 1000 && (verzik.getMiddleLocation().getTileDistance(spawnLoc) < 4 || !verzik.getRoom().inCombatZone(spawnLoc.getX(), spawnLoc.getY()))) ; i++) {
            if (i == 99) {
                spawnLoc = verzik.getMiddleLocation();
                break;
            }
            spawnLoc = player.getLocation().transform(Utils.random(5,7), Utils.random(5,7), 0);
        }
        return spawnLoc;
    }

    @Override
    public boolean isPhaseComplete() {
        return verzik.getHitpoints() == 0;
    }

    @Override
    public VerzikPhase advance() {
        verzik.setWalkAble(false);
        verzik.setAttackAble(false);
        return new VerzikEndPhase(verzik, throneLocation);
    }
}
