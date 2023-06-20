package com.zenyte.game.world.entity.player.action.combat.melee;

import com.zenyte.Game;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.player.action.combat.MeleeCombat;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import lombok.val;
import lombok.var;

/**
 * @author Kris | 2. juuni 2018 : 22:50:30
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class GraniteMaulCombat extends MeleeCombat {

	public GraniteMaulCombat(final Entity target) {
		super(target);
	}

    @Override
    public boolean process() {
        if (!initiateCombat(player)) {
            return false;
        }
        val attribute = player.getTemporaryAttributes().get("cached granite maul specials");
        val cachedMaulSpecials = attribute instanceof LongArrayList ? (LongArrayList) attribute : null;
        if (cachedMaulSpecials == null || cachedMaulSpecials.isEmpty()) {
            return true;
        }
        val latestTick = cachedMaulSpecials.getLong(cachedMaulSpecials.size() - 1);
        if (latestTick <= Game.getCurrentCycle() - 2) {
            cachedMaulSpecials.clear();
            return true;
        }
        var countOfCachedSpecials = 0;
        for (val special : cachedMaulSpecials) {
            if (special == latestTick) {
                countOfCachedSpecials++;
            }
        }
        cachedMaulSpecials.clear();
        val numberOfSpecials = Math.min(countOfCachedSpecials, 2);
        val combatDefinitions = player.getCombatDefinitions();
        var numberOfSpecialsUsed = 0;
        for (int i = 0; i < numberOfSpecials; i++) {
            combatDefinitions.setUsingSpecial(true);
            if (combatDefinitions.getSpecialEnergy() < 50) {
                continue;
            }
            processWithDelay();
            numberOfSpecialsUsed++;
        }
        combatDefinitions.setUsingSpecial(false);
        combatDefinitions.refresh();
        if (numberOfSpecialsUsed > 0) {
            val actionManager = player.getActionManager();
            //Avoid it running the processWithDelay method on-top of the special attack executions.
            if (actionManager.getActionDelay() == 0) {
                actionManager.setActionDelay(1);
            }
        }
        return true;
    }

}
