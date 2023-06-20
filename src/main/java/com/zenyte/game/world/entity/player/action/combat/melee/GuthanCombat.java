package com.zenyte.game.world.entity.player.action.combat.melee;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.entity.player.action.combat.MeleeCombat;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import lombok.val;

import static com.zenyte.game.world.entity.player.action.combat.CombatUtilities.GUTHANS_SET_GFX;

/**
 * @author Kris | 2. juuni 2018 : 22:46:15
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class GuthanCombat extends MeleeCombat {

	public GuthanCombat(final Entity target) {
		super(target);
	}

    @Override
    protected void extra(final Hit hit) {
        if (Utils.random(3) != 0 || !CombatUtilities.hasFullBarrowsSet(player, "Guthan's")) return;
        target.setGraphics(GUTHANS_SET_GFX);
        val amuletId = player.getEquipment().getId(EquipmentSlot.AMULET);
        val amount = hit.getDamage();
        if (amuletId == 12851 || amuletId == 12853) {
            val hitpoints = player.getHitpoints();
            if (hitpoints < (player.getMaxHitpoints() + 10) + amount) {
                player.setHitpoints(Math.min((hitpoints + amount), (player.getMaxHitpoints() + 10)));
            }
        } else {
            player.heal(amount);
        }
    }

}
