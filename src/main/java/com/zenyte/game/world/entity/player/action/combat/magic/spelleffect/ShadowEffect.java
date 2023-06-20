package com.zenyte.game.world.entity.player.action.combat.magic.spelleffect;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;

public class ShadowEffect implements SpellEffect{

    public ShadowEffect(final int percentage) {
        this.percentage = percentage;
    }
    
    private final int percentage;
    
    @Override
    public void spellEffect(Entity player, Entity target, int damage) {
        if(player instanceof Player && ((Player) player).getEquipment().getId(EquipmentSlot.WEAPON) == ItemId.ZURIELS_STAFF) {
            target.drainSkill(Skills.ATTACK, (double) percentage * 2, 0);
        } else {
            target.drainSkill(Skills.ATTACK, percentage, 0);
        }
    }
}
