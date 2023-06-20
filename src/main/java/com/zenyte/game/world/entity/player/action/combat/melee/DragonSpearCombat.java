package com.zenyte.game.world.entity.player.action.combat.melee;

import com.zenyte.cores.WorldThread;
import com.zenyte.game.item.degradableitems.DegradeType;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.CombatType;
import com.zenyte.game.world.entity.player.action.combat.MeleeCombat;
import com.zenyte.game.world.entity.player.action.combat.SpecialAttack;
import com.zenyte.game.world.entity.player.action.combat.SpecialAttackScript;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.area.plugins.PlayerCombatPlugin;
import lombok.val;

/**
 * @author Kris | 17/06/2019 16:27
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class DragonSpearCombat extends MeleeCombat {
    public DragonSpearCombat(final Entity target) {
        super(target);
    }

    @Override
    public int processWithDelay() {
        if (!target.startAttacking(player, CombatType.MELEE)) {
            return -1;
        }
        if (!isWithinAttackDistance()) {
            return 0;
        }
        if (!canAttack()) {
            return -1;
        }
        val area = player.getArea();
        if (area instanceof PlayerCombatPlugin) {
            ((PlayerCombatPlugin) area).onAttack(player, target, "Melee");
        }

        if (player.getCombatDefinitions().isUsingSpecial()) {
            val spec = SpecialAttack.SPECIAL_ATTACKS.get(player.getEquipment().getId(EquipmentSlot.WEAPON.getSlot()));
            if (spec == SpecialAttack.SHOVE && target instanceof Player) {
                val lastShoveTick = ((Player) target).getNumericTemporaryAttribute("Last shove push").longValue();
                if (WorldThread.WORLD_CYCLE < lastShoveTick) {
                    return 0;
                }
            }
        }

        addAttackedByDelay(player, target);

        val delay = special();
        if (delay != -2) {
            return delay == SpecialAttackScript.WEAPON_SPEED ? getSpeed() : delay;
        }
        sendSoundEffect();
        val hit = getHit(player, target, 1, 1, 1, false);
        extra(hit);
        addPoisonTask(hit.getDamage(), 0);
        delayHit(0, hit);
        animate();
        player.getChargesManager().removeCharges(DegradeType.OUTGOING_HIT);
        resetFlag();
        addExtraEffect(hit);
        checkIfShouldTerminate();
        return getSpeed();
    }

}
