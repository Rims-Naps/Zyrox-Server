package com.zenyte.game.world.entity.player.action.combat.ranged;

import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.action.combat.AmmunitionDefinitions;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.entity.player.action.combat.RangedCombat;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import mgi.types.config.items.ItemDefinitions;
import lombok.val;

/**
 * @author Kris | 01/11/2018 02:32
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class DragonHunterCrossbowCombat extends RangedCombat {

    public DragonHunterCrossbowCombat(final Entity target, final AmmunitionDefinitions defs) {
        super(target, defs);
    }

    //TODO Verify: DHC stacks _additively_ with void, and multiplicatively with slayer helmets.
    protected void setModifiers() {
        float modifier = 0;
        val amuletId = player.getEquipment().getId(EquipmentSlot.AMULET);
        if ((amuletId == 12017 || amuletId == 12018) && CombatUtilities.SALVE_AFFECTED_NPCS.contains(name)) {
            modifier = amuletId == 12017 ? 0.15F : 0.2F;
        } else {
            if (player.getSlayer().isCurrentAssignment(target)) {
                val helmId = player.getEquipment().getId(EquipmentSlot.HELMET);
                val definitions = ItemDefinitions.get(helmId);
                val name = definitions == null ? null : definitions.getName().toLowerCase();
                if (name != null && (name.contains("black mask") || name.contains("slayer helm")) && name.endsWith("(i)")) {
                    modifier = 0.15f;
                }
            }
        }

        if (isDragon()) {
            modifier += 3F;
        }


        if (CombatUtilities.hasFullRangedVoid(player, true)) {
            maxhitModifier += 0.12F;
        } else if (CombatUtilities.hasFullRangedVoid(player, false)) {
            maxhitModifier += 0.1F;
        }
        if (maxhitModifier > 1) {
            accuracyModifier += maxhitModifier - 1;
        }
        maxhitModifier += modifier;
        accuracyModifier += modifier;
    }

    private boolean isDragon() {
        if (!(target instanceof NPC))
            return false;
        val name = ((NPC) target).getDefinitions().getName().toLowerCase();
        return name.equals("baby green dragon") || name.equals("baby blue dragon") || name.equals("baby red dragon")
                || name.equals("baby black dragon") || name.equals("brutal green dragon")
                || name.equals("brutal red dragon") || name.equals("brutal blue dragon")
                || name.equals("brutal black dragon") || name.equals("vorkath") || name.equals("king black dragon")
                || name.equals("bronze dragon") || name.equals("iron dragon") || name.equals("steel dragon")
                || name.equals("mithril dragon") || name.equals("adamant dragon") || name.equals("rune dragon")
                || name.equals("skeletal wyvern") || name.equals("lava dragon") || name.equals("reanimated dragon")
                || name.equals("galvek") || name.equals("ancient wyvern") || name.equals("long-tailed wyvern")
                || name.equals("spitting wyvern") || name.equals("taloned wyvern");
    }

}
