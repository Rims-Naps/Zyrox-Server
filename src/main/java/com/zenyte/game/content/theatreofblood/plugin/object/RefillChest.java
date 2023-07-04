package com.zenyte.game.content.theatreofblood.plugin.object;

import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;


/**
 * @author Cresinkel
 */

public class RefillChest implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        val party = VerSinhazaArea.getParty(player);
        if (option.equals("Open")) {
            if (party.getRaid().getActiveRoom().isCompleted() && party.getRaid().getActiveRoom().getRoom().getWave() == 2) {
                val raid = party.getRaid();
                var MVP = (Player) raid.getParty().getLeader();
                int MVPDamage;
                for (val p : raid.getParty().getPlayers()) {
                    MVPDamage = MVP.getNumericAttribute("bloatbossdamage").intValue();
                    int PlayerDamage = p.getNumericAttribute("bloatbossdamage").intValue();
                    if (PlayerDamage > MVPDamage) {
                        MVP = p;
                    }
                }
                if (MVP != null) {
                    if (MVP.getAttributes().containsKey("tobpoints")) {
                        MVP.getAttributes().put("tobpoints", MVP.getNumericAttribute("tobpoints").intValue() + 2);
                    } else {
                        MVP.sendMessage("You did not have the attribute for points.");
                    }
                } else {
                    raid.getParty().getLeader().sendMessage("MVP was null.");
                }
                for (val p : raid.getParty().getPlayers()) {
                    p.getAttributes().put("tobrefillpoints", p.getNumericAttribute("tobpoints").intValue());
                }

                World.removeObject(object);
                WorldObject newChest = new WorldObject(ObjectId.CHEST_32759, 10, object.getRotation(), object.getPosition());
                World.spawnObject(newChest);
            } else if (party.getRaid().getActiveRoom().isCompleted() && party.getRaid().getActiveRoom().getRoom().getWave() == 4) {
                val raid = party.getRaid();
                var MVP = (Player) raid.getParty().getLeader();
                int MVPDamage;
                for (val p : raid.getParty().getPlayers()) {
                    MVPDamage = MVP.getNumericAttribute("sotebossdamage").intValue();
                    int PlayerDamage = p.getNumericAttribute("sotebossdamage").intValue();
                    if (PlayerDamage > MVPDamage) {
                        MVP = p;
                    }
                }
                if (MVP != null) {
                    if (MVP.getAttributes().containsKey("tobpoints")) {
                        MVP.getAttributes().put("tobpoints", MVP.getNumericAttribute("tobpoints").intValue() + 1);
                    } else {
                        MVP.sendMessage("You did not have the attribute for points.");
                    }
                } else {
                    raid.getParty().getLeader().sendMessage("MVP was null.");
                }
                for (val p : raid.getParty().getPlayers()) {
                    p.getAttributes().put("tobrefillpoints", p.getNumericAttribute("tobpoints").intValue());
                }

                World.removeObject(object);
                WorldObject newChest = new WorldObject(ObjectId.CHEST_32759, 10, object.getRotation(), object.getPosition());
                World.spawnObject(newChest);
            }
            else {
                player.sendMessage("You must complete this room before getting access to new supplies.");
            }
        }
        if (option.equals("Search")) {
            player.openShop("Theatre of Blood Resupply Chest");
            player.sendMessage("You have " + Colour.RED.wrap(player.getNumericAttribute("tobrefillpoints").intValue()) + " points.");
        }

    }

    @Override
    public Object[] getObjects() {
        return new Object[]{ObjectId.CHEST_32758, ObjectId.CHEST_32759};
    }
}
