package com.zenyte.game.content.minigame.barrows.wights;

import com.zenyte.game.content.minigame.barrows.BarrowsWightNPC;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 29. sept 2018 : 05:39:42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public class ToragTheCorrupted extends BarrowsWightNPC implements Spawnable, CombatScript {

	private static final Graphics TORAGS_GFX = new Graphics(399);
	
	public ToragTheCorrupted(final int id, final Location tile, final Direction facing, final int radius) {
		super(id, tile, facing, radius);
	}

	@Override
	public int attack(final Entity target) {
		target.getTemporaryAttributes().put("has_been_hit_by_torag", true);
        this.executeMeleeHit(target, combatDefinitions.getMaxHit()).onLand(hit -> {
            if (hit.getDamage() > 0 && Utils.random(3) == 0) {
                target.setGraphics(TORAGS_GFX);
                if (target instanceof Player) {
                    val variables = ((Player) target).getVariables();
                    double energy = variables.getRunEnergy();
                    energy -= (energy * 0.2F);
                    variables.setRunEnergy(energy);
                }
            }
        });
		animate();
		return combatDefinitions.getAttackSpeed();
	}

	@Override
	public boolean validate(final int id, final String name) {
		return id == NpcId.TORAG_THE_CORRUPTED;
	}

}
