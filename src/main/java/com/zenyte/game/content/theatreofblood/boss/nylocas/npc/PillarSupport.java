package com.zenyte.game.content.theatreofblood.boss.nylocas.npc;

import com.zenyte.game.content.theatreofblood.boss.nylocas.NylocasRoom;
import com.zenyte.game.content.theatreofblood.boss.nylocas.model.PillarLocation;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.EntityHitBar;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.npc.NPCCombat;
import com.zenyte.game.world.object.WorldObject;
import lombok.Getter;
import lombok.val;

/**
 * @author Tommeh | 6/7/2020 | 12:34 AM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class PillarSupport extends TheatreNPC<NylocasRoom> {

    public static final Animation crumbleAnimation = new Animation(8074);

    @Getter
    public PillarLocation type;

    @Getter
    private WorldObject object;

    public PillarSupport(final NylocasRoom room, final PillarLocation type) {
        super(room.getRaid(), room, 8358, room.getLocation(type.getLocation()), Direction.SOUTH);
        this.type = type;
        this.object = new WorldObject(32863, 10, type.getRotation(), room.getLocation(type.getLocation()));
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

        combatDefinitions.setHitpoints(380 - (partySize * 20));
        setHitpoints(combatDefinitions.getHitpoints());
    }

    @Override
    public int getMaxHitpoints() {
        int partySize = getRaid().getParty().getSize();
        return 380 - (partySize * 20);
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
    protected void processHit(final Hit hit) {
        /*if (isDead()) {
            return;
        }*/
        if (isImmune(hit.getHitType())) {
            hit.setDamage(0);
        }
        if (hit.getDamage() > Short.MAX_VALUE) {
            hit.setDamage(Short.MAX_VALUE);
        }
        if (hit.getDamage() > getHitpoints()) {
            hit.setDamage(getHitpoints());
        }
        getUpdateFlags().flag(UpdateFlag.HIT);
        addHitbar();
        if (hit.getHitType() == HitType.HEALED) {
            heal(hit.getDamage());
        } else {
            removeHitpoints(hit);
        }
        postHitProcess();
    }

    @Override
    public void reset() {
        receivedHits.clear();
        walkSteps.clear();
        toxins.reset();
        receivedDamage.clear();
        hitBars.clear();
        nextHits.clear();
    }

    @Override
    protected void onDeath(final Entity source) {
        super.onDeath(source);
        val party = raid.getParty();

        for (val m : party.getTargetableMembers()) {
            val member = RaidingParty.getPlayer(m);
            if (member == null || !raid.getParty().getAlivePlayers().contains(member)) {
                continue;
            }
            WorldTasksManager.schedule(() -> {
                member.applyHit(new Hit(this, Utils.random(20, 35), HitType.REGULAR));
            });
        }

        World.sendObjectAnimation(object, crumbleAnimation);
        room.getPillars().remove(type);
        for (val p : raid.getParty().getPlayers()) {
            p.putBooleanAttribute("PerfectNylocas", false);
        }
        if (!room.getPillars().isEmpty()) {
            return;
        }

        for (val m : party.getMembers()) {
            val member = RaidingParty.getPlayer(m);
            if (member == null) {
                continue;
            }
            getRoom().wipeTeam();
        }
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
            return 5;
        }

    }
}
