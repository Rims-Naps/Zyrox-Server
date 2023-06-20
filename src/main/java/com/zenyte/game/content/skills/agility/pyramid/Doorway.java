package com.zenyte.game.content.skills.agility.pyramid;

import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.val;

public class Doorway implements Obstacle {
    @Override
    public int getDuration(boolean success, WorldObject object) {
        return 0;
    }

    @Override
    public void startSuccess(Player player, WorldObject object) {
        player.setLocation(new Location(3364, 2830, 0));
        player.getDialogueManager().start(new PlainChat(player, "You climb down the steep passage. It leads to the base of the<br>pyramid"));
        player.getVarManager().sendBit(AgilityPyramid.HIDE_PYRAMID_VARBIT, false);
        player.addAttribute("Pyramid_lap_count", player.getNumericAttribute("Pyramid_lap_count").intValue() + 1);
        val laps = player.getNumericAttribute("Pyramid_lap_count").intValue();
        player.sendMessage("You have ran "+ Colour.RED.wrap(laps) + (laps == 1 ? " lap " : " laps ") + "on the Pyramid Course.");
    }

    @Override
    public double getSuccessXp(WorldObject object) {
        return 0;
    }

    @Override
    public int getLevel(WorldObject object) {
        return 30;
    }

    @Override
    public int[] getObjectIds() {
        return new int[] { 10855, 10856 };
    }

}
