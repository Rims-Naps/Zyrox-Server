package com.zenyte.game.content.godwars.npcs;

import com.zenyte.game.content.boss.BossRespawnTimer;
import com.zenyte.game.content.combatachievements.combattasktiers.GrandmasterTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.godwars.instance.ArmadylInstance;
import com.zenyte.game.content.godwars.instance.SaradominInstance;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.MovementLock;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import com.zenyte.game.world.entity.player.action.combat.ranged.SalamanderCombat;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 26 mrt. 2018 : 16:55:49
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class KreeArra extends GodwarsBossNPC implements Spawnable, CombatScript {

    private boolean wasRicochet;
    private boolean wasSalamander;

    public KreeArra(final int id, final Location tile, final Direction direction, final int radius) {
        super(id, tile, direction, radius);
        if (isAbstractNPC() || tile.getX() >= 6400) return;
        setMinions(new GodwarsBossMinion[]{
                new WingmanSkree(NpcId.WINGMAN_SKREE, new Location(2834, 5297, 2), Direction.SOUTH, 5),
                new FlockleaderGeerin(NpcId.FLOCKLEADER_GEERIN, new Location(2827, 5299, 2), Direction.SOUTH, 5),
                new GodwarsBossMinion(NpcId.FLIGHT_KILISA, new Location(2829, 5300, 2), Direction.SOUTH, 5),
        });
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        if (source instanceof Player) {
            val player = (Player) source;
            int deathCount = 0;
            val minions = super.minions;
            for (int i = 0; i < 3; i++) {
                val minion = minions[i];
                if (minion.isDead() || minion.isFinished() || minion.getHitpoints() == 0) {
                    deathCount++;
                }
            }
            if (deathCount >= 3 && !player.getBooleanAttribute("hard-combat-achievement25")) {
                player.putBooleanAttribute("hard-combat-achievement25", true);
                HardTasks.sendHardCompletion(player, 25);
            }
            if (wasRicochet && !player.getBooleanAttribute("master-combat-achievement22")) {
                player.putBooleanAttribute("master-combat-achievement22", true);
                MasterTasks.sendMasterCompletion(player, 22);
            }
            if (!player.getBooleanAttribute("has_taken_melee_damage_in_kree") && !player.getBooleanAttribute("master-combat-achievement34")) {
                player.putBooleanAttribute("master-combat-achievement34", true);
                MasterTasks.sendMasterCompletion(player, 34);
            }
            if (wasSalamander && !player.getBooleanAttribute("grandmaster-combat-achievement19")) {
                player.putBooleanAttribute("grandmaster-combat-achievement19", true);
                GrandmasterTasks.sendGrandmasterCompletion(player, 19);
            }
            if (player.getAttributes().containsKey("armadyl_kc_on_instance_creation")) {
                if (player.getNumericAttribute("armadyl_kc_on_instance_creation").intValue() + 29 <= player.getNotificationSettings().getKillcount("kree'arra")
                        && !player.getBooleanAttribute("grandmaster-combat-achievement35")
                        && player.getArea() instanceof ArmadylInstance) {
                    player.putBooleanAttribute("grandmaster-combat-achievement35", true);
                    GrandmasterTasks.sendGrandmasterCompletion(player, 35);
                }
            }
        }
    }

    @Override
    BossRespawnTimer timer() {
        return BossRespawnTimer.KREE_ARRA;
    }

    public KreeArra(final GodwarsBossMinion[] minions, final int id, final Location tile, final Direction direction, final int radius) {
        this(id, tile, direction, radius);
        setMinions(minions);
    }

    long clickDelay;

    @Override
    public void processNPC() {
        super.processNPC();
        if (isForceFollowClose()) {
            if (clickDelay > Utils.currentTimeMillis()) {
                setForceFollowClose(false);
            }
        } else {
            if (clickDelay < Utils.currentTimeMillis()) {
                setForceFollowClose(true);
            }
        }
    }

    @Override
    ForceTalk[] getQuotes() {
        return null;
    }

    @Override
    int diaryFlag() {
        return 0x4;
    }

    @Override
    public GodType type() {
        return GodType.ARMADYL;
    }

    @Override
    public boolean validate(final int id, final String name) {
        return id == NpcId.KREEARRA;
    }

    private static final Animation meleeAnimation = new Animation(6981);
    private static final Animation distancedAnimation = new Animation(6980);
    private static final Projectile magicProjectile = new Projectile(1200, 41, 16, 40, 5, 10, 0, 5);
    private static final Projectile rangedProjectile = new Projectile(1199, 41, 16, 40, 5, 10, 0, 5);
    private static final SoundEffect meleeSound = new SoundEffect(3892, 10, 0);
    private static final SoundEffect tornadoSound = new SoundEffect(3870, 10, 0);
    private static final SoundEffect tornadoHitSound = new SoundEffect(2727, 10, -1);
    private static final SoundEffect tornadoSplashSound = new SoundEffect(227, 10, -1);

    @Override
    public int attack(final Entity target) {
        val npc = this;
        if (npc.isForceFollowClose() && Utils.random(1) == 0) {
            final int distanceX = target.getX() - npc.getX();
            final int distanceY = target.getY() - npc.getY();
            final int size = npc.getSize();
            if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1) {
                return 0;
            }
            npc.setAnimation(meleeAnimation);
            World.sendSoundEffect(getMiddleLocation(), meleeSound);
            delayHit(npc, 0, target, new Hit(npc, getRandomMaxHit(npc, 26, MELEE, MAGIC, target), HitType.MELEE));
            return npc.getCombatDefinitions().getAttackSpeed();
        }
        npc.setAnimation(distancedAnimation);
        World.sendSoundEffect(getMiddleLocation(), tornadoSound);
        for (final Entity t : npc.getPossibleTargets(EntityType.PLAYER)) {
            final int style = Utils.random(1);
            if (style == 0) {
                int damage = getRandomMaxHit(npc, 21, MAGIC, RANGED, t);
                //kree'arra deals a minimum of 10 damage upon successful hit; for even distribution, we re-calc it.
                if (damage > 0) {
                    damage = Utils.random(10, 21);
                }
                val hit = new Hit(npc, damage, HitType.MAGIC);
                delayHit(npc, World.sendProjectile(npc, t, magicProjectile), t, hit);
                World.sendSoundEffect(new Location(target.getLocation()),
                        (hit.getDamage() == 0 ? tornadoSplashSound : tornadoHitSound).withDelay(magicProjectile.getProjectileDuration(getMiddleLocation(), target)));
                if (t instanceof Player && Utils.random(2) == 0) {
                    push((Player) t);
                }
            } else {
                val hit = new Hit(npc, getRandomMaxHit(npc, 71, RANGED, t), HitType.RANGED);
                delayHit(npc, World.sendProjectile(npc, t, rangedProjectile), t, hit);
                World.sendSoundEffect(new Location(target.getLocation()),
                        (hit.getDamage() == 0 ? tornadoSplashSound : tornadoHitSound).withDelay(rangedProjectile.getProjectileDuration(getMiddleLocation(), target)));
                if (t instanceof Player && Utils.random(2) == 0) {
                    push((Player) t);
                }
            }
        }
        return npc.getCombatDefinitions().getAttackSpeed();
    }

    private static final Animation knockbackAnimation = new Animation(848);
    private static final Graphics stunGraphics = new Graphics(348, 0, 92);

    private final void push(@NotNull final Player player) {
        val tile = player.getFaceLocation(this, 2, 1024);
        val destination = new Location(player.getLocation());
        val dir = Utils.getMoveDirection(tile.getX() - destination.getX(), tile.getY() - destination.getY());
        if (dir != -1) {
            if (World.checkWalkStep(destination.getPlane(), destination.getX(), destination.getY(), dir, player.getSize(), false, false)) {
                destination.setLocation(tile);
            }
        }
        player.faceEntity(this);
        if (!destination.matches(player)) {
            player.setLocation(destination);
        }
        //50% chance to stun regardless if teleported or not.
        if (Utils.random(1) == 0) {
            if (player.getActionManager().getAction() instanceof PlayerCombat && player.getActionManager().getActionDelay() == 0) {
                player.getActionManager().addActionDelay(1);
            }
            player.addMovementLock(new MovementLock(System.currentTimeMillis() + TimeUnit.TICKS.toMillis(1), "You're stunned."));
            player.setAnimation(knockbackAnimation);
            player.setGraphics(stunGraphics);
        }
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        if (hitpoints == this.getMaxHitpoints()) {
            wasRicochet = true;
            wasSalamander = true;
            val players = this.getPossibleTargets(EntityType.PLAYER);
            for (val player : players) {
                ((Player) player).putBooleanAttribute("has_taken_melee_damage_in_kree", false);
            }
        }
        if (hit.getAttributes().getOrDefault("notMainFocusedTarget", false).equals(false)) {
            wasRicochet = false;
        }
        val source = hit.getSource();
        if (source instanceof Player) {
            val player = (Player) source;
            val weapon = player.getWeapon();
            if (!(weapon.getName().toLowerCase().contains("salamander") || weapon.getName().toLowerCase().contains("swamp lizard"))) {
                wasSalamander = false;
            }
        }
        super.handleIngoingHit(hit);
    }
}
