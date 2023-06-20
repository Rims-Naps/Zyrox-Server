package com.zenyte.game.content.godwars.npcs;

import com.zenyte.game.content.achievementdiary.diaries.FremennikDiary;
import com.zenyte.game.content.boss.BossRespawnTimer;
import com.zenyte.game.content.godwars.instance.GodwarsInstance;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import com.zenyte.game.world.region.GlobalAreaManager;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import static com.zenyte.game.content.godwars.objects.GodwarsBossDoorObject.getInstanceChamberCount;

/**
 * @author Kris | 20/04/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public abstract class GodwarsBossNPC extends KillcountNPC implements Spawnable, CombatScript {

    protected GodwarsBossNPC(int id, Location tile, Direction facing, int radius) {
        super(id, tile, facing, radius);
        setTargetType(EntityType.PLAYER);
        this.setAggressionDistance(30);
        this.radius = 30;
        this.maxDistance = 50;
    }

    final void setMinions(@NotNull final GodwarsBossMinion[] minions) {
        this.minions = minions;
        for (val minion : minions) {
            minion.setBossNPC(this);
        }
    }

    GodwarsBossMinion[] minions;
    private long lastQuote = Utils.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15);
    private int healthResetTicks = 15;
    WeakReference<Entity> minionTarget;

    abstract BossRespawnTimer timer();

    @Override
    public int getRespawnDelay() {
        val timer = timer();
        if (getX() >= 6400) {
            return timer.getDefaultTimer();
        }
        return timer.getTimer().intValue();
    }

    @Override
    public void autoRetaliate(final Entity source) {
        if (combat.getTarget() == source)
            return;
        val target = combat.getTarget();
        if (target != null) {
            if (!isProjectileClipped(target, false) && getAttackingDelay() + 1200 > System.currentTimeMillis()) {
                return;
            }
        }
        if (!combat.isForceRetaliate()) {
            if (target != null) {
                if (target instanceof Player) {
                    val player = (Player) target;
                    if (player.getActionManager().getAction() instanceof PlayerCombat) {
                        val combat = (PlayerCombat) player.getActionManager().getAction();
                        if (combat.getTarget() == this) {
                            return;
                        }
                    }
                } else {
                    val npc = (NPC) target;
                    if (npc.getCombat().getTarget() == this) return;
                }
            }
        }
        this.randomWalkDelay = 1;
        resetWalkSteps();
        val previousTarget = combat.getTarget();
        combat.setTarget(source);
        if (previousTarget == null && combat.getCombatDelay() == 0) {
            combat.setCombatDelay(2);
        }
    }

    @Override
    public void applyHit(Hit hit) {
        super.applyHit(hit);
        if (isDead() || isFinished()) {
            return;
        }
        val source = hit.getSource();
        if (source instanceof Player) {
            minionTarget = new WeakReference<>(source);
            for (val minion : minions) {
                if (minion == null || minion.isDead() || minion.isFinished()) {
                    continue;
                }
                minion.getCombat().setTarget(source);
            }
        }
    }

    @Override
    public NPC spawn() {
        for (val minion : minions) {
            if (minion.isFinished()) {
                minion.spawn();
            }
        }
        return super.spawn();
    }

    @Override
    public void processNPC() {
        super.processNPC();
        if (lastQuote < Utils.currentTimeMillis()) {
            lastQuote = Utils.currentTimeMillis() + TimeUnit.SECONDS.toMillis(Utils.random(10, 25));
            val quotes = getQuotes();
            if (quotes != null) {
                setForceTalk(quotes[Utils.random(quotes.length - 1)]);
            }
        }
        if (--healthResetTicks <= 0) {
            healthResetTicks = 15;
            if (getHitpoints() < getMaxHitpoints()) {
                val area = GlobalAreaManager.getArea(getLocation());
                assert area != null;
                val count = area instanceof GodwarsInstance ? getInstanceChamberCount(area) : area.getPlayers().size();
                if (count == 0) {
                    this.setHitpoints(getMaxHitpoints());
                    this.getReceivedDamage().clear();
                }
            }
        }
        if (minionTarget != null) {
            val t = minionTarget.get();
            if (t != null) {
                if (t.isProjectileClipped(getLocation(), true)) {
                    minionTarget = null;
                }
            }
        }
    }

    abstract ForceTalk[] getQuotes();

    @Override
    protected boolean isAcceptableTarget(final Entity target) {
        return true;
    }

    @Override
    public boolean isEntityClipped() {
        return false;
    }

    @Override
    public boolean isTolerable() {
        return false;
    }

    abstract int diaryFlag();

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        if (!(source instanceof Player)) {
            return;
        }
        val player = (Player) source;
        player.getAchievementDiaries().update(FremennikDiary.KILL_GODWARS_GENERALS, diaryFlag());
        minionTarget = null;
    }

}
