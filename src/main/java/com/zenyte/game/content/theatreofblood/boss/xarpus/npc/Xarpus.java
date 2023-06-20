package com.zenyte.game.content.theatreofblood.boss.xarpus.npc;

import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.theatreofblood.boss.xarpus.XarpusRoom;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ImmutableLocation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.CharacterLoop;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;
import java.util.Set;

/**
 * @author Chris
 * @since August 23 2020
 */
public class Xarpus extends TheatreNPC<XarpusRoom> implements CombatScript {
    private final Set<Entity> underneathPlayers = new ObjectOpenHashSet<>();
    private static final ImmutableLocation SPAWN_LOCATION = new ImmutableLocation(3169, 4386, 1);
    @Getter private final MutableInt ticks = new MutableInt();
    @Getter private XarpusPhase phase;
    @Getter private final List<WorldObject> poisonSplats = new ObjectArrayList<>();
    @Getter @Setter private int healedAmount = 0;

    private final Projectile xarpusTypelessRangedProjectile = new Projectile(1557, 60, 0, 0, 2, 30, 0, 5);

    public Xarpus(final XarpusRoom room) {
        super(room.getRaid(), room, NpcId.XARPUS, room.getLocation(SPAWN_LOCATION), Direction.SOUTH);
        phase = XarpusExhumedPhaseFactory.getPhase(this, room.getRaid().getParty().getSize());
    }

    @Override
    public NPC spawn() {
        super.spawn();
        phase.onPhaseStart();
        return this;
    }

    @Override
    public void processNPC() {
        super.processNPC();

        phase = phase.process();
        if (phase instanceof XarpusPoisonPhase || phase instanceof XarpusCounterPhase) {
            processPoisonSplats();

            underneathPlayers.clear();
            CharacterLoop.populateEntityList(underneathPlayers, getLocation(), getSize(), Player.class,
                    player -> !player.isDead() && !player.isLocked() && Utils.collides(getX(), getY(), getSize(), player.getX(), player.getY(), player.getSize()));
            if (!underneathPlayers.isEmpty()) {
                for (val player : underneathPlayers) {
                    if (player instanceof Player) {
                        val delay = World.sendProjectile(getMiddleLocation(), player.getLocation(), xarpusTypelessRangedProjectile);
                        WorldTasksManager.schedule(() -> {
                            player.applyHit(new Hit(this, Utils.random(5, 8), HitType.RANGED));
                            player.applyHit(new Hit(this, Utils.random(5, 8), HitType.RANGED));
                        }, Math.max(0, delay));
                    }
                }
            }
        }

        ticks.increment();
    }

    private void processPoisonSplats() {
        for (val poisonSplat : poisonSplats) {
            val players = getRaid().getParty().getTargetablePlayers();
            for (val player : players) {
                if (player.getLocation().equals(poisonSplat)) {
                    XarpusPoisonPhase.poison(player);
                }
            }
        }
    }

    @Override
    public void faceEntity(final Entity entity) {
        if (phase instanceof XarpusCounterPhase) {
            return;
        }
        super.faceEntity(entity);
    }

    @Override
    public void setFaceEntity(final Entity entity) {
        if (phase instanceof XarpusCounterPhase) {
            super.setFaceEntity(null);
            return;
        }
        super.setFaceEntity(entity);
    }

    public void hitsplatHeal(final int amount) {
        if (getHitpoints() == getMaxHitpoints()) {
            return;
        }
        applyHit(new Hit(amount, HitType.HEALED));
        room.refreshHealthBar(room.getRaid());
    }

    @Override
    protected void setStats() {
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
        if (phase instanceof XarpusCounterPhase) {
            ((XarpusCounterPhase) phase).counter(hit);
        }
        super.removeHitpoints(hit);
        room.refreshHealthBar(raid);
    }

    @Override
    public void heal(int amount) {
        super.heal(amount);
        room.refreshHealthBar(raid);
    }

    @Override
    public int attack(Entity target) {
        return 0;
    }

    @Override
    public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check) {
        return false;
    }

    @Override
    public void finish() {
        for (val p : room.getPlayers()) {
            if (!raid.getSpectators().contains(p.getUsername())) {
                if (p.getBooleanAttribute("PerfectXarpus")
                        && !p.getBooleanAttribute("master-combat-achievement62")) {
                    p.putBooleanAttribute("master-combat-achievement62", true);
                    MasterTasks.sendMasterCompletion(p, 62);
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
}
