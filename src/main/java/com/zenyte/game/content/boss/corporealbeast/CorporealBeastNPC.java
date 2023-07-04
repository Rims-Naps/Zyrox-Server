package com.zenyte.game.content.boss.corporealbeast;

import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.EntityHitBar;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.game.world.region.GlobalAreaManager;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Kris | 9. veebr 2018 : 6:11.00
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class CorporealBeastNPC extends NPC implements CombatScript {

    private static final Animation stomp = new Animation(1686);

    @Getter
    private final Area cavern;
    private DarkCoreNPC core;

    public CorporealBeastNPC(final Location tile, final Area cavern) {
        super(319, tile, Direction.SOUTH, 5);
        setDamageCap(100);
        this.cavern = cavern;
        hitBar = new CorporealBeastHitBar(this);
        setAggressionDistance(25);
        this.maxDistance = 50;
        this.attackDistance = 15;
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        if (source != null) {
            if (source instanceof Player) {
                val player = (Player) source;
                if (!this.getTemporaryAttributes().containsKey("dark_core_died")
                        && !this.getTemporaryAttributes().containsKey("dark_core_hit_someone")
                        && !player.getBooleanAttribute("elite-combat-achievement47")) {
                    player.putBooleanAttribute("elite-combat-achievement47", true);
                    EliteTasks.sendEliteCompletion(player, 47);
                }
                boolean solo = true;
                for (val hit : this.getReceivedHits()) {
                    val p = (Player) hit.getSource();
                    if (p != player) {
                        solo = false;
                    }
                }
                if (solo && !player.getBooleanAttribute("elite-combat-achievement55")) {
                    player.putBooleanAttribute("elite-combat-achievement55", true);
                    EliteTasks.sendEliteCompletion(player, 55);
                }
                if (!player.getBooleanAttribute("elite-combat-achievement56")) {
                    if (player.getWeapon().getName().toLowerCase().contains("crystal halberd") && player.getBooleanAttribute("used_special")) {
                        player.putBooleanAttribute("elite-combat-achievement56", true);
                        EliteTasks.sendEliteCompletion(player, 56);
                    }
                }
            }
        }
        this.getTemporaryAttributes().remove("dark_core_died");
        this.getTemporaryAttributes().remove("dark_core_hit_someone");
    }

    @Override
    public int getRespawnDelay() {
        return 50;
    }

    @Override
    public boolean isTickEdible() {
        return false;
    }

    @Override
    public double getMagicPrayerMultiplier() {
        return 0.66;
    }

    @Override
    public double getMeleePrayerMultiplier() {
        return 0.66;
    }

    @Override
    public boolean isIntelligent() {
        return true;
    }

    @Override
    public boolean isTolerable() {
        return false;
    }

    public void refreshDamageDealt(final Player player, final int extra) {
        int damage = 0;
        val damageList = getReceivedDamage().get(player.getUsername());
        if (damageList != null) {
            for (val value : damageList) {
                if (value.getLeft() < System.currentTimeMillis()) {
                    continue;
                }
                damage += value.getRight();
            }
        }
        player.getVarManager().sendBit(999, damage + extra);
    }

    @Override
    public void applyHit(final Hit hit) {
        super.applyHit(hit);
        val source = hit.getSource();
        retaliate(source);
        halveDamage(hit);
        refreshDamageDealt(hit);
        checkCore(hit);
    }

    private void checkCore(final Hit hit) {
        if (hit.getDamage() <= 32 || Utils.random(7) != 0 || core != null && !core.isFinished() && !core.isDead()) {
            return;
        }
        if (core != null) {
            if (!core.isCanRespawn()) {
                return;
            }
        }
        core = (DarkCoreNPC) new DarkCoreNPC(new Location(getX() + 3, getY(), getPlane()), this).spawn();
    }

    private void refreshDamageDealt(final Hit hit) {
        if (hit.getDamage() <= 0) return;
        if (!(hit.getSource() instanceof Player)) return;
        val player = (Player) hit.getSource();
        val dmg = hit.getDamage();
        refreshDamageDealt(player, dmg > getHitpoints() ? getHitpoints() : dmg);
    }

    private void halveDamage(final Hit hit) {
        if (halve(hit)) {
            hit.setDamage(hit.getDamage() >> 1);
        }
    }

    private boolean halve(final Hit hit) {
        if (hit.getHitType() != HitType.MELEE) return true;
        val weapon = hit.getWeapon();
        if (!(weapon instanceof Item)) return true;
        val definitions = ((Item) weapon).getDefinitions();
        if (definitions == null) return true;
        val name = definitions.getName();
        return (!name.contains("spear") && !name.contains("halberd")) || hit.containsAttribute("using special");
    }

    @Override
    public float getXpModifier(Hit hit) {
        return halve(hit) ? 0.5F : 1F;
    }

    private void retaliate(final Entity source) {
        if (source == null) return;
        val combat = getCombat();
        val target = combat.getTarget();
        if (target == source) return;
        combat.setTarget(source);
    }

    @Override
    public void onFinish(final Entity source) {
        super.onFinish(source);
        if (core != null) {
            core.finish();
            core = null;
        }
        for (final Player p : cavern.getPlayers()) {
            p.getVarManager().sendBit(999, 0);
        }
    }

    private final Set<Entity> underneathPlayers = new ObjectOpenHashSet<>();

    @Override
    public void processNPC() {
        if (!isDead()) {
            if (this.combat.getCombatDelay() <= 1) {
                val cave = cavern == null ?
                           GlobalAreaManager.get("Corporeal Beast cavern") :
                           cavern;
                if (cave.getPlayers().size() > 0) {
                    underneathPlayers.clear();
                    CharacterLoop.populateEntityList(underneathPlayers, getLocation(), getSize(), Player.class,
                            player -> !player.isDead() && !player.isLocked() && Utils.collides(getX(), getY(), getSize(), player.getX(), player.getY(), player.getSize()));
                    if (!underneathPlayers.isEmpty()) {
                        setAnimation(stomp);
                        for (val player : underneathPlayers) {
                            if (player instanceof Player) {
                                ((Player) player).sendMessage("You get trampled under the Beast's massive legs.");
                                player.applyHit(new Hit(this, Utils.random(40, 51), HitType.DEFAULT));
                            }
                        }
                        combat.setCombatDelay(6);
                    }
                }
            }
        }
        super.processNPC();
    }

    private static final Animation HIGH_DAMAGE_ATTACK_ANIM = new Animation(1679);
    private static final Animation STAT_DRAIN_ATTACK_ANIM = new Animation(1680);
    private static final Animation AOE_ATTACK_ANIM = new Animation(1681);
    private static final Animation LEFT_CLAW_MELEE_ANIM = new Animation(1682);
    private static final Animation RIGHT_CLAW_MELEE_ANIM = new Animation(1683);

    private static final Projectile HIGH_DAMAGE_PROJ = new Projectile(316, 50, 25, 25, 15, 18, 64, 5);
    private static final Projectile STAT_DRAIN_PROJ = new Projectile(314, 50, 25, 25, 15, 18, 64, 5);
    private static final Projectile AOE_PROJ = new Projectile(315, 50, 5, 25, 15, 18, 64, 5);

    private static final Projectile SPLIT_AOE_PROJ = new Projectile(315, 5, 5, 0, 15, 28, 64, 5);

    private static final Graphics SPLITTING_GFX = new Graphics(317);

    @Override
    public int attack(final Entity target) {
        val npc = this;
        final boolean meleeDistance = isWithinMeleeDistance(npc, target);
        if (meleeDistance && Utils.random(1) == 0) {
            npc.setAnimation(Utils.random(1) == 0 ? LEFT_CLAW_MELEE_ANIM : RIGHT_CLAW_MELEE_ANIM);
            delayHit(npc, 0, target, new Hit(npc, getRandomMaxHit(npc, 51, MELEE, target), HitType.MELEE));
            return 6;
        }
        final Location center = npc.getMiddleLocation();
        final int type = Utils.random(2);
        if (type == 0) {
            npc.setAnimation(HIGH_DAMAGE_ATTACK_ANIM);
            World.sendProjectile(npc, target, HIGH_DAMAGE_PROJ);
            final int delay = HIGH_DAMAGE_PROJ.getTime(center, target.getLocation());
            delayHit(npc, delay, target, new Hit(npc, getRandomMaxHit(npc, 65, MAGIC, target), HitType.MAGIC));
        } else if (type == 1) {
            npc.setAnimation(STAT_DRAIN_ATTACK_ANIM);
            World.sendProjectile(npc, target, STAT_DRAIN_PROJ);
            final int delay = STAT_DRAIN_PROJ.getTime(center, target.getLocation());
            final int damage = getRandomMaxHit(npc, 50, MAGIC, target);
            delayHit(npc, delay, target, new Hit(npc, damage, HitType.MAGIC).onLand(hit -> {
                if (target.getEntityType() == EntityType.PLAYER && Utils.random(3) == 0) {
                    final Player player = (Player) target;
                    final int skill = Utils.random(1) == 0 ? Skills.PRAYER : Skills.MAGIC;
                    if (skill == Skills.PRAYER) {
                        player.getPrayerManager().drainPrayerPoints(Utils.random(1, 5));
                    } else {
                        player.getSkills().drainSkill(Skills.MAGIC, Utils.random(1, 5));
                    }
                    player.sendMessage("Your " + Skills.getSkillName(skill) + " has been slighly drained!");
                }
            }));
        } else {
            npc.setAnimation(AOE_ATTACK_ANIM);
            final Location tile = new Location(target.getLocation());
            World.sendProjectile(npc, tile, AOE_PROJ);
            final int delay = AOE_PROJ.getTime(center, tile);
            WorldTasksManager.schedule(new WorldTask() {
                private final List<Location> splitTiles = new ArrayList<Location>(5);
                private List<Entity> targets;
                private boolean split;

                @Override
                public void run() {
                    if (!split) {
                        World.sendGraphics(SPLITTING_GFX, tile);
                        targets = npc.getPossibleTargets(EntityType.PLAYER);
                        for (final Entity e : targets) {
                            if (!e.getLocation().withinDistance(tile, 1)) {
                                continue;
                            }
                            delayHit(npc, -1, target, new Hit(npc, getRandomMaxHit(npc, 30, MAGIC, target), HitType.MAGIC));
                        }
                        for (int i = 0; i < 5; i++) {
                            final int hash = tile.getPositionHash();
                            Location t = null;
                            int count = 50;
                            loop: while (--count > 0) {
                                t = new Location(tile.getX() + Utils.random(-3, 3), tile.getY() + Utils.random(-3, 3), tile.getPlane());
                                final int posHash = t.getPositionHash();
                                if (posHash == hash || !World.isFloorFree(t, 1)) {
                                    continue;
                                }
                                for (final Location x : splitTiles) {
                                    if (posHash == x.getPositionHash()) {
                                        continue loop;
                                    }
                                }
                                break;
                            }
                            splitTiles.add(t);
                            World.sendProjectile(tile, t, SPLIT_AOE_PROJ);
                        }
                        split = true;
                        return;
                    }
                    for (final Location tile : splitTiles) {
                        World.sendGraphics(SPLITTING_GFX, tile);
                        for (final Entity e : targets) {
                            if (!e.getLocation().withinDistance(tile, 1)) {
                                continue;
                            }
                            delayHit(npc, -1, target, new Hit(npc, getRandomMaxHit(npc, 30, MAGIC, target), HitType.MAGIC));
                        }
                    }
                    stop();

                }
            }, delay, 0);
        }
        return 6;
    }

    private static final class CorporealBeastHitBar extends EntityHitBar {

        CorporealBeastHitBar(final Entity entity) {
            super(entity);
        }

        @Override
        public int getType() {
            return 3;
        }

        @Override
        public int getMultiplier() {
            return 160;
        }

    }

}
