package com.zenyte.game.world.entity.player.action.combat.magic;

import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.game.item.degradableitems.DegradeType;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.player.action.combat.MagicCombat;
import lombok.val;

/**
 * @author Kris | 22/01/2019 18:57
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class StarterStaffCombat extends MagicCombat {

    public StarterStaffCombat(final Entity target, final CombatSpell spell, final CastType type) {
        super(target, spell, type);
    }

    protected void extra() {
        val weapon = player.getWeapon();
        if (weapon == null || weapon.getCharges() <= 0) {
            interrupt = true;
        }
    }

    @Override
    protected int attackSpeed() {
        return 3;
    }

    @Override
    protected boolean canAttack() {
        val weapon = player.getWeapon();
        val charges = weapon.getCharges();
        if (DegradableItem.getDefaultCharges(weapon.getId(), -1) != charges && charges <= 0) {
            player.sendMessage("Your staff is out of charges.");
            return false;
        }
        return super.canAttack();
    }

    @Override
    protected int getAttackDistance() {
        if (player.getCombatDefinitions().getStyle() == 3) {
            return 8;
        }
        return 6;
    }

    @Override
    protected void degrade() {
        player.getChargesManager().removeCharges(DegradeType.OUTGOING_HIT);
    }


}