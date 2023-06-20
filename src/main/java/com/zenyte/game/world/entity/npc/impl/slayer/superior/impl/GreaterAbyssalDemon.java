package com.zenyte.game.world.entity.npc.impl.slayer.superior.impl;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.impl.slayer.superior.SuperiorNPC;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 28/05/2019 02:20
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class GreaterAbyssalDemon extends SuperiorNPC implements CombatScript {
    public GreaterAbyssalDemon(@NotNull final Player owner, @NotNull final NPC root, final Location tile) {
        super(owner, root, 7410, tile);
    }

    private static final Graphics TELEPORT_GRAPHICS = new Graphics(409);
    private static final byte[][] BASIC_OFFSETS = new byte[][] { { 0, -1 }, { -1, 0 }, { 0, 1 }, { 1, 0 } };

    @Override
    public int attack(Entity target) {
        if (!(target instanceof Player)) {
            return 0;
        }
        val player = (Player) target;
        if (Utils.random(3) == 0) {
            for (int i = 0; i < 4; i++) {
                WorldTasksManager.schedule(() -> {
                    if (isDead() || target.isFinished() || target.isDead() || !target.getLocation().withinDistance(getLocation(), 15)) {
                        return;
                    }
                    val tile = nextTeleportTile(target);
                    if (tile != null) {
                        setLocation(tile);
                        setGraphics(TELEPORT_GRAPHICS);
                    }
                    WorldTasksManager.schedule(() -> {
                        if (isDead() || target.isFinished() || target.isDead() || !target.getLocation().withinDistance(getLocation(), 15)) {
                            return;
                        }
                        delayHit(0, target, new Hit(GreaterAbyssalDemon.this, Utils.random(31), HitType.MELEE));
                        setAnimation(combatDefinitions.getAttackAnim());
                    });
                }, i * 2);
            }
            return 11;
        }
        setAnimation(getCombatDefinitions().getAttackAnim());
        delayHit(this, 0, target, new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), MELEE, target), HitType.MELEE));
        if (Utils.random(4) == 0) {
            Location tile = null;
            for (int tryCount = 0; tryCount <= 20; tryCount++) {
                if (tryCount == 20) {
                    tile = null;
                    break;
                }
                final byte[] offsets = BASIC_OFFSETS[Utils.random(3)];
                tile = new Location(getX() + offsets[0], getY() + offsets[1], getPlane());
                if (!target.isProjectileClipped(tile, true) && World.isFloorFree(tile, size)) {
                    break;
                }
            }
            if (tile != null) {
                player.setLocation(tile);
                player.setGraphics(TELEPORT_GRAPHICS);
            }
        }
        return getCombatDefinitions().getAttackSpeed();
    }

    private Location nextTeleportTile(final Entity target) {
        Location tile = null;
        for (int tryCount = 0; tryCount <= 20; tryCount++) {
            if (tryCount == 20) {
                tile = null;
                break;
            }
            final byte[] offsets = BASIC_OFFSETS[Utils.random(3)];
            tile = new Location(target.getX() + offsets[0], target.getY() + offsets[1], getPlane());
            if (!this.isProjectileClipped(tile, true) && !tile.matches(getLocation()) && World.isFloorFree(tile, size)) {
                break;
            }
        }
        return tile;
    }
}
