package com.zenyte.game.content.area.abandonedmine.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public class PointsSettingsObjectAction implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                player("Okay so it looks like some type of display, but I can't quite figure out what it all means!");
            }
        });
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {4949};
    }
}
