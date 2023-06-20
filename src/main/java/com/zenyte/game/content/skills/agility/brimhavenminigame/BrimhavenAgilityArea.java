package com.zenyte.game.content.skills.agility.brimhavenminigame;

import com.zenyte.game.HintArrow;
import com.zenyte.game.HintArrowPosition;
import com.zenyte.game.RuneDate;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.packet.PacketDispatcher;
import com.zenyte.game.packet.out.SetHintArrow;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.npc.impl.kalphite.KalphiteQueen;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.CycleProcessPlugin;
import lombok.val;
import org.checkerframework.checker.units.qual.A;

/**
 * @author Cresinkel
 */
public class BrimhavenAgilityArea extends Area implements CycleProcessPlugin {


    @Override
    public void process() {
        for(Player p : players) {
            if (p.getNumericAttribute("timeOfNewTargetDispenser").intValue() >= 100
                && p.getVarManager().getBitValue(TicketDispenser.BRIMHAVEN_AGILITY_STREAK_VARBIT) == 1) {
                TicketDispenser.reset(p);
            }
            if (p.getVarManager().getBitValue(TicketDispenser.BRIMHAVEN_AGILITY_STREAK_VARBIT) == 1) {
                p.getVarManager().sendBit(TicketDispenser.BRIMHAVEN_AGILITY_COLOR_VARBIT, 1);
                p.getAttributes().put("timeOfNewTargetDispenser", p.getNumericAttribute("timeOfNewTargetDispenser").intValue() + 1);
            }
            for(Obstacles obstacle : Obstacles.values()) {
                if (obstacle.type == 1) {
                    if (p.getLocation().equals(obstacle.getLocation())) {
                        if (p.getNextWalkStepPeek() > 0) {
                            return;
                        }
                        if (Utils.random(5) == 0) {
                            p.setForceMovement(new ForceMovement(obstacle.getLocation(), 25, obstacle.getPushTo(), 45, obstacle.getDirection()));
                            p.setLocation(obstacle.getPushTo());
                            p.setAnimation(new Animation(1125, 25));
                            World.sendGraphics(new Graphics(271, 0, 0), obstacle.getLocation());
                            CombatUtilities.processHit(p, new Hit(Utils.random(2, 5), HitType.REGULAR));
                            p.lock(1);
                        } else {
                            p.setForceMovement(new ForceMovement(obstacle.getLocation(), 25, obstacle.getForceTo(), 45, obstacle.getDirection()));
                            p.setLocation(obstacle.getForceTo());
                            p.setAnimation(new Animation(1115, 25));
                            World.sendGraphics(new Graphics(271, 25, 0), obstacle.getLocation());
                            p.lock(4);
                        }
                    }
                }
                if (obstacle.type == 2) {
                    if (p.getLocation().equals(obstacle.getLocation())) {
                        if (p.getNextWalkStepPeek() > 0) {
                            return;
                        }
                        if (Utils.random(5) == 0) {
                            p.setForceMovement(new ForceMovement(obstacle.getLocation(), 25, obstacle.getPushTo(), 45, obstacle.getDirection()));
                            p.setLocation(obstacle.getPushTo());
                            p.setAnimation(new Animation(1125, 25));
                            World.sendObjectAnimation(obstacle.getTrapObject(), new Animation(1107, 25));
                            CombatUtilities.processHit(p, new Hit(Utils.random(2, 5), HitType.REGULAR));
                            p.lock(1);
                        } else {
                            p.setForceMovement(new ForceMovement(obstacle.getLocation(), 25, obstacle.getForceTo(), 45, obstacle.getDirection()));
                            p.setLocation(obstacle.getForceTo());
                            p.setAnimation(new Animation(1115, 25));
                            World.sendObjectAnimation(obstacle.getTrapObject(), new Animation(1107));
                            p.lock(4);
                        }
                    }
                }
                if (obstacle.type == 3) {
                    if (p.getLocation().equals(obstacle.getLocation())) {
                        if (p.getNextWalkStepPeek() > 0 || p.isLocked()) {
                            return;
                        }
                        if (Utils.random(5) == 0) {
                            p.setForceMovement(new ForceMovement(obstacle.getLocation(), 25, obstacle.getPushTo(), 45, obstacle.getDirection()));
                            p.setLocation(obstacle.getPushTo());
                            World.sendProjectile(obstacle.getTrapObject().getPosition(), p.getLocation(), new Projectile(270, 20, 20, 20, 0, 30, 0, 1));
                            p.setAnimation(new Animation(1114, 60));
                            CombatUtilities.processHit(p, new Hit(Utils.random(2, 5), HitType.REGULAR));
                            p.drainSkill(Skills.AGILITY, 2);
                            p.lock(2);
                        } else {
                            p.lock(5);
                            World.sendProjectile(obstacle.getTrapObject().getPosition(), new Location(obstacle.getPushTo().getX() + (obstacle.getDirection() == ForceMovement.WEST ? 3 : obstacle.getDirection() == ForceMovement.EAST ? -3 : 0), obstacle.getPushTo().getY() + (obstacle.getDirection() == ForceMovement.NORTH ? -3 : obstacle.getDirection() == ForceMovement.SOUTH ? 3 : 0), 3), new Projectile(270, 20, 20, 20, 0, 50, 0, 1));
                            p.setAnimation(new Animation(1110, 20));
                            WorldTasksManager.schedule(() -> {
                            p.addWalkSteps(obstacle.getForceTo().getX(), obstacle.getForceTo().getY(), -1, false);
                            }, 4);
                        }
                    }
                }
                if (obstacle.type == 4) {
                    if (p.getLocation().equals(obstacle.getLocation())) {
                        if (p.getNextWalkStepPeek() > 0) {
                            return;
                        }
                        if (Utils.random(5) == 0) {
                            p.setForceMovement(new ForceMovement(obstacle.getLocation(), 25, obstacle.getPushTo(), 45, obstacle.getDirection()));
                            p.setLocation(obstacle.getPushTo());
                            p.setAnimation(new Animation(1125, 25));
                            World.sendObjectAnimation(obstacle.getTrapObject(), new Animation(1111, 25));
                            CombatUtilities.processHit(p, new Hit(Utils.random(2, 5), HitType.REGULAR));
                            p.lock(1);
                        } else {
                            p.setForceMovement(new ForceMovement(obstacle.getLocation(), 25, obstacle.getForceTo(), 45, obstacle.getDirection()));
                            p.setLocation(obstacle.getForceTo());
                            p.setAnimation(new Animation(1115, 25));
                            p.lock(4);
                            WorldTasksManager.schedule(new WorldTask() {
                                int ticks;
                                @Override
                                public void run() {
                                    if (ticks == 1) {
                                        World.sendObjectAnimation(obstacle.getTrapObject(), new Animation(1111));
                                        stop();
                                    }
                                    ticks++;
                                }

                            }, 0, 1);
                        }
                    }
                }
                if (obstacle.type == 5) {
                    if (!obstacle.getTrapObject().isLocked())
                    WorldTasksManager.schedule(new WorldTask() {
                        int ticks;
                        @Override
                        public void run() {
                            if (ticks == 0) {
                                obstacle.getTrapObject().setLocked(true);
                                World.removeObject(obstacle.getTrapObject());
                                if (obstacle.getTrapObject().getId() == 3568) {
                                    WorldObject newObject = new WorldObject(3567, 10, obstacle.getTrapObject().getRotation(), obstacle.getTrapObject().getX(), obstacle.getTrapObject().getY(), 3);
                                    World.spawnObject(newObject);
                                    newObject.setLocked(true);
                                }
                            }
                            if (ticks < 3 && ticks >= 0) {
                                if (p.getLocation().equals(obstacle.getLocation())) {
                                    if (p.getNextWalkStepPeek() <= 0) {
                                        CombatUtilities.processHit(p, new Hit(Utils.random(2, 5), HitType.REGULAR));
                                        p.setForceMovement(new ForceMovement(obstacle.getLocation(), 25, obstacle.getPushTo(), 45, obstacle.getDirection()));
                                        p.setLocation(obstacle.getPushTo());
                                        p.setAnimation(new Animation(1125, 25));
                                        p.lock(1);
                                    }
                                }
                            }
                            if (ticks == 2) {
                                World.spawnObject(obstacle.getTrapObject());
                                if (obstacle.getTrapObject().getId() == 3568) {
                                    WorldObject newObject = new WorldObject(3567, 10, obstacle.getTrapObject().getRotation(), obstacle.getTrapObject().getX(), obstacle.getTrapObject().getY(), 3);
                                    World.removeObject(newObject);
                                    newObject.setLocked(false);
                                }
                            }
                            if (ticks == 7) {
                                obstacle.getTrapObject().setLocked(false);
                                stop();
                            }
                            ticks++;
                        }

                    }, 0, 1);
                }
            }
        }
    }

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[]{ new RSPolygon(new int[][]{
                {2757, 9594},
                {2757, 9542},
                {2809, 9542},
                {2809, 9594},
        })};
    }

    @Override
    public void enter(Player player) {
        GameInterface.BRIMHAVEN_AGILITY.open(player);
    }

    @Override
    public void leave(Player player, boolean logout) {
        TicketDispenser.reset(player);
        player.getInterfaceHandler().closeInterface(GameInterface.BRIMHAVEN_AGILITY);
    }

    @Override
    public String name() {
        return "Brimhaven Agility Arena";
    }
}
