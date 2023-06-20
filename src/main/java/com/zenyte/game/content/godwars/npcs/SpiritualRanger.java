package com.zenyte.game.content.godwars.npcs;

import com.zenyte.game.util.Direction;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import lombok.val;

/**
 * @author Kris | 21/08/2019 00:35
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SpiritualRanger extends KillcountNPC implements Spawnable, CombatScript {

    protected SpiritualRanger(final int id, final Location tile, final Direction facing, final int radius) {
        super(id, tile, facing, radius);
    }

    @Override
    public boolean validate(final int id, final String name) {
        return id == 2211 || id == 2242 || id == 3160;
    }

    private static final Graphics graphics = new Graphics(19, 0, 90);
    private static final Projectile projectile = new Projectile(10, 42, 30, 40, 15, 10, 64, 5);
    private static final Projectile thrownaxeProjectile = new Projectile(1197, 16, 22, 30, 15, 10, 64, 5);
    private static final SoundEffect attackSound = new SoundEffect(2693, 10, 0);

    @Override
    public int attack(final Entity target) {
        val npc = this;
        npc.setAnimation(npc.getCombatDefinitions().getAttackAnim());
        World.sendSoundEffect(getMiddleLocation(), attackSound);
        if (npc.getId() == 2242) {
            delayHit(npc, World.sendProjectile(npc, target, thrownaxeProjectile), target, new Hit(npc, getRandomMaxHit(npc, npc.getCombatDefinitions().getMaxHit(), RANGED, target), HitType.RANGED));
        } else {
            npc.setGraphics(graphics);
            delayHit(npc, World.sendProjectile(npc, target, projectile), target, new Hit(npc, getRandomMaxHit(npc, npc.getCombatDefinitions().getMaxHit(), RANGED, target), HitType.RANGED));
        }
        return npc.getCombatDefinitions().getAttackSpeed();
    }
}
