package com.zenyte.game.content.boss.bryophyta;

import com.zenyte.game.content.combatachievements.combattasktiers.EasyTasks;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.Toxins;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.combatdefs.ToxinDefinitions;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import lombok.val;

/**
 * @author Tommeh | 17/05/2019 | 14:56
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class Bryophyta extends NPC implements CombatScript {

    private boolean lastHitWasVenomOrPoison;

    private static final Animation AUTO_ATTACK_ANIM = new Animation(4658);
    private static final Animation MAGIC_ATTACK_ANIM = new Animation(7173);
    private static final Projectile MAGIC_ATTACK_PROJ = new Projectile(139, 50, 33, 46, 23, -5, 64, 10);
    private static final Graphics MAGIC_ATTACK_ONHIT_GFX = new Graphics(140, 0, 124);
    private static final Graphics SPLASH_GFX = new Graphics(85, 0, 124);
    private static final Graphics GROWTHLING_SPAWN_GFX = new Graphics(86, 0, 100);
    private static final Location[] GROWTHLING_SPAWNS = {
            new Location(3215, 9936, 0),
            new Location(3224, 9937, 0),
            new Location(3221, 9928, 0)
    };

    private Growthling[] growthlings;
    private final BryophytaInstance instance;

    public Bryophyta(final Location tile, final BryophytaInstance instance) {
        super(NpcId.BRYOPHYTA, tile, Direction.SOUTH, 3);
        this.instance = instance;
        setSpawned(true);
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        if (!growthlingsDead()) {
            hit.setDamage(0);
        }
        lastHitWasVenomOrPoison = hit.getHitType() == HitType.POISON || hit.getHitType() == HitType.VENOM;
        super.handleIngoingHit(hit);
    }

    @Override
    public float getXpModifier(final Hit hit) {
        return !growthlingsDead() ? 0 : 1;
    }

    @Override
    public int attack(Entity target) {
        if (isWithinMeleeDistance(this, target)) {
            if (Utils.random(1) == 0) {
                setAnimation(AUTO_ATTACK_ANIM);
                delayHit(this, 1, target, new Hit(this, getRandomMaxHit(this, 16, MELEE, target), HitType.MELEE));
            } else {
               return distanceAttack(target);
            }
        } else {
            return distanceAttack(target);
        }
        return getCombatDefinitions().getAttackSpeed();
    }

    private int distanceAttack(final Entity target) {
        if (Utils.random(7) == 0 && growthlingsDead()) {
            spawnGrowthlings(target);
        } else {
            setAnimation(MAGIC_ATTACK_ANIM);
            World.sendProjectile(this, target, MAGIC_ATTACK_PROJ);
            delayHit(MAGIC_ATTACK_PROJ.getTime(this, target), target, new Hit(this, getRandomMaxHit(this, 16, MAGIC, target), HitType.MAGIC).onLand(hit -> {
                if (hit.getDamage() == 0) {
                    target.setGraphics(SPLASH_GFX);
                } else {
                    target.getToxins().applyToxin(Toxins.ToxinType.POISON, 8);
                    target.setGraphics(MAGIC_ATTACK_ONHIT_GFX);
                }
            }));
        }
        return getCombatDefinitions().getAttackSpeed();
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        growthlings = null;
        if (source != null) {
            if (source instanceof Player) {
                val player = (Player) source;
                if (player.getPrayerManager().isActive(Prayer.PROTECT_FROM_MAGIC) && !player.getBooleanAttribute("easy-combat-achievement15")) {
                    player.putBooleanAttribute("easy-combat-achievement15", true);
                    EasyTasks.sendEasyCompletion(player, 15);
                }
                if (!player.getToxins().isPoisoned() && !player.getBooleanAttribute("easy-combat-achievement19")) {
                    player.putBooleanAttribute("easy-combat-achievement19", true);
                    EasyTasks.sendEasyCompletion(player, 19);
                }
                if (player.getEquipment().getItem(EquipmentSlot.WEAPON) != null) {
                    if (player.getEquipment().getItem(EquipmentSlot.WEAPON).getName().contains("Rune scimitar") && !player.getBooleanAttribute("easy-combat-achievement22")) {
                        player.putBooleanAttribute("easy-combat-achievement22", true);
                        EasyTasks.sendEasyCompletion(player, 22);
                    }
                }
                if (lastHitWasVenomOrPoison && !player.getBooleanAttribute("easy-combat-achievement23")) {
                    player.putBooleanAttribute("easy-combat-achievement23", true);
                    EasyTasks.sendEasyCompletion(player, 23);
                }

            }
        }
    }

    private void spawnGrowthlings(final Entity target) {
        growthlings = new Growthling[3];
        for (int index = 0; index < 3 ; index++) {
            val location = instance.getLocation(GROWTHLING_SPAWNS[index]);
            growthlings[index] = (Growthling) new Growthling(8194, location).spawn();
            growthlings[index].setGraphics(GROWTHLING_SPAWN_GFX);
            growthlings[index].getCombat().setTarget(target);
        }
    }

    private boolean growthlingsDead() {
        if (growthlings == null) {
            return true;
        }
        for (val growthling : growthlings) {
            if (growthling == null || !growthling.isFinished()) {
                return false;
            }
        }
        return true;
    }
}
