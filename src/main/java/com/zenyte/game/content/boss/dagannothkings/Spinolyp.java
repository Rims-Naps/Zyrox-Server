package com.zenyte.game.content.boss.dagannothkings;

import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.Toxins;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.magic.CombatSpell;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.RSPolygon;
import lombok.val;

/**
 * @author Tommeh | 25 mrt. 2018 : 17:35:28
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class Spinolyp extends NPC implements Spawnable, CombatScript {

    static final int[][] polyBounds = new int[][]{
            { 2912, 4464 },
            { 2910, 4463 },
            { 2908, 4463 },
            { 2905, 4462 },
            { 2903, 4462 },
            { 2903, 4460 },
            { 2901, 4457 },
            { 2901, 4455 },
            { 2899, 4453 },
            { 2899, 4445 },
            { 2900, 4443 },
            { 2900, 4442 },
            { 2902, 4438 },
            { 2904, 4437 },
            { 2905, 4437 },
            { 2907, 4436 },
            { 2909, 4436 },
            { 2910, 4435 },
            { 2917, 4435 },
            { 2920, 4436 },
            { 2922, 4436 },
            { 2926, 4438 },
            { 2928, 4442 },
            { 2929, 4446 },
            { 2929, 4451 },
            { 2927, 4454 },
            { 2927, 4457 },
            { 2922, 4462 },
            { 2916, 4463 }
    };

    private static final RSPolygon poly = new RSPolygon(polyBounds);

    private static final RSPolygon poly2;

    static {
        val bounds = new int[polyBounds.length][2];
        for (int i = 0; i < polyBounds.length; i++) {
            val current = polyBounds[i];
            val x = current[0];
            val y = current[1];
            bounds[i] = new int[] {x, y - 64};
        }
        poly2 = new RSPolygon(bounds);
    }



    public Spinolyp(final int id, final Location tile, final Direction facing, final int radius) {
        super(id, tile, facing, radius);
        setForceAggressive(true);
        this.attackDistance = 15;
        this.aggressionDistance = 15;
        this.maxDistance = 30;
    }

    protected final boolean canMove(final int fromX, final int fromY, final int direction) {
        val x = Utils.DIRECTION_DELTA_X[direction] + fromX;
        val y = Utils.DIRECTION_DELTA_Y[direction] + fromY;
        return (World.getMask(getPlane(), x, y) & 0xFFFF) == 0 && !poly.contains(x, y) && !poly2.contains(x, y);
    }

    @Override
    public boolean isTolerable() {
        return false;
    }

    @Override
    public boolean checkProjectileClip(final Player player) {
        return false;
    }

    @Override
    public boolean validate(final int id, final String name) {
        return id == 5947 || id == 5961 || id == 5963;
    }

    private static final Projectile ATTACK_PROJ = new Projectile(294, 20, 25, 25, 30, 28, 5, 5);

    @Override
    public int attack(final Entity target) {
        val ranged = Utils.random(1) == 0;
        setAnimation(combatDefinitions.getAttackAnim());
        if (ranged) {
            World.sendProjectile(this, target, ATTACK_PROJ);
            delayHit(this, World.sendProjectile(this, target, ATTACK_PROJ), target, new Hit(this, getRandomMaxHit(this, combatDefinitions.getMaxHit(), RANGED, target),
                    HitType.RANGED).onLand(hit -> {
                        if (hit.getDamage() > 0) {
                            target.getToxins().applyToxin(Toxins.ToxinType.POISON, 6);
                        }
            }));
        } else {
            delayHit(this, World.sendProjectile(this, target, CombatSpell.WATER_STRIKE.getProjectile()), target, new Hit(this, getRandomMaxHit(this, combatDefinitions.getMaxHit(),
                    RANGED, target), HitType.MAGIC).onLand(hit -> {
                if (hit.getDamage() > 0 && target instanceof Player) {
                    val player = ((Player) target);
                    if (player.getEquipment().getId(EquipmentSlot.SHIELD) == 12821) {
                        if (Utils.random(1) == 0) {
                            player.getPrayerManager().drainPrayerPoints(1);
                        }
                    } else {
                        player.getPrayerManager().drainPrayerPoints(1);
                    }
                }
            }));
        }
        return combatDefinitions.getAttackSpeed();
    }

}
