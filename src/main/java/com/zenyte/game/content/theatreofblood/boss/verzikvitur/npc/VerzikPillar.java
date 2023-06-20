package com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc;

import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.VerzikRoom;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.object.VerzikPillarLocation;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.EntityHitBar;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NPCCombat;
import com.zenyte.game.world.object.WorldObject;
import lombok.Getter;
import lombok.val;

public class VerzikPillar extends TheatreNPC<VerzikRoom> {

    public static final Animation crumbleAnimation = new Animation(8055);

    @Getter
    public VerzikPillarLocation type;

    @Getter
    private WorldObject object;

    @Getter
    private NPC fallingPillar;

    public VerzikPillar(final VerzikRoom room, final VerzikPillarLocation type) {
        super(room.getRaid(), room, 8379, room.getLocation(type.getLocation()), Direction.SOUTH);
        this.type = type;
        this.object = new WorldObject(32687, 10, Direction.SOUTH.getDirection(), room.getLocation(type.getLocation()));
        this.hitBar = new PillarSupportHitBar(this);
        this.combat = new NPCCombat(this) {
            @Override
            public void setTarget(final Entity target) { }
            @Override
            public void forceTarget(final Entity target) { }
        };
        setTargetType(EntityType.NPC);
    }


    @Override
    protected void setStats() {
        val partySize = getRaid().getParty().getSize();
        combatDefinitions.setHitpoints(380 - (partySize * 50));
        setHitpoints(380 - (partySize * 50));
    }

    @Override
    public int getMaxHitpoints() {
        int partySize = getRaid().getParty().getSize();
        return 380 - (partySize * 50);
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
    public boolean isDead() {
        return false;
    }

    @Override
    protected void processHit(final Hit hit) {
        if (hit.getDamage() > Short.MAX_VALUE) {
            hit.setDamage(Short.MAX_VALUE);
        }
        if (hit.getDamage() > getHitpoints()) {
            hit.setDamage(getHitpoints());
        }

        getUpdateFlags().flag(UpdateFlag.HIT);
        nextHits.add(hit);
        addHitbar();
        if (hit.getHitType() == HitType.HEALED) {
            heal(hit.getDamage());
        } else {
            removeHitpoints(hit);
        }
        postHitProcess();
    }

    @Override
    public void postHitProcess() {
        /*if(getHitpoints() == 0) {
            setHitpoints(getMaxHitpoints());
        }*/
    }

   /* @Override
    public void updateCombatDefinitions() {
        getCombatDefinitions().setHitpoints(380 - (getRaid().getParty().getSize() * 50));
    }*/

    @Override
    public void reset() {
        receivedHits.clear();
        walkSteps.clear();
        toxins.reset();
        receivedDamage.clear();
        hitBars.clear();
        nextHits.clear();
    }

    public void initiateFall() {
        collapsePillar();
        room.getPillars().remove(type);
        if (!room.getPillars().isEmpty()) {
            return;
        }
        getRoom().wipeTeam();
    }

    @Override
    protected void onDeath(final Entity source) {
        deathDelay = 50;
        initiateFall();
    }

    @Override
    protected boolean isMovableEntity() {
        return false;
    }

    private static final class PillarSupportHitBar extends EntityHitBar {

        PillarSupportHitBar(final Entity entity) {
            super(entity);
        }

        @Override
        public int getSize() {
            return 3;
        }

    }

    public void collapsePillar() {
        fallingPillar = new NPC(8377, new Location(getX() - 1, getY() - 1, getPlane()), Direction.SOUTH, 0);
        fallingPillar.setSpawned(true);
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                if(ticks == 0) {
                    fallingPillar.spawn();
                    fallingPillar.setAnimation(new Animation(8052));
                    World.removeObject(object);
                }
                if(ticks == 1) {
                    val party = getRaid().getParty();
                    for (val m : party.getTargetableMembers()) {
                        val member = RaidingParty.getPlayer(m);
                        if (member == null) {
                            continue;
                        }
                        if (member.getLocation().withinDistance(getMiddleLocation().getX(), getMiddleLocation().getY(), 3)) {
                            member.applyHit(new Hit(VerzikPillar.this, Utils.random(20, 35), HitType.REGULAR));
                        }
                        for (val p : VerSinhazaArea.getParty(member).getPlayers()) {
                            p.putBooleanAttribute("PerfectVerzik", false);
                        }
                    }
                }
                if(ticks == 2) {
                    fallingPillar.setTransformation(8378);
                    fallingPillar.setAnimation(new Animation(8104));
                }
                if(ticks == 4) {
                    fallingPillar.finish();
                    stop();
                }/*
                if(ticks == 5) {

                }*/
                ticks++;
            }
        }, 0, 1);
    }
}