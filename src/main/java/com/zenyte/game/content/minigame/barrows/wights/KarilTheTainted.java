package com.zenyte.game.content.minigame.barrows.wights;

import com.zenyte.game.content.minigame.barrows.BarrowsWightNPC;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import lombok.val;

/**
 * @author Kris | 29. sept 2018 : 06:11:54
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public class KarilTheTainted extends BarrowsWightNPC implements Spawnable, CombatScript {

	public static boolean allHitsOnKarilWereSpecial;

	private static final Graphics GFX = new Graphics(401, 0, 96);
	private static final Projectile PROJ = new Projectile(27, 42, 30, 40, 15, 3, 64, 5);

	public KarilTheTainted(final int id, final Location tile, final Direction facing, final int radius) {
		super(id, tile, facing, radius);
		allHitsOnKarilWereSpecial = true;
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		val source = hit.getSource();
		if (source != null) {
			if (source instanceof Player) {
				val player = (Player) source;
				if (!player.getBooleanAttribute("used_special") && !player.getBooleanAttribute("hard-combat-achievement42")) {
					allHitsOnKarilWereSpecial = false;
				}
			}
		}
		super.handleIngoingHit(hit);
	}

	@Override
	public int attack(final Entity target) {
		animate();
        this.delayHit(World.sendProjectile(this, target, PROJ), target, ranged(target, combatDefinitions.getMaxHit()).onLand(hit -> {
            if (hit.getDamage() > 0) {
                if (Utils.random(3) == 0) {
                    target.setGraphics(GFX);
                    target.drainSkill(Skills.AGILITY, 20F);
                }
            }
        }));
		return combatDefinitions.getAttackSpeed();
	}

	@Override
	public boolean validate(final int id, final String name) {
		return id == NpcId.KARIL_THE_TAINTED;
	}

}
