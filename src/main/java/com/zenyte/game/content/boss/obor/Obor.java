package com.zenyte.game.content.boss.obor;

import com.zenyte.game.CameraShakeType;
import com.zenyte.game.content.combatachievements.combattasktiers.EasyTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MediumTasks;
import com.zenyte.game.content.skills.magic.Magic;
import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.content.skills.magic.spells.MagicSpell;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.Toxins;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.MagicCombat;
import com.zenyte.game.world.entity.player.action.combat.magic.CombatSpell;
import lombok.val;

/**
 * @author Tommeh | 14/05/2019 | 10:23
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class Obor extends NPC implements CombatScript {

    private boolean hasNotBeenKnockedBack;
    private boolean hasBeenHitOffPrayer;

    private static final Animation AUTO_ATTACK_ANIM = new Animation(4652);
    private static final Animation STAMP_ATTACK_ANIM = new Animation(7183);
    private static final Animation STAMP_ONHIT_ANIM = new Animation(7212);
    private static final Animation KNOCKBACK_ONHIT_ANIM = new Animation(1157);
    private static final Animation PUMMEL_ATTACK_ANIM = new Animation(4666);
    private static final Animation PUMMEL_ONHIT_ANIM = new Animation(7210);
    private static final Graphics STAMP_ATTACK_GFX = new Graphics(140);
    private static final Graphics FALLING_ROCKS_GFX = new Graphics(60, 0, 92);

    public Obor(final Location tile) {
        super(NpcId.OBOR, tile, Direction.SOUTH, 3);
        setSpawned(true);
        hasNotBeenKnockedBack = true;
        hasBeenHitOffPrayer = false;
    }

    @Override
    public double getRangedPrayerMultiplier() {
        return 0.5;
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        if (source != null) {
            if (source instanceof Player) {
                val player = (Player) source;
                if ((super.isStunned() || super.isFrozen() || super.isMovementRestricted()) && !player.getBooleanAttribute("easy-combat-achievement16")) {
                    player.putBooleanAttribute("easy-combat-achievement16", true);
                    EasyTasks.sendEasyCompletion(player, 16);
                }
                if (player.getCombatDefinitions().getAutocastSpell() == CombatSpell.FIRE_BLAST && !player.getBooleanAttribute("easy-combat-achievement25")) {
                    player.putBooleanAttribute("easy-combat-achievement25", true);
                    EasyTasks.sendEasyCompletion(player, 25);
                }
                if (hasNotBeenKnockedBack && !player.getBooleanAttribute("medium-combat-achievement22")) {
                    player.putBooleanAttribute("medium-combat-achievement22", true);
                    MediumTasks.sendMediumCompletion(player, 22);
                }
                if (!hasBeenHitOffPrayer && !player.getBooleanAttribute("medium-combat-achievement28")) {
                    player.putBooleanAttribute("medium-combat-achievement28", true);
                    MediumTasks.sendMediumCompletion(player, 28);
                }
            }
        }
    }

    @Override
    public int attack(final Entity target) {
        val attack = Utils.random(10);
        if (attack <= 2) { //stamp attack
            if (!(target instanceof Player)) {
                return getCombatDefinitions().getAttackSpeed();
            }
            val player = (Player) target;
            val location = new Location(player.getLocation());
            setAnimation(STAMP_ATTACK_ANIM);
            delayHit(this, 1, target, new Hit(this, getRandomMaxHit(this, 26, RANGED, target), HitType.REGULAR).onLand(hit -> {
                if (hit.getDamage() != 0) {
                    hasBeenHitOffPrayer = true;
                }
            }));
            if (!player.getPrayerManager().isActive(Prayer.PROTECT_FROM_MISSILES)) {
                hasBeenHitOffPrayer = true;
            }
            WorldTasksManager.schedule(new WorldTask() {
                int ticks;

                @Override
                public void run() {
                    if (ticks == 0) {
                        //TODO find the correct player anim
                        player.setAnimation(STAMP_ONHIT_ANIM);
                        player.getPacketDispatcher().sendCameraShake(CameraShakeType.UP_AND_DOWN, 20, 5, 0);
                        World.sendGraphics(STAMP_ATTACK_GFX, getFaceLocation(target, 2, 1900));
                        World.sendGraphics(FALLING_ROCKS_GFX, location);
                    } else if (ticks == 1) {
                        player.getPacketDispatcher().resetCamera();
                    }
                    ticks++;
                }
            }, 0, 0);
        } else if (attack == 3) { //knockback attack
            val location = getLocation();
            double degrees = Math.toDegrees(Math.atan2(target.getY() - location.getY(), location.getX() - location.getX()));
            if (degrees < 0) {
                degrees += 360;
            }
            val angle = Math.toRadians(degrees);
            val px = (int) Math.round(location.getX() + (getSize() + 6) * Math.cos(angle));
            val py = (int) Math.round(location.getY() + (getSize() + 6) * Math.sin(angle));
            val tiles = Utils.calculateLine(target.getX(), target.getY(), px, py, target.getPlane());
            if (!tiles.isEmpty()) {
                tiles.remove(0);
            }
            val destination = new Location(target.getLocation());
            for (val tile : tiles) {
                val dir = Utils.getMoveDirection(tile.getX() - destination.getX(), tile.getY() - destination.getY());
                if (dir == -1) {
                    continue;
                }
                if (!World.checkWalkStep(destination.getPlane(), destination.getX(), destination.getY(), dir, target.getSize(), false, false))
                    break;
                destination.setLocation(tile);
            }
            val direction = Utils.getFaceDirection(target.getX() - destination.getX(), target.getY() - destination.getY());
            if (!destination.matches(target)) {
                target.setForceMovement(new ForceMovement(destination, 30, direction));
                target.lock();
            }
            if (destination.getTileDistance(target.getLocation()) > 1) {
                hasNotBeenKnockedBack = false;
            }
            setAnimation(AUTO_ATTACK_ANIM);
            target.setAnimation(KNOCKBACK_ONHIT_ANIM);
            WorldTasksManager.schedule(new WorldTask() {
                int ticks;
                @Override
                public void run() {
                    if (ticks == 0) {
                        if (!destination.matches(target)) {
                            target.setForceMovement(new ForceMovement(destination, 30, direction));
                            target.lock();
                        }
                        target.faceEntity(Obor.this);
                    } else if (ticks == 1) {
                        delayHit(Obor.this, 0, target, new Hit(Obor.this, getRandomMaxHit(Obor.this, 22, MELEE, target), HitType.REGULAR).onLand(hit -> {
                            if (hit.getDamage() != 0) {
                                hasBeenHitOffPrayer = true;
                            }
                        }));
                        target.setLocation(destination);
                        target.unlock();
                        stop();
                    }
                    ticks++;
                }
            }, 0, 0);
        } else if (attack == 4) { //pummel attack
            setAnimation(PUMMEL_ATTACK_ANIM);
            WorldTasksManager.schedule(() -> {
                target.stun(2);
                target.setAnimation(PUMMEL_ONHIT_ANIM);
            });
            delayHit(this, 1, target, new Hit(this, getRandomMaxHit(this, 22, MELEE, target), HitType.MELEE).onLand(hit -> {
                if (hit.getDamage() != 0) {
                    hasBeenHitOffPrayer = true;
                }
            }));
        } else { //regular auto attack
            setAnimation(AUTO_ATTACK_ANIM);
            delayHit(this, 1, target, new Hit(this, getRandomMaxHit(this, 22, MELEE, target), HitType.MELEE).onLand(hit -> {
                if (hit.getDamage() != 0) {
                    hasBeenHitOffPrayer = true;
                }
            }));
        }
        return getCombatDefinitions().getAttackSpeed();
    }
}
