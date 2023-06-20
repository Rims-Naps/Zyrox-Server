package com.zenyte.game.world.entity.npc.impl.wilderness;

import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.Toxins.ToxinType;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;

/**
 * @author Tommeh | 12 mrt. 2018 : 22:51:04
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ScorpiaOffspring extends NPC implements CombatScript, Spawnable {

	public ScorpiaOffspring(int id, Location tile, Direction facing, int radius) {
		super(id, tile, facing, radius);
		this.forceAggressive = true;
	}

	@Override
	public int attack(final Entity target) {
		setAnimation(getCombatDefinitions().getAttackAnim());
		boolean isHit = CombatUtilities.isHit(this, RANGED, RANGED, target);

		if(!isHit && target instanceof Player) {
			((Player) target).getPrayerManager().drainPrayerPoints(1);
		}

		delayHit(this, 0, target, new Hit(this, getRandomMaxHit(this, 2, target, isHit), HitType.RANGED).onLand(hit -> {
			if (Utils.random(3) == 0 && !target.getToxins().isPoisoned()) {
				target.getToxins().applyToxin(ToxinType.POISON, 6);
			}
		}));
		return getCombatDefinitions().getAttackSpeed();
	}

	@Override
	public boolean validate(int id, String name) {
		return name.equals("scorpia's offspring");
	}

	@Override
	public int getAggressionDistance() {
		return 2;
	}

	@Override
	public int getAttackDistance() {
		return 1;
	}


}