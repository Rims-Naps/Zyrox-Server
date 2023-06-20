package com.zenyte.game.content.combatachievements.npcs;

import com.zenyte.game.content.combatachievements.combattasktiers.EasyTasks;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import lombok.val;

/**
 * @author Cresinkel
 */

public class GreaterDemon extends NPC implements Spawnable {


    public GreaterDemon(int id, Location tile, Direction facing, int radius) {
        super(id, tile, facing, radius);
    }


    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        if (source instanceof Player) {
            val player = (Player) source;
            if (!player.getBooleanAttribute("easy-combat-achievement24") && player.getEquipment().getItem(EquipmentSlot.WEAPON) != null) {
                if (player.getEquipment().getItem(EquipmentSlot.WEAPON).getName().toLowerCase().contains("silverlight")
                        || player.getEquipment().getItem(EquipmentSlot.WEAPON).getName().toLowerCase().contains("darklight")
                        || player.getEquipment().getItem(EquipmentSlot.WEAPON).getName().toLowerCase().contains("arclight")) {
                    player.putBooleanAttribute("easy-combat-achievement24", true);
                    EasyTasks.sendEasyCompletion(player, 24);
                }
            }
        }
    }

    @Override
    public boolean validate(final int id, final String name) {
        return name.equalsIgnoreCase("greater demon");
    }

}
