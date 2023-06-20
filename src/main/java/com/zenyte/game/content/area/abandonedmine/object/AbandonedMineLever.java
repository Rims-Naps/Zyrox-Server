package com.zenyte.game.content.area.abandonedmine.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public class AbandonedMineLever implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                player("I probably shouldn't touch that... I don't know what it does.");
            }
        });
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {4950, 4951, 4952, 4953, 4954, 4955, 4956, 4957};
    }
}
