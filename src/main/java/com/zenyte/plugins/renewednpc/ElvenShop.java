package com.zenyte.plugins.renewednpc;

import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.plugins.dialogue.WiseOldManD;

/**
 * @author Matt
 */
public class ElvenShop extends NPCPlugin {
    @Override
    public void handle() {

        bind("Crystal Weapons", (player, npc) -> player.openShop("Elven Weapon Shop"));
        bind("Crystal Armours", (player, npc) -> player.openShop("Elven Armour Shop"));
        bind("Crystal Tools", (player, npc) -> player.openShop("Elven Tool Shop"));
    }
    @Override
    public int[] getNPCs() {
        return new int[] {13001};
    }
}
