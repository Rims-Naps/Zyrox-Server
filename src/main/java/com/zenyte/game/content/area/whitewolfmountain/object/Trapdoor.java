package com.zenyte.game.content.area.whitewolfmountain.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public class Trapdoor implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                plain("You try to open the trapdoor but it won't budge! It looks like the trapdoor can only be opened from the other side.");
            }
        });
        player.sendMessage("You try to open the trapdoor but it won't budge!");
        player.sendMessage("It looks like the trapdoor can only be opened from the other side.");
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {100};
    }
}
