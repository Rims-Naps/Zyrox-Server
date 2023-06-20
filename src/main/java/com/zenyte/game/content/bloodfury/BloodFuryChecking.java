package com.zenyte.game.content.bloodfury;

import com.zenyte.game.item.pluginextensions.ItemPlugin;

/**
 * @author Matt, redone by Cresinkel
 */
public class BloodFuryChecking extends ItemPlugin {

    @Override
   public void handle() {
       bind("Check", (player, item, slotId) -> {
           int charges = item.getCharges();
           player.sendMessage("This amulet has " + charges + " left");
       });
    }

    @Override
  public int[] getItems() {
     return new int[]{24780, 24782, 24784, 24786, 24788};
    }
}


