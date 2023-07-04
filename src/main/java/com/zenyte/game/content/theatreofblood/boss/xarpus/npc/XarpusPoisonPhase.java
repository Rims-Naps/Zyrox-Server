package com.zenyte.game.content.theatreofblood.boss.xarpus.npc;

import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.ImmutableLocation;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combatdefs.NPCCDLoader;
import com.zenyte.game.world.entity.npc.combatdefs.NPCCombatDefinitions;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris
 * @since August 25 2020
 */
public class XarpusPoisonPhase extends XarpusPhase {
    private static final int START_DELAY = (int) TimeUnit.SECONDS.toTicks(4);
    private static final int ATTACK_INTERVAL = (int) TimeUnit.SECONDS.toTicks(3);
    private static final Animation transformationAnim = new Animation(8061);
    private static final Animation attackAnim = new Animation(8059);
    private static final Projectile xarpusPoisonProjectile = new Projectile(1555, 150, 0, 0, 60, 60, 0, 5);
    private static final Projectile splashPoisonProjectile = new Projectile(1555, 10, 0, 0, 60, 60, 0, 5);
    private static final Graphics poisonLandGfx = new Graphics(1556);
    private List<Player> targets = new ArrayList<>();

    public XarpusPoisonPhase(@NotNull final Xarpus xarpus) {
        super(xarpus);
    }

    @Override
    void onPhaseStart() {
        xarpus.setAnimation(transformationAnim);
        xarpus.setTransformation(NpcId.XARPUS_8340);
        xarpus.setLocation(xarpus.getLocation().transform(-1, -1, 0));
        val cachedDefs = NPCCombatDefinitions.clone(NpcId.XARPUS_8340, NPCCDLoader.get(NpcId.XARPUS_8340));
        xarpus.setCombatDefinitions(cachedDefs);
    }

    @Override
    void onTick() {
        if (ticks.getValue() >= START_DELAY && (ticks.getValue() + 1) % ATTACK_INTERVAL == 0) {
            val newTarget = getNewTarget();
            if(newTarget != null) {
                xarpus.setTarget(newTarget);
            }
            WorldTasksManager.schedule(() -> {
                if(newTarget != null) {
                    xarpus.setAnimation(attackAnim);
                    val targetLocation = new ImmutableLocation(newTarget.getLocation());
                    sendPoisonOrb(new ImmutableLocation(xarpus.getMiddleLocation()), targetLocation, false, newTarget);
                }
            },1);
        }
    }

    private Player getNewTarget() {
        if (targets.isEmpty()) {
            targets = xarpus.getRaid().getParty().getTargetablePlayers();
        }
        val player = targets.get(0);
        targets.remove(player);
        return player;
/*
        val possibleInitialTargets = xarpus.getRaid().getParty().getTargetablePlayers();
        possibleInitialTargets.removeIf(p -> xarpus.getCombat().getTarget() == p && possibleInitialTargets.size() > 1);
        if(possibleInitialTargets.size() == 0) {
            return null;
        } else {
            return possibleInitialTargets.get(Utils.random(possibleInitialTargets.size() - 1));
        }

 */
    }

    private void sendPoisonOrb(@NotNull final ImmutableLocation fromLocation, @NotNull final ImmutableLocation toLocation, final boolean isSplash, final Player player) {
        val poisonSplat = new WorldObject(ObjectId.ACIDIC_MIASMA, 22, getSplatDirection(toLocation), toLocation);
        val delay = World.sendProjectile(fromLocation, toLocation, isSplash ? splashPoisonProjectile : xarpusPoisonProjectile);
        val exists = xarpus.getPoisonSplats().contains(poisonSplat);
        WorldTasksManager.schedule(() -> {
            if (!exists) {
                World.spawnObject(poisonSplat);
                xarpus.getPoisonSplats().add(poisonSplat);
            }
            World.sendGraphics(poisonLandGfx, poisonSplat);
            WorldTasksManager.schedule(() -> {
                if (!exists) {
                    World.sendObjectAnimation(poisonSplat, new Animation(8068));
                }
                poisonNearbyPlayers(toLocation);
                if (!isSplash) {
                    splash(toLocation, player);
                }
            });
        }, Math.max(0, delay));
    }

    private int getSplatDirection(@NotNull final ImmutableLocation splatLocation) {
        if (XarpusQuadrant.SOUTH_EAST.isInside(splatLocation, xarpus)) {
            return 0;
        } else if (XarpusQuadrant.SOUTH_WEST.isInside(splatLocation, xarpus)) {
            return 1;
        } else if (XarpusQuadrant.NORTH_WEST.isInside(splatLocation, xarpus)) {
            return 2;
        } else {
            return 3;
        }
    }

    private void poisonNearbyPlayers(@NotNull final ImmutableLocation splatLocation) {
        for (val player : xarpus.getRaid().getParty().getAlivePlayers()) {
            if (player.getLocation().withinDistance(splatLocation, 1)) {
                poison(player);
            }
        }
    }

    public static void poison(@NotNull final Player player) {
        for (val p : VerSinhazaArea.getParty(player).getPlayers()) {
            p.putBooleanAttribute("PerfectXarpus", false);
        }
        if (VerSinhazaArea.getParty(player).getAlivePlayers().contains(player)) {
            WorldTasksManager.schedule(() -> player.applyHit(new Hit(Utils.random(6, 8), HitType.POISON)));
        }
    }

    private void splash(@NotNull final ImmutableLocation fromLocation, @NotNull final Player player) {
        val aliveSize = xarpus.getRaid().getParty().getAlivePlayers().size();
        if (aliveSize == 1) {
            val splashAmount = Utils.random(1, 2);
            for (var i = 0; i < splashAmount; i++) {
                val splashLocation = new ImmutableLocation(xarpus.getLocation().random(6, 8, 6, 8, 0, 2, 0, 2));
                sendPoisonOrb(fromLocation, splashLocation, true, null);
            }
        } else {
            val splashAmount = 2;//Utils.random(1, 2);

            if (aliveSize == 2) {
                if (splashAmount == 2) {
                    val splashLocation = new ImmutableLocation(xarpus.getLocation().random(6, 8, 6, 8, 0, 2, 0, 2));
                    sendPoisonOrb(fromLocation, splashLocation, true, null);
                    var otherPlayer = xarpus.getRaid().getParty().getRandomPlayer();
                    for (int index = 0; index < 100 && otherPlayer.equals(player); index++) {
                        otherPlayer = xarpus.getRaid().getParty().getRandomPlayer();
                    }
                    val splashLocation2 = new ImmutableLocation(otherPlayer.getPosition());
                    sendPoisonOrb(fromLocation, splashLocation2, true, otherPlayer);
                } else {
                    var otherPlayer = xarpus.getRaid().getParty().getRandomPlayer();
                    for (int index = 0; index < 100 && otherPlayer.equals(player); index++) {
                        otherPlayer = xarpus.getRaid().getParty().getRandomPlayer();
                    }
                    val splashLocation2 = new ImmutableLocation(otherPlayer.getPosition());
                    sendPoisonOrb(fromLocation, splashLocation2, true, otherPlayer);
                }
            } else {
                if (splashAmount == 2) {
                    var otherPlayer = xarpus.getRaid().getParty().getRandomPlayer();
                    for (int index = 0; index < 100 && otherPlayer.equals(player); index++) {
                        otherPlayer = xarpus.getRaid().getParty().getRandomPlayer();
                    }
                    var otherPlayer2 = xarpus.getRaid().getParty().getRandomPlayer();
                    for (int index = 0; index < 100 && (otherPlayer2.equals(player) || otherPlayer2.equals(otherPlayer)); index++) {
                        otherPlayer2 = xarpus.getRaid().getParty().getRandomPlayer();
                    }
                    val splashLocation = new ImmutableLocation(otherPlayer.getPosition());
                    sendPoisonOrb(fromLocation, splashLocation, true, otherPlayer);
                    val splashLocation2 = new ImmutableLocation(otherPlayer2.getPosition());
                    sendPoisonOrb(fromLocation, splashLocation2, true, otherPlayer2);
                } else {
                    var otherPlayer = xarpus.getRaid().getParty().getRandomPlayer();
                    for (int index = 0; index < 100 && otherPlayer.equals(player); index++) {
                        otherPlayer = xarpus.getRaid().getParty().getRandomPlayer();
                    }
                    val splashLocation2 = new ImmutableLocation(otherPlayer.getPosition());
                    sendPoisonOrb(fromLocation, splashLocation2, true, otherPlayer);
                }
            }
        }
    }

    @Override
    boolean isPhaseComplete() {
        return xarpus.getHitpointsAsPercentage() <= 22.5;
    }

    @Override
    XarpusPhase advance() {
        xarpus.getTemporaryAttributes().put("screech", true);
        return new XarpusCounterPhase(xarpus);
    }
}
