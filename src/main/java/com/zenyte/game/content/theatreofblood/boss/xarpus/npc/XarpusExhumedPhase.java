package com.zenyte.game.content.theatreofblood.boss.xarpus.npc;

import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import kotlin.Pair;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Chris
 * @since August 25 2020
 */
public class XarpusExhumedPhase extends XarpusPhase {
    /**
     * The amount of ticks before Xarpus starts spawning exhumed.
     */
    private static final int START_DELAY = (int) TimeUnit.SECONDS.toTicks(4);
    /**
     * The amount of ticks before next phase starts after Xarpus finishes spawning exhumed.
     */
    private static final int END_DELAY = (int) TimeUnit.SECONDS.toTicks(4);
    private static final Graphics exhumedDespawnGfx = new Graphics(1549);
    private static final Projectile exhumedHealProj = new Projectile(1550, 0, 80, 0, 60, 30, 0, 1);
    /**
     * The total amount of exhumeds that will be spawned during phase one.
     */
    private final int maxExhumeds;
    /**
     * The amount of health an exhumed will heal per tick if not stood on.
     */
    private final int exhumedHealAmount;
    /**
     * The amount of ticks to wait to spawn the next exhumed.
     */
    private final int exhumedSpawnInterval;
    /**
     * The amount of ticks an exhumed will stay in the fight for until it is removed.
     */
    private final int exhumedLifespan;
    /**
     * A list of currently active exhumeds.
     */
    private final List<Pair<Integer, WorldObject>> exhumeds = new ObjectArrayList<>();
    /**
     * The total amount of exhumeds spawned since the start of this fight.
     */
    private int exhumedIndex = 0;

    private Object2IntOpenHashMap<WorldObject> healTimes = new Object2IntOpenHashMap<WorldObject>();

    public XarpusExhumedPhase(@NotNull final Xarpus xarpus, final int maxSpawns, final int healAmount, final int spawnInterval, final int lifespan) {
        super(xarpus);
        this.maxExhumeds = maxSpawns;
        this.exhumedHealAmount = healAmount;
        this.exhumedSpawnInterval = spawnInterval;
        this.exhumedLifespan = lifespan;
    }

    @Override
    void onPhaseStart() {
        xarpus.setHitpoints(xarpus.getMaxHitpoints() - (exhumedHealAmount * exhumedLifespan * maxExhumeds));
        xarpus.getRoom().refreshHealthBar(xarpus.getRoom().getRaid());
    }

    @Override
    void onTick() {
        if (xarpus.getTicks().getValue() <= START_DELAY) {
            return;
        }
        if (canSpawnExhumed()) {
            spawnExhumed();
        }
        processActiveExhumeds();
    }

    private boolean canSpawnExhumed() {
        return xarpus.getTicks().getValue() % exhumedSpawnInterval == 0 && exhumedIndex < maxExhumeds;
    }

    private void spawnExhumed() {
        val exhumed = new WorldObject(ObjectId.EXHUMED, 22, 0, xarpus.getLocation().random(6, 8, 6, 8, 0, 2, 0, 2));
        val pair = new Pair<>(xarpus.getTicks().getValue(), exhumed);
        exhumeds.add(pair);
        healTimes.put(exhumed,0);
        exhumedIndex++;

        WorldTasksManager.schedule(() -> World.sendObjectAnimation(exhumed, new Animation(8065)));

        World.spawnTemporaryObject(exhumed, exhumedLifespan + 1, () -> {
            exhumeds.remove(pair);
            World.sendGraphics(exhumedDespawnGfx, exhumed.getPosition());
        });
    }

    private void processActiveExhumeds() {
        exhumeds.forEach((pair) -> {
            if (canExhumedHeal(pair.getFirst(), pair.getSecond())) {
                WorldTasksManager.schedule(() -> {
                    xarpus.hitsplatHeal(exhumedHealAmount);
                    xarpus.setHealedAmount(xarpus.getHealedAmount() + exhumedHealAmount);
                    healTimes.replace(pair.getSecond(), healTimes.getInt(pair.getSecond()) + 1);
                }, Math.max(0, World.sendProjectile(pair.getSecond(), xarpus, exhumedHealProj) - 1));
            }
        });
    }

    private boolean canExhumedHeal(final int startTick, @NotNull final WorldObject exhumedObject) {
        for (val memberName : xarpus.getRaid().getParty().getMembers()) {
            val member = RaidingParty.getPlayer(memberName);
            if (member == null) {
                continue;
            }
            if (member.getLocation().getPositionHash() == exhumedObject.getPositionHash()) {
                return false;
            }
        }
        return xarpus.getTicks().getValue() >= startTick + 3 // First three ticks are used for animation.
                && xarpus.getTicks().getValue() < startTick + 11;
    }

    @Override
    boolean isPhaseComplete() {
        return xarpus.getTicks().getValue() >= maxExhumeds * exhumedSpawnInterval + exhumedLifespan + START_DELAY + END_DELAY;
    }

    @Override
    XarpusPhase advance() {
        healTimes.forEach((exhumed, times) -> {
            if (times > 2) {
                for (val p : xarpus.getRaid().getParty().getPlayers()) {
                    p.putBooleanAttribute("PerfectXarpus",false);
                }
            }
        });
        return new XarpusPoisonPhase(xarpus);
    }
}
