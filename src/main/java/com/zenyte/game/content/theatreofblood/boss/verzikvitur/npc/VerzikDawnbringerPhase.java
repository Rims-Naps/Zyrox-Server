package com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc;

import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.object.VerzikPillarLocation;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
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
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

import java.util.ArrayList;

public class VerzikDawnbringerPhase extends VerzikPhase {

    private int ticksUntilAttack = -1;
    private int ticksUntilAnim = 4;
    private int attackNum = 0;
    private int animNum = 0;
    private int randomAnimNum = -1;

    public VerzikDawnbringerPhase(VerzikVitur verzik) {
        super(verzik, 2);
    }

    @Override
    public void onPhaseStart() {
        verzik.setTransformation(NpcId.VERZIK_VITUR_8370);
        verzik.getRoom().getPillars().forEach((l , p) -> {
            p.getCombatDefinitions().setHitpoints(380 - (verzik.getRaid().getParty().getTargetableMembers().size() * 50));
            p.setHitpoints(p.getCombatDefinitions().getHitpoints());
        });
        verzik.getRaid().getActiveRoom().refreshHealthBar(verzik.getRaid());
        verzik.setAttackAble(true);
        verzik.setWalkAble(false);
    }

    @Override
    public void onTick() {
        if(attackNum == 0) {
            if (ticksUntilAnim == 0 && ticksUntilAttack == -1) {
                verzik.setAnimation(new Animation(8110));
                ticksUntilAnim = 4;
                animNum++;
                if(animNum == 3) {
                    ticksUntilAttack = 4;
                    ticksUntilAnim = -1;
                }
            }
        } else if(attackNum == 1) {
            if(ticksUntilAnim == 0 && ticksUntilAttack == -1) {
                verzik.setAnimation(new Animation(8110));
                if(animNum < (verzik.getRaid().getParty().getTargetablePlayers().size() > 1 ? 2 : 1)) {
                    ticksUntilAnim = 4;
                    animNum++;
                } else {
                    ticksUntilAttack = 4;
                    ticksUntilAnim = -1;
                }
            }
        } else if (attackNum > 1) {
            if(randomAnimNum == -1) {
                randomAnimNum = Utils.random(1, 2);
            } else {
                if (ticksUntilAnim == 0 && ticksUntilAttack == -1) {
                    verzik.setAnimation(new Animation(8110));
                    if (animNum < randomAnimNum) {
                        ticksUntilAnim = 4;
                        animNum++;
                    } else {
                        ticksUntilAttack = 4;
                        ticksUntilAnim = -1;
                        randomAnimNum = -1;
                    }
                }
            }
        }

        if(ticksUntilAnim > 0) {
            ticksUntilAnim--;
        }


        if(ticksUntilAttack == 0) {
            ArrayList<Entity> targs = getTargets();

            if(targs.size() > 0) {
                verzik.setAnimation(new Animation(8109));

                for(Entity e : targs) {
                    Projectile proj = new Projectile(1580, 100, 25, 62, 20, 75, 100, 0);
                    World.sendProjectile(verzik.getMiddleLocation(), e instanceof Player ? (Player)e : new Location(e.getLocation().getX() + 1, e.getLocation().getY() + 1),  proj);
                    WorldTasksManager.schedule(new WorldTask() {
                        @Override
                        public void run() {
                            if(e instanceof Player) {
                                Player p = (Player)e;
                                if(p.getPrayerManager().getActivePrayers().containsKey(Prayer.PROTECT_FROM_MAGIC)) {
                                    p.applyHit(new Hit(verzik, Utils.random(68), HitType.MAGIC));
                                } else {
                                    p.applyHit(new Hit(verzik, Utils.random(136), HitType.MAGIC));
                                }
                                for (val player : VerSinhazaArea.getParty(p).getPlayers()) {
                                    player.putBooleanAttribute("PerfectVerzik", false);
                                }
                            } else {
                               VerzikPillar pill = (VerzikPillar) e;
                               pill.applyHit(new Hit(verzik, Utils.random(pill.getMaxHitpoints() / 8, pill.getMaxHitpoints() / 4), HitType.MAGIC));
                               //Graphics(id = 1548, delay = 106, height = 60)
                               World.sendGraphics(new Graphics(1582, 0, 0), pill.getMiddleLocation());
                            }
                        }
                    }, (proj.getDuration() / 30) + 1);
                }

                ticksUntilAnim = 4;
                attackNum++;
                animNum = 0;
                ticksUntilAttack = -1;
            }
        } else if(ticksUntilAttack > 0) {
            ticksUntilAttack--;
        }
    }

    private ArrayList<Entity> getTargets() {
        ArrayList<Entity> targets = new ArrayList<Entity>();
        for(Player p : verzik.getRaid().getParty().getTargetablePlayers()) {
            boolean inSafespot = false;
            Entity target = null;
            for(VerzikPillarLocation loc : verzik.getRoom().getPillars().keySet()) {
                for(Location safespot : loc.getSafespots()) {
                    if(verzik.getRoom().getStaticLocation(p.getLocation()).equals(safespot.getX(), safespot.getY(), safespot.getPlane())) {
                        inSafespot = true;
                        VerzikPillar pillar = verzik.getRoom().getPillars().get(loc);
                        if(!targets.contains(pillar)) {
                            target = verzik.getRoom().getPillars().get(loc);
                        }
                    }
                }
            }
            if(!inSafespot) {
                target = p;
            }
            if(target != null) {
                targets.add(target);
            }
        }
        return targets;
    }



    @Override
    public boolean isPhaseComplete() {
        return verzik.getHitpoints() == 0;
    }

    @Override
    public VerzikPhase advance() {
        WorldTasksManager.schedule(() -> {
            if(verzik.getRoom().getPillars().size() > 0) {
                for (VerzikPillarLocation pill : VerzikPillarLocation.values()) {
                    if (verzik.getRoom().getPillars().get(pill) != null) {
                        verzik.getRoom().getPillars().get(pill).collapsePillar();
                    }
                }
            }
        });
        verzik.setAttackAble(false);
        verzik.blockIncomingHits();
        verzik.getNextHits().clear();
        verzik.setHitpoints(verzik.getMaxHitpoints());
        verzik.setWalkAble(true);
        return new VerzikPhase2(verzik);
    }
}
